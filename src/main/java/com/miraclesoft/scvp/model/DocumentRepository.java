package com.miraclesoft.scvp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class DocumentRepository.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class DocumentRepository {
	private Long id;
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
	private String postTransFileName;
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
	private List<String> filePath;
	private List<String> toAddress;
	private String body;
	private String subject;
	private String errFileId;
	private int count;
	private String deliveredTo;
	private String sapId;

}