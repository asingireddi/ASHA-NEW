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
@Table("users") 
public class Users {

	@Id
    @Column("id")
    private int id;
	
    @Column("partner_id")
    private String partnerId;
    
    @Column("partner_name")
    private String parterName;

    @Column("first_name")
    private String firstName;

    @Column("last_name")
    private String lastName;
    
    @Column("user_status")
    private String userStatus;
    
    @Column("password")
    private String password;
    
    @Column("email_id")
    private String emailId;
    
    @Column("created_at")
    private LocalDateTime createdAt;
    
    @Column("updated_at")
    private LocalDateTime updatedAt;
       
    @Column("created_by")
    private String createdBy;
    
    
}
