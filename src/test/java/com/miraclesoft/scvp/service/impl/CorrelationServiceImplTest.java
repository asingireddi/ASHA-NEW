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

import org.assertj.core.api.JUnitSoftAssertions;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentMatchers;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import com.miraclesoft.scvp.model.Correlation;

/**
 * The Class CorrelationServiceImplTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
@SuppressWarnings("unchecked")
public class CorrelationServiceImplTest {

    @Autowired
    private CorrelationServiceImpl correlationServiceImpl;

    @MockBean
    private JdbcTemplate jdbcTemplate;

    @Rule
    public final JUnitSoftAssertions softly = new JUnitSoftAssertions();

    @Test
    public void isCorrelationExistedTest() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Correlation");

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(1);

        // Then
        assertEquals("Correlation already existed!", correlationServiceImpl.save(correlation));
    }

    @Test
    public void saveCorrelationTest() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Correlation");

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(0);
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(1);

        // Then
        assertEquals("Correlation added succesfully.", correlationServiceImpl.save(correlation));
    }

    @Test
    public void unSaveCorrelationTest() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Correlation");

        // When
        when(jdbcTemplate.queryForObject(anyString(), any(), (Class<Object>) any())).thenReturn(0);
        when(jdbcTemplate.update(anyString(), (Object) any())).thenReturn(0);

        // Then
        assertEquals("Please try again!", correlationServiceImpl.save(correlation));
    }

    @Test
    public void findAllCorrelationsTest() throws Exception {
        // Given
        final Correlation correlationOne = buildCorrelation("850", "Correlation");
        final Correlation correlationTwo = buildCorrelation("850", "Correlation");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(
                getCorrelationResultset(Arrays.asList(correlationOne, correlationTwo)));

        // Then
        assertEquals(correlationServiceImpl.findAll(Correlation.builder()
                                                               .build())
                                           .size(),
                2);
    }

    @Test
    public void findAllCorrelationsTestWithData() throws Exception {
        // Given
        final Correlation actualCorrelation = buildCorrelation("850", "Correlation");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(
                getCorrelationResultset(Arrays.asList(actualCorrelation)));

        // Then
        final List<Correlation> expectedCorrelations = correlationServiceImpl.findAll(actualCorrelation);
        softly.assertThat(expectedCorrelations.size())
              .isEqualTo(1);
        for (final Correlation correlation : expectedCorrelations) {
            softly.assertThat(correlation.getTransaction())
                  .isEqualTo(actualCorrelation.getTransaction());
            softly.assertThat(correlation.getValue())
                  .isEqualTo(actualCorrelation.getValue());
        }
    }

    @Test
    public void findAllCorrelationsTestWhenTransactionIsNull() throws Exception {
        // Given
        final Correlation actualCorrelation = buildCorrelation(null, "Correlation");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(
                getCorrelationResultset(Arrays.asList(actualCorrelation)));

        // Then
        final List<Correlation> expectedCorrelations = correlationServiceImpl.findAll(actualCorrelation);
        softly.assertThat(expectedCorrelations.size())
              .isEqualTo(1);
        for (final Correlation correlation : expectedCorrelations) {
            softly.assertThat(correlation.getTransaction())
                  .isEqualTo("");
            softly.assertThat(correlation.getValue())
                  .isEqualTo(actualCorrelation.getValue());
        }
    }

    @Test
    public void findAllCorrelationsTestWhenValueIsNull() throws Exception {
        // Given
        final Correlation actualCorrelation = buildCorrelation("850", null);

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(
                getCorrelationResultset(Arrays.asList(actualCorrelation)));

        // Then
        final List<Correlation> expectedCorrelations = correlationServiceImpl.findAll(actualCorrelation);
        softly.assertThat(expectedCorrelations.size())
              .isEqualTo(1);
        for (final Correlation correlation : expectedCorrelations) {
            softly.assertThat(correlation.getTransaction())
                  .isEqualTo(actualCorrelation.getTransaction());
            softly.assertThat(correlation.getValue())
                  .isEqualTo("");
        }
    }

    @Test
    public void findAllCorrelationsTestWhenTransactionIsEmpty() throws Exception {
        // Given
        final Correlation actualCorrelation = buildCorrelation("850", "Correlation");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(
                getCorrelationResultset(Arrays.asList(actualCorrelation)));

        // Then
        final List<Correlation> expectedCorrelations = correlationServiceImpl.findAll(
                buildCorrelation("-1", "Correlation"));
        softly.assertThat(expectedCorrelations.size())
              .isEqualTo(1);
        for (final Correlation correlation : expectedCorrelations) {
            softly.assertThat(correlation.getTransaction())
                  .isEqualTo(actualCorrelation.getTransaction());
            softly.assertThat(correlation.getValue())
                  .isEqualTo(actualCorrelation.getValue());
        }
    }

    @Test
    public void findAllCorrelationsTestWhenValueIsEmpty() throws Exception {
        // Given
        final Correlation actualCorrelation = buildCorrelation("850", "Correlation");

        // When
        when(jdbcTemplate.queryForList(anyString())).thenReturn(
                getCorrelationResultset(Arrays.asList(actualCorrelation)));

        // Then
        final List<Correlation> expectedCorrelations = correlationServiceImpl.findAll(buildCorrelation("850", ""));
        softly.assertThat(expectedCorrelations.size())
              .isEqualTo(1);
        for (final Correlation correlation : expectedCorrelations) {
            softly.assertThat(correlation.getTransaction())
                  .isEqualTo(actualCorrelation.getTransaction());
            softly.assertThat(correlation.getValue())
                  .isEqualTo(actualCorrelation.getValue());
        }
    }

    @Test
    public void deleteCorrelationTest() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Correlation");

        // When
        when(jdbcTemplate.update(anyString(), (Object[]) ArgumentMatchers.<Object> any())).thenReturn(1);

        // Then
        assertEquals(correlationServiceImpl.delete(correlation), "Correlation deleted succesfully.");
    }

    @Test
    public void deleteCorrelationTestWhenCorrelationIsNull() throws Exception {
        // Given
        final Correlation correlation = buildCorrelation("850", "Correlation");

        // When
        when(jdbcTemplate.update(anyString(), (Object[]) ArgumentMatchers.<Object> any())).thenReturn(0);

        // Then
        assertEquals(correlationServiceImpl.delete(correlation), "Please try again!");
    }

    private Correlation buildCorrelation(final String transaction, final String value) {
        return Correlation.builder()
                          .transaction(transaction)
                          .value(value)
                          .build();
    }

    private List<Map<String, Object>> getCorrelationResultset(final List<Correlation> correlations) {
        final List<Map<String, Object>> correlationsFromDB = new ArrayList<Map<String, Object>>();
        for (final Correlation correlation : correlations) {
            final Map<String, Object> row = new HashMap<String, Object>();
            row.put("transaction", correlation.getTransaction());
            row.put("value", correlation.getValue());
            correlationsFromDB.add(row);
        }
        return correlationsFromDB;
    }
}
