package com.miraclesoft.scvp.service.impl;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.LifeCycle;
import com.miraclesoft.scvp.model.Partner;

/**
 * The Class DashboardServiceImpl.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class LifeCycleServiceImplTest {

    @Autowired
    private LifeCycleServiceImpl lifeCycleServiceImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        final Partner partnerOne = buildPartner("Mock Sender Id", "Mock Sender");
        final Partner partnerTwo = buildPartner("Mock Receiver Id", "Mock Receiver");
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("tp"))).thenReturn(
                getPartnersResultset(Arrays.asList(partnerOne, partnerTwo)));
    }

    @Test
    public void warehouseOrderLifeCycleDetailInfoIBTest() {
        // Given
        final List<Map<String, Object>> output = getDocuoments("FileId", "INBOUND", "940", "SUCCESS", "ACCEPTED",
                "Mock Sender Id", "Mock Receiver Id", "DepositorOrderNumber");

        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files"))).thenReturn(output);

        // Then
        final LifeCycle response = lifeCycleServiceImpl.lifeCycleDetailInfo("DepositorOrderNumber",
                "FileId", "Mock db", "940");
        assertEquals("Mock Sender", response.getSenderName());
    }

    @Test
    public void warehouseOrderLifeCycleDetailInfoOBTest() {
        // Given
        final List<Map<String, Object>> output = getDocuoments("FileId", "OUTBOUND", "940", "SUCCESS", "ACCEPTED",
                "Mock Sender Id", "Mock Receiver Id", "DepositorOrderNumber");

        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files"))).thenReturn(output);

        // Then
        final LifeCycle response = lifeCycleServiceImpl.lifeCycleDetailInfo("DepositorOrderNumber",
                "FileId", "Mock db", "940");
        assertEquals("Mock Receiver", response.getReceiverName());
    }

    @Test
    public void warehouseOrderLifeCycleDetailInfoWithoutFilesTest() {
        // Given
        final List<Map<String, Object>> output = getDocuomentsWithoutFiles("FileId", "INBOUND", "940", "SUCCESS",
                "ACCEPTED", "Mock Sender Id", "Mock Receiver Id", "DepositorOrderNumber");

        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files"))).thenReturn(output);

        // Then
        final LifeCycle response = lifeCycleServiceImpl.lifeCycleDetailInfo("DepositorOrderNumber",
                "FileId", "Mock db", "940");
        assertEquals("Mock Sender", response.getSenderName());
    }

    private Partner buildPartner(final String id, final String name) {
        return Partner.builder()
                      .partnerId(id)
                      .partnerIdentifier(id)
                      .partnerName(name)
                      .build();
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

    private List<Map<String, Object>> getDocuoments(final String fileId, final String direction,
            final String transaction, final String status, final String ackStatus, final String senderId,
            final String receiverId, final String secondaryKeyValue) {
        final String filePath = "src/test/resources/TestFile.txt";
        final Map<String, Object> document = new HashMap<String, Object>();
        document.put("file_id", fileId);
        document.put("transaction_type", transaction);
        document.put("status", status);
        document.put("ack_status", ackStatus);
        document.put("sender_id", senderId);
        document.put("receiver_id", receiverId);
        document.put("direction", direction);
        document.put("sec_key_val", secondaryKeyValue);
        document.put("pre_trans_filepath", filePath);
        document.put("post_trans_filepath", filePath);
        document.put("ack_file_id", filePath);
        document.put("error_report_filepath", filePath);
        document.put("org_filepath", filePath);
        return Arrays.asList(document);
    }

    private List<Map<String, Object>> getDocuomentsWithoutFiles(final String fileId, final String direction,
            final String transaction, final String status, final String ackStatus, final String senderId,
            final String receiverId, final String secondaryKeyValue) {
        final Map<String, Object> document = new HashMap<String, Object>();
        document.put("file_id", fileId);
        document.put("transaction_type", transaction);
        document.put("status", status);
        document.put("ack_status", ackStatus);
        document.put("sender_id", senderId);
        document.put("receiver_id", receiverId);
        document.put("direction", direction);
        document.put("sec_key_val", secondaryKeyValue);
        return Arrays.asList(document);
    }

}
