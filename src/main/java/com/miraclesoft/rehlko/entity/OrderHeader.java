package com.miraclesoft.rehlko.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

@Table("order_header")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderHeader {

    @Id
    private Integer id;

    @Column("correlation_key1")
    private String correlationKey1;

    @Column("correlation_key2")
    private String correlationKey2;

    @Column("correlation_key_name1")
    private String correlationKeyName1;

    @Column("correlation_key_name2")
    private String correlationKeyName2;

    @Column("partner_id")
    private String partnerId;

    @Column("partner_name")
    private String partnerName;

    @Column("order_number")
    private String orderNumber;

    @Column("ship_to_address")
    private String shipToAddress;

    @Column("order_type")
    private String orderType;

    @Column("supplier_address")
    private String supplierAddress;

    @Column("bill_to_address")
    private String billToAddress;

    @Column("LI_TEXT")
    private String liText;

    @Column("KK_TEXT")
    private String kkText;

    @Column("K6_TEXT")
    private String k6Text;
}