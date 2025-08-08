package com.miraclesoft.scvp.util;

import static com.miraclesoft.scvp.util.DateUtility.getTodayWithCustomTime;
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

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.LifeCycle;
import com.miraclesoft.scvp.model.LifeCyclePayload;
import com.miraclesoft.scvp.model.Partner;

/**
 * The Class LifeCycleUtilityTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class LifeCycleUtilityTest {

    @Autowired
    private LifeCycleUtility lifeCycleUtility;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Before
    public void setUp() {
        final Partner partnerOne = buildPartner("pId1", "pName1");
        final Partner partnerTwo = buildPartner("pId2", "pName2");
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("tp"))).thenReturn(
                getPartnersResultset(Arrays.asList(partnerOne, partnerTwo)));
    }

    @Test
    public void warehouseOrderLifecyclesInboundTest() {
        //Given
    	final LifeCyclePayload lifeCyclePayload = buildLifeCyclePayload("LIVE","1258");
        final Map<String, Object> lifeCycle = (Map<String, Object>) buildLifeCycle("Mock Id", "INBOUND", "pId1", "pId2", "940", "SUCCESS", "ACCEPTED",
                "12345");

        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files"))).thenReturn(getLifeCycles((LifeCycle) lifeCycle));

        // Then
        final CustomResponse response = lifeCycleUtility.lifeCycle(lifeCyclePayload);
        assertEquals(1, response.getData());
//        assertEquals("pName1", ((LifeCycle) response.get(0))
//                                       .getPartner());
    }
    
    
    private LifeCyclePayload buildLifeCyclePayload(String database, String depositorOrderNumber) {
        return LifeCyclePayload.builder()
                        .database(database)
                        .depositorOrderNumber(depositorOrderNumber)
                        .build();
    }

    @Test
    public void warehouseOrderLifecyclesOutboundTest() {
        //Given
    	final LifeCyclePayload lifeCyclePayload = buildLifeCyclePayload("LIVE","1258");
        final Map<String, Object> lifeCycle = (Map<String, Object>) buildLifeCycle("Mock Id", "OUTBOUND", "pId1", "pId2", "940", "SUCCESS", "ACCEPTED",
                "12345");

        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files"))).thenReturn(getLifeCycles((LifeCycle) lifeCycle));

        // Then
        final CustomResponse response = lifeCycleUtility.lifeCycle(lifeCyclePayload);
        assertEquals(1, response.getData());
//        assertEquals("pName2", ((LifeCycle) response.get(0))
//                                       .getPartner());
    }

    @Test
    public void warehouseOrderLifecyclesNullValuesTest() {
        //Given
    	final LifeCyclePayload lifeCyclePayload = buildLifeCyclePayload("LIVE","1258");
        final Map<String, Object> lifeCycle = (Map<String, Object>) LifeCycle.builder()
                                             .depositorOrderNumber("12345")
                                             .build();

        // When
        when(jdbcTemplate.queryForList(ArgumentMatchers.contains("files"))).thenReturn(getLifeCycles((LifeCycle) lifeCycle));

        // Then
        final CustomResponse response = lifeCycleUtility.lifeCycle(lifeCyclePayload);
        assertEquals(1, response.getData());
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

    private LifeCycle buildLifeCycle(final String fileId, final String direction, final String senderId,
            final String receiverId, final String transaction, final String status, final String ackStatus,
            final String secondaryKeyValue) {
        return LifeCycle.builder()
                        .fileId(fileId)
                        .fileType("EDI")
                        .transactionType(transaction)
                        .direction(direction)
                        .senderId(senderId)
                        .receiverId(receiverId)
                        .status(status)
                        .ackStatus(ackStatus)
                        .poNumber(secondaryKeyValue)
                        .depositorOrderNumber(secondaryKeyValue)
                        .dateTimeReceived(getTodayWithCustomTime(0, 0, 0))
                        .build();
    }

    private List<Map<String, Object>> getLifeCycles(final LifeCycle lifeCycle) {
        final Map<String, Object> document = new HashMap<String, Object>();
        document.put("file_id", lifeCycle.getFileId());
        document.put("file_type", lifeCycle.getFileType());
        document.put("transaction_type", lifeCycle.getTransactionType());
        document.put("status", lifeCycle.getStatus());
        document.put("ack_status", lifeCycle.getAckStatus());
        document.put("sender_id", lifeCycle.getSenderId());
        document.put("receiver_id", lifeCycle.getReceiverId());
        document.put("direction", lifeCycle.getDirection());
        document.put("po_number", lifeCycle.getPoNumber());
        document.put("sec_key_val", lifeCycle.getDepositorOrderNumber());
        document.put("reprocessstatus", lifeCycle.getStatus());
        return Arrays.asList(document);
    }

}
