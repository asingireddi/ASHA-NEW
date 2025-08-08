package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class LifeCycle.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class LifeCycle {
    private String fileId;
    private String database;
    private String fileType;
    private String transactionType;
    private String direction;
    private String dateTimeReceived;
    private String isaControlNumber;
    private String stControlNumber;
    private String gsControlNumber;
    private String senderId;
    private String senderName;
    private String receiverId;
    private String receiverName;
    private String partner;
    private String status;
    private String sapIdocNumber;
    private String isaNumber;
    private String poNumber;
    private String asnNumber;
    private String invoiceNumber;
    private String res;
    private String preTransFilePath;
    private String postTransFilePath;
    private String orgFilePath;
    private String ackFilePath;
    private String poDate;
    private String poValue;
    private String poStatus;
    private String soNumber;
    private String iteamQuantity;
    private String bolNumber;
    private String isaDate;
    private String isaTime;
    private String invoiceAmount;
    private String chequeNumber;
    private String ackStatus;
    private String reProcessStatus;
    private String primaryKeyType;
    private String primaryKeyValue;
    private String secondaryKeyType;
    private String secondaryKeyValue;
    private String errorMessage;
    private String fileOrigin;
    private String filePath;
    private String depositorOrderNumber;
    private String errorReportFilePath;
}
