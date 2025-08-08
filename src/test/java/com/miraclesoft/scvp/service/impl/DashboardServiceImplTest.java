package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.DateUtility.convertToSqlDate;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.contains;
import static org.mockito.Mockito.when;

import java.math.BigDecimal;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.Dashboard;
import com.miraclesoft.scvp.reports.Report;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

/**
 * The Class DashboardServiceImplTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class DashboardServiceImplTest {
    @Autowired
    private DashboardServiceImpl dashboardServiceImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @MockBean
    private Report gridDownload;
    
    
    @MockBean
    private TokenAuthenticationService tokenAuthenticationService;

    @MockBean
    private DataSourceDataProvider dataSourceDataProvider;
    
    @Mock
    private HttpServletRequest httpServletRequest;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void ediDailyStatsTest() {
        final Map<String, Object> row = new HashMap<String, Object>();
        row.put("transaction_type", "Mock Transaction Type");
        row.put("count", 1000L);
        final List<Map<String, Object>> output = Arrays.asList(row);

        final String expected = "[{\"count\":1000,\"type\":\"Mock Transaction Type\"}]";
        when(jdbcTemplate.queryForList(anyString())).thenReturn(output);
        final String response = dashboardServiceImpl.dailyTransactions();
        assertEquals(expected, response);
    }

  /*  @Test
    public void documentsDailyFailureRateTest() {
        final Map<String, Object> row = new HashMap<String, Object>();
        row.put("success", 2000L);
        row.put("failure", 3000L);
        row.put("pending", 4000L);
        final List<Map<String, Object>> output = Arrays.asList(row);
        final String expected = "[{\"count\":200,\"status\":\"Success\"},{\"count\":300,\"status\":\"Failure\"},{\"count\":400,\"status\":\"Pending\"}]";
        when(jdbcTemplate.queryForList(anyString())).thenReturn(output);

        final String response = dashboardServiceImpl.dailyFailureRate();
        assertEquals(expected, response);
    }
*/
    @Test
    public void topTpEdiInboundCountTest() {
        final Map<String, Object> row = new HashMap<String, Object>();
        row.put("count", 1000L);
        final List<Map<String, Object>> output = Arrays.asList(row);

        final List<Long> expected = Arrays.asList(1000L);
        when(jdbcTemplate.queryForList(anyString())).thenReturn(output);

        final List<Long> response = dashboardServiceImpl.topTpInboundCount("Mock(Test)");
        assertEquals(expected, response);
    }

    @Test
    public void topTpDocumentsOutboundCountTest() {
        Map<String, Object> row = new HashMap<String, Object>();
        row.put("count", 1000L);
        final List<Map<String, Object>> output = Arrays.asList(row);

        final List<Long> expected = Arrays.asList(1000L);
        when(jdbcTemplate.queryForList(anyString())).thenReturn(output);

        final List<Long> response = dashboardServiceImpl.topTpOutboundCount("Mock(Test)");
        assertEquals(expected, response);
    }

    @Test
    public void topTenTradingPartnersTest() {
        final Map<String, Object> row = new HashMap<String, Object>();
        row.put("partner_name", "Mock Partner Name");
        final List<Map<String, Object>> output = Arrays.asList(row);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(output);

        final String response = dashboardServiceImpl.topTenTradingPartners();
        assertEquals(response, "Mock Partner Name(null)");
    }

    @Test
    public void getDashboardManufacturingIBTest() throws Exception {
        final Dashboard dashboard = new Dashboard();
        dashboard.setTpId("Mock Tp id");

        final Map<String, Object> partnerMap = new HashMap<>();
        partnerMap.put("id", "Mock Sender Id");
        partnerMap.put("name", "Mock Sender");
        final List<Map<String, Object>> list = Arrays.asList(partnerMap);

        when(jdbcTemplate.queryForList(contains("tp"))).thenReturn(list);

        final Map<String, Object> inbound = new HashMap<String, Object>();
        inbound.put("sender_id", "Mock Sender Id");
        inbound.put("direction", "inbound");
        inbound.put("total", 1000L);
        final List<Map<String, Object>> output = Arrays.asList(inbound);
        when(jdbcTemplate.queryForList(contains("receiver_id"))).thenReturn(output);

        final String response = dashboardServiceImpl.dashboard(dashboard);
        assertEquals(response, "{\"data1\":[{\"name\":\"Mock Tp id_null\",\"y\":1000}],\"data2\":[]}");
    }

    @Test
    public void getDashboardManufacturingOBTest() throws Exception {
        final Dashboard dashboard = new Dashboard();
        dashboard.setTpId("Mock Tp id");

        final Map<String, Object> partnerMap = new HashMap<>();
        partnerMap.put("id", "Mock Receiver Id");
        partnerMap.put("name", "Mock Receiver");
        final List<Map<String, Object>> list = Arrays.asList(partnerMap);

        when(jdbcTemplate.queryForList(contains("tp"))).thenReturn(list);

        final Map<String, Object> inbound = new HashMap<String, Object>();
        inbound.put("receiver_id", "Mock Receiver Id");
        inbound.put("direction", "outbound");
        inbound.put("total", 1000L);
        final List<Map<String, Object>> output = Arrays.asList(inbound);
        when(jdbcTemplate.queryForList(contains("receiver_id"))).thenReturn(output);

        final String response = dashboardServiceImpl.dashboard(dashboard);
        assertEquals(response, "{\"data1\":[],\"data2\":[{\"name\":\"Mock Tp id_null\",\"y\":1000}]}");
    }

    @Ignore
    @Test
    public void monthlyVolumesTest() {
        final Map<String, Object> row = new HashMap<String, Object>();
        final String today = new SimpleDateFormat("MM/dd/yyyy HH:mm").format(new Date());
        row.put("temporary_date", convertToSqlDate(today));
        row.put("ib", BigDecimal.ONE);
        row.put("ob", BigDecimal.ZERO);
        final List<Map<String, Object>> output = Arrays.asList(row);

        final String expected = "[{\"day\":"
                + new SimpleDateFormat("MMM dd").format(new java.sql.Date(new Date().getTime()))
                + ",\"ib\":1,\"ob\":0}]";
        when(jdbcTemplate.queryForList(anyString())).thenReturn(output);
        final int userId = 0;
        final String response = dashboardServiceImpl.monthlyVolumes(31,userId);
        assertEquals(expected, response);
    }

    @Test
    public void warehouseVolumesTest() {
    	List<String> warehouses = new ArrayList<>();
        warehouses.add("MockA");
        warehouses.add("MockB");
        final Dashboard dashboard = Dashboard.builder()
                                             .warehouse(warehouses)
                                             .build();
        final Map<String, Object> row = new HashMap<String, Object>();
        row.put("warehouse", "Mock warehouse");
        row.put("ib", BigDecimal.ONE);
        row.put("ob", BigDecimal.ZERO);
        final List<Map<String, Object>> output = Arrays.asList(row);

        final String expected = "[{\"ob\":0,\"ib\":1,\"warehouse\":\"Mock warehouse\"}]";
        when(jdbcTemplate.queryForList(anyString())).thenReturn(output);

        final String response = dashboardServiceImpl.warehouseVolumes(dashboard);
        assertEquals(expected, response);
    }

    /*
     * @Test public void searchByTransactionGroupTest() {
     * 
     * when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files"))).
     * thenReturn( getDocumentRepositories(documentRepository));
     * when(httpServletRequest.getHeader(Mockito.any())).thenReturn("true");
     * when(tokenAuthenticationService.getUserIdfromToken()).thenReturn("10003");
     * Map<String,String> map = new HashMap<>(); map.put("Mock Sender Id",
     * "Mock Sender Id"); map.put("Mock Receiver Id", "Mock Receiver Id"); try {
     * when(dataSourceDataProvider.allTradingPartners()).thenReturn(map); } catch
     * (Exception e) { }
     * 
     * }
     */
}
