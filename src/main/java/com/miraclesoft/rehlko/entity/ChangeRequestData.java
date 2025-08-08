package com.miraclesoft.rehlko.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("order_ack") 
public class ChangeRequestData {

	@Id   
	 @Column("id")
	 private Integer id;
	
	 @Column("change_request_id")
	 private String orderId;
	
	 @Column("correlation_key1")
	 private String correlationKey1;
	
    @Column("correlation_key2")
    private String correlationKey2;
    
    @Column("correlation_key3")
    private String correlationKey3;  
    
    @Column("correlation_key4")
    private String correlationKey4;  
            
    @Column("partner_id")
    private String partnerId;
    
    @Column("partner_name")
    private String partnerName;    
   
    @Column("order_number")
    private String orderNumber;     
    
    @Column("comments")
    private String comments; 
    
    @Column("created_date")
	 private LocalDateTime createdDate;
	 
	 @Column("modified_date")
	 private LocalDateTime modifiedDate;
	 
	 @Column("created_by")
	private String createdBy;
	 
	 @Column("modified_by")
	 private String modifiedBy;	
	 
	 @Column("status")
	 private String status;	
	 
	 @Column("division_id")
	    private String divisionId;
	    
	    @Column("division_name")
	    private String divisionName;
	    
	    @Column("order_amount")
	    private double orderAmount;
	    
	    @Column("fa_file_location")
	    private String faFileLocation;
	    
	    @Column("fa_file_name")
	    private String faFileName;
	    
	    @Column("order_qty")
	    private int orderQty;
	    
	    @Column("plant_code")
	    private String plantCode;

	    @Column("supplier_code")
	    private String supplierCode;

	    @Column("supplier_name")
	    private String supplierName;
	 
	    @Column("order_status")
	    private String orderStatus;
	    
	    @Column("sales_terms")
	    private String salesTerms;
	    
	    @Column("billto_code")
	    private String billtoCode;
	    
	    @Column("billto_name")
	    private String billtoName;
	  
	    @Column("shipto_name")
	    private String shiptoName;
	  
	    @Column("shipto_code")
	    private String shiptoCode;
		 		 
	    @Column("receipt_number")
	    private String receiptNumber;
		
	    @Column("totalline_items")
	    private String totalLineItems;
		
}