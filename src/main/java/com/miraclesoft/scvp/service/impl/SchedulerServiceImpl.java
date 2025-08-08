package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.SqlCondition.equalOperator;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.Scheduler;

/**
 * The Class SchedulerServiceImpl.
 *
 * @author Priyanka Kolla
 */
@Component
public class SchedulerServiceImpl {

    /** The jdbc template. */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /** The logger. */
    private static Logger logger = LogManager.getLogger(SchedulerServiceImpl.class.getName());

    /**
     * Find all.
     *
     * @param scheduler the scheduler
     * @return the list
     */
    public List<Scheduler> findAll(final Scheduler scheduler) {
        final List<Scheduler> schedulers = new ArrayList<Scheduler>();
        List<Object> params = new ArrayList<>();
        try {
            final String status = scheduler.getStatus();
            final String createdBy = scheduler.getCreatedBy();
            final StringBuilder schedulerSearchQuery = new StringBuilder();
            schedulerSearchQuery.append("SELECT sch_id, sch_title, sch_type, sch_status FROM scheduler WHERE 1 = 1");
            if (nonNull(status) && !"-1".equals(status.trim())) {
                schedulerSearchQuery.append(equalOperator("sch_status"));
                params.add(status.trim());
            }
            if (nonNull(createdBy) && !"".equals(createdBy.trim())) {
                schedulerSearchQuery.append(equalOperator("created_by"));
                params.add(createdBy.trim());
            }
            final List<Map<String, Object>> rows = jdbcTemplate.queryForList(schedulerSearchQuery.toString(), params.toArray());
            for (final Map<String, Object> row : rows) {
                final Scheduler doc = new Scheduler();
                doc.setId(nonNull(row.get("sch_id")) ? (Long) row.get("sch_id") : 0L);
                doc.setTitle(nonNull(row.get("sch_title")) ? (String) row.get("sch_title") : "-");
                doc.setType(nonNull(row.get("sch_type")) ? (String) row.get("sch_type") : "-");
                doc.setStatus(nonNull(row.get("sch_status")) ? (String) row.get("sch_status") : "-");
                schedulers.add(doc);
            }
        }
        catch (Exception exception) {
            logger.log(Level.ERROR, " findAll :: " + exception.getMessage());
        }
        return schedulers;
    }

    /**
     * Save.
     *
     * @param scheduler the scheduler
     * @return the string
     * @throws Exception the exception
     */
    public String save(final Scheduler scheduler) throws Exception {
        return !isSchedulerExists(scheduler.getCreatedBy(),
                scheduler.getType())
                        ? jdbcTemplate.update(
                                "INSERT INTO scheduler (sch_title, sch_type, sch_status, reciver_ids, "
                                        + "extranal_emailids, created_by) VALUES (?, ?, ?, ?, ?, ?)",
                        new Object[] {
                                scheduler.getTitle(),
                                scheduler.getType(),
                                "Active",
                                scheduler.getReceiverEmails(),
                                scheduler.getExternalEmails(),
                                        scheduler.getCreatedBy() }) > 0 ? "Scheduler added succesfully."
                                                : "Please try again!"
                        : "Scheduler with same report type is already exists.";
    }

    /**
     * Find one.
     *
     * @param id the id
     * @return the scheduler
     */
    public Scheduler findOne(final Long id) {
        final Scheduler doc = new Scheduler();
        try {
            final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
                    "SELECT sch_id, sch_title, sch_type, reciver_ids, extranal_emailids, sch_status from scheduler"
                            + " WHERE sch_id = ?", id);
            for (final Map<String, Object> row : rows) {
                doc.setId(nonNull(row.get("sch_id")) ? (Long) row.get("sch_id") : 0);
                doc.setTitle(nonNull(row.get("sch_title")) ? (String) row.get("sch_title") : "-");
                doc.setType(nonNull(row.get("sch_type")) ? (String) row.get("sch_type") : "-");
                doc.setStatus(nonNull(row.get("sch_status")) ? (String) row.get("sch_status") : "-");

                String userIds = (String) row.get("reciver_ids");
                String[] reciverids = userIds.split(",");
                List<String> wordList = Arrays.asList(reciverids);
                Iterator<String> iter = wordList.iterator();
                List<String> list = new ArrayList<String>();
                while (iter.hasNext()) {
                    list.add(iter.next()
                                 .trim());
                }
                doc.setReceiverIds(list);

                doc.setExternalEmails(
                        nonNull(row.get("extranal_emailids")) ? (String) row.get("extranal_emailids") : "-");
            }
        }
        catch (Exception exception) {
            logger.log(Level.ERROR, " findOne :: " + exception.getMessage());
        }
        return doc;
    }

    /**
     * Update.
     *
     * @param scheduler the scheduler
     * @return the string
     * @throws Exception the exception
     */
    public String update(final Scheduler scheduler) throws Exception {
        return jdbcTemplate.update(
                "UPDATE scheduler SET sch_title = ?, sch_type = ?, reciver_ids = ?, "
                        + "extranal_emailids = ?, sch_status = ? WHERE sch_id = ?",
                new Object[] {
                        scheduler.getTitle(),
                        scheduler.getType(),
                        scheduler.getReceiverEmails(),
                        scheduler.getExternalEmails(),
                        scheduler.getStatus(),
                        scheduler.getId() }) > 0 ? "Scheduler updated succesfully." : "Please try again!";
    }

    /**
     * Delete.
     *
     * @param id the id
     * @return the string
     * @throws Exception the exception
     */
    public String delete(final Long id) throws Exception {
        return jdbcTemplate.update("DELETE FROM scheduler WHERE sch_id = ?", new Object[] { id }) > 0
                ? "Scheduler deleted succesfully."
                : "Please try again!";
    }

    /**
     * Checks if is scheduler exists.
     *
     * @param createdBy the created by
     * @param type the type
     * @return true, if is scheduler exists
     * @throws Exception the exception
     */
    private boolean isSchedulerExists(final String createdBy, final String type) throws Exception {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM scheduler WHERE created_by = ? AND sch_type = ?",
                new Object[] { createdBy, type }, Integer.class) > 0 ? true : false;
    }

}
