package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class UserInfoBean.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class UserInfoBean {
    private Long userId;
    private String loginId;
    private String password;
    private String firstName;
    private String lastName;
    private String mailId;
    private int deptartmentId;
    private String activeFlag;
    private Long id;
    private String email;
    private String active;
    private boolean fileVisibility;
    private boolean addPartnersAccess;
    private String partnerId;
    private String partnerName;
   private String webForms;
   private String mscvp;
   private String tpm;
   private String otp;
   private String otpGeneratedTime;
   private String userType;
}
