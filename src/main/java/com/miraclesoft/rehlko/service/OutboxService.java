package com.miraclesoft.rehlko.service;

import com.miraclesoft.rehlko.entity.ChangeRequest;
import com.miraclesoft.rehlko.entity.ChangeRequestData;
import com.miraclesoft.rehlko.entity.ChangeRequestLines;
import com.miraclesoft.rehlko.entity.Outbox;
import com.miraclesoft.rehlko.repository.ChangeRequestLinesRepository;
import com.miraclesoft.rehlko.repository.ChangeRequestRepository;
import com.miraclesoft.rehlko.repository.ConfigurationsRepository;
import com.miraclesoft.rehlko.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.core.async.AsyncResponseTransformer;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.NoSuchKeyException;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class OutboxService {
	private static final Logger logger = LoggerFactory.getLogger(OutboxService.class);
	private final OutboxRepository outboxRepository;
	private final ChangeRequestRepository changeRequestRepository;
	private final ChangeRequestLinesRepository changeRequestLinesRepository;
	@Autowired
	private ConfigurationsRepository configurationsRepository;

	public OutboxService(OutboxRepository outboxRepository, ChangeRequestRepository changeRequestRepository,
			ChangeRequestLinesRepository changeRequestLinesRepository) {
		this.outboxRepository = outboxRepository;
		this.changeRequestRepository = changeRequestRepository;
		this.changeRequestLinesRepository = changeRequestLinesRepository;
	}

	public Mono<Map<String, Object>> getOutBoxData(String partnerId) {
		logger.info("Executing the method :: getOutBoxData ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to retrieve outbox data");
		response.put("status", false);
		response.put("data", new ArrayList<>());
		return outboxRepository.findByPartnerId(partnerId).collectList().map(outboxList -> {
			if (!outboxList.isEmpty()) {
				response.put("message", "Outbox data retrieved successfully");
				response.put("status", true);
				response.put("data", outboxList);
			}
			logger.info("Executed the method :: getOutBoxData ");
			return response;
		}).onErrorResume(ex -> {
			logger.error(" getOutBoxData :: {}", ex.getMessage());
			return Mono.just(response);
		});
	}

//	public Mono<Map<String, Object>> saveToOutbox(String type, Outbox outbox) {
//		logger.info("Executing the method :: saveToOutbox ");
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Failed to save data into outbox");
//		response.put("status", false);
//		try {
//			String correlationKey = outbox.getCorrelationKey1();
//			outbox.setRecievedDate(LocalDateTime.now());
//			outbox.setFileInterchangeControlNumber("");
//			outbox.setFileLocation("");
//			outbox.setFileName("");
//			outbox.setShiptoCode(outbox.getShiptoCode());
//			outbox.setShiptoName(outbox.getShiptoName());
//			outbox.setSupplierCode(outbox.getSupplierCode());
//			outbox.setSupplierName(outbox.getSupplierName());
//			outbox.setPlantCode(outbox.getPlantCode());
//			outbox.setBilltoCode(outbox.getBilltoCode());
//			outbox.setBilltoName(outbox.getBilltoName());
//			outbox.setSalesTerms(outbox.getSalesTerms());
//			outbox.setComments(outbox.getComments());
//			outbox.setRecieptNumber(outbox.getRecieptNumber());
//			outbox.setDivisionId(outbox.getDivisionId());
//			outbox.setDivisionName(outbox.getDivisionName());
//			outbox.setGroupControlNumber(outbox.getGroupControlNumber());
//			outbox.setFakStatus(outbox.getFakStatus());
//			outbox.setFunctionaGroupCode(outbox.getFunctionaGroupCode());
//			outbox.setTotalLineItems(outbox.getTotalLineItems());
//			outbox.setPartialShipFlag(outbox.getPartialShipFlag());
//			outbox.setTransactionType(outbox.getTransactionType());
//			if (type.equalsIgnoreCase("acceptOrder")) {
//				Random rnd = new Random();
//				int number = rnd.nextInt(999999);
//				String random = String.format("%06d", number);
//				outbox.setCorrelationKey3(random + "_" + correlationKey);
//				outbox.setCorrelation_key_name1("Purchase Order");
//				outbox.setTransactionName("Purchase Order Ack");
//				//outbox.setTransactionType("855");
//			}
//			if (type.equalsIgnoreCase("changeRequest")) {
//				Random rnd = new Random();
//				int number = rnd.nextInt(999999);
//				String random = String.format("%06d", number);
//				outbox.setCorrelationKey3("CR_" + random + "_" + correlationKey);
//				outbox.setCorrelation_key_name1("Change Request");
//				outbox.setTransactionName("PO Change Request Ack");
//				//outbox.setTransactionType("855");
//			}
//			if (type.equalsIgnoreCase("reject")) {
//				Random rnd = new Random();
//				int number = rnd.nextInt(999999);
//				String random = String.format("%06d", number);
//				outbox.setCorrelationKey3("CR_" + random + "_" + correlationKey);
//				outbox.setCorrelation_key_name1("Reject Request");
//				outbox.setTransactionName("PO Reject Order Ack");
//				//outbox.setTransactionType("855");
//			}
//			if (type.equalsIgnoreCase("poChangeOrder")) {
//				Random rnd = new Random();
//				int number = rnd.nextInt(999999);
//				String random = String.format("%06d", number);
//				outbox.setCorrelationKey3(random + "_" + correlationKey);
//	            outbox.setCorrelation_key_name1("PO Change Order");
//	            outbox.setTransactionName("PO Change Order Ack");
//	            //outbox.setTransactionType("855-860");
//	        }
//			outbox = outboxRepository.save(outbox).block();
//			if (outbox != null) {
//				response.put("data", outbox);
//				response.put("message", "Data saved into outbox successfully");
//				response.put("status", true);
//			}
//			logger.info("Executed the method :: saveToOutbox ");
//		} catch (Exception ex) {
//			logger.error( " saveToOutbox :: {}", ex.getMessage());
//		}
//		return Mono.just(response);
//	}

	public Mono<Map<String, Object>> saveToOutbox(String type, Outbox outbox) {
		logger.info("Executing the method :: saveToOutbox ");
		Map<String, Object> defaultSaveErrorResponse = new HashMap<>();
		defaultSaveErrorResponse.put("message", "Failed to save data into outbox");
		defaultSaveErrorResponse.put("status", false);
		defaultSaveErrorResponse.put("data", new HashMap<>());

		outbox.setRecievedDate(LocalDateTime.now());
		outbox.setFileInterchangeControlNumber("");
		outbox.setFileLocation("");
		outbox.setFileName("");

		String correlationKey = outbox.getCorrelationKey1();
		Random rnd = new Random();
		int number = rnd.nextInt(999999);
		String random = String.format("%06d", number);

		// Apply type-specific logic using a switch statement for clarity
		switch (type.toLowerCase()) {
		case "acceptorder":
			outbox.setCorrelationKey3(random + "_" + correlationKey);
			outbox.setCorrelation_key_name1("Purchase Order");
			outbox.setTransactionName("Purchase Order Ack");
			// outbox.setTransactionType("855"); // Uncomment if needed
			break;
		case "changerequest":
			outbox.setCorrelationKey3("CR_" + random + "_" + correlationKey);
			outbox.setCorrelation_key_name1("Change Request");
			outbox.setTransactionName("PO Change Request Ack");
			// outbox.setTransactionType("855");
			break;
		case "reject":
			outbox.setCorrelationKey3("CR_" + random + "_" + correlationKey);
			outbox.setCorrelation_key_name1("Reject Request");
			outbox.setTransactionName("PO Reject Order Ack");
			// outbox.setTransactionType("855");
			break;
		case "pochangeorder":
			outbox.setCorrelationKey3(random + "_" + correlationKey);
			outbox.setCorrelation_key_name1("PO Change Order");
			outbox.setTransactionName("PO Change Order Ack");
			// outbox.setTransactionType("855-860");
			break;
		default:
			logger.warn("Unknown type '{}' provided for saveToOutbox. Defaulting to generic settings.", type);
			break;
		}

		return outboxRepository.save(outbox).map(savedOutbox -> {
			logger.info("Data saved into outbox successfully for CorrelationKey3: {}",
					savedOutbox.getCorrelationKey3());
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Data saved into outbox successfully");
			successResponse.put("status", true);
			successResponse.put("data", savedOutbox);
			return successResponse;
		}).doOnSuccess(res -> logger.info("Executed the method :: saveToOutbox ")).onErrorResume(Exception.class,
				ex -> {
					logger.error(" Error saveToOutbox :: {}", ex.getMessage());
					return Mono.just(defaultSaveErrorResponse);
				});
	}

//	public Mono<Map<String, Object>> saveShipmentsToOutbox(String type, String asnNumber, Outbox outbox) {
//		logger.info("Executing the method :: saveShipmentsToOutbox ");
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Failed to save data into outbox");
//		response.put("status", false);
//		try {
//			outbox.setRecievedDate(LocalDateTime.now());
//			outbox.setFileLocation("");
//			outbox.setFileName("");
//			outbox.setFileInterchangeControlNumber("");
//			outbox.setShiptoCode(outbox.getShiptoCode());
//			outbox.setShiptoName(outbox.getShiptoName());
//			outbox.setSupplierCode(outbox.getSupplierCode());
//			outbox.setSupplierName(outbox.getSupplierName());
//			outbox.setPlantCode(outbox.getPlantCode());
//			outbox.setBilltoCode(outbox.getBilltoCode());
//			outbox.setBilltoName(outbox.getBilltoName());
//			outbox.setSalesTerms(outbox.getSalesTerms());
//			outbox.setComments(outbox.getComments());
//			outbox.setRecieptNumber(outbox.getRecieptNumber());
//			outbox.setDivisionId(outbox.getDivisionId());
//			outbox.setDivisionName(outbox.getDivisionName());
//			outbox.setGroupControlNumber(outbox.getGroupControlNumber());
//			outbox.setFakStatus(outbox.getFakStatus());
//			outbox.setFunctionaGroupCode(outbox.getFunctionaGroupCode());
//			outbox.setPartialShipFlag(outbox.getPartialShipFlag());
//			outbox.setTransactionType(outbox.getTransactionType());
////	    		outbox.setStatus("Submitted");
//			if (type.equalsIgnoreCase("shipment")) {
//				outbox.setCorrelationKey3(asnNumber);
//				outbox.setCorrelation_key_name1("Purchase Order");
//				outbox.setCorrelation_key_name3("ASN");
//				outbox.setTransactionName("ASN");
//				//outbox.setTransactionType("856");
//			}
//			if (type.equalsIgnoreCase("invoice")) {
//				outbox.setCorrelationKey3(asnNumber);
//				outbox.setTransactionName("Invoice");
//				outbox.setCorrelation_key_name1("Purchase Order");
//				outbox.setCorrelation_key_name3("Invoice");
//				//outbox.setTransactionType("810");
//			}
//			outbox = outboxRepository.save(outbox).block();
//			if (outbox != null) {
//				response.put("data", outbox);
//				response.put("message", "Data saved into outbox successfully");
//				response.put("status", true);
//			}
//			logger.info("Executed the method :: saveShipmentsToOutbox ");
//		} catch (Exception ex) {
//			logger.error( " saveShipmentsToOutbox :: {}", ex.getMessage());
//		}
//		return Mono.just(response);
//	}

	public Mono<Map<String, Object>> saveShipmentsToOutbox(String type, String asnNumber, Outbox outbox) {
		logger.info("Executing the method :: saveShipmentsToOutbox ");

		Map<String, Object> defaultSaveErrorResponse = new HashMap<>();
		defaultSaveErrorResponse.put("message", "Failed to save data into outbox");
		defaultSaveErrorResponse.put("status", false);
		defaultSaveErrorResponse.put("data", new HashMap<>());

		outbox.setRecievedDate(LocalDateTime.now());
		outbox.setFileLocation("");
		outbox.setFileName("");
		outbox.setFileInterchangeControlNumber("");

		switch (type.toLowerCase()) {
		case "shipment":
			outbox.setCorrelationKey3(asnNumber);
			outbox.setCorrelation_key_name1("Purchase Order");
			outbox.setCorrelation_key_name3("ASN");
			outbox.setTransactionName("ASN");
			// outbox.setTransactionType("856");
			break;
		case "invoice":
			outbox.setCorrelationKey3(asnNumber);
			outbox.setTransactionName("Invoice");
			outbox.setCorrelation_key_name1("Purchase Order");
			outbox.setCorrelation_key_name3("Invoice");
			// outbox.setTransactionType("810");
			break;
		default:
			logger.warn("Unknown type '{}' provided for saveShipmentsToOutbox. Defaulting to generic settings.", type);
			break;
		}

		return outboxRepository.save(outbox).map(savedOutbox -> {
			logger.info("Data saved into outbox successfully for ASN Number: {}", savedOutbox.getCorrelationKey3());
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Data saved into outbox successfully");
			successResponse.put("status", true);
			successResponse.put("data", savedOutbox);
			return successResponse;
		}).doOnSuccess(res -> logger.info("Executed the method :: saveShipmentsToOutbox "))
				.onErrorResume(Exception.class, ex -> {
					logger.error(" Error saveShipmentsToOutbox :: {}", ex.getMessage());
					return Mono.just(defaultSaveErrorResponse);
				});
	}

//	public Mono<Map<String, Object>> getOutBox(int id) {
//		logger.info("Executing the method :: getOutBox ");
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Error while fetching data");
//		response.put("status", false);
//		response.put("data", new HashMap<>());
//		try {
//			Outbox outbox = outboxRepository.findById(id).block();
//			if (outbox != null) {
//				response.put("message", "Fetching data Successfully");
//				response.put("status", true);
//				response.put("data", outbox);
//			} else {
//				response.put("message", "data doesn't exists");
//			}
//			logger.info("Executed the method :: getOutBox ");
//		} catch (Exception ex) {
//			logger.error( " getOutBox :: {}", ex.getMessage());
//		}
//		return Mono.just(response);
//	}

	public Mono<Map<String, Object>> getOutBox(int id) {
		logger.info("Executing the method :: getOutBox ");
		Map<String, Object> defaultFetchErrorResponse = new HashMap<>();
		defaultFetchErrorResponse.put("message", "Error while fetching data");
		defaultFetchErrorResponse.put("status", false);
		defaultFetchErrorResponse.put("data", new HashMap<>());

		return outboxRepository.findById(id).map(outbox -> {
			logger.info("Fetching data Successfully for ID: {}", id);
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Fetching data Successfully");
			successResponse.put("status", true);
			successResponse.put("data", outbox);
			return successResponse;
		}).switchIfEmpty(Mono.defer(() -> {
			logger.warn("Data for ID {} doesn't exist.", id);
			return Mono.just(Map.of("message", "data doesn't exists", "status", false, "data", new HashMap<>()));
		})).doOnSuccess(res -> logger.info("Executed the method :: getOutBox ")).onErrorResume(Exception.class, ex -> {
			logger.error(" Error getOutBox :: {}", ex.getMessage());
			return Mono.just(defaultFetchErrorResponse);
		});
	}

//	public Mono<Map<String, Object>> saveChangeRequest(String type, ChangeRequest changeRequest) {
//		logger.info("Executing the method :: saveChangeRequest ");
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Failed to save data into Shipment Notice");
//		response.put("status", false);
//		try {
//			String orderId = UUID.randomUUID().toString();
//			ChangeRequestData changeRequestData = new ChangeRequestData();
//			ChangeRequestLines shipmentLine = new ChangeRequestLines();
//			List<ChangeRequestLines> changeRequestLinesList = new ArrayList<>();
//			changeRequestData.setOrderId(orderId);
//			changeRequestData.setCreatedDate(LocalDateTime.now());
//			changeRequestData.setModifiedDate(LocalDateTime.now());
//			changeRequestData.setComments(changeRequest.getComments());
//			changeRequestData.setCorrelationKey1(changeRequest.getCorrelationKey1());
//			changeRequestData.setCorrelationKey2(changeRequest.getCorrelationKey2());
//			changeRequestData.setCorrelationKey3(changeRequest.getCorrelationKey3());
//			changeRequestData.setCorrelationKey4(changeRequest.getCorrelationKey4());
//			changeRequestData.setCreatedBy(changeRequest.getCreatedBy());
//			changeRequestData.setModifiedBy(changeRequest.getModifiedBy());
//			changeRequestData.setOrderNumber(changeRequest.getOrderNumber());
//			changeRequestData.setPartnerId(changeRequest.getPartnerId());
//			changeRequestData.setPartnerName(changeRequest.getPartnerName());
//			changeRequestData.setStatus(changeRequest.getStatus());
//			changeRequestData.setDivisionId(changeRequest.getDivisionId());
//			changeRequestData.setDivisionName(changeRequest.getDivisionName());
//			changeRequestData.setOrderAmount(changeRequest.getOrderAmount());
//			changeRequestData.setFaFileLocation(changeRequest.getFaFileLocation());
//			changeRequestData.setFaFileName(changeRequest.getFaFileName());
//			changeRequestData.setOrderQty(changeRequest.getOrderQty());
//			changeRequestData.setSupplierCode(changeRequest.getSupplierCode());
//			changeRequestData.setSupplierName(changeRequest.getSupplierName());
//			changeRequestData.setPlantCode(changeRequest.getPlantCode());
//			changeRequestData.setOrderStatus(changeRequest.getOrderStatus());
//			changeRequestData.setSalesTerms(changeRequest.getSalesTerms());
//			changeRequestData.setBilltoCode(changeRequest.getBilltoCode());
//			changeRequestData.setShiptoName(changeRequest.getShiptoName());
//			changeRequestData.setShiptoCode(changeRequest.getShiptoCode());
//			changeRequestData.setReceiptNumber(changeRequest.getReceiptNumber());
//			changeRequestData.setTotalLineItems(changeRequest.getTotalLineItems());
//			changeRequestData = changeRequestRepository.save(changeRequestData).block();
//			for (ChangeRequestLines change : changeRequest.getChangeRequestLines()) {
//				ChangeRequestLines changeLines = new ChangeRequestLines();
//				changeLines.setOrderId(orderId);
//				changeLines.setBuyerNumber(change.getBuyerNumber());
//				changeLines.setQty(change.getQty());
//				changeLines.setUom(change.getUom());
//				changeLines.setPoLineNumber(change.getPoLineNumber());
//				changeLines.setUnitPrice(change.getUnitPrice());
//				changeLines.setStatus(change.getStatus());
//				changeLines.setOrderNumber(change.getOrderNumber());
//				changeLines.setOrderStatus(change.getOrderStatus());
//				changeLines.setComments(change.getComments());
//				changeLines.setItemDescription(change.getItemDescription());
//				changeLines.setVendorPartnumber(change.getVendorPartnumber());
//				changeLines.setShipDate(change.getShipDate());
//				changeLines.setPriceUnit(change.getPriceUnit());
//				changeLines.setPoLineItemStatus(change.getPoLineItemStatus());
//				shipmentLine = changeRequestLinesRepository.save(changeLines).block();
//				changeRequestLinesList.add(shipmentLine);
//			}
//			if (changeRequestData != null) {
//				response.put("data", changeRequestData);
//				response.put("lines", changeRequestLinesList);
//				response.put("message", "Data saved into order ack successfully");
//				response.put("status", true);
//			}
//			logger.info("Executed the method :: saveChangeRequest ");
//		} catch (Exception ex) {
//			logger.error( " saveChangeRequest :: {}", ex.getMessage());
//		}
//		return Mono.just(response);
//	}

	public Mono<Map<String, Object>> saveChangeRequest(String type, ChangeRequest changeRequest) {
		logger.info("Executing the method :: saveChangeRequest ");

		Map<String, Object> defaultSaveErrorResponse = new HashMap<>();
		defaultSaveErrorResponse.put("message", "Failed to save data into Change Request");
		defaultSaveErrorResponse.put("status", false);
		defaultSaveErrorResponse.put("data", new HashMap<>());

		String orderId = UUID.randomUUID().toString();

		ChangeRequestData changeRequestData = new ChangeRequestData();
		changeRequestData.setOrderId(orderId);
		changeRequestData.setCreatedDate(LocalDateTime.now());
		changeRequestData.setModifiedDate(LocalDateTime.now());
		changeRequestData.setComments(changeRequest.getComments());
		changeRequestData.setCorrelationKey1(changeRequest.getCorrelationKey1());
		changeRequestData.setCorrelationKey2(changeRequest.getCorrelationKey2());
		changeRequestData.setCorrelationKey3(changeRequest.getCorrelationKey3());
		changeRequestData.setCorrelationKey4(changeRequest.getCorrelationKey4());
		changeRequestData.setCreatedBy(changeRequest.getCreatedBy());
		changeRequestData.setModifiedBy(changeRequest.getModifiedBy());
		changeRequestData.setOrderNumber(changeRequest.getOrderNumber());
		changeRequestData.setPartnerId(changeRequest.getPartnerId());
		changeRequestData.setPartnerName(changeRequest.getPartnerName());
		changeRequestData.setStatus(changeRequest.getStatus());
		changeRequestData.setDivisionId(changeRequest.getDivisionId());
		changeRequestData.setDivisionName(changeRequest.getDivisionName());
		changeRequestData.setOrderAmount(changeRequest.getOrderAmount());
		changeRequestData.setFaFileLocation(changeRequest.getFaFileLocation());
		changeRequestData.setFaFileName(changeRequest.getFaFileName());
		changeRequestData.setOrderQty(changeRequest.getOrderQty());
		changeRequestData.setSupplierCode(changeRequest.getSupplierCode());
		changeRequestData.setSupplierName(changeRequest.getSupplierName());
		changeRequestData.setPlantCode(changeRequest.getPlantCode());
		changeRequestData.setOrderStatus(changeRequest.getOrderStatus());
		changeRequestData.setSalesTerms(changeRequest.getSalesTerms());
		changeRequestData.setBilltoCode(changeRequest.getBilltoCode());
		changeRequestData.setShiptoName(changeRequest.getShiptoName());
		changeRequestData.setShiptoCode(changeRequest.getShiptoCode());
		changeRequestData.setReceiptNumber(changeRequest.getReceiptNumber());
		changeRequestData.setTotalLineItems(changeRequest.getTotalLineItems());

		return changeRequestRepository.save(changeRequestData).flatMap(savedChangeRequestData -> {
			return Flux.fromIterable(changeRequest.getChangeRequestLines()).flatMap(change -> {

				ChangeRequestLines changeLines = new ChangeRequestLines();
				changeLines.setOrderId(orderId); // Use the generated orderId
				changeLines.setBuyerNumber(change.getBuyerNumber());
				changeLines.setQty(change.getQty());
				changeLines.setUom(change.getUom());
				changeLines.setPoLineNumber(change.getPoLineNumber());
				changeLines.setUnitPrice(change.getUnitPrice());
				changeLines.setStatus(change.getStatus());
				changeLines.setOrderNumber(change.getOrderNumber());
				changeLines.setOrderStatus(change.getOrderStatus());
				changeLines.setComments(change.getComments());
				changeLines.setItemDescription(change.getItemDescription());
				changeLines.setVendorPartnumber(change.getVendorPartnumber());
				changeLines.setShipDate(change.getShipDate());
				changeLines.setPriceUnit(change.getPriceUnit());
				changeLines.setPoLineItemStatus(change.getPoLineItemStatus());
				return changeRequestLinesRepository.save(changeLines);
			}).collectList().map(savedChangeRequestLinesList -> {
				logger.info("Data saved into Change Request successfully for Order ID: {}",
						savedChangeRequestData.getOrderId());
				Map<String, Object> successResponse = new HashMap<>();
				successResponse.put("message", "Data saved into order ack successfully");
				successResponse.put("status", true);
				successResponse.put("data", savedChangeRequestData);
				successResponse.put("lines", savedChangeRequestLinesList);
				return successResponse;
			});
		}).doOnSuccess(res -> logger.info("Executed the method :: saveChangeRequest ")).onErrorResume(Exception.class,
				ex -> {
					logger.error(" Error saveChangeRequest :: {}", ex.getMessage());
					return Mono.just(defaultSaveErrorResponse);
				});
	}

	// public Mono<Integer> updateStatus(int id, String correlationKey, String
	// status ) {
//		return outboxRepository.updateStatusById(status, correlationKey, id);
//	}
//	
	public Mono<Map<String, String>> updateStatus(int id, String transactionType, String correlationKey,
			String status) {
		logger.info("Executing the method :: updateStatus ");
		Mono<Integer> result = null;
		try {
			result = outboxRepository.updateStatusById(status, correlationKey, id, transactionType);
			logger.info("Executed the method :: updateStatus ");
		} catch (Exception e) {
			logger.error(" updateStatus :: {}", e.getMessage());
		}
		return result.map(updatedCount -> {
			System.out.println("status" + updatedCount);
			if (updatedCount > 0) {
				return Map.of("Success", "Status updated successfully for ID: " + id);
			} else {
				return Map.of("Success", "Status updated failed for ID: " + id);
			}
		}).defaultIfEmpty(Map.of("Failed", "Status update failed for ID: " + id));
	}

	public Mono<Map<String, Object>> getChangeRequestInformation(String orderId, String requestType)
			throws IOException {
		logger.info("Executing the method :: getChangeRequestInformation ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Error while fetching Data");
		response.put("status", false);
		response.put("data", new HashMap<>());
		return changeRequestRepository.findByOrderId(orderId, requestType).collectList().map(changeRequest -> {
			if (!changeRequest.isEmpty()) {
				response.put("message", "Order ack retrieved successfully");
				response.put("status", true);
				response.put("data", changeRequest);
			}
			logger.info("Executed the method :: getChangeRequestInformation ");
			return response;
		}).onErrorResume(ex -> {
			logger.error(" getChangeRequestInformation ::{}", ex.getMessage());
			return Mono.just(response);
		});
	}

	public Mono<Map<String, Object>> getChangeRequestLines(String orderId, String requestType) throws IOException {
		logger.info("Executing the method :: getChangeRequestLines ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Error while fetching Data");
		response.put("status", false);
		response.put("data", new HashMap<>());
		return changeRequestLinesRepository.findByOrderId(orderId, requestType).collectList()
				.map(changeRequestLines -> {
					if (!changeRequestLines.isEmpty()) {
						response.put("message", "order ack Lines retrieved successfully");
						response.put("status", true);
						response.put("data", changeRequestLines);
					}
					logger.info("Executed the method :: getChangeRequestLines ");
					return response;
				}).onErrorResume(ex -> {
					logger.error(" getChangeRequestLines :: {}", ex.getMessage());
					return Mono.just(response);
				});
	}

//    public Mono<Map<String, Object>> updateChaneRequest(int id, String orderId, ChangeRequest changeRequest) {
//        logger.info("Executing the method :: updateChaneRequest ");
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Failed to update data into Shipment Notice");
//        response.put("status", false);
//        ChangeRequestData changeRequestData = new ChangeRequestData();
//        ChangeRequestLines shipmentLine = new ChangeRequestLines();
//        List<ChangeRequestLines> changeRequestLinesList = new ArrayList<>();
//        try {
//            ChangeRequestData invoice = changeRequestRepository.findById(id).block();
//            if (invoice.getOrderId().equalsIgnoreCase(orderId)) {
//                changeRequestData.setOrderId(orderId);
//                changeRequestData.setModifiedDate(LocalDateTime.now());
//                changeRequestData.setComments(changeRequest.getComments());
//                changeRequestData.setCorrelationKey1(changeRequest.getCorrelationKey1());
//                changeRequestData.setCorrelationKey2(changeRequest.getCorrelationKey2());
//                changeRequestData.setCorrelationKey3(changeRequest.getCorrelationKey3());
//                changeRequestData.setCorrelationKey4(changeRequest.getCorrelationKey4());
//                changeRequestData.setCreatedBy(changeRequest.getCreatedBy());
//                changeRequestData.setModifiedBy(changeRequest.getModifiedBy());
//                changeRequestData.setOrderNumber(changeRequest.getOrderNumber());
//                changeRequestData.setPartnerId(changeRequest.getPartnerId());
//                changeRequestData.setPartnerName(changeRequest.getPartnerName());
//                changeRequestData.setStatus(changeRequest.getStatus());
//                changeRequestData.setDivisionId(changeRequest.getDivisionId());
//                changeRequestData.setDivisionName(changeRequest.getDivisionName());
//                changeRequestData.setOrderAmount(changeRequest.getOrderAmount());
//                changeRequestData.setFaFileLocation(changeRequest.getFaFileLocation());
//                changeRequestData.setFaFileName(changeRequest.getFaFileName());
//                changeRequestData.setOrderQty(changeRequest.getOrderQty());
//                changeRequestData.setSupplierCode(changeRequest.getSupplierCode());
//                changeRequestData.setSupplierName(changeRequest.getSupplierName());
//                changeRequestData.setPlantCode(changeRequest.getPlantCode());
//                changeRequestData.setOrderStatus(changeRequest.getOrderStatus());
//                changeRequestData.setSalesTerms(changeRequest.getSalesTerms());
//                changeRequestData.setBilltoCode(changeRequest.getBilltoCode());
//                changeRequestData.setShiptoName(changeRequest.getShiptoName());
//                changeRequestData.setShiptoCode(changeRequest.getShiptoCode());
//                changeRequestData.setReceiptNumber(changeRequest.getReceiptNumber());
//                changeRequestData.setTotalLineItems(changeRequest.getTotalLineItems());
//            }
//            changeRequestData = changeRequestRepository.save(changeRequestData).block();
//            for (ChangeRequestLines change : changeRequest.getChangeRequestLines()) {
//                ChangeRequestLines changeLines = new ChangeRequestLines();
//                ChangeRequestLines ChangeRequestLinesData = changeRequestLinesRepository.findById(change.getId()).block();
//                if (ChangeRequestLinesData.getOrderId().equalsIgnoreCase(orderId)) {
//                    ChangeRequestLinesData.setOrderId(orderId);
//                    ChangeRequestLinesData.setBuyerNumber(change.getBuyerNumber());
//                    ChangeRequestLinesData.setQty(change.getQty());
//                    ChangeRequestLinesData.setUom(change.getUom());
//                    ChangeRequestLinesData.setPoLineNumber(change.getPoLineNumber());
//                    ChangeRequestLinesData.setUnitPrice(change.getUnitPrice());
//                    ChangeRequestLinesData.setOrderNumber(change.getOrderNumber());
//                    ChangeRequestLinesData.setStatus(change.getStatus());
//                    changeLines.setComments(change.getComments());
//                    changeLines.setItemDescription(change.getItemDescription());
//                    changeLines.setVendorPartnumber(change.getVendorPartnumber());
//                    changeLines.setShipDate(change.getShipDate());
//                    changeLines.setPoLineItemStatus(change.getPoLineItemStatus());
//                    ChangeRequestLinesData.setOrderStatus(change.getOrderStatus());
//                    shipmentLine = changeRequestLinesRepository.save(ChangeRequestLinesData).block();
//                    changeRequestLinesList.add(shipmentLine);
//                }
//            }
//            if (changeRequestData != null) {
//                response.put("data", changeRequestData);
//                response.put("lines", changeRequestLinesList);
//                response.put("message", "Data updated into order ack successfully");
//                response.put("status", true);
//            }
//            logger.info("Executing the method :: updateChaneRequest ");
//        } catch (Exception e) {
//            logger.error(" updateChaneRequest :: {}", e.getMessage());
//        }
//        return Mono.just(response);
//    }

	public Mono<Map<String, ?>> updateChaneRequest(int id, String orderId, ChangeRequest changeRequest) {
		logger.info("Executing the method :: updateChaneRequest ");

		// Define a default error response map for this method
		Map<String, Object> defaultUpdateErrorResponse = new HashMap<>();
		defaultUpdateErrorResponse.put("message", "Failed to update data into Change Request");
		defaultUpdateErrorResponse.put("status", false);
		defaultUpdateErrorResponse.put("data", new HashMap<>());

		// 1. Find the existing ChangeRequestData by ID reactively
		return changeRequestRepository.findById(id).flatMap(existingChangeRequestData -> {
			// 2. Check if the orderId matches (optional, based on your business logic)
			if (!existingChangeRequestData.getOrderId().equalsIgnoreCase(orderId)) {
				logger.warn(
						"Attempted to update ChangeRequest with ID {} but orderId {} does not match existing orderId {}.",
						id, orderId, existingChangeRequestData.getOrderId());
				return Mono.just(
						Map.of("message", "Order ID mismatch for update", "status", false, "data", new HashMap<>()));
			}

			existingChangeRequestData.setModifiedDate(LocalDateTime.now());
			existingChangeRequestData.setComments(changeRequest.getComments());
			existingChangeRequestData.setCorrelationKey1(changeRequest.getCorrelationKey1());
			existingChangeRequestData.setCorrelationKey2(changeRequest.getCorrelationKey2());
			existingChangeRequestData.setCorrelationKey3(changeRequest.getCorrelationKey3());
			existingChangeRequestData.setCorrelationKey4(changeRequest.getCorrelationKey4());
			existingChangeRequestData.setCreatedBy(changeRequest.getCreatedBy());
			existingChangeRequestData.setModifiedBy(changeRequest.getModifiedBy());
			existingChangeRequestData.setOrderNumber(changeRequest.getOrderNumber());
			existingChangeRequestData.setPartnerId(changeRequest.getPartnerId());
			existingChangeRequestData.setPartnerName(changeRequest.getPartnerName());
			existingChangeRequestData.setStatus(changeRequest.getStatus());
			existingChangeRequestData.setDivisionId(changeRequest.getDivisionId());
			existingChangeRequestData.setDivisionName(changeRequest.getDivisionName());
			existingChangeRequestData.setOrderAmount(changeRequest.getOrderAmount());
			existingChangeRequestData.setFaFileLocation(changeRequest.getFaFileLocation());
			existingChangeRequestData.setFaFileName(changeRequest.getFaFileName());
			existingChangeRequestData.setOrderQty(changeRequest.getOrderQty());
			existingChangeRequestData.setSupplierCode(changeRequest.getSupplierCode());
			existingChangeRequestData.setSupplierName(changeRequest.getSupplierName());
			existingChangeRequestData.setPlantCode(changeRequest.getPlantCode());
			existingChangeRequestData.setOrderStatus(changeRequest.getOrderStatus());
			existingChangeRequestData.setSalesTerms(changeRequest.getSalesTerms());
			existingChangeRequestData.setBilltoCode(changeRequest.getBilltoCode());
			existingChangeRequestData.setShiptoName(changeRequest.getShiptoName());
			existingChangeRequestData.setShiptoCode(changeRequest.getShiptoCode());
			existingChangeRequestData.setReceiptNumber(changeRequest.getReceiptNumber());
			existingChangeRequestData.setTotalLineItems(changeRequest.getTotalLineItems());

			return changeRequestRepository.save(existingChangeRequestData).flatMap(updatedChangeRequestData -> {
				return Flux.fromIterable(changeRequest.getChangeRequestLines()).flatMap(lineFromRequest -> {
					return changeRequestLinesRepository.findById(lineFromRequest.getId()).flatMap(existingLine -> {
						if (!existingLine.getOrderId().equalsIgnoreCase(orderId)) {
							logger.warn(
									"Line ID {} has orderId {} which does not match main orderId {}. Skipping update for this line.",
									lineFromRequest.getId(), existingLine.getOrderId(), orderId);
							return Mono.empty();
						}

						existingLine.setBuyerNumber(lineFromRequest.getBuyerNumber());
						existingLine.setQty(lineFromRequest.getQty());
						existingLine.setUom(lineFromRequest.getUom());
						existingLine.setPoLineNumber(lineFromRequest.getPoLineNumber());
						existingLine.setUnitPrice(lineFromRequest.getUnitPrice());
						existingLine.setStatus(lineFromRequest.getStatus());
						existingLine.setOrderNumber(lineFromRequest.getOrderNumber());
						existingLine.setOrderStatus(lineFromRequest.getOrderStatus());
						existingLine.setComments(lineFromRequest.getComments());
						existingLine.setItemDescription(lineFromRequest.getItemDescription());
						existingLine.setVendorPartnumber(lineFromRequest.getVendorPartnumber());
						existingLine.setShipDate(lineFromRequest.getShipDate());
						existingLine.setPriceUnit(lineFromRequest.getPriceUnit());
						existingLine.setPoLineItemStatus(lineFromRequest.getPoLineItemStatus());

						return changeRequestLinesRepository.save(existingLine);
					}).switchIfEmpty(Mono.defer(() -> {
						logger.warn("ChangeRequestLine with ID {} not found for update. It will not be updated.",
								lineFromRequest.getId());
						return Mono.empty();
					}));
				}).collectList().map(updatedChangeRequestLinesList -> {
					logger.info("Data updated into Change Request successfully for Order ID: {}",
							updatedChangeRequestData.getOrderId());
					Map<String, Object> successResponse = new HashMap<>();
					successResponse.put("message", "Data updated into order ack successfully");
					successResponse.put("status", true);
					successResponse.put("data", updatedChangeRequestData);
					successResponse.put("lines", updatedChangeRequestLinesList);
					return successResponse;
				});
			});
		}).switchIfEmpty(Mono.defer(() -> {
			logger.warn("ChangeRequestData with ID {} not found for update.", id);
			return Mono
					.just(Map.of("message", "Change Request data not found", "status", false, "data", new HashMap<>()));
		})).doOnSuccess(res -> logger.info("Executed the method :: updateChaneRequest ")).onErrorResume(Exception.class,
				ex -> {
					logger.error(" Error updateChaneRequest :: {}", ex.getMessage());
					return Mono.just(defaultUpdateErrorResponse);
				});
	}

//    public Mono<Map<String, Object>> orderAcknowledgeMent(ChangeRequest changeRequest) {
//        logger.info("Executing the method :: orderAcknowledgement ");
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Failed to save data into order ack");
//        response.put("status", false);
//        try {
//            String orderId = UUID.randomUUID().toString();
//            ChangeRequestData changeRequestData = new ChangeRequestData();
//            ChangeRequestLines shipmentLine = new ChangeRequestLines();
//            List<ChangeRequestLines> changeRequestLinesList = new ArrayList<>();
//            changeRequestData.setOrderId(orderId);
//            changeRequestData.setCreatedDate(LocalDateTime.now());
//            changeRequestData.setModifiedDate(LocalDateTime.now());
//            changeRequestData.setComments(changeRequest.getComments());
//            changeRequestData.setCorrelationKey1(changeRequest.getCorrelationKey1());
//            changeRequestData.setCorrelationKey2(changeRequest.getCorrelationKey2());
//            changeRequestData.setCorrelationKey3(changeRequest.getCorrelationKey3());
//            changeRequestData.setCorrelationKey4(changeRequest.getCorrelationKey4());
//            changeRequestData.setCreatedBy(changeRequest.getCreatedBy());
//            changeRequestData.setModifiedBy(changeRequest.getModifiedBy());
//            changeRequestData.setOrderNumber(changeRequest.getOrderNumber());
//            changeRequestData.setPartnerId(changeRequest.getPartnerId());
//            changeRequestData.setPartnerName(changeRequest.getPartnerName());
//            changeRequestData.setStatus(changeRequest.getStatus());
//            changeRequestData.setOrderStatus(changeRequest.getOrderStatus());
//            changeRequestData.setSalesTerms(changeRequest.getSalesTerms());
//            changeRequestData.setBilltoCode(changeRequest.getBilltoCode());
//            changeRequestData.setShiptoName(changeRequest.getShiptoName());
//            changeRequestData.setShiptoCode(changeRequest.getShiptoCode());
//            changeRequestData.setReceiptNumber(changeRequest.getReceiptNumber());
//            changeRequestData.setOrderAmount(changeRequest.getOrderAmount());
//            changeRequestData.setOrderQty(changeRequest.getOrderQty());
//            changeRequestData.setTotalLineItems(changeRequest.getTotalLineItems());
//            changeRequestData = changeRequestRepository.save(changeRequestData).block();
//            for (ChangeRequestLines change : changeRequest.getChangeRequestLines()) {
//                ChangeRequestLines changeLines = new ChangeRequestLines();
//                changeLines.setOrderId(orderId);
//                changeLines.setBuyerNumber(change.getBuyerNumber());
//                changeLines.setQty(change.getQty());
//                changeLines.setUom(change.getUom());
//                changeLines.setPoLineNumber(change.getPoLineNumber());
//                changeLines.setUnitPrice(change.getUnitPrice());
//                changeLines.setStatus(change.getStatus());
//                changeLines.setOrderNumber(change.getOrderNumber());
//                changeLines.setOrderStatus(change.getOrderStatus());
//                changeLines.setComments(change.getComments());
//                changeLines.setItemDescription(change.getItemDescription());
//                changeLines.setVendorPartnumber(change.getVendorPartnumber());
//                changeLines.setShipDate(change.getShipDate());
//                changeLines.setPoLineItemStatus(change.getPoLineItemStatus());
//                changeLines.setPriceUnit(change.getPriceUnit());
//                changeLines.setOriginalOrderQty(change.getOriginalOrderQty());
//                changeLines.setTransactionType(change.getTransactionType());
//                shipmentLine = changeRequestLinesRepository.save(changeLines).block();
//                changeRequestLinesList.add(shipmentLine);
//            }
//            if (changeRequestData != null) {
//                response.put("data", changeRequestData);
//                response.put("lines", changeRequestLinesList);
//                response.put("message", "Data saved into order ack successfully");
//                response.put("status", true);
//            }
//            logger.info("Executing the method :: orderAcknowledgeMent ");
//        } catch (Exception ex) {
//            logger.error(" orderAcknowledgeMent :: {}", ex.getMessage());
//        }
//        return Mono.just(response);
//    }

	public Mono<Map<String, Object>> orderAcknowledgeMent(ChangeRequest changeRequest) {
		logger.info("Executing the method :: orderAcknowledgement ");
		Map<String, Object> defaultSaveErrorResponse = new HashMap<>();
		defaultSaveErrorResponse.put("message", "Failed to save data into order ack");
		defaultSaveErrorResponse.put("status", false);
		defaultSaveErrorResponse.put("data", new HashMap<>());

		String orderId = UUID.randomUUID().toString();

		ChangeRequestData changeRequestData = new ChangeRequestData();
		changeRequestData.setOrderId(orderId);
		changeRequestData.setCreatedDate(LocalDateTime.now());
		changeRequestData.setModifiedDate(LocalDateTime.now());
		changeRequestData.setComments(changeRequest.getComments());
		changeRequestData.setCorrelationKey1(changeRequest.getCorrelationKey1());
		changeRequestData.setCorrelationKey2(changeRequest.getCorrelationKey2());
		changeRequestData.setCorrelationKey3(changeRequest.getCorrelationKey3());
		changeRequestData.setCorrelationKey4(changeRequest.getCorrelationKey4());
		changeRequestData.setCreatedBy(changeRequest.getCreatedBy());
		changeRequestData.setModifiedBy(changeRequest.getModifiedBy());
		changeRequestData.setOrderNumber(changeRequest.getOrderNumber());
		changeRequestData.setPartnerId(changeRequest.getPartnerId());
		changeRequestData.setPartnerName(changeRequest.getPartnerName());
		changeRequestData.setStatus(changeRequest.getStatus());
		changeRequestData.setOrderStatus(changeRequest.getOrderStatus());
		changeRequestData.setSalesTerms(changeRequest.getSalesTerms());
		changeRequestData.setBilltoCode(changeRequest.getBilltoCode());
		changeRequestData.setShiptoName(changeRequest.getShiptoName());
		changeRequestData.setShiptoCode(changeRequest.getShiptoCode());
		changeRequestData.setReceiptNumber(changeRequest.getReceiptNumber());
		changeRequestData.setOrderAmount(changeRequest.getOrderAmount());
		changeRequestData.setOrderQty(changeRequest.getOrderQty());
		changeRequestData.setTotalLineItems(changeRequest.getTotalLineItems());

		return changeRequestRepository.save(changeRequestData).flatMap(savedChangeRequestData -> {
			return Flux.fromIterable(changeRequest.getChangeRequestLines()).flatMap(lineFromRequest -> {
				ChangeRequestLines changeLines = new ChangeRequestLines();
				changeLines.setOrderId(orderId);
				changeLines.setBuyerNumber(lineFromRequest.getBuyerNumber());
				changeLines.setQty(lineFromRequest.getQty());
				changeLines.setUom(lineFromRequest.getUom());
				changeLines.setPoLineNumber(lineFromRequest.getPoLineNumber());
				changeLines.setUnitPrice(lineFromRequest.getUnitPrice());
				changeLines.setStatus(lineFromRequest.getStatus());
				changeLines.setOrderNumber(lineFromRequest.getOrderNumber());
				changeLines.setOrderStatus(lineFromRequest.getOrderStatus());
				changeLines.setComments(lineFromRequest.getComments());
				changeLines.setItemDescription(lineFromRequest.getItemDescription());
				changeLines.setVendorPartnumber(lineFromRequest.getVendorPartnumber());
				changeLines.setShipDate(lineFromRequest.getShipDate());
				changeLines.setPriceUnit(lineFromRequest.getPriceUnit());
				changeLines.setPoLineItemStatus(lineFromRequest.getPoLineItemStatus());
				changeLines.setOriginalOrderQty(lineFromRequest.getOriginalOrderQty());
				changeLines.setTransactionType(lineFromRequest.getTransactionType());
				return changeRequestLinesRepository.save(changeLines);
			}).collectList().map(savedChangeRequestLinesList -> {
				logger.info("Data saved into order ack successfully for Order ID: {}",
						savedChangeRequestData.getOrderId());
				Map<String, Object> successResponse = new HashMap<>();
				successResponse.put("message", "Data saved into order ack successfully");
				successResponse.put("status", true);
				successResponse.put("data", savedChangeRequestData);
				successResponse.put("lines", savedChangeRequestLinesList);
				return successResponse;
			});
		}).doOnSuccess(res -> logger.info("Executed the method :: orderAcknowledgement "))
				.onErrorResume(Exception.class, ex -> {
					logger.error(" Error orderAcknowledgement :: {}", ex.getMessage());
					return Mono.just(defaultSaveErrorResponse);
				});
	}

	public Mono<Map<String, Object>> getOrderAckLines(String orderId, String requestType) throws IOException {
		logger.info("Executing the method :: getOrderAckLines ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Error while fetching Data");
		response.put("status", false);
		response.put("data", new HashMap<>());
		return changeRequestLinesRepository.findByOrderIdAndStatus(orderId, requestType).collectList()
				.map(changeRequestLines -> {
					if (!changeRequestLines.isEmpty()) {
						response.put("message", "order ack Lines retrieved successfully");
						response.put("status", true);
						response.put("data", changeRequestLines);
					}
					logger.info("Executed the method :: getOrderAckLines ");
					return response;
				}).onErrorResume(ex -> {
					logger.error(" getOrderAckLines :: {}", ex.getMessage());
					return Mono.just(response);
				});
	}

	public Mono<Map<String, Object>> getOrderAck(String orderId, String requestType) throws IOException {
		logger.info("Executing the method :: getOrderAck ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Error while fetching Data");
		response.put("status", false);
		response.put("data", new HashMap<>());
		return changeRequestRepository.findByOrderIdAndStatus(orderId, requestType).collectList().map(changeRequest -> {
			if (!changeRequest.isEmpty()) {
				response.put("message", "order ack retrieved successfully");
				response.put("status", true);
				response.put("data", changeRequest);
			}
			logger.info("Executing the method :: getOrderAck ");
			return response;
		}).onErrorResume(ex -> {
			logger.error(" getOrderAck :: {}", ex.getMessage());
			return Mono.just(response);
		});
	}

	public Mono<Map<String, Object>> getOutboxByStatus(String status) throws IOException {
		logger.info("Executing the method :: getOutboxByStatus ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Error while fetching Data");
		response.put("status", false);
		response.put("data", new HashMap<>());
		return outboxRepository.getOutboxByStatus(status).collectList().map(changeRequestLines -> {
			if (!changeRequestLines.isEmpty()) {
				response.put("message", "out box Lines retrieved successfully");
				response.put("status", true);
				response.put("data", changeRequestLines);
			}
			logger.info("Executing the method :: getOutboxByStatus ");
			return response;
		}).onErrorResume(ex -> {
			logger.error(" getOutboxByStatus :: {}", ex.getMessage());
			return Mono.just(response);
		});
	}

	public Mono<Map<String, Object>> getOutboxOrderAckLines(String orderNumber, String transactionType)
			throws IOException {
		logger.info("Executing the method :: getOutboxOrderAckLines ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Error while fetching Data");
		response.put("status", false);
		response.put("data", new HashMap<>());
		return changeRequestLinesRepository.findByOrderNumber(orderNumber, transactionType).collectList()
				.map(changeRequestLines -> {
					if (!changeRequestLines.isEmpty()) {
						response.put("message", "order ack Lines retrieved successfully");
						response.put("status", true);
						response.put("data", changeRequestLines);
					}
					logger.info("Executed the method :: getOutboxOrderAckLines ");
					return response;
				}).onErrorResume(ex -> {
					logger.error(" getOutboxOrderAckLines :: {}", ex.getMessage());
					return Mono.just(response);
				});
	}

	public Mono<Map<String, Object>> b2bChangeRequestSubmit(ChangeRequest changeRequest) throws IOException {
		logger.info("Executing the method :: b2bChangeRequestSubmit ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Error while getting data from bpdurl");
		System.out.println("chnage request");
		try {
			final String uriForWithoutSsl = changeRequest.getBpdLink();
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);
			final StringBuilder xml = new StringBuilder();
			xml.append("<LookUpDetails> <changeRequestId>").append(changeRequest.getChangeRequestId())
					.append("</changeRequestId><partnerId>").append(changeRequest.getPartnerId()).append("</partnerId>")
					.append("<emailId>").append(changeRequest.getEmailId()).append("</emailId>")
					.append("</LookUpDetails>");
			System.out.println(xml);
			final HttpEntity<String> entityForWithoutSsl = new HttpEntity<String>(
					"<?xml version='1.0' encoding='UTF-8'?>" + xml, headers);
			final RestTemplate restTemplate = new RestTemplate();
			final ResponseEntity<String> data = restTemplate.postForEntity(uriForWithoutSsl, entityForWithoutSsl,
					String.class);
			System.out.println(data);
			System.out.println(data.getStatusCode());
			if (data.getStatusCode().value() == 250) {
				response.put("success", true);
				response.put("message", "Request Sent to B2B Successfully");
			}
			logger.info("Executing the method :: b2bChangeRequestSubmit ");
		} catch (final Exception e) {
			logger.error(" b2bChangeRequestSubmit :: {}", e.getMessage());
			response.put("success", false);
			response.put("message", "Request Failed to B2B. Please Contact Administrator:::" + e.getMessage());
		}
		return Mono.just(response);
	}

//	public Mono<Map<String, Object>> getOutboxFileData(int id) throws IOException {
//		logger.info("Executing the method :: getOutboxFileData ");
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Error while fetching file");
//		response.put("status", false);
//		response.put("data", new HashMap<>());
//		try {
//			Configurations config = configurationsRepository.findAll().blockFirst();
//			Outbox outbox = outboxRepository.findById(id).block();
//			if (outbox != null) {
//				String filePath = outbox.getFileLocation();
//				String fileName = outbox.getFileName();
//				logger.info("Outbox filepath before: {}", filePath);
//				if (filePath.contains(config.getS3_bucket_name())) {
//					filePath = filePath.replace("/" + config.getS3_bucket_name() + "/", "");
//				}
//				logger.info("Outbox filepath after: " + filePath);
//				try {
//					BasicAWSCredentials awsCreds = new BasicAWSCredentials(config.getS3_bucket_access_key(),
//							config.getS3_bucket_sceret_key());
//					AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//							.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//							.withRegion(config.getS3_bucket_region()).build();
//					boolean isObjectExist = s3Client.doesObjectExist(config.getS3_bucket_name(),
//							filePath + "/" + fileName);
//					if (isObjectExist) {
//						S3Object s3Object = s3Client.getObject(config.getS3_bucket_name(), filePath + "/" + fileName);
//						S3ObjectInputStream inputStream = s3Object.getObjectContent();
//						byte[] content = IOUtils.toByteArray(inputStream);
//						inputStream.close();
//						response.put("message", "Fetching Outbox File Successfully");
//						response.put("status", true);
//						response.put("data", content);
//					} else {
//						response.put("data", HttpStatus.SC_NOT_FOUND);
//					}
//					logger.info("Executing the method :: getOutboxFileData ");
//				} catch (Exception exception) {
//					logger.error("getFileFromAmazonS3 :: {}", exception.getMessage());
//					response.put("data", HttpStatus.SC_INTERNAL_SERVER_ERROR);
//				}
//			} else {
//				response.put("message", "Outbox File Id doesn't exist");
//			}
//		} catch (Exception ex) {
//			logger.error("getOutboxFileData :: {}", ex.getMessage());
//		}
//		return Mono.just(response);
//	}

	public Mono<Map<String, Object>> getOutboxFileData(int id) {
		logger.info("Executing the method :: getOutboxFileData ");
		Map<String, Object> defaultErrorResponse = new HashMap<>();
		defaultErrorResponse.put("message", "Error while fetching file");
		defaultErrorResponse.put("status", false);
		defaultErrorResponse.put("data", new HashMap<>());

		return configurationsRepository.findAll().next().flatMap(config -> {
			return outboxRepository.findById(id).flatMap(outbox -> {
				String filePath = outbox.getFileLocation();
				String fileName = outbox.getFileName();

				logger.info("Outbox filepath before: {}", filePath);

				if (filePath.contains(config.getS3_bucket_name())) {
					filePath = filePath.replace("/" + config.getS3_bucket_name() + "/", "");
				}
				logger.info("Outbox filepath after: {}", filePath);

				S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
						.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
								.create(config.getS3_bucket_access_key(), config.getS3_bucket_sceret_key())))
						.region(Region.of(config.getS3_bucket_region())).build();

				String s3ObjectKey = filePath + "/" + fileName;

				HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(config.getS3_bucket_name())
						.key(s3ObjectKey).build();

				return Mono.fromFuture(s3AsyncClient.headObject(headObjectRequest)).flatMap(headObjectResponse -> {
					GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(config.getS3_bucket_name())
							.key(s3ObjectKey).build();

					return Mono
							.fromFuture(s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()))
							.map(responseBytes -> {
								Map<String, Object> successResponse = new HashMap<>();
								successResponse.put("message", "Fetching Outbox File Successfully");
								successResponse.put("status", true);
								successResponse.put("data", responseBytes.asByteArray());
								return successResponse;
							}).doFinally(signalType -> s3AsyncClient.close());
				}).onErrorResume(NoSuchKeyException.class, e -> {
					logger.warn("S3 object not found (NoSuchKeyException): bucket={}, key={}",
							config.getS3_bucket_name(), s3ObjectKey);
					return Mono.just(
							Map.of("message", "File not found in S3", "status", false, "data", HttpStatus.NOT_FOUND));
				}).onErrorResume(Exception.class, s3Exception -> {
					logger.error(" Error during S3 operation for file {}: {}", fileName, s3Exception.getMessage());
					return Mono.just(Map.of("message", "Error during S3 operation", "status", false, "data",
							HttpStatus.INTERNAL_SERVER_ERROR));
				}).doFinally(signalType -> s3AsyncClient.close());
			}).switchIfEmpty(Mono.defer(() -> {
				logger.warn("Outbox File Id {} doesn't exist. Returning specific error response.", id);
				return Mono.just(
						Map.of("message", "Outbox File Id doesn't exist", "status", false, "data", new HashMap<>()));
			}));
		}).doOnSuccess(res -> logger.info("Executed the method :: getOutboxFileData ")).onErrorResume(Exception.class,
				overallException -> {
					logger.error(" Overall error in getOutboxFileData :: {}", overallException.getMessage());
					return Mono.just(defaultErrorResponse);
				});
	}

	public Mono<Map<String, String>> updateStatus(String correlationKey, String status, String transactionType,
			int id) {
		logger.info("Executing the method :: updateStatus ");
		Mono<Integer> result = null;
		try {
			result = outboxRepository.updateStatusById(status, correlationKey, id, transactionType);
			logger.info("Executed the method :: updateStatus ");
		} catch (Exception e) {
			logger.error(" updateStatus :: {}", e.getMessage());
		}
		return result.map(updatedCount -> {
			System.out.println("status" + updatedCount);
			if (updatedCount > 0) {
				return Map.of("Success", "Status updated successfully for ID: " + id);
			} else {
				return Map.of("Success", "Status updated failed for ID: " + id);
			}
		}).defaultIfEmpty(Map.of("Failed", "Status update failed for ID: " + id));
	}

	public Flux<Outbox> fetch(String transactionType, String startDate, String endDate, String partnerId,
			String correlationKey1, String status, Boolean trashFlag, Boolean archiveFlag) {
//Set parameters to null if they are empty or null
		transactionType = (transactionType != null && !transactionType.isEmpty()) ? transactionType : null;
		startDate = (startDate != null && !startDate.isEmpty()) ? startDate : null;
		endDate = (endDate != null && !endDate.isEmpty()) ? endDate : null;
		partnerId = (partnerId != null && !partnerId.isEmpty()) ? partnerId : null;
		correlationKey1 = (correlationKey1 != null && !correlationKey1.isEmpty()) ? correlationKey1 : null;
		trashFlag = (trashFlag != null) ? trashFlag : false;
		archiveFlag = (archiveFlag != null) ? archiveFlag : false;
		status = (status != null && !status.isEmpty()) ? status.toLowerCase() : null;

		return outboxRepository.findByFilter(transactionType, startDate, endDate, partnerId, correlationKey1, status,
				trashFlag, archiveFlag);
	}

	public Flux<Outbox> getOutboxByCorrelationKey(String correlationKey, String transactionType) {
		return outboxRepository.findOutboxByCorrelationKey(correlationKey, transactionType);
	}

	public Flux<Outbox> getOutboxInvoiceByCorrelationKey(String correlationKey, String transactionType) {
		return outboxRepository.getOutboxInvoiceByCorrelationKey(correlationKey, transactionType);
	}

	public Mono<Boolean> checkTransactionType856ByCorrelationKey(String correlationKey) {
		return outboxRepository.count856ByCorrelationKey(correlationKey).map(count -> count > 0);
	}

	public Mono<Void> updateFlags(List<String> keys, String type, boolean flagValue) {
		logger.info("Starting updateFlags - type: {}, flagValue: {}, keys: {}", type, flagValue, keys);

		List<Mono<?>> updates = keys.stream().map(key -> {
			if ("trash".equalsIgnoreCase(type)) {
				logger.debug("Updating trash flag for key: {}", key);
				return outboxRepository.updateTrashFlag(key, flagValue);
			} else if ("archive".equalsIgnoreCase(type)) {
				logger.debug("Updating archive flag for key: {}", key);
				return outboxRepository.updateArchiveFlag(key, flagValue);
			} else {
				logger.error("Invalid flag type provided: {}", type);
				return Mono.error(new IllegalArgumentException("Invalid flag type"));
			}
		}).toList();

		return Mono.when(updates).doOnSuccess(v -> logger.info("All flags updated successfully"))
				.doOnError(e -> logger.error("Error updating flags", e)).then();
	}
}
