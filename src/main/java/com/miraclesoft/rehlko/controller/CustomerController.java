package com.miraclesoft.rehlko.controller;

import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.rehlko.entity.Customers;
import com.miraclesoft.rehlko.service.CustomerService;

import reactor.core.publisher.Mono;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/customers")
public class CustomerController {

	private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

	private final CustomerService customerService;

	public CustomerController(CustomerService customerService) {
		this.customerService = customerService;
	}

	/**
	 * Saves a new customer to the system.
	 *
	 * @param customers The customer object to be saved.
	 * @return A Mono containing a response map with status and customer data.
	 */
	@PostMapping("/save")
	public Mono<Map<String, Object>> saveCustomer(@RequestBody Customers customers) {
		logger.info("Saving new customer: {}", customers);
		return customerService.saveCustomer(customers)
				.doOnSuccess(response -> logger.info("Customer saved successfully"))
				.doOnError(error -> logger.error("Error saving customer", error));
	}

	/**
	 * Retrieves all customers from the system.
	 *
	 * @return A Mono containing a response map with all customer data.
	 */
	@GetMapping("")
	public Mono<Map<String, Object>> getAllCustomers() {
		logger.info("Fetching all customers");
		return customerService.getAllCustomers()
				.doOnSuccess(response -> logger.info("Fetched all customers successfully"))
				.doOnError(error -> logger.error("Error fetching customers", error));
	}
}
