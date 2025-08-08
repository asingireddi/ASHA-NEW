package com.miraclesoft.scvp.util;

import static java.util.Objects.nonNull;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * The SqlCondition class is to search the data and appending wild cards.
 *
 * @author Narendar Geesidi
 */
public class SqlCondition {

	/**
	 * Equal operator.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the string
	 */
	public static String equalOperator(final String field) {
		return nonNull(field) ? " AND " + field + " = ?" : " ";
	}

	public static String equalOperatorWithOrAnd(final String field) {
		return nonNull(field) ? " AND " + field + " IN  (?)" : " ";
	}
	public String generateInClause(String fieldName, List<String> values) {
	    return values.stream()
	        .map(v -> "?")
	        .collect(Collectors.joining(", ", " AND " + fieldName + " IN (", ")"));
	}

	public static String equalOperatorWithOr(final String field) {
		return nonNull(field) ? " OR " + field + " IN  (?)" : " ";
	}

	public static String inOperatorWithAnd(final String field) {
		return nonNull(field) ? field + " IN (?)" + " AND " : " ";
	}
	public static String equalOperatorWithOrAnd(final String field, int size) {
	    if (!nonNull(field) || size <= 0) return "";
	    String placeholders = String.join(",", Collections.nCopies(size, "?"));
	    return " AND " + field + " IN (" + placeholders + ")";
	}
	/**
	 * Equal operator.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the string
	 */
	public static String equalOrOperator(final String field) {
		return nonNull(field) ? " Or " + field + " = ?" : " ";
	}

	/**
	 * Like operator.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the string
	 */
	public static String likeOperator(final String field) {
		return nonNull(field) ? " AND " + field + " LIKE % ? %" : " ";
	}

	/**
	 * In operator.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the string
	 */
	public static String inOperator(final String field) {
		return nonNull(field) ? " AND " + field + " IN ( ? )" : " ";
	}

	/**
	 * Equal operator with lowercase.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the string
	 */
	public static String equalOperatorWithLowercase(final String field) {
		return nonNull(field) ? " AND lower(" + field + ") = lower ( ? )" : " ";
	}

	/**
	 * Like operator starts with.
	 *
	 * @param field the field
	 * @param value the value
	 * @return the string
	 */
	public static String likeOperatorStartWith(final String field) {
		return nonNull(field) ? " AND " + field + " LIKE ? " : " ";
	}

	/**
	 * Comma separated string.
	 * 
	 * @param list the list
	 * @return the string
	 */
	public static String listToString(List<String> list) {
		if (list.isEmpty()) {
			return "null";
		}
		return list.stream().map(s -> "'" + s + "'").collect(Collectors.joining(","));
	}

	public static boolean isValidList(List<String> list) {
	    return list != null && !list.isEmpty() && !(list.size() == 1 && "All".equalsIgnoreCase(list.get(0)));
	}
}
