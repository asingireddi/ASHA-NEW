package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.ChangeRequestData;

import reactor.core.publisher.Flux;

@Repository
public interface  ChangeRequestRepository extends ReactiveCrudRepository<ChangeRequestData, Integer> {
	

	@Query("SELECT *FROM order_ack WHERE change_request_id =:orderId  and status=:requestType")
	Flux<ChangeRequestData>  findByOrderId(String orderId,String requestType);
	
	
	@Query("SELECT *FROM order_ack WHERE change_request_id =:orderId  and order_status=:requestType")
	Flux<ChangeRequestData>  findByOrderIdAndStatus(String orderId,String requestType);


}
