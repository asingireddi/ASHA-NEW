package com.miraclesoft.rehlko.service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.server.ResponseStatusException;

import com.miraclesoft.rehlko.entity.Inbox;
import com.miraclesoft.rehlko.repository.ConfigurationsRepository;
import com.miraclesoft.rehlko.repository.InboxRepository;
import com.miraclesoft.rehlko.repository.MUserRoleRepository;

import jakarta.mail.MessagingException;
import jakarta.mail.PasswordAuthentication;
import jakarta.mail.Session;
import jakarta.mail.Transport;
import jakarta.mail.internet.InternetAddress;
import jakarta.mail.internet.MimeMessage;
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

@Service
public class InboxService {

	private static final Logger logger = LoggerFactory.getLogger(InboxService.class.getName());
	private final InboxRepository inboxRepository;
	private final ConfigurationsRepository configurationsRepository;
	private final MUserRoleRepository mUserRoleRepository;

	public InboxService(InboxRepository inboxRepository, ConfigurationsRepository configurationsRepository,
			MUserRoleRepository mUserRoleRepository) {
		this.inboxRepository = inboxRepository;
		this.configurationsRepository = configurationsRepository;
		this.mUserRoleRepository = mUserRoleRepository;

	}

	public Mono<Map<String, Object>> getInboxData(String partnerId) {
		logger.info("Executing the method :: getInboxData ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to retrieve inbox data");
		response.put("status", false);
		response.put("data", new ArrayList<>());

		return inboxRepository.getByPartnerId(partnerId).collectList().map(inboxList -> {
			if (!inboxList.isEmpty()) {
				response.put("message", "Inbox data retrieved successfully");
				response.put("status", true);
				response.put("data", inboxList);
				logger.info("Executed the method :: getInboxData ");
			}
			return response;
		}).onErrorResume(ex -> {
			logger.error(" getInboxData :: {}", ex.getMessage());
			return Mono.just(response);
		});
	}

//    public Mono<Map<String, Object>> saveToInbox(Inbox inbox) {
//        logger.info("Executing the method :: saveToInbox ");
//        Map<String, Object> response = new HashMap<String, Object>();
//
//        response.put("message", "Failed to save data into inbox");
//        response.put("status", false);
//        try {
//            System.out.println(inbox.getFileLocation());
//            logger.debug("File location :: {}", inbox.getFileLocation());
//            inbox = inboxRepository.save(inbox).block();
//            if (inbox != null) {
//                response.put("message", "Data saved into inbox successfully");
//                response.put("status", true);
//            }
//            logger.info("Executed the method :: saveToInbox ");
//        } catch (Exception ex) {
//            logger.error(" saveToInbox :: {}", ex.getMessage());
//        }
//        return Mono.just(response);
//    }

	public Mono<Map<String, Object>> saveToInbox(Inbox inbox) {
		logger.info("Executing the method :: saveToInbox ");
		Map<String, Object> defaultSaveErrorResponse = new HashMap<>();
		defaultSaveErrorResponse.put("message", "Failed to save data into inbox");
		defaultSaveErrorResponse.put("status", false);

		return inboxRepository.save(inbox).map(savedInbox -> {
			logger.info("Data saved into inbox successfully for ID: {}", savedInbox.getId());
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Data saved into inbox successfully");
			successResponse.put("status", true);
			successResponse.put("data", savedInbox);
			return successResponse;
		}).doOnSuccess(res -> logger.info("Executed the method :: saveToInbox ")).onErrorResume(Exception.class, ex -> {
			logger.error(" Error saveToInbox :: {}", ex.getMessage());
			return Mono.just(defaultSaveErrorResponse);
		});
	}

	public Mono<Map<String, Object>> getFileData(int id) {
		logger.info("Executing the method :: getFileData ");

		// Define a default error response map to be returned in case of any general
		// failure
		Map<String, Object> defaultErrorResponse = new HashMap<>();
		defaultErrorResponse.put("message", "Error while fetching file");
		defaultErrorResponse.put("status", false);
		defaultErrorResponse.put("data", new HashMap<>());

		// 1. Fetch Configurations reactively
		return configurationsRepository.findAll().next().flatMap(config -> {
			// 2. Fetch Inbox reactively based on the fetched config
			return inboxRepository.findById(id).flatMap(inbox -> {
				// This flatMap block will only execute if an Inbox is found
				String filePath = inbox.getFileLocation();
				String fileName = inbox.getFileName();

				logger.info("filepath before:::::" + filePath);

				// Adjust file path if it contains the S3 bucket name
				if (filePath.contains(config.getS3_bucket_name())) {
					filePath = filePath.replace("/" + config.getS3_bucket_name() + "/", "");
				}
				logger.info("filepath after:::::{}", filePath);

				// 3. Build S3AsyncClient using configurations
				S3AsyncClient s3AsyncClient = S3AsyncClient.builder()
						.credentialsProvider(StaticCredentialsProvider.create(AwsBasicCredentials
								.create(config.getS3_bucket_access_key(), config.getS3_bucket_sceret_key())))
						.region(Region.of(config.getS3_bucket_region())).build();

				// Construct the full S3 object key
				String s3ObjectKey = filePath + "/" + fileName;

				// 4. Check if object exists using headObject (non-blocking)
				HeadObjectRequest headObjectRequest = HeadObjectRequest.builder().bucket(config.getS3_bucket_name())
						.key(s3ObjectKey).build();

				return Mono.fromFuture(s3AsyncClient.headObject(headObjectRequest)).flatMap(headObjectResponse -> {
					// If headObject succeeds, the object exists. Now, get the object content.
					GetObjectRequest getObjectRequest = GetObjectRequest.builder().bucket(config.getS3_bucket_name())
							.key(s3ObjectKey).build();

					return Mono
							.fromFuture(s3AsyncClient.getObject(getObjectRequest, AsyncResponseTransformer.toBytes()))
							.map(responseBytes -> {
								// Map the SdkBytes to a byte array and create the success response
								Map<String, Object> successResponse = new HashMap<>();
								successResponse.put("message", "Fetching File Successfully");
								successResponse.put("status", true);
								successResponse.put("data", responseBytes.asByteArray());
								return successResponse;
							}).doFinally(signalType -> s3AsyncClient.close()); // Close client after operation
				})
						// Handle NoSuchKeyException specifically for when the object doesn't exist
						.onErrorResume(NoSuchKeyException.class, e -> {
							logger.warn("S3 object not found (NoSuchKeyException): bucket={}, key={}",
									config.getS3_bucket_name(), s3ObjectKey);
							return Mono.just(Map.of("message", "File not found in S3", "status", false, "data",
									HttpStatus.NOT_FOUND));
						})
						// Handle other general S3 exceptions during the S3 operation
						.onErrorResume(Exception.class, s3Exception -> {
							logger.error(" Error during S3 operation for file {}: {}", fileName,
									s3Exception.getMessage());
							return Mono.just(Map.of("message", "Error during S3 operation", "status", false, "data",
									HttpStatus.INTERNAL_SERVER_ERROR));
						}).doFinally(signalType -> s3AsyncClient.close()); // Ensure client is closed even if headObject
																			// fails
			})
					// 2a. Handle the case where inboxRepository.findById(id) returns Mono.empty()
					.switchIfEmpty(Mono.defer(() -> {
						logger.warn("File Id {} doesn't exist. Returning specific error response.", id);
						return Mono.just(
								Map.of("message", "File Id doesn't exists", "status", false, "data", new HashMap<>()));
					}));
		})
				// 5. Add logging for successful completion of the reactive chain
				.doOnSuccess(res -> logger.info("Executed the method :: getFileData "))
				// 6. Global error handling for any exception in the entire reactive flow
				.onErrorResume(Exception.class, overallException -> {
					logger.error(" Overall error in getFileData :: {}", overallException.getMessage());
					return Mono.just(defaultErrorResponse); // Return the predefined error response
				});
	}

//	public Mono<Map<String, Object>> getFileData(int id) throws IOException {
//		logger.info("Executing the method :: getFileData ");
//		Map<String, Object> response = new HashMap<>();
//		response.put("message", "Error while fetching file");
//		response.put("status", false);
//		response.put("data", new HashMap<>());
//		try {
//			Configurations config = configurationsRepository.findAll().blockFirst();
//
//			Inbox inbox = inboxRepository.findById(id).block();
//			if (inbox != null) {
//
//				String filePath = inbox.getFileLocation();
//				String fileName = inbox.getFileName();
//
//				logger.info("filepath before:::::" + filePath);
//
//				if (filePath.contains(config.getS3_bucket_name())) {
//
//					filePath = filePath.replace("/" + config.getS3_bucket_name() + "/", "");
//
//				}
//				logger.info("filepath after:::::{}", filePath);
////				String data = new String(Files.readAllBytes(Paths.get(filePath)));
////				response.put("data", data);
//				try {
//					BasicAWSCredentials awsCreds = new BasicAWSCredentials(config.getS3_bucket_access_key(),
//							config.getS3_bucket_sceret_key());
//					AmazonS3 s3Client = AmazonS3ClientBuilder.standard()
//							.withCredentials(new AWSStaticCredentialsProvider(awsCreds))
//							.withRegion(config.getS3_bucket_region()).build();
////			    final HeadBucketRequest request = new HeadBucketRequest(bucketName);
////
////			    try {
////			    	 s3Client.headBucket(request);
////			        System.out.println(true);
////			    } catch (NoSuchBucketException e) {
////			    	 System.out.println(false);
////			    }
//
////			    ObjectListing listing = s3Client.listObjects( bucketName, filePath );
////			    List<S3ObjectSummary> summaries = listing.getObjectSummaries();
////
////			    while (listing.isTruncated()) {
////			       listing = s3Client.listNextBatchOfObjects (listing);
////			       summaries.addAll (listing.getObjectSummaries());
////			    }
////
////			    System.out.println("summaries:::"+summaries.get(0).getKey());
//					// String awsFilePath=summaries.get(0).getKey();
//					boolean isObjectExist = s3Client.doesObjectExist(config.getS3_bucket_name(),
//							filePath + "/" + fileName);
//
//					if (isObjectExist) {
//						S3Object s3Object = s3Client.getObject(config.getS3_bucket_name(), filePath + "/" + fileName);
//						S3ObjectInputStream inputStream = s3Object.getObjectContent();
//						byte[] content = IOUtils.toByteArray(inputStream);
//						inputStream.close();
//						response.put("message", "Fetching File Successfully");
//						response.put("status", true);
//						response.put("data", content);
//					} else {
//						response.put("data", HttpStatus.SC_NOT_FOUND);
//					}
//
//				} catch (Exception exception) {
//					logger.error(" getFileFromAmazonS3 :: {}", exception.getMessage());
//					response.put("data", HttpStatus.SC_INTERNAL_SERVER_ERROR);
//				}
//			} else {
//				response.put("message", "File Id doesn't exists");
//			}
//			logger.info("Executed the method :: getFileData ");
//		} catch (Exception ex) {
//			logger.error(" Error getFileFromAmazonS3 :: {}", ex.getMessage());
//		}
//		return Mono.just(response);
//	}

	public Mono<Map<String, Object>> writeDataToFile(Inbox inbox) throws IOException {
		logger.info("Executing the method :: writeDataToFile ");
		Map<String, Object> response = new HashMap<>();
		logger.debug("File content ::" + inbox.getFileContent());
		System.out.println(":::::::" + inbox.getFileContent());
		String fileLocation = inbox.getFileLocation();
		String fileName = fileLocation.split("/")[2];
		System.out.println("fileName:::;" + fileName);
		logger.debug("FileName ::" + fileName);
		response.put("message", "Error while fetching file");
		response.put("status", false);
		try {
			String dirName = "D:/inbox/";
			Path dirPath = Paths.get(dirName);
			if (!Files.exists(dirPath)) {
				Files.createDirectory(dirPath);
			}
			Path filePath = dirPath.resolve(fileName);
			if (!Files.exists(filePath)) {
				Files.createFile(filePath);
			}
			Files.writeString(filePath, inbox.getFileContent());
			Files.readString(filePath);
			response.put("message", "File Content updated successfully");
			response.put("status", "success");
			logger.info("Executed the method :: writeDataToFile ");
		} catch (Exception ex) {
			logger.error(" writeDataToFile :: {}", ex.getMessage());
		}
		return Mono.just(response);
	}

	// public Mono<Integer> updateStatus(String correlationKey1, String status) {
//		return inboxRepository.updateStatusById(status,correlationKey1);
//	}
	public Mono<Map<String, String>> updateStatus(String correlationKey, String status, int id,
			String transactionType) {
		logger.info("Executing the method :: updateStatus ");
		Mono<Integer> result = null;
		try {
			if ("Accepted".equalsIgnoreCase(status)) {
				Date now = new Date();
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
				String formattedDate = sdf.format(now);
				logger.debug("Accepted date for correlationKey :: " + correlationKey);
				System.out.println(now);
				result = inboxRepository.updateStatusByIdtime(status, formattedDate, correlationKey, id,
						transactionType);

			} else {
				result = inboxRepository.updateStatusById(status, correlationKey, id, transactionType);
			}

		} catch (Exception e) {
			logger.error(" updateStatus :: " + e.getMessage());
		}
		return result.map(updatedCount -> {
			logger.info("Executed the method :: updateStatus ");
			if (updatedCount > 0) {
				return Map.of("Success", "status updated successfully for order: " + id);
			} else {
				return Map.of("Failed", "No status found for order: " + id);
			}
		}).defaultIfEmpty(Map.of("Failed", "status updated: " + id));
	}

//    public Mono<Map<String, Object>> getInboxDataById(int id) {
//        logger.info("Executing the method :: getInboxDataById ");
//        Map<String, Object> response = new HashMap<>();
//        response.put("message", "Error while fetching data");
//        response.put("status", false);
//        response.put("data", new HashMap<>());
//        try {
//            Inbox inbox = inboxRepository.findById(id).block();
//            if (inbox != null) {
//                response.put("message", "Fetching data Successfully");
//                response.put("status", true);
//                response.put("data", inbox);
//
//            } else {
//                response.put("message", "data doesn't exists");
//            }
//            logger.info("Executed the method :: getInboxDataById ");
//        } catch (Exception ex) {
//            logger.error(" getInboxDataById :: {}", ex.getMessage());
//        }
//        return Mono.just(response);
//    }

	public Mono<Map<String, Object>> getInboxDataById(int id) {
		logger.info("Executing the method :: getInboxDataById ");
		Map<String, Object> defaultFetchErrorResponse = new HashMap<>();
		defaultFetchErrorResponse.put("message", "Error while fetching data");
		defaultFetchErrorResponse.put("status", false);
		defaultFetchErrorResponse.put("data", new HashMap<>());

		return inboxRepository.findById(id).map(inbox -> {
			logger.info("Fetching data Successfully for ID: {}", id);
			Map<String, Object> successResponse = new HashMap<>();
			successResponse.put("message", "Fetching data Successfully");
			successResponse.put("status", true);
			successResponse.put("data", inbox);
			return successResponse;
		}).switchIfEmpty(Mono.defer(() -> {
			logger.warn("Data for ID {} doesn't exist.", id);
			return Mono.just(Map.of("message", "data doesn't exists", "status", false, "data", new HashMap<>()));
		})).doOnSuccess(res -> logger.info("Executed the method :: getInboxDataById ")).onErrorResume(Exception.class,
				ex -> {
					logger.error(" Error getInboxDataById :: {}", ex.getMessage());
					return Mono.just(defaultFetchErrorResponse);
				});
	}

	public Mono<Map<String, String>> updateFakStatus(String id, String status) {
		logger.info("Executing the method :: updateFakStatus ");
		Mono<Integer> result = null;
		try {
			result = inboxRepository.updateFakStatusById(status, id);
			logger.info("Executed the method :: updateFakStatus ");
		} catch (Exception e) {
			logger.error(" updateFakStatus :: {}", e.getMessage());
		}
		return result.map(updatedCount -> {
			if (updatedCount > 0) {
				return Map.of("Success", "status updated successfully for ID: " + id);
			} else {
				return Map.of("Failed", "No status found for ID: " + id);
			}
		}).defaultIfEmpty(Map.of("Failed", "status updated " + id));
	}

	public Mono<Map<String, Object>> getInboxDataByStatus(String status) {
		logger.info("Executing the method :: getInboxDataByStatus ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to retrieve inbox data");
		response.put("status", false);
		response.put("data", new ArrayList<>());
		return inboxRepository.getInboxDataByStatus(status).collectList().map(inboxList -> {
			if (!inboxList.isEmpty()) {
				response.put("message", "Inbox data retrieved successfully");
				response.put("status", true);
				response.put("data", inboxList);
			}
			logger.info("Executed the method :: getInboxDataByStatus ");
			return response;
		}).onErrorResume(ex -> {
			logger.error(" getInboxDataByStatus :: {}", ex.getMessage());
			return Mono.just(response);
		});
	}

	public Mono<Map<String, Object>> b2bFakStatusSubmit(Inbox inbox) throws IOException {
		logger.info("Executing the method :: b2bFakStatusSubmit ");
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Error while getting data from bpdurl");
		try {
			final String uriForWithoutSsl = inbox.getBpdLink();
			final HttpHeaders headers = new HttpHeaders();
			headers.setContentType(MediaType.APPLICATION_XML);
			final StringBuilder xml = new StringBuilder();
			xml.append("<LookUpDetails> <fileName>").append(inbox.getFileName()).append("</fileName> <fileLocation>")
					.append(inbox.getFileLocation()).append("</fileLocation> <correlationKey1>")
					.append(inbox.getCorrelationKey1()).append("</correlationKey1> <id>").append(inbox.getId())
					.append("</id><partnerId>").append(inbox.getPartnerId()).append("</partnerId>")
					.append("</LookUpDetails>");
			logger.debug("xml content ::" + xml);
			System.out.println(xml);
			final HttpEntity<String> entityForWithoutSsl = new HttpEntity<String>(xml.toString(), headers);
			final RestTemplate restTemplate = new RestTemplate();
			Thread.sleep(10000);
			final ResponseEntity<String> data = restTemplate.postForEntity(uriForWithoutSsl, entityForWithoutSsl,
					String.class);
			if (data.getStatusCode().value() == 250) {
				response.put("success", true);
				response.put("message", "Request Sent to B2B Successfully");
			}
			logger.info("Executed the method :: b2bFakStatusSubmit ");
		} catch (final Exception e) {
			response.put("success", false);
			response.put("message", "Request Failed to B2B. Please Contact Administrator:::" + e.getMessage());
			logger.error(" b2bFakStatusSubmit :: {}", e.getMessage());
		}
		return Mono.just(response);
	}

	public void sendSimpleMessage() throws MessagingException {
		logger.info("Executing the method :: sendSimpleMessage ");
		Properties properties = new Properties();
		properties.setProperty("mail.smtp.host", "smtppro.zoho.com");
		properties.setProperty("mail.smtp.socketFactory.class", "javax.net.ssl.SSLSocketFactory");
		properties.setProperty("mail.smtp.socketFactory.fallback", "false");
		properties.setProperty("mail.smtp.port", "465");
		properties.setProperty("mail.smtp.socketFactory.port", "465");
		properties.put("mail.smtp.ssl.enable", "true");
		properties.put("mail.smtp.auth", "true");
		properties.put("mail.debug", "true");
		properties.put("mail.store.protocol", "pop3");
		properties.put("mail.transport.protocol", "smtp");
		properties.put("mail.debug.auth", "true");
		properties.setProperty("mail.pop3.socketFactory.fallback", "false");
		Session session = Session.getDefaultInstance(properties, new jakarta.mail.Authenticator() {
			@Override
			protected PasswordAuthentication getPasswordAuthentication() {
				return new PasswordAuthentication("mchippala@mirclesoft.com", "hRaPgip4mcjS");
			}
		});
		try {
			MimeMessage message = new MimeMessage(session);
			message.setFrom(new InternetAddress("mchippala@mirclesoft.com"));
			message.setRecipients(MimeMessage.RecipientType.TO, InternetAddress.parse("mchippala@mirclesoft.com"));
			message.setSubject("Test Subject");
			message.setText("Test Email Body");
			Transport.send(message);
			logger.info("Executed the method :: sendSimpleMessage ");
		} catch (MessagingException e) {
			logger.error(" sendSimpleMessage :: {}", e.getMessage());
		}
	}

	public Mono<Map<String, Object>> getAllPartners() {
		Map<String, Object> response = new HashMap<>();
		response.put("message", "Failed to retrieve inbox data");
		response.put("status", false);
		response.put("data", new ArrayList<>());

		logger.info("Fetching all partners...");

		return inboxRepository.getAllPartners().collectList().map(inboxList -> {
			if (!inboxList.isEmpty()) {
				logger.info("Successfully retrieved {} partner records", inboxList.size());
				response.put("message", "Partners data retrieved successfully");
				response.put("status", true);
				response.put("data", inboxList);
			} else {
				logger.warn("No partner records found");
			}
			return response;
		}).doOnError(ex -> logger.error("Error while retrieving partners data", ex))
				.onErrorResume(ex -> Mono.just(response));
	}

	// public Flux<Inbox>fetch(String transactionType,String startDate, String
	// endDate, String correlationKey1) {
//		
//		return inboxRepository.findByFilter(transactionType,startDate,endDate, correlationKey1);
//	}

	public Flux<Inbox> fetch(String transactionType, String startDate, String endDate, String partnerId,
			String correlationKey1, String status, Boolean trashFlag, Boolean archiveFlag) {

		logger.info(
				"Fetching Inbox with filters - transactionType: {}, startDate: {}, endDate: {}, partnerId: {}, correlationKey1: {}, status: {}, trashFlag: {}, archiveFlag: {}",
				transactionType, startDate, endDate, partnerId, correlationKey1, status, trashFlag, archiveFlag);

		transactionType = (transactionType != null && !transactionType.isEmpty()) ? transactionType : null;
		startDate = (startDate != null && !startDate.isEmpty()) ? startDate : null;
		endDate = (endDate != null && !endDate.isEmpty()) ? endDate : null;
		partnerId = (partnerId != null && !partnerId.isEmpty()) ? partnerId : null;
		correlationKey1 = (correlationKey1 != null && !correlationKey1.isEmpty()) ? correlationKey1 : null;
		trashFlag = (trashFlag != null) ? trashFlag : false;
		archiveFlag = (archiveFlag != null) ? archiveFlag : false;
		status = (status != null && !status.isEmpty()) ? status.toLowerCase() : null;

		return inboxRepository
				.findByFilter(transactionType, startDate, endDate, partnerId, correlationKey1, status, trashFlag,
						archiveFlag)
				.doOnComplete(() -> logger.info("Fetch completed successfully"))
				.doOnError(e -> logger.error("Error during fetch", e));
	}

	public Mono<String> updateInboxStatus(String correlationKey) {
		logger.info("Executing the method :: updateInboxStatus ");
		Mono<String> result = null;

		try {
			result = inboxRepository.updateByCorrelationId(correlationKey);
			logger.info("Executed the method :: updateInboxStatus ");

		} catch (Exception e) {
			logger.error(" updateInboxStatus :: {}", e.getMessage());
		}

		return result;
	}

	public LocalDateTime convertStringtoDate(String date) {
		DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-dd-MM HH:mm:ss");
		return LocalDateTime.parse(date, formatter);
	}

	public Mono<Void> updateFlags(List<String> keys, String type, boolean flagValue) {
		List<Mono<?>> updates = keys.stream().map(key -> {
			if ("trash".equalsIgnoreCase(type)) {
				return inboxRepository.updateTrashFlag(key, flagValue);
			} else if ("archive".equalsIgnoreCase(type)) {
				return inboxRepository.updateArchiveFlag(key, flagValue);
			} else {
				return Mono.error(new IllegalArgumentException("Invalid flag type"));
			}
		}).toList();

		return Mono.when(updates).then();
	}

	public Mono<List<String>> getDistinctStatuses() {
		logger.info("Fetching distinct statuses...");
		return inboxRepository.findDistinctStatuses().collectList()
				.doOnSuccess(statuses -> logger.info("Successfully fetched statuses: {}", statuses))
				.doOnError(error -> logger.error("Failed to fetch distinct statuses", error)).onErrorResume(error -> {
					return Mono.error(new RuntimeException("Unable to fetch statuses at the moment", error));
				});
	}

	public Mono<Void> updateInboxStatus(Long userId, List<String> correlationKeys, String status) {
		if (userId == null) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "User ID cannot be null."));
		}
		if (correlationKeys == null || correlationKeys.isEmpty()) {
			return Mono.error(new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Correlation keys list cannot be null or empty."));
		}
		if (status == null || status.isBlank()) {
			return Mono
					.error(new ResponseStatusException(HttpStatus.BAD_REQUEST, "New status cannot be null or empty."));
		}
		return mUserRoleRepository.findByUserId(userId)
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
						"User not found or does not have any assigned roles.")))
				.filter(userRole -> userRole.getRoleId() != null && userRole.getRoleId().equals(4))
				.switchIfEmpty(Mono.error(new ResponseStatusException(HttpStatus.FORBIDDEN,
						"User does not have the 'Business Analyst' role to perform this operation.")))
				.flatMap(userRole -> inboxRepository.updateStatusByCorrelationKey1(status, correlationKeys))
				.flatMap(rowsAffected -> {
					if (rowsAffected == 0) {
						return Mono.error(new ResponseStatusException(HttpStatus.NOT_FOUND,
								"No inbox records found with the provided correlation keys, or update failed."));
					}
					return Mono.empty(); // Successfully updated
				}).onErrorResume(Throwable.class, e -> {
					if (e instanceof ResponseStatusException) {
						return Mono.error(e);
					}
					return Mono.error(new ResponseStatusException(HttpStatus.INTERNAL_SERVER_ERROR,
							"An unexpected error occurred. Please try again.", e));
				}).then();
	}
}
