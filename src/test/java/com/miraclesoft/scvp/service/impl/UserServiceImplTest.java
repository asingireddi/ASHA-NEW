package com.miraclesoft.scvp.service.impl;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.mail.MessagingException;

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.mail.MailManager;
import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.User;

/**
 * The Class UserServiceImplTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class UserServiceImplTest {

    @Autowired
    private UserServiceImpl userServiceImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Mock
    Connection connection;
    
    @MockBean
    MailManager mailManager;

    @Mock
    PreparedStatement statement;

    @Before
    public void setUpBeforeClass() throws Exception {
        MockitoAnnotations.initMocks(this);

    }

    @Test
    public void userSearchTest() {
        // Given
        final User actualUser = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getUsersResultSet(Arrays.asList(actualUser)));

        // Then
        final CustomResponse users = userServiceImpl.userSearch(actualUser);
        assertEquals(users.getData(), 1);
//        for ( Map<String, Object> user : users) {
//            softly.assertThat(((User) user).getLoginId()).isEqualTo(actualUser.getLoginId());
//            softly.assertThat(((User) user).getEmail()).isEqualTo(actualUser.getEmail());
//        }
    }

   
    @Test
    public void assignUserFlowsSuccessTest() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.update(anyString(), eq(user.getUserId()))).thenReturn(1);

        // Then
        assertEquals(userServiceImpl.assignUserFlows(user), "Successfully assigned flows.");
    }

    @Test
    public void assignUserFlowsFailedTest() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.update(anyString(), eq(user.getUserId()))).thenReturn(0);

        // Then
        assertEquals(userServiceImpl.assignUserFlows(user), "Failed to assign flows!");
    }

    @Test
    public void userInformationTest() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getUsersResultSet(Arrays.asList(user)));

        // Then
        final User userOutput = userServiceImpl.userInformation(1L);
        softly.assertThat(userOutput.getRoleId()).isEqualTo(user.getRoleId());
        softly.assertThat(userOutput.getEmail()).isEqualTo(user.getEmail());
        softly.assertThat(userOutput.getFirstName()).isEqualTo(user.getFirstName());
        softly.assertThat(userOutput.getLastName()).isEqualTo(user.getLastName());
        softly.assertThat(userOutput.getStatus()).isEqualTo(user.getStatus());
        softly.assertThat(userOutput.getDepartmentId()).isEqualTo(-1);
    }

    @Test
    public void userFlowsInformationTest() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.queryForObject(contains("mscvp_roles"), new Object[] { ArgumentMatchers.<Object>any() },
                eq(String.class))).thenReturn("Mock Role Id");
        when(jdbcTemplate.queryForObject(contains("m_user_flows_action"),
                new Object[] { ArgumentMatchers.<Object>any() }, eq(Integer.class))).thenReturn(1000);
        when(jdbcTemplate.queryForList(contains("mscvp_flows"))).thenReturn(Collections.emptyList());
        when(jdbcTemplate.queryForList(contains("m_user_roles"))).thenReturn(getUsersResultSet(Arrays.asList(user)));

        // Then
        final User userOutput = userServiceImpl.userFlowsInformation(1L);
        softly.assertThat(userOutput.getLoginId()).isEqualTo(user.getLoginId());
        softly.assertThat(userOutput.getUserName()).isEqualTo(user.getUserName());
    }

    @Test
    public void userProfileTest() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getUsersResultSet(Arrays.asList(user)));

        // Then
        final User userOutput = userServiceImpl.userProfile(user.getUserId());
        softly.assertThat(userOutput.getEmail()).isEqualTo(user.getEmail());
        softly.assertThat(userOutput.getUserName()).isEqualTo(user.getUserName());
        softly.assertThat(userOutput.getOfficePhone()).isEqualTo(user.getOfficePhone());

    }

    @Test
    public void updateUserProfileTest() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.update(anyString(), (Object[]) ArgumentMatchers.<Object>any())).thenReturn(2);

        // Then
        assertEquals(userServiceImpl.updateUserProfile(user), "Profile updated successfully");
    }

    @Test
    public void userByEmailWithEmailTest() {
        // Given
        final User actualUser = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getUsersResultSet(Arrays.asList(actualUser)));

        // Then
        final User expectedUser = userServiceImpl.userByEmail(actualUser.getEmail());
        assertEquals(expectedUser.getEmail(), actualUser.getEmail());
    }

    @Test
    public void userByLoginIdTest() {
        // Given
        final User actualUser = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(getUsersResultSet(Arrays.asList(actualUser)));

        // Then
        final User expectedUser = userServiceImpl.userByLoginId(actualUser.getLoginId());
        assertEquals(expectedUser.getLoginId(), actualUser.getLoginId());
    }

    @Test
    public void activeUsersTest() {
        // Given
        final User activeUserOne = buildUser(1L, "test1", "firstName1", "lastName1", "mock1@miraclesoft.com", "A");
        final User activeUserTwo = buildUser(2L, "test2", "firstName2", "lastName2", "mock2@miraclesoft.com", "A");
        final User inactiveUser = buildUser(3L, "test3", "firstName3", "lastName3", "mock3@miraclesoft.com", "I");

        // When
        when(jdbcTemplate.queryForList(anyString()))
                .thenReturn(getUsersResultSet(Arrays.asList(activeUserOne, activeUserTwo)));

        // Then
        final Map<String, String> users = userServiceImpl.activeUsers();
        softly.assertThat(users.containsKey(activeUserOne.getEmail())).isTrue();
        softly.assertThat(users.containsKey(activeUserTwo.getEmail())).isTrue();
        softly.assertThat(users.containsKey(inactiveUser.getEmail())).isFalse();
    }

    @Ignore
    @Test
    public void addUserTest() throws SQLException, MessagingException {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "ngeesidi@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(0);
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(1);

        // Then
        assertEquals("User created successfully.", userServiceImpl.addUser(user));
    }

    @Test
    public void isUserExistsTest() throws Exception {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "ngeesidi@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(1);

        // Then
        assertThat(userServiceImpl.isUserExists(user.getEmail())).isTrue();
    }

    @Test
    public void isUserNotExistsTest() throws Exception {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "ngeesidi@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(0);

        // Then
        assertThat(userServiceImpl.isUserExists(user.getEmail())).isFalse();
    }

    @Test
    public void forgotPasswordForNotExistingEmailTest() {
        // Given
        final User user = buildUser(1L, "test", "firstName", "lastName", "mock@miraclesoft.com", "A");

        // When
        when(jdbcTemplate.update(anyString(), (Object[]) ArgumentMatchers.<Object>any())).thenReturn(2);

        // Then
        assertEquals(userServiceImpl.forgotPassword(user.getEmail()), "Email is not existed. Please try again.");
    }

    private List<Map<String, Object>> getUsersResultSet(final List<User> users) {
        final List<Map<String, Object>> usersFromDB = new ArrayList<Map<String, Object>>();
        for (final User user : users) {
            final Map<String, Object> userMap = new HashMap<String, Object>();
            userMap.put("id", user.getUserId());
            userMap.put("loginid", user.getLoginId());
            userMap.put("fnme", user.getFirstName());
            userMap.put("lnme", user.getLastName());
            userMap.put("email", user.getEmail());
            userMap.put("active", user.getStatus());
            userMap.put("role_id", user.getRoleId());
            userMap.put("office_phone", user.getOfficePhone());
            userMap.put("username", user.getFirstName() + " " + user.getLastName());
            usersFromDB.add(userMap);
        }
        return usersFromDB;
    }

    private User buildUser(final Long userId, final String loginId, final String firstName, final String lastName,
            final String email, final String status) {
        return User.builder().userId(userId).loginId(loginId).firstName(firstName).lastName(lastName).email(email)
                .status(status).officePhone("1230456789").roleId(2).userName(firstName + " " + lastName).build();
    }
    @Test
    public void deletePartnersForUserTestTrue() {

    final String responseString = "Records deleted successfully";

    final Long userId=1l;

    when(jdbcTemplate.update(Mockito.anyString(),Mockito.anyLong())).thenReturn(1);
    assertEquals(responseString,userServiceImpl.deletePartnersForUser(userId));

    }


    @Test
    public void deletePartnersForUserTestFalse() {

    final String responseString = "Failed to delete";

    final Long userId=null;

    when(jdbcTemplate.update(Mockito.anyString(),Mockito.anyLong())).thenReturn(1);
    assertEquals(responseString,userServiceImpl.deletePartnersForUser(userId));

    }
    
    @Test
    public void forgotUserNameTestTrue() throws Exception {

    final User actualUser = buildUser(1L, "test", "firstName", "lastName", "mss@miraclesoft.com", "A");

    final String email="mss@miraclesoft.com";


    final String response="Username sent to mail.";

    Mockito.doNothing().when(mailManager).sendUserName(actualUser.getEmail(),actualUser.getLoginId());

    assertThat(userServiceImpl.forgotUserName(email)).isEqualTo(response);



    }
}
