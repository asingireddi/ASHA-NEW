package com.miraclesoft.rehlko.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("invoice")
public class InvoiceInformation {

	@Id
	@Column("id")
	private Integer id;

	@Column("invoice_id")
	private String invoiceId;

	@Column("order_number")
	private String orderNumber;

	@Column("invoice")
	private String invoice;

	@Column("invoice_type")
	private String invoiceType;
	
	@Column("po")
	private String po;

	@Column("po_date")
	private LocalDate poDate;

	@Column("bill_of_lading")
	private String billOfLanding;

	@Column("routing_instruction")
	private String routingInstruction;

	@Column("tracking")
	private String tracking;
	
	@Column("issuer_id_code")
	private Integer issuerIdCode;
	
	@Column("issuer_name")
	private String issuerName;
	
	@Column("issuer_address")
	private String issuerAddress;
	
	@Column("billto_id_code")
	private String billtoIdCode;
	
	@Column("billto_name")
	private String billtoName;
	
	@Column("billto_address")
	private String billtoAddress;
	
	@Column("sales_terms")
	private String salesTerms;	
	
	 @Column("created_date")
	 private LocalDateTime createdDate;
	 
	 @Column("modified_date")
	 private LocalDateTime modifiedDate;
	
	 @Column("shipped_date")
	 private LocalDate shippedDate;
	
	 @Column("created_by")
	private String createdBy;
	 
	 @Column("modified_by")
	 private String modifiedBy;
	 
	 @Column("partner_id")
		private String partnerId;
	 
	 @Column("status")
		private String status;
	 
	 @Column("state_and_local_tax")
	    private float stateAndLocalTax;   
	 
	 @Column("invoice_date")
	 private LocalDate invoiceDate;	 
	 
	 @Column("total_invoice_amount")
	 private double totalInvoiceAmount;
	 
	 @Column("transaction_type")
	 private String transactionType;

}
