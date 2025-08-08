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
@Table("order_ack_lines")
public class ChangeRequestLines {

	@Id
	@Column("id")
	private Integer id;

	@Column("change_request_id")
	private String orderId;

	@Column("po_line_number")
	private String poLineNumber;

	@Column("buyer_number")
	private String buyerNumber;

	@Column("uom")
	private String uom;

	@Column("order_number")
	private String orderNumber;

	@Column("qty")
	private int qty;


	@Column("unit_price")
	private String unitPrice;
	
	
	@Column("status")
	 private String status;
	
	 @Column("order_status")
	 private String orderStatus;
	 
	 @Column("comments")
	 private String comments;
	 
	 @Column("item_description")
	 private String itemDescription;
	 
	 @Column("vendor_number")
	 private String vendorPartnumber;
	 
	 @Column("ship_date")
	 private String shipDate;
	 
	 @Column("po_line_item_status")
	 private String poLineItemStatus;
	 
	 @Column("price_unit")
	 private int  priceUnit;
	 
	    
	    @Column("original_order_qty")
	    private int  originalOrderQty;
	    
	    @Column("transaction_type")
	    private String  transactionType;
	    
	    @Column("transaction_name")
	    private String  transactionName;
	    
	   
	    
}
