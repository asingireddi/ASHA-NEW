package com.miraclesoft.rehlko.controller;

import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.rehlko.dto.ShipmentLineDto;
import com.miraclesoft.rehlko.entity.ShipmentLines;
import com.miraclesoft.rehlko.entity.ShipmentNotice;
import com.miraclesoft.rehlko.entity.ShipmentRequest;
import com.miraclesoft.rehlko.service.ShipmentService;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/shipment")
public class ShipmentController {
	private static final Logger logger = LoggerFactory.getLogger(ShipmentController.class);

	private final ShipmentService shipmentService;
	// private final DraftsShipmentService draftsshipmentService;

	public ShipmentController(ShipmentService shipmentService) {
		this.shipmentService = shipmentService;
		// this.draftsshipmentService=draftsshipmentService;
	}

	/**
	 * Send a new shipment notice.
	 */
	@PostMapping("/sendShipment")
	public Mono<Map<String, Object>> sendShipmentNotice(@RequestBody ShipmentRequest shipmentRequest) {
		logger.info("Sending shipment notice for partner: {}", shipmentRequest.getPartnerId());
		return shipmentService.sendShipmentNotice(shipmentRequest);
	}

//    @PostMapping("/draftShipment")
//    public Mono<Map<String, Object>> saveShipmentNotice(@RequestBody DraftsShipmentRequest draftsShipmentRequest) {
//    	return shipmentService.saveShipmentNotice(draftsShipmentRequest);
//    }

//	@GetMapping("/orderdetails/{correlationkey1}/{order_type}")
//	public Mono<Map<String, Object>> getShipmentOrderDetails(
//	        @PathVariable String correlationkey1, 
//	        @PathVariable String order_type) throws IOException {
//	    return shipmentService.getShipmentOrderDetails(correlationkey1, order_type);
//	}

	/**
	 * Fetch all draft shipments by partner and type.
	 */
	@GetMapping("/all-drafts/{partnerId}/{shipmentType}")
	public Mono<Map<String, Object>> getDraftShipmentNotice(@PathVariable String partnerId,
			@PathVariable String shipmentType) {
		logger.info("Fetching all drafts for partnerId: {}, shipmentType: {}", partnerId, shipmentType);
		return shipmentService.getDraftShipmentNotice(partnerId, shipmentType);
	}

	/**
	 * Get a specific draft shipment by ID and type.
	 */
	@GetMapping("/get-draft/{shipmentId}/{shipmentType}")
	public Mono<Map<String, Object>> getDraftShipment(@PathVariable String shipmentId,
			@PathVariable String shipmentType) throws IOException {
		logger.info("Fetching draft shipment with shipmentId: {}, type: {}", shipmentId, shipmentType);
		return shipmentService.getDraftShipment(shipmentId, shipmentType);
	}

	/**
	 * Get shipment lines by shipment ID and type.
	 */
	@GetMapping("/get-shipment-lines/{shipmentId}/{shipmentType}")
	public Mono<Map<String, Object>> getShipmentLines(@PathVariable String shipmentId,
			@PathVariable String shipmentType) throws IOException {
		logger.info("Fetching shipment lines for shipmentId: {}, type: {}", shipmentId, shipmentType);
		return shipmentService.getShipmentLines(shipmentId, shipmentType);
	}

//    @GetMapping("/get-draft-shipment-lines/{shipmentId}/{partnerId}/{shipmentType}")
//    public Mono<Map<String, Object>>  getDraftShipmetLines(@PathVariable String shipmentId,@PathVariable String partnerId,@PathVariable String shipmentType) throws IOException  {    	
//		return shipmentService.getDraftShipmetLines(shipmentId,partnerId,shipmentType);
//	}

	/**
	 * Fetch shipment notice data by shipmentId, partnerId and shipmentType.
	 */
	@GetMapping("/get-shipment-notice/{shipmentId}/{partnerId}/{shipmentType}")
	public Mono<Map<String, Object>> getShipmentNotice(@PathVariable String shipmentId, @PathVariable String partnerId,
			@PathVariable String shipmentType) throws IOException {
		logger.info("Fetching shipment notice for shipmentId: {}, partnerId: {}, type: {}", shipmentId, partnerId,
				shipmentType);
		return shipmentService.getShipmentNotice(shipmentId, partnerId, shipmentType);
	}

	/**
	 * Update existing shipment data.
	 */
	@PutMapping("/update-shipment/{id}/{shipmentId}")
	public Mono<Map<String, Object>> updateShipmentStatus(@PathVariable int id, @PathVariable String shipmentId,
			@RequestBody ShipmentRequest shipmentRequest) {
		logger.info("Updating shipment with ID: {}, shipmentId: {}", id, shipmentId);
		return shipmentService.updateShipments(id, shipmentId, shipmentRequest);
	}

	/**
	 * Submit shipment to B2B.
	 */
	@PostMapping("/b2bShipmentSubmit")
	public Mono<Map<String, Object>> b2bSendShipment(@RequestBody ShipmentRequest shipmentRequest) throws IOException {
		logger.info("Submitting B2B shipment for orderNumber: {}", shipmentRequest.getOrderNumber());
		return shipmentService.b2bSendShipment(shipmentRequest);
	}

	/**
	 * Get Bill of Lading for a specific order.
	 */
	@GetMapping("/getBillOfLanding/{orderNumber}/{transactionType}")
	public Flux<ShipmentNotice> getBillOfLanding(@PathVariable int orderNumber, @PathVariable String transactionType) {
		logger.info("Getting Bill of Lading for order: {}, type: {}", orderNumber, transactionType);
		return shipmentService.getBillOfLanding(orderNumber, transactionType);
	}

	/**
	 * Get pending shipment quantity by order number.
	 */
	@GetMapping("/quantityCount/{orderNumber}")
	public Flux<ShipmentLines> getPendingQuantity(@PathVariable int orderNumber) {
		logger.info("Getting pending quantity for orderNumber: {}", orderNumber);
		return shipmentService.getPendingQuantity(orderNumber);
	}

//	@GetMapping("/getBillOfLading")
//	public Flux<ShipmentLineDTO> getBillOfLading(@RequestBody ShipmentNotice shipmentNotice){
//		return shipmentService.getBillOfLading(shipmentNotice);
//	}

	/**
	 * Get shipment details by Bill of Lading and orderNumber.
	 */
	@GetMapping("/getBillOfLading")
	public Flux<ShipmentLineDto> getShipmetNotice1(@RequestParam String billOfLanding, @RequestParam String orderNumber,
			@RequestParam String transactionType) throws IOException {
		logger.info("Getting Bill of Lading for orderNumber: {}, billOfLanding: {}, transactionType: {}", orderNumber,
				billOfLanding, transactionType);
		return shipmentService.getBillOfLading(billOfLanding, orderNumber, transactionType);
	}

	/**
	 * Get shipment order details by correlationKey, orderType, and status.
	 */
	@GetMapping("/orderdetails/{correlationkey1}/{order_type}/{status}")
	public Mono<Map<String, Object>> getShipmentOrderDetails(@PathVariable String correlationkey1,
			@PathVariable String order_type, @PathVariable String status) throws IOException {
		logger.info("Getting shipment order details for correlationKey1: {}, orderType: {}, status: {}",
				correlationkey1, order_type, status);
		return shipmentService.getShipmentOrderDetails(correlationkey1, order_type, status);
	}
}