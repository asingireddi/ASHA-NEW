package com.miraclesoft.scvp.controller;

import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.service.CronJobService;

/**
 * The Class CronJobControllerTest.java
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CronJobControllerTest {

    @Mock
    private CronJobService cronJobService;

    @InjectMocks
    private CronJobController cronJobController = new CronJobController();

    @Test
    public void shouldGetDailyScheduler() throws Exception {
        // Given
        cronJobController = spy(cronJobController);

        // When
        doNothing().when(cronJobController)
                   .dailyScheduler();
        cronJobController.dailyScheduler();

        // Then
        verify(cronJobController, times(1)).dailyScheduler();
    }

    @Test
    public void shouldGetWeeklyScheduler() throws Exception {
        // Given
        cronJobController = spy(cronJobController);

        // When
        doNothing().when(cronJobController)
                   .weeklyScheduler();
        cronJobController.weeklyScheduler();

        // Then
        verify(cronJobController, times(1)).weeklyScheduler();
    }

    @Test
    public void shouldGetMonthlyScheduler() throws Exception {
        // Given
        cronJobController = spy(cronJobController);

        // When
        doNothing().when(cronJobController)
                   .monthlyScheduler();
        cronJobController.monthlyScheduler();

        // Then
        verify(cronJobController, times(1)).monthlyScheduler();
    }

}

