package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.DateUtility.convertToSqlDate;
import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;
import static com.miraclesoft.scvp.util.SqlCondition.equalOperator;
import static com.miraclesoft.scvp.util.SqlCondition.equalOperatorWithOrAnd;
import static com.miraclesoft.scvp.util.SqlCondition.isValidList;
import static com.miraclesoft.scvp.util.SqlCondition.likeOperatorStartWith;
import static java.util.Objects.nonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StreamUtils;
import org.springframework.web.client.RestTemplate;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ListObjectsRequest;
import com.amazonaws.services.s3.model.ObjectListing;
import com.amazonaws.services.s3.model.S3ObjectSummary;
import com.miraclesoft.scvp.mail.MailManager;
import com.miraclesoft.scvp.model.Configurations;
import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.reports.Report;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.service.UtilizationService;
import com.miraclesoft.scvp.util.AwsS3Util;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

/**
 * The Class DocumentRepositoryServiceImpl.
 *
 * @author Narendar Geesidi
 */
@Component
@SuppressWarnings("PMD.TooManyStaticImports")
public class DocumentRepositoryServiceImpl {

	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	/** The report. */
	@Autowired
	private Report report;

//	@Value("${bpUrl}")
//	private String bpUrl;

	/** The data source data provider. */
	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;

	/** The MailManager data provider. */
	@Autowired
	private MailManager mailManager;

	/** The AwsS3Util data provider. */
	@Autowired
	private AwsS3Util awsS3Util;

	@Autowired
	private AmazonS3 amazonS3;

	@Autowired
	private HttpServletRequest httpServletRequest;
	@Autowired
	HttpServletResponse response;

	@Autowired
	private UtilizationService utilizationService;

	@Autowired

	private Configurations configurations;

	private static final int SUCCESS = 200;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(DocumentRepositoryServiceImpl.class.getName());

	/**
	 * Search.
	 *
	 * @param searchCriteria the search criteria
	 * @return the list
	 */
	@SuppressWarnings("unchecked")
	public CustomResponse search(final SearchCriteria searchCriteria) {
		final List<DocumentRepository> documentRepositories = new ArrayList<DocumentRepository>();
		int count = 0;
		try {
			Map<String, Object> queryMap = dcmntRepositorySearch(searchCriteria);
			String mainQuery = (String) queryMap.get("mainQuery");
			String countQuery = (String) queryMap.get("countQuery");
			List<Object> params = (List<Object>) queryMap.get("params");
			List<Object> countParams = (List<Object>) queryMap.get("countParams");

			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(mainQuery, params.toArray());
			if (searchCriteria.getCountFlag()) {
				count = jdbcTemplate.queryForObject(countQuery, countParams.toArray(), Integer.class);
			}
			// final Map<String, String> tradingPartners =
			// dataSourceDataProvider.allTradingPartners();
			for (final Map<String, Object> row : rows) {
				final DocumentRepository documentRepository = new DocumentRepository();
				documentRepository.setId((Long) row.get("id"));
				documentRepository.setFileId(nonNull(row.get("file_id")) ? (String) row.get("file_id") : "-");
				documentRepository.setSenderId(nonNull(row.get("sender_id")) ? (String) row.get("sender_id") : "-");
				documentRepository.setFileType(nonNull(row.get("file_type")) ? (String) row.get("file_type") : "-");
				documentRepository.setTransactionType(
						nonNull(row.get("transaction_type")) ? (String) row.get("transaction_type") : "-");
				final String direction = nonNull(row.get("direction")) ? (String) row.get("direction") : "-";
				documentRepository.setDirection(direction);
				documentRepository
						.setDeliveredTo(nonNull(row.get("delivered_To")) ? (String) row.get("delivered_To") : "-");
				documentRepository.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
				documentRepository.setAckStatus(nonNull(row.get("ack_status")) ? (String) row.get("ack_status") : "-");
				documentRepository.setDateTimeReceived(nonNull(row.get("date_time_received"))
						? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("date_time_received"))
						: "-");
//				String partnerName = "-";
//				if ("INBOUND".equalsIgnoreCase(direction) && nonNull(row.get("sender_id"))
//						&& nonNull(tradingPartners.get(row.get("sender_id")))) {
//					partnerName = tradingPartners.get(row.get("sender_id"));
//				} else if ("OUTBOUND".equalsIgnoreCase(direction) && nonNull(row.get("receiver_id"))
//						&& nonNull(tradingPartners.get(row.get("receiver_id")))) {
//					partnerName = tradingPartners.get(row.get("receiver_id"));
//				}
//				documentRepository.setPartnerName(partnerName);
				documentRepository
						.setPartnerName(nonNull(row.get("partnerName")) ? (String) row.get("partnerName") : "-");
				documentRepository.setReProcessStatus(
						nonNull(row.get("reprocessstatus")) ? (String) row.get("reprocessstatus") : "-");
				documentRepository
						.setPrimaryKeyValue(nonNull(row.get("pri_key_val")) ? (String) row.get("pri_key_val") : "-");
				documentRepository
						.setShipmentId(nonNull(row.get("sec_key_val")) ? (String) row.get("sec_key_val") : "-");
				documentRepository.setFileName(nonNull(row.get("filename")) ? (String) row.get("filename") : "-");
				// documentRepository.setPostTransFileName(nonNull(row.get("post_trans_filename"))
				// ? (String) row.get("post_trans_filename") : "-");
				documentRepository.setWarehouse(
						nonNull(row.get("parent_warehouse")) ? (String) row.get("parent_warehouse") : "-");
				documentRepository
						.setParentWarehouse(nonNull(row.get("warehouse")) ? (String) row.get("warehouse") : "-");
				documentRepository
						.setIsaControlNumber(nonNull(row.get("isa_number")) ? (String) row.get("isa_number") : "-");
				documentRepository.setGsControlNumber(
						nonNull(row.get("gs_control_number")) ? (String) row.get("gs_control_number") : "-");
				documentRepository.setStControlNumber(
						nonNull(row.get("st_control_number")) ? (String) row.get("st_control_number") : "-");
				documentRepository
						.setErrFileId(nonNull(row.get("err_file_id")) ? (String) row.get("err_file_id") : "-");

//				documentRepository.setSapId(nonNull(row.get("sap_id")) ? (String) row.get("sap_id") : "-");

				if (nonNull(row.get("sap_id"))) {
					String sapId = (String) row.get("sap_id");
					// Strip leading zeroes here
					sapId = sapId.replaceFirst("^0+(?!$)", "");

					documentRepository.setSapId(sapId);
				} else {
					documentRepository.setSapId("-");
				}

				documentRepositories.add(documentRepository);
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, " search :: " + exception.getMessage());
		}
		return new CustomResponse(documentRepositories, count);
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
		return getInputStreamResource(new File(
				report.documentRepositoryExcelDownload((List<DocumentRepository>) search(searchCriteria).getData())));
	}

	/**
	 * Detail info.
	 *
	 * @param id       the id
	 * @param database the database
	 * @return the document repository
	 */

	public DocumentRepository detailInfo(final Long id, final String database) {
		final DocumentRepository documentRepository = new DocumentRepository();
		try {
			final StringBuilder documentRepositoryDetailInfoQuery = new StringBuilder();
			documentRepositoryDetailInfoQuery
					.append("SELECT f.id, f.parent_file_id, f.file_id, f.transaction_type, f.file_type, "
							+ "f.pri_key_val, f.sec_key_val, f.pri_key_type, f.sender_id, f.receiver_id, f.isa_number, "
							+ "f.gs_control_number, f.st_control_number, f.isa_date, f.isa_time, f.status,fa.ack_status, fa.ack_file_path, f.pre_trans_filepath, "
							+ "f.post_trans_filepath, f.ack_file_id, f.err_message, f.error_report_filepath,f.ack_filepath,f.err_file_id FROM ");
			documentRepositoryDetailInfoQuery.append(database.equals("ARCHIVE") ? "archive_files f" : "files f");
			documentRepositoryDetailInfoQuery.append(" LEFT JOIN m_functionalAck fa ON f.parent_file_id  = fa.fileID");
			documentRepositoryDetailInfoQuery.append(" WHERE flowflag = 'M' AND f.id = ?");
			final List<Map<String, Object>> rows = jdbcTemplate
					.queryForList(documentRepositoryDetailInfoQuery.toString(), id);
			logger.log(Level.INFO, "detailInfo query:  " + documentRepositoryDetailInfoQuery);
			final Map<String, String> tradingPartners = dataSourceDataProvider.allTradingPartners();
			for (final Map<String, Object> row : rows) {
				documentRepository
						.setErrFileId(nonNull(row.get("err_file_id")) ? (String) row.get("err_file_id") : "-");
				documentRepository.setFileId(nonNull(row.get("file_id")) ? (String) row.get("file_id") : "-");
				documentRepository.setTransactionType(
						nonNull(row.get("transaction_type")) ? (String) row.get("transaction_type") : "-");
				documentRepository.setFileType(nonNull(row.get("file_type")) ? (String) row.get("file_type") : "-");
				documentRepository
						.setPrimaryKeyType(nonNull(row.get("pri_key_type")) ? (String) row.get("pri_key_type") : "-");
				documentRepository
						.setPrimaryKeyValue(nonNull(row.get("pri_key_val")) ? (String) row.get("pri_key_val") : "-");
				documentRepository
						.setSecondaryKeyValue(nonNull(row.get("sec_key_val")) ? (String) row.get("sec_key_val") : "-");
				String senderName = "-";
				if (nonNull(row.get("sender_id"))) {
					documentRepository.setSenderId((String) row.get("sender_id"));
					if (nonNull(tradingPartners.get(row.get("sender_id")))) {
						senderName = tradingPartners.get(row.get("sender_id"));
					}
				} else {
					documentRepository.setSenderId("-");
				}
				documentRepository.setSenderName(senderName);
				String recieverName = "-";
				if (nonNull(row.get("receiver_id"))) {
					documentRepository.setReceiverId((String) row.get("receiver_id"));
					if (nonNull(tradingPartners.get(row.get("receiver_id")))) {
						recieverName = tradingPartners.get(row.get("receiver_id"));
					}
				} else {
					documentRepository.setReceiverId("-");
				}
				documentRepository.setReceiverName(recieverName);
				documentRepository
						.setIsaControlNumber(nonNull(row.get("isa_number")) ? (String) row.get("isa_number") : "-");
				documentRepository.setGsControlNumber(
						nonNull(row.get("gs_control_number")) ? (String) row.get("gs_control_number") : "-");
				documentRepository.setStControlNumber(
						nonNull(row.get("st_control_number")) ? (String) row.get("st_control_number") : "-");
				documentRepository.setIsaDate(nonNull(row.get("isa_date")) ? (String) row.get("isa_date") : "-");
				documentRepository.setIsaTime(nonNull(row.get("isa_time")) ? (String) row.get("isa_time") : "-");
				documentRepository.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
				documentRepository
						.setErrorMessage(nonNull(row.get("err_message")) ? (String) row.get("err_message") : "NO MSG");
				logger.log(Level.INFO, "detailInfo pre_trans_filepath:  " + row.get("pre_trans_filepath"));
				if (nonNull(row.get("pre_trans_filepath"))) {
					logger.log(Level.INFO,
							"detailInfo pre_trans_filepath in if condition:  " + row.get("pre_trans_filepath"));
					documentRepository.setPreTransFilePath((String) row.get("pre_trans_filepath"));
				} else {
					logger.log(Level.INFO,
							"detailInfo pre_trans_filepath in else condition:  " + row.get("pre_trans_filepath"));
					documentRepository.setPreTransFilePath("No File");
				}
				if (nonNull(row.get("post_trans_filepath"))) {
					logger.log(Level.INFO,
							"detailInfo post_trans_filepath IN IF CONDITION :  " + row.get("post_trans_filepath"));
					documentRepository.setPostTransFilePath((String) row.get("post_trans_filepath"));
				} else {
					logger.log(Level.INFO,
							"detailInfo post_trans_filepath in else condition:  " + row.get("post_trans_filepath"));
					documentRepository.setPostTransFilePath("No File");
				}
				logger.log(Level.INFO, "detailInfo ack_filepath :  " + row.get("ack_filepath"));
				if (nonNull(row.get("ack_file_path"))) {
					logger.log(Level.INFO, "detailInfo ack_file_path in if condition:  " + row.get("ack_file_path"));
					documentRepository.setAckFilePath((String) row.get("ack_file_path"));

				} else {
					logger.log(Level.INFO, "detailInfo ack_file_path in else condition:  " + row.get("ack_file_path"));
					documentRepository.setAckFilePath("No File");
				}
				logger.log(Level.INFO, "detailInfo ack_filepath:  " + row.get("error_report_filepath"));
				if (nonNull(row.get("error_report_filepath"))) {
					logger.log(Level.INFO,
							"detailInfo ack_filepath in if condition:  " + row.get("error_report_filepath"));
					documentRepository.setErrorReportFilePath((String) row.get("error_report_filepath"));
				} else {
					logger.log(Level.INFO,
							"detailInfo ack_filepath in else condition:  " + row.get("error_report_filepath"));
					documentRepository.setErrorReportFilePath("No File");
				}
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, " detailInfo :: " + exception.getMessage());
		}
		return documentRepository;
	}

	/**
	 * Search by status.
	 *
	 * @param status the status
	 * @return the list
	 */
	public CustomResponse searchByStatus(final SearchCriteria searchCriteria) {
		final List<DocumentRepository> documentRepositories = new ArrayList<DocumentRepository>();
		int count = 0;
		List<Object> params = new ArrayList<>();
		try {
			final StringBuilder searchQuery = new StringBuilder();
			final StringBuilder criteriaForSearchQuery = new StringBuilder();
			final StringBuilder sortingAndPaginationQuery = new StringBuilder();
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
			final int userId = Integer.parseInt(tokenAuthenticationService.getUserIdfromToken());
			boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAll").toString());
			String joinQuery = !all ? dataSourceDataProvider.partnersJoinCondition().toString() : "";
			String userIdRequired = !all ? " AND pv.user_id = ? " : " ";
			params.add(userId);
			searchQuery.append(
					"SELECT f.id, file_id, file_type, transaction_type, direction, status, ack_status,  CONVERT_TZ(f.date_time_received, ?, ?) as date_time_received, "
							+ "warehouse, parent_warehouse, sender_id, receiver_id, reprocessstatus, pri_key_val, sec_key_val, filename FROM files f ")
					.append(joinQuery);
			params.add(defaultTimeZone);
			params.add(userTimeZone);
			if ("Success".equals(searchCriteria.getStatus())) {
				criteriaForSearchQuery.append(
						"WHERE ((status = 'SUCCESS' OR status = 'DROPPED') AND (ack_status = 'ACCEPTED' OR ack_status = '200' OR ack_status = '201')) ")
						.append(userIdRequired);
			} else if ("Failure".equals(searchCriteria.getStatus())) {
				criteriaForSearchQuery
						.append("WHERE (status = 'ERROR' AND (ack_status = 'REJECTED' OR ack_status = '400')) ")
						.append(userIdRequired);
			} else if ("Pending".equals(searchCriteria.getStatus())) {
				criteriaForSearchQuery.append("WHERE (status = 'SUCCESS' AND ack_status = 'OVERDUE') ")
						.append(userIdRequired);
			}
			criteriaForSearchQuery.append(" AND (date_time_received >= CURRENT_DATE)");
			if (nonNull(searchCriteria.getSortField()) && nonNull(searchCriteria.getSortOrder())) {
				sortingAndPaginationQuery
						.append(dataSourceDataProvider.criteriaForSortingAndPagination(searchCriteria.getSortField(),
								searchCriteria.getSortOrder(), searchCriteria.getLimit(), searchCriteria.getOffSet()));
			}
			searchQuery.append(criteriaForSearchQuery).append(sortingAndPaginationQuery);
			if (searchCriteria.getCountFlag()) {
				count = jdbcTemplate.queryForObject(
						"select count(id) FROM files f " + joinQuery + criteriaForSearchQuery, params.toArray(),
						Integer.class);
			}
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(searchQuery.toString(), params.toArray());
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
				documentRepository.setWarehouse(
						nonNull(row.get("parent_warehouse")) ? (String) row.get("parent_warehouse") : "-");
				documentRepository
						.setParentWarehouse(nonNull(row.get("warehouse")) ? (String) row.get("warehouse") : "-");
				documentRepositories.add(documentRepository);
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, " searchByStatus :: " + exception.getMessage());
		}
		return new CustomResponse(documentRepositories, count);
	}

	/**
	 * Gets the document repository search query.
	 *
	 * @param searchCriteria the search criteria
	 * @return the document repository search query
	 * @throws Exception
	 */
	private Map<String, Object> dcmntRepositorySearch(final SearchCriteria search) throws Exception {
		Map<String, Object> result = new HashMap<>();
		final StringBuilder documentSearchQuery = new StringBuilder();
		final StringBuilder searchFieldQuery = new StringBuilder();
		final StringBuilder groupByAndPageQuery = new StringBuilder();
		final String database = search.getDatabase();
		final String status = search.getStatus();
		final String direction = search.getDirection();
		final String doctype = search.getTransactionType();
		final String toDate = search.getToDate();
		final String fromDate = search.getFromDate();
		final String deliveredTo = search.getDeliveredTo();
		final String corrAttribute = search.getCorrAttribute();
		final String corrValue = search.getCorrValue();
		final String corrAttribute1 = search.getCorrAttribute1();
		final String corrValue1 = search.getCorrValue1();
		final String corrAttribute2 = search.getCorrAttribute2();
		final String corrValue2 = search.getCorrValue2();
		final List<String> warehouses = search.getWarehouse();
		final List<String> parentWarehouse = search.getParentWarehouse();
		final String documentNumber = search.getDocumentNumber();
		final List<String> partnerName = search.getPartnerName();
		final String ackStatus = search.getAckStatus();
		// final String userId = tokenAuthenticationService.getUserIdfromToken();
		final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
		final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
		final String sapId = search.getSapId();

		List<Object> params = new ArrayList<>();
		// boolean all =
		// Boolean.parseBoolean(httpServletRequest.getHeader("isAll").toString());
		documentSearchQuery.append(
				"SELECT f.id, f.parent_file_id, f.partnerName, f.file_id, f.file_type, f.transaction_type, f.direction, "
						+ "f.status, fa.ack_status, fa.ack_file_path, CONVERT_TZ(f.date_time_received,?,?) as date_time_received, f.warehouse, f.parent_warehouse, f.sender_id, f.receiver_id, "
						+ "f.reprocessstatus, f.pri_key_val, f.sec_key_val, f.filename, f.isa_number, f.gs_control_number, f.st_control_number,f.err_file_id, f.delivered_To,f.sap_id   FROM ");
		params.add(defaultTimeZone);
		params.add(userTimeZone);
		searchFieldQuery.append(database.equals("ARCHIVE") ? "archive_files f" : "files f");
		searchFieldQuery.append(
				" LEFT OUTER JOIN m_functionalAck fa ON f.parent_file_id = fa.fileID WHERE 1=1 and f.flowflag = 'M' ");
		if (nonNull(search.getParentFileId()) && !"-1".equals(search.getParentFileId())) {
			searchFieldQuery.append("and f.parent_file_id = ?");
			params.add(search.getParentFileId());
		}
		if (nonNull(fromDate) && !"".equals(fromDate)) {
			searchFieldQuery.append(" AND f.date_time_received >= CONVERT_TZ(?,?,?)");
			params.add(convertToSqlDate(fromDate));
			params.add(userTimeZone);
			params.add(defaultTimeZone);

		}
		if (nonNull(toDate) && !"".equals(toDate)) {
			searchFieldQuery.append(" AND f.date_time_received <= CONVERT_TZ(?,?,?)");
			params.add(convertToSqlDate(toDate));
			params.add(userTimeZone);
			params.add(defaultTimeZone);

		}
		System.out.println(deliveredTo);
		if (nonNull(deliveredTo) && !"-1".equals(deliveredTo)) {
			searchFieldQuery.append(equalOperator("f.delivered_To"));
			params.add(deliveredTo);
		}
//		if (ackStatus != "null" && !ackStatus.equals("'All'")) {
//			searchFieldQuery.append(equalOperatorWithOrAnd("fa.ack_status", ackStatus));
//		}
		if (nonNull(doctype) && !"-1".equals(doctype)) {
			searchFieldQuery.append(equalOperator("f.transaction_type"));
			params.add(doctype);
		}
		if (nonNull(direction) && !"-1".equals(direction)) {
			searchFieldQuery.append(equalOperator("f.direction"));
			params.add(direction);
		}
		if (nonNull(status) && !"-1".equals(status)) {
			searchFieldQuery.append(equalOperator("f.status"));
			params.add(status);
		}
		if (nonNull(ackStatus) && !"-1".equals(ackStatus)) {
			searchFieldQuery.append(equalOperator("fa.ack_status"));
			params.add(ackStatus);
		}
//		if (nonNull(warehouses) && !"-1".equals(warehouses)) {
//			searchFieldQuery.append(equalOperator("f.parent_warehouse", warehouses));
//		}
		if (nonNull(documentNumber) && !"".equals(documentNumber)) {
			searchFieldQuery.append(likeOperatorStartWith("f.pri_key_val"));
			params.add(documentNumber.toString() + "%");
		}
		if (isValidList(partnerName)) {
			searchFieldQuery.append(equalOperatorWithOrAnd("f.partnerName", partnerName.size()));
			params.addAll(partnerName);
		}

		if (isValidList(warehouses)) {
			searchFieldQuery.append(equalOperatorWithOrAnd("f.parent_warehouse", warehouses.size()));
			params.addAll(warehouses);
		}

		if (isValidList(parentWarehouse)) {
			searchFieldQuery.append(equalOperatorWithOrAnd("f.warehouse", parentWarehouse.size()));
			params.addAll(parentWarehouse);
		}
		if (nonNull(corrAttribute)
				&& (corrAttribute.equals("PO Number") || corrAttribute.equals("Depositor Order Number")
						|| corrAttribute.equals("Customer Adjustment Number"))
				&& nonNull(corrValue) && !"".equals(corrValue.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.sec_key_val"));
			params.add(corrValue.trim() + "%");
		}
		if (nonNull(corrAttribute1)
				&& (corrAttribute1.equals("PO Number") || corrAttribute1.equals("Depositor Order Number")
						|| corrAttribute1.equals("Customer Adjustment Number"))
				&& nonNull(corrValue1) && !"".equals(corrValue1.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.sec_key_val"));
			params.add(corrValue1.trim() + "%");
		}
		if (nonNull(corrAttribute2)
				&& (corrAttribute2.equals("PO Number") || corrAttribute2.equals("Depositor Order Number")
						|| corrAttribute2.equals("Customer Adjustment Number"))
				&& nonNull(corrValue2) && !"".equals(corrValue2.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.sec_key_val"));
			params.add(corrValue2.trim() + "%");
		}
		if (nonNull(corrAttribute)
				&& (corrAttribute.equals("Invoice Number") || corrAttribute.equals("Shipment Number")
						|| corrAttribute.equals("Cheque Number") || corrAttribute.equals("Warehouse adjustment Number")
						|| corrAttribute.equals("Warehouse Receipt Number"))
				|| corrAttribute.equals("OrderNumber") && nonNull(corrValue) && !"".equals(corrValue.trim())) {
			if (corrAttribute.equals("Warehouse adjustment Number")) {
				searchFieldQuery.append(likeOperatorStartWith(
						" (case when transaction_type=947 then sec_key_val else f.pri_key_val end)"));
				params.add(corrValue.trim() + "%");
			} else {
				searchFieldQuery.append(likeOperatorStartWith("f.pri_key_val"));
				params.add(corrValue.trim() + "%");
			}
		}
		if (nonNull(corrAttribute1)
				&& (corrAttribute1.equals("Invoice Number") || corrAttribute1.equals("Shipment Number")
						|| corrAttribute1.equals("Cheque Number")
						|| corrAttribute1.equals("Warehouse adjustment Number")
						|| corrAttribute1.equals("Warehouse Receipt Number"))
				|| corrAttribute.equals("OrderNumber") && nonNull(corrValue1) && !"".equals(corrValue1.trim())) {
			if (corrAttribute1.equals("Warehouse adjustment Number")) {
				searchFieldQuery.append(likeOperatorStartWith(
						" (case when transaction_type=947 then sec_key_val else f.pri_key_val end)"));
				params.add(corrValue1.trim() + "%");
			} else {
				searchFieldQuery.append(likeOperatorStartWith("f.pri_key_val"));
				params.add(corrValue1.trim() + "%");
			}
		}
		if (nonNull(corrAttribute2)
				&& (corrAttribute2.equals("Invoice Number") || corrAttribute2.equals("Shipment Number")
						|| corrAttribute2.equals("Cheque Number")
						|| corrAttribute2.equals("Warehouse adjustment Number")
						|| corrAttribute2.equals("Warehouse Receipt Number"))
				|| corrAttribute.equals("OrderNumber") && nonNull(corrValue2) && !"".equals(corrValue2.trim())) {
			if (corrAttribute2.equals("Warehouse adjustment Number")) {
				searchFieldQuery.append(likeOperatorStartWith(
						" (case when transaction_type=947 then sec_key_val else f.pri_key_val end)"));
				params.add(corrValue2.trim() + "%");
			} else {
				searchFieldQuery.append(likeOperatorStartWith("f.pri_key_val"));
				params.add(corrValue2.trim() + "%");
			}
		}
		if (nonNull(corrAttribute) && corrAttribute.equals("ISA Number") && nonNull(corrValue)
				&& !"".equals(corrValue.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.isa_number"));
			params.add(corrValue.trim() + "%");
		}
		if (nonNull(corrAttribute1) && corrAttribute1.equals("ISA Number") && nonNull(corrValue1)
				&& !"".equals(corrValue1.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.isa_number"));
			params.add(corrValue1.trim() + "%");
		}
		if (nonNull(corrAttribute2) && corrAttribute2.equals("ISA Number") && nonNull(corrValue2)
				&& !"".equals(corrValue2.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.isa_number"));
			params.add(corrValue2.trim() + "%");
		}
		if (nonNull(corrAttribute) && corrAttribute.equals("GS Number") && nonNull(corrValue)
				&& !"".equals(corrValue.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.gs_control_number"));
			params.add(corrValue.trim() + "%");
		}
		if (nonNull(corrAttribute1) && corrAttribute1.equals("GS Number") && nonNull(corrValue1)
				&& !"".equals(corrValue1.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.gs_control_number"));
			params.add(corrValue1.trim() + "%");
		}
		if (nonNull(corrAttribute) && corrAttribute.equals("GS Number") && nonNull(corrValue)
				&& !"".equals(corrValue.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.gs_control_number"));
			params.add(corrValue.trim() + "%");
		}
		if (nonNull(corrAttribute) && corrAttribute.equals("Instance Id") && nonNull(corrValue)
				&& !"".equals(corrValue.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.file_id"));
			params.add(corrValue.trim() + "%");
		}
		if (nonNull(corrAttribute1) && corrAttribute1.equals("Instance Id") && nonNull(corrValue1)
				&& !"".equals(corrValue1.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.file_id"));
			params.add(corrValue1.trim() + "%");
		}
		if (nonNull(corrAttribute2) && corrAttribute2.equals("Instance Id") && nonNull(corrValue2)
				&& !"".equals(corrValue2.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.file_id"));
			params.add(corrValue2.trim() + "%");
		}
		if (nonNull(corrAttribute) && corrAttribute.equals("Direction") && nonNull(corrValue)
				&& !"".equals(corrValue.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.direction"));
			params.add(corrValue.trim() + "%");
		}
		if (nonNull(corrAttribute1) && corrAttribute1.equals("Direction") && nonNull(corrValue1)
				&& !"".equals(corrValue1.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.direction"));
			params.add(corrValue1.trim() + "%");
		}
		if (nonNull(corrAttribute2) && corrAttribute2.equals("Direction") && nonNull(corrValue2)
				&& !"".equals(corrValue2.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.direction"));
			params.add(corrValue2.trim() + "%");
		}
		if (nonNull(corrAttribute) && corrAttribute.equals("File name") && nonNull(corrValue)
				&& !"".equals(corrValue.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.filename"));
			params.add(corrValue.trim() + "%");
		}
		if (nonNull(corrAttribute1) && corrAttribute1.equals("File name") && nonNull(corrValue1)
				&& !"".equals(corrValue1.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.filename"));
			params.add(corrValue1.trim() + "%");
		}
		if (nonNull(corrAttribute2) && corrAttribute2.equals("File name") && nonNull(corrValue2)
				&& !"".equals(corrValue2.trim())) {
			searchFieldQuery.append(likeOperatorStartWith("f.filename"));
			params.add(corrValue2.trim() + "%");
		}
//		groupByAndPageQuery.append(" group by f.id");
		if (nonNull(search.getSortField()) && nonNull(search.getSortOrder())) {
			groupByAndPageQuery.append(dataSourceDataProvider.criteriaForSortingAndPagination(search.getSortField(),
					search.getSortOrder(), search.getLimit(), search.getOffSet()));
		}
//		if (nonNull(sapId) && !sapId.trim().isEmpty() && !"-1".equals(sapId)) {
//			searchFieldQuery.append(equalOperator("f.sap_id"));
//			params.add(sapId.trim());
//		}

		if (nonNull(sapId) && !sapId.trim().isEmpty() && !"-1".equals(sapId)) {
			searchFieldQuery.append(" AND CAST(f.sap_id AS UNSIGNED) LIKE ?");
			params.add("%" + sapId.trim().replaceFirst("^0+(?!$)", "") + "%");
		}

		result.put("mainQuery",
				documentSearchQuery.toString() + searchFieldQuery.toString() + groupByAndPageQuery.toString());
		result.put("countQuery", "SELECT COUNT(f.id) FROM " + searchFieldQuery.toString());
		result.put("params", params);
		result.put("countParams", params.subList(2, params.size()));
		return result;
	}

	/**
	 * Sets the primary key type.
	 *
	 * @param key the key
	 * @return the string
	 */
	private String setPrimaryKeyType(final String key) {
		String value = "-";
		if (key.equals("PO")) {
			value = "PO";
		} else if (key.equals("ASN")) {
			value = "ASN";
		} else if (key.equals("IN")) {
			value = "Invoice";
		} else if (key.equals("PAYMENT")) {
			value = "Cheque";
		} else if (key.equals("Inventory")) {
			value = "Inventory";
		}
		return value;
	}

	/**
	 * Reprocess request.
	 *
	 * @param searchCriteria the searchCriteria
	 * @return the map object
	 */
	public Map<String, Object> reprocessRequest(final SearchCriteria searchCriteria) {
		final Map<String, Object> response = new HashMap<>();

		// SearchCriteria element;
		String xml = "";
		String result = "";
		String finalData = "";

		// Response from the db

		// Preparing the data that needs to be send when the ssl is not true

		xml = convertToXMLForBp(searchCriteria);
		finalData += xml;

		result = "<?xml version='1.0' encoding='UTF-8'?>" + finalData;
		// xml converted string for each element in the list

		try {
			final String uriForWithoutSsl = configurations.getB2bReprocessUrl();

			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);
			final HttpEntity<String> entityForWithoutSsl = new HttpEntity<String>(result, headers);
			final RestTemplate restTemplate = new RestTemplate();
			final ResponseEntity<String> data = restTemplate.postForEntity(uriForWithoutSsl, entityForWithoutSsl,
					String.class);
			if (data.getStatusCodeValue() == SUCCESS) {
				response.put("success", true);
				response.put("message", "Bp sent");
			}
		} catch (final Exception e) {
			logger.log(Level.ERROR, " reprocessRequest :: " + e.getMessage());
			response.put("success", false);
			response.put("message", "Bp failed to sent");
		}

		return response;

	}

	/**
	 * convertToXMLForBp.
	 *
	 * @param data the data
	 * @return the string
	 */
	public String convertToXMLForBp(final SearchCriteria data) {
		final StringBuilder xml = new StringBuilder();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"select receiver_id, transaction_type, sender_id, mailbox_name, pre_trans_filepath, file_id, file_type, direction, filename,"
							+ " sec_key_val from files where id = ?",
					data.getId());
			if (!rows.isEmpty()) {
				for (final Map<String, Object> row : rows) {
					data.setInstanceId(
							nonNull(row.get("file_id")) ? Integer.parseInt(row.get("file_id").toString()) : 0);
					data.setMailBoxPath(nonNull(row.get("mailbox_name")) ? row.get("mailbox_name").toString() : "");
					data.setSecKeyVal(nonNull(row.get("sec_key_val")) ? row.get("sec_key_val").toString() : "");
					data.setTransactionType(
							nonNull(row.get("transaction_type")) ? row.get("transaction_type").toString() : "");
					data.setFileName(nonNull(row.get("filename")) ? row.get("filename").toString() : "");
					data.setDirection(nonNull(row.get("direction")) ? row.get("direction").toString() : "");
					data.setPreTranslationPath(
							nonNull(row.get("pre_trans_filepath")) ? row.get("pre_trans_filepath").toString() : "");
					data.setDocumentFormat(nonNull(row.get("file_type")) ? row.get("file_type").toString() : "");
					data.setMailBoxPath(nonNull(row.get("mailbox_name")) ? row.get("mailbox_name").toString() : "");
					data.setSenderId(nonNull(row.get("sender_id")) ? row.get("sender_id").toString() : "");
					data.setReceiveId(nonNull(row.get("receiver_id")) ? row.get("receiver_id").toString() : "");

				}
			} else {
				data.setInstanceId(0);
				data.setMailBoxPath("");
				data.setSecKeyVal("");
				data.setTransactionType("");
				data.setFileName("");
				data.setDirection("");
				data.setPreTranslationPath("");
				data.setDocumentFormat("");
				data.setMailBoxPath("");
				data.setSenderId("");
				data.setReceiveId("");
			}
			xml.append("<root> <Instance_id>").append(data.getInstanceId()).append("</Instance_id> <Transaction_type>")
					.append(data.getTransactionType()).append("</Transaction_type> <File_name>")
					.append(data.getFileName()).append("</File_name> <Direction>").append(data.getDirection())
					.append("</Direction> <PreTranslation_Path>").append(data.getPreTranslationPath())
					.append("</PreTranslation_Path> <Document_format>").append(data.getDocumentFormat())
					.append("</Document_format> <Mailboxpath>").append(data.getMailBoxPath())
					.append("</Mailboxpath> <Sender_id>").append(data.getSenderId())
					.append("</Sender_id> <Receiver_id>").append(data.getReceiveId())
					.append("</Receiver_id> <Bucket_Filename>");
			String fileName = "";
			if (data.getPreTranslationPath() != null && data.getPreTranslationPath().length() > 0) {
				fileName = new StringBuilder(data.getPreTranslationPath()).reverse().toString();
				final int indexForlash = fileName.indexOf("/");
				fileName = new StringBuilder(fileName.substring(0, indexForlash)).reverse().toString();
			}
			xml.append(fileName).append("</Bucket_Filename> <sec_key_val>").append(data.getSecKeyVal())
					.append("</sec_key_val> </root>");
		} catch (final Exception e) {
			logger.log(Level.ERROR, " reprocessRequest :: " + e.getMessage());
		}
		return xml.toString();
	}

	/**
	 * transactionsDetailAttachment.
	 *
	 * @param documentRepository the DocumentRepository
	 * @return the Map
	 */
	public Map<String, Object> transactionsDetailAttachment(final DocumentRepository documentRepository) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to sent email");
		response.put("success", false);
		List<byte[]> fileAttachmentData = new ArrayList<>();
		System.out.println("documentRepository.getFilePath()" + documentRepository.getFilePath());
		System.out.println("documentRepository.getFilePath().size()" + documentRepository.getFilePath().size());
		if (documentRepository.getFilePath().size() > 0) {

			for (int i = 0; i < documentRepository.getFilePath().size(); i++) {
				System.out
						.println("documentRepository.getFilePath().get(i))" + documentRepository.getFilePath().get(i));
				if (awsS3Util.checkObject(documentRepository.getFilePath().get(i))) {
					byte[] fileInputStream = awsS3Util.getDownloadedFile(documentRepository.getFilePath().get(i));
					fileAttachmentData.add(fileInputStream);
				}
			}
			try {
				mailManager.sendMailWithAttachment(documentRepository.getBody(), documentRepository.getToAddress(),
						documentRepository.getFilePath(), fileAttachmentData, documentRepository.getSubject());
				response.put("message", "Mail has been sent.");
				response.put("success", true);
			} catch (Exception exception) {
				exception.printStackTrace();
				logger.log(Level.ERROR, " sendAttachment :: " + exception.getMessage());
			}
		}
		return response;
	}

	public ResponseEntity<ByteArrayResource> multipleDownload() {
		try {
			ListObjectsRequest listObjectsRequest = new ListObjectsRequest()
					.withBucketName(configurations.getS3BbucketName()).withPrefix("/");
			List<String> keys = new ArrayList<>();

			ObjectListing objects = amazonS3.listObjects(listObjectsRequest);

			for (;;) {
				List<S3ObjectSummary> summaries = objects.getObjectSummaries();
				if (summaries.size() < 1) {
					break;
				}
				summaries.forEach(s -> keys.add(s.getKey()));
				// keys.forEach(key->filesByteArray.add(awsS3Util.downloadFile(key)));
				objects = amazonS3.listNextBatchOfObjects(objects);
			}
			if (keys.size() > 1) {
				keys.remove(0);
			}
			String string = String.join(",", keys);
			ByteArrayResource resource = null;
			String[] file = string.split(",");
			try (ZipOutputStream zippedOut = new ZipOutputStream(response.getOutputStream())) {
				for (String fileName : file) {
					final ResponseEntity<byte[]> data = utilizationService.getFileFromAmazonS3(fileName);
					resource = new ByteArrayResource(data.getBody());
					ZipEntry e = new ZipEntry(fileName.replace("/", "-"));
					// Configure the zip entry, the properties of the file
					e.setSize(resource.contentLength());
					e.setTime(System.currentTimeMillis());
					// etc.
					zippedOut.putNextEntry(e);
					// And the content of the resource:
					StreamUtils.copy(resource.getInputStream(), zippedOut);
					zippedOut.closeEntry();
				}
				zippedOut.finish();
			} catch (Exception e) {
				e.printStackTrace();
				// Exception handling goes here
			}
			// response.setContentType("application/zip");
			return ResponseEntity.ok().header("Content-type", "application/zip")
					.header("Content-disposition", "attachment; filename=\"" + string + "\"").body(resource);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return null;
	}

	public List<DocumentRepository> searchByStats(SearchCriteria searchCriteria) {
		final List<DocumentRepository> documentRepositories = new ArrayList<DocumentRepository>();
		List<Object> params = new ArrayList<>();
		try {
			final StringBuilder searchQuery = new StringBuilder();
			final StringBuilder searchFieldQuery = new StringBuilder();
			final StringBuilder sortingAndPaginationQuery = new StringBuilder();
			final String status = searchCriteria.getStatus();
			final String direction = searchCriteria.getDirection();
			final String toDate = searchCriteria.getToDate();
			final String fromDate = searchCriteria.getFromDate();
			final String ackStatus = searchCriteria.getAckStatus();
			final String partnerNames = searchCriteria.getPartnerNames();
			final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			// Begin SQL query construction
			searchQuery.append("SELECT partnerName, ack_status, direction, warehouse, parent_warehouse, status, "
					+ "transaction_type, COUNT(*) AS count_value FROM files WHERE 1=1 ");
			// Handle fromDate and toDate with proper formatting
			if (nonNull(fromDate) && !"".equals(fromDate)) {
				searchFieldQuery.append(" AND date_time_received >= CONVERT_TZ(?, ?, ?)");
				params.add(convertToSqlDate(fromDate));
				params.add(userTimeZone);
				params.add(defaultTimeZone);
			}
			if (nonNull(toDate) && !"".equals(toDate)) {
				searchFieldQuery.append(" AND date_time_received <= CONVERT_TZ(?, ?, ?)");
				params.add(convertToSqlDate(toDate));
				params.add(userTimeZone);
				params.add(defaultTimeZone);
			}
			// Filter by partnerName (only if it's not "-1")
			if (nonNull(partnerNames) && !"-1".equals(partnerNames)) {
				searchFieldQuery.append(equalOperator("partnerName"));
				params.add(partnerNames);
			}
			// Filter by direction
			if (nonNull(direction) && !"-1".equals(direction)) {
				searchFieldQuery.append(equalOperator("direction"));
				params.add(direction);
			}
			// Filter by status
			if (nonNull(status) && !"-1".equals(status)) {
				searchFieldQuery.append(equalOperator("status"));
				params.add(status);
			}
			// Filter by ack_status
			if (nonNull(ackStatus) && !"-1".equals(ackStatus)) {
				searchFieldQuery.append(equalOperator("ack_status"));
				params.add(ackStatus);
			}
			System.out.println(searchCriteria.getDocType());
			if (isValidList(searchCriteria.getDocType())) {
				searchFieldQuery.append(equalOperatorWithOrAnd("transaction_type", searchCriteria.getDocType().size()));
				params.addAll(searchCriteria.getDocType());
			}

			if (isValidList(searchCriteria.getWarehouse())) {
				searchFieldQuery
						.append(equalOperatorWithOrAnd("parent_warehouse", searchCriteria.getWarehouse().size()));
				params.addAll(searchCriteria.getWarehouse());
			}

			if (isValidList(searchCriteria.getParentWarehouse())) {
				searchFieldQuery
						.append(equalOperatorWithOrAnd("warehouse", searchCriteria.getParentWarehouse().size()));
				params.addAll(searchCriteria.getParentWarehouse());
			}

			// Append GROUP BY and ORDER BY clauses
			sortingAndPaginationQuery
					.append(" GROUP BY direction, warehouse, parent_warehouse, status, transaction_type, "
							+ "partnerName, ack_status ORDER BY count_value DESC");
			// Combine all parts into the final query
			searchQuery.append(searchFieldQuery).append(sortingAndPaginationQuery);

			// Execute the query and map the results to the DocumentRepository
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(searchQuery.toString(), params.toArray());
			for (final Map<String, Object> row : rows) {
				final DocumentRepository documentRepository = new DocumentRepository();
				documentRepository.setTransactionType(
						nonNull(row.get("transaction_type")) ? (String) row.get("transaction_type") : "-");
				documentRepository.setDirection(nonNull(row.get("direction")) ? (String) row.get("direction") : "-");
				documentRepository.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
				documentRepository
						.setCount(nonNull(row.get("count_value")) ? ((Number) row.get("count_value")).intValue() : 0);
				documentRepository.setAckStatus(nonNull(row.get("ack_status")) ? (String) row.get("ack_status") : "-");
				documentRepository
						.setPartnerName(nonNull(row.get("partnerName")) ? (String) row.get("partnerName") : "-");
				documentRepository.setWarehouse(
						nonNull(row.get("parent_warehouse")) ? (String) row.get("parent_warehouse") : "-");
				documentRepository
						.setParentWarehouse(nonNull(row.get("warehouse")) ? (String) row.get("warehouse") : "-");
				documentRepositories.add(documentRepository);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		return documentRepositories;
	}

	public Map<String, Object> updateReprocessStatus(int id, String reprocessStatus) {
		Map<String, Object> responseMap = new HashMap<>();

		String fetchSql = "SELECT * FROM files WHERE id = ?";
		List<Map<String, Object>> results = jdbcTemplate.queryForList(fetchSql, id);

		if (results.isEmpty()) {
			responseMap.put("message", "No record found with id: " + id);
			responseMap.put("status", "FAILURE");
			responseMap.put("data", null);
			return responseMap;
		}

		String updateSql = "UPDATE files SET reprocessstatus = ? WHERE id = ?";
		int response = jdbcTemplate.update(updateSql, reprocessStatus, id);
		if (response > 0) {
			List<Map<String, Object>> updatedResults = jdbcTemplate.queryForList(fetchSql, id);
			responseMap.put("data", updatedResults.get(0));
			responseMap.put("message", "Updated Successfully");
			responseMap.put("status", true);
		} else {
			responseMap.put("message", "Failed to update");
			responseMap.put("data", null);
			responseMap.put("status", false);
		}
		return responseMap;
	}

	public List<String> getSapIdlist() {
		String sql = "SELECT DISTINCT CAST(sap_id AS UNSIGNED) AS sap_id FROM files WHERE sap_id IS NOT NULL";
		logger.info("Executing method getSapIdlist {}");

		try {
			List<String> sapIds = jdbcTemplate.queryForList(sql, String.class);
			logger.info("Successfully fetched {} SAP IDs", sapIds.size());
			return sapIds;
		} catch (Exception e) {
			logger.error("Error while fetching SAP ID list from DB", e);
			throw e;
		}
	}
}
