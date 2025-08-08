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
@Table("invoice_information")
public class InvoiceRequest {

	private Integer id;
	private String invoiceId;
	private String orderNumber;
	private String invoice;
	private String invoiceType;
	private String po;
	private LocalDate poDate;
	private String billOfLanding;
	private String routingInstruction;
	private String tracking;
	private int issuerIdCode;
	private String issuerName;
	private String issuerAddress;
	private String billToIdCode;
	private String billToName;
	private String billToAddress;
	private String salesTerms;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private LocalDate shippedDate;
	private String createdBy;
	private String modifiedBy;
	private String partnerId;
	private List<InvoiceLines> invoiceLines;
	private String status;
	private float stateAndLocalTax;
	private LocalDate invoiceDate;
	private double totalInvoiceAmount;
private String transactionType;
	  private String bpdLink;
	  private int partialInvoiceQty;
	
	


}
