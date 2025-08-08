package com.miraclesoft.scvp.controller;

import static org.junit.Assert.assertEquals;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.Correlation;
import com.miraclesoft.scvp.service.CorrelationService;

/**
 * The Class CorrelationControllerTest.java
 * 
 * @author Priyanka Kolla
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CorrelationControllerTest {

    @Mock
    private CorrelationService correlationService;

    @InjectMocks
    private CorrelationController correlationController = new CorrelationController();

    @Test
    public void isCorrelationExistedTest() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Direction");

        // When
        when(correlationService.save(Mockito.any())).thenReturn("Correlation already existed!");

        // Then
        assertEquals(correlationController.save(correlation), "Correlation already existed!");
    }

    @Test
    public void shouldSaveCorrelation() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Direction");

        // When
        when(correlationService.save(Mockito.any())).thenReturn("Correlation added succesfully.");

        // Then
        assertEquals(correlationController.save(correlation), "Correlation added succesfully.");
    }

    @Test
    public void searchCorrelationTest() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Direction");
        final List<Correlation> correlations = Arrays.asList(correlation);

        // When
        when(correlationService.findAll(Mockito.any())).thenReturn(correlations);

        // Then
        assertEquals(correlationController.search(correlation), correlations);
    }

    @Test
    public void shouldDeleteCorrelation() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Direction");

        // When
        when(correlationService.delete(Mockito.any())).thenReturn("Correlation deleted succesfully");

        // Then
        assertEquals(correlationController.delete(correlation), "Correlation deleted succesfully");
    }

    private Correlation buildCorrelation(final String transaction, final String value) {
        return Correlation.builder()
                          .transaction(transaction)
                          .value(value)
                          .build();
    }
}
