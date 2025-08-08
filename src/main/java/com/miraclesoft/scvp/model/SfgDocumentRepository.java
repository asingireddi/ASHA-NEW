package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class SfgDocumentRepository.
 *
 * @author rpidugu
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SfgDocumentRepository {
    private Long id;
    private String direction;
    private Long fileId;
    private String dateTimeReceived;
    private String producer;
    private String consumer;
    private String filename;
    private String fileStatus;
    private String reprocessStatus;
    private String sfgPath;
    private String mailboxName;
    private String bucketName;
    private String secValKey;
}
