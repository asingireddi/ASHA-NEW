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

import com.miraclesoft.scvp.model.Correlation;
import com.miraclesoft.scvp.service.impl.CorrelationServiceImpl;

/**
 * The Test CorrelationServiceTest
 * 
 * @author Priyanka Kolla
 */
@RunWith(SpringJUnit4ClassRunner.class)
public class CorrelationServiceTest {

    @InjectMocks
    private CorrelationService correlationService;

    @Mock
    private CorrelationServiceImpl correlationServiceImpl;

    @Test
    public void shouldAddCorrelation() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Direction");

        // When
        doReturn("Correlation added successfully.").when(correlationServiceImpl)
                                                   .save(correlation);

        // Then
        assertThat(correlationService.save(correlation)).isEqualTo("Correlation added successfully.");
    }

    @Test
    public void findCorrelations() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Direction");
        final List<Correlation> expectedCorrelations = Arrays.asList(correlation);

        // When
        doReturn(expectedCorrelations).when(correlationServiceImpl)
                                      .findAll(correlation);

        // Then
        assertThat(correlationService.findAll(correlation)).isEqualTo(expectedCorrelations);
    }

    @Test
    public void shouldDeleteCorrelation() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Direction");

        // When
        doReturn("Correlation deleted successfully.").when(correlationServiceImpl)
                                                     .delete(correlation);

        // Then
        assertThat(correlationService.delete(correlation)).isEqualTo("Correlation deleted successfully.");
    }

    private Correlation buildCorrelation(final String transaction, final String value) {
        return Correlation.builder()
                          .transaction(transaction)
                          .value(value)
                          .build();
    }

}
