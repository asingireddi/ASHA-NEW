package com.miraclesoft.scvp.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.MessagingException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.User;
import com.miraclesoft.scvp.service.UserService;

/**
 * The Class UserControllerTest.java
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserControllerTest {

    @Mock
    private UserService userService;

    @InjectMocks
    private UserController userController = new UserController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldVerifyUserExistance() throws SQLException, MessagingException {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");

        // When
        when(userService.addUser(Mockito.any())).thenReturn("User already registered with this email Id!");

        // Then
        assertEquals(userController.addUser(user), "User already registered with this email Id!");
    }
    @Test
    public void shouldGetforgotUserName() throws SQLException {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");
        final String response = "Username sent to mail.";

        // When
        when(userService.forgotUserName(user.getEmail())).thenReturn(response);

        // Then
        assertEquals(userController.forgotUserName(user.getEmail()), response);
    }

    @Test
    public void shouldSearchUsers() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");
//        final Map<String,Object> result=new HashMap<>();
//        result.put("usersData", result);
        int count = 0;
        List<User> userList = Arrays.asList(user);
        final CustomResponse customResponse = new CustomResponse(userList, count);

        // When
        when(userService.userSearch(user)).thenReturn(customResponse);

        // then
        assertEquals(userController.userSearch(user), customResponse);
    }

    @Test
    public void shouldAddUser() throws SQLException, MessagingException {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");
        final String response = "User added succesfully.";

        // When
        when(userService.addUser(user)).thenReturn(response);

        // Then
        assertEquals(userController.addUser(user), response);
    }

    @Test
    public void shouldFindUserInformation() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");

        // When
        when(userService.userInformation(user.getUserId())).thenReturn(user);

        // Then
        assertEquals(userController.userInformation(user.getUserId()), user);
    }

    @Test
    public void shouldFindUserFlowsInformation() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");

        // When
        when(userService.userFlowsInformation(user.getUserId())).thenReturn(user);

        // Then
        assertEquals(userController.userFlowsInformation(user.getUserId()), user);
    }

    @Test
    public void shouldAssignUserFlows() throws SQLException, MessagingException {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");
        final String response = "Successfully flows assigned !";

        // When
        when(userService.assignRegisteredUserFlows(user)).thenReturn(response);

        // Then
        assertEquals(userController.assignFlow(user), response);
    }

    @Test
    public void shouldUpdateUserInfo() throws SQLException {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");
        final String response = "User Updated Successfully.";

        // When
        when(userService.updateUserInfo(user)).thenReturn(response);

        // Then
        assertEquals(userController.updateUserInfo(user), response);
    }

    @Test
    public void shouldFindUserProfile() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");

        // When
        when(userService.userProfile(user.getUserId())).thenReturn(user);

        // Then
        assertEquals(userController.userProfile(user.getUserId()), user);
    }

    @Test
    public void shouldUpdateProfile() throws SQLException {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");
        final String response = "Profile updated successfully";

        // When
        when(userService.updateUserProfile(user)).thenReturn(response);

        // Then
        assertEquals(userController.updateUserProfile(user), response);
    }

    @Test
    public void shouldResetMyPassword() throws SQLException {
        // Given
        final User user = User.builder()
                              .loginId("Test")
                              .oldPassword("Old")
                              .newPassword("password")
                              .build();
        final String response = "You have changed User password succesfully";

        // When
        when(userService.resetMyPassword(user.getLoginId(), user.getOldPassword(), user.getNewPassword())).thenReturn(
                response);

        // Then
        assertEquals(userController.resetPassword(user), response);
    }

    @Test
    public void shouldGetForgotPassword() throws SQLException {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "test@miraclesoft.com", "A");
        final String response = "Password updated and sent to mail.";

        // When
        when(userService.forgotPassword(user.getEmail())).thenReturn(response);

        // Then
        assertEquals(userController.forgotPassword(user.getEmail()), response);
    }

    @Test
    public void shouldGetActiveUsers() throws SQLException {
        // Given
        final Map<String, String> activeUsers = new TreeMap<String, String>();
        activeUsers.put("test1@miraclesoft.com", "test1");
        activeUsers.put("test2@miraclesoft.com", "test2");

        // When
        when(userService.activeUsers()).thenReturn(activeUsers);

        // Then
        assertEquals(userController.activeUsers(), activeUsers);
    }

    private User buildUser(final Long userId, final String loginId, final String firstName, final String lastName,
            final String email, final String status) {
        return User.builder()
                   .userId(userId)
                   .loginId(loginId)
                   .firstName(firstName)
                   .lastName(lastName)
                   .email(email)
                   .status(status)
                   .officePhone("1230456789")
                   .roleId(2)
                   .userName(firstName + " " + lastName)
                   .build();
    }

}
