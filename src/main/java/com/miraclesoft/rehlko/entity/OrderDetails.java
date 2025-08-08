package com.miraclesoft.rehlko.entity;

import java.time.LocalDate;

import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Table("order_details")
public class OrderDetails {

@Id
@Column("id")
private Integer id;

@Column("correlation_key1")
private String correlationKey1;

@Column("correlation_key2")
private String correlationKey2;

@Column("correlation_key3")
private String correlationKey3;

@Column("correlation_key4")
private String correlationKey4;

@Column("partner_id")
private String partnerId;

@Column("partner_name")
private String partnerName;

@Column("ship_to_name")
private String shiptoName;

@Column("ship_to_code")
private String shiptoCode;

@Column("po_line_number")
private String poLineNumber;

@Column("buyer_number")
private String buyerNumber;

@Column("uom")
private String uom;

@Column("order_number")
private String orderNumber;

@Column("line_item")
private String lineItem;

@Column("qty")
private int qty;

@Column("sales_terms")
private String salesTerms;

@Column("unit_price")
private String unitPrice;

@Column("order_type")
private String orderType;

@Column("plant_code")
private String plantCode;

@Column("supplier_code")
private String supplierCode;

@Column("supplier_name")
private String supplierName;

@Column("vendor_partnumber")
private String vendorPartnumber;

@Column("item_description")
private String itemDescription;

@Column("tax_message")
private String taxMessage;


@Column("text_comments")
private String textComments;

@Column("ship_date")
private LocalDate shipDate;


@Column("price_unit")
private int  priceUnit;

@Column("original_order_qty")
private int  originalOrderQty;

@Column("remaining_qty")
private int remainingQty;

@Column("order_id")
private int orderId;


@Column("remaining_invoice_qty")
private int remainingInvoiceQty;

@Column("status")
private String status;


@Column("qty_Flag")
private int qtyFlag;


@Column("price_Flag")
private int priceFlag;


@Column("dd_Flag")
private int ddFlag;

@Column("revision")
private String revision;

@Column("total_netvalue")
private float totalNetValue;

@Column("tax_code")
private String taxCode;

@Column("zz_comments")
private String zzComments;

@Column("currency")
private String currency;

@Column("changeDate")
private LocalDate changeDate;

@Column("IL_TEXT")
private String ilText;

@Column("CO_TEXT")
private String coText;

@Column("DG_TEXT")
private String DGText;

@Column("HF_TEXT")
private String hfText;

@Column("KK_TEXT")
private String kkText;

@Column("K6_TEXT")
private String k6Text;

@Column("taxDesc")
private String taxDesc;


}