package com.miraclesoft.scvp.service.impl;

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
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.Scheduler;

/**
 * The Class DashboardServiceImpl.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class SchedulerServiceImplTest {

    @Autowired
    private SchedulerServiceImpl schedulerServiceImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Test
    public void findAllTest() {
        final List<Map<String, Object>> output = new ArrayList<Map<String, Object>>();
        final Map<String, Object> userMap = new HashMap<String, Object>();
        userMap.put("sch_id", 1L);
        userMap.put("sch_title", "mock_title");
        userMap.put("sch_type", "mock_type");
        userMap.put("sch_status", "mock_status");
        userMap.put("created_by", "test");
        output.add(userMap);
        when(jdbcTemplate.queryForList(anyString())).thenReturn(output);

        final List<Scheduler> response = schedulerServiceImpl.findAll(Scheduler.builder()
                                                                               .status("mock_status")
                                                                               .createdBy("test")
                                                                               .build());
        assertEquals(1, response.size());
    }

    @Test
    public void saveTest() throws Exception {
        final Scheduler scheduler = new Scheduler();
        scheduler.setType("Mock");
        scheduler.setCreatedBy("Test");

        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(0);
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(1);

        final String response = schedulerServiceImpl.save(scheduler);
        assertEquals(response, "Scheduler added succesfully.");
    }

    @Test
    public void unSaveIfDuplication() throws Exception {
        final Scheduler scheduler = new Scheduler();
        scheduler.setType("Mock");
        scheduler.setCreatedBy("Test");

        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(1);
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(1);

        final String response = schedulerServiceImpl.save(scheduler);
        assertEquals(response, "Scheduler with same report type is already exists.");
    }

    @Test
    public void findOneTest() {
        final Scheduler scheduler = new Scheduler();
        scheduler.setStatus("Mock");
        final Map<String, Object> schedulerMap = new HashMap<String, Object>();
        schedulerMap.put("sch_id", 1L);
        schedulerMap.put("sch_title", "mock_title");
        schedulerMap.put("sch_type", "mock_type");
        schedulerMap.put("sch_status", "mock_status");
        schedulerMap.put("reciver_ids", "receiver1");
        schedulerMap.put("extranal_emailids", "mock@miraclesoft.com");
        final List<Map<String, Object>> rows = Arrays.asList(schedulerMap);

        when(jdbcTemplate.queryForList(anyString())).thenReturn(rows);

        final Scheduler response = schedulerServiceImpl.findOne(1L);
        assertEquals(response.getType(), "mock_type");
    }

    @Test
    public void updateTest() throws Exception {
        final Scheduler scheduler = new Scheduler();
        scheduler.setStatus("Mock");

        when(jdbcTemplate.update(anyString(), (Object[]) ArgumentMatchers.<Object> any())).thenReturn(1);

        final String response = schedulerServiceImpl.update(scheduler);
        assertEquals(response, "Scheduler updated succesfully.");
    }

    @Test
    public void ifNotUpdatedTest() throws Exception {
        final Scheduler scheduler = new Scheduler();
        scheduler.setStatus("Mock");

        when(jdbcTemplate.update(anyString(), (Object[]) ArgumentMatchers.<Object> any())).thenReturn(0);

        final String response = schedulerServiceImpl.update(scheduler);
        assertEquals(response, "Please try again!");
    }

    @Test
    public void deleteTest() throws Exception {
        final Scheduler scheduler = new Scheduler();
        scheduler.setStatus("mock");

        when(jdbcTemplate.update(anyString(), (Object[]) ArgumentMatchers.<Object> any())).thenReturn(1);

        final String response = schedulerServiceImpl.delete(1L);
        assertEquals(response, "Scheduler deleted succesfully.");
    }
}
