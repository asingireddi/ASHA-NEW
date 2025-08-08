package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.DateUtility.convertToSqlDate;
import static com.miraclesoft.scvp.util.SqlCondition.*;
import static java.util.Objects.nonNull;

import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Dashboard;
import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.reports.Report;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

/**
 * The Class DashboardServiceImpl.
 *
 * @author Narendar Geesidi
 */
@Component
public class DashboardServiceImpl {

	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** The data source data provider. */
	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;

	/** The report. */
	@Autowired
	private Report report;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(DashboardServiceImpl.class.getName());

	/** The default top 10 trading partners. */
	@Value("${defaultTop10TradingPartners}")
	private String defaultTop10TradingPartners;

	@Value("${rehlko.ids}")
	private String rehlkoIds;

	/**
	 * Dashboard.
	 *
	 * @param dashboard the dashboard
	 * @return the string
	 * @throws Exception the exception
	 */
	public String dashboard(final Dashboard dashboard) throws Exception {
		String inboundString = "";
		String outboundString = "";
		String resultString = "";
		final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
		final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
		final String datePickerTo = dashboard.getToDate();
		final String datePickerFrom = dashboard.getFromDate();
		final JSONArray inBoundJsonArray = new JSONArray();
		final JSONArray outBoundJsonArray = new JSONArray();
		final StringBuilder dashboardQuery = new StringBuilder();
		final StringBuilder addOnQuery = new StringBuilder();
		final StringBuilder commonQuery = new StringBuilder();
		List<Object> params = new ArrayList<>();
		final Map<String, String> partnerMap = dataSourceDataProvider.allTradingPartners();
		if ("ALL".equalsIgnoreCase(dashboard.getTpId())) {
			final int userId = Integer.parseInt(tokenAuthenticationService.getUserIdfromToken());
			boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAll").toString());
			String partnerJoinQuery = !all ? dataSourceDataProvider.partnersJoinCondition().toString() : " ";
			String userIdRequired = !all ? " and pv.user_id = ?" : " ";
			params.add(userId);
			dashboardQuery.append("SELECT COUNT(f.id) total, direction, sender_id, receiver_id FROM files f ")
					.append(partnerJoinQuery).append(" WHERE 1 = 1");
			if (nonNull(datePickerFrom) && !"".equals(datePickerFrom)) {
				dashboardQuery.append(" AND date_time_received >= CONVERT_TZ(?, ?, ?)");
				params.add(convertToSqlDate(datePickerFrom));
				params.add(userTimeZone);
				params.add(defaultTimeZone);
			}
			if (nonNull(datePickerTo) && !"".equals(datePickerTo)) {
				dashboardQuery.append(" AND date_time_received <= CONVERT_TZ(?, ?, ?)");
				params.add(convertToSqlDate(datePickerTo));
				params.add(userTimeZone);
				params.add(defaultTimeZone);
			}
			if (nonNull(dashboard.getDocType()) && !"-1".equals(dashboard.getDocType())) {
				dashboardQuery.append(equalOperator("transaction_type"));
				params.add(dashboard.getDocType());
			}
			if (nonNull(dashboard.getStatus()) && !"-1".equals(dashboard.getStatus())) {
				dashboardQuery.append(equalOperator("status"));
				params.add(dashboard.getStatus());
			}
			// Executing query twice with different group by conditions, to achieve group by
			// response separately for inbound and outbound
			addOnQuery.append(dashboardQuery);
			dashboardQuery.append(" AND direction IS NOT NULL AND sender_id IS NOT NULL AND receiver_id IS NOT NULL")
					.append(userIdRequired).append(" GROUP BY direction, sender_id ");
			addOnQuery.append(" AND direction IS NOT NULL AND sender_id IS NOT NULL AND receiver_id IS NOT NULL")
					.append(userIdRequired).append(" GROUP BY direction, receiver_id ");
			logger.log(Level.INFO, "\n======> dashboard " + dashboard);
			System.out.println("dashboardQuery.toString()" + dashboardQuery.toString());
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(dashboardQuery.toString(), params.toArray());
			for (final Map<String, Object> row : rows) {
				final JSONObject inboundJsonObject = new JSONObject();
				if (((String) row.get("direction")).equalsIgnoreCase("inbound")) {
					if (partnerMap.containsKey(row.get("sender_id"))) {
						inboundJsonObject.put("name",
								(String) row.get("sender_id") + "_" + partnerMap.get(row.get("sender_id").toString()));
					} else {
						inboundJsonObject.put("name",
								(String) row.get("sender_id") + "_" + (String) row.get("sender_id").toString());
					}
					inboundJsonObject.put("count", row.get("total"));
					inBoundJsonArray.put(inboundJsonObject);
				}
			}
			System.out.println("addOnQuery.toString()" + addOnQuery.toString());
			final List<Map<String, Object>> addOnRows = jdbcTemplate.queryForList(addOnQuery.toString());
			for (final Map<String, Object> row : addOnRows) {
				final JSONObject outboundJsonObject = new JSONObject();
				if (((String) row.get("direction")).equalsIgnoreCase("outbound")) {
					if (partnerMap.containsKey(row.get("receiver_id"))) {
						outboundJsonObject.put("name", (String) row.get("receiver_id") + "_"
								+ partnerMap.get(row.get("receiver_id").toString()));
					} else {
						outboundJsonObject.put("name",
								(String) row.get("receiver_id") + "_" + (String) row.get("receiver_id").toString());
					}
					outboundJsonObject.put("count", row.get("total"));
					outBoundJsonArray.put(outboundJsonObject);
				}
			}
		} else {
			final String partnerName = dataSourceDataProvider.getPartnerNameById(dashboard.getTpId());
			dashboardQuery.append("SELECT COUNT(direction) total, direction FROM files WHERE sender_id = ?");
			addOnQuery.append("SELECT COUNT(direction) total, direction FROM files WHERE receiver_id = ?");
			params.add(dashboard.getTpId());
			if (nonNull(datePickerFrom) && !"".equals(datePickerFrom)) {
				commonQuery.append(" AND date_time_received >= CONVERT_TZ(?, ?, ?)");
				params.add(convertToSqlDate(datePickerFrom));
				params.add(userTimeZone);
				params.add(defaultTimeZone);
			}
			if (nonNull(datePickerTo) && !"".equals(datePickerTo)) {
				commonQuery.append(" AND date_time_received <= CONVERT_TZ(?, ?, ?)");
				params.add(convertToSqlDate(datePickerTo));
				params.add(userTimeZone);
				params.add(defaultTimeZone);
			}
			if (nonNull(dashboard.getDocType()) && !"-1".equals(dashboard.getDocType())) {
				commonQuery.append(equalOperator("transaction_type"));
				params.add(dashboard.getDocType());
			}
			if (nonNull(dashboard.getStatus()) && !"-1".equals(dashboard.getStatus())) {
				commonQuery.append(equalOperator("status"));
				params.add(dashboard.getStatus());
			}
			commonQuery.append(" GROUP BY direction");
			dashboardQuery.append(commonQuery);
			addOnQuery.append(commonQuery);
			logger.log(Level.INFO, "\n======> dashboard else " + dashboard);
			final List<Map<String, Object>> inboundRows = jdbcTemplate.queryForList(dashboardQuery.toString(), params.toArray());
			for (final Map<String, Object> row : inboundRows) {
				final JSONObject inobj = new JSONObject();
				if (((String) row.get("direction")).equalsIgnoreCase("inbound")) {
					inobj.put("name", dashboard.getTpId() + "_" + partnerName);
					inobj.put("count", row.get("total"));
					inBoundJsonArray.put(inobj);
				}
			}
			final List<Map<String, Object>> outboundRows = jdbcTemplate.queryForList(addOnQuery.toString(), params.toArray());
			for (final Map<String, Object> row : outboundRows) {
				final JSONObject outobj = new JSONObject();
				if (((String) row.get("direction")).equalsIgnoreCase("outbound")) {
					outobj.put("name", dashboard.getTpId() + "_" + partnerName);
					outobj.put("count", row.get("total"));
					outBoundJsonArray.put(outobj);
				}
			}
		}
		inboundString = inBoundJsonArray.toString();
		outboundString = outBoundJsonArray.toString();
		resultString = "{\"inbound\":" + inboundString + ",\"outbound\":" + outboundString + "}";
		return resultString;
	}

//	public String dashboard(final Dashboard dashboard) throws Exception {
//		final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
//		final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
//		final String datePickerTo = dashboard.getToDate();
//		final String datePickerFrom = dashboard.getFromDate();
//		final JSONArray inBoundJsonArray = new JSONArray();
//		final JSONArray outBoundJsonArray = new JSONArray();
//		final Map<String, String> partnerMap = dataSourceDataProvider.allTradingPartners();
//
//		if ("ALL".equalsIgnoreCase(dashboard.getTpId())) {
//			processAllPartners(dashboard, datePickerFrom, datePickerTo, userTimeZone, defaultTimeZone, inBoundJsonArray,
//					outBoundJsonArray, partnerMap);
//		} else {
//			processSpecificPartner(dashboard, datePickerFrom, datePickerTo, userTimeZone, defaultTimeZone,
//					inBoundJsonArray, outBoundJsonArray, partnerMap);
//		}
//
//		return new JSONObject().put("inbound", inBoundJsonArray).put("outbound", outBoundJsonArray).toString();
//	}
//
//	private void processAllPartners(Dashboard dashboard, String fromDate, String toDate, String userTZ,
//			String defaultTZ, JSONArray inArray, JSONArray outArray, Map<String, String> partnerMap) throws Exception {
//
//		List<Object> params = new ArrayList<>();
//		StringBuilder dashboardQuery = new StringBuilder();
//		StringBuilder addOnQuery = new StringBuilder();
//
//		int userId = Integer.parseInt(tokenAuthenticationService.getUserIdfromToken());
//		boolean isAll = Boolean.parseBoolean(httpServletRequest.getHeader("isAll"));
//		String partnerJoinQuery = isAll ? " " : dataSourceDataProvider.partnersJoinCondition().toString();
//		String userIdFilter = isAll ? " " : " AND pv.user_id = ?";
//		if (!isAll)
//			params.add(userId);
//
//		dashboardQuery.append("SELECT COUNT(f.id) total, direction, sender_id, receiver_id FROM files f ")
//				.append(partnerJoinQuery).append(" WHERE 1=1");
//
//		appendCommonConditions(dashboardQuery, params, dashboard, fromDate, toDate, userTZ, defaultTZ);
//
//		dashboardQuery.append(" AND direction IS NOT NULL AND sender_id IS NOT NULL AND receiver_id IS NOT NULL")
//				.append(userIdFilter).append(" GROUP BY direction, sender_id");
//
//		addOnQuery.append("SELECT COUNT(f.id) total, direction, sender_id, receiver_id FROM files f ")
//				.append(partnerJoinQuery).append(" WHERE 1=1");
//		appendCommonConditions(addOnQuery, params, dashboard, fromDate, toDate, userTZ, defaultTZ);
//		addOnQuery.append(" AND direction IS NOT NULL AND sender_id IS NOT NULL AND receiver_id IS NOT NULL")
//				.append(userIdFilter).append(" GROUP BY direction, receiver_id");
//
//		logger.info("Dashboard Query: {}", dashboardQuery);
//
//		List<Map<String, Object>> inboundRows = jdbcTemplate.queryForList(dashboardQuery.toString(), params.toArray());
//		for (Map<String, Object> row : inboundRows) {
//			String direction = (String) row.get("direction");
//			if ("inbound".equalsIgnoreCase(direction)) {
//				String senderId = row.get("sender_id").toString();
//				inArray.put(createResultObject(senderId, (int) row.get("total"), partnerMap));
//			}
//		}
//
//		List<Map<String, Object>> outboundRows = jdbcTemplate.queryForList(addOnQuery.toString(), params.toArray());
//		for (Map<String, Object> row : outboundRows) {
//			String direction = (String) row.get("direction");
//			if ("outbound".equalsIgnoreCase(direction)) {
//				String receiverId = row.get("receiver_id").toString();
//				outArray.put(createResultObject(receiverId, (int) row.get("total"), partnerMap));
//			}
//		}
//	}
//
//	private void processSpecificPartner(Dashboard dashboard, String fromDate, String toDate, String userTZ,
//			String defaultTZ, JSONArray inArray, JSONArray outArray, Map<String, String> partnerMap) throws Exception {
//
//		List<Object> params = new ArrayList<>();
//		String partnerId = dashboard.getTpId();
//		String partnerName = dataSourceDataProvider.getPartnerNameById(partnerId);
//
//		StringBuilder commonConditions = new StringBuilder();
//		appendCommonConditions(commonConditions, params, dashboard, fromDate, toDate, userTZ, defaultTZ);
//		commonConditions.append(" GROUP BY direction");
//
//		String inboundQuery = "SELECT COUNT(direction) total, direction FROM files WHERE sender_id = ?"
//				+ commonConditions;
//		String outboundQuery = "SELECT COUNT(direction) total, direction FROM files WHERE receiver_id = ?"
//				+ commonConditions;
//
//		params.add(0, partnerId); // Reuse params for both queries
//
//		List<Map<String, Object>> inRows = jdbcTemplate.queryForList(inboundQuery, params.toArray());
//		for (Map<String, Object> row : inRows) {
//			if ("inbound".equalsIgnoreCase((String) row.get("direction"))) {
//				inArray.put(createNamedResultObject(partnerId, partnerName, (int) row.get("total")));
//			}
//		}
//
//		List<Map<String, Object>> outRows = jdbcTemplate.queryForList(outboundQuery, params.toArray());
//		for (Map<String, Object> row : outRows) {
//			if ("outbound".equalsIgnoreCase((String) row.get("direction"))) {
//				outArray.put(createNamedResultObject(partnerId, partnerName, (int) row.get("total")));
//			}
//		}
//	}
//
//	private void appendCommonConditions(StringBuilder query, List<Object> params, Dashboard dashboard, String fromDate,
//			String toDate, String userTZ, String defaultTZ) {
//		if (fromDate != null && !fromDate.isEmpty()) {
//			query.append(" AND date_time_received >= CONVERT_TZ(?, ?, ?)");
//			params.add(convertToSqlDate(fromDate));
//			params.add(userTZ);
//			params.add(defaultTZ);
//		}
//		if (toDate != null && !toDate.isEmpty()) {
//			query.append(" AND date_time_received <= CONVERT_TZ(?, ?, ?)");
//			params.add(convertToSqlDate(toDate));
//			params.add(userTZ);
//			params.add(defaultTZ);
//		}
//		if (dashboard.getDocType() != null && !"-1".equals(dashboard.getDocType())) {
//			query.append(" AND transaction_type = ?");
//			params.add(dashboard.getDocType());
//		}
//		if (dashboard.getStatus() != null && !"-1".equals(dashboard.getStatus())) {
//			query.append(" AND status = ?");
//			params.add(dashboard.getStatus());
//		}
//	}
//
//	private JSONObject createResultObject(String id, int count, Map<String, String> partnerMap) {
//		String name = partnerMap.getOrDefault(id, id);
//		JSONObject obj = new JSONObject();
//		obj.put("name", id + "_" + name);
//		obj.put("count", count);
//		return obj;
//	}
//
//	private JSONObject createNamedResultObject(String id, String name, int count) {
//		JSONObject obj = new JSONObject();
//		obj.put("name", id + "_" + name);
//		obj.put("count", count);
//		return obj;
//	}

	/**
	 * Daily transactions.
	 *
	 * @return the string
	 */
	public String dailyTransactions() {
		final JSONArray dailyTransactions = new JSONArray();
		try {
			boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAll").toString());
			final int userId = Integer.parseInt(tokenAuthenticationService.getUserIdfromToken());
			String joinQuery = !all ? dataSourceDataProvider.partnersJoinCondition().toString() : "";
			String userIdRequired = !all ? " AND pv.user_id = ?" : "";
			final List<Map<String, Object>> rows = jdbcTemplate
					.queryForList("SELECT COUNT(f.id) count, transaction_type FROM files f " + joinQuery
							+ " WHERE (date_time_received >= CURRENT_DATE) " + " AND transaction_type IS NOT NULL"
							+ userIdRequired + " GROUP BY transaction_type ORDER BY count DESC", userId);
			for (final Map<String, Object> row : rows) {
				final JSONObject dailyTransaction = new JSONObject();
				dailyTransaction.put("type", row.get("transaction_type"));
				dailyTransaction.put("count", row.get("count"));
				dailyTransactions.put(dailyTransaction);
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, " dailyTransactions :: " + exception.getMessage());
		}
		return dailyTransactions.toString();
	}

	/**
	 * Hourly volumes.
	 *
	 * @return the string
	 */
	public String hourlyVolumes() {
		final JSONArray hourlyVolumes = new JSONArray();
		try {
			final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			String CurrDate = dataSourceDataProvider.getCurrentDateOfUser();
			String CurrDateTime = dataSourceDataProvider.getCurrentDateTimeOfUser();
			String query = "SELECT  HOUR(CONVERT_TZ(date_time_received,?,?)) AS hour, COUNT(id) AS total\r\n"
					+ " FROM files RIGHT JOIN (SELECT 0 AS hour UNION ALL \r\n"
					+ " SELECT  1 UNION ALL SELECT  2 UNION ALL SELECT  3 \r\n"
					+ " UNION ALL SELECT  4 UNION ALL SELECT  5 UNION ALL\r\n"
					+ " SELECT  6 UNION ALL SELECT  7 UNION ALL SELECT  8\r\n"
					+ " UNION ALL SELECT  9 UNION ALL SELECT 10 UNION ALL\r\n"
					+ " SELECT 11 UNION ALL SELECT 12 UNION ALL SELECT 13\r\n"
					+ " UNION ALL SELECT 14 UNION ALL SELECT 15 UNION ALL\r\n"
					+ " SELECT 16 UNION ALL SELECT 17 UNION ALL SELECT 18\r\n"
					+ " UNION ALL SELECT 19 UNION ALL SELECT 20 UNION ALL SELECT 21 UNION ALL SELECT 22"
					+ " UNION ALL SELECT 23) AS AllHours ON HOUR(CONVERT_TZ(date_time_received,?,?)) = hour WHERE CONVERT_TZ(date_time_received,?,?)"
					+ " BETWEEN  ?  AND ? GROUP BY   HOUR(CONVERT_TZ(date_time_received,?,?))  ORDER BY   HOUR(CONVERT_TZ(date_time_received,?,?)) ";
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, defaultTimeZone, userTimeZone,
					defaultTimeZone, userTimeZone, defaultTimeZone, userTimeZone, CurrDate, CurrDateTime,
					defaultTimeZone, userTimeZone, defaultTimeZone, userTimeZone);

			for (final Map<String, Object> row : rows) {
				final JSONObject hourVolume = new JSONObject();
				hourVolume.put("hour", row.get("hour") + ":00");
				hourVolume.put("count", row.get("total"));
				hourlyVolumes.put(hourVolume);
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, " hourlyVolumes :: " + exception.getMessage());
		}
		return hourlyVolumes.toString();
	}

	/**
	 * Daily failure rate.
	 *
	 * @return the string
	 */
	public String dailyFailureRate() {
		final JSONArray dailyFailureRate = new JSONArray();
		StringBuilder dailyFailureRateQuery = new StringBuilder();
		try {
			double successPercentage = 0;
			double failurePercentage = 0;
			double pendingPercentage = 0;
			float successCount = 0;
			float failureCount = 0;
			float pendingCount = 0;
			float totalCount = 0;
			dailyFailureRateQuery.append(
					"SELECT ( SELECT COUNT(id) success FROM files WHERE ((status = 'SUCCESS' OR status = 'DROPPED') AND (ack_status = 'ACCEPTED' OR ack_status = '200' OR ack_status = '201')) ");
			dailyFailureRateQuery.append("AND (date_time_received >= CURRENT_DATE)) success, ");
			dailyFailureRateQuery.append(
					" ( SELECT COUNT(id) failure FROM files WHERE (status = 'ERROR' AND (ack_status = 'REJECTED' OR ack_status = '400')) ");
			dailyFailureRateQuery.append("AND (date_time_received >= CURRENT_DATE)) failure,");
			dailyFailureRateQuery.append(
					" (SELECT COUNT(id) pending FROM files WHERE (status = 'SUCCESS' AND ack_status = 'OVERDUE') ");
			dailyFailureRateQuery.append(" AND (date_time_received >= CURRENT_DATE)) pending;");
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(dailyFailureRateQuery.toString());
			for (final Map<String, Object> row : rows) {
				successCount = ((Number) row.get("success")).floatValue(); // Handle potential Long or BigDecimal
				failureCount = ((Number) row.get("failure")).floatValue();
				pendingCount = ((Number) row.get("pending")).floatValue();
				totalCount = successCount + failureCount + pendingCount;
			}
			if (totalCount > 0) { // Changed from != 0 to > 0 for safety
				successPercentage = successCount * 100 / totalCount; // Assuming HUNDRED is 100
				failurePercentage = failureCount * 100 / totalCount;
				pendingPercentage = pendingCount * 100 / totalCount;
			}
			final String name = "status";
			final String y = "count";
			int decimalPlaces = 1; // number of decimal places to round off to
			final JSONObject count = new JSONObject();
			final JSONObject count1 = new JSONObject();
			final JSONObject count2 = new JSONObject();
			count.put(name, "Success");
			count.put(y, (double) (Math.round(successPercentage * Math.pow(10, decimalPlaces))
					/ Math.pow(10, decimalPlaces)));
			dailyFailureRate.put(count);
			count1.put(name, "Failure");
			count1.put(y, (double) (Math.round(failurePercentage * Math.pow(10, decimalPlaces))
					/ Math.pow(10, decimalPlaces)));
			dailyFailureRate.put(count1);
			count2.put(name, "Pending");
			count2.put(y, (double) (Math.round(pendingPercentage * Math.pow(10, decimalPlaces))
					/ Math.pow(10, decimalPlaces)));
			dailyFailureRate.put(count2);
		} catch (final Exception exception) {
			logger.log(Level.ERROR, "dailyFailureRate :: " + exception.getMessage());
		}
		return dailyFailureRate.toString();
	}

	/**
	 * Top tp inbound count.
	 *
	 * @param topTenTP the top ten TP
	 * @return the list
	 */
	public List<Long> topTpInboundCount(final String topTenTP) {
		final List<Long> topTpInboundCounts = new ArrayList<Long>();
		try {
			final String[] topTpNames = topTenTP.split(",");
			String query = "SELECT COUNT(f.id) count FROM files f JOIN tp t ON t.id = f.sender_id WHERE f.direction = 'INBOUND' AND t.id = ?"
					+ " AND date_time_received >= CURRENT_DATE";
			for (int i = 0; i < topTpNames.length; i++) {
				String tpId = topTpNames[i].substring(topTpNames[i].indexOf("(") + 1, topTpNames[i].indexOf(")"));
				final List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, tpId);
				for (final Map<String, Object> row : rows) {
					topTpInboundCounts.add((Long) row.get("count"));
				}
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, " topTpInboundCount :: " + exception.getMessage());
		}
		return topTpInboundCounts;
	}

	/**
	 * Top tp outbound count.
	 *
	 * @param topTenTP the top ten TP
	 * @return the list
	 */
	public List<Long> topTpOutboundCount(final String topTenTP) {
		final List<Long> topTpOutboundCounts = new ArrayList<Long>();
		try {
			final String[] topTpNames = topTenTP.split(",");
			String query = "SELECT COUNT(f.id) count FROM files f JOIN tp t ON (t.id = f.receiver_id) WHERE t.id = ?"
					+ " AND f.direction = 'OUTBOUND' AND date_time_received >= CURRENT_DATE";
			for (int i = 0; i < topTpNames.length; i++) {
				String tpId = topTpNames[i].substring(topTpNames[i].indexOf("(") + 1, topTpNames[i].indexOf(")"));
				final List<Map<String, Object>> rows = jdbcTemplate.queryForList(query, tpId);
				for (final Map<String, Object> row : rows) {
					topTpOutboundCounts.add((Long) row.get("count"));
				}
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, " topTpOutboundCount :: " + exception.getMessage());
		}
		return topTpOutboundCounts;
	}

	/**
	 * Top ten trading partners.
	 *
	 * @return the string
	 */
	public String topTenTradingPartners() {
		final StringBuilder partnerNamesWithCommaSeperated = new StringBuilder();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate
					.queryForList("SELECT COUNT(f.id) count, t.id AS partner_id, t.name as partner_name "
							+ "FROM files f JOIN tp t ON (f.sender_id = t.id OR f.receiver_id = t.id) "
							+ "WHERE t.name IS NOT NULL AND date_time_received >= CURRENT_DATE AND t.id NOT IN (?) "
							+ "GROUP BY t.id, t.name " + // Added t.name to GROUP BY clause
							"ORDER BY count DESC LIMIT 0,10", rehlkoIds); // Assuming rehlkoIds is a collection or array
																			// of IDs

			for (final Map<String, Object> row : rows) {
				String partnerId = String.valueOf(row.get("partner_id"));
				String partnerName = String.valueOf(row.get("partner_name"));
				partnerNamesWithCommaSeperated.append(partnerName).append("(").append(partnerId).append(")")
						.append(",");
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, "topTenTradingPartners :: " + exception.getMessage());
		}

		// Check if result exists and remove trailing comma if present
		if (partnerNamesWithCommaSeperated.length() > 0) {
			return partnerNamesWithCommaSeperated.substring(0, partnerNamesWithCommaSeperated.length() - 1);
		} else {
			return defaultTop10TradingPartners;
		}
	}

	/**
	 * Monthly volumes.
	 *
	 * @param days   the days
	 * @param userId the userId
	 * @return the string
	 */
	public String monthlyVolumes(final int days, final int userId) {
		final JSONArray monthlyVolumes = new JSONArray();
		try {
			final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			final StringBuilder monthlyVolumesQuery = new StringBuilder();
			// Building the days subquery
			StringBuilder daysSubquery = new StringBuilder("SELECT 0 AS D");
			for (int i = 1; i < days; i++) {
				daysSubquery.append(" UNION SELECT ").append(i);
			}
			monthlyVolumesQuery.append("SELECT DATE(D) AS temporary_date, "
					+ "SUM(CASE WHEN f.direction = 'INBOUND' THEN 1 ELSE 0 END) ib, "
					+ "SUM(CASE WHEN f.direction = 'OUTBOUND' THEN 1 ELSE 0 END) ob "
					+ "FROM (SELECT DATE_SUB(CONVERT_TZ(current_timestamp(),@@session.time_zone,?), INTERVAL D DAY) AS D FROM (")
					.append(daysSubquery)
					.append(") AS days) AS D LEFT JOIN files f ON DATE(CONVERT_TZ(f.date_time_received,?,?)) = DATE(D) "
							+ "GROUP BY temporary_date " + "ORDER BY temporary_date ASC");
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(monthlyVolumesQuery.toString(),
					userTimeZone, defaultTimeZone, userTimeZone);
			for (final Map<String, Object> row : rows) {
				final JSONObject dailyVolumes = new JSONObject();
				final String monthAndDate = new SimpleDateFormat("MMM dd").format((Date) row.get("temporary_date"));
				dailyVolumes.put("day", monthAndDate);
				dailyVolumes.put("ib", row.get("ib"));
				dailyVolumes.put("ob", row.get("ob"));
				monthlyVolumes.put(dailyVolumes);
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, "monthlyVolumes :: " + exception.getMessage());
		}
		return monthlyVolumes.toString();
	}

	/**
	 * Warehouse volumes.
	 *
	 * @param dashboard the dashboard
	 * @return the string
	 */
	public String warehouseVolumes(final Dashboard dashboard) {
	    JSONArray inBoundJsonArray = new JSONArray();
	    JSONArray outBoundJsonArray = new JSONArray();
	    List<Object> params = new ArrayList<>();
	    String resultString = "";

	    try {
	        final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
	        final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
	        final String datePickerTo = dashboard.getToDate();
	        final String datePickerFrom = dashboard.getFromDate();
	        final String transaction = dashboard.getDocType();
	        final String status = dashboard.getStatus();
	        StringBuilder query = new StringBuilder(
	                "SELECT parent_warehouse, direction, COUNT(*) as total FROM files f WHERE 1=1 ");
	        if (datePickerFrom != null && !datePickerFrom.isEmpty()) {
	            query.append(" AND date_time_received >= CONVERT_TZ(?, ?, ?)");
	            params.add(convertToSqlDate(datePickerFrom));
	            params.add(userTimeZone);
	            params.add(defaultTimeZone);
	        }
	        if (datePickerTo != null && !datePickerTo.isEmpty()) {
	            query.append(" AND date_time_received <= CONVERT_TZ(?, ?, ?)");
	            params.add(convertToSqlDate(datePickerTo));
	            params.add(userTimeZone);
	            params.add(defaultTimeZone);
	        }
	        if (isValidList(dashboard.getWarehouse())) {
			    query.append(equalOperatorWithOrAnd("parent_warehouse", dashboard.getWarehouse().size()));
			    params.addAll(dashboard.getWarehouse());
			}
			if (isValidList(dashboard.getParentWarehouse())) {
			    query.append(equalOperatorWithOrAnd("warehouse", dashboard.getParentWarehouse().size()));
			    params.addAll(dashboard.getParentWarehouse());
			}
	        if (transaction != null && !"-1".equals(transaction)) {
	            query.append(" AND transaction_type = ?");
	            params.add(transaction);
	        }
	        if (status != null && !"-1".equals(status)) {
	            query.append(" AND status = ?");
	            params.add(status);
	        }
	        query.append(" AND parent_warehouse IS NOT NULL AND direction IS NOT NULL ");
	        query.append(" GROUP BY parent_warehouse, direction ORDER BY parent_warehouse ASC");
	        logger.info("Warehouse Volumes Query: " + query);
	        logger.info("Parameters: " + params);
	        List<Map<String, Object>> rows = jdbcTemplate.queryForList(query.toString(), params.toArray());
	        for (Map<String, Object> row : rows) {
	            String direction = (String) row.get("direction");
	            String warehouse = (String) row.get("parent_warehouse");
	            int count = ((Number) row.get("total")).intValue();
	            JSONObject obj = new JSONObject();
	            obj.put("name", warehouse);
	            obj.put("count", count);
	            if ("inbound".equalsIgnoreCase(direction)) {
	                inBoundJsonArray.put(obj);
	            } else if ("outbound".equalsIgnoreCase(direction)) {
	                outBoundJsonArray.put(obj);
	            }
	        }
	    } catch (Exception e) {
	        logger.log(Level.ERROR, "Error in warehouseVolumes: " + e.getMessage(), e);
	    }
	    resultString = new JSONObject()
	            .put("inbound", inBoundJsonArray)
	            .put("outbound", outBoundJsonArray)
	            .toString();
	    return resultString;
	}
	/**
	 * Search by transaction group.
	 *
	 * @param type the type
	 * @return the list
	 * @throws Exception the exception
	 */
	public CustomResponse searchByTransactionGroup(final Dashboard dashboard) throws Exception {
		final List<DocumentRepository> documentRepositories = new ArrayList<DocumentRepository>();
		int count = 0;
		List<Object> params = new ArrayList<>();
		try {
			final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			final int userId = Integer.parseInt(tokenAuthenticationService.getUserIdfromToken());
			boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAll").toString());
			String joinQuery = !all ? dataSourceDataProvider.partnersJoinCondition().toString() : "";
			String userIdRequired = !all ? " pv.user_id = ? AND " : " ";
			params.add(userId);
			final StringBuilder transactionsGroupQuery = new StringBuilder();
			final StringBuilder criteriaForTransactionsGroupQuery = new StringBuilder();
			final StringBuilder sortingAndPaginationQuery = new StringBuilder();
			transactionsGroupQuery
					.append("SELECT f.id, f.file_id, f.file_type, f.transaction_type, f.direction, f.status, "
							+ "f.ack_status, CONVERT_TZ(f.date_time_received,?,) as date_time_received, f.warehouse, f.parent_warehouse,"
							+ " f.sender_id, f.receiver_id, f.reprocessstatus, f.pri_key_val, f.sec_key_val, f.filename FROM files f  "
							+ joinQuery + " WHERE ");
			params.add(defaultTimeZone);
			params.add(userTimeZone);
			if ("Orders".equals(dashboard.getTransactionType())) {
				criteriaForTransactionsGroupQuery
						.append("(f.transaction_type = '940' OR f.transaction_type = '1080') AND " + userIdRequired);
			} else if ("Shipments".equals(dashboard.getTransactionType())) {
				criteriaForTransactionsGroupQuery.append(
						"(f.transaction_type = '945' OR f.transaction_type = '856' OR f.transaction_type = '1082') AND "
								+ userIdRequired);
			} else if ("Receipts".equals(dashboard.getTransactionType())) {
				criteriaForTransactionsGroupQuery.append("f.transaction_type = '943' AND " + userIdRequired);
			}
			criteriaForTransactionsGroupQuery.append("(f.date_time_received >= CURRENT_DATE)");
			if (nonNull(dashboard.getSortField()) && nonNull(dashboard.getSortOrder())) {
				sortingAndPaginationQuery
						.append(dataSourceDataProvider.criteriaForSortingAndPagination(dashboard.getSortField(),
								dashboard.getSortOrder(), dashboard.getLimit(), dashboard.getOffSet()));
			}
			transactionsGroupQuery.append(criteriaForTransactionsGroupQuery).append(sortingAndPaginationQuery);
			if (dashboard.getCountFlag()) {
				count = jdbcTemplate.queryForObject(
						"SELECT COUNT(id) FROM files f " + joinQuery + " WHERE " + criteriaForTransactionsGroupQuery,
						Integer.class);
			}
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(transactionsGroupQuery.toString(),
					params.toArray());
			final Map<String, String> tradingPartners = dataSourceDataProvider.allTradingPartners();
			for (final Map<String, Object> row : rows) {
				final DocumentRepository documentRepository = new DocumentRepository();
				documentRepository.setId((Long) row.get("id"));
				documentRepository.setFileId(nonNull(row.get("file_id")) ? (String) row.get("file_id") : "-");
				documentRepository.setFileType(nonNull(row.get("file_type")) ? (String) row.get("file_type") : "-");
				documentRepository.setTransactionType(
						nonNull(row.get("transaction_type")) ? (String) row.get("transaction_type") : "-");
				final String direction = nonNull(row.get("direction")) ? (String) row.get("direction") : "-";
				documentRepository.setDirection(direction);
				documentRepository.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
				documentRepository.setAckStatus(nonNull(row.get("ack_status")) ? (String) row.get("ack_status") : "-");
				documentRepository.setDateTimeReceived(nonNull(row.get("date_time_received"))
						? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("date_time_received"))
						: "-");
				documentRepository.setWarehouse(
						nonNull(row.get("parent_warehouse")) ? (String) row.get("parent_warehouse") : "-");
				documentRepository
						.setParentWarehouse(nonNull(row.get("warehouse")) ? (String) row.get("warehouse") : "-");
				String partnerName = "-";
				if ("INBOUND".equalsIgnoreCase(direction) && nonNull(row.get("sender_id"))
						&& nonNull(tradingPartners.get(row.get("sender_id")))) {
					partnerName = tradingPartners.get(row.get("sender_id"));
				} else if ("OUTBOUND".equalsIgnoreCase(direction) && nonNull(row.get("receiver_id"))
						&& nonNull(tradingPartners.get(row.get("receiver_id")))) {
					partnerName = tradingPartners.get(row.get("receiver_id"));
				}
				documentRepository.setPartnerName(partnerName);
				documentRepository.setReProcessStatus(
						nonNull(row.get("reprocessstatus")) ? (String) row.get("reprocessstatus") : "-");
				documentRepository
						.setPrimaryKeyValue(nonNull(row.get("pri_key_val")) ? (String) row.get("pri_key_val") : "-");
				documentRepository
						.setShipmentId(nonNull(row.get("sec_key_val")) ? (String) row.get("sec_key_val") : "-");
				documentRepository.setFileName(nonNull(row.get("filename")) ? (String) row.get("filename") : "-");
				documentRepositories.add(documentRepository);
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, " searchByTransactionGroup :: " + exception.getMessage());
		}
		return new CustomResponse(documentRepositories, count);
	}

	/**
	 * Gets the dash board excel pdf data.
	 *
	 * @param dashboard the dashboard
	 * @return the dash board excel pdf data
	 * @throws Exception the exception
	 */
	public Map<String, Object> getDashBoardExcelPdfData(final Dashboard dashboard) throws Exception {
		return report.dashBoardExcelPdfData(dashboard(dashboard));
	}

	public Map<String, Object> download(Dashboard dashboard) {
		return report.dashBoardExcelPdfData(warehouseVolumes(dashboard));
	}

}