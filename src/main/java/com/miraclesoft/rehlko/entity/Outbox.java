package com.miraclesoft.rehlko.entity;

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
@Table("outbox")
public class Outbox {
    @Id
    @Column("id")
    private Integer id;

    @Column("partner_name")
    private String partnerName;

    @Column("order_id")
    private Integer orderId;

    @Column("change_request_id")
    private String changeRequestId;

    @Column("partner_id")
    private String partnerId;

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

    @Column("shipment_id")
    private String shipmentId;

    @Column("invoice_id")
    private String invoiceId;

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

    @Column("comments")
    private String comments;

    @Column("reciept_number")
    private String recieptNumber;

    @Column("division_id")
    private String divisionId;

    @Column("division_name")
    private String divisionName;

    @Column("group_control_number")
    private String groupControlNumber;

    @Column("fa_status")
    private String fakStatus;

    @Column("functional_group_code")
    private String functionaGroupCode;

    @Column("totalline_items")
    private String totalLineItems;

    @Column("order_amount")
    private double orderAmount;

    @Column("order_qty")
    private int orderQty;

    @Column("partial_ship_flag")
    private String partialShipFlag;

    @Column("order_type")
    private String orderType;

    @Column("order_disable")
    private String orderDisable;

    private String fileContent;

    @Column("trash_flag")
    private boolean trashFlag;

    @Column("archive_flag")
    private boolean archiveFlag;

    void setData(String data) {
        this.fileContent = data;
    }

}
