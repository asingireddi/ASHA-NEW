package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.SqlCondition.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCellStyle;
import org.apache.poi.hssf.usermodel.HSSFFont;
import org.apache.poi.hssf.util.HSSFColor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFCellStyle;
import org.apache.poi.xssf.usermodel.XSSFFont;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import static com.miraclesoft.scvp.util.DateUtility.convertToSqlDate;
import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;
import static com.miraclesoft.scvp.util.SqlCondition.equalOperator;
import static com.miraclesoft.scvp.util.SqlCondition.likeOperator;
import static com.miraclesoft.scvp.util.SqlCondition.likeOperatorStartWith;
import static java.util.Objects.nonNull;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;

import com.miraclesoft.scvp.mail.MailManager;
import com.miraclesoft.scvp.model.Configurations;
import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.DocumentRepository;
import com.miraclesoft.scvp.model.Sfg;
import com.miraclesoft.scvp.model.SfgDocumentRepository;
import com.miraclesoft.scvp.model.Sfgpartner;
import com.miraclesoft.scvp.reports.Report;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.AwsS3Util;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

/**
 * The Class SfgDocumentRepositoryServiceImpl.
 *
 * @author shanmukhavarma kalidindi
 */
@SuppressWarnings("PMD.TooManyStaticImports")
@Component
public class SfgDocumentRepositoryServiceImpl {

	/** The aws S3 util. */
	@Autowired
	private AwsS3Util awsS3Util;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	private static final int SUCCESS = 200;

//    @Value("${sfgBpUrl}")
//    private String sfgBpUrl;

	/** The documents creation path. */
	@Value("${documentPath}")
	private String documentPath;

	/** The MailManager data provider. */
	@Autowired
	private MailManager mailManager;

	/** The data source data provider. */
	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;

	@Autowired
	private Report report;

	@Autowired
	private Configurations configurations;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(SfgDocumentRepositoryServiceImpl.class.getName());

	/** The Constant SMALL_COLUMN_SIZE. */
	private static final int SMALL_COLUMN_SIZE = 3000;

	/** The Constant MEDIUM_COLUMN_SIZE. */
	private static final int MEDIUM_COLUMN_SIZE = 5000;

	/** The Constant ONE. */
	private static final int ONE = 1;

	/** The Constant TWO. */
	private static final int TWO = 2;

	/** The Constant THREE. */
	private static final int THREE = 3;

	/** The Constant FOUR. */
	private static final int FOUR = 4;

	/** The Constant FIVE. */
	private static final int FIVE = 5;

	/** The Constant SIX. */
	private static final int SIX = 6;

	/** The Constant SEVEN. */
	private static final int SEVEN = 7;

	/** The Constant EIGHT. */
	private static final int EIGHT = 8;

	public CustomResponse search(final Sfg sfgSearch) {
		List<SfgDocumentRepository> sfgResult = new ArrayList<SfgDocumentRepository>();
		int count = 0;
		try {
			Map<String, Object> queryMap = searchCriteria(sfgSearch);
			String mainQuery = (String) queryMap.get("mainQuery");
			String countQuery = (String) queryMap.get("countQuery");
			List<Object> params = (List<Object>) queryMap.get("params");
			List<Object> countParams = (List<Object>) queryMap.get("countParams");
			System.out.println(mainQuery);
			System.out.println(params);
			final List<Map<String, Object>> searchQueryResult = jdbcTemplate.queryForList(mainQuery, params.toArray());
			if (sfgSearch.getCountFlag()) {
				count = jdbcTemplate.queryForObject(countQuery, countParams.toArray(), Integer.class);
			}
			for (Map<String, Object> row : searchQueryResult) {
				SfgDocumentRepository sfgDocumentRepository = new SfgDocumentRepository();
				sfgDocumentRepository.setId(nonNull(row.get("id")) ? Long.parseLong(row.get("id").toString()) : 0);
				sfgDocumentRepository
						.setDirection(nonNull(row.get("direction")) ? (String) row.get("direction").toString() : "-");
				sfgDocumentRepository
						.setFileId(nonNull(row.get("file_id")) ? Long.parseLong(row.get("file_id").toString()) : 0);
				sfgDocumentRepository.setDateTimeReceived(nonNull(row.get("date_time_received"))
						? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("date_time_received"))
						: "-");
				sfgDocumentRepository.setProducer(nonNull(row.get("Producer")) ? (String) row.get("Producer") : "-");
				sfgDocumentRepository.setConsumer(nonNull(row.get("Consumer")) ? (String) row.get("Consumer") : "-");
				sfgDocumentRepository.setFilename(nonNull(row.get("filename")) ? (String) row.get("filename") : "-");
				sfgDocumentRepository
						.setFileStatus(nonNull(row.get("File_Status")) ? (String) row.get("File_Status") : "-");
				sfgDocumentRepository.setReprocessStatus(
						nonNull(row.get("reprocessstatus")) ? (String) row.get("reprocessstatus") : "-");
				sfgDocumentRepository.setSfgPath(nonNull(row.get("sfg_path")) ? row.get("sfg_path").toString() : "-");
				sfgDocumentRepository
						.setDirection(nonNull(row.get("direction")) ? row.get("direction").toString() : "-");
				sfgResult.add(sfgDocumentRepository);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " search :: " + exception.getMessage());
		}
		return new CustomResponse(sfgResult, count);
	}

	private Map<String, Object> searchCriteria(final Sfg sfgSearch) {
		Map<String, Object> result = new HashMap<>();
		final StringBuilder sfgSearchCriteria = new StringBuilder();
		final StringBuilder searchFieldsQuery = new StringBuilder();
		final StringBuilder sortingAndPaginationQuery = new StringBuilder();
		final String fromDate = sfgSearch.getFromDate();
		final String toDate = sfgSearch.getToDate();
		final String fileName = sfgSearch.getFilename();
		final String fileStatus = sfgSearch.getFileStatus();
		final String instanceId = sfgSearch.getInstanceId();
		final String direction = sfgSearch.getDirection();
		final String userId = tokenAuthenticationService.getUserIdfromToken();
		final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
		final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
		boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAllSfg").toString());
		List<Object> params = new ArrayList<>();
		StringBuilder sfgJoin = new StringBuilder();
		if (!all) {
			sfgJoin.append(
					" join sfg_partner_visibilty spv on (spv.sfg_partner_name=f.Producer OR spv.sfg_partner_name=f.Consumer)");
		}
		sfgSearchCriteria.append(
				"SELECT f.id, f.direction, f.file_id, CONVERT_TZ(f.date_time_received, ?, ?) as date_time_received, f.Producer, f.Consumer, f.filename,"
						+ " f.File_Status, f.reprocessstatus, f.sfg_path FROM ");
		params.add(defaultTimeZone);
		params.add(userTimeZone);
		searchFieldsQuery.append(
				nonNull(sfgSearch.getLiveOrArchive()) && sfgSearch.getLiveOrArchive().equals("LIVE") ? "SFG_Files f"
						: "sfg_archive f")
				.append(sfgJoin + " where 1=1 ");
		if (nonNull(fromDate) && !"".equals(fromDate) && !"-1".equals(fromDate)) {
			searchFieldsQuery.append(" AND f.date_time_received >= CONVERT_TZ(?, ?, ?)");
			params.add(convertToSqlDate(fromDate));
			params.add(userTimeZone);
			params.add(defaultTimeZone);
		}
		if (nonNull(toDate) && !"".equals(toDate) && !"-1".equals(toDate)) {
			searchFieldsQuery.append(" AND f.date_time_received <= CONVERT_TZ(?, ?, ?)");
			params.add(convertToSqlDate(toDate));
			params.add(userTimeZone);
			params.add(defaultTimeZone);
		}
		if (isValidList(sfgSearch.getProducer())) {
		    searchFieldsQuery.append(equalOperatorWithOrAnd("f.producer", sfgSearch.getProducer().size()));
		    params.addAll(sfgSearch.getProducer());
		}
		if (isValidList(sfgSearch.getConsumer())) {
		    searchFieldsQuery.append(equalOperatorWithOrAnd("f.Consumer", sfgSearch.getConsumer().size()));
		    params.addAll(sfgSearch.getConsumer());
		}
		if (nonNull(fileName) && !"".equals(fileName) && !"-1".equals(fileName)) {
			searchFieldsQuery.append(likeOperatorStartWith("f.filename"));
			params.add(fileName + "%");
		}
		if (nonNull(fileStatus) && !"".equals(fileStatus) && !"-1".equals(fileStatus)) {
			searchFieldsQuery.append(equalOperator("f.File_Status"));
			params.add(fileStatus);
		}
		if (nonNull(instanceId) && !"".equals(instanceId) && !"-1".equals(instanceId)) {
			searchFieldsQuery.append(equalOperator("f.file_id"));
			params.add(instanceId);
		}
		if (nonNull(direction) && !"".equals(direction) && !"-1".equals(direction)) {
			searchFieldsQuery.append(equalOperator("f.direction"));
			params.add(direction);
		}
		if (nonNull(direction) && !"".equals(direction) && direction.equals("INBOUND")) {
			searchFieldsQuery.append(equalOperator("f.direction"));
			params.add(direction);
		}
		if (nonNull(direction) && !"".equals(direction) && direction.equals("OUTBOUND")) {
			searchFieldsQuery.append(equalOperator("f.direction"));
			params.add(direction);
		}
		if (!all) {
			searchFieldsQuery.append(" AND spv.user_id = ?");
			params.add(userId);
		}
//        searchFieldsQuery.append(" group by f.id");
		if (nonNull(sfgSearch.getSortField()) && nonNull(sfgSearch.getSortOrder())) {
			sortingAndPaginationQuery.append(dataSourceDataProvider.criteriaForSortingAndPagination(
					sfgSearch.getSortField(), sfgSearch.getSortOrder(), sfgSearch.getLimit(), sfgSearch.getOffSet()));
		}
		result.put("mainQuery",
				sfgSearchCriteria.toString() + searchFieldsQuery.toString() + sortingAndPaginationQuery.toString());
		result.put("countQuery", "SELECT COUNT(*) FROM (SELECT COUNT(*) FROM " + searchFieldsQuery.toString() +")AS inner_count");
		result.put("params", params);
		result.put("countParams", params.subList(2, params.size()));
		return result;
	}

	public List<String> getProducerOrConsumer(String liveOrArchive, String isProducerOrConsumer) {
		final StringBuilder tableName = new StringBuilder();
		tableName.append(nonNull(liveOrArchive) && liveOrArchive.equals("LIVE") ? "SFG_Files" : "sfg_archive");
		final StringBuilder query = new StringBuilder();
		if (isProducerOrConsumer.equals("producer")) {
			query.append("select distinct(producer) as producer from " + tableName + " ORDER BY producer");
		} else {
			query.append("select distinct(consumer) as consumer from " + tableName + " ORDER BY consumer");
		}
		List<String> rows = jdbcTemplate.queryForList(query.toString(), String.class);
		return rows;
	}

	public List<String> producer(String liveOrArchive) {
		return getProducerOrConsumer(liveOrArchive, "producer");
	}

	public List<String> consumer(String liveOrArchive) {
		return getProducerOrConsumer(liveOrArchive, "consumer");
	}

	public Map<String, Object> sfgReprocess(Long id) {
		Sfg sfg = new Sfg();
		sfg.setId(id);
		final Map<String, Object> response = new HashMap<>();
		String xml = "";
		String result = "";
		String finalData = "";
		xml = convertToXMLForBp(sfg);
		finalData += xml;
		result = "<?xml version='1.0' encoding='UTF-8'?>" + finalData;
		try {
			final String uriForWithoutSsl = configurations.getB2bReprocessSfgUrl();
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
	public String convertToXMLForBp(final Sfg sfg) {
		final StringBuilder xml = new StringBuilder();
		SfgDocumentRepository sfgDocumentRepository = new SfgDocumentRepository();

		try {
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"SELECT file_id,Producer,Consumer,filename,mailbox_name,bucket_name,sec_key_val,direction FROM SFG_Files where id = ?",
					sfg.getId());
			if (!rows.isEmpty()) {
				for (final Map<String, Object> row : rows) {
					sfgDocumentRepository.setFileId(
							nonNull(row.get("file_id")) ? Long.parseLong(row.get("file_id").toString().trim()) : 0);
					sfgDocumentRepository
							.setFilename(nonNull(row.get("filename")) ? row.get("filename").toString() : "");
					sfgDocumentRepository
							.setProducer(nonNull(row.get("Producer")) ? row.get("Producer").toString() : "");
					sfgDocumentRepository
							.setConsumer(nonNull(row.get("Consumer")) ? row.get("Consumer").toString() : "");
					sfgDocumentRepository
							.setMailboxName(nonNull(row.get("mailbox_name")) ? row.get("mailbox_name").toString() : "");
					sfgDocumentRepository
							.setBucketName(nonNull(row.get("bucket_name")) ? row.get("bucket_name").toString() : "");
					sfgDocumentRepository
							.setSecValKey(nonNull(row.get("sec_key_val")) ? row.get("sec_key_val").toString() : "");
					sfgDocumentRepository
							.setDirection(nonNull(row.get("direction")) ? row.get("direction").toString() : "");
				}
			} else {
				sfgDocumentRepository.setFileId(0L);
				sfgDocumentRepository.setFilename("");
				sfgDocumentRepository.setProducer("");
				sfgDocumentRepository.setConsumer("");
				sfgDocumentRepository.setMailboxName("");
				sfgDocumentRepository.setBucketName("");
				sfgDocumentRepository.setSecValKey("");
				sfgDocumentRepository.getDirection();
			}
			xml.append("<root> ").append("<file_id>")
					.append(sfgDocumentRepository.getFileId() == 0 ? "" : sfgDocumentRepository.getFileId())
					.append("</file_id>").append("<filename>").append(sfgDocumentRepository.getFilename())
					.append("</filename>").append("<Producer>").append(sfgDocumentRepository.getProducer())
					.append("</Producer>").append("<Consumer>").append(sfgDocumentRepository.getConsumer())
					.append("</Consumer>").append("<mailbox_path>").append(sfgDocumentRepository.getMailboxName())
					.append("</mailbox_path>").append("<bucket_name>").append(sfgDocumentRepository.getBucketName())
					.append("</bucket_name>").append("<sec_key_val>").append(sfgDocumentRepository.getSecValKey())
					.append("</sec_key_val>").append("<Direction>").append(sfgDocumentRepository.getDirection())
					.append("</Direction>").append("</root>");
		} catch (final Exception e) {
			logger.log(Level.ERROR, " reprocessRequest :: " + e.getMessage());
		}
		return xml.toString();
	}

	public String save(Sfgpartner sfgpartner) {
		String responseString = "";
		try {
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			if (!isSfgPartnerExists(sfgpartner.getSfgPartnerName())) {
				final int tpInsertCount = jdbcTemplate.update(
						"INSERT INTO sfg_partner (name, created_by, status, modified_by, modified_ts) VALUES ( ?, ?, ?,?,convert_tz(current_timestamp, @@session.time_zone, ?))",
						new Object[] { sfgpartner.getSfgPartnerName(), sfgpartner.getCreatedBy(),
								sfgpartner.getStatus(), sfgpartner.getCreatedBy(), defaultTimeZone });
				if (tpInsertCount > 0) {
					final int tpDetailsInsertCount = jdbcTemplate.update(
							"INSERT INTO sfg_partner_details ( partner_name,  state) VALUES( ?, ?)",
							new Object[] { sfgpartner.getSfgPartnerName(), sfgpartner.getSfgCountryCode() });
					if (tpDetailsInsertCount > 0) {
						responseString = "SFG partner added succesfully.";
					}
				} else {
					responseString = "Please try again!";
				}
			} else {
				responseString = "SFG partner Id already existed!";
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " save :: " + exception.getMessage());
		}
		return responseString;
	}

	private boolean isSfgPartnerExists(String id) {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM sfg_partner WHERE name = ?", new Object[] { id },
				Integer.class) > 0 ? true : false;
	}

	public Map<String, Object> findAll(Sfgpartner sfgPartner) {
		final List<Sfgpartner> sfgPartners = new ArrayList<Sfgpartner>();
		final Map<String, Object> result = new HashMap<>();
		List<Object> params = new ArrayList<>();
		try {
			final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
			final String partnerName = sfgPartner.getSfgPartnerName();
			final String status = sfgPartner.getStatus();
			final String countryCode = sfgPartner.getSfgCountryCode();
			final StringBuilder partnerSearchQuery = new StringBuilder();
			final StringBuilder criteriaForPartnerSearchQuery = new StringBuilder();
			final StringBuilder sortingAndPaginationQuery = new StringBuilder();
			String partnerJoinQuery = "";
			String userIdRequired = "";
			final int userId = Integer.parseInt(tokenAuthenticationService.getUserIdfromToken());
			boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAllSfg").toString());
			partnerJoinQuery = !all ? dataSourceDataProvider.partnersVisibilityWithSfgJoinCondition().toString() : " ";
			userIdRequired = !all ? " and pv.user_id = ?" : " ";
			params.add(userId);
			partnerSearchQuery.append("SELECT sp.name,  spd.state,"
					+ " sp.status, sp.created_by, CONVERT_TZ(sp.created_ts, @@session.time_zone, ?) as created_ts FROM sfg_partner sp"
					+ " LEFT JOIN sfg_partner_details spd ON (spd.partner_name = sp.name) " + partnerJoinQuery
					+ " WHERE 1 = 1" + userIdRequired);
			params.add(userTimeZone);
			if (nonNull(partnerName) && !"".equals(partnerName.trim())) {
				criteriaForPartnerSearchQuery.append(likeOperator("spd.partner_name"));
				params.add(partnerName.trim() + "%");
			}
			if (nonNull(status) && !"-1".equals(status) && !"".equals(status.trim())) {
				criteriaForPartnerSearchQuery.append(equalOperator("sp.status"));
				params.add(status.trim() + "%");
			}
			if (nonNull(countryCode) && !"".equals(countryCode.trim())) {
				criteriaForPartnerSearchQuery.append(likeOperatorStartWith("spd.state"));
				params.add(countryCode.trim() + "%");
			}
			if (nonNull(sfgPartner.getSortField()) && nonNull(sfgPartner.getSortOrder())) {
				sortingAndPaginationQuery
						.append(dataSourceDataProvider.criteriaForSortingAndPagination(sfgPartner.getSortField(),
								sfgPartner.getSortOrder(), sfgPartner.getLimit(), sfgPartner.getOffSet()));
			}
			partnerSearchQuery.append(criteriaForPartnerSearchQuery).append(sortingAndPaginationQuery);
			int count = 0;
			if (sfgPartner.getCountFlag()) {
				count = jdbcTemplate.queryForObject(
						"SELECT COUNT(name) FROM sfg_partner sp LEFT JOIN sfg_partner_details spd ON (spd.partner_name = sp.name) "
								+ partnerJoinQuery + " WHERE 1 = 1" + userIdRequired + criteriaForPartnerSearchQuery,
						params.toArray(), Integer.class);
			}
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(partnerSearchQuery.toString(),
					params.toArray());
			for (final Map<String, Object> row : rows) {
				final Sfgpartner doc = new Sfgpartner();
				doc.setSfgPartnerIdentifier(nonNull(row.get("id")) ? (String) row.get("id") : "");
				doc.setSfgPartnerName(nonNull(row.get("name")) ? (String) row.get("name") : "");
				doc.setSfgCountryCode(nonNull(row.get("state")) ? (String) row.get("state") : "");
				doc.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "");
				doc.setCreatedBy(nonNull(row.get("created_by")) ? (String) row.get("created_by") : "");
				doc.setCreatedDate(nonNull(row.get("created_ts"))
						? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("created_ts"))
						: "");
				sfgPartners.add(doc);
			}
			result.put("data", sfgPartners);
			result.put("totalRecordsCount", count);
		} catch (Exception exception) {
			logger.log(Level.ERROR, " findAll :: " + exception.getMessage());
		}
		return result;
	}

	/**
	 * Gets the file.
	 *
	 * @param file the file
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ResponseEntity<byte[]> getSfgFileFromAmazonS3(final String file) throws IOException {
		return awsS3Util.getSfgFileFromAmazonS3(file);
	}

	public String update(Sfgpartner sfgPartner) {
		String responseString = "";
		int tpCount = 0;
		int tpDetailsCount = 0;
		try {
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			tpCount = jdbcTemplate.update(
					"UPDATE sfg_partner SET  modified_by = ?, modified_ts = convert_tz(current_timestamp, @@session.time_zone, ?), status = ? WHERE name = ?",
					new Object[] { sfgPartner.getCreatedBy(), defaultTimeZone, sfgPartner.getStatus(),
							sfgPartner.getSfgPartnerName() });
			// getCurrentTimestamp() which is previously used for modified_ts
			if (tpCount > 0) {
				tpDetailsCount = jdbcTemplate.update(
						"UPDATE sfg_partner_details SET   state = ? WHERE partner_name = ?",
						new Object[] { sfgPartner.getSfgCountryCode(), sfgPartner.getSfgPartnerName() });
			}
			responseString = tpCount > 0 && tpDetailsCount > 0 ? "Partner updated succesfully."
					: "Something went wrong !";
		} catch (Exception exception) {
			logger.log(Level.ERROR, " update :: " + exception.getMessage());
		}
		return responseString;
	}

	/**
	 * Download.
	 *
	 * @param searchCriteria the search criteria
	 * @return the response entity
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	@SuppressWarnings("unchecked")
	public ResponseEntity<InputStreamResource> download(final Sfg sfg) throws IOException {
		return getInputStreamResource(
				new File(sfgDocumentRepositoryExcelDownload((List<SfgDocumentRepository>) search(sfg).getData())));
	}

	public String sfgDocumentRepositoryExcelDownload(final List<SfgDocumentRepository> documents) throws IOException {
		final String filePath = getFilePath("sfgDocumentRepository.xlsx");
		try {
			String CurrDateTime = dataSourceDataProvider.getCurrentDateTimeOfUser();
			final FileOutputStream fileOut = new FileOutputStream(filePath);
			final XSSFWorkbook workbook = new XSSFWorkbook();
			final XSSFSheet worksheet = workbook.createSheet("SfgDocumentRepository");
			XSSFRow row;
			if (!documents.isEmpty()) {
				final XSSFCellStyle greenFontCellStyle = greenFontCellStyle(workbook);
				final XSSFCellStyle redFontCellStyle = redFontCellStyle(workbook);
				final XSSFCellStyle orangeFontCellStyle = orangeFontCellStyle(workbook);

				row = worksheet.createRow(0);
				row = worksheet.createRow(ONE);
				final Cell cell = row.createCell(ONE);
				cell.setCellValue("SFG DocumentRepository records on " + CurrDateTime);
				cell.setCellStyle(pageHeaderCellStyle(workbook));
				worksheet.addMergedRegion(CellRangeAddress.valueOf("B2:I2"));

				row = worksheet.createRow(THREE);
				final List<String> headers = getSfgDocumentRepositoryHeaders();
				for (int headerIndex = 0; headerIndex < headers.size(); headerIndex++) {
					final XSSFCell headerCell = row.createCell(headerIndex + 1);
					headerCell.setCellValue(headers.get(headerIndex));
					headerCell.setCellStyle(tableHeaderCellStyle(workbook));
				}

				for (int i = 0; i < documents.size(); i++) {
					final SfgDocumentRepository document = documents.get(i);
					row = worksheet.createRow(i + FOUR);
					row.createCell(ONE).setCellValue(document.getDateTimeReceived());
					row.createCell(TWO).setCellValue(document.getFileId());
					row.createCell(THREE).setCellValue(document.getDirection());
					row.createCell(FOUR).setCellValue(document.getProducer());
					row.createCell(FIVE).setCellValue(document.getConsumer());
					row.createCell(SIX).setCellValue(document.getFilename());
					final XSSFCell cellG1 = row.createCell(SEVEN);
					if (nonNull(document.getFileStatus())) {
						cellG1.setCellValue(document.getFileStatus().toUpperCase());
						if (document.getFileStatus().equalsIgnoreCase("SUCCESS")) {
							cellG1.setCellStyle(greenFontCellStyle);
						} else if (document.getFileStatus().equalsIgnoreCase("ERROR")) {
							cellG1.setCellStyle(redFontCellStyle);
						} else {
							cellG1.setCellStyle(orangeFontCellStyle);
						}
					}
					row.createCell(EIGHT).setCellValue(document.getReprocessStatus());
				}
			}
			worksheet.setColumnWidth(ONE, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(TWO, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(THREE, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(FOUR, MEDIUM_COLUMN_SIZE);
			worksheet.setColumnWidth(FIVE, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(SIX, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(SEVEN, SMALL_COLUMN_SIZE);
			worksheet.setColumnWidth(EIGHT, SMALL_COLUMN_SIZE);
			workbook.write(fileOut);
			fileOut.flush();
			fileOut.close();
		} catch (FileNotFoundException fileNotFoundException) {
			logger.log(Level.ERROR, "FileNotFoundException occurred in mDocRepositoryExcelDownload :: "
					+ fileNotFoundException.getMessage());
		} catch (IOException ioException) {
			logger.log(Level.ERROR,
					"IOException occurred in mDocRepositoryExcelDownload :: " + ioException.getMessage());
		} catch (Exception exception) {
			logger.log(Level.ERROR, "Exception occurred in mDocRepositoryExcelDownload :: " + exception.getMessage());
		}
		return filePath;
	}

	/**
	 * Green font cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle greenFontCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle greenFontCellStyle = workbook.createCellStyle();
		final XSSFFont greenFont = workbook.createFont();
		greenFont.setColor(HSSFColor.GREEN.index);
		greenFontCellStyle.setFont(greenFont);
		return greenFontCellStyle;
	}

	/**
	 * Gets the file path.
	 *
	 * @param type the type
	 * @return the file path
	 */
	private String getFilePath(final String type) {
		final File file = new File(documentPath);
		if (!file.exists()) {
			file.mkdirs();
		}
		return file.getAbsolutePath() + File.separator + type;
	}

	/**
	 * Red font cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle redFontCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle redFontCellStyle = workbook.createCellStyle();
		final XSSFFont redFont = workbook.createFont();
		redFont.setColor(HSSFColor.RED.index);
		redFontCellStyle.setFont(redFont);
		return redFontCellStyle;
	}

	/**
	 * Orange font cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle orangeFontCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle orangeFontCellStyle = workbook.createCellStyle();
		final XSSFFont orangeFont = workbook.createFont();
		orangeFont.setColor(HSSFColor.ORANGE.index);
		orangeFontCellStyle.setFont(orangeFont);
		return orangeFontCellStyle;
	}

	/**
	 * Page header cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle pageHeaderCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle pageHeaderCellStyle = workbook.createCellStyle();
		final XSSFFont pageHeaderFont = workbook.createFont();
		pageHeaderFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		pageHeaderCellStyle.setFont(pageHeaderFont);
		pageHeaderCellStyle.setAlignment(HSSFCellStyle.ALIGN_CENTER);
		pageHeaderCellStyle.setFillForegroundColor(HSSFColor.YELLOW.index);
		pageHeaderCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		return pageHeaderCellStyle;
	}

	/**
	 * Table header cell style.
	 *
	 * @param workbook the workbook
	 * @return the XSSF cell style
	 */
	private XSSFCellStyle tableHeaderCellStyle(final XSSFWorkbook workbook) {
		final XSSFCellStyle tableHeaderCellStyle = workbook.createCellStyle();
		final XSSFFont tableHeaderFont = workbook.createFont();
		tableHeaderFont.setColor(HSSFColor.WHITE.index);
		tableHeaderFont.setBoldweight(HSSFFont.BOLDWEIGHT_BOLD);
		tableHeaderCellStyle.setFillForegroundColor(HSSFColor.GREEN.index);
		tableHeaderCellStyle.setFillPattern(HSSFCellStyle.SOLID_FOREGROUND);
		tableHeaderCellStyle.setFont(tableHeaderFont);
		return tableHeaderCellStyle;
	}

	/**
	 * Gets the document repository headers.
	 *
	 * @return the document repository headers
	 */
	private List<String> getSfgDocumentRepositoryHeaders() {
		return Arrays.asList("Date Time", "Instance Id", "Direction", "Producer", "Consumer", "File name",
				"File Status", "Reprocess");
	}

	/**
	 * transactionsDetailAttachment.
	 *
	 * @param documentRepository the DocumentRepository
	 * @return the Map
	 */
	public Map<String, Object> sendMail(final DocumentRepository documentRepository) {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to sent email");
		response.put("success", false);
		List<byte[]> fileAttachmentData = new ArrayList<>();
		if (documentRepository.getFilePath().size() > 0) {
			for (int i = 0; i < documentRepository.getFilePath().size(); i++) {
				if (awsS3Util.checkSfgObject(documentRepository.getFilePath().get(i))) {
					byte[] fileInputStream = awsS3Util.getSfgDownloadedFile(documentRepository.getFilePath().get(i));
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

	public List<String> partnerVisibility() {
		List<String> result = null;
		try {

			result = jdbcTemplate.queryForList("SELECT name FROM sfg_partner", String.class);
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		return result;
	}

	@SuppressWarnings("unchecked")
	public ResponseEntity<InputStreamResource> download(Sfgpartner partner) throws IOException {
		return getInputStreamResource(
				new File(report.downloadSfgPartnersData((List<Sfgpartner>) findAll(partner).get("data"))));
	}

	public String deletePartner(String partnerName) {
		int sfgPartnerDetailsDeleteCount = 0;
		try {
			final int sfgPartnerDeleteCount = jdbcTemplate.update("DELETE FROM sfg_partner WHERE name = ?",
					partnerName);
			if (sfgPartnerDeleteCount > 0) {
				sfgPartnerDetailsDeleteCount = jdbcTemplate
						.update("DELETE FROM sfg_partner_details WHERE partner_name = ?", partnerName);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " sfgDeletePartner :: " + exception.getMessage());
		}
		return sfgPartnerDetailsDeleteCount > 0 ? "Partner deleted seccessfully" : "Failed to delete partner";
	}

}
