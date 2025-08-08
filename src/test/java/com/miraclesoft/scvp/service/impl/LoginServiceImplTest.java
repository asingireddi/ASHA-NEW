package com.miraclesoft.scvp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.UserInfoBean;

/**
 * The Class LoginServiceImplTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class LoginServiceImplTest {

    @Autowired
    private LoginServiceImpl loginServiceImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    public void userInformationTest() {
        final String loginId = "Mock LoginId";
        final List<Map<String, Object>> expected = new ArrayList<Map<String, Object>>();
        final Map<String, Object> user = new HashMap<String, Object>();
        user.put("id", 1000L);
        user.put("loginid", loginId);
        user.put("passwd", "passwd");
        user.put("fnme", "fnme");
        user.put("lnme", "lnme");
        user.put("email", "email");
        user.put("dept_id", 2000);
        user.put("active", "active");
        user.put("last_login_ts", new Timestamp(10000L));
        user.put("last_logout_ts", new Timestamp(10000L));
        expected.add(user);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(expected);
        final UserInfoBean result = loginServiceImpl.userInformation(loginId);
        assertEquals(result.getLoginId(), loginId);
    }

    @Test
    public void checkUserRolesTest() {
        final List<Map<String, Object>> rolesMapList = new ArrayList<Map<String, Object>>();
        final Map<String, Object> role = new HashMap<String, Object>();
        role.put("priority", 1000);
        role.put("role_id", 1000);
        rolesMapList.add(role);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(rolesMapList);

        final Map<Integer, Integer> rolesMap = loginServiceImpl.userRoles(1000);
        assertEquals(rolesMap.get(1000), new Integer(1000));
    }

}
