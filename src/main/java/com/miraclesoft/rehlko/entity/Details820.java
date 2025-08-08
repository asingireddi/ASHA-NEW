package com.miraclesoft.rehlko.entity;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("820details")
public class Details820 {

	@Id
	private Integer id;
	private String transactionName;
	private String transactionType;
	private String sapId;
	private String isaSenderId;
	private String isaReceiverId;
	private String isaDateTime;
	private String isaControlNumber;
	private String correlationKey1Val;
	private String correlationKey1Name;
	private String correlationKey2Val;
	private String correlationKey2Name;
	private String correlationKey3Val;
	private String correlationKey3Name;
	private String correlationKey4Val;
	private String correlationKey4Name;
	private String originalInvoiceNumber;
	private String buyerCreditMemo;
	private String paymentAmount;
	private String invoiceAmount;
	private String discountAmountTaken;
	private String invoicePaymentDate;
	private String adjustmentAmount;
}