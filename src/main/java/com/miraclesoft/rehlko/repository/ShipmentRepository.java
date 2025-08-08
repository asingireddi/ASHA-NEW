package com.miraclesoft.rehlko.repository;

import java.util.List;
import java.util.Map;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.ShipmentLines;
import com.miraclesoft.rehlko.entity.ShipmentNotice;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface  ShipmentRepository extends ReactiveCrudRepository<ShipmentNotice, Integer> {
	Flux<ShipmentNotice> findAll();
	
	@Query("SELECT *FROM shipment_notice WHERE shipment_id =:shipmentId and partner_is=:partnerId and status=:shipmentType")
	Flux<ShipmentNotice>  findByShipmentId(String shipmentId,String partnerId,String shipmentType);
	
	@Query("SELECT *FROM shipment_notice WHERE partner_id =:partnerId and status=:shipmentType")
	Flux<ShipmentNotice>  findByPartnerId(String partnerId,String shipmentType);

	@Query("SELECT *FROM shipment_notice WHERE shipment_id =:shipmentId and status=:shipmentType")
	Flux<ShipmentNotice>  findByIdAndShipmentType(String shipmentId,String shipmentType);

	@Query("SELECT DISTINCT bill_of_landing, shipment_tracking_number, status FROM shipment_notice WHERE order_number =:orderNumber and transaction_type =:transactionType AND status IN ('Partial_Shipment', 'submit')")
	Flux<ShipmentNotice> findByOrderNumber1(int orderNumber, String transactionType);

	
	
//	@Query("SELECT qty FROM shipment_lines WHERE order_number =: order_number")
//	Flux<ShipmentLines> findBy(int orderNumber);
//	
	
}
