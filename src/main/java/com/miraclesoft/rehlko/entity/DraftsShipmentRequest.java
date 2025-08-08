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

public class DraftsShipmentRequest {

	private Integer id;	
	private String shipmentId;	
	private String orderNumber;
	private String transactionPurpose;	
	private String asnNmber;	
	private LocalDate shipmentDate;	
	private LocalTime shipmentTime;	
	private LocalDate shippedDate;	
	private LocalDate estimatedDeliveryDate;	
	private String supplierName;	
	private Integer supplierNumber;	
	private String shiptoName;	
	private String shiptoPlantCode;	
	private String billOfLanding;	
	private String shipmentTrackingNumber;
	private String meansOfTransport;	
	private String poNumber;	
	private LocalDate poDate;	
	private LocalDateTime createdDate;	
    private LocalDateTime modifiedDate;	
	private String createdBy;	
	 private String modifiedBy;	 
	 private List<ShipmentLines> shipmentLines;
	   private String partnerId; 
	   private String status; 
}
