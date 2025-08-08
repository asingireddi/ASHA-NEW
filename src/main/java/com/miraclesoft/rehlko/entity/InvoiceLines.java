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
@Table("invoice_line_items") 
public class InvoiceLines {

	@Id   
	 @Column("id")
	 private Integer id;
	
	 @Column("invoice_id")
	 private String invoiceId;
	
    @Column("item")
    private String item;
    
    @Column("kohler_product")
    private String kohlerProduct;       
        
    @Column("vendor_product")
    private String vendorPartnumber;
    
    @Column("upc")
    private String upc;
    
    @Column("qty_invoiced")
    private Integer qtyInvoiced; 
    
    @Column("uom")
    private String uom;   
    
    @Column("unit_price")
    private double unitPrice; 
    
    @Column("line_total")
    private double lineTotal; 
    
       
    @Column("order_number")
    private String orderNumber; 
    
    @Column("partner_id")
	private String partnerId;
    
    @Column("status")
	private String status;
    
    @Column("item_description")
   	private String itemDescription;
  
    @Column("price_unit")
    private int  priceUnit;
    
    @Column("order_id")
    private int orderId;

}
