package com.miraclesoft.rehlko.service;

import java.io.ByteArrayOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DefaultDataBufferFactory;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import com.miraclesoft.rehlko.dto.Inbox820WithDetailsResponse;
import com.miraclesoft.rehlko.dto.StatusCountDTO;
import com.miraclesoft.rehlko.entity.Details820;
import com.miraclesoft.rehlko.entity.Inbox820;
import com.miraclesoft.rehlko.repository.ConfigurationsRepository;
import com.miraclesoft.rehlko.repository.Details820Repository;
import com.miraclesoft.rehlko.repository.Inbox820Repository;
import com.miraclesoft.rehlko.util.ExcelStyleUtil;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

/**
 * Service class for handling business logic related to Inbox820 operations.
 * This includes CRUD operations, filtering, file processing, and
 * correlation-key based queries.
 */
@Service
public class Inbox820Service {

	private static final Logger logger = LoggerFactory.getLogger(Inbox820Service.class.getName());

	@Autowired
	ConfigurationsRepository configurationsRepository;

	@Autowired
	Inbox820Repository inbox820Repository;

	@Autowired
	Details820Repository details820Repository;

	/**
	 * Retrieves all Inbox820 records.
	 * 
	 * @return Flux stream of all Inbox820 records
	 */
	public Flux<Inbox820> getAll() {
		return inbox820Repository.findAll();
	}

	/**
	 * Retrieves a single Inbox820 record by its ID.
	 * 
	 * @param id the Inbox820 ID
	 * @return Mono with the found Inbox820 or empty if not found
	 */
	public Mono<Inbox820> getById(Integer id) {
		return inbox820Repository.findById(id);
	}

	/**
	 * Retrieves Inbox820 records filtered by correlationKey1Val.
	 * 
	 * @param keyVal the correlationKey1Val to filter by
	 * @return Flux of matching Inbox820 records
	 */
	public Flux<Inbox820> getByCorrelationKey1Val(String keyVal) {
		return inbox820Repository.findByCorrelationKey1Val(keyVal);
	}

	/**
	 * Updates an existing Inbox820 record by ID with new data.
	 * 
	 * @param inboxId       the ID of the Inbox820 record to update
	 * @param updateRequest the new data to update
	 * @return Mono of updated Inbox820 record
	 * @throws ResponseStatusException if record not found
	 */

	public Mono<Inbox820> updateInbox820(Integer inboxId, Inbox820 updateRequest) {
		logger.info("Received request to update Inbox820 with ID: {}", inboxId);

		return inbox820Repository.findById(inboxId).switchIfEmpty(Mono.error(
				new ResponseStatusException(HttpStatus.NOT_FOUND, "Inbox820 record not found for ID: " + inboxId)))
				.flatMap(existingRecord -> {
					logger.debug("Existing Inbox820 record found: {}", existingRecord);

					updateIfPresent(updateRequest.getTransactionName(), existingRecord::setTransactionName);
					updateIfPresent(updateRequest.getTransactionType(), existingRecord::setTransactionType);
					updateIfPresent(updateRequest.getSapId(), existingRecord::setSapId);
					updateIfPresent(updateRequest.getIsaSenderId(), existingRecord::setIsaSenderId);
					updateIfPresent(updateRequest.getIsaReceiverId(), existingRecord::setIsaReceiverId);
					updateIfPresent(updateRequest.getIsaDateTime(), existingRecord::setIsaDateTime);
					updateIfPresent(updateRequest.getIsaControlNumber(), existingRecord::setIsaControlNumber);
					updateIfPresent(updateRequest.getPaymentMethod(), existingRecord::setPaymentMethod);
					updateIfPresent(updateRequest.getSenderAccountNumber(), existingRecord::setSenderAccountNumber);
					updateIfPresent(updateRequest.getSenderAccountType(), existingRecord::setSenderAccountType);
					updateIfPresent(updateRequest.getReceiverAccountNumber(), existingRecord::setReceiverAccountNumber);
					updateIfPresent(updateRequest.getReceiverAccountType(), existingRecord::setReceiverAccountType);
					updateIfPresent(updateRequest.getCorrelationKey1Val(), existingRecord::setCorrelationKey1Val);
					updateIfPresent(updateRequest.getCorrelationKey1Name(), existingRecord::setCorrelationKey1Name);
					updateIfPresent(updateRequest.getCorrelationKey2Val(), existingRecord::setCorrelationKey2Val);
					updateIfPresent(updateRequest.getCorrelationKey2Name(), existingRecord::setCorrelationKey2Name);
					updateIfPresent(updateRequest.getCorrelationKey3Val(), existingRecord::setCorrelationKey3Val);
					updateIfPresent(updateRequest.getCorrelationKey3Name(), existingRecord::setCorrelationKey3Name);
					updateIfPresent(updateRequest.getCorrelationKey4Val(), existingRecord::setCorrelationKey4Val);
					updateIfPresent(updateRequest.getCorrelationKey4Name(), existingRecord::setCorrelationKey4Name);
					updateIfPresent(updateRequest.getBusinessFunction(), existingRecord::setBusinessFunction);
					updateIfPresent(updateRequest.getPayerName(), existingRecord::setPayerName);
					updateIfPresent(updateRequest.getPayerAccountNumber(), existingRecord::setPayerAccountNumber);
					updateIfPresent(updateRequest.getPayeeName(), existingRecord::setPayeeName);
					updateIfPresent(updateRequest.getPayeeAccountNumber(), existingRecord::setPayeeAccountNumber);
					updateIfPresent(updateRequest.getMonetaryAmount(), existingRecord::setMonetaryAmount);
					updateIfPresent(updateRequest.getCurrencyCode(), existingRecord::setCurrencyCode);
					updateIfPresent(updateRequest.getDebitCredit(), existingRecord::setDebitCredit);
					updateIfPresent(updateRequest.getFileName(), existingRecord::setFileName);
					updateIfPresent(updateRequest.getFileLocation(), existingRecord::setFileLocation);
					updateIfPresent(updateRequest.getStatus(), existingRecord::setStatus);

					logger.info("Updating Inbox820 record with ID: {}", inboxId);
					return inbox820Repository.save(existingRecord);
				})
				.doOnSuccess(saved -> logger
						.info("Successfully updated Inbox820 with ID: {} -> " + updateRequest.getStatus(), inboxId))
				.doOnError(error -> logger.error("Failed to update Inbox820 with ID: {}. Error: {}", inboxId,
						error.getMessage()));
	}

	/**
	 * Helper method to update a field only if the new value is non-null.
	 * 
	 * @param <T>      the type of the field
	 * @param newValue the new value to set if not null
	 * @param setter   the setter function to apply the new value
	 */
	private <T> void updateIfPresent(T newValue, Consumer<T> setter) {
		if (newValue != null) {
			setter.accept(newValue);
		}
	}

	/**
	 * Fetches file data for the given Inbox820 record ID from AWS S3.
	 * 
	 * @param id the Inbox820 ID
	 * @return Mono containing a map with file data or error details
	 */
	public Mono<Map<String, Object>> getInbox820FileData(int id) {
		logger.info("Executing the method :: getInbox820FileData ");

		Map<String, Object> defaultErrorResponse = new HashMap<>();
		defaultErrorResponse.put("message", "Error while fetching file");
		defaultErrorResponse.put("status", false);
		defaultErrorResponse.put("data", new HashMap<>());

		return configurationsRepository.findAll().next().flatMap(config -> {
			return inbox820Repository.findById(id).flatMap(inbox820 -> {
				String filePath = inbox820.getFileLocation();
				String fileName = inbox820.getFileName();

				logger.info("filepath before:::::" + filePath);

				if (filePath != null && filePath.contains(config.getS3_bucket_name())) {
					filePath = filePath.replace("/" + config.getS3_bucket_name() + "/", "");
				}
				logger.info("filepath after:::::{}", filePath);

				S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
						.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
								.create(config.getS3_bucket_access_key(), config.getS3_bucket_sceret_key())))
						.region(Region.of(config.getS3_bucket_region())).build();

				String s3ObjectKey = filePath + "/" + fileName;

				HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(config.getS3_bucket_name())
						.key(s3ObjectKey).build();

				return Mono.fromFuture(s3AsyncClient.headObject(headObjectRequest)).flatMap(headObjectResponse -> {
					GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(config.getS3_bucket_name())
							.key(s3ObjectKey).build();

					return Mono
							.fromFuture(s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()))
							.map(responseBytes -> {
								Map<String, Object> successResponse = new HashMap<>();
								successResponse.put("message", "Fetching File Successfully");
								successResponse.put("status", true);
								successResponse.put("data", responseBytes.asByteArray());
								return successResponse;
							}).doFinally(signalType -> s3AsyncClient.close());
				}).onErrorResume(NoSuchKeyException.class, e -> {
					logger.warn("S3 object not found (NoSuchKeyException): bucket={}, key={}",
							config.getS3_bucket_name(), s3ObjectKey);
					return Mono.just(
							Map.of("message", "File not found in S3", "status", false, "data", HttpStatus.NOT_FOUND));
				}).onErrorResume(Exception.class, s3Exception -> {
					logger.error("Error during S3 operation for file {}: {}", fileName, s3Exception.getMessage());
					return Mono.just(Map.of("message", "Error during S3 operation", "status", false, "data",
							HttpStatus.INTERNAL_SERVER_ERROR));
				}).doFinally(signalType -> s3AsyncClient.close());
			}).switchIfEmpty(Mono.defer(() -> {
				logger.warn("File Id {} doesn't exist. Returning specific error response.", id);
				return Mono.just(Map.of("message", "File Id doesn't exist", "status", false, "data", new HashMap<>()));
			}));
		}).doOnSuccess(res -> logger.info("Executed the method :: getInbox820FileData ")).onErrorResume(Exception.class,
				overallException -> {
					logger.error("Overall error in getInbox820FileData :: {}", overallException.getMessage());
					return Mono.just(defaultErrorResponse);
				});
	}

	/**
	 * Counts and groups Inbox820 records by status for a specific partner ID.
	 * 
	 * @param partnerId the partner ID to filter records
	 * @return Mono containing a Map with counts of "Read" and "UnRead" statuses
	 */
	public Mono<Map<String, Long>> getStatusCountsByPartnerId(String partnerId) {
		Map<String, Long> defaultStatusCounts = new HashMap<>();
		defaultStatusCounts.put("Unread", 0L);
		defaultStatusCounts.put("Read", 0L);

		return inbox820Repository.countStatusGroupedByPartnerId(partnerId.trim())
				.collectMap(StatusCountDTO::getStatus, StatusCountDTO::getCount).map(counts -> {
					defaultStatusCounts.putAll(counts);
					return defaultStatusCounts;
				}).defaultIfEmpty(defaultStatusCounts);
	}

	/**
	 * Filters Inbox820 records based on multiple optional criteria.
	 * 
	 * @param transactionName    transaction name filter (optional)
	 * @param transactionType    transaction type filter (optional)
	 * @param sapId              SAP ID filter (optional)
	 * @param isaSenderId        ISA sender ID filter (optional)
	 * @param isaReceiverId      ISA receiver ID filter (optional)
	 * @param partnerId          partner ID filter (optional)
	 * @param correlationKey1Val correlation key 1 value filter (optional)
	 * @param correlationKey2Val correlation key 2 value filter (optional)
	 * @param correlationKey3Val correlation key 3 value filter (optional)
	 * @param correlationKey4Val correlation key 4 value filter (optional)
	 * @param status             status filter (optional)
	 * @param startDate          start date filter (yyyy-MM-dd) (optional)
	 * @param endDate            end date filter (yyyy-MM-dd) (optional)
	 * @return Flux of Inbox820 records matching the filters
	 */
	public Flux<Inbox820> getFilteredInbox820(String transactionName, String transactionType, String sapId,
			String isaSenderId, String isaReceiverId, String partnerId, String correlationKey1Val,
			String correlationKey2Val, String correlationKey3Val, String correlationKey4Val, String status,
			String startDate, String endDate) {
		logger.info("Filtering Inbox820 records in service");

		LocalDate correlationKey4Value = parseDate(correlationKey4Val);
		LocalDate start = parseDate(startDate);
		LocalDate end = parseDate(endDate);

		return inbox820Repository.findFilteredInbox820(normalize(transactionName), normalize(transactionType),
				normalize(sapId), normalize(isaSenderId), normalize(isaReceiverId), normalize(partnerId),
				normalize(correlationKey1Val), normalize(correlationKey2Val), normalize(correlationKey3Val),
				correlationKey4Value, normalize(status), start, end);
	}

	private LocalDate parseDate(String localDate) {
		if (localDate == null || localDate.isBlank())
			return null;
		return LocalDate.parse(localDate.trim());
	}

	/**
	 * Normalize input strings by trimming and converting empty or null to null.
	 * 
	 * @param input the input string
	 * @return normalized string or null
	 */
	private String normalize(String input) {
		return (input == null || input.trim().isEmpty()) ? null : input.trim();
	}

	/**
	 * Fetches Inbox820 and Details820 records by correlationKey1Val, combining
	 * results into a response DTO.
	 * 
	 * @param correlationKey1Val the correlation key to filter by
	 * @return Mono containing combined Inbox820WithDetailsResponse
	 */
	public Mono<Inbox820WithDetailsResponse> getInboxAndDetailsByCorrelationKey(String correlationKey1Val) {
		if (correlationKey1Val == null || correlationKey1Val.trim().isEmpty()) {
			logger.warn("Invalid input: correlationKey1Val is null or empty");
			return Mono.error(new IllegalArgumentException("correlationKey1Val must not be null or empty"));
		}

		logger.info("Fetching Inbox820 and Details820 records for correlationKey1Val: {}", correlationKey1Val);

		Mono<List<Inbox820>> inboxMono = inbox820Repository.findByCorrelationKey1Val(correlationKey1Val.trim())
				.collectList().defaultIfEmpty(List.of());

		Mono<List<Details820>> detailsMono = details820Repository.findByCorrelationKey1Val(correlationKey1Val.trim())
				.collectList().defaultIfEmpty(List.of());

		return Mono.zip(inboxMono, detailsMono).map(tuple -> {
			List<Inbox820> inboxList = tuple.getT1();
			List<Details820> detailsList = tuple.getT2();

			logger.debug("Fetched {} Inbox820 records and {} Details820 records for correlationKey1Val: {}",
					inboxList.size(), detailsList.size(), correlationKey1Val);

			Inbox820WithDetailsResponse response = new Inbox820WithDetailsResponse();
			response.setInbox820List(inboxList);
			response.setDetails820List(detailsList);
			return response;
		}).doOnSuccess(
				response -> logger.info("Successfully fetched records for correlationKey1Val: {}", correlationKey1Val))
				.onErrorResume(ex -> {
					logger.error("Exception while fetching records for correlationKey1Val: {}", correlationKey1Val, ex);
					Inbox820WithDetailsResponse fallbackResponse = new Inbox820WithDetailsResponse();
					fallbackResponse.setInbox820List(List.of());
					fallbackResponse.setDetails820List(List.of());
					return Mono.just(fallbackResponse);
				});
	}

	/**
	 * Searches the Inbox820 table for records that match the provided invoice
	 * number and partner ID. The method trims both inputs and performs a
	 * case-insensitive partial match on the invoice number. It also filters results
	 * by exact match on the trimmed partner ID. Matching logic is handled at the
	 * repository level using a custom SQL query.
	 * 
	 * @param invoiceNumber The invoice number (partial or full) to search for;
	 *                      case-insensitive.
	 * @param partnerId     The partner ID (ISA Receiver ID) to filter results by;
	 *                      trimmed before use.
	 * @return A Flux stream of {@link Inbox820} records matching the criteria.
	 */
	public Flux<Inbox820> searchInboxByInvoiceNumber(String invoiceNumber, String partnerId) {
		String trimmedInvoiceNumber = invoiceNumber != null ? invoiceNumber.trim() : "";
		String trimmedPartnerId = partnerId != null ? partnerId.trim() : "";

		logger.info("Searching Inbox820 records with invoiceNumber: [{}], partnerId: [{}]", trimmedInvoiceNumber,
				trimmedPartnerId);

		return inbox820Repository.searchByInvoiceNumberAndPartnerId(trimmedInvoiceNumber, trimmedPartnerId)
				.doOnNext(record -> logger.debug("Fetched Inbox820 record: {}", record))
				.doOnComplete(
						() -> logger.info("Completed fetching records for invoiceNumber: [{}]", trimmedInvoiceNumber))
				.doOnError(error -> logger.error("Error while fetching records for invoiceNumber: [{}] -> {}",
						trimmedInvoiceNumber, error.getMessage()));
	}

	public Mono<DataBuffer> generateExcelWithHeaderAndLineItems(Map<String, Object> input) {
		logger.info("Starting Excel generation process...");

		try (Workbook workbook = new XSSFWorkbook()) {
			Sheet sheet = workbook.createSheet("Remittance_details");

			// Create styles for headers and data cells
//			CellStyle headerStyle = ExcelStyleUtil.createHeaderStyle(workbook, IndexedColors.DARK_BLUE.getIndex());
			CellStyle headerStyle = ExcelStyleUtil.createHeaderStyleWithHexColor(workbook, "#336EE5");
			CellStyle dataStyle = ExcelStyleUtil.createBorderedStyle(workbook);

			int rowNum = 0;

			// ----------- Header Information Section -----------
			LinkedHashMap<String, Object> headerInfo = (LinkedHashMap<String, Object>) input.get("headerInformation");
			if (headerInfo != null) {

				Row titleRow = sheet.createRow(rowNum++);
				Cell titleCell = titleRow.createCell(0);
				titleCell.setCellValue("Header Information");
				titleCell.setCellStyle(headerStyle);

				for (Map.Entry<String, Object> entry : headerInfo.entrySet()) {
					Row row = sheet.createRow(rowNum++);
					Cell keyCell = row.createCell(0);
					keyCell.setCellValue(beautifyKey(entry.getKey()));
					keyCell.setCellStyle(dataStyle);

					Cell valCell = row.createCell(1);
					Object value = entry.getValue();

					String cellValue;
					if (value instanceof Map) {
						Map<?, ?> nestedMap = (Map<?, ?>) value;
						Object nameVal = nestedMap.get("Name");
						cellValue = nameVal != null ? nameVal.toString() : nestedMap.toString();
					} else {
						cellValue = value != null ? value.toString() : "";
					}

					valCell.setCellValue(cellValue);
					valCell.setCellStyle(dataStyle);
				}
			}

			rowNum++; // Spacer row

			// Line items section
			List<LinkedHashMap<String, Object>> lineItems = (List<LinkedHashMap<String, Object>>) input
					.get("lineItems");
			if (lineItems != null && !lineItems.isEmpty()) {

				// Custom header mapping
				Map<String, String> customHeaderMap = Map.of("invoiceNumber", "Supplier Invoice #", "invoiceDate",
						"Supplier Invoice Date"
				// Add more mappings if needed
				);

				List<String> headers = new ArrayList<>(lineItems.get(0).keySet());
				if (headers.remove("invoiceDate")) {
					headers.add(0, "invoiceDate");
				}
				Row headerRow = sheet.createRow(rowNum++);
				for (int i = 0; i < headers.size(); i++) {
					String rawKey = headers.get(i);
					String displayKey = customHeaderMap.getOrDefault(rawKey, beautifyKey(rawKey));
					Cell cell = headerRow.createCell(i);
					cell.setCellValue(displayKey);
					cell.setCellStyle(headerStyle);
				}

				for (Map<String, Object> item : lineItems) {
					Row row = sheet.createRow(rowNum++);
					for (int i = 0; i < headers.size(); i++) {
						String header = headers.get(i);
						Object val = item.get(header);

						String cellValue;
						if (val instanceof Map) {
							Map<?, ?> nestedMap = (Map<?, ?>) val;
							Object nameVal = nestedMap.get("Name");
							cellValue = nameVal != null ? nameVal.toString() : nestedMap.toString();
						} else {
							cellValue = val != null ? val.toString() : "";
						}

						Cell cell = row.createCell(i);
						cell.setCellValue(cellValue);
						cell.setCellStyle(dataStyle);
					}
				}

				for (int i = 0; i < headers.size(); i++) {
					sheet.autoSizeColumn(i);
				}

				sheet.autoSizeColumn(0);
				sheet.autoSizeColumn(1);
			}

			// Convert workbook to DataBuffer
			ByteArrayOutputStream out = new ByteArrayOutputStream();
			workbook.write(out);
			logger.info("Excel file generated successfully.");

			return Mono.just(new DefaultDataBufferFactory().wrap(out.toByteArray()));

		} catch (Exception e) {
			logger.error("Failed to generate Excel file", e);
			return Mono.error(new RuntimeException("Failed to generate Excel", e));
		}
	}

	/**
	 * Beautifies keys for better readability in Excel headers. - Replaces
	 * underscores with spaces - Inserts spaces before capital letters (camelCase to
	 * words) - Capitalizes each word
	 *
	 * @param key The original key string.
	 * @return A beautified string suitable for display.
	 */
	private String beautifyKey(String key) {
		if (key == null || key.isEmpty())
			return "";

		key = key.replace("_", " ");
		key = key.replaceAll("([a-z])([A-Z])", "$1 $2");

		return Arrays.stream(key.split(" ")).map(
				word -> word.isEmpty() ? word : Character.toUpperCase(word.charAt(0)) + word.substring(1).toLowerCase())
				.collect(Collectors.joining(" "));
	}
}
