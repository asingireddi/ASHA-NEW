package com.miraclesoft.rehlko.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.*;

import com.miraclesoft.rehlko.entity.InvoiceRequest;
import com.miraclesoft.rehlko.service.InvoiceService;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/invoice")
public class InvoiceController {

	private static final Logger logger = LoggerFactory.getLogger(InvoiceController.class);

	private final InvoiceService invoiceService;

	public InvoiceController(InvoiceService invoiceService) {
		this.invoiceService = invoiceService;
	}

	/**
	 * Sends invoice data to be processed and saved.
	 *
	 * @param invoiceRequest The invoice request payload.
	 * @return Response containing status and any saved invoice metadata.
	 */
	@PostMapping("/sendInvoice")
	public Mono<Map<String, Object>> sendInvoice(@RequestBody InvoiceRequest invoiceRequest) {
		logger.info("Received request to send invoice for invoiceId: {}", invoiceRequest.getInvoiceId());
		return invoiceService.sendInvoice(invoiceRequest).doOnSuccess(resp -> logger.info("Invoice sent successfully"))
				.doOnError(err -> logger.error("Error sending invoice", err));
	}

	/**
	 * Fetches full invoice data based on invoiceId, invoiceType, and
	 * transactionType.
	 */
	@GetMapping("/get-invoice/{invoiceId}/{invoiceType}/{transactionType}")
	public Mono<Map<String, Object>> getInvoiceData(@PathVariable String invoiceId, @PathVariable String invoiceType,
			@PathVariable String transactionType) throws IOException {

		logger.info("Fetching invoice data for invoiceId={}, type={}, transaction={}", invoiceId, invoiceType,
				transactionType);
		return invoiceService.getInvoiceData(invoiceId, invoiceType, transactionType)
				.doOnSuccess(data -> logger.info("Successfully fetched invoice data"))
				.doOnError(err -> logger.error("Error fetching invoice data", err));
	}

	/**
	 * Fetches invoice line items for a given invoiceId and invoiceType.
	 */
	@GetMapping("/get-invoice-lines/{invoiceId}/{invoiceType}")
	public Mono<Map<String, Object>> getInvoiceLines(@PathVariable String invoiceId, @PathVariable String invoiceType)
			throws IOException {

		logger.info("Fetching invoice lines for invoiceId={}, type={}", invoiceId, invoiceType);
		return invoiceService.getInvoiceLines(invoiceId, invoiceType)
				.doOnSuccess(data -> logger.info("Successfully fetched invoice lines"))
				.doOnError(err -> logger.error("Error fetching invoice lines", err));
	}
//      
//    @PostMapping("/draftShipment")
//    public Mono<Map<String, Object>> saveShipmentNotice(@RequestBody DraftsShipmentRequest shipmentRequest) {
//    	return shipmentService.saveShipmentNotice(shipmentRequest);
//    }

//    
//    @PostMapping("/sendShipment/{id}")
//    public Mono<Map<String, Object>> saveShipmentLines(@PathVariable int id,@RequestBody List<ShipmentLines> shipmentNotice) {
//    	return shipmentService.saveShipmentNotice(id,shipmentNotice);
//    }

	/**
	 * Updates an existing invoice record.
	 */
	@PutMapping("/update-invoice/{id}/{invoiceId}")
	public Mono<Map<String, Object>> updateShipmentStatus(@PathVariable int id, @PathVariable String invoiceId,
			@RequestBody InvoiceRequest invoiceRequest) {

		logger.info("Updating invoice with DB ID: {}, Invoice ID: {}", id, invoiceId);
		return invoiceService.updateInvoice(id, invoiceId, invoiceRequest)
				.doOnSuccess(resp -> logger.info("Invoice updated successfully"))
				.doOnError(err -> logger.error("Error updating invoice", err));
	}

	/**
	 * Submits invoice to external B2B endpoint.
	 */
	@PostMapping("/b2bInvoiceSubmit")
	public Mono<Map<String, Object>> b2bIncoiceSubmit(@RequestBody InvoiceRequest invoiceRequest) throws IOException {
		logger.info("Submitting B2B invoice for invoiceId: {}", invoiceRequest.getInvoiceId());
		return invoiceService.b2bInvoiceSubmit(invoiceRequest)
				.doOnSuccess(resp -> logger.info("B2B Invoice submission successful"))
				.doOnError(err -> logger.error("B2B Invoice submission failed", err));
	}

//  @GetMapping("/PartnersIdAndName")
//  public List<Map<String, Object>> getGroupedPartners() {
//      return utilizationService.getGroupedPartners();
//  }
//  @GetMapping("/get-invoice-lines/{invoiceId}/{invoiceType}")
//  public Mono<Map<String, Object>>  getInvoiceLines(@PathVariable String invoiceId,@PathVariable String invoiceType) throws IOException  {    	
//		return invoiceService.getInvoiceLines(invoiceId,invoiceType);
//	}

}
