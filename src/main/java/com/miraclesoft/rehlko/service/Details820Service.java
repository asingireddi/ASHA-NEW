package com.miraclesoft.rehlko.service;

import com.miraclesoft.rehlko.entity.Details820;
import com.miraclesoft.rehlko.repository.Details820Repository;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Service class to handle operations related to Details820 entities.
 */
@Service
public class Details820Service {

	private static final Logger logger = LoggerFactory.getLogger(Details820Service.class);

	@Autowired
	private Details820Repository details820Repository;

	/**
	 * Retrieves all Details820 records.
	 * 
	 * @return Flux of Details820 records
	 */
	public Flux<Details820> getAll() {
		logger.info("Fetching all Details820 records");
		return details820Repository.findAll()
				.doOnNext(record -> logger.debug("Fetched Details820 record with ID: {}", record.getId()))
				.doOnComplete(() -> logger.info("Completed fetching all Details820 records")).doOnError(error -> logger
						.error("Error occurred while fetching Details820 records: {}", error.getMessage(), error));
	}

	/**
	 * Retrieves all Details820 records matching the given correlationKey1Val.
	 * 
	 * @param key the correlation key to filter by
	 * @return Flux of matching Details820 records
	 */
	public Flux<Details820> getByCorrelationKey1Val(String key) {
		logger.info("Fetching Details820 records by correlationKey1Val: {}", key);
		return details820Repository.findByCorrelationKey1Val(key)
				.doOnNext(record -> logger.debug("Matched Details820 record ID: {}", record.getId()))
				.doOnComplete(() -> logger.info("Completed fetching Details820 records for key: {}", key))
				.doOnError(error -> logger.error("Error fetching Details820 records for key {}: {}", key,
						error.getMessage(), error));
	}

	/**
	 * Retrieves a Details820 record by ID.
	 * 
	 * @param id the ID of the record
	 * @return Mono of Details820 record
	 */
	public Mono<Details820> getById(Integer id) {
		logger.info("Fetching Details820 record by ID: {}", id);
		return details820Repository.findById(id).doOnSuccess(record -> {
			if (record != null) {
				logger.info("Found Details820 record with ID: {}", id);
			} else {
				logger.warn("No Details820 record found with ID: {}", id);
			}
		}).doOnError(
				error -> logger.error("Error fetching Details820 record by ID {}: {}", id, error.getMessage(), error));
	}
}
