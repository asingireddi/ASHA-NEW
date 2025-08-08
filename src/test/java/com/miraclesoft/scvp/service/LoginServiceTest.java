package com.miraclesoft.scvp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.UserInfoBean;
import com.miraclesoft.scvp.service.impl.LoginServiceImpl;

/**
 * The Test LoginServiceTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class LoginServiceTest {

    @InjectMocks
    private LoginService loginService;

    @Mock
    private LoginServiceImpl loginServiceImpl;

    @Test
    public void shouldGetLoggedInUserDetails() {
        // Given
        final UserInfoBean userInfoBean = UserInfoBean.builder()
                                                      .userId(1L)
                                                      .loginId("test")
                                                      .password("password")
                                                      .build();

        // When
        doReturn("User").when(loginServiceImpl)
                        .loggedInUserDetails(userInfoBean.getLoginId(), userInfoBean.getPassword());

        // Then
        final String actualUserInfo = loginService.loggedInUserDetails(userInfoBean.getLoginId(),
                userInfoBean.getPassword());
        assertThat(actualUserInfo).isEqualTo("User");
    }

}
