package com.miraclesoft.rehlko.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.rehlko.entity.Details820;
import com.miraclesoft.rehlko.service.Details820Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * REST controller for managing Details820 HTTP Requests.
 */

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/820details")
public class Details820Controller {

	private static final Logger logger = LoggerFactory.getLogger(Details820Controller.class);

	@Autowired
	private Details820Service details820Service;

	/**
	 * Retrieves all Details820 records.
	 *
	 * @return Flux of Details820
	 */
	@GetMapping
	public Flux<Details820> getAll() {
		logger.info("Received request to fetch all Details820 records");
		return details820Service.getAll().doOnComplete(() -> logger.info("Completed fetching all Details820 records"));
	}

	/**
	 * Retrieves a Details820 record by ID.
	 *
	 * @param id the ID of the Details820 record
	 * @return Mono of Details820
	 */
	@GetMapping("/{id}")
	public Mono<Details820> getById(@PathVariable Integer id) {
		logger.info("Received request to fetch Details820 by ID: {}", id);
		return details820Service.getById(id);
	}

	/**
	 * Retrieves Details820 records by correlationKey1Val.
	 *
	 * @param key the correlation key value
	 * @return Flux of Details820
	 */
	@GetMapping("/correlationKey1Val/{key}")
	public Flux<Details820> getByCorrelationKey1Val(@PathVariable String key) {
		logger.info("Received request to fetch Details820 by correlationKey1Val: {}", key);
		return details820Service.getByCorrelationKey1Val(key);
	}

}
