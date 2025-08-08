package com.miraclesoft.rehlko.controller;

import com.miraclesoft.rehlko.dto.UpdateFlagsRequestDTO;
import com.miraclesoft.rehlko.entity.Inbox;
import com.miraclesoft.rehlko.service.InboxService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "*")
public class InboxController {

	private static final Logger logger = LoggerFactory.getLogger(InboxController.class);

	private final InboxService inboxService;

	public InboxController(InboxService inboxService) {
		this.inboxService = inboxService;
	}

	/**
	 * Health check endpoint.
	 */
	@GetMapping("/healthcheck")
	public String healthCheck() {
		logger.info("Health check requested");
		return "Application loaded successfully";
	}

	/**
	 * Retrieves inbox data by partnerId.
	 */
	@GetMapping("/inbox/{partnerId}")
	public Mono<Map<String, Object>> getInboxData(@PathVariable String partnerId) {
		logger.info("Fetching inbox data for partnerId: {}", partnerId);
		return inboxService.getInboxData(partnerId);
	}

	/**
	 * Saves a new inbox record.
	 */
	@PostMapping("/save-to-inbox")
	public Mono<Map<String, Object>> saveToInbox(@RequestBody Inbox inbox) {
		logger.info("Saving inbox record: {}", inbox);
		return inboxService.saveToInbox(inbox);
	}

	/**
	 * Retrieves file data for a given inbox record ID.
	 */
	@GetMapping("/file/{id}")
	public Mono<Map<String, Object>> getFileData(@PathVariable int id) throws IOException {
		logger.info("Fetching file data for inbox ID: {}", id);
		return inboxService.getFileData(id);
	}

	/**
	 * Writes data to file from an inbox object.
	 */
	@PutMapping("/update-file")
	public Mono<Map<String, Object>> writeDataToFile(@RequestBody Inbox inbox) throws IOException {
		logger.info("Writing data to file for inbox: {}", inbox);
		return inboxService.writeDataToFile(inbox);
	}

	// @PutMapping("/updatestatus/{id}")
//	public Mono<Map<String, String>> updateCheckinStatus(@PathVariable int id, @RequestBody Inbox inbox) {
//		return inboxService.updateStatus(inbox.getCorrelationKey1(), inbox.getStatus()).map(updatedCount -> {
//			if (updatedCount > 0) {
//				return Map.of("Success", "status updated successfully for order: " + id);
//			} else {
//				return Map.of("Failed", "No status found for order: " + id);
//			}
//		}).defaultIfEmpty(Map.of("Failed", "status updated " + id));
//	}

	/**
	 * Updates status of an inbox record.
	 */
	@PutMapping("/updatestatus/{id}/{transactionType}")
	public Mono<Map<String, String>> updateCheckinStatus(@PathVariable int id, @PathVariable String transactionType,
			@RequestBody Inbox inbox) {
		logger.info("Updating status for ID: {}, transactionType: {}, correlationKey1: {}", id, transactionType,
				inbox.getCorrelationKey1());
		return inboxService.updateStatus(inbox.getCorrelationKey1(), inbox.getStatus(), id, transactionType);
	}

	/**
	 * Retrieves inbox data by record ID.
	 */
	@GetMapping("/inbox-data/{id}")
	public Mono<Map<String, Object>> getInboxDataById(@PathVariable int id) {
		logger.info("Fetching inbox data by ID: {}", id);
		return inboxService.getInboxDataById(id);
	}

	/**
	 * Updates the FAK status for an inbox record.
	 */
	@PutMapping("/update_fak_status/{id}")
	public Mono<Map<String, String>> updateFakStatus(@PathVariable String id, @RequestBody Inbox inbox) {
		logger.info("Updating FAK status for correlationKey1: {}", inbox.getCorrelationKey1());
		return inboxService.updateFakStatus(inbox.getCorrelationKey1(), inbox.getFakStatus());
	}

	/**
	 * Retrieves inbox records filtered by status.
	 */
	@GetMapping("/inbox-by-status/{status}")
	public Mono<Map<String, Object>> getInboxDataByStatus(@PathVariable String status) {
		logger.info("Fetching inbox data by status: {}", status);
		return inboxService.getInboxDataByStatus(status);
	}

	/**
	 * Submits B2B FAK status update.
	 */
	@PostMapping("/b2bFakStatusSubmit")
	public Mono<Map<String, Object>> b2bFakStatusSubmit(@RequestBody Inbox inbox) throws IOException {
		logger.info("Submitting B2B FAK status for inbox: {}", inbox);
		return inboxService.b2bFakStatusSubmit(inbox);
	}

	// @GetMapping("/send-email")
//    public String sendEmail() throws AddressException, MessagingException, javax.mail.internet.AddressException, javax.mail.MessagingException {
//    	inboxService.sendSimpleMessage();
//    return "Email Sent!";
//    }

	/**
	 * Retrieves all partner information.
	 */
	@GetMapping("/getAllPartners")
	public Mono<Map<String, Object>> getAllPartners() {
		logger.info("Fetching all partners");
		return inboxService.getAllPartners();
	}

	/**
	 * Searches inbox data with multiple filters.
	 */
	@GetMapping("/search")
	public Flux<Inbox> search(@RequestParam(name = "transactionType", required = false) String transactionType,
			@RequestParam(name = "startDate", required = false) String startDate,
			@RequestParam(name = "endDate", required = false) String endDate,
			@RequestParam(name = "partnerId", required = false) String partnerId,
			@RequestParam(name = "correlationKey1", required = false) String correlationKey1,
			@RequestParam(name = "status", required = false) String status,
			@RequestParam(required = false, defaultValue = "false") Boolean trashFlag,
			@RequestParam(required = false, defaultValue = "false") Boolean archiveFlag) {
		logger.info("Performing inbox search with filters: partnerId={}, transactionType={}, status={}", partnerId,
				transactionType, status);
		return inboxService.fetch(transactionType, startDate, endDate, partnerId, correlationKey1, status, trashFlag,
				archiveFlag);
	}

	/**
	 * Updates inbox status by correlation key.
	 */
	@GetMapping("/update-status/{correlationKey}")
	public Mono<String> updateStatus(@PathVariable String correlationKey) {
		logger.info("Updating inbox status for correlationKey: {}", correlationKey);
		return inboxService.updateInboxStatus(correlationKey).thenReturn("Status updated successfully.");
	}

	/**
	 * Updates archive/trash flags on multiple records.
	 */
	@PutMapping("/inbox/update-flags")
	public Mono<ResponseEntity<String>> updateFlags(@RequestBody UpdateFlagsRequestDTO request) {
		logger.info("Updating flags. Type: {}, FlagValue: {}, CorrelationKeys: {}", request.getType(),
				request.isFlagValue(), request.getCorrelationKey1List());
		return inboxService.updateFlags(request.getCorrelationKey1List(), request.getType(), request.isFlagValue())
				.thenReturn(ResponseEntity.ok("Flags updated successfully")).onErrorResume(e -> {
					logger.error("Error updating flags", e);
					return Mono.just(ResponseEntity.badRequest().body(e.getMessage()));
				});
	}

	/**
	 * Retrieves a distinct list of statuses.
	 */
	@GetMapping("/distinct-statuses")
	public Mono<List<String>> getDistinctStatuses() {
		logger.info("Fetching distinct statuses from inbox");
		return inboxService.getDistinctStatuses();
	}

	/**
	 * Bulk update of statuses by user and correlation keys.
	 */
	@PutMapping("/update-status")
	public Mono<ResponseEntity<String>> updateStatusList(@RequestParam("userId") Long userId,
			@RequestParam("correlationKeys") List<String> correlationKeys, @RequestParam("status") String status) {
		logger.info(" updating status to '{}' by userId: {} for {} correlationKeys", status, userId,
				correlationKeys.size());
		return inboxService.updateInboxStatus(userId, correlationKeys, status)
				.then(Mono.just(ResponseEntity.ok("Inbox statuses updated successfully for list of records.")));
	}
}
