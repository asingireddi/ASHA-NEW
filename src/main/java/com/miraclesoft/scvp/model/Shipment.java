package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class Shipment.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Shipment {
    private Long id;
    private String shipmentDate;
    private String asnNumber;
    private String totalWeight;
    private String totalVolume;
    private String carrierStatus;
    private String bolNumber;

    private String fileId;
    private String fileType;
    private String fileOrigin;
    private String transactionType;
    private String direction;
    private String status;
    private String ackStatus;
    private String senderId;
    private String receiverId;
    private String primaryKeyType;
    private String priKeyValue;
    private String secondaryKeyType;
    private String secKeyValue;
    private String dateTimeReceived;
    private String isaControlNumber;
    private String isaDate;
    private String isaTime;
    private String gsControlNumber;
    private String stControlNumber;
    private String fileName;
    private String ackFilePath;
    private String orgFilePath;
    private String preTransFilePath;
    private String postTransFilePath;
    private String errorReportFilePath;
    private String errorMessage;
    private String reProcessStatus;

    private String senderName;
    private String receiverName;
    private String partnerName;
    private String shipmentId;
    private String poNumber;
}
