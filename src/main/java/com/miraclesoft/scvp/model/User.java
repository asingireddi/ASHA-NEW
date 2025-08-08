package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class User.
 *
 * @author Priyanka Kolla
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User extends SortingAndPagination {
    private Long userId;
    private String loginId;
    private String password;
    private String active;
    private String status;
    private int primaryFlowId;
    private int roleId;
    private String roleName;
    private String firstName;
    private String lastName;
    private String userName;
    private String email;
    private String secondaryEmail;
    private int departmentId;
    private String organization;
    private String designation;
    private String location;
    private String phoneNumber;
    private String officePhone;
    private String education;
    private String imageUpdateFileName;
    private String createdBy;
    private String confirmPassword;
    private String newPassword;
    private String oldPassword;
    private boolean manufacturing;
    private boolean logistics;
    private String[] partnerIds;
    private String[] sfgPartnersNames;
    private boolean fileVisibility;
    private String timeZone;
    private boolean addPartnersAccess ;
    private String partnerId;
    private String partnerName;
   private String webForms;
   private String mscvp;
   private String tpm;
   private String otp;
   private String otpGeneratedTime;
   private String userType;
   private String buyerContacts;
}
