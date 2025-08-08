package com.miraclesoft.scvp.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.Scheduler;
import com.miraclesoft.scvp.service.SchedulerService;

/**
 * The Class SchedulerControllerTest.java
 * 
 * @author Priyanka Kolla
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SchedulerControllerTest {

    @Mock
    private SchedulerService schedulerService;

    @InjectMocks
    private SchedulerController schedulerController = new SchedulerController();

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
    }

    @Test
    public void shouldSearchSchedulers() {
        // Given
        final Scheduler scheduler = buildScheduler();
        final List<Scheduler> schedulers = Arrays.asList(scheduler);

        // When
        when(schedulerService.findAll(Mockito.any())).thenReturn(schedulers);

        // Then
        assertEquals(schedulers, schedulerController.schedulers(scheduler));
    }

    @Test
    public void shouldAddScheduler() throws Exception {
        // Given
        final Scheduler scheduler = buildScheduler();
        final String response = "Scheduler added succesfully.";

        // When
        when(schedulerService.save(scheduler)).thenReturn(response);

        // Then
        assertEquals(schedulerController.addScheduler(scheduler), response);
    }

    @Test
    public void shouldFindScheduler() {
        // Given
        final Scheduler scheduler = buildScheduler();

        // When
        when(schedulerService.findOne(scheduler.getId())).thenReturn(scheduler);

        // Then
        assertEquals(schedulerController.getScheduler(scheduler.getId()), scheduler);
    }

    @Test
    public void shouldUpdateScheduler() throws Exception {
        // Given
        final Scheduler scheduler = buildScheduler();
        final String response = "Scheduler updated succesfully.";

        // When
        when(schedulerService.update(scheduler)).thenReturn(response);

        // Then
        assertEquals(schedulerController.updateScheduler(scheduler), response);
    }

    @Test
    public void shouldDeleteScheduler() throws Exception {
        // Given
        final Scheduler scheduler = buildScheduler();
        final String response = "Scheduler deleted succesfully.";

        // When
        when(schedulerService.delete(scheduler.getId())).thenReturn(response);

        // Then
        assertEquals(schedulerController.delete(scheduler.getId()), response);
    }

    private Scheduler buildScheduler() {
        return Scheduler.builder()
                        .status("Active")
                        .build();
    }
}
