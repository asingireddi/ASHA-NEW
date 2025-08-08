package com.miraclesoft.scvp.util;

import static java.util.Objects.nonNull;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * The Class SqlConditionTest.
 *
 * @author Narendar Geesidi
 */
@SpringBootTest
@RunWith(SpringJUnit4ClassRunner.class)
public class SqlConditionTest {

    @Test
    public void equalOperatorTest() {
        // Given
        final String field = "name";
        final String value = "MSCVP";

        // When
        final String output = SqlCondition.equalOperator(field);

        // Then
        assertThat(output).isEqualTo(formEqualOperator(field, value));
    }

    @Test
    public void equalOperatorWithNullTest() {
        // Given
        final String field = null;
        final String value = null;

        // When
        final String output = SqlCondition.equalOperator(field);

        // Then
        assertThat(output).isEqualTo(formEqualOperator(field, value));
    }

    @Test
    public void likeOperatorTest() {
        // Given
        final String field = "name";
        final String value = "MSCVP";

        // When
        final String output = SqlCondition.likeOperator(field);

        // Then
        assertThat(output).isEqualTo(formLikeOperator(field, value));
    }

    @Test
    public void likeOperatorWithNullTest() {
        // Given
        final String field = null;
        final String value = null;

        // When
        final String output = SqlCondition.likeOperator(field);

        // Then
        assertThat(output).isEqualTo(formLikeOperator(field, value));
    }

    @Test
    public void inOperatorTest() {
        // Given
        final String field = "name";
        final String value = "MSCVP";

        // When
        final String output = SqlCondition.inOperator(field);

        // Then
        assertThat(output).isEqualTo(formInOperator(field, value));
    }

    @Test
    public void inOperatorWithNullTest() {
        // Given
        final String field = null;
        final String value = null;

        // When
        final String output = SqlCondition.inOperator(field);

        // Then
        assertThat(output).isEqualTo(formInOperator(field, value));
    }

    @Test
    public void equalOperatorWithLowercaseTestWithNullValues() {
        // Given
        final String field = "name";
        final String value = "MSCVP";

        // When
        final String output = SqlCondition.equalOperatorWithLowercase(field);

        // Then
        assertThat(output).isEqualTo(formEqualOperatorWithLowercase(field, value));
    }

    @Test
    public void equalOperatorWithLowercaseTest() {
        // Given
        final String field = null;
        final String value = null;

        // When
        final String output = SqlCondition.equalOperatorWithLowercase(field);

        // Then
        assertThat(output).isEqualTo(formEqualOperatorWithLowercase(field, value));
    }

    private String formEqualOperator(final String field, final String value) {
        return nonNull(field) && nonNull(value) ? " AND " + field + " = '" + value + "'" : " ";
    }

    private String formLikeOperator(final String field, final String value) {
        return nonNull(field) && nonNull(value) ? " AND " + field + " LIKE '%" + value + "%'" : " ";
    }

    private String formInOperator(final String field, final String value) {
        return nonNull(field) && nonNull(value) ? " AND " + field + " IN (" + value + ")" : " ";
    }

    private String formEqualOperatorWithLowercase(final String field, final String value) {
        return nonNull(field) && nonNull(value) ? " AND lower(" + field + ") = lower('" + value + "')" : " ";
    }
}
