package com.miraclesoft.rehlko.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class Inbox820FilterDTO {
	private String transactionName;
	private String transactionType;
	private String sapId;
	private String isaSenderId;
	private String isaReceiverId;
	private String partnerId;
	private String correlationKey1Val;
	private String correlationKey2Val;
	private String correlationKey3Val;
	private LocalDate correlationKey4Val;
	private String status;
	private String startDate;
	private String endDate;
}