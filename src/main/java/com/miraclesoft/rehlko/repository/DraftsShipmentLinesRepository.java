package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.DraftsShipmentLines;
import com.miraclesoft.rehlko.entity.OrderDetails;
import com.miraclesoft.rehlko.entity.ShipmentLines;

import reactor.core.publisher.Flux;

@Repository
public interface  DraftsShipmentLinesRepository extends ReactiveCrudRepository<DraftsShipmentLines, Integer> {
	Flux<DraftsShipmentLines> findAll();
	
	@Query("SELECT *FROM drafts_shipment_lines WHERE shipment_id =:shipmentId and partner_id=:partnerId")
	Flux<DraftsShipmentLines>  findByshipmentId(String shipmentId,String partnerId);

}
