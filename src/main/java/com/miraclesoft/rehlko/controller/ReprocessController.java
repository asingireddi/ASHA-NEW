package com.miraclesoft.rehlko.controller;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.miraclesoft.rehlko.service.ReprocessService;

import reactor.core.publisher.Mono;

@CrossOrigin
@RestController
public class ReprocessController {

	private static final Logger logger = LoggerFactory.getLogger(ReprocessController.class);

	private final ReprocessService reprocessService;

	public ReprocessController(ReprocessService reprocessService) {
		this.reprocessService = reprocessService;
	}

	/**
	 * Reprocesses transactions based on parameters provided.
	 * 
	 * @param transactionType the type of transaction (required)
	 * @param id              the record ID (optional)
	 * @param partnerId       the partner ID (optional)
	 * @param limit           number of records to process (required if id not
	 *                        given)
	 * @return Mono<String> response message after reprocessing
	 * @throws IOException in case of any processing error
	 */
	@PostMapping("/reprocess")
	public Mono<String> reprocess(@RequestParam(name = "transactionType", required = true) String transactionType,
			@RequestParam(name = "id", required = false) String id,
			@RequestParam(name = "partnerId", required = false) String partnerId,
			@RequestParam(name = "limit", required = true) int limit) throws IOException {

		if (id != null && partnerId != null) {
			logger.info("Reprocessing with id={}, partnerId={}, transactionType={}", id, partnerId, transactionType);
			return reprocessService.reprocess(transactionType, id, partnerId)
					.doOnSuccess(msg -> logger.info("Reprocessing (by id) completed successfully"))
					.doOnError(err -> logger.error("Error during reprocessing (by id)", err));
		} else {
			logger.info("Reprocessing with transactionType={}, limit={}", transactionType, limit);
			return reprocessService.reprocess(transactionType, limit)
					.doOnSuccess(msg -> logger.info("Reprocessing (by limit) completed successfully"))
					.doOnError(err -> logger.error("Error during reprocessing (by limit)", err));
		}
	}
}
