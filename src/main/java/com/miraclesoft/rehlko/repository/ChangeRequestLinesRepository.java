package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.ChangeRequestLines;

import reactor.core.publisher.Flux;

@Repository
public interface  ChangeRequestLinesRepository extends ReactiveCrudRepository<ChangeRequestLines, Integer> {

	
	@Query("SELECT *FROM order_ack_lines WHERE change_request_id =:orderId  and status=:requestType")
	Flux<ChangeRequestLines>  findByOrderId(String orderId,String requestType);
	
	
	@Query("SELECT *FROM order_ack_lines WHERE change_request_id =:orderId  and order_status=:requestType")
	Flux<ChangeRequestLines>  findByOrderIdAndStatus(String orderId,String requestType);
	
	
	@Query("SELECT * FROM order_ack_lines WHERE order_number = :orderNumber and transaction_type = :transactionType")
	Flux<ChangeRequestLines>  findByOrderNumber(String orderNumber, String transactionType);
	
	

}
