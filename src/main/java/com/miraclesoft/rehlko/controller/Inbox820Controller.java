package com.miraclesoft.rehlko.controller;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;
import java.util.Optional;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import com.miraclesoft.rehlko.dto.Inbox820WithDetailsResponse;
import com.miraclesoft.rehlko.entity.Inbox820;
import com.miraclesoft.rehlko.service.Inbox820Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Controller for handling all Inbox820 related HTTP requests. Provides
 * endpoints for fetching, updating, filtering, and retrieving Inbox820 data.
 */
@CrossOrigin("*")
@RequestMapping("/Inbox820")
@RestController
public class Inbox820Controller {

	private static final Logger logger = LoggerFactory.getLogger(Inbox820Controller.class.getName());

	@Autowired
	Inbox820Service inbox820Service;

	/**
	 * Fetches all Inbox820 records.
	 *
	 * @return Flux of Inbox820 records
	 */
	@GetMapping
	public Flux<Inbox820> getAllInbox820Records() {
		logger.info("Fetching all Inbox820 records");
		return inbox820Service.getAll().doOnNext(record -> logger.debug("Fetched Inbox820 with ID: {}", record.getId()))
				.doOnComplete(() -> logger.info("Completed fetching all Inbox820 records"))
				.doOnError(ex -> logger.error("Error while fetching Inbox820 records: {}", ex.getMessage(), ex));
	}

	/**
	 * Fetches a specific Inbox820 record by its ID.
	 *
	 * @param id the ID of the record
	 * @return Mono of Inbox820
	 */
	@GetMapping("/getById/{id}")
	public Mono<Inbox820> getInbox820ById(@PathVariable Integer id) {
		logger.info("Fetching Inbox820 record for ID: {}", id);

		if (id == null || id <= 0) {
			logger.warn("Invalid ID provided: {}", id);
			return Mono.error(new IllegalArgumentException("ID must be a positive integer"));
		}

		return inbox820Service.getById(id).doOnSuccess(record -> {
			if (record != null) {
				logger.info("Successfully retrieved Inbox820 with ID: {}", id);
			} else {
				logger.warn("No Inbox820 record found for ID: {}", id);
			}
		});
	}

	/**
	 * Fetches Inbox820 records based on the provided correlationKey1Val.
	 *
	 * @param keyVal the correlation key value
	 * @return Flux of Inbox820 records
	 */
	@GetMapping("/getByCorrelationKey1Val/{keyVal}")
	public Flux<Inbox820> getByCorrelationKey1Val(@PathVariable String keyVal) {
		logger.info("Received request to fetch Inbox820 records by correlationKey1Val: {}", keyVal);

		if (keyVal == null || keyVal.trim().isEmpty()) {
			logger.warn("Invalid correlationKey1Val received: null or empty");
			return Flux.error(new IllegalArgumentException("correlationKey1Val must not be null or empty"));
		}

		return inbox820Service.getByCorrelationKey1Val(keyVal.trim())
				.doOnNext(record -> logger.debug("Found Inbox820 record with ID: {}", record.getId())).doOnComplete(
						() -> logger.info("Completed fetching Inbox820 records for correlationKey1Val: {}", keyVal));
	}

	/**
	 * Updates an existing Inbox820 record.
	 *
	 * @param id          the ID of the record to update
	 * @param updatedData the new data to update
	 * @return ResponseEntity with updated record or appropriate error
	 */
	@PutMapping("/updateInbox820/{id}")
	public Mono<ResponseEntity<Inbox820>> updateInbox820(@PathVariable Integer id, @RequestBody Inbox820 updatedData) {
		logger.info("Received request to update Inbox820 with ID: {}", id);

		return inbox820Service.updateInbox820(id, updatedData).map(updated -> {
			logger.info("Successfully updated Inbox820 with ID: {}", id);
			return ResponseEntity.ok(updated);
		}).onErrorResume(e -> {
			if (e instanceof ResponseStatusException rse && rse.getStatusCode() == HttpStatus.NOT_FOUND) {
				logger.warn("Inbox820 not found with ID: {}", id);
				return Mono.just(ResponseEntity.notFound().build());
			}
			logger.error("Error occurred while updating Inbox820 with ID: {}: {}", id, e.getMessage(), e);
			return Mono.just(ResponseEntity.badRequest().build());
		});
	}

	/**
	 * Retrieves file-related data for a specific Inbox820 record by ID.
	 *
	 * @param id the ID of the Inbox820 record
	 * @return Mono containing file data as a Map
	 */
	@GetMapping("/file/{id}")
	public Mono<Map<String, Object>> getInbox820FileData(@PathVariable int id) {
		logger.info("Received request to get file data for Inbox820 with ID: {}", id);
		return inbox820Service.getInbox820FileData(id)
				.doOnSuccess(result -> logger.info("Successfully fetched file data for ID: {}", id)).doOnError(
						error -> logger.error("Error fetching file data for ID {}: {}", id, error.getMessage(), error));
	}

	/**
	 * Retrieves status counts (e.g., Read/Unread) for a given partner ID.
	 *
	 * @param partnerId the ID of the partner
	 * @return Mono containing a map of status and counts
	 */
	@GetMapping("statusCounts/{partnerId}")
	public Mono<Map<String, Long>> getStatusCountsByPartnerId(@PathVariable String partnerId) {
		logger.info("Received request for status counts by partnerId: {}", partnerId);

		if (partnerId == null || partnerId.trim().isEmpty()) {
			logger.warn("Invalid partnerId received: null or empty");
			return Mono.error(new IllegalArgumentException("partnerId must not be null or empty"));
		}

		return inbox820Service.getStatusCountsByPartnerId(partnerId.trim());
	}

	/**
	 * Filters Inbox820 records based on provided optional query parameters.
	 *
	 * @return Flux of matching Inbox820 records
	 */
	@GetMapping("/filter")
	public Flux<Inbox820> filterInbox820(@RequestParam(required = false) String transactionName,
			@RequestParam(required = false) String transactionType, @RequestParam(required = false) String sapId,
			@RequestParam(required = false) String isaSenderId, @RequestParam(required = false) String isaReceiverId,
			@RequestParam(required = false) String partnerId, @RequestParam(required = false) String correlationKey1Val,
			@RequestParam(required = false) String correlationKey2Val,
			@RequestParam(required = false, name = "clearingDocNumber") String correlationKey3Val,
			@RequestParam(required = false) String correlationKey4Val, @RequestParam(required = false) String status,
			@RequestParam(required = false) String startDate, @RequestParam(required = false) String endDate) {
		logger.info("Received filter request for Inbox820 with params...");
		return inbox820Service.getFilteredInbox820(transactionName, transactionType, sapId, isaSenderId, isaReceiverId,
				partnerId, correlationKey1Val, correlationKey2Val, correlationKey3Val, correlationKey4Val, status,
				startDate, endDate);
	}

	/**
	 * Fetches both Inbox820 and its related Details820 records using
	 * correlationKey1Val.
	 *
	 * @param correlationKey1Val the correlation key to match
	 * @return Mono of combined Inbox820WithDetailsResponse
	 */
	@GetMapping("/inbox820/correlation/{correlationKey1Val}")
	public Mono<Inbox820WithDetailsResponse> getAllDataByCorrelationKey(@PathVariable String correlationKey1Val) {
		logger.info("Received request to fetch Inbox820 and Details820 for correlationKey1Val: {}", correlationKey1Val);
		return inbox820Service.getInboxAndDetailsByCorrelationKey(correlationKey1Val);
	}

	/**
	 * Endpoint to search Inbox820 records based on the provided invoice number and
	 * partner ID. The search supports partial matching on the invoice number
	 * (case-insensitive), and exact matching on the trimmed partner ID.
	 * 
	 * @param invoiceNumber the invoice number or partial value to search
	 *                      (case-insensitive)
	 * @param partnerId     the partner ID (ISA Receiver ID) to filter records
	 *                      (exact match after trimming)
	 * @return a {@link Flux} stream of matching {@link Inbox820} records
	 */
	@GetMapping("/search")
	public Flux<Inbox820> searchInboxByInvoiceNumber(@RequestParam String invoiceNumber,
			@RequestParam(name = "partnerId") String partnerId) {
		logger.info("Received request to search Inbox820 by invoiceNumber: [{}], partnerId: [{}]", invoiceNumber,
				partnerId);

		return inbox820Service.searchInboxByInvoiceNumber(invoiceNumber, partnerId)
				.doOnSubscribe(
						subscription -> logger.debug("Subscription started for invoiceNumber: [{}]", invoiceNumber))
				.doOnComplete(() -> logger.info("Completed search for invoiceNumber: [{}]", invoiceNumber))
				.doOnError(error -> logger.error("Error during search for invoiceNumber: [{}] -> {}", invoiceNumber,
						error.getMessage()));
	}

	/**
	 * Endpoint to generate an Excel file from JSON input containing header and line
	 * item data. This method receives a JSON payload with structure: {
	 * "headerInformation": { ... }, "lineItems": [ { ... }, ... ] } The Excel file
	 * is dynamically created using the provided data, and returned as a
	 * downloadable .xlsx file with a name like:
	 * Remittance_details_{clearingDocNumber}_{timestamp}.xlsx
	 *
	 * @param requestBody the incoming JSON request body wrapped in a Mono
	 * @param response    the reactive HTTP response to write the Excel file into
	 * @return a Mono<Void> signaling completion of file generation and streaming
	 */

	@PostMapping(value = "/generate-excel", produces = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
	public Mono<Void> generateExcelFromJson(@RequestBody Mono<Map<String, Object>> requestBody,
			ServerHttpResponse response) {

		return requestBody.flatMap(input -> {
			String clearingDocNumber = Optional.ofNullable((Map<String, Object>) input.get("headerInformation"))
					.map(h -> String.valueOf(h.getOrDefault("clearing_Doc_Number", "NA"))).orElse("NA");

			String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMdd_HHmmss"));
			String fileName = "Remittance_details_" + clearingDocNumber + "_" + timestamp + ".xlsx";

			logger.info("Generating Excel file: {}", fileName);

			return inbox820Service.generateExcelWithHeaderAndLineItems(input).flatMap(dataBuffer -> {
				ContentDisposition disposition = ContentDisposition.attachment().filename(fileName).build();

				response.getHeaders().setContentDisposition(disposition);
				response.getHeaders().add("Access-Control-Expose-Headers", "Content-Disposition");

				response.getHeaders().setContentType(
						MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));

				return response.writeWith(Mono.just(dataBuffer));
			});

		}).doOnError(error -> logger.error("Error generating Excel: {}", error.getMessage(), error));
	}
}
