package com.miraclesoft.scvp.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * The Class SearchCriteria.
 *
 * @author Narendar Geesidi
 */
@Builder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class SearchCriteria extends SortingAndPagination {
	private int id;
	private String database;
	private String flag;
	private String fromDate;
	private String toDate;
	private String transactionType;
	private String status;
	private String senderId;
	private String receiverId;
	private String ackStatus;
	private List<String> ackStatus1;
	private List<String> warehouse;
	private List<String> parentWarehouse;
	private String corrAttribute;
	private String corrValue;
	private String corrAttribute1;
	private String corrValue1;
	private String corrAttribute2;
	private String corrValue2;
	private String documentNumber;
	private int instanceId;
	private int transType;
	private String fileName;
	private String direction;
	private String preTranslationPath;
	private String documentFormat;
	private String receiveId;
	private String mailBoxPath;
	private String secKeyVal;
	private String bucketFilename;
	private int userId;
	private boolean isAllPartners;
	private String errFileId;
	private List<String> partnerName;
	private String parentFileId;
	private String count;
	private List<String> docType;
	private String partnerNames;
	private String deliveredTo;
	private String partnerId;
	private String sapId;

}
