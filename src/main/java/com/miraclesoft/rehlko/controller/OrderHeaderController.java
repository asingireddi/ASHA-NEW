package com.miraclesoft.rehlko.controller;

import com.miraclesoft.rehlko.entity.OrderHeader;
import com.miraclesoft.rehlko.service.OrderHeaderService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;

@RestController
@CrossOrigin(origins = "*")
public class OrderHeaderController {

	private static final Logger log = LoggerFactory.getLogger(OrderHeaderController.class);

	@Autowired
	private OrderHeaderService orderHeaderService;

	/**
	 * Retrieves order headers by correlationKey1 and orderType.
	 *
	 * @param correlationKey1 The correlation key to filter by.
	 * @param orderType       The order type to filter by.
	 * @return Flux of matching OrderHeader records.
	 */
	@GetMapping("/order-headers")
	public Flux<OrderHeader> getOrderHeadersByCorrelationKey1(@RequestParam String correlationKey1,
			@RequestParam String orderType) {

		log.info("Fetching order headers for correlationKey1: {}, orderType: {}", correlationKey1, orderType);
		return orderHeaderService.getOrdersByCorrelationKey1andorderType(correlationKey1, orderType)
				.doOnComplete(() -> log.info("Completed fetching order headers"))
				.doOnError(err -> log.error("Error fetching order headers", err));
	}
}
