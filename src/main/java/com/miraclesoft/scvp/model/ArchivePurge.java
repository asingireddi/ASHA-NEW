package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class ArchivePurge.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class ArchivePurge {
    private Integer id;
    private String transaction;
    private Integer archiveDays;
    private Integer purgeDays;
    private String createdBy;
    private String createdDate;
    private String modifiedBy;
    private String modifiedDate;
}
