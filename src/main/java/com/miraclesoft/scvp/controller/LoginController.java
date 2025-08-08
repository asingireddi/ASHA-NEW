package com.miraclesoft.scvp.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import javax.activation.FileTypeMap;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.Configurations;
import com.miraclesoft.scvp.model.User;
import com.miraclesoft.scvp.service.LoginService;

/**
 * The Class LoginController.
 *
 * @author Narendar Geesidi
 */
@RestController
public class LoginController {

    /** The login service. */
    @Autowired
    private LoginService loginService;

    @GetMapping("/healthcheck")
    public String getHealthCheck() {
        return "Rehlko mscvp Service Loaded";
    }

   
    /**
     * Logged in user details.
     *
     * @param loginId  the login id
     * @param password the password
     * @return the string
     */
    @PostMapping("/getUserInfo")
    public String loggedInUserDetails(@RequestBody User userDetails) {
        return loginService.loggedInUserDetails(userDetails.getLoginId(), userDetails.getPassword());
    }

//    @GetMapping("/validateOtp")
//    public String validateOtp(@RequestBody User userDetails) {
//        return loginService.validateOtp(userDetails.getLoginId(),userDetails.getOtp());
//    }
//    @GetMapping("/validateOtp/{loginId}/{otp}")
//    public String validateOtp(final String loginId,final String otp) {
//        return loginService.validateOtp(loginId,otp);
//    }
    @GetMapping("/validateOtp/{loginId}/{otp}")
    public String validateOtp(@PathVariable final String loginId,@PathVariable final String otp) {
        return loginService.validateOtp(loginId,otp);
    }
    
    @PutMapping("/generatePassCode")
    public String generatePasscode(@RequestBody User userDetails) {
        return loginService.generatePasscode(userDetails.getLoginId());
    }
    
    @GetMapping("/getConfigurations")
    public Configurations getConfigurations() {
        return loginService.getConfigurations();
    }
    
    @PostMapping("/saveConfigurations")
    public String saveConfigurations(@RequestBody Configurations configurations) {
        return loginService.saveConfigurations(configurations);
    }
    
    @PutMapping("/updateConfigurations")
    public String updateConfigurations(@RequestBody Configurations configurations) {
        return loginService.updateConfigurations(configurations);
    }
    
    @GetMapping("/getimage")
    public ResponseEntity<byte[]> getImage() throws IOException{
    	Configurations config=loginService.getConfigurations();
    	System.out.println(":::::::::::"+config.getGlobalImg());
        File img = new File(config.getGlobalImg());
        return ResponseEntity.ok().contentType(MediaType.valueOf(FileTypeMap.getDefaultFileTypeMap().getContentType(img))).body(Files.readAllBytes(img.toPath()));
    }

}