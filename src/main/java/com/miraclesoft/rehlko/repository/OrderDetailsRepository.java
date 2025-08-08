package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.OrderDetails;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface OrderDetailsRepository extends ReactiveCrudRepository<OrderDetails, Integer> {
	Flux<OrderDetails> findAll();

	@Query("SELECT * FROM order_details WHERE order_number =:orderNumber")
	Flux<OrderDetails> findByOrderNumber(String orderNumber);

	Mono<OrderDetails> getById(int id);

	@Query("UPDATE order_details o SET o.remaining_qty = :remQty WHERE o.id = :id")
	Mono<OrderDetails> update(int remQty, int id);

	@Query("UPDATE order_details o SET o.remaining_invoice_qty = :remainingInvoiceQty WHERE o.id = :id")
	Mono<OrderDetails> updatRemainingInvoiceQty(int remainingInvoiceQty, int id);

	@Query("SELECT * FROM order_details WHERE correlation_key1 = :correlationKey AND order_type = :orderType AND NOT (trash_flag = 1 OR archive_flag = 1)")
	Flux<OrderDetails> findAllByCorrelationKeyAndOrderType(@Param("correlationKey") String correlationKey,
			@Param("orderType") String orderType);

	@Query("SELECT * FROM order_details " + "WHERE correlation_key1 = :correlationKey " + "AND order_type = :orderType "
			+ "AND (line_item IS NULL OR LOWER(line_item) != 'cancelled') "
			+ "AND NOT (trash_flag = 1 OR archive_flag = 1)")
	Flux<OrderDetails> findNonCancelledByCorrelationKeyAndOrderType(@Param("correlationKey") String correlationKey,
			@Param("orderType") String orderType);

//	@Query("SELECT * FROM order_details WHERE correlation_key1 = :correlationKey AND order_type = :orderType")
//	Flux<OrderDetails> findByCorrelationKeyAndOrderType(String correlationKey, String orderType);

}