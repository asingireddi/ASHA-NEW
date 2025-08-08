package com.miraclesoft.scvp.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.doReturn;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.Scheduler;
import com.miraclesoft.scvp.service.impl.SchedulerServiceImpl;

/**
 * The Test SchedulerServiceTest
 * 
 * @author Narendar Geesidi
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class SchedulerServiceTest {

    @InjectMocks
    private SchedulerService schedulerService;

    @Mock
    private SchedulerServiceImpl schedulerServiceImpl;

    @Test
    public void shouldSearchSchedulers() {
        // Given
        final Scheduler scheduler = buildScheduler();
        final List<Scheduler> expectedSchedulers = Arrays.asList(scheduler);

        // When
        doReturn(expectedSchedulers).when(schedulerServiceImpl)
                                    .findAll(scheduler);

        // Then
        assertThat(schedulerService.findAll(scheduler)).isEqualTo(expectedSchedulers);
    }

    @Test
    public void shouldAddScheduler() throws Exception {
        // Given
        final Scheduler scheduler = buildScheduler();

        // When
        doReturn("Scheduler added successfully.").when(schedulerServiceImpl)
                                                 .save(scheduler);

        // Then
        assertThat(schedulerService.save(scheduler)).isEqualTo("Scheduler added successfully.");
    }

    @Test
    public void shouldFindScheduler() {
        // Given
        final Scheduler scheduler = buildScheduler();

        // When
        doReturn(scheduler).when(schedulerServiceImpl)
                           .findOne(scheduler.getId());

        // Then
        assertThat(schedulerService.findOne(scheduler.getId())).isEqualTo(scheduler);
    }

    @Test
    public void shouldUpdateScheduler() throws Exception {
        // Given
        final Scheduler scheduler = buildScheduler();

        // When
        doReturn("Scheduler updated successfully.").when(schedulerServiceImpl)
                                                   .update(scheduler);

        // Then
        assertThat(schedulerService.update(scheduler)).isEqualTo("Scheduler updated successfully.");
    }

    @Test
    public void shouldDeleteScheduler() throws Exception {
        // Given
        final Scheduler scheduler = buildScheduler();

        // When
        doReturn("Scheduler deleted successfully.").when(schedulerServiceImpl)
                                                   .delete(scheduler.getId());

        // Then
        assertThat(schedulerService.delete(scheduler.getId())).isEqualTo("Scheduler deleted successfully.");
    }

    private Scheduler buildScheduler() {
        return Scheduler.builder()
                        .id(1L)
                        .status("Active")
                        .title("My Weekly Reports")
                        .type("Weekly")
                        .receiverEmails("pkolla@miraclesoft.com,ngeesidi@miraclesoft.com")
                        .externalEmails("pkolla@miraclesoft.com,sdarapureddi@miraclesoft.com")
                        .build();
    }
}
