package com.miraclesoft.rehlko.dto;

import lombok.Data;

@Data
public class InvoiceDTO {
	private String invoiceNumber;
	private String buyers_CreditMemo;
	private String invoiceAmount;
	private String discount;
	private String paymentAmount;
	private String adjustmentAmount;
	private String invoiceDate;
}