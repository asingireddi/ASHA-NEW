package com.miraclesoft.scvp.service.impl;

import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

/**
 * The Class TimeZoneServiceImpl.
 * 
 * @author rpidugu
 *
 */
@Component
public class TimeZoneServiceImpl {
	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(TimeZoneServiceImpl.class.getName());

	/**
	 * Time zones list
	 * 
	 * @return the string
	 */
	public String timeZone() {
		final JSONArray timeZoneList = new JSONArray();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate
					.queryForList("SELECT id, label, code FROM timezones WHERE 1=1");
			for (final Map<String, Object> row : rows) {
				final JSONObject timezone = new JSONObject();
				timezone.put("id", row.get("id"));
				timezone.put("label", row.get("label"));
				timezone.put("code", row.get("code"));
				timeZoneList.put(timezone);
			}
		} catch (final Exception exception) {
			logger.log(Level.ERROR, " timeZoneList :: " + exception.getMessage());
		}
		return timeZoneList.toString();

	}

}
