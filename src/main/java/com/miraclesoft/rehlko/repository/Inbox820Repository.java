package com.miraclesoft.rehlko.repository;

import java.time.LocalDate;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import com.miraclesoft.rehlko.dto.StatusCountDTO;
import com.miraclesoft.rehlko.entity.Inbox820;

import reactor.core.publisher.Flux;

public interface Inbox820Repository extends ReactiveCrudRepository<Inbox820, Integer> {

	Flux<Inbox820> findByCorrelationKey1Val(String correlationKey1Val);

	@Query(value = """
			    SELECT DISTINCT i.*
			    FROM inbox820 i
			    JOIN 820Details d ON i.Correlation_Key3_Val = d.Correlation_Key3_Val
			    WHERE LOWER(TRIM(d.Original_Invoice_Number)) LIKE LOWER(CONCAT('%', :invoiceNumber, '%'))
			      AND TRIM(i.ISA_Receiver_ID) = :partnerId
			""")
	Flux<Inbox820> searchByInvoiceNumberAndPartnerId(@Param("invoiceNumber") String invoiceNumber,
			@Param("partnerId") String partnerId);

	@Query("SELECT status, COUNT(*) AS count FROM inbox820 WHERE TRIM(partner_id) = :partnerId GROUP BY status")
	Flux<StatusCountDTO> countStatusGroupedByPartnerId(String partnerId);

	@Query("""
					    SELECT * FROM inbox820 i
					    WHERE (:transactionName IS NULL OR i.transaction_name = :transactionName)
					      AND (:transactionType IS NULL OR i.transaction_type = :transactionType)
					      AND (:sapId IS NULL OR i.sap_id = :sapId)
					      AND (:isaSenderId IS NULL OR i.isa_sender_id = :isaSenderId)
					      AND (:isaReceiverId IS NULL OR i.isa_receiver_id = :isaReceiverId)
					      AND (:partnerId IS NULL OR TRIM(i.partner_id) = TRIM(:partnerId))
					      AND (:correlationKey1Val IS NULL OR i.correlation_key1_val = :correlationKey1Val)
					      AND (:correlationKey2Val IS NULL OR i.correlation_key2_val = :correlationKey2Val)
					      AND (:correlationKey3Val IS NULL OR i.correlation_key3_val = :correlationKey3Val)
					      AND (:correlationKey4Val IS NULL OR i.correlation_key4_val = :correlationKey4Val)
					      AND (:status IS NULL OR i.status = :status)
					      AND (
					        (:startDate IS NULL OR i.correlation_key4_val >= :startDate)
					        AND (:endDate IS NULL OR i.correlation_key4_val <= :endDate)
					      )
			ORDER BY i.correlation_key4_val DESC
					""")

	Flux<Inbox820> findFilteredInbox820(@Param("transactionName") String transactionName,
			@Param("transactionType") String transactionType, @Param("sapId") String sapId,
			@Param("isaSenderId") String isaSenderId, @Param("isaReceiverId") String isaReceiverId,
			@Param("partnerId") String partnerId, @Param("correlationKey1Val") String correlationKey1Val,
			@Param("correlationKey2Val") String correlationKey2Val,
			@Param("correlationKey3Val") String correlationKey3Val,
			@Param("correlationKey4Val") LocalDate correlationKey4Val, @Param("status") String status,
			@Param("startDate") LocalDate startDate, @Param("endDate") LocalDate endDate);

}
