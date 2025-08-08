package com.miraclesoft.scvp.model;

import java.util.List;

import lombok.Data;

@Data
public class TradingPartner {
	private int id;
	private String tcDirection;
	private List<String> selectedColumns;
	private int pageSize;
	private int pageNumber;
	private String tcCompanyCode;
	private String tcDivision;
	private String tcPurchOrg;
	private String tcSapIdQualif;
	private String tcSapId;
	private String tcIdocType;
	private String tcMessageCode;
	private String tcMessagePort;
	private String tcMessageFunction;
	private String tcMessageType;
	private String tcVersion;
	private String tcKohlerIsaIdQualf;
	private String tcKohlerIsaId;
	private String tcKohlerGs;
	private String tcPartnerIsaIdQualf;
	private String tcPartnerIsaId;
	private String tcPartnerGs;
	private String tcGsFunctionalCode;
	private String tcMapName;
	private String tcPartnerName;
	private String tcListVersion;
	private String tcStream;
	private String tcDivisionName;
	private String flowType;
	private String tcTransactionCode;
	private String tcDeliveryType;
	private String active;
	private String genericRID;
	private String genericSID;
	private String testRehlkoID;
	private String sortField;
	private String sortOrder;
	private Integer limit;
	private Integer offset;

}