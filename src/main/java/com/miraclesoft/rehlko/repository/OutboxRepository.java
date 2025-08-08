package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.miraclesoft.rehlko.dto.StatusCountDTO;
import com.miraclesoft.rehlko.entity.Outbox;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository

public interface OutboxRepository extends ReactiveCrudRepository<Outbox, Integer> {
	Flux<Outbox> findAll();

	@Query("SELECT *FROM outbox WHERE partner_id =:partnerId")
	Flux<Outbox> findByPartnerId(String partnerId);

//	@Modifying
//	@Transactional
//	@Query("UPDATE outbox i SET i.status = ? WHERE i.correlation_key1 = ? and i.id = ?")
//	Mono<Integer> updateStatusById(String status, String correlationKey,int id);

	@Modifying
	@Transactional
	@Query("UPDATE outbox i SET i.status = ? WHERE i.correlation_key1 = ? AND i.id = ? AND i.transaction_type = ?")
	Mono<Integer> updateStatusById(String status, String correlationKey, int id, String transactionType);

	@Query("SELECT *FROM outbox WHERE status =:status")
	Flux<Outbox> getOutboxByStatus(String status);

	@Modifying
	@Transactional
	@Query("UPDATE outbox i SET i.status = ? WHERE i.correlation_key1 = ? AND i.transaction_type = ?")
	Mono<Integer> updateStatusById(String status, String correlationKey, String transactionType);

	@Query("SELECT * FROM outbox o " + "WHERE (:transactionType IS NULL OR o.transaction_type = :transactionType) "
			+ "AND (:startDate IS NULL OR o.recieved_date >= :startDate) "
			+ "AND (:endDate IS NULL OR o.recieved_date <= :endDate) "
			+ "AND (:partnerId IS NULL OR o.partner_id = :partnerId) "
			+ "AND (:correlationKey1 IS NULL OR o.correlation_key1 = :correlationKey1)"
			+ "AND (:status IS NULL OR o.status = :status) " + "AND (:trashFlag IS NULL OR o.trash_flag = :trashFlag)"
			+ "AND (:archiveFlag IS NULL OR o.archive_flag = :archiveFlag)" + " ORDER BY o.recieved_date DESC")
	Flux<Outbox> findByFilter(@Param("transactionType") String transactionType, @Param("startDate") String startDate,
			@Param("endDate") String endDate, @Param("partnerId") String partnerId,
			@Param("correlationKey1") String correlationKey1, @Param("status") String status,
			@Param("trashFlag") Boolean trashFlag, @Param("archiveFlag") Boolean archiveFlag);

	@Query("SELECT shipment_id, id FROM outbox WHERE status='Drafted' AND correlation_key1 = :correlationKey AND transaction_type =:transactionType")
	Flux<Outbox> findOutboxByCorrelationKey(String correlationKey, String transactionType);

	@Query("SELECT invoice_id, id FROM outbox WHERE status='Drafted' AND correlation_key1 = :correlationKey AND transaction_type =:transactionType")
	Flux<Outbox> getOutboxInvoiceByCorrelationKey(String correlationKey, String transactionType);

	@Query("SELECT COUNT(*) FROM outbox WHERE transaction_type = '856' AND correlation_key1 = :correlationKey")
	Mono<Long> count856ByCorrelationKey(@Param("correlationKey") String correlationKey);

	@Query("SELECT partner_id,shipment_id,invoice_id,change_request_id FROM outbox WHERE file_name = '' AND transaction_type = :transactionType "
			+ "AND (order_status NOT IN ('Change Request', 'Rejected') OR order_status IS NULL) LIMIT :limit ")
	Flux<Outbox> getAllNonSentTransactions(@Param("transactionType") String transactionType, @Param("limit") int limit);

	@Query("SELECT count(*) FROM outbox WHERE file_name = '' " + "AND transaction_type = :transactionType "
			+ "AND partner_id=:partnerId  and change_request_id=:changeRequestId "
			+ "AND (order_status NOT IN ('Change Request', 'Rejected') OR order_status IS NULL)")
	Mono<Integer> getAllNonSentTransactions855(@Param("transactionType") String transactionType,
			@Param("changeRequestId") String changeRequestId, @Param("partnerId") String partnerId);

	@Query("SELECT count(*) FROM outbox WHERE file_name = '' " + "AND transaction_type = :transactionType "
			+ "AND partner_id=:partnerId  and shipment_id=:shipmentId "
			+ "AND (order_status NOT IN ('Change Request', 'Rejected') OR order_status IS NULL)")
	Mono<Integer> getAllNonSentTransactions856(@Param("transactionType") String transactionType,
			@Param("shipmentId") String shipmentId, @Param("partnerId") String partnerId);

	@Query("SELECT count(*) FROM outbox WHERE file_name = '' " + "AND transaction_type =:transactionType "
			+ "AND partner_id=:partnerId  and invoice_id=:invoiceId "
			+ "AND (order_status NOT IN ('Change Request', 'Rejected') OR order_status IS NULL) ")
	Mono<Integer> getAllNonSentTransactions810(@Param("transactionType") String transactionType,
			@Param("invoiceId") String invoiceId, @Param("partnerId") String partnerId);

	@Modifying
	@Query("UPDATE outbox SET archive_flag = :flagValue WHERE correlation_key1 = :key")
	Mono<Integer> updateArchiveFlag(String key, boolean flagValue);

	@Modifying
	@Query("UPDATE outbox SET trash_flag = :flagValue WHERE correlation_key1 = :key")
	Mono<Integer> updateTrashFlag(String key, boolean flagValue);
	
	@Query("SELECT status AS status, COUNT(*) AS count FROM outbox WHERE partner_id = :partnerId GROUP BY status")
	Flux<StatusCountDTO> countStatusByPartnerIdOutbox(String partnerId);
}
