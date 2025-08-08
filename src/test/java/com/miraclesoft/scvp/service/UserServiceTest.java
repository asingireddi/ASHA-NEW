package com.miraclesoft.scvp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.mail.MessagingException;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.User;
import com.miraclesoft.scvp.service.impl.UserServiceImpl;

/**
 * The Test UserServiceTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class UserServiceTest {

    @InjectMocks
    private UserService userService;

    @Mock
    private UserServiceImpl userServiceImpl;

    @Test
    public void shouldAddUser() throws SQLException, MessagingException {
        // Given
        final User user = buildUser();

        // When
        doReturn("User added succesfully.").when(userServiceImpl)
                                           .addUser(user);

        // Then
        assertThat(userService.addUser(user)).isEqualTo("User added succesfully.");
    }

    @Test
    public void shouldSearchUsers() {
        // Given
        final User user = buildUser();
        final List<User> expectedUsers = Arrays.asList(user);

        // When
        doReturn(expectedUsers).when(userServiceImpl)
                               .userSearch(user);

        // Then
        assertThat(userService.userSearch(user)).isEqualTo(expectedUsers);
    }

    @Test
    public void shouldFindUserInformation() {
        // Given
        final User user = buildUser();

        // When
        doReturn(user).when(userServiceImpl)
                      .userInformation(user.getUserId());

        // Then
        assertThat(userService.userInformation(user.getUserId())).isEqualTo(user);
    }

    @Test
    public void shouldFindUserFlowsInformation() {
        // Given
        final User user = buildUser();

        // When
        doReturn(user).when(userServiceImpl)
                      .userFlowsInformation(user.getUserId());

        // Then
        assertThat(userService.userFlowsInformation(user.getUserId())).isEqualTo(user);
    }

    @Test
    public void shouldAssignUserFlows() throws SQLException, MessagingException {
        // Given
        final User user = buildUser();

        // When
        doReturn("Successfully flows assigned !").when(userServiceImpl)
                                                 .assignUserFlows(user);

        // Then
        assertThat(userService.assignRegisteredUserFlows(user)).isEqualTo("Successfully flows assigned !");
    }

    @Test
    public void shouldUpdateUserInfo() throws SQLException {
        // Given
        final User user = buildUser();

        // When
        doReturn("User Updated Successfully.").when(userServiceImpl)
                                              .updateUserInfo(user);

        // Then
        assertThat(userService.updateUserInfo(user)).isEqualTo("User Updated Successfully.");
    }

    @Test
    public void shouldFindUserProfile() {
        // Given
        final User user = buildUser();

        // When
        doReturn(user).when(userServiceImpl)
                      .userProfile(user.getUserId());

        // Then
        assertThat(userService.userProfile(user.getUserId())).isEqualTo(user);
    }

    @Test
    public void shouldUpdateProfile() throws SQLException {
        // Given
        final User user = buildUser();

        // When
        doReturn("Profile updated successfully").when(userServiceImpl)
                                                .updateUserProfile(user);

        // Then
        assertThat(userService.updateUserProfile(user)).isEqualTo("Profile updated successfully");
    }

    @Test
    public void shouldResetMyPassword() throws SQLException {
        // Given
        final User user = buildUser();

        // When
        doReturn("You have changed User password succesfully").when(userServiceImpl)
                                                              .resetMyPassword(user.getLoginId(), user.getOldPassword(),
                                                                      user.getNewPassword());

        // Then
        assertThat(
                userService.resetMyPassword(user.getLoginId(), user.getOldPassword(), user.getNewPassword())).isEqualTo(
                        "You have changed User password succesfully");
    }

    @Test
    public void shouldFindUserByEmail() {
        // Given
        final User user = buildUser();

        // When
        doReturn(user).when(userServiceImpl)
                      .userByEmail(user.getEmail());

        // Then
        assertThat(userService.userByEmail(user.getEmail())).isEqualTo(user);
    }

    @Test
    public void shouldGetForgotPassword() throws SQLException {
        // Given
        final User user = buildUser();

        // When
        doReturn("Password updated and sent to mail.").when(userServiceImpl)
                                                      .forgotPassword(user.getEmail());

        // Then
        assertThat(userService.forgotPassword(user.getEmail())).isEqualTo("Password updated and sent to mail.");
    }

    @Test
    public void shouldGetActiveUsers() throws SQLException {
        final Map<String, String> activeUsers = new TreeMap<String, String>();
        activeUsers.put("test1@miraclesoft.com", "test1");
        activeUsers.put("test2@miraclesoft.com", "test2");

        // When
        doReturn(activeUsers).when(userServiceImpl)
                             .activeUsers();

        // Then
        assertThat(userService.activeUsers()).isEqualTo(activeUsers);
    }

    private User buildUser() {
        return User.builder()
                   .userId(1L)
                   .loginId("test")
                   .password("password")
                   .email("test@miraclesoft.com")
                   .build();
    }
    @Test
    public void forgotUserNameTest() {

    //Given
    final User user = buildUser();

    //When
    doReturn("Username updated and sent to mail.").when(userServiceImpl)
    .forgotUserName(user.getEmail());

    //Then
    assertThat(userService.forgotUserName(user.getEmail())).isEqualTo("Username updated and sent to mail.");

    }
    
    
}
