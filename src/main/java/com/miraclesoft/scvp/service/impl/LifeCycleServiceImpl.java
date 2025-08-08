package com.miraclesoft.scvp.service.impl;

import static java.util.Objects.nonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.LifeCycle;
import com.miraclesoft.scvp.model.LifeCyclePayload;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.DataSourceDataProvider;
import com.miraclesoft.scvp.util.LifeCycleUtility;

/**
 * The Class LifeCycleServiceImpl.
 *
 * @author Narendar Geesidi
 */
@Component
public class LifeCycleServiceImpl {

	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** The data source data provider. */
	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;

	/** The life cycle utility. */
	@Autowired
	private LifeCycleUtility lifeCycleUtility;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(LifeCycleServiceImpl.class.getName());

	/**
	 * Warehouse order life cycle detail info.
	 *
	 * @param depositorOrderNumber the depositor order number
	 * @param fileId               the file id
	 * @param database             the database
	 * @param transaction          the transaction
	 * @return the life cycle
	 */
	public LifeCycle lifeCycleDetailInfo(final String depositorOrderNumber, final String fileId,
			final String database, final String transaction) {
		final LifeCycle lifeCycle = new LifeCycle();
		List<Object> params = new ArrayList<>();
		try {
			final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			final StringBuilder poDetailsQuery = new StringBuilder();
			poDetailsQuery.append("SELECT f.file_id, f.file_type, f.transaction_type, f.direction, "
					+ "CONVERT_TZ(f.date_time_received, ?, ?) as date_time_received, f.gs_control_number, f.sender_id, f.receiver_id, f.status, "
					+ "f.isa_number, f.isa_date, f.isa_time, " + "f.ack_status, f.pre_trans_filepath, "
					+ "f.post_trans_filepath, f.org_filepath, f.ack_file_id, f.gs_control_number, "
					+ "f.st_control_number, f.pri_key_val, f.pri_key_type, f.err_message, f.sec_key_val,f.error_report_filepath ,f.ack_filepath FROM ");
			params.add(defaultTimeZone);
			params.add(userTimeZone);
			poDetailsQuery.append("ARCHIVE".equals(database) ? "archive_files f" : "files f");
			poDetailsQuery.append(" WHERE f.sec_key_val = ? AND f.file_id = ? AND f.transaction_type = ? ORDER BY f.date_time_received");
			params.add(depositorOrderNumber);
			params.add(fileId);
			params.add(transaction);
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(poDetailsQuery.toString(), params.toArray());
			final Map<String, String> tradingPartners = dataSourceDataProvider.allTradingPartners();
			for (final Map<String, Object> row : rows) {
				lifeCycle.setFileId(nonNull(row.get("file_id")) ? (String) row.get("file_id") : "-");
				lifeCycle
						.setFileType(nonNull(row.get("file_type")) ? (String) row.get("file_type") : "-");
				lifeCycle.setTransactionType(
						nonNull(row.get("transaction_type")) ? (String) row.get("transaction_type") : "-");
				final String direction = nonNull(row.get("direction")) ? (String) row.get("direction") : "-";
				lifeCycle.setDirection(direction);
				lifeCycle.setDateTimeReceived(nonNull(row.get("date_time_received"))
						? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("date_time_received"))
						: "-");
				lifeCycle.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
				lifeCycle
						.setAckStatus(nonNull(row.get("ack_status")) ? (String) row.get("ack_status") : "-");
				lifeCycle
						.setIsaControlNumber(nonNull(row.get("isa_number")) ? (String) row.get("isa_number") : "-");
				lifeCycle.setGsControlNumber(
						nonNull(row.get("gs_control_number")) ? (String) row.get("gs_control_number") : "-");
				lifeCycle.setStControlNumber(
						nonNull(row.get("st_control_number")) ? (String) row.get("st_control_number") : "-");
				lifeCycle.setIsaDate(nonNull(row.get("isa_date")) ? (String) row.get("isa_date") : "-");
				lifeCycle.setIsaTime(nonNull(row.get("isa_time")) ? (String) row.get("isa_time") : "-");
				lifeCycle
						.setPrimaryKeyType(nonNull(row.get("pri_key_type")) ? (String) row.get("pri_key_type") : "-");
				lifeCycle
						.setPrimaryKeyValue(nonNull(row.get("pri_key_val")) ? (String) row.get("pri_key_val") : "-");
				lifeCycle.setDepositorOrderNumber(
						nonNull(row.get("sec_key_val")) ? (String) row.get("sec_key_val") : "-");
				String senderName = "-";
				if (nonNull(row.get("sender_id"))) {
					lifeCycle.setSenderId((String) row.get("sender_id"));
					if (nonNull(tradingPartners.get(row.get("sender_id")))) {
						senderName = tradingPartners.get(row.get("sender_id"));
					}
				} else {
					lifeCycle.setSenderId("-");
				}
				lifeCycle.setSenderName(senderName);
				String recieverName = "-";
				if (nonNull(row.get("receiver_id"))) {
					lifeCycle.setReceiverId((String) row.get("receiver_id"));
					if (nonNull(tradingPartners.get(row.get("receiver_id")))) {
						recieverName = tradingPartners.get(row.get("receiver_id"));
					}
				} else {
					lifeCycle.setReceiverId("-");
				}
				lifeCycle.setReceiverName(recieverName);

				if (nonNull(row.get("pre_trans_filepath"))) {
					lifeCycle.setPreTransFilePath((String) row.get("pre_trans_filepath"));
				} else {
					lifeCycle.setPreTransFilePath("No File");
				}
				if (nonNull(row.get("post_trans_filepath"))) {
					lifeCycle.setPostTransFilePath((String) row.get("post_trans_filepath"));
				} else {
					lifeCycle.setPostTransFilePath("No File");
				}
				if (nonNull(row.get("ack_filepath"))) {
					lifeCycle.setAckFilePath((String) row.get("ack_filepath"));
				} else {
					lifeCycle.setAckFilePath("No File");
				}
				if (nonNull(row.get("org_filepath"))) {
					lifeCycle.setOrgFilePath((String) row.get("org_filepath"));
				} else {
					lifeCycle.setOrgFilePath("No File");
				}

				lifeCycle
						.setErrorMessage(nonNull(row.get("err_message")) ? (String) row.get("err_message") : "NO MSG");
				lifeCycle.setRes("1");
				if (nonNull(row.get("error_report_filepath"))) {

					lifeCycle.setErrorReportFilePath((String) row.get("error_report_filepath"));

				} else {
					lifeCycle.setErrorReportFilePath("No File");
				}
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " lifeCycleDetailInfo :: " + exception.getMessage());
		}
		return lifeCycle;
	}

	/**
	 * Warehouse order life cycle.
	 *
	 * @param depositorOrderNumber the depositor order number
	 * @param database             the database
	 * @return the list
	 */
	public CustomResponse lifeCycle(final LifeCyclePayload lifeCyclePayload) {
		return lifeCycleUtility.lifeCycle(lifeCyclePayload);
	}
}
