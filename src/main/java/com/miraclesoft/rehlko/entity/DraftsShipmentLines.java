package com.miraclesoft.rehlko.entity;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("drafts_shipment_lines") 
public class DraftsShipmentLines {

	@Id   
	 @Column("id")
	 private Integer id;
	
	 @Column("shipment_id")
	 private String shipmentId;
	
   @Column("line_item")
   private String lineItem;
   
   @Column("shipto_name")
   private String shiptoName;       
       
   @Column("buyer_number")
   private String buyerNumber;
   
   @Column("shipto_code")
   private String shiptoCode;
   
   @Column("qty")
   private Integer qty; 
   
   @Column("uom")
   private String uom; 
   
   @Column("po_line_number")
   private Integer poLineNumber; 
   
   @Column("order_number")
   private String orderNumber; 
   
   
   @Column("partner_id")
   private String partnerId; 
  
   @Column("status")
   private String status;
  
   
}