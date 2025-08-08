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
@Table("shipment_notice")
public class ShipmentNotice {

	@Id
	@Column("id")
	private Integer id;

	@Column("shipment_id")
	private String shipmentId;

	@Column("order_number")
	private String orderNumber;

	@Column("transaction_purpose")
	private String transactionPurpose;

	@Column("asn_number")
	private String asnNmber;

	@Column("shipment_date")
	private LocalDate shipmentDate;

	@Column("shipment_time")
	private LocalTime shipmentTime;

	@Column("shipped_date")
	private LocalDate shippedDate;

	@Column("estimated_delivery_date")
	private LocalDate estimatedDeliveryDate;

	@Column("supplier_name")
	private String supplierName;

	@Column("supplier_number")
	private Integer supplierNumber;

	@Column("shipto_name")
	private String shiptoName;

	@Column("shipto_plant_code")
	private String shiptoPlantCode;

	@Column("bill_of_landing")
	private String billOfLanding;

	@Column("shipment_tracking_number")
	private String shipmentTrackingNumber;

	@Column("means_of_transport")
	private String meansOfTransport;

	@Column("po_number")
	private String poNumber;

	@Column("po_date")
	private LocalDate poDate;
	
	 @Column("created_date")
	 private LocalDateTime createdDate;
	 
	 @Column("modified_date")
	 private LocalDateTime modifiedDate;
	 
	 @Column("created_by")
	private String createdBy;
	 
	 @Column("modified_by")
	 private String modifiedBy;	 
	 
	 @Column("partner_id")
	   private String partnerId; 
	 
	 @Column("status")
	   private String status; 
	 
	 @Column("total_order_qty")
	 private int totalOrderQty;
	 
	   @Column("price_unit")
	    private int  priceUnit;
	   
	 @Column("partial_ship_flag")
	 private String partialShipFlag;
	 
	
	 
	 @Column("transaction_type")
	   private String transactionType;
	
}
