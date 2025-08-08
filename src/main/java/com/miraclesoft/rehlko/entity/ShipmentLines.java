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
@Table("shipment_lines") 
public class ShipmentLines {

	@Id   
	 @Column("id")
	 private Integer id;
	
	 @Column("shipment_id")
	 private String shipmentId;      
        
    @Column("buyer_number")
    private String buyerNumber;
    
    @Column("qty")
    private Integer qty; 
    
    @Column("uom")
    private String uom; 
    
    @Column("po_line_number")
    private String poLineNumber; 
    
    @Column("order_number")
    private String orderNumber; 
  
    @Column("partner_id")
    private String partnerId; 
    
    @Column("status")
	   private String status;
    
    @Column("order_type")
    private String orderType;
    
    @Column("before_lines_qty")
    private int beforeLinesQty;
    
    @Column("after_lines_qty")
    private int afterLinesQty;
    
    @Column("item_description")
     private String itemDescription;
    
    @Column("vendor_product")
    private String VendorPartnumber;
    
    @Column("price_unit")
    private int  priceUnit;

    
    @Column("original_order_qty")
    private int  originalOrderQty;
    
    @Column("remaining_qty")
    private int remainingQty;
    
    @Column("order_id")
    private int orderId;

@Column("unit_price")
private String unitPrice;
    
@Column("price")
private String price;
}
