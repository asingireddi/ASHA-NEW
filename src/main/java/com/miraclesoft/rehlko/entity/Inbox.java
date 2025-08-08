package com.miraclesoft.rehlko.entity;

import java.sql.Timestamp;
import java.time.LocalDateTime;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("inbox")
public class Inbox {

	@Id
	@Column("id")
	private Integer id;

	@Column("partner_name")
	private String partnerName;

	@Column("partner_id")
	private String partnerId;

	@Column("box_type")
	private String boxType;

	@Column("transaction_type")
	private String transactionType;

	@Column("transaction_name")
	private String transactionName;

	@Column("file_location")
	private String fileLocation;

	@Column("file_name")
	private String fileName;

	@Column("file_interchange_control_number")
	private String fileInterchangeControlNumber;

	@Column("file_interchange_date_time")
	private LocalDateTime fileInterchangeDateTime;

	@Column("correlation_key1")
	private String correlationKey1;

	@Column("correlation_key2")
	private String correlationKey2;

	@Column("correlation_key3")
	private String correlationKey3;

	@Column("correlation_key4")
	private String correlationKey4;

	@Column("correlation_key_name1")
	private String correlation_key_name1;

	@Column("correlation_key_name2")
	private String correlation_key_name2;

	@Column("correlation_key_name3")
	private String correlation_key_name3;

	@Column("correlation_key_name4")
	private String correlation_key_name4;

	@Column("status")
	private String status;

	@Column("recieved_date")
	private LocalDateTime recievedDate;

	@Column("fak_status")
	private String fakStatus;

	@Column("division_id")
	private String divisionId;

	@Column("division_name")
	private String divisionName;

	@Column("order_amount")
	private double orderAmount;

	@Column("fa_file_location")
	private String faFileLocation;

	@Column("fa_file_name")
	private String faFileName;

	@Column("order_qty")
	private int orderQty;

	@Column("order_status")
	private String orderStatus;

	@Column("supplier_name")
	private String supplierName;

	@Column("supplier_code")
	private String supplierCode;

	@Column("shiptocode")
	private String shiptoCode;

	@Column("shipto_name")
	private String shiptoName;

	@Column("plant_code")
	private String plantCode;

	@Column("billto_code")
	private String billtoCode;

	@Column("billto_name")
	private String billtoName;

	@Column("sales_terms")
	private String salesTerms;

	@Column("totalline_items")
	private String totalLineItems;

	@Column("order_disable")
	private String orderDisable;

	@Column("order_change_flag")
	private int orderChangeFlag;

	@Column("acknowledge_date")
	private Timestamp acknowledgeDate;
	
	private String bpdLink;
	private String fileContent;

	void setData(String data) {
		this.fileContent = data;
	}

}
