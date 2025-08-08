package com.miraclesoft.rehlko.repository;

import java.util.List;

import org.springframework.data.r2dbc.repository.Modifying;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import com.miraclesoft.rehlko.dto.PartnerDTO;
import com.miraclesoft.rehlko.dto.StatusCountDTO;
import com.miraclesoft.rehlko.entity.Inbox;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Repository

public interface InboxRepository extends ReactiveCrudRepository<Inbox, Integer> {

	Flux<Inbox> findAll();

//	@Modifying
//    @Transactional
//    @Query("UPDATE inbox i SET i.status = ? WHERE i.correlation_key1 = ?")
//	 Mono<Integer> updateStatusById(String status,String correlationKey1);

	@Query("SELECT * FROM inbox WHERE partner_id =:partnerId")
	Flux<Inbox> getByPartnerId(String partnerId);

	@Modifying
	@Transactional
	@Query("UPDATE inbox i SET i.fak_status = ? WHERE i.correlation_key1 = ?")
	Mono<Integer> updateFakStatusById(String status, String id);

	@Query("SELECT *FROM inbox WHERE status =:status")
	Flux<Inbox> getInboxDataByStatus(String status);

	@Query("""
			    SELECT DISTINCT TRIM(partner_id) AS partner_id, TRIM(partner_name) AS partner_name FROM inbox
			    UNION
			    SELECT DISTINCT TRIM(partner_id) AS partner_id, TRIM(partner_name) AS partner_name FROM inbox820
			""")
	Flux<PartnerDTO> getAllPartners();

	@Modifying
	@Transactional
	@Query("UPDATE inbox i SET i.status = ? WHERE i.correlation_key1 = ? AND i.id =? AND i.transaction_type = ?")
	Mono<Integer> updateStatusById(String status, String correlationKey, int id, String transactionType);

	@Modifying
	@Transactional
	@Query("UPDATE inbox i SET i.status = ?,i.acknowledge_date=? WHERE i.correlation_key1 = ? AND i.id =? AND i.transaction_type = ?")
	Mono<Integer> updateStatusByIdtime(String status, String currentTime, String correlationKey, int id,
			String transactionType);

//	@Query("SELECT * FROM inbox i where i.transaction_type = ? and  i.recieved_date >= ? and i.recieved_date <= ? and  i.correlation_key1 = ?" )
//	
//	Flux<Inbox> findByFilter(String transactionType,String startDate,String endDate, String correlationKey1);

	@Query("SELECT * FROM inbox i " + "WHERE (:transactionType IS NULL OR i.transaction_type = :transactionType) "
			+ "AND (:startDate IS NULL OR i.recieved_date >= :startDate) "
			+ "AND (:endDate IS NULL OR i.recieved_date <= :endDate) "
			+ "AND (:partnerId IS NULL OR i.partner_id = :partnerId) "
			+ "AND (:correlationKey1 IS NULL OR i.correlation_key1 = :correlationKey1) "
			+ "AND (:status IS NULL OR i.status = :status) " + "AND (:trashFlag IS NULL OR i.trash_flag = :trashFlag)"
			+ "AND (:archiveFlag IS NULL OR i.archive_flag = :archiveFlag)" + "ORDER BY i.recieved_date DESC")

	Flux<Inbox> findByFilter(@Param("transactionType") String transactionType, @Param("startDate") String startDate,
			@Param("endDate") String endDate, @Param("partnerId") String partnerId,
			@Param("correlationKey1") String correlationKey1, @Param("status") String status,
			@Param("trashFlag") Boolean trashFlag, @Param("archiveFlag") Boolean archiveFlag);

	@Query("UPDATE inbox i JOIN outbox o ON i.correlation_key1 = o.correlation_key1 SET i.status = o.status, "
			+ "i.order_status = o.order_status WHERE o.correlation_key1 = :correlationKey1")
	Mono<String> updateByCorrelationId(String correlationKey1);

	@Modifying
	@Query("UPDATE inbox SET archive_flag = :flagValue WHERE correlation_key1 = :key")
	Mono<Integer> updateArchiveFlag(String key, boolean flagValue);

	@Modifying
	@Query("UPDATE inbox SET trash_flag = :flagValue WHERE correlation_key1 = :key")
	Mono<Integer> updateTrashFlag(String key, boolean flagValue);

	@Query("SELECT DISTINCT status FROM inbox")
	Flux<String> findDistinctStatuses();

	@Modifying
	@Query("UPDATE inbox SET status = :status WHERE correlation_key1 IN (:correlationKeys)")
	Mono<Integer> updateStatusByCorrelationKey1(String status, List<String> correlationKeys);

	@Query("SELECT status AS status, COUNT(*) AS count FROM inbox WHERE partner_id = :partnerId GROUP BY status")
	Flux<StatusCountDTO> countStatusByPartnerIdInbox(String partnerId);
}
