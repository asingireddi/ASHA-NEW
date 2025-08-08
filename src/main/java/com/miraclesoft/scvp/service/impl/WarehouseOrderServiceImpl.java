package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.DateUtility.convertToSqlDate;
import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;
import static com.miraclesoft.scvp.util.SqlCondition.*;
import static java.util.Objects.nonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.model.WarehouseOrder;
import com.miraclesoft.scvp.reports.Report;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

/**
 * The Class WarehouseOrderServiceImpl.
 *
 * @author Narendar Geesidi
 */
@Component
@SuppressWarnings("PMD.TooManyStaticImports")
public class WarehouseOrderServiceImpl {

	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	/** The report. */
	@Autowired
	private Report report;

	/** The data source data provider. */
	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;

	@Autowired
	private HttpServletRequest httpServletRequest;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(WarehouseOrderServiceImpl.class.getName());

	/**
	 * Search.
	 *
	 * @param searchCriteria the search criteria
	 * @return the list
	 */
	public CustomResponse search(final SearchCriteria searchCriteria) {
		final List<WarehouseOrder> warehouseOrders = new ArrayList<WarehouseOrder>();
		int count = 0;
		try {
			Map<String, Object> queryMap = getWarehouseSearchQuery(searchCriteria);
			String mainQuery = (String) queryMap.get("mainQuery");
			String countQuery = (String) queryMap.get("countQuery");
			List<Object> params = (List<Object>) queryMap.get("params");
			List<Object> countParams = (List<Object>) queryMap.get("countParams");
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(mainQuery, params.toArray());
			if (searchCriteria.getCountFlag()) {
				count = jdbcTemplate.queryForObject(countQuery, countParams.toArray(), Integer.class);
			}
			final Map<String, String> tradingPartners = dataSourceDataProvider.allTradingPartners();
			for (final Map<String, Object> row : rows) {
				final WarehouseOrder warehouseOrder = new WarehouseOrder();
				warehouseOrder.setId(Integer.parseInt(row.get("id").toString()));
				warehouseOrder.setSenderId(nonNull(row.get("sender_id")) ? (String) row.get("sender_id") : "-");
				warehouseOrder.setDateTimeReceived(nonNull(row.get("date_time_received"))
						? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("date_time_received"))
						: "-");
				warehouseOrder.setFileId(nonNull(row.get("file_id")) ? (String) row.get("file_id") : "-");
				warehouseOrder.setDepositorOrderNumber(
						nonNull(row.get("sec_key_val")) ? (String) row.get("sec_key_val") : "-");
				warehouseOrder.setTransactionType(
						nonNull(row.get("transaction_type")) ? (String) row.get("transaction_type") : "-");
				final String direction = nonNull(row.get("direction")) ? (String) row.get("direction") : "-";
				warehouseOrder.setDirection(direction);
				String partnerName = "-";
				if ("INBOUND".equalsIgnoreCase(direction) && nonNull(row.get("sender_id"))
						&& nonNull(tradingPartners.get(row.get("sender_id")))) {
					partnerName = tradingPartners.get(row.get("sender_id"));
				} else if ("OUTBOUND".equalsIgnoreCase(direction) && nonNull(row.get("receiver_id"))
						&& nonNull(tradingPartners.get(row.get("receiver_id")))) {
					partnerName = tradingPartners.get(row.get("receiver_id"));
				}
				warehouseOrder.setPartnerName(partnerName);
				warehouseOrder.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
				warehouseOrder.setAckStatus(nonNull(row.get("ack_status")) ? (String) row.get("ack_status") : "-");
				warehouseOrder.setReProcessStatus(
						nonNull(row.get("reprocessstatus")) ? (String) row.get("reprocessstatus") : "-");
				warehouseOrder.setWarehouse(
						nonNull(row.get("parent_warehouse")) ? (String) row.get("parent_warehouse") : "-");
				warehouseOrder.setParentWarehouse(nonNull(row.get("warehouse")) ? (String) row.get("warehouse") : "-");
				warehouseOrder
						.setIsaControlNumber(nonNull(row.get("isa_number")) ? (String) row.get("isa_number") : "-");
				warehouseOrder.setGsControlNumber(
						nonNull(row.get("gs_control_number")) ? (String) row.get("gs_control_number") : "-");
				warehouseOrder.setStControlNumber(
						nonNull(row.get("st_control_number")) ? (String) row.get("st_control_number") : "-");
				warehouseOrder.setErrFileId(nonNull(row.get("err_file_id")) ? (String) row.get("err_file_id") : "-");
				warehouseOrder.setPostTransFileName(
						nonNull(row.get("post_trans_filename")) ? (String) row.get("post_trans_filename") : "-");
				warehouseOrder.setFileName(nonNull(row.get("filename")) ? (String) row.get("filename") : "-");
				warehouseOrders.add(warehouseOrder);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " search :: " + exception.getMessage());
		}
		return new CustomResponse(warehouseOrders, count);
	}

	/**
	 * Download.
	 *
	 * @param searchCriteria the search criteria
	 * @return the response entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public ResponseEntity<InputStreamResource> download(final SearchCriteria searchCriteria) throws IOException {
		return getInputStreamResource(
				new File(report.warehouseOrderExcelDownload((List<WarehouseOrder>) search(searchCriteria).getData())));
	}

	/**
	 * Detail info.
	 *
	 * @param depositorOrderNumber the depositor order number
	 * @param fileId               the file id
	 * @param database             the database
	 * @return the warehouse order
	 */

	public WarehouseOrder detailInfo(final String depositorOrderNumber, final String fileId, final String database) {
		final WarehouseOrder warehouseOrder = new WarehouseOrder();
		try {
			final StringBuilder warehouseOrderDetailsQuery = new StringBuilder();
			warehouseOrderDetailsQuery.append("SELECT f.file_id, f.file_type, f.transaction_type, f.isa_number, "
					+ "f.gs_control_number, f.st_control_number, f.sec_key_val, f.sender_id, f.receiver_id, "
					+ "f.pre_trans_filepath, f.post_trans_filepath, f.error_report_filepath, f.org_filepath, "
					+ "f.err_message, f.status, f.isa_date, f.isa_time, f.ack_file_id ,f.ack_filepath,f.err_file_id FROM ");
			warehouseOrderDetailsQuery.append(database.equals("ARCHIVE") ? "archive_files f" : "files f");
			warehouseOrderDetailsQuery.append(" WHERE flowflag = 'M' AND f.sec_key_val = '" + depositorOrderNumber
					+ "' AND f.file_id = ?");
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(warehouseOrderDetailsQuery.toString(), fileId);
			final Map<String, String> tradingPartners = dataSourceDataProvider.allTradingPartners();
			for (Map<String, Object> row : rows) {
				warehouseOrder.setErrFileId(nonNull(row.get("err_file_id")) ? (String) row.get("err_file_id") : "-");
				warehouseOrder.setFileId(nonNull(row.get("file_id")) ? (String) row.get("file_id") : "-");
				warehouseOrder.setDepositorOrderNumber(
						nonNull(row.get("sec_key_val")) ? (String) row.get("sec_key_val") : "-");
				warehouseOrder.setTransactionType(
						nonNull(row.get("transaction_type")) ? (String) row.get("transaction_type") : "-");
				warehouseOrder.setFileType(nonNull(row.get("file_type")) ? (String) row.get("file_type") : "-");
				String senderName = "-";
				if (nonNull(row.get("sender_id"))) {
					warehouseOrder.setSenderId((String) row.get("sender_id"));
					if (nonNull(tradingPartners.get(row.get("sender_id")))) {
						senderName = tradingPartners.get(row.get("sender_id"));
					}
				} else {
					warehouseOrder.setSenderId("-");
				}
				warehouseOrder.setSenderName(senderName);
				String recieverName = "-";
				if (nonNull(row.get("receiver_id"))) {
					warehouseOrder.setReceiverId((String) row.get("receiver_id"));
					if (nonNull(tradingPartners.get(row.get("receiver_id")))) {
						recieverName = tradingPartners.get(row.get("receiver_id"));
					}
				} else {
					warehouseOrder.setReceiverId("-");
				}
				warehouseOrder.setReceiverName(recieverName);
				warehouseOrder
						.setIsaControlNumber(nonNull(row.get("isa_number")) ? (String) row.get("isa_number") : "-");
				warehouseOrder.setGsControlNumber(
						nonNull(row.get("gs_control_number")) ? (String) row.get("gs_control_number") : "-");
				warehouseOrder.setStControlNumber(
						nonNull(row.get("st_control_number")) ? (String) row.get("st_control_number") : "-");
				warehouseOrder.setIsaDate(nonNull(row.get("isa_date")) ? (String) row.get("isa_date") : "-");
				warehouseOrder.setIsaTime(nonNull(row.get("isa_time")) ? (String) row.get("isa_time") : "-");
				warehouseOrder.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
				warehouseOrder
						.setErrorMessage(nonNull(row.get("err_message")) ? (String) row.get("err_message") : "NO MSG");
				if (nonNull(row.get("pre_trans_filepath"))) {
					warehouseOrder.setPreTransFilePath((String) row.get("pre_trans_filepath"));
				} else {
					warehouseOrder.setPreTransFilePath("No File");
				}
				if (nonNull(row.get("post_trans_filepath"))) {
					warehouseOrder.setPostTransFilePath((String) row.get("post_trans_filepath"));
				} else {
					warehouseOrder.setPostTransFilePath("No File");
				}
				if (nonNull(row.get("ack_filepath"))) {
					warehouseOrder.setAckFilePath((String) row.get("ack_filepath"));
				} else {
					warehouseOrder.setAckFilePath("No File");
				}
				if (nonNull(row.get("error_report_filepath"))) {
					warehouseOrder.setErrorReportFilePath((String) row.get("error_report_filepath"));
				} else {
					warehouseOrder.setErrorReportFilePath("No File");
				}
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " detailInfo :: " + exception.getMessage());
		}
		return warehouseOrder;
	}

	/**
	 * Gets the warehouse search query.
	 *
	 * @param searchCriteria the search criteria
	 * @return the warehouse search query
	 * @throws Exception
	 */
	private Map<String, Object> getWarehouseSearchQuery(final SearchCriteria searchCriteria) throws Exception {
		Map<String, Object> result = new HashMap<>();
		final StringBuilder warehouseOrderSearchQuery = new StringBuilder();
		final StringBuilder searchFieldsQuery = new StringBuilder();
		final StringBuilder groupByAndPageQuery = new StringBuilder();
		String database = searchCriteria.getDatabase();
		String status = searchCriteria.getStatus();
		String doctype = searchCriteria.getTransactionType();
		String toDate = searchCriteria.getToDate();
		String fromDate = searchCriteria.getFromDate();
		String corrAttribute = searchCriteria.getCorrAttribute();
		String corrValue = searchCriteria.getCorrValue();
		String corrAttribute1 = searchCriteria.getCorrAttribute1();
		String corrValue1 = searchCriteria.getCorrValue1();
		String corrAttribute2 = searchCriteria.getCorrAttribute2();
		String corrValue2 = searchCriteria.getCorrValue2();
		final List<String> partnerName = searchCriteria.getPartnerName();
		String userId = tokenAuthenticationService.getUserIdfromToken();
		String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
		final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
		boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAll").toString());
		List<Object> params = new ArrayList<>();
		warehouseOrderSearchQuery.append("SELECT distinct(f.file_id), f.id, f.transaction_type, f.direction, "
				+ "f.status, f.ack_status, f.sender_id, f.receiver_id, f.sec_key_val, "
				+ "CONVERT_TZ(f.date_time_received, ?, ?) as date_time_received, f.reprocessstatus, f.warehouse, f.parent_warehouse, f.isa_number,"
				+ " f.gs_control_number, f.st_control_number,f.err_file_id, f.filename FROM ");
		params.add(defaultTimeZone);
		params.add(userTimeZone);
		searchFieldsQuery.append(database.equals("ARCHIVE") ? "archive_files f" : "files f ");
		if (all) {
			searchFieldsQuery.append(" WHERE 1=1 and f.flowflag = 'M'");
		} else if (!all) {
			searchFieldsQuery.append(
					"join partner_visibilty pv on (pv.partner_id=f.sender_id or pv.partner_id=f.receiver_id) WHERE 1=1 AND pv.user_id=?");
			params.add(userId);
		}
		if (nonNull(fromDate) && !"".equals(fromDate)) {
			searchFieldsQuery.append(" AND f.date_time_received >= CONVERT_TZ(?, ?, ?)");
			params.add(convertToSqlDate(fromDate));
			params.add(userTimeZone);
			params.add(defaultTimeZone);
		}
		if (nonNull(toDate) && !"".equals(toDate)) {
			searchFieldsQuery.append(" AND f.date_time_received <= CONVERT_TZ(?, ?, ?)");
			params.add(convertToSqlDate(toDate));
			params.add(userTimeZone);
			params.add(defaultTimeZone);
		}
		if (nonNull(doctype) && "-1".equals(doctype)) {
			searchFieldsQuery.append(" AND (transaction_type like '9%' or transaction_type like '8%') ");
		}
		if (nonNull(doctype) && !"-1".equals(doctype)) {
			searchFieldsQuery.append(equalOperator("f.transaction_type"));
			params.add(doctype);
		}
		if (nonNull(status) && !"-1".equals(status)) {
			searchFieldsQuery.append(equalOperator("f.status"));
			params.add(status);
		}
		if (isValidList(searchCriteria.getAckStatus1())) {
		    searchFieldsQuery.append(equalOperatorWithOrAnd("f.ack_status", searchCriteria.getAckStatus1().size()));
		    params.addAll(searchCriteria.getAckStatus1());
		}
		if (isValidList(searchCriteria.getWarehouse())) {
		    searchFieldsQuery.append(equalOperatorWithOrAnd("f.parent_warehouse", searchCriteria.getWarehouse().size()));
		    params.addAll(searchCriteria.getWarehouse());
		}
		if (isValidList(searchCriteria.getParentWarehouse())) {
		    searchFieldsQuery.append(equalOperatorWithOrAnd("f.warehouse", searchCriteria.getParentWarehouse().size()));
		    params.addAll(searchCriteria.getParentWarehouse());
		}
		if (nonNull(corrAttribute)
				&& (corrAttribute.equals("Depositor Order Number")
						|| corrAttribute.equals("Customer Adjustment Number"))
				&& nonNull(corrValue) && !"".equals(corrValue.trim())) {
			searchFieldsQuery.append(likeOperatorStartWith("f.sec_key_val"));
			params.add(corrValue.trim() + " % ");
		}
		if (nonNull(corrAttribute1)
				&& (corrAttribute1.equals("Depositor Order Number")
						|| corrAttribute1.equals("Customer Adjustment Number"))
				&& nonNull(corrValue1) && !"".equals(corrValue1.trim())) {
			searchFieldsQuery.append(likeOperatorStartWith("f.sec_key_val"));
			params.add(corrValue1.trim() + " % ");
		}
		if (nonNull(corrAttribute2)
				&& (corrAttribute2.equals("Depositor Order Number")
						|| corrAttribute2.equals("Customer Adjustment Number"))
				&& nonNull(corrValue2) && !"".equals(corrValue2.trim())) {
			searchFieldsQuery.append(likeOperatorStartWith("f.sec_key_val"));
			params.add(corrValue2.trim() + " % ");
		}
		if (listToString(partnerName) != "" && !"'All'".equals(listToString(partnerName))) {
			searchFieldsQuery.append(equalOperatorWithOrAnd("(f.partnerName")).append(")");
			params.add(listToString(partnerName).toString());
		}
		if (nonNull(corrAttribute)
				&& (corrAttribute.equals("Shipment Number") || corrAttribute.equals("Warehouse adjustment Number")
						|| corrAttribute.equals("Warehouse Receipt Number") || corrAttribute.equals("PO Number"))
				&& nonNull(corrValue) && !"".equals(corrValue.trim())) {
			if (corrAttribute.equals("Warehouse adjustment Number")) {
				searchFieldsQuery.append(likeOperatorStartWith(
						" (case when transaction_type=947 then sec_key_val else f.pri_key_val end)"));
				params.add(corrValue.trim() + " % ");
			} else {
				searchFieldsQuery.append(likeOperatorStartWith("f.pri_key_val"));
				params.add(corrValue.trim() + " % ");
			}
		}
		if (nonNull(corrAttribute1)
				&& (corrAttribute1.equals("Shipment Number") || corrAttribute1.equals("Warehouse adjustment Number")
						|| corrAttribute1.equals("Warehouse Receipt Number") || corrAttribute.equals("PO Number"))
				&& nonNull(corrValue1) && !"".equals(corrValue1.trim())) {
			if (corrAttribute1.equals("Warehouse adjustment Number")) {
				searchFieldsQuery.append(likeOperatorStartWith(
						" (case when transaction_type=947 then sec_key_val else f.pri_key_val end)"));
				params.add(corrValue1.trim() + " % ");
			} else {
				searchFieldsQuery.append(likeOperatorStartWith("f.pri_key_val"));
				params.add(corrValue1.trim() + " % ");
			}
		}
		if (nonNull(corrAttribute2)
				&& (corrAttribute2.equals("Shipment Number") || corrAttribute2.equals("Warehouse adjustment Number")
						|| corrAttribute2.equals("Warehouse Receipt Number") || corrAttribute.equals("PO Number"))
				&& nonNull(corrValue2) && !"".equals(corrValue2.trim())) {
			if (corrAttribute2.equals("Warehouse adjustment Number")) {
				searchFieldsQuery.append(likeOperatorStartWith(
						" (case when transaction_type=947 then sec_key_val else f.pri_key_val end)"));
				params.add(corrValue2.trim() + " % ");
			} else {
				searchFieldsQuery.append(likeOperatorStartWith("f.pri_key_val"));
				params.add(corrValue2.trim() + " % ");
			}
		}
		if (nonNull(corrAttribute) && corrAttribute.equals("Instance Id") && nonNull(corrValue)
				&& !"".equals(corrValue.trim())) {
			searchFieldsQuery.append(likeOperatorStartWith("f.file_id"));
			params.add(corrValue.trim() + " % ");
		}
		if (nonNull(corrAttribute1) && corrAttribute1.equals("Instance Id") && nonNull(corrValue1)
				&& !"".equals(corrValue1.trim())) {
			searchFieldsQuery.append(likeOperatorStartWith("f.file_id"));
			params.add(corrValue1.trim() + " % ");
		}
		if (nonNull(corrAttribute2) && corrAttribute2.equals("Instance Id") && nonNull(corrValue2)
				&& !"".equals(corrValue2.trim())) {
			searchFieldsQuery.append(likeOperatorStartWith("f.file_id"));
			params.add(corrValue2.trim() + " % ");
		}
		if (nonNull(corrAttribute) && corrAttribute.equals("Direction") && nonNull(corrValue)
				&& !"".equals(corrValue.trim())) {
			searchFieldsQuery.append(likeOperatorStartWith("f.direction"));
			params.add(corrValue.trim() + " % ");
		}
		if (nonNull(corrAttribute1) && corrAttribute1.equals("Direction") && nonNull(corrValue1)
				&& !"".equals(corrValue1.trim())) {
			searchFieldsQuery.append(likeOperatorStartWith("f.direction"));
			params.add(corrValue1.trim() + " % ");
		}
		if (nonNull(corrAttribute2) && corrAttribute2.equals("Direction") && nonNull(corrValue2)
				&& !"".equals(corrValue2.trim())) {
			searchFieldsQuery.append(likeOperatorStartWith("f.direction"));
			params.add(corrValue2.trim() + " % ");
		}
		// groupByAndPageQuery.append(" group by f.id");
		if (nonNull(searchCriteria.getSortField()) && nonNull(searchCriteria.getSortOrder())) {
			groupByAndPageQuery
					.append(dataSourceDataProvider.criteriaForSortingAndPagination(searchCriteria.getSortField(),
							searchCriteria.getSortOrder(), searchCriteria.getLimit(), searchCriteria.getOffSet()));
		}
		result.put("mainQuery",
				warehouseOrderSearchQuery.toString() + searchFieldsQuery.toString() + groupByAndPageQuery.toString());
		result.put("countQuery", "SELECT COUNT(f.id) FROM " + searchFieldsQuery.toString());
		result.put("params", params);
		result.put("countParams", params.subList(2, params.size()));
		return result;
	}

	/**
	 * wareHouseDocument types.
	 *
	 * @param database the database
	 * @return the list
	 */
	public List<String> wareHouseDocumentType(final String database) {
		final List<String> documentTypeList = new ArrayList<>();
		final StringBuilder wareHouseDocumentTypeQuery = new StringBuilder();
		wareHouseDocumentTypeQuery.append("SELECT distinct(transaction_type) as transaction  FROM ");
		wareHouseDocumentTypeQuery.append(nonNull(database) && database.equals("LIVE") ? "files" : "archive_files");
		wareHouseDocumentTypeQuery
				.append(" where transaction_type like '9%' or transaction_type like '8%' ORDER BY transaction ASC");
		try {
			final List<String> rows = jdbcTemplate.queryForList(wareHouseDocumentTypeQuery.toString(), String.class);
			for (final String row : rows) {
				documentTypeList.add(row);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " documentTypes :: " + exception.getMessage());
		}
		return documentTypeList;
	}

}
