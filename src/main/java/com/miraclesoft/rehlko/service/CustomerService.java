package com.miraclesoft.rehlko.service;

import com.miraclesoft.rehlko.entity.Customers;
import com.miraclesoft.rehlko.repository.CustomersRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

@Service
public class CustomerService {

	private final CustomersRepository customersRepository;

	private static final Logger logger = LoggerFactory.getLogger(CustomerService.class.getName());

	public CustomerService(CustomersRepository customersRepository) {
		this.customersRepository = customersRepository;
	}

//	public Mono<Map<String, Object>> saveCustomer(Customers customers) {
//		logger.info("Executing the method :: saveCustomer ");
//		Map<String, Object> response = new HashMap<String, Object>();
//		response.put("message", "Customer registration failed");
//		response.put("status", false);
//		try {
//			customers.setCreatedAt(LocalDateTime.now());
//			customers = customersRepository.save(customers).block();
//			if (customers != null) {
//				response.put("message", "Customer registration successful");
//				response.put("status", true);
//			}
//			logger.info("Executed the method :: saveCustomer ");
//		} catch (Exception ex) {
//			logger.error(" saveCustomer :: {}" , ex.getMessage());
//		}
//		return Mono.just(response);
//	}


	public Mono<Map<String, Object>> saveCustomer(Customers customers) {
		logger.info("Executing the method :: saveCustomer");

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Customer registration failed");
		response.put("status", false);

//		customers.setCreatedAt(LocalDateTime.now());

		return customersRepository.save(customers)
				.map(savedCustomer -> {
					response.put("message", "Customer registration successful");
					response.put("status", true);
					response.put("data", savedCustomer);
					logger.info("Executed the method :: saveCustomer");
					return response;
				})
				.onErrorResume(ex -> {
					logger.error("saveCustomer :: {}", ex.getMessage(), ex);
					return Mono.just(response);
				});
	}


	public Mono<Map<String, Object>> getAllCustomers() {
		logger.info("Executing the method :: getAllCustomers ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to retrieve customers");
		response.put("status", false);
		response.put("data", new ArrayList<>());
		return customersRepository.findAll().collectList().map(customersList -> {
			if (!customersList.isEmpty()) {
				response.put("message", "customers retrieved successfully");
				response.put("status", true);
				response.put("data", customersList);
			}
			logger.info("Executed the method :: getAllCustomers ");
			return response;
		}).onErrorResume(ex -> {
			logger.error(" getAllCustomers :: {}", ex.getMessage());
			return Mono.just(response);
		});
	}
}
