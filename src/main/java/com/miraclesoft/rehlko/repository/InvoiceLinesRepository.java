package com.miraclesoft.rehlko.repository;

import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.stereotype.Repository;

import com.miraclesoft.rehlko.entity.InvoiceLines;
import com.miraclesoft.rehlko.entity.ShipmentLines;

import reactor.core.publisher.Flux;

@Repository
public interface  InvoiceLinesRepository extends ReactiveCrudRepository<InvoiceLines, Integer> {
	Flux<InvoiceLines> findAll();
	
	@Query("SELECT * FROM invoice_line_items WHERE invoice_id =:invoiceId  and status=:invoiceType")
	Flux<InvoiceLines>  findByInvoiceId(String invoiceId,String invoiceType);
}
