package com.miraclesoft.rehlko.service;

import com.miraclesoft.rehlko.entity.ChangeRequest;
import com.miraclesoft.rehlko.entity.Configurations;
import com.miraclesoft.rehlko.entity.InvoiceRequest;
import com.miraclesoft.rehlko.entity.ShipmentRequest;
import com.miraclesoft.rehlko.repository.ConfigurationsRepository;
import com.miraclesoft.rehlko.repository.OutboxRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import java.io.IOException;

@Service
@Configuration
@EnableScheduling
public class ReprocessService {

	@Autowired
	private final OutboxRepository outboxRepository;
	private final OutboxService outboxService;

	private final ShipmentService shipmentService;
	private final InvoiceService invoiceService;
	private final ConfigurationsRepository configurationsRepository;

	private static final Logger logger = LoggerFactory.getLogger(ReprocessService.class.getName());

	public ReprocessService(OutboxRepository outboxRepository, OutboxService outboxService,
			ShipmentService shipmentService, InvoiceService invoiceService,
			ConfigurationsRepository configurationsRepository) {
		this.outboxRepository = outboxRepository;
		this.outboxService = outboxService;
		this.shipmentService = shipmentService;
		this.invoiceService = invoiceService;
		this.configurationsRepository = configurationsRepository;

	}

//
//	public Mono<String> reprocess(String transactionType, int limit) {
//        Mono<Configurations> configurationsMono = configurationsRepository.findAll().next(); // Get the first Configurations object
//
//        String submit855URL = configurationsMono.map(Configurations::getSubmit855).block();
//        String submit856URL = configurationsMono.map(Configurations::getSubmit856).block();
//        String submit810URL = configurationsMono.map(Configurations::getSubmit810).block();
//		logger.info("Executing reprocess for transactionType: {}", transactionType);
//		return outboxRepository.getAllNonSentTransactions(transactionType, limit).flatMap(data -> {
//			if ("855".equals(transactionType)) {
//				ChangeRequest changeRequest = new ChangeRequest();
//				changeRequest.setChangeRequestId(data.getChangeRequestId());
//				changeRequest.setPartnerId(data.getPartnerId());
//                changeRequest.setBpdLink(submit855URL);
//
//                logger.info("Submitting ChangeRequest for ChangeRequestId: {}", data.getChangeRequestId());
//				try {
//					return outboxService.b2bChangeRequestSubmit(changeRequest).flatMap(response -> {
//						if (response != null && response.containsKey("success")
//								&& Boolean.TRUE.equals(response.get("success"))) {
//							return Mono
//									.just("Submitted successfully for ChangeRequestId: " + data.getChangeRequestId());
//						} else {
//							return Mono.just("Error submitting ChangeRequest of ChangeRequestId: "
//									+ data.getChangeRequestId() + " - Response: " + response);
//						}
//					}).onErrorResume(IOException.class,
//							e -> Mono.error(new RuntimeException(
//									"Error submitting ChangeRequest of ChangeRequestId: " + data.getChangeRequestId(),
//									e)));
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			} else if ("856".equals(transactionType)) {
//				ShipmentRequest shipmentRequest = new ShipmentRequest();
//				shipmentRequest.setShipmentId(data.getShipmentId());
//				shipmentRequest.setPartnerId(data.getPartnerId());
//                shipmentRequest.setBpdLink(submit856URL);
//				logger.info("Submitting ShipmentRequest of shipmentId : {}", data.getShipmentId());
//				try {
//					return shipmentService.b2bSendShipment(shipmentRequest).flatMap(response -> {
//						if (response != null && response.containsKey("success")
//								&& Boolean.TRUE.equals(response.get("success"))) {
//							return Mono.just(
//									"ShipmentRequest submitted successfully for shipmentId: " + data.getShipmentId());
//						} else {
//							return Mono.just("Error submitting ShipmentRequest of shipmentId: " + data.getShipmentId()
//									+ " - Response: " + response);
//						}
//					}).onErrorResume(IOException.class, e -> Mono.error(new RuntimeException(
//							"Error submitting ShipmentRequest of shipmentId: " + data.getShipmentId(), e)));
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			} else if ("810".equals(transactionType)) {
//				InvoiceRequest invoiceRequest = new InvoiceRequest();
//				invoiceRequest.setInvoiceId(data.getInvoiceId());
//				invoiceRequest.setPartnerId(data.getPartnerId());
//                invoiceRequest.setBpdLink(submit810URL);
//				logger.info("Submitting InvoiceRequest for invoiceId: {}", data.getInvoiceId());
//				try {
//					return invoiceService.b2bInvoiceSubmit(invoiceRequest).flatMap(response -> {
//						if (response != null && response.containsKey("success")
//								&& Boolean.TRUE.equals(response.get("success"))) {
//							return Mono.just(
//									"InvoiceRequest submitted successfully for invoiceId: " + data.getInvoiceId());
//						} else {
//							return Mono.just("Error submitting InvoiceRequest of invoiceId: " + data.getInvoiceId()
//									+ " - Response: " + response);
//						}
//					}).onErrorResume(IOException.class, e -> Mono.error(new RuntimeException(
//							"Error submitting InvoiceRequest of invoiceId: " + data.getInvoiceId(), e)));
//				} catch (IOException e) {
//					throw new RuntimeException(e);
//				}
//			} else {
//				logger.warn("Unsupported transactionType: {}", transactionType);
//				return Mono.just("Unsupported transactionType: " + transactionType);
//			}
//		}).reduce((acc, current) -> acc + "\n" + current) // Aggregate success messages
//				.defaultIfEmpty("No transactions found for transactionType: " + transactionType)
//				.doOnError(error -> logger.error("Error processing transactions for type {}{}", transactionType,
//						error.getMessage()));
//	}

	public Mono<String> reprocess(String transactionType, int limit) {
		logger.info("Executing reprocess for transactionType: {}", transactionType);

		return configurationsRepository.findAll().next().flatMap(config -> {
			String submit855URL = config.getSubmit855();
			String submit856URL = config.getSubmit856();
			String submit810URL = config.getSubmit810();

			return outboxRepository.getAllNonSentTransactions(transactionType, limit).flatMap(data -> {
				switch (transactionType) {
				case "855": {
					ChangeRequest changeRequest = new ChangeRequest();
					changeRequest.setChangeRequestId(data.getChangeRequestId());
					changeRequest.setPartnerId(data.getPartnerId());
					changeRequest.setBpdLink(submit855URL);

					logger.info("Submitting ChangeRequest for ChangeRequestId: {}", data.getChangeRequestId());

					try {
						return outboxService.b2bChangeRequestSubmit(changeRequest).map(response -> {
							if (Boolean.TRUE.equals(response.get("success"))) {
								return "Submitted successfully for ChangeRequestId: " + data.getChangeRequestId();
							} else {
								return "Error submitting ChangeRequest of ChangeRequestId: " + data.getChangeRequestId()
										+ " - Response: " + response;
							}
						}).onErrorResume(ex -> {
							logger.error("Error submitting ChangeRequest: {}", ex.getMessage(), ex);
							return Mono.just("Exception during ChangeRequestId " + data.getChangeRequestId() + ": "
									+ ex.getMessage());
						});
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				case "856": {
					ShipmentRequest shipmentRequest = new ShipmentRequest();
					shipmentRequest.setShipmentId(data.getShipmentId());
					shipmentRequest.setPartnerId(data.getPartnerId());
					shipmentRequest.setBpdLink(submit856URL);

					logger.info("Submitting ShipmentRequest for shipmentId: {}", data.getShipmentId());

					try {
						return shipmentService.b2bSendShipment(shipmentRequest).map(response -> {
							if (Boolean.TRUE.equals(response.get("success"))) {
								return "ShipmentRequest submitted successfully for shipmentId: " + data.getShipmentId();
							} else {
								return "Error submitting ShipmentRequest of shipmentId: " + data.getShipmentId()
										+ " - Response: " + response;
							}
						}).onErrorResume(ex -> {
							logger.error("Error submitting ShipmentRequest: {}", ex.getMessage(), ex);
							return Mono.just(
									"Exception during shipmentId " + data.getShipmentId() + ": " + ex.getMessage());
						});
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				case "810": {
					InvoiceRequest invoiceRequest = new InvoiceRequest();
					invoiceRequest.setInvoiceId(data.getInvoiceId());
					invoiceRequest.setPartnerId(data.getPartnerId());
					invoiceRequest.setBpdLink(submit810URL);

					logger.info("Submitting InvoiceRequest for invoiceId: {}", data.getInvoiceId());

					try {
						return invoiceService.b2bInvoiceSubmit(invoiceRequest).map(response -> {
							if (Boolean.TRUE.equals(response.get("success"))) {
								return "InvoiceRequest submitted successfully for invoiceId: " + data.getInvoiceId();
							} else {
								return "Error submitting InvoiceRequest of invoiceId: " + data.getInvoiceId()
										+ " - Response: " + response;
							}
						}).onErrorResume(ex -> {
							logger.error("Error submitting InvoiceRequest: {}", ex.getMessage(), ex);
							return Mono
									.just("Exception during invoiceId " + data.getInvoiceId() + ": " + ex.getMessage());
						});
					} catch (IOException e) {
						throw new RuntimeException(e);
					}
				}
				default:
					logger.warn("Unsupported transactionType: {}", transactionType);
					return Mono.just("Unsupported transactionType: " + transactionType);
				}
			}).reduce((acc, current) -> acc + "\n" + current)
					.defaultIfEmpty("No transactions found for transactionType: " + transactionType);
		}).onErrorResume(ex -> {
			logger.error("Error processing reprocess() for type {}: {}", transactionType, ex.getMessage(), ex);
			return Mono.just("System error occurred during reprocessing: " + ex.getMessage());
		});
	}

	public Mono<String> reprocess(String transactionType, String id, String partnerId) {
		logger.info("Executing reprocess for transactionType: {}", transactionType);
		Mono<Configurations> configurationsMono = configurationsRepository.findAll().next();

		String submit855URL = configurationsMono.map(Configurations::getSubmit855).block();
		String submit856URL = configurationsMono.map(Configurations::getSubmit856).block();
		String submit810URL = configurationsMono.map(Configurations::getSubmit810).block();
		if ("855".equals(transactionType)) {
			return outboxRepository.getAllNonSentTransactions855(transactionType, id, partnerId)
					.flatMap(updatedCount -> {
						logger.info("Submitting ChangeRequest for ChangeRequestId: {}", id);
						if (updatedCount > 0) {
							ChangeRequest changeRequest = new ChangeRequest();
							changeRequest.setChangeRequestId(id);
							changeRequest.setPartnerId(partnerId);
							changeRequest.setBpdLink(submit855URL);

							try {
								return outboxService.b2bChangeRequestSubmit(changeRequest)
										.thenReturn("ChangeRequest submitted successfully for ID: " + id)
										.onErrorResume(IOException.class, e -> Mono.error(new RuntimeException(
												"Error submitting ChangeRequest for ID: " + id, e)));
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						} else {
							return Mono.just("No data found with id: " + id + " partnerId: " + partnerId);
						}
					}).defaultIfEmpty("No non-sent 855 transactions found.");

		} else if ("856".equals(transactionType)) {
			return outboxRepository.getAllNonSentTransactions856(transactionType, id, partnerId)
					.flatMap(updatedCount -> {
						logger.info("Submitting ShipmentRequest of shipmentId : {}", id);
						if (updatedCount > 0) {
							ShipmentRequest shipmentRequest = new ShipmentRequest();
							shipmentRequest.setShipmentId(id);
							shipmentRequest.setPartnerId(partnerId);
							shipmentRequest.setBpdLink(submit856URL);

							try {
								return shipmentService.b2bSendShipment(shipmentRequest)
										.thenReturn("Shipment submitted successfully for ID: " + id)
										.onErrorResume(IOException.class, e -> Mono.error(
												new RuntimeException("Error submitting Shipment for ID: " + id, e)));
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						} else {
							return Mono.just("No data found with id: " + id + " partnerId: " + partnerId);
						}
					}).defaultIfEmpty("No 856 transactions found.");

		} else if ("810".equals(transactionType)) {
			return outboxRepository.getAllNonSentTransactions810(transactionType, id, partnerId)
					.flatMap(updatedCount -> {
						logger.info("Submitting InvoiceRequest for invoiceId: {}", id);
						if (updatedCount > 0) {
							InvoiceRequest invoiceRequest = new InvoiceRequest();
							invoiceRequest.setInvoiceId(id);
							invoiceRequest.setPartnerId(partnerId);
							invoiceRequest.setBpdLink(submit810URL);

							try {
								return invoiceService.b2bInvoiceSubmit(invoiceRequest)
										.thenReturn("Invoice submitted successfully for ID: " + id)
										.onErrorResume(IOException.class, e -> Mono.error(
												new RuntimeException("Error submitting Invoice for ID: " + id, e)));
							} catch (IOException e) {
								throw new RuntimeException(e);
							}
						} else {
							return Mono.just("No data found with id: " + id + " partnerId: " + partnerId);
						}
					}).defaultIfEmpty("No non-sent 810 transactions found.");

		} else {
			return Mono.just("Unsupported transaction type: " + transactionType);
		}
	}

}
