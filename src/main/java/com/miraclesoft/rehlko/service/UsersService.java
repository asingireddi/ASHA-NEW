package com.miraclesoft.rehlko.service;

import com.miraclesoft.rehlko.entity.MUser;
import com.miraclesoft.rehlko.entity.Users;
import com.miraclesoft.rehlko.repository.CustomersRepository;
import com.miraclesoft.rehlko.repository.MUserRepository;
import com.miraclesoft.rehlko.repository.UserRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;


@Service
public class UsersService {
//	@Autowired
//	private JavaMailSender emailSender;

    private final UserRepository userRepository;
    private final CustomersRepository customersRepository;
    private final MUserRepository mUserRepository;
    private static final Logger logger = LoggerFactory.getLogger(UsersService.class.getName());

    public UsersService(UserRepository userRepository, CustomersRepository customersRepository, MUserRepository mUserRepository) {
        this.userRepository = userRepository;
        this.customersRepository = customersRepository;
        this.mUserRepository = mUserRepository;
    }

    public Flux<Users> getAllUsers() {
        return userRepository.findAll();

    }

//    public Mono<Map<String, Object>> saveUser(Users user) {
//		logger.info("Executing the method :: saveUser ");
//    	Map<String, Object> response = new HashMap<String, Object>();
//
//    	response.put("message", "User registration failed");
//    	response.put("status", false);
//    	try {
//    		user.setCreatedAt(LocalDateTime.now());
//    		user.setUpdatedAt(LocalDateTime.now());
//
//    		user = userRepository.save(user).block();
//         if(user!= null) {
//        	 response.put("message", "User registered successfully");
//        	 response.put("status", true);
//         }
//    	} catch (Exception ex) {logger.error("Exception in saveUser ::{}", ex.getMessage());
//    	}
//		logger.info("Executed the method :: saveUser ");
//
//		return Mono.just(response);
//    }


    public Mono<Map<String, Object>> saveUser(Users user) {
        logger.info("Executing the method :: saveUser ");
        user.setCreatedAt(LocalDateTime.now());
        user.setUpdatedAt(LocalDateTime.now());
        return userRepository.save(user).flatMap(savedUser -> {
            Map<String, Object> response = new HashMap<>();
            response.put("message", "User Registered Success");
            response.put("status", true);
            response.put("data", savedUser);
            return Mono.just(response);
        }).doOnSuccess(success -> logger.info("Executed the method :: saveUser ")).onErrorResume(err -> {
            logger.error("Error while saving user {} ", err.getMessage());
            Map<String, Object> defaultResponse = new HashMap<String, Object>();
            defaultResponse.put("message", "User registration failed");
            defaultResponse.put("status", false);
            return Mono.just(defaultResponse);
        });
    }

    public Mono<Map<String, Object>> userLogin(Users user) {
        logger.info("Executing the method :: userLogin ");

        Map<String, Object> response = new HashMap<>();
        response.put("message", "User not found");
        response.put("status", false);
        response.put("data", new HashMap<>());
        return userRepository.findByEmailId(user.getEmailId()).flatMap(userData -> {
            if (user.getPassword().equals(userData.getPassword())) {
                response.put("message", "User login success");
                response.put("status", true);
                response.put("data", userData);
            } else {
                response.put("message", "User password wrong");
                response.put("status", false);
                response.put("data", userData);
            }
            logger.info("Executed the method :: userLogin ");
            return Mono.just(response);
        }).onErrorResume(err -> {
            logger.error("Error in the method :: userLogin {}", err.getMessage());
            response.put("message", "User login failed");
            response.put("status", false);
            response.put("data", new HashMap<>());
            return Mono.just(response);
        }).defaultIfEmpty(response);

    }

//    public Mono<Map<String, Object>> userLogin(Users user) {
//        logger.info("Executing the method :: userLogin ");
//        Map<String, Object> response = new HashMap<>();
//
//        response.put("message", "User login failed");
//        response.put("status", false);
//        response.put("data", new HashMap<>());
//        try {
//            Users userDedailes = userRepository.findByEmailId(user.getEmailId()).block();
//            if (userDedailes != null) {
//                if (userDedailes.getPassword().equals(user.getPassword())) {
//                    response.put("message", "User logged in successfully");
//                    response.put("status", true);
//                    response.put("data", userDedailes);
//                } else {
//                    response.put("message", "Incorrect password");
//                }
//            } else {
//                response.put("message", "User does not exist");
//            }
//        } catch (Exception ex) {
//            logger.error("Exception in userLogin ::{}", ex.getMessage());
//        }
//        logger.info("Executed the method :: userLogin ");
//
//        return Mono.just(response);
//    }

    public Mono<Map<String, Object>> getAllUser() {
        logger.info("Executing the method :: getAllUser ");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Failed to retrieve users");
        response.put("status", false);
        response.put("data", new ArrayList<>());

        return userRepository.findAll().collectList().map(usersList -> {
            if (!usersList.isEmpty()) {
                response.put("message", "Users retrieved successfully");
                response.put("status", true);
                response.put("data", usersList);
            }
            logger.info("Executed the method :: getAllUser ");

            return response;
        }).onErrorResume(ex -> {
            logger.error("Exception in getAllUser ::{}", ex.getMessage());
            return Mono.just(response);
        });
    }


    public Mono<Map<String, Object>> userProfile(Long userId) {
        logger.info("Executing the method :: userProfile ");
        Map<String, Object> response = new HashMap<>();
        response.put("message", "Failed to retrieve user profile");
        response.put("status", false);
        response.put("data", new ArrayList<>());

        return mUserRepository.findByUserId(userId).collectList().map(usersList -> {
            if (!usersList.isEmpty()) {
                response.put("message", "Users retrieved successfully");
                response.put("status", true);
                response.put("data", usersList);
            }
            logger.info("Executed the method :: userProfile ");

            return response;
        }).onErrorResume(ex -> {
            logger.error("Exception in userProfile ::{}", ex.getMessage());
            return Mono.just(response);
        });
    }

    public Mono<String> updateUserProfile(MUser mUser) {
        logger.info("Executing the method :: updateUserProfile ");
        return mUserRepository.findById(mUser.getId()).flatMap(existingUser -> {
            // Update the fields that need to be changed from mUser to existingUser
            if (mUser.getEmail() != null) {
                existingUser.setEmail(mUser.getEmail());
            }
            if (mUser.getDesignation() != null) {
                existingUser.setDesignation(mUser.getDesignation());
            }
            if (mUser.getLocation() != null) {
                existingUser.setLocation(mUser.getLocation());
            }
            if (mUser.getOrganization() != null) {
                existingUser.setOrganization(mUser.getOrganization());
            }
            if (mUser.getOfficePhone() != null) {
                existingUser.setOfficePhone(mUser.getOfficePhone());
            }
            if (mUser.getBuyerContacts() != null) {
                existingUser.setBuyerContacts(mUser.getBuyerContacts());
            }
            if (mUser.getTimezone() != null) {
                existingUser.setTimezone(mUser.getTimezone());
            }

            logger.info("Executed in updateUserProfile.");
            // Save the updated user and return a success message
            return mUserRepository.save(existingUser).thenReturn("Profile updated successfully");
        }).switchIfEmpty(Mono.just("User not found!"));
    }

//	public Mono<Map<String, Object>> updateUser(Users user) {
//		Map<String, Object> response = new HashMap<String, Object>();
//
//		response.put("message", "User associated with customer is failed");
//		response.put("status", false);
//		try {
//			user.setUpdatedAt(LocalDateTime.now());
//
//			int result = userRepository
//					.updateCustomerNameAndCustomerIdById(user.getCustomerName(), user.getCustomerId(), user.getId())
//					.block();
//			if (result > 0) {
//				response.put("message", "User associated with customer successfully");
//				response.put("status", true);
//			}
//		} catch (Exception ex) {
//			ex.printStackTrace();
//		}
//		return Mono.just(response);
//	}

//	public void sendSimpleMessage(String to, String subject, String text) {
//		SimpleMailMessage message = new SimpleMailMessage();
//		message.setTo(to);
//		message.setSubject(subject);
//		message.setText(text);
//		emailSender.send(message);
//		}
}

