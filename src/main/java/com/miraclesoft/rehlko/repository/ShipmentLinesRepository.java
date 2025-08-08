package com.miraclesoft.rehlko.repository;

import java.util.Map;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.dto.ShipmentLineDto;
import com.miraclesoft.rehlko.entity.ShipmentLines;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository
public interface ShipmentLinesRepository extends ReactiveCrudRepository<ShipmentLines, Integer> {
	Flux<ShipmentLines> findAll();

	@Query("SELECT *FROM shipment_lines WHERE shipment_id =:shipmentId  and status=:shipmentType")
	Flux<ShipmentLines> findByshipmentId(String shipmentId, String shipmentType);

//	@Query("SELECT sl.*, (od.qty - sl.qty) AS remaining_qtys " + "FROM shipment_lines sl "
//			+ "JOIN order_details od ON sl.order_number = od.order_number " + "WHERE sl.order_number = :orderNumber")
//	Flux<ShipmentLines> findByOrderNumber(@Param("orderNumber") int orderNumber);
	
	@Query("SELECT remaining_qty FROM order_details WHERE order_number =:orderNumber")
	Flux<ShipmentLines> findByOrderNumber(int orderNumber);

	@Query("SELECT *FROM shipment_lines WHERE shipment_id =:shipmentId")
	Flux<ShipmentLines> findLinesByshipmentId(String shipmentId);

	@Query("SELECT sl.*, od.unit_price AS price FROM shipment_lines sl join shipment_notice sn ON (sn.shipment_id=sl.shipment_id) "
			+ "JOIN order_details od ON od.correlation_key1 = sl.order_number AND od.po_line_number = sl.po_line_number where"
			+ " sn.bill_of_landing=:billOfLading and sn.order_number=:orderNumber and sn.transaction_Type = :transactionType")
	Flux<ShipmentLineDto> getBillOfLading(@Param("orderNumber") String orderNumber,
			@Param("billOfLading") String billOfLading, @Param("transactionType") String transactionType) ;

	@Query(" UPDATE shipment_lines SET remaining_qty =:remainingQty  where id=:id")
	Mono<Map<String, Object>> update(int reminingQty, int id);
}
