package com.miraclesoft.scvp.controller;

import java.sql.SQLException;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.User;

import com.miraclesoft.scvp.service.UserService;


/**
 * The Class UserController.
 *
 * @author Priyanka Kolla
 */
@RestController
@RequestMapping("/user")
public class UserController {
    /** The user service. */
    @Autowired
    private UserService userService;

    /**
     * Adds the user.
     *
     * @param user the user
     * @return the string
     * @throws SQLException       the SQL exception
     * @throws MessagingException the messaging exception
     */
    @PostMapping("/add")
    public String addUser(@RequestBody final User user) throws SQLException, MessagingException {
        return userService.addUser(user);
    }

    /**
     * User search.
     *
     * @param user the user
     * @return the list
     */
    @PostMapping("/search")
    public CustomResponse userSearch(@RequestBody final User user) {
        return userService.userSearch(user);
    }

    /**
     * User information.
     *
     * @param userId the user id
     * @return the user
     */
    @GetMapping("/info/{userId}")
    public User userInformation(@PathVariable final long userId) {
        return userService.userInformation(userId);
    }

    /**
     * User flows information.
     *
     * @param userId the user id
     * @return the user
     */
    @GetMapping("/assignFlowinfo/{userId}")
    public User userFlowsInformation(@PathVariable final Long userId) {
        return userService.userFlowsInformation(userId);
    }

    /**
     * Assign flow.
     *
     * @param user the user
     * @return the string
     */
    @PostMapping("/assignFlow")
    public String assignFlow(@RequestBody final User user) {
        return userService.assignRegisteredUserFlows(user);
    }

    /**
     * Update user info.
     *
     * @param user the user
     * @return the string
     * @throws SQLException the SQL exception
     */
    @PostMapping("/updateUser")
    public String updateUserInfo(@RequestBody final User user) throws SQLException {
        return userService.updateUserInfo(user);
    }

    /**
     * User profile. 
     *
     * @param userId the user id
     * @return the user profile
     */
    @GetMapping("/getProfile/{userId}")
    public User userProfile(@PathVariable final Long userId) {
        return userService.userProfile(userId);
    }

    /**
     * Update user profile.
     *
     * @param user the user
     * @return the string
     */
    @PostMapping("/updateProfile")
    public String updateUserProfile(@RequestBody final User user) {
        return userService.updateUserProfile(user);
    }

    /**
     * Reset password.
     *
     * @param user the user
     * @return the string
     */
    @PostMapping("/resetPassword")
    public String resetPassword(@RequestBody final User user) {
        return userService.resetMyPassword(user.getLoginId(), user.getOldPassword(), user.getNewPassword());
    }

    /**
     * Forgot password.
     *
     * @param email the email
     * @return the string
     */
    @GetMapping("/forgotPassword/{email}")
    public String forgotPassword(@PathVariable final String email) {
        return userService.forgotPassword(email);
    }

    /**
     * Forgot userName.
     *
     * @param email the email
     * @return the string
     */
    @GetMapping("/forgotUserName/{email}")
    public String forgotUserName(@PathVariable final String email) {
        return userService.forgotUserName(email);
    }

    /**
     * Active users.
     *
     * @return the map
     */
    @GetMapping("/activeUsers")
    public Map<String, String> activeUsers() {
        return userService.activeUsers();
    }

    /**
     * Delete users.
     *
     * @return the String
     */
    @PostMapping("/deleteUser/{userId}")
    public String deleteUser(@PathVariable final Long userId) throws SQLException {
    return userService.deleteUser(userId);

    }
    /**
     * Adds the user.
     *
     * @param user the user
     * @return the string
     * @throws SQLException       the SQL exception
     * @throws MessagingException the messaging exception
     */
    @PostMapping("/register")
    public String registerUser(@RequestBody final User user) throws SQLException, MessagingException {
        return userService.registerdUser(user);
    }

    @GetMapping("/registerd")
    public ResponseEntity<String> registerdUser(@RequestParam String email) {
        String result = userService.registerdUsers(email);
        if ("success".equals(result)) {
            return ResponseEntity.ok("No email found");
        } else {
            return ResponseEntity.status(HttpStatus.OK).body(result);
        }
}
}