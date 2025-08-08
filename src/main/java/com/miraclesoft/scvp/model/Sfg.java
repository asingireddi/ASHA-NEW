package com.miraclesoft.scvp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class Sfg.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Sfg extends SortingAndPagination {
    private String fromDate;
    private String toDate;
    private String direction;
    private String fileStatus;
    private List<String> producer;
    private List<String> consumer;
    private String filename;
    private String instanceId;
    private String liveOrArchive;
    private Long id;
}
