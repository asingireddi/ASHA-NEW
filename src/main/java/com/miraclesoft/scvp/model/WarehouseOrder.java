package com.miraclesoft.scvp.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class WarehouseOrder.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class WarehouseOrder {
    private int id;
    private String fileId;
    private String fileType;
    private String fileOrigin;
    private String transactionType;
    private String direction;
    private String status;
    private String ackStatus;
    private String warehouse;
    private String parentWarehouse;
    private String senderId;
    private String receiverId;
    private String primaryKeyType;
    private String primaryKeyValue;
    private String secondaryKeyType;
    private String secondaryKeyValue;
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

    private String depositorOrderNumber;
    private String warehouseReceiptNumber;
    private String warehouseAdjustmentNumber;
    private String customerAdjustmentNumber;

    private String senderName;
    private String receiverName;
    private String partnerName;
    private String shipmentNumber;
    private String errFileId;
    private int totalRecords;
    private String postTransFileName;
}
