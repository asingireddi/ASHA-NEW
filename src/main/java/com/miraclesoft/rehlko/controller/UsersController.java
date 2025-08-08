package com.miraclesoft.rehlko.controller;

import java.util.Map;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.rehlko.entity.MUser;
import com.miraclesoft.rehlko.entity.Users;
import com.miraclesoft.rehlko.service.UsersService;

import reactor.core.publisher.Mono;

@RestController
@CrossOrigin(origins = "*")
@RequestMapping("/users")
public class UsersController {

	private final UsersService usersService;

    public UsersController(UsersService usersService) {
        this.usersService = usersService;
    }

//    @GetMapping("/login")
//    public Flux<Users> getAllUsers() {
//        return usersService.getAllUsers();
//        
//    }	
    
    @PostMapping("/registration")
    public Mono<Map<String, Object>> saveUser(@RequestBody Users user) {
    	return usersService.saveUser(user);
    }  
    
    @PostMapping("/login")
    public Mono<Map<String, Object>> userLogin(@RequestBody Users user) {
		return usersService.userLogin(user);
    	    }
    
    @GetMapping("/allusers")
    public Mono<Map<String, Object>> getAllUser() {    
    	return usersService.getAllUser();
    }
    
    @GetMapping("/getProfile/{userId}")
    public Mono<Map<String, Object>> userProfile(@PathVariable final Long userId) {
        return usersService.userProfile(userId);
    }
    
    @PostMapping("/updateProfile")
    public Mono<String> updateUserProfile(@RequestBody final MUser mUser) {
        return usersService.updateUserProfile(mUser);
    }
//    @PutMapping("/associate")
//    public Mono<Map<String, Object>> updateUser(@RequestBody Users user) {
//    	return usersService.updateUser(user);
//    }
//    @GetMapping("/send-email")
//    public String sendEmail() {
//    usersService.sendSimpleMessage("madhuchippala@gmail.com", "Test Subject", "Test Email Body");
//    return "Email Sent!";
//    }
}