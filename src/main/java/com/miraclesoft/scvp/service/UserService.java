package com.miraclesoft.scvp.service;

import java.sql.SQLException;
import java.util.Map;

import javax.mail.MessagingException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.User;
import com.miraclesoft.scvp.service.impl.UserServiceImpl;

/**
 * The Interface UserService.
 *
 * @author Priyanka Kolla
 */
@Service
public class UserService {

	/** The user service impl. */
	@Autowired
	private UserServiceImpl userServiceImpl;

	/**
	 * Adds the user.
	 *
	 * @param user the user
	 * @return the string
	 * @throws SQLException       the SQL exception
	 * @throws MessagingException the messaging exception
	 */
	public String addUser(final User user) throws SQLException, MessagingException {
		return userServiceImpl.addUser(user);
	}

	/**
	 * User search.
	 *
	 * @param user the user
	 * @return the list
	 */
	public CustomResponse userSearch(final User user) {
		return userServiceImpl.userSearch(user);
	}

	/**
	 * User information.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	public User userInformation(final long userId) {
		return userServiceImpl.userInformation(userId);
	}

	/**
	 * User flows information.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	public User userFlowsInformation(final long userId) {
		return userServiceImpl.userFlowsInformation(userId);
	}

	/**
	 * Assign user flows.
	 *
	 * @param user the user
	 * @return the string
	 */
	public String assignRegisteredUserFlows(final User user) {
		return userServiceImpl.assignRegisteredUserFlows(user);
	}

	/**
	 * Update user info.
	 *
	 * @param user the user
	 * @return the string
	 * @throws SQLException the SQL exception
	 */
	public String updateUserInfo(final User user) throws SQLException {
		return userServiceImpl.updateUserInfo(user);
	}

	/**
	 * User profile.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	public User userProfile(final long userId) {
		return userServiceImpl.userProfile(userId);
	}

	/**
	 * Update user profile.
	 *
	 * @param user the user
	 * @return the string
	 */
	public String updateUserProfile(final User user) {
		return userServiceImpl.updateUserProfile(user);
	}

	/**
	 * Reset my password.
	 *
	 * @param loginId     the login id
	 * @param oldPassword the old password
	 * @param newPassword the new password
	 * @return the string
	 */
	public String resetMyPassword(final String loginId, final String oldPassword, final String newPassword) {
		return userServiceImpl.resetMyPassword(loginId, oldPassword, newPassword);
	}

	/**
	 * User by email.
	 *
	 * @param email the email
	 * @return the user
	 */
	public User userByEmail(final String email) {
		return userServiceImpl.userByEmail(email);
	}

	/**
	 * Forgot password.
	 *
	 * @param email the email
	 * @return the string
	 */
	public String forgotPassword(final String email) {
		return userServiceImpl.forgotPassword(email);
	}

	/**
	 * Active users.
	 *
	 * @return the map
	 */
	public Map<String, String> activeUsers() {
		return userServiceImpl.activeUsers();
	}

	/**
	 * Forgot username.
	 *
	 * @param email the email
	 * @return the string
	 */
	public String forgotUserName(final String email) {
		return userServiceImpl.forgotUserName(email);
	}

	/**
	 * delete user.
	 *
	 * @param userId the userId
	 * @return the string
	 */
	public String deleteUser(final Long userId) throws SQLException {
		return userServiceImpl.deleteUser(userId);
	}

	public String registerdUser(final User user) throws SQLException, MessagingException {
		return userServiceImpl.registerdUser(user);
	}

	public String registerdUsers(String email) {
		return userServiceImpl.validateAndRegister(email);
	}
}
