package com.miraclesoft.rehlko.entity;

import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("inbox820")
public class Inbox820 {

	@Id
	private Integer id;
	private String transactionName;
	private String transactionType;
	private String sapId;
	private String isaSenderId;
	private String isaReceiverId;
	private String isaDateTime;
	private String isaControlNumber;
	private String paymentMethod;
	private String senderAccountNumber;
	private String senderAccountType;
	private String receiverAccountNumber;
	private String receiverAccountType;
	private String correlationKey1Val;
	private String correlationKey1Name;
	private String correlationKey2Val;
	private String correlationKey2Name;
	private String correlationKey3Val;
	private String correlationKey3Name;
	private LocalDate correlationKey4Val;
	private String correlationKey4Name;
	private String businessFunction;
	private String payerName;
	private String payerAccountNumber;
	private String payeeName;
	private String payeeAccountNumber;
	private String monetaryAmount;
	private String currencyCode;
	private String debitCredit;
	private String fileName;
	private String fileLocation;
	private String status;
	private String partnerName;
	private String partnerId;
	private LocalDateTime recievedDate;
}
