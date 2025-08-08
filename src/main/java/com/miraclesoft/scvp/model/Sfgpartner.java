package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sfgpartner extends SortingAndPagination {
    private String sfgPartnerId;
    private String sfgPartnerName;
    private String sfgInternalIdentifier;
    private String sfgPartnerIdentifier;
    private String sfgApplicationId;
    private String sfgCountryCode;
    private String status;
    private String createdBy;
    private String createdDate;
    private String changedBy;
    private String changedDate;
}
