package com.miraclesoft.scvp.util;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
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

import com.google.common.base.Objects;

/**
 * The Class DataSourceDataProviderTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class DataSourceDataProviderTest {

    @Autowired
    private DataSourceDataProvider dataSourceDataProvider;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    public void shouldGetRoleNameByRoleId() throws Exception {
        // Given
        final int roleId = 2;
        final List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        final Map<String, Object> row1 = new HashMap<String, Object>();
        row1.put("id", 1);
        row1.put("role_name", "MSCVP ADMIN");
        final Map<String, Object> row2 = new HashMap<String, Object>();
        row2.put("id", 2);
        row2.put("role_name", "GIS On-call, Admin");
        rows.add(row1);
        rows.add(row2);

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(row2.get("role_name"));

        // Then
        assertEquals(dataSourceDataProvider.getRoleNameByRoleId(roleId), "GIS On-call, Admin");
    }

    @Test
    public void shouldGetFlowNameByFlowId() throws Exception {
        // Given
        final int flowId = 2;
        final List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        final Map<String, Object> row1 = new HashMap<String, Object>();
        row1.put("id", 1);
        row1.put("flowname", "Admin");
        final Map<String, Object> row2 = new HashMap<String, Object>();
        row2.put("id", 2);
        row2.put("flowname", "Manufacturing");
        rows.add(row1);
        rows.add(row2);

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(row2.get("flowname"));

        // Then
        assertEquals(dataSourceDataProvider.getFlowNameByFlowId(flowId), "Manufacturing");
    }

    @Test
    public void isAssignedFlowsToTest() throws Exception {
        // Given
        final Long userId = 1L;

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(1);

        // Then
        assertThat(dataSourceDataProvider.isAssignedFlowsTo(userId)).isTrue();
    }

    @Test
    public void isAdminTest() throws Exception {
        // Given
        final Long userId = 1L;

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(1);

        // Then
        assertThat(dataSourceDataProvider.isAdmin(userId)).isTrue();
    }

    @Test
    public void shouldGetStates() throws Exception {
        // Given
        final Map<String, Object> stateOne = new HashMap<String, Object>();
        stateOne.put("id", 1);
        stateOne.put("name", "USA");
        final Map<String, Object> stateTwo = new HashMap<String, Object>();
        stateTwo.put("id", 2);
        stateTwo.put("name", "UK");
        final List<Map<String, Object>> rows = Arrays.asList(stateOne, stateTwo);

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);

        // Then
        assertEquals(dataSourceDataProvider.getStates()
                                           .size(),
                2);
    }

    @Test
    public void shouldGetFlows() throws Exception {
        // Given
        final Map<Integer, String> flowsMap = new HashMap<Integer, String>();
        flowsMap.put(1, "Admin");
        flowsMap.put(2, "Manufacturing");

        final List<Map<String, Object>> rows = new ArrayList<Map<String, Object>>();
        final Map<String, Object> row1 = new HashMap<String, Object>();
        row1.put("id", 1);
        row1.put("flowname", "Admin");
        final Map<String, Object> row2 = new HashMap<String, Object>();
        row2.put("id", 2);
        row2.put("flowname", "Manufacturing");
        rows.add(row1);
        rows.add(row2);

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);

        // Then
        final Map<Integer, String> flows = dataSourceDataProvider.getFlows(1L);
        assertThat(Objects.equal(flowsMap, flows)).isTrue();
    }

}
