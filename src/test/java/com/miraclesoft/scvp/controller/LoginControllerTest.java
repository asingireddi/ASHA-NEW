package com.miraclesoft.scvp.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.UserInfoBean;
import com.miraclesoft.scvp.service.LoginService;

/**
 * The Class LoginControllerTest.java
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LoginControllerTest {
    @Mock
    private LoginService loginService;

    @InjectMocks
    private LoginController loginController = new LoginController();

    public static final String SES_USER_ID = "userId";
    public static final String SES_LOGIN_ID = "loginId";
    public static final String ACTIVE_FLAG = "isActive";
    public static final String SES_USER_NAME = "userName";
    public static final String SES_EMAIL_ID = "emailid";
    public static final String SES_USER_DEFAULT_FLOWID = "userDefaultFlowID";
    public static final String SES_ROLE_ID = "roleId";
    public static final String SES_USER_FLOW_MAP = "userFlowMap";
    public static final String SES_USER_ROLE_NAME = "userRoleName";
    public static final String SES_STATES_MAP = "statesMap";
    public static final String MSCVPROLE = "mscvpRole";
    public static final String DEFAULT_FLOW_NAME = "defaultFlowName";
    public static final String REQ_ERROR_INFO = "errorMessage";

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldGetLoggedInUserDetails() throws JSONException {
        // Given
        final UserInfoBean userInfo = UserInfoBean.builder()
                                                  .userId(1L)
                                                  .loginId("test")
                                                  .password("password")
                                                  .firstName("firstName")
                                                  .lastName("lastName")
                                                  .email("test@miraclesoft.com")
                                                  .active("A")
                                                  .build();

        final Map<Integer, String> flowsMap = new HashMap<Integer, String>();
        flowsMap.put(2, "Manufacturing");

        final JSONObject jsonObject = new JSONObject();
        jsonObject.put(SES_USER_ID, userInfo.getUserId());
        jsonObject.put(SES_LOGIN_ID, userInfo.getLoginId());
        jsonObject.put(ACTIVE_FLAG, userInfo.getActive());
        jsonObject.put(SES_USER_NAME, userInfo.getFirstName() + " " + userInfo.getLastName());
        jsonObject.put(SES_EMAIL_ID, userInfo.getMailId());
        jsonObject.put(SES_USER_DEFAULT_FLOWID, 2);
        jsonObject.put(SES_ROLE_ID, 2);
        jsonObject.put(SES_USER_FLOW_MAP, flowsMap);
        jsonObject.put(MSCVPROLE, "GIS On-call, Admin");
        jsonObject.put(DEFAULT_FLOW_NAME, "Manufacturing");

        // When
        when(loginService.loggedInUserDetails(userInfo.getLoginId(), userInfo.getPassword())).thenReturn(
                jsonObject.toString());

        // Then
		/*
		 * assertEquals(loginController.loggedInUserDetails(userInfo.getLoginId(),
		 * userInfo.getPassword()), jsonObject.toString());
		 */
    }

}
