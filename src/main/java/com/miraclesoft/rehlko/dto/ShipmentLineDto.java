package com.miraclesoft.rehlko.dto;

import lombok.Data;

@Data
public class ShipmentLineDto {
	private int id;
	private String shipmentId;
	private String orderNumber;
	private String shipmentTrackingNumber;
	private Integer qty;
	private String uom;
	private String poLineNumber;
	private String itemDescription;
	private String billOfLading; 
	private String orderNumberFromNotice; 
	private String buyerNumber;
	private String status;
	private String orderType;
	private String originalOrderQty;
	private String beforeLinesQty;
	private String afterLinesQty;
	private String partnerId;
	private int priceUnit;
	private String VendorPartnumber;
	private String balanceQuantity;
	private String orderId;
	private String remainingQty;
	private String unitPrice;
	private String transactionType;
	private String price;
}
