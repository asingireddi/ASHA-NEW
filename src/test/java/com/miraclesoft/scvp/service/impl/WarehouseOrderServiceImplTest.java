package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.DateUtility.getTodayWithCustomTime;
import static org.junit.Assert.assertEquals;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.Mockito.when;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.junit.Before;
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
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;
import static org.springframework.security.test.web.servlet.setup.SecurityMockMvcConfigurers.*;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Partner;
import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.model.WarehouseOrder;
import com.miraclesoft.scvp.reports.Report;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

/**
 * The Class WarehouseOrderServiceImplTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@WebAppConfiguration
public class WarehouseOrderServiceImplTest {

    @InjectMocks
    private WarehouseOrderServiceImpl warehouseOrderServiceImpl;

    @Autowired
    private WarehouseOrderServiceImpl warehouseOrderServiceImpl1;
    
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
    public void warehouseOrderSearchIBTest() {
        // Given
        final WarehouseOrder warehouseOrder = buildWarehouseOrder("Mock Id", "INBOUND", "Mock Sender Id",
                "Mock Receiver Id", "SUCCESS", "ACCEPTED");
        final SearchCriteria searchCriteria = buildSearchCriteria("LIVE", warehouseOrder);
        when(httpServletRequest.getHeader(Mockito.any())).thenReturn("true");
        when(tokenAuthenticationService.getUserIdfromToken()).thenReturn("10003");
        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files")))
                .thenReturn(getWarehouseOrderResultSet(warehouseOrder));
        Map<String,String> map = new HashMap<>();
        map.put("Mock Sender Id", "Mock Sender Id");
        map.put("Mock Receiver Id", "Mock Receiver Id");
        try {
            when(dataSourceDataProvider.allTradingPartners()).thenReturn(map);
        } catch (Exception e) {
        }
        
        // Then
        final CustomResponse response = warehouseOrderServiceImpl.search(searchCriteria);
        assertEquals(1, response.getData());
//        assertEquals("Mock Sender Id", response.get("partnerName"));
    }

    @Test
    public void warehouseOrderSearchOBTest() {
        // Given
        final WarehouseOrder warehouseOrder = buildWarehouseOrder("Mock Id", "OUTBOUND", "Mock Sender Id",
                "Mock Receiver Id", "SUCCESS", "ACCEPTED");
        final SearchCriteria searchCriteria = buildSearchCriteria("LIVE", warehouseOrder);
        Map<String,String> map = new HashMap<>();
        map.put("Mock Sender Id", "Mock Sender Id");
        map.put("Mock Receiver Id", "Mock Receiver Id");
        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files")))
                .thenReturn(getWarehouseOrderResultSet(warehouseOrder));
        when(httpServletRequest.getHeader(Mockito.any())).thenReturn("true");
        try {
            when(dataSourceDataProvider.allTradingPartners()).thenReturn(map);
        } catch (Exception e) {
        }
        when(tokenAuthenticationService.getUserIdfromToken()).thenReturn("10003");
        // Then
        final CustomResponse response = warehouseOrderServiceImpl.search(searchCriteria);
        assertEquals(1, response.getData());
//        assertEquals("Mock Receiver Id", response.get("partnerName"));
    }

    @Test
    public void warehouseOrderSecondarySearchTest() {
        // Given
        final WarehouseOrder warehouseOrder = buildWarehouseOrder("Mock Id", "INBOUND", "Mock Sender Id",
                "Mock Receiver Id", "SUCCESS", "ACCEPTED");
        final SearchCriteria searchCriteria = buildSecondarySearchCriteria("LIVE", warehouseOrder);
        when(httpServletRequest.getHeader(Mockito.any())).thenReturn("true");
        when(tokenAuthenticationService.getUserIdfromToken()).thenReturn("10003");
        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files")))
                .thenReturn(getWarehouseOrderResultSet(warehouseOrder));
        Map<String,String> map = new HashMap<>();
        map.put("Mock Sender Id", "Mock Sender Id");
        map.put("Mock Receiver Id", "Mock Receiver Id");
        try {
            when(dataSourceDataProvider.allTradingPartners()).thenReturn(map);
        } catch (Exception e) {
        }
        // Then
        final CustomResponse response = warehouseOrderServiceImpl.search(searchCriteria);
        assertEquals(1, response.getData());
//        assertEquals("Mock Sender Id", response.get("partnerName"));
    }

    @Test
    public void warehouseOrderLiveDbDetailInfoTest() {
        // Given
        final WarehouseOrder warehouseOrder = buildWarehouseOrder("Mock Id", "INBOUND", "Mock Sender Id",
                "Mock Receiver Id", "SUCCESS", "ACCEPTED");
        warehouseOrder.setDepositorOrderNumber("Deposit order number");

        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files")))
                .thenReturn(getWarehouseOrderResultSet(warehouseOrder));
        when(httpServletRequest.getHeader(Mockito.any())).thenReturn("true");
        when(tokenAuthenticationService.getUserIdfromToken()).thenReturn("10003");
        Map<String,String> map = new HashMap<>();
        map.put("Mock Sender Id", "Mock Sender Id");
        map.put("Mock Receiver Id", "Mock Receiver Id");
        try {
            when(dataSourceDataProvider.allTradingPartners()).thenReturn(map);
        } catch (Exception e) {
        }
        // Then
        final WarehouseOrder response = warehouseOrderServiceImpl1.detailInfo(warehouseOrder.getDepositorOrderNumber(),
                warehouseOrder.getFileId(), "LIVE");
        assertEquals("Mock Sender Id", response.getSenderName());
    }

    @Test
    public void warehouseOrderArchiveDbDetailInfoTest() {
        // Given
        final WarehouseOrder warehouseOrder = buildWarehouseOrder("Mock Id", "OUTBOUND", "Mock Sender Id",
                "Mock Receiver Id", "SUCCESS", "ACCEPTED");
        warehouseOrder.setDepositorOrderNumber("Deposit order number");

        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("archive_files")))
                .thenReturn(getWarehouseOrderResultSet(warehouseOrder));
        Map<String,String> map = new HashMap<>();
        map.put("Mock Sender Id", "Mock Sender Id");
        map.put("Mock Receiver Id", "Mock Receiver Id");
        try {
            when(dataSourceDataProvider.allTradingPartners()).thenReturn(map);
        } catch (Exception e) {
        }
        // Then
        final WarehouseOrder response = warehouseOrderServiceImpl1.detailInfo(warehouseOrder.getDepositorOrderNumber(),
                warehouseOrder.getFileId(), "ARCHIVE");
        assertEquals("Mock Receiver Id", response.getReceiverName());
    }

    @Test
    public void warehouseOrderExcelDownloadTest() throws IOException {
        // Given
        final WarehouseOrder warehouseOrder = buildWarehouseOrder("Mock Id", "INBOUND", "Mock Sender Id",
                "Mock Receiver Id", "SUCCESS", "ACCEPTED");
        final SearchCriteria searchCriteria = buildSearchCriteria("LIVE", warehouseOrder);

        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files")))
                .thenReturn(getWarehouseOrderResultSet(warehouseOrder));
        when(gridDownload.warehouseOrderExcelDownload(anyList())).thenReturn("mock-file-name");
        when(httpServletRequest.getHeader(Mockito.any())).thenReturn("true");
        when(tokenAuthenticationService.getUserIdfromToken()).thenReturn("10003");
        // Then
        final File mockFile = new File("mock-file-name");
        mockFile.createNewFile();
        final FileWriter fileWriter = new FileWriter(mockFile);
        fileWriter.write("Mock message");
        fileWriter.close();
        final ResponseEntity<InputStreamResource> response = warehouseOrderServiceImpl1.download(searchCriteria);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        mockFile.delete();
    }

    private WarehouseOrder buildWarehouseOrder(final String fileId, final String direction, final String senderId,
            final String receiverId, final String status, final String ackStatus) {
        return WarehouseOrder.builder().id(1).fileId(fileId).transactionType("940").direction(direction)
                .senderId(senderId).receiverId(receiverId).status(status).ackStatus(ackStatus).warehouse("Warehouse")
                .primaryKeyValue("123").secondaryKeyValue("12345").build();
    }

    private SearchCriteria buildSearchCriteria(final String database, final WarehouseOrder warehouseOrder) {
    	List<String> warehouses = Arrays.asList("MockA", "MockB");
        List<String> ackStatus = Arrays.asList("ACCEPTED", "PENDING");
        return SearchCriteria.builder().database(database).transactionType(warehouseOrder.getTransactionType())
                .senderId(warehouseOrder.getSenderId()).receiverId(warehouseOrder.getReceiverId())
                //.status(warehouseOrder.getStatus()).ackStatus(ackStatus)
                .warehouse(warehouses).fromDate(getTodayWithCustomTime(0, 0, 0))
                .toDate(getTodayWithCustomTime(23, 59, 59)).corrAttribute("Direction")
                .corrValue(warehouseOrder.getDirection()).corrAttribute1("Instance Id")
                .corrValue1(warehouseOrder.getFileId()).corrAttribute2("Depositor Order Number")
                .corrValue2(warehouseOrder.getSecondaryKeyValue()).build();
    }

    private SearchCriteria buildSecondarySearchCriteria(final String database, final WarehouseOrder warehouseOrder) {
        List<String> warehouses = Arrays.asList("MockA", "MockB");
        List<String> ackStatus = Arrays.asList("ACCEPTED", "PENDING");
        return SearchCriteria.builder().database(database).transactionType(warehouseOrder.getTransactionType())
                .senderId(warehouseOrder.getSenderId()).receiverId(warehouseOrder.getReceiverId())
                //.status(warehouseOrder.getStatus()).ackStatus(ackStatus)
                .warehouse(warehouses).fromDate(getTodayWithCustomTime(0, 0, 0))
                .toDate(getTodayWithCustomTime(23, 59, 59)).corrAttribute("Instance Id")
                .corrValue(warehouseOrder.getFileId()).corrAttribute1("Depositor Order Number")
                .corrValue1(warehouseOrder.getSecondaryKeyValue()).corrAttribute2("Direction")
                .corrValue2(warehouseOrder.getDirection()).build();
    }

    private Partner buildPartner(final String id, final String name) {
        return Partner.builder().partnerId(id).partnerIdentifier(id).partnerName(name).build();
    }

    private List<Map<String, Object>> getPartnersResultset(final List<Partner> partners) {
        final List<Map<String, Object>> partnersFromDB = new ArrayList<Map<String, Object>>();
        for (final Partner partner : partners) {
            final Map<String, Object> partnerMap = new HashMap<String, Object>();
            partnerMap.put("id", partner.getPartnerId());
            partnerMap.put("name", partner.getPartnerName());
            partnersFromDB.add(partnerMap);
        }
        return partnersFromDB;
    }

    private List<Map<String, Object>> getWarehouseOrderResultSet(final WarehouseOrder warehouseOrder) {
        final String filePath = "src/test/resources/TestFile.txt";
        final Map<String, Object> document = new HashMap<String, Object>();
        document.put("file_id", warehouseOrder.getFileId());
        document.put("transaction_type", warehouseOrder.getTransactionType());
        document.put("status", warehouseOrder.getStatus());
        document.put("ack_status", warehouseOrder.getAckStatus());
        document.put("sender_id", warehouseOrder.getSenderId());
        document.put("receiver_id", warehouseOrder.getReceiverId());
        document.put("direction", warehouseOrder.getDirection());
        document.put("warehouse", warehouseOrder.getWarehouse());
        document.put("sec_key_val", warehouseOrder.getSecondaryKeyValue());
        document.put("pre_trans_filepath", filePath);
        document.put("post_trans_filepath", filePath);
        document.put("ack_file_id", filePath);
        document.put("error_report_filepath", filePath);
        document.put("id", warehouseOrder.getId());
        return Arrays.asList(document);
    }

}
