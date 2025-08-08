package com.miraclesoft.scvp.model;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class Dashboard.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Dashboard extends SortingAndPagination{
    private String toDate;
    private String fromDate;
    private String tpId;
    private List<String> parentWarehouse;
    private List<String> warehouse;
    private String docType;
    private String status;
    private String downloadType;
    private int userId;
    private String transactionType;
}
