package com.miraclesoft.rehlko.entity;

import java.time.LocalDateTime;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ChangeRequest {
	private String orderId;
	private String correlationKey1;
	private String correlationKey2;
	private String correlationKey3;
	private String correlationKey4;
	private String partnerId;
	private String partnerName;
	private String orderNumber;
	private String comments;
	private LocalDateTime createdDate;
	private LocalDateTime modifiedDate;
	private String createdBy;
	private String modifiedBy;
	private String status;
	private String divisionId;
	private String divisionName;
	private double orderAmount;
	private String faFileLocation;
	private String faFileName;
	private int orderQty;
	private String plantCode;
	private String supplierCode;
	private String supplierName;
	private String salesTerms;
	private String billtoCode;
	private String billtoName;
	private String shiptoName;
	private String shiptoCode;
	private String orderStatus;
	private String receiptNumber;
	private String bpdLink;
	private String changeRequestId;
    private String totalLineItems;
private String transactionName;
private String transactionType;
private String emailId;
	private List<ChangeRequestLines> changeRequestLines;

}
