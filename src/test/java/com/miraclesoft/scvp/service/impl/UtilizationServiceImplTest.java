package com.miraclesoft.scvp.service.impl;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.AwsS3Util;

/**
 * The Class UtilizationServiceImplTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class UtilizationServiceImplTest {

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @InjectMocks
    UtilizationServiceImpl utilizationServiceImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;

    @Mock
    private HttpServletRequest httpServletRequest;
    
    @Mock
    private SearchCriteria searchCriteria;
    
    @MockBean
    private AwsS3Util awsS3Util;

    @Test
    public void mscvpRolesTest() {
        final Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("id", 1);
        userMap.put("role_name", "MOCK");
        final List<Map<String, Object>> rows = Arrays.asList(userMap);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);

        final String response = utilizationServiceImpl.mscvpRoles();
        assertEquals(response, "[{\"name\":\"MOCK\",\"id\":1}]");
    }

    @Test
    public void documentTypesTest() {
        final Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("transaction", "mock");
        final List<Map<String, Object>> rows = Arrays.asList(userMap);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);

        final List<String> response = utilizationServiceImpl.documentTypes("MOCK");
        assertEquals(response.size(), 1);
    }

    @Test
    public void primaryFlowsTest() {
        final Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("id", 100);
        userMap.put("flowname", "MOCK");
        final List<Map<String, Object>> rows = Arrays.asList(userMap);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);

        final String response = utilizationServiceImpl.primaryFlows();
        assertEquals(response, "[{\"name\":\"MOCK\",\"id\":100}]");
    }
    
    @Test
    public void correlationsTest() {
        final Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("id", "mockId");
        userMap.put("name", "mockName");
        final List<Map<String, Object>> rows = Arrays.asList(userMap);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);

        final List<String> response = utilizationServiceImpl.correlations("All");
        assertEquals(response.size(), 1);
    }

//    @Test
//    public void warehousesTest() {
//        final Map<String, Object> warehouseMap = new HashMap<String, Object>();
//        warehouseMap.put("id", "mockId");
//        warehouseMap.put("warehouse", "warehouse");
//        final List<Map<String, Object>> rows = Arrays.asList(warehouseMap);
//
//        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);
//
//        final List<String> response = utilizationServiceImpl.warehouses("LIVE");
//        assertEquals(response.size(), 1);
//    }

    @Test
    public void parentWarehousesTest() {
        final Map<String, Object> parentWarehouseMap = new HashMap<String, Object>();
        parentWarehouseMap.put("id", "mockId");
        parentWarehouseMap.put("parent_warehouse", "warehouse");
        final List<Map<String, Object>> rows = Arrays.asList(parentWarehouseMap);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);

        final List<String> response = utilizationServiceImpl.parentWarehouses("LIVE");
        assertEquals(response.size(), 1);
    }

    @Test
    public void warehousesForTest() {
        final Map<String, Object> warehouseMap = new HashMap<String, Object>();
        warehouseMap.put("id", "mockId");
        warehouseMap.put("parent_warehouse", "warehouse 1");
        warehouseMap.put("warehouse", "warehouse A");
        final List<Map<String, Object>> rows = Arrays.asList(warehouseMap);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);

        final List<String> response = utilizationServiceImpl.warehousesFor(searchCriteria);
        assertEquals(response.size(), 1);
    }
    @Test
    public void getFile() throws IOException {
        String file = null;
        ResponseEntity<byte[]> response = null;
        when(awsS3Util.getFileFromAmazonS3(file)).thenReturn(response);
        
        assertEquals(utilizationServiceImpl.getFileFromAmazonS3(Mockito.any()), response);
    }

}
