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
@Table("customers") 
public class Customers {

	@Id
    @Column("id")
    private Integer id;
	
    @Column("customer_name")
    private String customerName;
    
    @Column("customer_email")
    private String email;
    
    @Column("company_website")
    private String companyWebSite;
    
    @Column("created_on")
    private LocalDateTime createdAt;
    
    @Column("created_by")
    private String createdBy;
    
}
