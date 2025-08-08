package com.miraclesoft.rehlko.service;

import com.miraclesoft.rehlko.dto.ShipmentLineDto;
import com.miraclesoft.rehlko.entity.OrderDetails;
import com.miraclesoft.rehlko.entity.ShipmentLines;
import com.miraclesoft.rehlko.entity.ShipmentNotice;
import com.miraclesoft.rehlko.entity.ShipmentRequest;
import com.miraclesoft.rehlko.repository.OrderDetailsRepository;
import com.miraclesoft.rehlko.repository.ShipmentLinesRepository;
import com.miraclesoft.rehlko.repository.ShipmentRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ShipmentService {

	private final DatabaseClient databaseClient;
	private final ShipmentRepository shipmentRepository;
	private final ShipmentLinesRepository shipmentLinesRepository;

	private final OrderDetailsRepository orderDetailsRepository;
	private static final Logger logger = LoggerFactory.getLogger(ShipmentService.class.getName());

	public ShipmentService(ShipmentRepository shipmentRepository, ShipmentLinesRepository shipmentLinesRepository,
			OrderDetailsRepository orderDetailsRepository) {
		this.databaseClient = null;
		this.shipmentRepository = shipmentRepository;
		this.shipmentLinesRepository = shipmentLinesRepository;

		this.orderDetailsRepository = orderDetailsRepository;
	}

//	public Mono<Map<String, Object>> sendShipmentNotice(ShipmentRequest shipmentRequest) {
//		logger.info("Executing the method :: sendShipmentNotice ");
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Failed to save data into Shipment Notice");
//		response.put("status", false);
//		try {
//			String shipmentId = UUID.randomUUID().toString();
//			ShipmentNotice shipmentNotice = new ShipmentNotice();
//			ShipmentLines shipmentLine = new ShipmentLines();
//			List<ShipmentLines> shipmentLinesList = new ArrayList<>();
//			shipmentNotice.setShipmentId(shipmentId);
//			shipmentNotice.setCreatedDate(LocalDateTime.now());
//			shipmentNotice.setModifiedDate(LocalDateTime.now());
//			shipmentNotice.setTransactionPurpose(shipmentRequest.getTransactionPurpose());
//			shipmentNotice.setAsnNmber(shipmentRequest.getAsnNmber());
//			shipmentNotice.setShipmentDate(shipmentRequest.getShipmentDate());
//			shipmentNotice.setShipmentTime(shipmentRequest.getShipmentTime());
//			shipmentNotice.setShippedDate(shipmentRequest.getShippedDate());
//			shipmentNotice.setEstimatedDeliveryDate(shipmentRequest.getEstimatedDeliveryDate());
//			shipmentNotice.setSupplierName(shipmentRequest.getSupplierName());
//			shipmentNotice.setSupplierNumber(shipmentRequest.getSupplierNumber());
//			shipmentNotice.setShiptoName(shipmentRequest.getShiptoName());
//			shipmentNotice.setShiptoPlantCode(shipmentRequest.getShiptoPlantCode());
//			shipmentNotice.setBillOfLanding(shipmentRequest.getBillOfLanding());
//			shipmentNotice.setShipmentTrackingNumber(shipmentRequest.getShipmentTrackingNumber());
//			shipmentNotice.setMeansOfTransport(shipmentRequest.getMeansOfTransport());
//			shipmentNotice.setPoNumber(shipmentRequest.getPoNumber());
//			shipmentNotice.setPoDate(shipmentRequest.getPoDate());
//			shipmentNotice.setCreatedBy(shipmentRequest.getCreatedBy());
//			shipmentNotice.setModifiedBy(shipmentRequest.getModifiedBy());
//			shipmentNotice.setOrderNumber(shipmentRequest.getOrderNumber());
//			shipmentNotice.setPartnerId(shipmentRequest.getPartnerId());
//			shipmentNotice.setStatus(shipmentRequest.getStatus());
//			shipmentNotice.setPartialShipFlag(shipmentRequest.getPartialShipFlag());
//			shipmentNotice.setTransactionType(shipmentRequest.getTransactionType());
//			shipmentNotice = shipmentRepository.save(shipmentNotice).block();
//			// Save shipment lines
//			for (ShipmentLines shipments : shipmentRequest.getShipmentLines()) {
//				ShipmentLines shipment = new ShipmentLines();
//				shipment.setShipmentId(shipmentId);
//				shipment.setOrderNumber(shipmentRequest.getOrderNumber());
//				shipment.setBuyerNumber(shipments.getBuyerNumber());
//				shipment.setQty(shipments.getQty());
//				shipment.setUom(shipments.getUom());
//				shipment.setPoLineNumber(shipments.getPoLineNumber());
//				shipment.setPartnerId(shipments.getPartnerId());
//				shipment.setStatus(shipments.getStatus());
//				shipment.setOrderType(shipments.getOrderType());
//				shipment.setItemDescription(shipments.getItemDescription());
//				shipment.setVendorPartnumber(shipments.getVendorPartnumber());
//				shipment.setPriceUnit(shipments.getPriceUnit());
//				shipment.setOriginalOrderQty(shipments.getOriginalOrderQty());
//				shipment.setOrderId(shipments.getOrderId());
//				shipment.setUnitPrice(shipments.getUnitPrice());
//				shipment.setOrderType(shipments.getOrderType());
//				shipmentLine = shipmentLinesRepository.save(shipment).block();
//				shipmentLinesList.add(shipmentLine);
//			}
//			System.out.println(shipmentRequest.getStatus());
//			// Only execute this loop when the status is "submitted"
//			if ("Submit".equalsIgnoreCase(shipmentRequest.getStatus()) || "Partial_Shipment".equalsIgnoreCase(shipmentRequest.getStatus())) {
//
//				for (ShipmentLines shipments : shipmentRequest.getShipmentLines()) {
//					orderDetailsRepository.findById(shipments.getOrderId()).flatMap(orderDetails -> {
//						int newRemainingQty = Math.max(0,orderDetails.getRemainingQty() - shipments.getQty());
//						orderDetails.setRemainingQty(newRemainingQty);
//						// Print before saving
//						System.out.println("Updating Order ID: " + orderDetails.getId() + ", Old Remaining Qty: "
//								+ orderDetails.getRemainingQty() + ", New Remaining Qty: " + newRemainingQty);
//						// Now return the result of update, which is a Mono
//						return orderDetailsRepository.update(newRemainingQty, shipments.getOrderId())
//								.then(Mono.just(orderDetails)); // Make sure to return a Mono
//					}).subscribe(); // Ensure execution of the reactive stream
//				}
//			}
//			// If shipment notice is not null, prepare the success response
//			if (shipmentNotice != null) {
//				response.put("data", shipmentNotice);
//				response.put("lines", shipmentLinesList);
//				response.put("message", "Data saved into shipmentNotice successfully");
//				response.put("status", true);
//			}
//		} catch (Exception ex) {
//			logger.error("Exception in sendShipmentNotice ::{}", ex.getMessage());
//		}
//		logger.info("Executed the method :: sendShipmentNotice ");
//		return Mono.just(response);
//	}

	public Mono<Map<String, Object>> sendShipmentNotice(ShipmentRequest shipmentRequest) {
		logger.info("Executing the method :: sendShipmentNotice ");

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to save data into Shipment Notice");
		response.put("status", false);

		String shipmentId = UUID.randomUUID().toString();

		ShipmentNotice newShipmentNotice = new ShipmentNotice();
		newShipmentNotice.setShipmentId(shipmentId);
		newShipmentNotice.setCreatedDate(LocalDateTime.now());
		newShipmentNotice.setModifiedDate(LocalDateTime.now());
		newShipmentNotice.setTransactionPurpose(shipmentRequest.getTransactionPurpose());
		newShipmentNotice.setAsnNmber(shipmentRequest.getAsnNmber());
		newShipmentNotice.setShipmentDate(shipmentRequest.getShipmentDate());
		newShipmentNotice.setShipmentTime(shipmentRequest.getShipmentTime());
		newShipmentNotice.setShippedDate(shipmentRequest.getShippedDate());
		newShipmentNotice.setEstimatedDeliveryDate(shipmentRequest.getEstimatedDeliveryDate());
		newShipmentNotice.setSupplierName(shipmentRequest.getSupplierName());
		newShipmentNotice.setSupplierNumber(shipmentRequest.getSupplierNumber());
		newShipmentNotice.setShiptoName(shipmentRequest.getShiptoName());
		newShipmentNotice.setShiptoPlantCode(shipmentRequest.getShiptoPlantCode());
		newShipmentNotice.setBillOfLanding(shipmentRequest.getBillOfLanding());
		newShipmentNotice.setShipmentTrackingNumber(shipmentRequest.getShipmentTrackingNumber());
		newShipmentNotice.setMeansOfTransport(shipmentRequest.getMeansOfTransport());
		newShipmentNotice.setPoNumber(shipmentRequest.getPoNumber());
		newShipmentNotice.setPoDate(shipmentRequest.getPoDate());
		newShipmentNotice.setCreatedBy(shipmentRequest.getCreatedBy());
		newShipmentNotice.setModifiedBy(shipmentRequest.getModifiedBy());
		newShipmentNotice.setOrderNumber(shipmentRequest.getOrderNumber());
		newShipmentNotice.setPartnerId(shipmentRequest.getPartnerId());
		newShipmentNotice.setStatus(shipmentRequest.getStatus());
		newShipmentNotice.setPartialShipFlag(shipmentRequest.getPartialShipFlag());
		newShipmentNotice.setTransactionType(shipmentRequest.getTransactionType());

		return shipmentRepository.save(newShipmentNotice).flatMap(savedShipmentNotice -> {
			List<Mono<ShipmentLines>> shipmentLinesSaveMonos = new ArrayList<>();
			for (ShipmentLines shipmentLineRequest : shipmentRequest.getShipmentLines()) {
				shipmentLineRequest.setShipmentId(shipmentId);
				shipmentLineRequest.setOrderNumber(shipmentRequest.getOrderNumber());

				shipmentLinesSaveMonos.add(shipmentLinesRepository.save(shipmentLineRequest));
			}

			return Flux.merge(shipmentLinesSaveMonos).collectList().flatMap(savedShipmentLinesList -> {
				if ("Submit".equalsIgnoreCase(shipmentRequest.getStatus())
						|| "Partial_Shipment".equalsIgnoreCase(shipmentRequest.getStatus())) {

					List<Mono<Void>> orderDetailsUpdateMonos = new ArrayList<>();
					for (ShipmentLines shipmentLineRequest : shipmentRequest.getShipmentLines()) {
						Mono<Void> updateMono = orderDetailsRepository.findById(shipmentLineRequest.getOrderId())
								.switchIfEmpty(Mono.error(new RuntimeException(
										"Order details not found for ID: " + shipmentLineRequest.getOrderId())))
								.flatMap(orderDetails -> {
									int newRemainingQty = Math.max(0,
											orderDetails.getRemainingQty() - shipmentLineRequest.getQty());
									orderDetails.setRemainingQty(newRemainingQty); // Update the entity
									System.out.println("Updating Order ID: " + orderDetails.getId()
											+ ", Old Remaining Qty: " + orderDetails.getRemainingQty()
											+ ", New Remaining Qty: " + newRemainingQty);

									return orderDetailsRepository
											.update(newRemainingQty, shipmentLineRequest.getOrderId()).then();
								});
						orderDetailsUpdateMonos.add(updateMono);
					}
					return Flux.merge(orderDetailsUpdateMonos).then(Mono.just(savedShipmentLinesList));
				} else {
					return Mono.just(savedShipmentLinesList);
				}
			}).map(finalShipmentLinesList -> {
				response.put("data", savedShipmentNotice);
				response.put("lines", finalShipmentLinesList);
				response.put("message", "Data saved into shipmentNotice successfully");
				response.put("status", true);
				return response;
			});
		}).onErrorResume(e -> {
			logger.error("Exception in sendShipmentNotice :: {}", e.getMessage());
			return Mono.just(response);
		}).doFinally(signalType -> {
			logger.info("Executed the method :: sendShipmentNotice - Finished with signal: {}", signalType);
		});
	}

//	 public Mono<Map<String, Object>> saveShipmentNotice(DraftsShipmentRequest shipmentRequest) {
//	    	Map<String, Object> response = new HashMap<>();
//	   
//	    	response.put("message", "Failed to save data into Shipment Notice");
//	    	response.put("status", false);	    	
//	    	try {   	
//	    		String shipmentId = UUID.randomUUID().toString();	    		
//	    		DraftsShipmentNotice shipmentNotice=new DraftsShipmentNotice();
//	    		DraftsShipmentLines shipmentLine=new DraftsShipmentLines();
//	    		List<DraftsShipmentLines> shipmentLinesList=new ArrayList<>();	    		
//	    		shipmentNotice.setShipmentId(shipmentId);
//	    		shipmentNotice.setCreatedDate(LocalDateTime.now());	  
//	    		shipmentNotice.setModifiedDate(LocalDateTime.now());
//	    		shipmentNotice.setTransactionPurpose(shipmentRequest.getTransactionPurpose());
//	    		shipmentNotice.setAsnNmber(shipmentRequest.getAsnNmber());
//	    		shipmentNotice.setShipmentDate(shipmentRequest.getShipmentDate());
//	    		shipmentNotice.setShipmentTime(shipmentRequest.getShipmentTime());	
//	    		shipmentNotice.setShippedDate(shipmentRequest.getShippedDate());
//	    		shipmentNotice.setEstimatedDeliveryDate(shipmentRequest.getEstimatedDeliveryDate());
//	    		shipmentNotice.setSupplierName(shipmentRequest.getSupplierName());
//	    		shipmentNotice.setSupplierNumber(shipmentRequest.getSupplierNumber());
//	    		shipmentNotice.setShiptoName(shipmentRequest.getShiptoName());
//	    		shipmentNotice.setShiptoPlantCode(shipmentRequest.getShiptoPlantCode());
//	    		shipmentNotice.setBillOfLanding(shipmentRequest.getBillOfLanding());
//	    		shipmentNotice.setShipmentTrackingNumber(shipmentRequest.getShipmentTrackingNumber());
//	    		shipmentNotice.setMeansOfTransport(shipmentRequest.getMeansOfTransport());
//	    		shipmentNotice.setPoNumber(shipmentRequest.getPoNumber());
//	    		shipmentNotice.setPoDate(shipmentRequest.getPoDate());
//	    		shipmentNotice.setCreatedBy(shipmentRequest.getCreatedBy());
//	    		shipmentNotice.setModifiedBy(shipmentRequest.getModifiedBy());		
//	    		shipmentNotice.setOrderNumber(shipmentRequest.getOrderNumber());
//	    		shipmentNotice.setPartnerId(shipmentRequest.getPartnerId());
//	    		shipmentNotice = shipmentRepository.save(shipmentNotice).block();	    		
//	    		for(ShipmentLines shipments:shipmentRequest.getShipmentLines())
//	    		{
//	    			DraftsShipmentLines shipment=new DraftsShipmentLines();	    			
//	    			shipment.setShipmentId(shipmentId);
//	    			shipment.setOrderNumber(shipmentRequest.getOrderNumber());
//	    			shipment.setBuyerNumber(shipments.getBuyerNumber());
//	    			shipment.setLineItem(shipments.getLineItem());
//	    			shipment.setShiptoName(shipments.getShiptoName());
//	    			shipment.setShiptoCode(shipments.getShiptoCode());
//	    			shipment.setQty(shipments.getQty());
//	    			shipment.setUom(shipments.getUom());
//	    			shipment.setPoLineNumber(shipments.getPoLineNumber());
//	    			shipment.setPartnerId(shipments.getPartnerId());
//	    			shipmentLine= draftsShipmentLinesRepository.save(shipment).block();
//	    			shipmentLinesList.add(shipmentLine);
//	    		}
//	    		
//	         if(shipmentNotice!=null) {       	 
//	        	 response.put("data", shipmentNotice);
//	        	 response.put("lines", shipmentLinesList);
//	        	 response.put("message", "Data saved into shipmentNotice successfully");
//	        	 response.put("status", true);
//	        	
//	         }
//	    	} catch (Exception ex) {
//	    		ex.printStackTrace();
//	    	}
//			return Mono.just(response);   
//	    }

//	public Mono<Map<String, Object>> getShipmentOrderDetails(String correlationKey, String orderType) throws IOException {
//	    Map<String, Object> response = new HashMap<>();
//
//	    response.put("message", "Error while fetching file");
//	    response.put("status", false);
//	    response.put("data", new HashMap<>());
//
//	    return orderDetailsRepository.findByCorrelationKeyAndOrderType(correlationKey, orderType)
//	        .collectList()
//	        .map(orderDetails -> {
//	            if (!orderDetails.isEmpty()) {
//	                response.put("message", "Order details retrieved successfully");
//	                response.put("status", true);
//	                response.put("data", orderDetails);
//	            }
//	            return response;
//	        })
//	        .onErrorResume(ex -> {
//	            ex.printStackTrace();
//	            return Mono.just(response);
//	        });
//	}

	public Mono<Map<String, Object>> getShipmentLines(String shipmentId, String shipmentType) throws IOException {
		logger.info("Executing the method :: getShipmentLines {}{}", shipmentId, shipmentType);
		Map<String, Object> response = new HashMap<>();

		response.put("message", "Error while fetching file");
		response.put("status", false);
		response.put("data", new HashMap<>());

		return shipmentLinesRepository.findByshipmentId(shipmentId, shipmentType).collectList().map(shipmentLines -> {
			if (!shipmentLines.isEmpty()) {
				response.put("message", "ShipmetLines retrieved successfully");
				response.put("status", true);
				response.put("data", shipmentLines);
			}
			logger.info("Executed the method :: getShipmentLines {}{}", shipmentId, shipmentType);
			return response;
		}).onErrorResume(ex -> {
			logger.error("Exception in getShipmentLines ::{}", ex.getMessage());
			return Mono.just(response);
		});

	}

	public Mono<Map<String, Object>> getDraftShipmentLines(String shipmentId, String shipmentType) throws IOException {
		logger.info("Executing the method :: getDraftShipmentLines {}{}", shipmentId, shipmentType);
		Map<String, Object> response = new HashMap<>();

		response.put("message", "Error while fetching file");
		response.put("status", false);
		response.put("data", new HashMap<>());

		return shipmentLinesRepository.findByshipmentId(shipmentId, shipmentType).collectList().map(shipmentLines -> {
			if (!shipmentLines.isEmpty()) {
				response.put("message", "Shipment Lines retrieved successfully");
				response.put("status", true);
				response.put("data", shipmentLines);
			}
			logger.info("Executed the method :: getDraftShipmentLines {}{}", shipmentId, shipmentType);
			return response;
		}).onErrorResume(ex -> {
			logger.error("Exception in getDraftShipmentLines :: {}", ex.getMessage());
			return Mono.just(response);
		});

	}

	public Mono<Map<String, Object>> getDraftShipmentNotice(String partnerId, String shipmentType) {
		logger.info("Executing the method :: getDraftShipmentNotice {}{}", partnerId, shipmentType);
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to retrieve drafts data");
		response.put("status", false);
		response.put("data", new ArrayList<>());

		return shipmentRepository.findByPartnerId(partnerId, shipmentType).collectList().map(draftsShipments -> {
			if (!draftsShipments.isEmpty()) {
				response.put("message", "drafts data retrieved successfully");
				response.put("status", true);
				response.put("data", draftsShipments);
			}
			logger.info("Executed the method ::getDraftShipmentNotice {}{}", partnerId, shipmentType);
			return response;
		}).onErrorResume(ex -> {
			logger.error("Exception in getDraftShipmentNotice ::{}", ex.getMessage());
			return Mono.just(response);
		});
	}

	public Mono<Map<String, Object>> getDraftShipment(String shipmentId, String shipmentType) throws IOException {
		logger.info("Executing the method :: getDraftShipment {}{}", shipmentId, shipmentType);
//		System.out.println(
//				"getDraftShipmet method called with shipmentId: " + shipmentId + " and shipmentType: " + shipmentType);

		Map<String, Object> response = new HashMap<>();

		response.put("message", "Error while fetching data");
		response.put("status", false);
		response.put("data", new HashMap<>());
		return shipmentRepository.findByIdAndShipmentType(shipmentId, shipmentType).collectList()
				.map(draftsShipments -> {
					if (!draftsShipments.isEmpty()) {
						// System.out.println("Inside map(): draftsShipments size: " +
						// draftsShipments.size());

						response.put("message", "drafts data retrieved successfully");
						response.put("status", true);
						response.put("data", draftsShipments);
					}
					logger.info("Executed the method :: getDraftShipment {}{}", shipmentId, shipmentType);
					return response;
				}).onErrorResume(ex -> {
					logger.error("Exception in getDraftShipment ::{}", ex.getMessage());
					return Mono.just(response);
				});

	}

//    public Mono<Map<String, Object>> getShipmentNotice(String shipmentId, String partnerId, String shipmentType)
//            throws IOException {
//        logger.info("Executing the method :: getShipmentNotice {}{}{}", shipmentId, partnerId, shipmentType);
//        Map<String, Object> response = new HashMap<>();
//
//        response.put("message", "Error while fetching data");
//        response.put("status", false);
//        response.put("data", new HashMap<>());
//        try {
//            ShipmentNotice shipmentNotice = shipmentRepository.findByShipmentId(shipmentId, partnerId, shipmentType)
//                    .blockFirst();
//            if (shipmentNotice != null) {
//                response.put("message", "Fetching data Successfully");
//                response.put("status", true);
//                response.put("data", shipmentNotice);
//
//            } else {
//                response.put("message", "data doesn't exists");
//            }
//        } catch (Exception ex) {
//            logger.error("Exception in getShipmentNotice ::{}", ex.getMessage());
//        }
//        logger.info("Executed the method ::getShipmentNotice {}{}{}", shipmentId, partnerId, shipmentType);
//        return Mono.just(response);
//    }

	public Mono<Map<String, Object>> getShipmentNotice(String shipmentId, String partnerId, String shipmentType) {
		logger.info("Executing the method :: getShipmentNotice {}{}{}", shipmentId, partnerId, shipmentType);

		Map<String, Object> errorResponse = new HashMap<>();
		errorResponse.put("message", "Error while fetching data");
		errorResponse.put("status", false);

		return shipmentRepository.findByShipmentId(shipmentId, partnerId, shipmentType).collectList()
				.flatMap(shipmentList -> {
					if (shipmentList.isEmpty()) {
						Map<String, Object> notFoundResponse = new HashMap<>();
						notFoundResponse.put("message", "Data doesn't exist");
						notFoundResponse.put("status", false);
						notFoundResponse.put("data", new ArrayList<>());
						return Mono.just(notFoundResponse);
					} else {
						Map<String, Object> successResponse = new HashMap<>();
						successResponse.put("message", "Fetching data Successfully");
						successResponse.put("status", true);
						successResponse.put("data", shipmentList);
						return Mono.just(successResponse);
					}
				}).onErrorResume(ex -> {
					logger.error("Exception in getShipmentNotice :: {}", ex.getMessage(), ex);
					return Mono.just(errorResponse);
				});
	}

//    public Mono<Map<String, Object>> updateShipments(int id, String shipmentId, ShipmentRequest shipmentRequest) {
//        logger.info("Executing the method :: updateShipments {}", id);
//        Map<String, Object> response = new HashMap<>();
//
//        response.put("message", "Failed to update data into Shipment Notice");
//        response.put("status", false);
//        ShipmentLines shipmentLine = new ShipmentLines();
//        List<ShipmentLines> shipmentLinesList = new ArrayList<>();
//        ShipmentNotice Shipment = shipmentRepository.findById(id).block();
//        if (Shipment.getShipmentId().equalsIgnoreCase(shipmentId)) {
////		 Shipment.setShipmentId(shipmentId);
////		 Shipment.setCreatedDate(LocalDateTime.now());
//            Shipment.setModifiedDate(LocalDateTime.now());
//            Shipment.setTransactionPurpose(shipmentRequest.getTransactionPurpose());
//            Shipment.setAsnNmber(shipmentRequest.getAsnNmber());
//            Shipment.setShipmentDate(shipmentRequest.getShipmentDate());
//            Shipment.setShipmentTime(shipmentRequest.getShipmentTime());
//            Shipment.setShippedDate(shipmentRequest.getShippedDate());
//            Shipment.setEstimatedDeliveryDate(shipmentRequest.getEstimatedDeliveryDate());
//            Shipment.setSupplierName(shipmentRequest.getSupplierName());
//            Shipment.setSupplierNumber(shipmentRequest.getSupplierNumber());
//            Shipment.setShiptoName(shipmentRequest.getShiptoName());
//            Shipment.setShiptoPlantCode(shipmentRequest.getShiptoPlantCode());
//            Shipment.setBillOfLanding(shipmentRequest.getBillOfLanding());
//            Shipment.setShipmentTrackingNumber(shipmentRequest.getShipmentTrackingNumber());
//            Shipment.setMeansOfTransport(shipmentRequest.getMeansOfTransport());
//            Shipment.setPoNumber(shipmentRequest.getPoNumber());
//            Shipment.setPoDate(shipmentRequest.getPoDate());
//            Shipment.setCreatedBy(shipmentRequest.getCreatedBy());
//            Shipment.setModifiedBy(shipmentRequest.getModifiedBy());
//            Shipment.setOrderNumber(shipmentRequest.getOrderNumber());
//            Shipment.setPartnerId(shipmentRequest.getPartnerId());
//            Shipment.setStatus(shipmentRequest.getStatus());
//            Shipment.setTransactionType(shipmentRequest.getTransactionType());
//
//        }
//        ShipmentNotice shipmentNotice = shipmentRepository.save(Shipment).block();
//
////	 shipmentLinesRepository.findLinesByshipmentId(shipmentId);
//
//        for (ShipmentLines shipmentsLines : shipmentRequest.getShipmentLines()) {
//            ShipmentLines ShipmentLinesDate = shipmentLinesRepository.findById(shipmentsLines.getId()).block();
//            if (ShipmentLinesDate.getShipmentId().equalsIgnoreCase(shipmentId)) {
//                ShipmentLinesDate.setOrderNumber(shipmentsLines.getOrderNumber());
//                ShipmentLinesDate.setBuyerNumber(shipmentsLines.getBuyerNumber());
//                // ShipmentLinesDate.setLineItem(shipmentsLines.getLineItem());
//                // ShipmentLinesDate.setShiptoName(shipmentsLines.getShiptoName());
//                // ShipmentLinesDate.setShiptoCode(shipmentsLines.getShiptoCode());
//                ShipmentLinesDate.setQty(shipmentsLines.getQty());
//                ShipmentLinesDate.setUom(shipmentsLines.getUom());
//                ShipmentLinesDate.setPoLineNumber(shipmentsLines.getPoLineNumber());
//                ShipmentLinesDate.setPartnerId(shipmentsLines.getPartnerId());
//                ShipmentLinesDate.setStatus(shipmentsLines.getStatus());
//                ShipmentLinesDate.setOrderType(shipmentsLines.getOrderType());
//                ShipmentLinesDate.setItemDescription(shipmentsLines.getItemDescription());
//                ShipmentLinesDate.setVendorPartnumber(shipmentsLines.getVendorPartnumber());
//
//            }
//            shipmentLine = shipmentLinesRepository.save(ShipmentLinesDate).block();
//            shipmentLinesList.add(shipmentLine);
//        }
//        System.out.println(shipmentRequest.getStatus());
//        // Only execute this loop when the status is "submitted"
//        if ("Submit".equalsIgnoreCase(shipmentRequest.getStatus()) || "Partial_Shipment".equalsIgnoreCase(shipmentRequest.getStatus())) {
//            for (ShipmentLines shipments : shipmentRequest.getShipmentLines()) {
//                orderDetailsRepository.findById(shipments.getOrderId()).flatMap(orderDetails -> {
//                    int newRemainingQty = Math.max(0, orderDetails.getRemainingQty() - shipments.getQty());
//                    orderDetails.setRemainingQty(newRemainingQty);
//                    // Print before saving
//                    System.out.println("Updating Order ID: " + orderDetails.getId() + ", Old Remaining Qty: "
//                            + orderDetails.getRemainingQty() + ", New Remaining Qty: " + newRemainingQty);
//                    // Now return the result of update, which is a Mono
//                    return orderDetailsRepository.update(newRemainingQty, shipments.getOrderId())
//                            .then(Mono.just(orderDetails)); // Make sure to return a Mono
//                }).subscribe(); // Ensure execution of the reactive stream
//            }
//        }
//
//        if (shipmentNotice != null) {
//            response.put("data", shipmentNotice);
//            response.put("lines", shipmentLinesList);
//            response.put("message", "Data saved into shipmentNotice successfully");
//            response.put("status", true);
//
//        }
//        logger.info("Executed the method :: updateShipments {}", id);
//        return Mono.just(response);
//
//    }

	public Mono<Map<String, Object>> updateShipments(int id, String shipmentId, ShipmentRequest shipmentRequest) {
		logger.info("Executing the method :: updateShipments {}", id);

		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to update data into Shipment Notice");
		response.put("status", false);

		return shipmentRepository.findById(id)
				.switchIfEmpty(Mono.error(new RuntimeException("Shipment Notice not found for ID: " + id)))
				.flatMap(existingShipment -> {
					if (existingShipment.getShipmentId().equalsIgnoreCase(shipmentId)) {
						existingShipment.setModifiedDate(LocalDateTime.now());
						existingShipment.setTransactionPurpose(shipmentRequest.getTransactionPurpose());
						existingShipment.setAsnNmber(shipmentRequest.getAsnNmber());
						existingShipment.setShipmentDate(shipmentRequest.getShipmentDate());
						existingShipment.setShipmentTime(shipmentRequest.getShipmentTime());
						existingShipment.setShippedDate(shipmentRequest.getShippedDate());
						existingShipment.setEstimatedDeliveryDate(shipmentRequest.getEstimatedDeliveryDate());
						existingShipment.setSupplierName(shipmentRequest.getSupplierName());
						existingShipment.setSupplierNumber(shipmentRequest.getSupplierNumber());
						existingShipment.setShiptoName(shipmentRequest.getShiptoName());
						existingShipment.setShiptoPlantCode(shipmentRequest.getShiptoPlantCode());
						existingShipment.setBillOfLanding(shipmentRequest.getBillOfLanding());
						existingShipment.setShipmentTrackingNumber(shipmentRequest.getShipmentTrackingNumber());
						existingShipment.setMeansOfTransport(shipmentRequest.getMeansOfTransport());
						existingShipment.setPoNumber(shipmentRequest.getPoNumber());
						existingShipment.setPoDate(shipmentRequest.getPoDate());
						existingShipment.setCreatedBy(shipmentRequest.getCreatedBy()); // Consider if createdBy should
																						// be updated
						existingShipment.setModifiedBy(shipmentRequest.getModifiedBy());
						existingShipment.setOrderNumber(shipmentRequest.getOrderNumber());
						existingShipment.setPartnerId(shipmentRequest.getPartnerId());
						existingShipment.setStatus(shipmentRequest.getStatus());
						existingShipment.setTransactionType(shipmentRequest.getTransactionType());
						existingShipment.setPartialShipFlag(shipmentRequest.getPartialShipFlag()); // Added based on
																									// sendShipmentNotice
					} else {
						// If shipmentId doesn't match, you might want to throw an error or handle
						// differently
						return Mono.error(new IllegalArgumentException("Shipment ID mismatch for update."));
					}
					// Save the updated ShipmentNotice reactively
					return shipmentRepository.save(existingShipment);
				}).flatMap(updatedShipmentNotice -> {
					// 2. Process and update ShipmentLines for each item
					List<Mono<ShipmentLines>> shipmentLinesUpdateMonos = new ArrayList<>();
					for (ShipmentLines shipmentLineRequest : shipmentRequest.getShipmentLines()) {
						Mono<ShipmentLines> lineUpdateMono = shipmentLinesRepository
								.findById(shipmentLineRequest.getId())
								.switchIfEmpty(Mono.error(new RuntimeException(
										"Shipment Line not found for ID: " + shipmentLineRequest.getId())))
								.flatMap(existingShipmentLineData -> {
									if (existingShipmentLineData.getShipmentId().equalsIgnoreCase(shipmentId)) {
										existingShipmentLineData.setOrderNumber(shipmentLineRequest.getOrderNumber());
										existingShipmentLineData.setBuyerNumber(shipmentLineRequest.getBuyerNumber());
										existingShipmentLineData.setQty(shipmentLineRequest.getQty());
										existingShipmentLineData.setUom(shipmentLineRequest.getUom());
										existingShipmentLineData.setPoLineNumber(shipmentLineRequest.getPoLineNumber());
										existingShipmentLineData.setPartnerId(shipmentLineRequest.getPartnerId());
										existingShipmentLineData.setStatus(shipmentLineRequest.getStatus());
										existingShipmentLineData.setOrderType(shipmentLineRequest.getOrderType());
										existingShipmentLineData
												.setItemDescription(shipmentLineRequest.getItemDescription());
										existingShipmentLineData
												.setVendorPartnumber(shipmentLineRequest.getVendorPartnumber());
										// Add fields from request if they exist in the model and are being updated
										existingShipmentLineData.setPriceUnit(shipmentLineRequest.getPriceUnit());
										existingShipmentLineData
												.setOriginalOrderQty(shipmentLineRequest.getOriginalOrderQty());
										existingShipmentLineData.setOrderId(shipmentLineRequest.getOrderId());
										existingShipmentLineData.setUnitPrice(shipmentLineRequest.getUnitPrice());
									} else {
										return Mono.error(
												new IllegalArgumentException("Shipment line ID mismatch for update."));
									}
									return shipmentLinesRepository.save(existingShipmentLineData);
								});
						shipmentLinesUpdateMonos.add(lineUpdateMono);
					}

					// Merge all ShipmentLines update Monos and collect their results into a list
					return Flux.merge(shipmentLinesUpdateMonos).collectList().flatMap(updatedShipmentLinesList -> {
						// 3. Conditionally update OrderDetails based on shipment status
						System.out.println(shipmentRequest.getStatus()); // Original print preserved
						if ("Submit".equalsIgnoreCase(shipmentRequest.getStatus())
								|| "Partial_Shipment".equalsIgnoreCase(shipmentRequest.getStatus())) {

							List<Mono<Void>> orderDetailsUpdateMonos = new ArrayList<>();
							for (ShipmentLines shipmentLineRequest : shipmentRequest.getShipmentLines()) {
								Mono<Void> updateMono = orderDetailsRepository
										.findById(shipmentLineRequest.getOrderId())
										.switchIfEmpty(Mono.error(new RuntimeException(
												"Order details not found for ID: " + shipmentLineRequest.getOrderId())))
										.flatMap(orderDetails -> {
											int newRemainingQty = Math.max(0,
													orderDetails.getRemainingQty() - shipmentLineRequest.getQty());
											orderDetails.setRemainingQty(newRemainingQty); // Update the entity
											System.out.println("Updating Order ID: " + orderDetails.getId()
													+ ", Old Remaining Qty: " + orderDetails.getRemainingQty()
													+ ", New Remaining Qty: " + newRemainingQty);

											// Now return the result of update. Using .then() to signal completion.
											return orderDetailsRepository
													.update(newRemainingQty, shipmentLineRequest.getOrderId()).then(); // Signals
																														// completion
																														// without
																														// emitting
																														// a
																														// value
										});
								orderDetailsUpdateMonos.add(updateMono);
							}
							// Wait for all order details updates to complete, then pass the updated lines
							// along
							return Flux.merge(orderDetailsUpdateMonos).then(Mono.just(updatedShipmentLinesList)); // Pass
																													// saved
																													// lines
																													// to
																													// the
																													// final
																													// step
						} else {
							// If status condition is not met, directly proceed with the updated shipment
							// lines
							return Mono.just(updatedShipmentLinesList);
						}
					}).map(finalShipmentLinesList -> {
						// 4. Construct the final success response map
						response.put("data", updatedShipmentNotice);
						response.put("lines", finalShipmentLinesList);
						response.put("message", "Data saved into shipmentNotice successfully");
						response.put("status", true);
						return response;
					});
				}).onErrorResume(e -> {
					logger.error("Exception in updateShipments :: {}", e.getMessage());
					return Mono.just(response);
				}).doFinally(signalType -> {
					logger.info("Executed the method :: updateShipments {} - Finished with signal: {}", id, signalType);
				});
	}

	public Mono<Map<String, Object>> b2bSendShipment(ShipmentRequest shipment) throws IOException {
		logger.info("Executing the method :: b2bSendShipment ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Error while getting data from bpdurl");
		try {
			final String uriForWithoutSsl = shipment.getBpdLink();
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);
			final StringBuilder xml = new StringBuilder();
			xml.append("<LookUpDetails> <shipmentId>").append(shipment.getShipmentId())
					.append("</shipmentId><partnerId>").append(shipment.getPartnerId()).append("</partnerId>")
					.append("</LookUpDetails>");
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
		} catch (final Exception e) {
			logger.error("Exception in b2bSendShipment ::{}", e.getMessage());
			response.put("success", false);
			response.put("message", "Request Failed to B2B. Please Contact Administrator:::" + e.getMessage());
		}
		logger.info("Executed the method :: saveCustomer ");
		return Mono.just(response);
	}

	public Flux<ShipmentNotice> getBillOfLanding(int orderNumber, String transactionType) {
		logger.info("Executing the method :: getBillOfLanding");

		return shipmentRepository.findByOrderNumber1(orderNumber, transactionType)
				.doOnComplete(() -> logger.info("Executed the method :: getBillOfLanding"))
				.switchIfEmpty(Flux.defer(() -> {
					logger.info("Executed the method :: getBillOfLanding empty : {} {}", orderNumber, transactionType);
					return Flux.empty();
				})).onErrorResume(ex -> {
					logger.error("Exception in method getBillOfLanding :: {}", ex.getMessage(), ex);
					return Flux.empty();
				});
	}

	public Flux<ShipmentLines> getPendingQuantity(int orderNumber) {
		logger.info("Executing the method :: getPendingQuantity");

		return shipmentLinesRepository.findByOrderNumber(orderNumber)
				.doOnComplete(() -> logger.info("Executed the method :: getPendingQuantity"))
				.switchIfEmpty(Flux.defer(() -> {
					logger.info("Executed the method :: getPendingQuantity: {}", orderNumber);
					return Flux.empty();
				})).onErrorResume(ex -> {
					logger.error("Exception in method getPendingQuantity :: {}", ex.getMessage(), ex);
					return Flux.empty();
				});
	}

	public Flux<ShipmentLineDto> getBillOfLading(String billOfLading, String orderNumber, String transactionType) {
		logger.info("Executing the method :: getBillOfLading");

		return shipmentLinesRepository.getBillOfLading(orderNumber, billOfLading, transactionType)
				.doOnComplete(() -> logger.info("Executed the method :: getBillOfLading"))
				.switchIfEmpty(Flux.defer(() -> {
					logger.info("No shipment lines found for billOfLading: {}, orderNumber: {}, transactionType: {}",
							billOfLading, orderNumber, transactionType);
					return Flux.empty();
				})).onErrorResume(ex -> {
					logger.error("Exception in method getBillOfLading :: {}", ex.getMessage(), ex);
					return Flux.empty();
				});
	}

	public Mono<Map<String, Object>> getShipmentOrderDetails(String correlationKey, String orderType, String status) {
		Flux<OrderDetails> orderDetailsFlux;

		if ("Read".equalsIgnoreCase(status) || "Unread".equalsIgnoreCase(status)
				|| "Completed".equalsIgnoreCase(status)) {

			// Include all line items (including cancelled)
			orderDetailsFlux = orderDetailsRepository.findAllByCorrelationKeyAndOrderType(correlationKey, orderType);
			logger.info("Fetching all order details including cancelled for correlationKey={} and orderType={}",
					correlationKey, orderType);
			System.out.println(orderDetailsFlux.toString());
		} else {
			// Exclude cancelled line items
			orderDetailsFlux = orderDetailsRepository.findNonCancelledByCorrelationKeyAndOrderType(correlationKey,
					orderType);
			logger.info("Fetching order details excluding cancelled for correlationKey={} and orderType={}",
					correlationKey, orderType);
		}

		return orderDetailsFlux.collectList().map(orderDetails -> {
			Map<String, Object> response = new HashMap<>();
			if (orderDetails.isEmpty()) {
				response.put("message", "No order details found");
				response.put("status", false);
				response.put("data", Collections.emptyList());
				logger.warn("No order details found for correlationKey={} and orderType={}", correlationKey, orderType);
			} else {
				response.put("message", "Order details retrieved successfully");
				response.put("status", true);
				response.put("data", orderDetails);
				logger.info("Order details retrieved successfully for correlationKey={} and orderType={}",
						correlationKey, orderType);
			}
			return response;
		}).onErrorResume(ex -> {
			logger.error("Exception occurred while fetching order details: {}", ex.getMessage(), ex);
			Map<String, Object> errorResponse = new HashMap<>();
			errorResponse.put("message", "Exception occurred: " + ex.getMessage());
			errorResponse.put("status", false);
			errorResponse.put("data", Collections.emptyList());
			return Mono.just(errorResponse);
		});
	}

}
