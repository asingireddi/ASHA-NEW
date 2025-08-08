package com.miraclesoft.scvp.util;

import java.util.HashMap;
import java.util.Map;

public class ColumnsMapping {

	public static String SqlColumnsMapping(String sortField) {
		final Map<String, String> Columns = new HashMap<>();
		Columns.put("dateTimeReceived", "date_time_received");
		Columns.put("fileId", "file_id");
		Columns.put("primaryKeyValue", "pri_key_val");
		Columns.put("parentWarehouse", "warehouse");
		Columns.put("warehouse", "parent_warehouse");
		Columns.put("transactionType", "transaction_type");
		Columns.put("isaControlNumber", "isa_number");
		Columns.put("direction", "direction");
		Columns.put("fileType", "file_type");
		Columns.put("ackStatus", "ack_status");
		Columns.put("reProcessStatus", "reprocessstatus");
		Columns.put("depositorOrderNumber", "sec_key_val");
		Columns.put("producer", "producer");
		Columns.put("consumer", "consumer");
		Columns.put("filename", "filename");
		Columns.put("fileStatus", "File_Status");
		Columns.put("partner_Name", "name");
		Columns.put("partnerIdentifier", "id");
		Columns.put("countryCode", "state");
		Columns.put("createdDate", "created_ts");
		Columns.put("sfgPartnerName", "name");
		Columns.put("sfgCountryCode", "state");
		Columns.put("createdDate", "created_ts");
		Columns.put("status", "status");
		Columns.put("loginId", "loginid");
		Columns.put("userName", "concat(fnme,' ',lnme)");
		Columns.put("email", "email");
		Columns.put("officePhone", "office_phone");
		Columns.put("active", "active");
		
		Columns.put("ID", "ID");
		Columns.put("TC_DIRECTION", "TC_DIRECTION");
		Columns.put("TC_COMPANY_CODE", "TC_COMPANY_CODE");
		Columns.put("TC_DIVISION", "TC_DIVISION");
		Columns.put("TC_PURCH_ORG", "TC_PURCH_ORG");
		Columns.put("TC_SAP_ID_QUALIF", "TC_SAP_ID_QUALIF");
		Columns.put("TC_SAP_ID", "TC_SAP_ID");
		Columns.put("TC_IDOC_TYPE", "TC_IDOC_TYPE");
		Columns.put("TC_MESSAGE_CODE", "TC_MESSAGE_CODE");
		Columns.put("TC_MESSAGE_PORT", "TC_MESSAGE_PORT");
		Columns.put("TC_MESSAGE_FUNCTION","TC_MESSAGE_FUNCTION");
		Columns.put("TC_MESSAGE_TYPE", "TC_MESSAGE_TYPE");
		Columns.put("TC_VERSION", "TC_VERSION");
		Columns.put("TC_REHLKO_ISA_ID_QUALF", "TC_REHLKO_ISA_ID_QUALF");
		Columns.put("TC_REHLKO_ISA_ID", "TC_REHLKO_ISA_ID");
		Columns.put("TC_REHLKO_GS", "TC_REHLKO_GS");
		Columns.put("TC_PARTNER_ISA_ID_QUALF", "TC_PARTNER_ISA_ID_QUALF");
		Columns.put("TC_PARTNER_ISA_ID", "TC_PARTNER_ISA_ID");
		Columns.put("TC_PARTNER_GS", "TC_PARTNER_GS");
		Columns.put("TC_GS_FunctionalCode", "TC_GS_FunctionalCode");
		Columns.put("TC_MapName", "TC_MapName");
		Columns.put("TC_PartnerName", "TC_PartnerName");
		Columns.put("TC_ListVersion", "TC_ListVersion");
		Columns.put("TC_Stream", "TC_Stream");
		Columns.put("TC_DivisionName", "TC_DivisionName");
		Columns.put("FlowType", "FlowType");
		Columns.put("TC_TransactionCode", "TC_TransactionCode");
		Columns.put("TC_DeliveryType", "TC_DeliveryType");
		Columns.put("Active", "Active");
		Columns.put("GenericRID", "GenericRID");
		Columns.put("GenericSID", "GenericSID");
		Columns.put("Test_REHLKO_ID", "Test_REHLKO_ID");
		String result = Columns.get(sortField);
		return result;
	}	
}
