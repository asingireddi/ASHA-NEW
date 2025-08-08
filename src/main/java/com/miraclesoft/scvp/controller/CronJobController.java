package com.miraclesoft.scvp.controller;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Set;

import javax.mail.MessagingException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.mail.MailManager;
import com.miraclesoft.scvp.service.CronJobService;

/**
 * The Class CronJobController.
 */
@Component
public class CronJobController {
    /** The mail manager. */
    @Autowired
    private MailManager mailManager;

    /** The documents service. */
    @Autowired
    private CronJobService cronJobService;
    
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** The logger. */
    private static Logger logger = LogManager.getLogger(CronJobController.class.getName());

    /**
     * ┌─────────── second (0-59) │ ┌─────────── minute (0 - 59) │ │ ┌────────────
     * hour (0 - 23) │ │ │ ┌───────────── day of the month (1 - 31) │ │ │ │
     * ┌────────────── month (1 - 12) (or JAN-DEC) │ │ │ │ │ ┌─────────────── day of
     * the week (0 - 7) (or MON-SUN -- 0 or 7 is Sunday) │ │ │ │ │ │ │ │ │ │ │ │ * *
     * * * *
     */
    /** The Constant DAILY_JOB_TIME. */
    private static final String DAILY_JOB_TIME = "0 0 0 * * *";

    /** The Constant WEEKLY_JOB_TIME. */
    private static final String WEEKLY_JOB_TIME = "0 1 0 ? * SUN";

    /** The Constant MONTHLY_JOB_TIME. */
    private static final String MONTHLY_JOB_TIME = "0 2 0 1 * ?";

    /** The Constant DAILY_JOB_TIME. */
    private static final String ARCHIVE_JOB_TIME = "0 3 0 * * *";

    /**
     * Daily scheduler. Runs everyday at 12 AM.
     */
    @Scheduled(cron = DAILY_JOB_TIME)
    public void dailyScheduler() {
        try {
            schedule("Daily");
        } catch (final Exception exception) {
            logger.log(Level.ERROR, " dailyScheduler :: " + exception.getMessage());
        }
    }

    /**
     * Weekly scheduler. Runs every Sunday at 12:01 AM.
     */
    @Scheduled(cron = WEEKLY_JOB_TIME)
    public void weeklyScheduler() {
        try {
            schedule("Weekly");
        } catch (final Exception exception) {
            logger.log(Level.ERROR, " weeklyScheduler :: " + exception.getMessage());
        }
    }

    /**
     * Monthly scheduler. Runs every month first day at 12:02 AM.
     */
    @Scheduled(cron = MONTHLY_JOB_TIME)
    public void monthlyScheduler() {
        try {
            schedule("Monthly");
        } catch (final Exception exception) {
            logger.log(Level.ERROR, " monthlyScheduler :: " + exception.getMessage());
        }
    }

    /**
     * Archive purge scheduler. Runs everyday at 12:03 AM.
     *
     * @return the string
     * @throws SQLException the SQL exception
     */
    @Scheduled(cron = ARCHIVE_JOB_TIME)
    public String archivePurgeScheduler() throws SQLException {
        return cronJobService.archivePurgeScheduler();
    }

    /**
     * Schedule.
     *
     * @param type the type
     * @throws MessagingException the messaging exception
     * @throws IOException        Signals that an I/O exception has occurred.
     */
    public void schedule(final String type) throws MessagingException, IOException {
        try {
            final Set<String> toEmails = cronJobService.getToEmailsForReport(type);
            final Set<String> ccEmails = cronJobService.getCcEmailsForReport(type);
            final String filePath = cronJobService.getFile(type);
            if (toEmails.size() > 0 && !"No file".equals(filePath)) {
                mailManager.sendMailWithAttachment(type, toEmails, ccEmails, filePath);
            } else if (toEmails.size() > 0 && "No file".equals(filePath)) {
                mailManager.sendMailWithNoReports(type, toEmails, ccEmails);
            }
        } catch (final Exception exception) {
            logger.log(Level.ERROR, " schedule :: " + exception.getMessage());
        }
    }
    
    /**
     * Scheduler runs every day at 12:00 AM to archive 1-year-old data.
     */
    @Scheduled(cron = "0 0 0 * * ?") // Runs at 12:00 AM every day
	public void archiveOldData() {
		try {
			logger.info("Archiving process started...");

			// Move 1-year-old data to archive_table
			String moveDataQuery = "INSERT INTO archive_files (id, file_id, parent_file_id, file_type, file_origin, "
					+ "transaction_type, direction, status, ack_status, parent_warehouse, warehouse, sender_id, receiver_id,"
					+ " pri_key_type, pri_key_val, sec_key_type, sec_key_val, date_time_received, isa_number, isa_date, isa_time,"
					+ " gs_control_number, st_control_number, filename, file_size, ack_file_id, org_filepath, pre_trans_filepath,"
					+ " post_trans_filepath, re_submit_filepath, re_translate_filepath, error_report_filepath, err_message,"
					+ " reprocessstatus, carrier_status, source, target, idoc_num, network_van, map_name, mailbox_name, envelope_name,"
					+ " mailbox_time, flowflag, ack_filepath, ack_date, resubmit, SFG_Path, err_file_id, bootstrapID, deliveryReport,"
					+ " partnerName) SELECT id, file_id, parent_file_id, file_type, file_origin, transaction_type, direction, status,"
					+ " ack_status, parent_warehouse, warehouse, sender_id, receiver_id, pri_key_type, pri_key_val, sec_key_type,"
					+ " sec_key_val, date_time_received, isa_number, isa_date, isa_time, gs_control_number, st_control_number, filename,"
					+ " file_size, ack_file_id, org_filepath, pre_trans_filepath, post_trans_filepath, re_submit_filepath,"
					+ " re_translate_filepath, error_report_filepath, err_message, reprocessstatus, carrier_status, source,"
					+ " target, idoc_num, network_van, map_name, mailbox_name, envelope_name, mailbox_time, flowflag, ack_filepath,"
					+ " ack_date, resubmit, SFG_Path, err_file_id, bootstrapID, deliveryReport, partnerName FROM files"
					+ "WHERE date_time_received < NOW() - INTERVAL 1 YEAR";
			int insertedRows = jdbcTemplate.update(moveDataQuery);
			logger.info("Archived {} records to archive_files.", insertedRows);
			if(insertedRows > 0) {
			// Delete 1-year-old data from live_table
			String deleteDataQuery = "DELETE FROM files WHERE date_time_received < NOW() - INTERVAL 1 YEAR";
			int deletedRows = jdbcTemplate.update(deleteDataQuery);
			logger.info("Deleted {} records from files.", deletedRows);
			}
			logger.info("Archiving process completed successfully.");
		} catch (Exception e) {
			logger.error("Error during data archival: ", e);
		}
	}

}
