package com.miraclesoft.rehlko.entity;

import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("m_user")
public class MUser {

	@Id
	private Long id;

	@Column("loginid")
	private String loginId;

	@Column("passwd")
	private String password;

	@Column("fnme")
	private String firstName;

	@Column("lnme")
	private String lastName;

	@Column("email")
	private String email;

	@Column("office_phone")
	private String officePhone;

	@Column("dept_id")
	private int deptId;

	@Column("active")
	private String active;

	@Column("location")
	private String location;

	@Column("organization")
	private String organization;

	@Column("education")
	private String education;

	@Column("designation")
	private String designation;

	@Column("created_by")
	private String createdBy;

	@Column("created_ts")
	private LocalDateTime createdTs;

	@Column("authorized_by")
	private String authorizedBy;

	@Column("authorized_ts")
	private LocalDateTime authorizedTs;

	@Column("modified_by")
	private String modifiedBy;

	@Column("modified_ts")
	private LocalDateTime modifiedTs;

	@Column("deactivated_by")
	private String deactivatedBy;

	@Column("deactivated_ts")
	private LocalDateTime deactivatedTs;

	@Column("file_visibility")
	private int fileVisibility;

	@Column("timezone")
	private String timezone;

	@Column("secondary_email")
	private String secondaryEmail;

	@Column("webforms")
	private String webforms;

	@Column("tpm")
	private String tpm;

	@Column("mscvp")
	private String mscvp;

	@Column("partner_name")
	private String partnerName;

	@Column("partner_id")
	private String partnerId;

	@Column("user_type")
	private String userType;

	@Column("otp_generated_time")
	private LocalDateTime otpGeneratedTime;

	@Column("otp")
	private String otp;

	@Column("buyer_contacts")
	private String buyerContacts;

}
