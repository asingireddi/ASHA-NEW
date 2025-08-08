package com.miraclesoft.rehlko.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.rehlko.service.DashboardAPIService;

import reactor.core.publisher.Mono;

@RestController
public class DashboardAPIController {

	private static final Logger logger = LoggerFactory.getLogger(DashboardAPIController.class);

	@Autowired
	private DashboardAPIService dashboardAPIService;

	/**
	 * Retrieves the status counts grouped by types for a given partner ID.
	 *
	 * @param partnerId The partner ID for which to fetch the status counts.
	 * @return A Mono containing a nested Map with the count information wrapped in
	 *         a ResponseEntity.
	 */
	@GetMapping("/statuscounts")
	public Mono<ResponseEntity<Map<String, Map<String, Long>>>> getStatusCounts(@RequestParam String partnerId) {
		logger.info("Received request to fetch status counts for partnerId: {}", partnerId);

		return dashboardAPIService.getStatusCountByPartnerId(partnerId).map(counts -> {
			logger.info("Successfully retrieved status counts for partnerId: {}", partnerId);
			return ResponseEntity.ok(counts);
		}).doOnError(error -> logger.error("Error retrieving status counts for partnerId: {}", partnerId, error))
				.defaultIfEmpty(ResponseEntity.notFound().build());
	}
}
