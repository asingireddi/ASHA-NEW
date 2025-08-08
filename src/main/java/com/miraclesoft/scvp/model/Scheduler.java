package com.miraclesoft.scvp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class Scheduler.
 *
 * @author Priyanka Kolla
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Scheduler {
    private Long id;
    private String status;
    private String title;
    private String type;
    private String receiverEmails;
    private List<String> receiverIds;
    private String externalEmails;
    private String createdBy;

}
