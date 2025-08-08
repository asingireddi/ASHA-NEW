package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.DraftsShipmentNotice;
import com.miraclesoft.rehlko.entity.OrderDetails;
import com.miraclesoft.rehlko.entity.ShipmentNotice;

import reactor.core.publisher.Flux;

@Repository
public interface  DraftsShipmentRepository extends ReactiveCrudRepository<DraftsShipmentNotice, Integer> {
	Flux<DraftsShipmentNotice> findAll();
	
	@Query("SELECT *FROM drafts_shipment_notice WHERE asn_number =:asnNumber")
	Flux<DraftsShipmentNotice> findByIdAndAsnNumber(String asnNumber);
	
	@Query("SELECT *FROM drafts_shipment_notice WHERE partner_id =:partnerId")
	Flux<DraftsShipmentNotice>  findByPartnerId(String partnerId);
}





   