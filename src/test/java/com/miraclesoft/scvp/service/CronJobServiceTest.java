package com.miraclesoft.scvp.service;

import static org.junit.Assert.assertEquals;

import java.util.HashSet;
import java.util.Set;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import static org.mockito.Mockito.when;

import com.miraclesoft.scvp.service.impl.CronJobServiceImpl;

@RunWith(SpringJUnit4ClassRunner.class)
public class CronJobServiceTest {

    @InjectMocks
    private CronJobService cronJobService;

    @Mock
    private CronJobServiceImpl cronJobServiceImpl;

    @Test
    public void getToEmailsForReportTest() {

//Given
        final Set<String> set = new HashSet<String>();
        set.add("type");

//When
        when(cronJobServiceImpl.getToEmailsForReport("type")).thenReturn(set);
//Then
        assertEquals(set, cronJobService.getToEmailsForReport("type"));
    }

    @Test
    public void getCcEmailsForReportTest() {

//Given
        final Set<String> set = new HashSet<String>();
        set.add("type");

//When
        when(cronJobServiceImpl.getCcEmailsForReport("type")).thenReturn(set);

//Then
        assertEquals(set, cronJobService.getCcEmailsForReport("type"));

    }

    @Test
    public void getFileTest() throws Exception {

//Given
        final String type = "type";

//When
        when(cronJobServiceImpl.getFile(type)).thenReturn("type");

//Then
        assertEquals("type", cronJobService.getFile(type));
    }

    @Test
    public void archivePurgeScheduler() throws Exception {

//Given
        final String response = "Files archived and purged successfully.";

//When
        when(cronJobServiceImpl.archivePurgeScheduler()).thenReturn(response);

//Then
        assertEquals(response, cronJobService.archivePurgeScheduler());
    }

}
