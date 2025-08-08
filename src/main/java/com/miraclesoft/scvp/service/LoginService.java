package com.miraclesoft.scvp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.Configurations;
import com.miraclesoft.scvp.service.impl.LoginServiceImpl;

/**
 * The Interface LoginService.
 *
 * @author Narendar Geesidi
 */
@Service
public class LoginService {

    /** The login service impl. */
    @Autowired
    private LoginServiceImpl loginServiceImpl;
    

    /**
     * Logged in user details.
     *
     * @param loginId the login id
     * @param password the password
     * @return the string
     */
    public String loggedInUserDetails(final String loginId, final String password) {
        return loginServiceImpl.loggedInUserDetails(loginId, password);
    }
    
    public String generatePasscode(final String loginId) {
        return loginServiceImpl.generatePasscode(loginId);
    }
    
    public String validateOtp(final String loginId,final String otp) {
        return loginServiceImpl.validateOtp(loginId,otp);
    }
    
    public Configurations getConfigurations() {
        return loginServiceImpl.getConfigurations();
    }
    public String saveConfigurations(Configurations configurations) {
        return loginServiceImpl.saveConfigurations(configurations);
    }
    
    
    public String updateConfigurations(Configurations configurations) {
        return loginServiceImpl.updateConfigurations(configurations);
    }
   
   

}
