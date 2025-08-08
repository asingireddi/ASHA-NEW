package com.miraclesoft.scvp.util;

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

/**
 * The Class LifeCycleUtility.
 *
 * @author Narendar Geesidi
 */
@Component
public class LifeCycleUtility {

	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** The data source data provider. */
	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(LifeCycleUtility.class.getName());

	/**
	 * Warehouse order lifecycles.
	 *
	 * @param depositorOrderNumber the depositor order number
	 * @param database             the database
	 * @return the list
	 */
	public CustomResponse lifeCycle(final LifeCyclePayload lifeCyclePayload) {
		final List<LifeCycle> lifeCycleList = new ArrayList<LifeCycle>();
	    int count = 0;
	    List<Object> params = new ArrayList<>();
		try {
			final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();			
			final StringBuilder lifeCycleSearchQuery = new StringBuilder();
			final StringBuilder searchCriteriaQuery = new StringBuilder();
			final StringBuilder sortingAndPaginationQuery = new StringBuilder();
			lifeCycleSearchQuery.append("SELECT distinct(f.file_id), f.file_type, f.sender_id, f.receiver_id, "
					+ "f.transaction_type, f.direction, CONVERT_TZ(f.date_time_received, ?, ?) as date_time_received, f.status, f.ack_status, "
					+ "f.reprocessstatus, f.sec_key_val FROM ");
			params.add(defaultTimeZone);
			params.add(userTimeZone);
			searchCriteriaQuery.append("ARCHIVE".equals(lifeCyclePayload.getDatabase()) ? "archive_files f" : "files f");
			searchCriteriaQuery
					.append(" WHERE f.sec_key_val = ?");
			params.add(lifeCyclePayload.getDepositorOrderNumber());
			if (nonNull(lifeCyclePayload.getSortField()) && nonNull(lifeCyclePayload.getSortOrder())) {
				sortingAndPaginationQuery.append(dataSourceDataProvider.criteriaForSortingAndPagination(
						lifeCyclePayload.getSortField(), lifeCyclePayload.getSortOrder(), lifeCyclePayload.getLimit(),
						lifeCyclePayload.getOffSet()));
			}
			lifeCycleSearchQuery.append(searchCriteriaQuery).append(sortingAndPaginationQuery);
			if (lifeCyclePayload.getCountFlag()) {
	            String countQuery = "SELECT COUNT(id) FROM " + (lifeCyclePayload.getDatabase().equals("ARCHIVE") ? "archive_files" : "files") + " f WHERE f.sec_key_val = ?";
	            count = jdbcTemplate.queryForObject(countQuery, new Object[]{lifeCyclePayload.getDepositorOrderNumber()}, Integer.class);
	        }
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(lifeCycleSearchQuery.toString(), params.toArray());
			final Map<String, String> tradingPartners = dataSourceDataProvider.allTradingPartners();
			for (final Map<String, Object> row : rows) {
				final LifeCycle lifeCycle = new LifeCycle();
				lifeCycle.setRes("1");
				lifeCycle.setFileId(nonNull(row.get("file_id")) ? (String) row.get("file_id") : "-");
				lifeCycle.setFileType(nonNull(row.get("file_type")) ? (String) row.get("file_type") : "-");
				lifeCycle.setTransactionType(
						nonNull(row.get("transaction_type")) ? (String) row.get("transaction_type") : "-");
				final String direction = nonNull(row.get("direction")) ? (String) row.get("direction") : "-";
				lifeCycle.setDirection(direction);
				String partnerName = "-";
				if ("INBOUND".equalsIgnoreCase(direction) && nonNull(row.get("sender_id"))
						&& nonNull(tradingPartners.get(row.get("sender_id")))) {
					partnerName = tradingPartners.get(row.get("sender_id"));
				} else if ("OUTBOUND".equalsIgnoreCase(direction) && nonNull(row.get("receiver_id"))
						&& nonNull(tradingPartners.get(row.get("receiver_id")))) {
					partnerName = tradingPartners.get(row.get("receiver_id"));
				}
				lifeCycle.setPartner(partnerName);
				lifeCycle.setDateTimeReceived(nonNull(row.get("date_time_received"))
						? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("date_time_received"))
						: "-");
				lifeCycle.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
				lifeCycle.setDepositorOrderNumber(
						nonNull(row.get("sec_key_val")) ? (String) row.get("sec_key_val") : "-");
				lifeCycle.setAckStatus(nonNull(row.get("ack_status")) ? (String) row.get("ack_status") : "-");
				lifeCycle.setReProcessStatus(
						nonNull(row.get("reprocessstatus")) ? (String) row.get("reprocessstatus") : "-");
				lifeCycleList.add(lifeCycle);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " lifeCycle :: " + exception.getMessage());
		}
		return new CustomResponse(lifeCycleList, count);
	}
}
