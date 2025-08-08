package com.miraclesoft.rehlko.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.miraclesoft.rehlko.entity.InvoiceInformation;
import com.miraclesoft.rehlko.entity.InvoiceLines;
import com.miraclesoft.rehlko.entity.InvoiceRequest;
import com.miraclesoft.rehlko.repository.InvoiceLinesRepository;
import com.miraclesoft.rehlko.repository.InvoiceRepository;
import com.miraclesoft.rehlko.repository.OrderDetailsRepository;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@Service
public class InvoiceService {

    private final InvoiceRepository invoiceRepository;
    private final InvoiceLinesRepository invoiceLinesRepository;
    private final OrderDetailsRepository orderDetailsRepository;

    // private final DraftsShipmentRepository draftsShipmentRepository;
    // private final DraftsShipmentLinesRepository draftsShipmentLinesRepository;

    private static final Logger logger = LoggerFactory.getLogger(InvoiceService.class.getName());

    public InvoiceService(InvoiceRepository invoiceRepository, InvoiceLinesRepository invoiceLinesRepository,
                          OrderDetailsRepository orderDetailsRepository) {
        this.invoiceRepository = invoiceRepository;
        this.invoiceLinesRepository = invoiceLinesRepository;
        this.orderDetailsRepository = orderDetailsRepository;
        // this.draftsShipmentRepository = draftsShipmentRepository;
        // this.draftsShipmentLinesRepository=draftsShipmentLinesRepository;
    }

//	public Mono<Map<String, Object>> sendInvoice(InvoiceRequest invoiceRequest) {
//		logger.info("Executing the method :: sendInvoice ");
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Failed to save data into Invoice information and Lines");
//		response.put("status", false);
//		try {
//			String invoiceId = UUID.randomUUID().toString();
//			InvoiceInformation invoice = new InvoiceInformation();
//			InvoiceLines invoiceLines = new InvoiceLines();
//			List<InvoiceLines> invoiceLinesList = new ArrayList<>();
//			invoice.setInvoiceId(invoiceId);
//			invoice.setOrderNumber(invoiceRequest.getOrderNumber());
//			invoice.setCreatedDate(LocalDateTime.now());
//			invoice.setModifiedDate(LocalDateTime.now());
//			invoice.setInvoice(invoiceRequest.getInvoice());
//			invoice.setInvoiceType(invoiceRequest.getInvoiceType());
//			invoice.setPo(invoiceRequest.getPo());
//			invoice.setPoDate(invoiceRequest.getPoDate());
//			invoice.setBillOfLanding(invoiceRequest.getBillOfLanding());
//			invoice.setRoutingInstruction(invoiceRequest.getRoutingInstruction());
//			invoice.setTracking(invoiceRequest.getTracking());
//			invoice.setIssuerIdCode(invoiceRequest.getIssuerIdCode());
//			invoice.setIssuerName(invoiceRequest.getIssuerName());
//			invoice.setIssuerAddress(invoiceRequest.getIssuerAddress());
//			invoice.setBilltoIdCode(invoiceRequest.getBillToIdCode());
//			invoice.setBilltoName(invoiceRequest.getBillToName());
//			invoice.setBilltoAddress(invoiceRequest.getBillToAddress());
//			invoice.setSalesTerms(invoiceRequest.getSalesTerms());
//			invoice.setPartnerId(invoiceRequest.getPartnerId());
//			invoice.setCreatedBy(invoiceRequest.getCreatedBy());
//			invoice.setModifiedBy(invoiceRequest.getModifiedBy());
//			invoice.setStatus(invoiceRequest.getStatus());
//			invoice.setShippedDate(invoiceRequest.getShippedDate());
//			invoice.setStateAndLocalTax(invoiceRequest.getStateAndLocalTax());
//			invoice.setInvoiceDate(invoiceRequest.getInvoiceDate());
//			invoice.setTotalInvoiceAmount(invoiceRequest.getTotalInvoiceAmount());
//			invoice.setTransactionType(invoiceRequest.getTransactionType());
//			invoice = invoiceRepository.save(invoice).block();
//			for (InvoiceLines invoices : invoiceRequest.getInvoiceLines()) {
//				InvoiceLines invoiceLinesData = new InvoiceLines();
//				invoiceLinesData.setInvoiceId(invoiceId);
//				invoiceLinesData.setItem(invoices.getItem());
//				invoiceLinesData.setKohlerProduct(invoices.getKohlerProduct());
//				invoiceLinesData.setLineTotal(invoices.getLineTotal());
//				invoiceLinesData.setOrderNumber(invoices.getOrderNumber());
//				invoiceLinesData.setPartnerId(invoices.getPartnerId());
//				invoiceLinesData.setQtyInvoiced(invoices.getQtyInvoiced());
//				invoiceLinesData.setUnitPrice(invoices.getUnitPrice());
//				invoiceLinesData.setUom(invoices.getUom());
//				invoiceLinesData.setUpc(invoices.getUpc());
//				invoiceLinesData.setVendorPartnumber(invoices.getVendorPartnumber());
//				invoiceLinesData.setStatus(invoices.getStatus());
//				invoiceLinesData.setItemDescription(invoices.getItemDescription());
//				invoiceLinesData.setPriceUnit(invoices.getPriceUnit());
//				invoiceLinesData.setOrderId(invoices.getOrderId());
//				invoiceLines = invoiceLinesRepository.save(invoiceLinesData).block();
//				invoiceLinesList.add(invoiceLines);
//
//			}
//			if ("Invoice".equalsIgnoreCase(invoiceRequest.getStatus())
//					|| "Partial_Invoice".equalsIgnoreCase(invoiceRequest.getStatus())
//					|| "Submitted".equalsIgnoreCase(invoiceRequest.getStatus()))
//			{
//				for (InvoiceLines invoices : invoiceRequest.getInvoiceLines()) {
//					orderDetailsRepository.findById(invoices.getOrderId()).flatMap(orderDetails -> {
//						// System.out.println("orderDetails.getRemainingInvoiceQty()"+"orderDetails.getRemainingInvoiceQty()")
//						int newRemainingQty = Math.max(0,
//								orderDetails.getRemainingInvoiceQty() - invoices.getQtyInvoiced());
//						// Print before saving
//						System.out.println("Updating Order ID: " + orderDetails.getId()
//								+ ", Old invoice Remaining Qty: " + orderDetails.getRemainingInvoiceQty()
//								+ ", New invoice Remaining Qty: " + newRemainingQty);
//						// Now return the result of update, which is a Mono
//						return orderDetailsRepository.updatRemainingInvoiceQty(newRemainingQty, invoices.getOrderId())
//								.then(Mono.just(orderDetails)); // Make sure to return a Mono
//					}).subscribe(); // Ensure execution of the reactive stream
//				}
//			}
//			if (invoice != null) {
//				response.put("data", invoice);
//				response.put("lines", invoiceLinesList);
//				response.put("message", "Data saved into invoice Information and lines successfully");
//				response.put("status", true);
//			}
//			logger.info("Executed the method :: sendInvoice ");
//		} catch (Exception ex) {
//			logger.error(" sendInvoice :: {}", ex.getMessage());
//		}
//		return Mono.just(response);
//	}

    public Mono<Map<String, Object>> sendInvoice(InvoiceRequest invoiceRequest) {
        logger.info("Executing the method :: sendInvoice ");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Failed to save data into Invoice information and Lines");
        response.put("status", false);

        String invoiceId = UUID.randomUUID().toString();

        InvoiceInformation newInvoice = new InvoiceInformation();
        newInvoice.setInvoiceId(invoiceId);
        newInvoice.setOrderNumber(invoiceRequest.getOrderNumber());
        newInvoice.setCreatedDate(LocalDateTime.now());
        newInvoice.setModifiedDate(LocalDateTime.now());
        newInvoice.setInvoice(invoiceRequest.getInvoice());
        newInvoice.setInvoiceType(invoiceRequest.getInvoiceType());
        newInvoice.setPo(invoiceRequest.getPo());
        newInvoice.setPoDate(invoiceRequest.getPoDate());
        newInvoice.setBillOfLanding(invoiceRequest.getBillOfLanding());
        newInvoice.setRoutingInstruction(invoiceRequest.getRoutingInstruction());
        newInvoice.setTracking(invoiceRequest.getTracking());
        newInvoice.setIssuerIdCode(invoiceRequest.getIssuerIdCode());
        newInvoice.setIssuerName(invoiceRequest.getIssuerName());
        newInvoice.setIssuerAddress(invoiceRequest.getIssuerAddress());
        newInvoice.setBilltoIdCode(invoiceRequest.getBillToIdCode());
        newInvoice.setBilltoName(invoiceRequest.getBillToName());
        newInvoice.setBilltoAddress(invoiceRequest.getBillToAddress());
        newInvoice.setSalesTerms(invoiceRequest.getSalesTerms());
        newInvoice.setPartnerId(invoiceRequest.getPartnerId());
        newInvoice.setCreatedBy(invoiceRequest.getCreatedBy());
        newInvoice.setModifiedBy(invoiceRequest.getModifiedBy());
        newInvoice.setStatus(invoiceRequest.getStatus());
        newInvoice.setShippedDate(invoiceRequest.getShippedDate());
        newInvoice.setStateAndLocalTax(invoiceRequest.getStateAndLocalTax());
        newInvoice.setInvoiceDate(invoiceRequest.getInvoiceDate());
        newInvoice.setTotalInvoiceAmount(invoiceRequest.getTotalInvoiceAmount());
        newInvoice.setTransactionType(invoiceRequest.getTransactionType());

        return invoiceRepository.save(newInvoice)
                .flatMap(savedInvoiceInformation -> {
                    List<Mono<InvoiceLines>> invoiceLinesSaveMonos = new ArrayList<>();
                    for (InvoiceLines invoiceLineRequest : invoiceRequest.getInvoiceLines()) {
                        invoiceLineRequest.setInvoiceId(invoiceId);

                        invoiceLinesSaveMonos.add(invoiceLinesRepository.save(invoiceLineRequest));
                    }

                    return Flux.merge(invoiceLinesSaveMonos)
                            .collectList()
                            .flatMap(savedInvoiceLinesList -> {

                                if ("Invoice".equalsIgnoreCase(invoiceRequest.getStatus())
                                        || "Partial_Invoice".equalsIgnoreCase(invoiceRequest.getStatus())
                                        || "Submitted".equalsIgnoreCase(invoiceRequest.getStatus())) {

                                    List<Mono<Void>> orderDetailsUpdateMonos = new ArrayList<>();
                                    for (InvoiceLines invoiceLineRequest : invoiceRequest.getInvoiceLines()) {
                                        Mono<Void> updateMono = orderDetailsRepository.findById(invoiceLineRequest.getOrderId())
                                                .switchIfEmpty(Mono.error(new RuntimeException("Order details not found for ID: " + invoiceLineRequest.getOrderId())))
                                                .flatMap(orderDetails -> {
                                                    int newRemainingQty = Math.max(0,
                                                            orderDetails.getRemainingInvoiceQty() - invoiceLineRequest.getQtyInvoiced());
                                                    System.out.println("Updating Order ID: " + orderDetails.getId()
                                                            + ", Old invoice Remaining Qty: " + orderDetails.getRemainingInvoiceQty()
                                                            + ", New invoice Remaining Qty: " + newRemainingQty);

                                                    return orderDetailsRepository.updatRemainingInvoiceQty(newRemainingQty, invoiceLineRequest.getOrderId())
                                                            .then();
                                                });
                                        orderDetailsUpdateMonos.add(updateMono);
                                    }
                                    return Flux.merge(orderDetailsUpdateMonos)
                                            .then(Mono.just(savedInvoiceLinesList));
                                } else {
                                    return Mono.just(savedInvoiceLinesList);
                                }
                            })
                            .map(finalInvoiceLinesList -> {
                                response.put("data", savedInvoiceInformation);
                                response.put("lines", finalInvoiceLinesList);
                                response.put("message", "Data saved into invoice Information and lines successfully");
                                response.put("status", true);
                                return response;
                            });
                })
                .onErrorResume(e -> {
                    logger.error("Error in sendInvoice :: {}", e.getMessage());
                    return Mono.just(response);
                });
    }

    public Mono<Map<String, Object>> getInvoiceLines(String shipmentId, String shipmentType) throws IOException {
        logger.info("Executing the method :: getInvoiceLines ");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Error while fetching file");
        response.put("status", false);
        response.put("data", new HashMap<>());
        return invoiceLinesRepository.findByInvoiceId(shipmentId, shipmentType).collectList().map(invoiceLines -> {
            if (!invoiceLines.isEmpty()) {
                response.put("message", "invoiceLines retrieved successfully");
                response.put("status", true);
                response.put("data", invoiceLines);
            }
            logger.info("Executed the method :: getInvoiceLines ");
            return response;
        }).onErrorResume(ex -> {
            logger.error(" getInvoiceLines :: {}", ex.getMessage());
            return Mono.just(response);
        });
    }

    public Mono<Map<String, Object>> getInvoiceData(String shipmentId, String shipmentType, String transactionType)
            throws IOException {
        logger.info("Executing the method :: getInvoiceData ");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Error while fetching file");
        response.put("status", false);
        response.put("data", new HashMap<>());
        return invoiceRepository.findByInvoiceId(shipmentId, shipmentType, transactionType).collectList()
                .map(invoiceData -> {
                    if (!invoiceData.isEmpty()) {
                        response.put("message", "invoiceData retrieved successfully");
                        response.put("status", true);
                        response.put("data", invoiceData);
                    }
                    logger.info("Executed the method :: getInvoiceData ");
                    return response;
                }).onErrorResume(ex -> {
                    logger.error(" getInvoiceData :: {}", ex.getMessage());
                    return Mono.just(response);
                });
    }

//	public Mono<Map<String, Object>> updateInvoice(int id, String invoiceId, InvoiceRequest invoiceRequest) {
//		logger.info("Executing the method :: updateInvoice ");
//		System.out.println(id);
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Failed to update data into Shipment Notice");
//		response.put("status", false);
//		InvoiceLines invoiceLine = new InvoiceLines();
//		List<InvoiceLines> invoiceLinesList = new ArrayList<>();
//		System.out.println(id);
//		try {
//			InvoiceInformation invoice = invoiceRepository.findById(id).block();
//			if (invoice.getInvoiceId().equalsIgnoreCase(invoiceId)) {
//				invoice.setModifiedDate(LocalDateTime.now());
//				invoice.setOrderNumber(invoiceRequest.getOrderNumber());
//				invoice.setInvoice(invoiceRequest.getInvoice());
//				invoice.setInvoiceType(invoiceRequest.getInvoiceType());
//				invoice.setPo(invoiceRequest.getPo());
//				invoice.setPoDate(invoiceRequest.getPoDate());
//				invoice.setBillOfLanding(invoiceRequest.getBillOfLanding());
//				invoice.setRoutingInstruction(invoiceRequest.getRoutingInstruction());
//				invoice.setTracking(invoiceRequest.getTracking());
//				invoice.setIssuerIdCode(invoiceRequest.getIssuerIdCode());
//				invoice.setIssuerName(invoiceRequest.getIssuerName());
//				invoice.setIssuerAddress(invoiceRequest.getIssuerAddress());
//				invoice.setBilltoIdCode(invoiceRequest.getBillToIdCode());
//				invoice.setBilltoName(invoiceRequest.getBillToName());
//				invoice.setBilltoAddress(invoiceRequest.getBillToAddress());
//				invoice.setSalesTerms(invoiceId);
//				invoice.setPartnerId(invoiceRequest.getPartnerId());
//				invoice.setCreatedBy(invoiceRequest.getCreatedBy());
//				invoice.setModifiedBy(invoiceRequest.getModifiedBy());
//				invoice.setStatus(invoiceRequest.getStatus());
//				invoice.setShippedDate(invoiceRequest.getShippedDate());
//				invoice.setStateAndLocalTax(invoiceRequest.getStateAndLocalTax());
//				invoice.setInvoiceDate(invoiceRequest.getInvoiceDate());
//				invoice.setTotalInvoiceAmount(invoiceRequest.getTotalInvoiceAmount());
//				invoice.setTransactionType(invoiceRequest.getTransactionType());
//			}
//			InvoiceInformation invoiceInformation = invoiceRepository.save(invoice).block();
//			for (InvoiceLines invoiceLines : invoiceRequest.getInvoiceLines()) {
//				InvoiceLines invoiceLinesData = invoiceLinesRepository.findById(invoiceLines.getId()).block();
//				if (invoiceLinesData.getInvoiceId().equalsIgnoreCase(invoiceId)) {
//					invoiceLinesData.setInvoiceId(invoiceId);
//					invoiceLinesData.setItem(invoiceLines.getItem());
//					invoiceLinesData.setKohlerProduct(invoiceLines.getKohlerProduct());
//					invoiceLinesData.setLineTotal(invoiceLines.getLineTotal());
//					invoiceLinesData.setOrderNumber(invoiceLines.getOrderNumber());
//					invoiceLinesData.setPartnerId(invoiceLines.getPartnerId());
//					invoiceLinesData.setQtyInvoiced(invoiceLines.getQtyInvoiced());
//					invoiceLinesData.setUnitPrice(invoiceLines.getUnitPrice());
//					invoiceLinesData.setUom(invoiceLines.getUom());
//					invoiceLinesData.setUpc(invoiceLines.getUpc());
//					invoiceLinesData.setVendorPartnumber(invoiceLines.getVendorPartnumber());
//					invoiceLinesData.setStatus(invoiceLines.getStatus());
//					invoiceLinesData.setItemDescription(invoiceLines.getItemDescription());
//					invoiceLinesData.setOrderId(invoiceLines.getOrderId());
//				}
//				invoiceLines = invoiceLinesRepository.save(invoiceLinesData).block();
//				invoiceLinesList.add(invoiceLines);
//			}
//			if ("Invoice".equalsIgnoreCase(invoiceRequest.getStatus())
//					|| "Partial_Invoice".equalsIgnoreCase(invoiceRequest.getStatus())
//					|| "Submitted".equalsIgnoreCase(invoiceRequest.getStatus())) {
//				for (InvoiceLines invoices : invoiceRequest.getInvoiceLines()) {
//					orderDetailsRepository.findById(invoices.getOrderId()).flatMap(orderDetails -> {
//						int newRemainingQty = Math.max(0,
//								orderDetails.getRemainingInvoiceQty() - invoices.getQtyInvoiced());
//						// Print before saving
//						System.out.println("Updating Order ID: " + orderDetails.getId()
//								+ ", Old Invoice Remaining Qty: " + orderDetails.getRemainingInvoiceQty()
//								+ ", New Invoice Remaining Qty: " + newRemainingQty);
//						// Now return the result of update, which is a Mono
//						return orderDetailsRepository.updatRemainingInvoiceQty(newRemainingQty, invoices.getOrderId())
//								.then(Mono.just(orderDetails)); // Make sure to return a Mono
//					}).subscribe(); // Ensure execution of the reactive stream
//				}
//			}
//			if (invoiceInformation != null) {
//				response.put("data", invoiceInformation);
//				response.put("lines", invoiceLinesList);
//				response.put("message", "Data saved into Invoices successfully");
//				response.put("status", true);
//
//			}
//			logger.info("Executed the method :: updateInvoice ");
//		} catch (Exception e) {
//			logger.error(" updateInvoice :: {}", e.getMessage());
//		}
//		return Mono.just(response);
//	}

    public Mono<Map<String, Object>> updateInvoice(int id, String invoiceId, InvoiceRequest invoiceRequest) {
        logger.info("Executing the method :: updateInvoice ");
        System.out.println(id);

        Map<String, Object> response = new HashMap<>();
        response.put("message", "Failed to update data into Shipment Notice");
        response.put("status", false);

        System.out.println(id);

        return invoiceRepository.findById(id)
                .switchIfEmpty(Mono.error(new RuntimeException("Invoice not found for ID: " + id)))
                .flatMap(invoice -> {
                    if (invoice.getInvoiceId().equalsIgnoreCase(invoiceId)) {
                        invoice.setModifiedDate(LocalDateTime.now());
                        invoice.setOrderNumber(invoiceRequest.getOrderNumber());
                        invoice.setInvoice(invoiceRequest.getInvoice());
                        invoice.setInvoiceType(invoiceRequest.getInvoiceType());
                        invoice.setPo(invoiceRequest.getPo());
                        invoice.setPoDate(invoiceRequest.getPoDate());
                        invoice.setBillOfLanding(invoiceRequest.getBillOfLanding());
                        invoice.setRoutingInstruction(invoiceRequest.getRoutingInstruction());
                        invoice.setTracking(invoiceRequest.getTracking());
                        invoice.setIssuerIdCode(invoiceRequest.getIssuerIdCode());
                        invoice.setIssuerName(invoiceRequest.getIssuerName());
                        invoice.setIssuerAddress(invoiceRequest.getIssuerAddress());
                        invoice.setBilltoIdCode(invoiceRequest.getBillToIdCode());
                        invoice.setBilltoName(invoiceRequest.getBillToName());
                        invoice.setBilltoAddress(invoiceRequest.getBillToAddress());
                        invoice.setSalesTerms(invoiceId);
                        invoice.setPartnerId(invoiceRequest.getPartnerId());
                        invoice.setCreatedBy(invoiceRequest.getCreatedBy());
                        invoice.setModifiedBy(invoiceRequest.getModifiedBy());
                        invoice.setStatus(invoiceRequest.getStatus());
                        invoice.setShippedDate(invoiceRequest.getShippedDate());
                        invoice.setStateAndLocalTax(invoiceRequest.getStateAndLocalTax());
                        invoice.setInvoiceDate(invoiceRequest.getInvoiceDate());
                        invoice.setTotalInvoiceAmount(invoiceRequest.getTotalInvoiceAmount());
                        invoice.setTransactionType(invoiceRequest.getTransactionType());
                    } else {
                        return Mono.error(new IllegalArgumentException("Invoice ID mismatch for update."));
                    }
                    return invoiceRepository.save(invoice);
                })
                .flatMap(invoiceInformation -> {
                    List<Mono<InvoiceLines>> invoiceLinesUpdateMonos = new ArrayList<>();
                    for (InvoiceLines invoiceLineRequest : invoiceRequest.getInvoiceLines()) {
                        Mono<InvoiceLines> lineUpdateMono = invoiceLinesRepository.findById(invoiceLineRequest.getId())
                                .switchIfEmpty(Mono.error(new RuntimeException("Invoice line not found for ID: " + invoiceLineRequest.getId())))
                                .flatMap(invoiceLinesData -> {
                                    if (invoiceLinesData.getInvoiceId().equalsIgnoreCase(invoiceId)) {
                                        invoiceLinesData.setInvoiceId(invoiceId);
                                        invoiceLinesData.setItem(invoiceLineRequest.getItem());
                                        invoiceLinesData.setKohlerProduct(invoiceLineRequest.getKohlerProduct());
                                        invoiceLinesData.setLineTotal(invoiceLineRequest.getLineTotal());
                                        invoiceLinesData.setOrderNumber(invoiceLineRequest.getOrderNumber());
                                        invoiceLinesData.setPartnerId(invoiceLineRequest.getPartnerId());
                                        invoiceLinesData.setQtyInvoiced(invoiceLineRequest.getQtyInvoiced());
                                        invoiceLinesData.setUnitPrice(invoiceLineRequest.getUnitPrice());
                                        invoiceLinesData.setUom(invoiceLineRequest.getUom());
                                        invoiceLinesData.setUpc(invoiceLineRequest.getUpc());
                                        invoiceLinesData.setVendorPartnumber(invoiceLineRequest.getVendorPartnumber());
                                        invoiceLinesData.setStatus(invoiceLineRequest.getStatus());
                                        invoiceLinesData.setItemDescription(invoiceLineRequest.getItemDescription());
                                        invoiceLinesData.setOrderId(invoiceLineRequest.getOrderId());
                                    } else {
                                        return Mono.error(new IllegalArgumentException("Invoice line ID mismatch for update."));
                                    }
                                    return invoiceLinesRepository.save(invoiceLinesData);
                                });
                        invoiceLinesUpdateMonos.add(lineUpdateMono);
                    }

                    return Flux.merge(invoiceLinesUpdateMonos)
                            .collectList()
                            .flatMap(updatedInvoiceLinesList -> {
                                // Conditional update for OrderDetails based on invoice status
                                if ("Invoice".equalsIgnoreCase(invoiceRequest.getStatus())
                                        || "Partial_Invoice".equalsIgnoreCase(invoiceRequest.getStatus())
                                        || "Submitted".equalsIgnoreCase(invoiceRequest.getStatus())) {

                                    List<Mono<Void>> orderDetailsUpdateMonos = new ArrayList<>();
                                    for (InvoiceLines invoiceLineRequest : invoiceRequest.getInvoiceLines()) {
                                        Mono<Void> orderDetailsUpdateMono = orderDetailsRepository.findById(invoiceLineRequest.getOrderId())
                                                .switchIfEmpty(Mono.error(new RuntimeException("Order details not found for ID: " + invoiceLineRequest.getOrderId())))
                                                .flatMap(orderDetails -> {
                                                    int newRemainingQty = Math.max(0,
                                                            orderDetails.getRemainingInvoiceQty() - invoiceLineRequest.getQtyInvoiced());
                                                    System.out.println("Updating Order ID: " + orderDetails.getId()
                                                            + ", Old Invoice Remaining Qty: " + orderDetails.getRemainingInvoiceQty()
                                                            + ", New Invoice Remaining Qty: " + newRemainingQty);
                                                    // Update remaining quantity and return Mono<Void> using then()
                                                    return orderDetailsRepository.updatRemainingInvoiceQty(newRemainingQty, invoiceLineRequest.getOrderId())
                                                            .then(); // Use then() to signal completion without emitting a value
                                                });
                                        orderDetailsUpdateMonos.add(orderDetailsUpdateMono);
                                    }
                                    // Wait for all order details updates to complete, then return the invoice information and updated lines
                                    return Flux.merge(orderDetailsUpdateMonos)
                                            .then(Mono.just(updatedInvoiceLinesList)); // Pass the updatedInvoiceLinesList downstream
                                } else {
                                    // If status condition is not met, just pass the updated invoice lines list
                                    return Mono.just(updatedInvoiceLinesList);
                                }
                            })
                            .map(finalInvoiceLinesList -> {
                                // Construct the final success response map
                                response.put("data", invoiceInformation);
                                response.put("lines", finalInvoiceLinesList);
                                response.put("message", "Data saved into Invoices successfully");
                                response.put("status", true);
                                logger.info("Executed the method :: updateInvoice ");
                                return response;
                            });
                })
                .onErrorResume(e -> {
                    // Catch any exceptions in the reactive chain and log them
                    logger.error("Error in updateInvoice :: {}", e.getMessage());
                    // Return the initial failure response map
                    return Mono.just(response);
                });
    }

    public Mono<Map<String, Object>> b2bInvoiceSubmit(InvoiceRequest invoice) throws IOException {
        logger.info("Executing the method :: b2bInvoiceSubmit ");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Error while getting data from bpdurl");
        try {
            final String uriForWithoutSsl = invoice.getBpdLink();
            final HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_XML);
            final StringBuilder xml = new StringBuilder();
            xml.append("<LookUpDetails> <invoiceId>").append(invoice.getInvoiceId()).append("</invoiceId><partnerId>")
                    .append(invoice.getPartnerId()).append("</partnerId>").append("</LookUpDetails>");
            System.out.println(xml);
            final HttpEntity<String> entityForWithoutSsl = new HttpEntity<String>(xml.toString(), headers);
            final RestTemplate restTemplate = new RestTemplate();
            final ResponseEntity<String> data = restTemplate.postForEntity(uriForWithoutSsl, entityForWithoutSsl,
                    String.class);
            System.out.println(data);
            System.out.println(data.getStatusCode());
            if (data.getStatusCode().value() == 250) {
                response.put("success", true);
                response.put("message", "Request Sent to B2B Successfully");
            }
            logger.info("Executed the method :: b2bInvoiceSubmit ");
        } catch (final Exception e) {
            System.out.println(" reprocessRequest :: " + e.getMessage());
            response.put("success", false);
            response.put("message", "Request Failed to B2B. Please Contact Administrator:::" + e.getMessage());
            logger.error(" b2bInvoiceSubmit :: " + e.getMessage());
        }
        return Mono.just(response);
    }

}
