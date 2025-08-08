package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class Partner.
 *
 * @author Priyanka Kolla
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Partner extends SortingAndPagination {
    private String partnerId;
    private String partnerName;
    private String internalIdentifier;
    private String partnerIdentifier;
    private String applicationId;
    private String countryCode;
    private String status;
    private String createdBy;
    private String createdDate;
    private String changedBy;
    private String changedDate;
}
