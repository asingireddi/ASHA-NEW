package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.InvoiceInformation;

import reactor.core.publisher.Flux;

@Repository
public interface  InvoiceRepository extends ReactiveCrudRepository<InvoiceInformation, Integer> {
	Flux<InvoiceInformation> findAll();
	
	@Query("SELECT * FROM invoice WHERE invoice_id =:invoiceId  and status =:shipmentType and transaction_Type =:transactionType")
	Flux<InvoiceInformation>  findByInvoiceId(String invoiceId,String shipmentType, String transactionType);
	
	Flux<InvoiceInformation>  findByOrderNumber(String orderNumber);
	
}
