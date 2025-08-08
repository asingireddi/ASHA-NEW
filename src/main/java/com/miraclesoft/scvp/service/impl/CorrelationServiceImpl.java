package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.SqlCondition.equalOperator;
import static java.util.Objects.nonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.Correlation;

/**
 * The Class CorrelationServiceImpl.
 */
@Component
public class CorrelationServiceImpl {

    /** The jdbc template. */
    @Autowired
    private JdbcTemplate jdbcTemplate;

    /**
     * Save.
     *
     * @param correlation the correlation
     * @return the string
     * @throws Exception the exception
     */
    public String save(final Correlation correlation) throws Exception {
        final String transaction = correlation.getTransaction();
        final String value = correlation.getValue();
        return !isCorrelationExists(transaction, value)
                ? (jdbcTemplate.update("INSERT INTO correlation (transaction, value) VALUES (?, ?)",
                        new Object[] { transaction, value })) > 0 ? "Correlation added succesfully."
                                : "Please try again!"
                : "Correlation already existed!";
    }

    /**
     * Find all.
     *
     * @param correlation the correlation
     * @return the list
     * @throws Exception the exception
     */
    public List<Correlation> findAll(final Correlation correlation) throws Exception {
        final List<Correlation> correlations = new ArrayList<Correlation>();
        final String transaction = correlation.getTransaction();
        final String value = correlation.getValue();
        final StringBuilder correlationSearchQuery = new StringBuilder();
        correlationSearchQuery.append("SELECT transaction, value FROM correlation WHERE 1 = 1");
        List<Object> params = new ArrayList<>();
        if (nonNull(transaction) && !"-1".equals(transaction)) {
            correlationSearchQuery.append(equalOperator("transaction"));
            params.add(transaction);
        }
        if (nonNull(value) && !"".equals(value.trim())) {
            correlationSearchQuery.append(equalOperator("value"));
            params.add(value);
        }
        correlationSearchQuery.append(" ORDER BY transaction,value ASC");
        final List<Map<String, Object>> rows = jdbcTemplate.queryForList(correlationSearchQuery.toString());
        for (final Map<String, Object> row : rows) {
            final Correlation doc = new Correlation();
            doc.setTransaction(nonNull(row.get("transaction")) ? (String) row.get("transaction") : "");
            doc.setValue(nonNull(row.get("value")) ? (String) row.get("value") : "");
            correlations.add(doc);
        }
        return correlations;
    }

    /**
     * Delete.
     *
     * @param correlation the correlation
     * @return the string
     * @throws Exception the exception
     */
    public String delete(final Correlation correlation) throws Exception {
        return jdbcTemplate.update("DELETE FROM correlation WHERE transaction = ? AND value = ?",
                new Object[] { correlation.getTransaction(), correlation.getValue() }) > 0
                        ? "Correlation deleted succesfully."
                        : "Please try again!";
    }

    /**
     * Checks if is correlation exists.
     *
     * @param transaction the transaction
     * @param value the value
     * @return true, if is correlation exists
     * @throws Exception the exception
     */
    private boolean isCorrelationExists(final String transaction, final String value) throws Exception {
        return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM correlation WHERE transaction = ? AND value = ?",
                new Object[] { transaction, value }, Integer.class) > 0 ? true : false;
    }

}
