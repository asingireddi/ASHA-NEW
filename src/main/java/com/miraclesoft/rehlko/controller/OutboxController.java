package com.miraclesoft.rehlko.controller;

import com.miraclesoft.rehlko.dto.UpdateFlagsRequestDTO;
import com.miraclesoft.rehlko.entity.ChangeRequest;
import com.miraclesoft.rehlko.entity.Outbox;
import com.miraclesoft.rehlko.service.OutboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/outbox")
public class OutboxController {

	private static final Logger logger = LoggerFactory.getLogger(OutboxController.class);

	private final OutboxService outboxService;

	public OutboxController(OutboxService outboxService) {
		this.outboxService = outboxService;
	}

	/**
	 * Fetches outbox data for a given partner ID.
	 */
	@GetMapping("/{partnerId}")
	public Mono<Map<String, Object>> getOutboxData(@PathVariable String partnerId) {
		logger.info("Fetching outbox data for partnerId: {}", partnerId);
		return outboxService.getOutBoxData(partnerId);
	}

	/**
	 * Saves a new outbox record.
	 */
	@PostMapping("/save-to-outbox/{type}")
	public Mono<Map<String, Object>> saveToOutbox(@PathVariable String type, @RequestBody Outbox outbox) {
		logger.info("Saving outbox for type: {}", type);
		return outboxService.saveToOutbox(type, outbox);
	}

	/**
	 * Saves a shipment record to the outbox.
	 */
	@PostMapping("/save-to-outbox/{type}/{asn}")
	public Mono<Map<String, Object>> saveShipmentsToOutbox(@PathVariable String type, @PathVariable String asn,
			@RequestBody Outbox outbox) {
		logger.info("Saving shipment outbox for type: {}, ASN: {}", type, asn);
		return outboxService.saveShipmentsToOutbox(type, asn, outbox);
	}

	/**
	 * Retrieves outbox details by ID.
	 */
	@GetMapping("/get-outbox/{id}")
	public Mono<Map<String, Object>> getOutbox(@PathVariable int id) {
		logger.info("Getting outbox details for ID: {}", id);
		return outboxService.getOutBox(id);
	}

	/**
	 * Saves a change request to the outbox.
	 */
	@PostMapping("/save-change-request/{type}")
	public Mono<Map<String, Object>> changeRequest(@PathVariable String type,
			@RequestBody ChangeRequest changeRequest) {
		logger.info("Saving change request for type: {}", type);
		return outboxService.saveChangeRequest(type, changeRequest);
	}

	/**
	 * Updates the draft status of an outbox record.
	 */
	@PutMapping("/update-draft-status/{id}/{transactionType}")
	public Mono<Map<String, String>> updateStatus(@PathVariable int id, @PathVariable String transactionType,
			@RequestBody Outbox outbox) {
		logger.info("Updating draft status for ID: {}, transactionType: {}", id, transactionType);
		return outboxService.updateStatus(id, transactionType, outbox.getCorrelationKey1(), outbox.getStatus());
	}

	// @PutMapping("/update-draft-status/{id}")
//	public Mono<Map<String, String>> updatetatus(@PathVariable int id, @RequestBody Outbox outbox) {
//		return outboxService.updateStatus(id, outbox.getCorrelationKey1(), outbox.getStatus()).map(updatedCount -> {
//			if (updatedCount > 0) {
//				return Map.of("Success", "status updated successfully for ID: " + id);
//			} else {
//				return Map.of("Failed", "No status found for ID: " + id);
//			}
//		}).defaultIfEmpty(Map.of("Failed", "status updated " + id));
//	}
	/**
	 * Updates check-in status of outbox.
	 */
	@PutMapping("/update/{id}/{transactionType}")
	public Mono<Map<String, String>> updateCheckinStatus(@PathVariable int id, @PathVariable String transactionType,
			@RequestBody Outbox outbox) {
		logger.info("Updating check-in status for ID: {}, transactionType: {}", id, transactionType);
		return outboxService.updateStatus(outbox.getCorrelationKey1(), outbox.getStatus(), transactionType, id);
	}

	/**
	 * Retrieves change request details.
	 */
	@GetMapping("/get-change-request/{orderId}/{requestType}")
	public Mono<Map<String, Object>> getInvoiceData(@PathVariable String orderId, @PathVariable String requestType)
			throws IOException {
		logger.info("Getting change request for orderId: {}, requestType: {}", orderId, requestType);
		return outboxService.getChangeRequestInformation(orderId, requestType);
	}

	/**
	 * Retrieves lines associated with a change request.
	 */
	@GetMapping("/get-change-request-lines/{orderId}/{requestType}")
	public Mono<Map<String, Object>> getChangeRequestLines(@PathVariable String orderId,
			@PathVariable String requestType) throws IOException {
		logger.info("Getting change request lines for orderId: {}, requestType: {}", orderId, requestType);
		return outboxService.getChangeRequestLines(orderId, requestType);
	}

	/**
	 * Updates a change request record.
	 */
	@PutMapping("/update-change-request/{id}/{orderId}")
	public Mono<Map<String, ?>> updateShipmentStatus(@PathVariable int id, @PathVariable String orderId,
			@RequestBody ChangeRequest changeRequest) {
		logger.info("Updating change request for ID: {}, orderId: {}", id, orderId);
		return outboxService.updateChaneRequest(id, orderId, changeRequest);
	}

	/**
	 * Saves an order acknowledgment.
	 */
	@PostMapping("/save-order-ack")
	public Mono<Map<String, Object>> orderAcknowledgeMent(@RequestBody ChangeRequest changeRequest) {
		logger.info("Saving order acknowledgment");
		return outboxService.orderAcknowledgeMent(changeRequest);
	}

	/**
	 * Retrieves order acknowledgment lines.
	 */
	@GetMapping("/get-order-ack-lines/{orderId}/{requestType}")
	public Mono<Map<String, Object>> getOrderAckLines(@PathVariable String orderId, @PathVariable String requestType)
			throws IOException {
		logger.info("Getting order acknowledgment lines for orderId: {}, requestType: {}", orderId, requestType);
		return outboxService.getOrderAckLines(orderId, requestType);
	}

	/**
	 * Retrieves order acknowledgment by order ID and request type.
	 */
	@GetMapping("/get-order-ack/{orderId}/{requestType}")
	public Mono<Map<String, Object>> getOrderAck(@PathVariable String orderId, @PathVariable String requestType)
			throws IOException {
		logger.info("Getting order acknowledgment for orderId: {}, requestType: {}", orderId, requestType);
		return outboxService.getOrderAck(orderId, requestType);
	}

	/**
	 * Retrieves outbox entries filtered by status.
	 */
	@GetMapping("/outbox-by-status/{status}")
	public Mono<Map<String, Object>> getOutboxByStatus(@PathVariable String status) throws IOException {
		logger.info("Fetching outbox by status: {}", status);
		return outboxService.getOutboxByStatus(status);
	}

	/**
	 * Retrieves order acknowledgment lines for an outbox record.
	 */
	@GetMapping("/getoutbox-order-ack-lines/{orderNumber}/{transactionType}")
	public Mono<Map<String, Object>> getOutboxOrderAckLines(@PathVariable String orderNumber,
			@PathVariable String transactionType) throws IOException {
		logger.info("Getting outbox order ack lines for orderNumber: {}, transactionType: {}", orderNumber,
				transactionType);
		return outboxService.getOutboxOrderAckLines(orderNumber, transactionType);
	}

	/**
	 * Submits a B2B change request.
	 */
	@PostMapping("/b2bChangeRequestSubmit")
	public Mono<Map<String, Object>> b2bChangeRequestSubmit(@RequestBody ChangeRequest changeRequest)
			throws IOException {
		logger.info("Submitting B2B change request");
		return outboxService.b2bChangeRequestSubmit(changeRequest);
	}

	/**
	 * Retrieves file data by outbox record ID.
	 */
	@GetMapping("/file/{id}")
	public Mono<Map<String, Object>> getOutboxFileData(@PathVariable int id) throws IOException {
		logger.info("Fetching outbox file data for ID: {}", id);
		return outboxService.getOutboxFileData(id);
	}

	/**
	 * Searches outbox with filters.
	 */
	@GetMapping("/search")
	public Flux<Outbox> search(@RequestParam(name = "transactionType", required = false) String transactionType,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate,
			@RequestParam(name = "partnerId", required = false) String partnerId,
			@RequestParam(name = "correlationKey1", required = false) String correlationKey1,
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(required = false, defaultValue = "false") Boolean trashFlag,
			@RequestParam(required = false, defaultValue = "false") Boolean archiveFlag) throws IOException {
		logger.info("Searching outbox with filters: transactionType={}, partnerId={}, status={}", transactionType,
				partnerId, status);
		return outboxService.fetch(transactionType, startDate, endDate, partnerId, correlationKey1, status, trashFlag,
				archiveFlag);
	}

	/**
	 * Fetches outbox entries by correlation key and transaction type.
	 */
	@GetMapping("/outboxdata/{correlationKey}/{transactionType}")
	public Flux<Outbox> getOutboxByCorrelationKey(@PathVariable String correlationKey,
			@PathVariable String transactionType) {
		logger.info("Fetching outbox by correlationKey: {}, transactionType: {}", correlationKey, transactionType);
		return outboxService.getOutboxByCorrelationKey(correlationKey, transactionType);
	}

	/**
	 * Fetches invoice-related outbox entries by correlation key.
	 */
	@GetMapping("/outboxinvoice/{correlationKey}/{transactionType}")
	public Flux<Outbox> getOutboxInvoiceByCorrelationKey(@PathVariable String correlationKey,
			@PathVariable String transactionType) {
		logger.info("Fetching outbox invoice by correlationKey: {}, transactionType: {}", correlationKey,
				transactionType);
		return outboxService.getOutboxInvoiceByCorrelationKey(correlationKey, transactionType);
	}

	/**
	 * Checks if 856 transaction exists for given correlationKey1.
	 */
	@GetMapping("/check-856")
	public Mono<ResponseEntity<Map<String, Boolean>>> checkTransaction856(
			@RequestParam("correlation_key1") String correlationKey) {
		logger.info("Checking if 856 transaction exists for correlationKey1: {}", correlationKey);
		return outboxService.checkTransactionType856ByCorrelationKey(correlationKey)
				.map(result -> ResponseEntity.ok(Map.of("exists", result)));
	}

	/**
	 * Updates archive or trash flags for multiple correlation keys.
	 */
	@PutMapping("/update-flags")
	public Mono<ResponseEntity<String>> updateFlags(@RequestBody UpdateFlagsRequestDTO request) {
		logger.info("Updating flags for correlationKeys: {}, type: {}, value: {}", request.getCorrelationKey1List(),
				request.getType(), request.isFlagValue());
		return outboxService.updateFlags(request.getCorrelationKey1List(), request.getType(), request.isFlagValue())
				.thenReturn(ResponseEntity.ok("Flags updated successfully")).onErrorResume(e -> {
					logger.error("Error updating flags", e);
					return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
				});
	}
}
