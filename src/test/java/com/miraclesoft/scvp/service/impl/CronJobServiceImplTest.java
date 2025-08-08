package com.miraclesoft.scvp.service.impl;
import static org.junit.Assert.assertEquals;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.junit.Before;
import org.junit.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
public class CronJobServiceImplTest {

    @Mock
    private JdbcTemplate jdbcTemplate;

    @InjectMocks
    CronJobServiceImpl cronJobServiceImpl;

    @Before
    public void initMocks() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void getToEmailsForReport() throws Exception {
        final Set<String> toEmails = new HashSet<String>();
        final List rows = new ArrayList<>();
        final Map<String, Object> map = new HashMap<>();
        map.put("reciver_ids", 1);
        rows.add(map);
        Mockito.when(jdbcTemplate.queryForList(Mockito.anyString())).thenReturn(rows);
        assertEquals(cronJobServiceImpl.getToEmailsForReport(Mockito.anyString()), toEmails);

    }
    
}
