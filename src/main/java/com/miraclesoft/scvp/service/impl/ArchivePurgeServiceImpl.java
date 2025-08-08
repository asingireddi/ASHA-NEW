package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.SqlCondition.equalOperator;
import static java.util.Objects.nonNull;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.ArchivePurge;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

/**
 * The class ArchivePurgeServiceImpl.
 *
 * @author Narendar Geesidi
 */

@Component
public class ArchivePurgeServiceImpl {

	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** The data source data provider. */
	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;
	
	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	// private static Logger logger =
	// LogManager.getLogger(ArchivePurgeServiceImpl.class.getName());
	/**
	 * Save.
	 *
	 * @param archivePurge the archive purge
	 * @return the string
	 * @throws Exception the exception
	 */
	public String save(final ArchivePurge archivePurge) throws Exception {
		final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
		final String transaction = archivePurge.getTransaction();
		return !isArchivePurgeExists(transaction) ? (jdbcTemplate.update(
				"INSERT INTO archive_purge (transaction, archive_days, purge_days, created_by, "
						+ "modified_by, modified_ts) VALUES (?, ?, ?, ?, ?, convert_tz(current_timestamp, @@session.time_zone, ?))",
				new Object[] { transaction, archivePurge.getArchiveDays(), archivePurge.getPurgeDays(),
						archivePurge.getCreatedBy(), archivePurge.getCreatedBy(), defaultTimeZone})) > 0
								? transaction + " archive/purge days added succesfully."
								: "Please try again!"
				: transaction + " config already existed!";
						// removed get getCurrentTimestamp() in new object for modified_ts
	}

	/**
	 * Update.
	 *
	 * @param archivePurge the archive purge
	 * @return the string
	 * @throws DataAccessException the data access exception
	 * @throws Exception           the exception
	 */
	public String update(final ArchivePurge archivePurge) throws DataAccessException, Exception {
		final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
		return jdbcTemplate.update(
				"UPDATE archive_purge SET archive_days = ?, purge_days = ?, "
						+ "modified_by = ?, modified_ts = convert_tz(current_timestamp,@@session.time_zone, ?) WHERE id = ? AND transaction = ?",
				new Object[] { archivePurge.getArchiveDays(), archivePurge.getPurgeDays(), archivePurge.getModifiedBy(), defaultTimeZone,
						 archivePurge.getId(), archivePurge.getTransaction() }) > 0
								? "Updated succesfully."
								: "Something went wrong. Please try again!";
	}
	//CONVERT_TZ(modified_ts,'"+defaultTimeZone+"','" + userTimeZone + "')

	/**
	 * Find one.
	 *
	 * @param id the id
	 * @return the archive purge
	 * @throws Exception the exception
	 */
	public ArchivePurge findOne(final int id) throws Exception {
		final ArchivePurge archivePurge = new ArchivePurge();
		final List<Map<String, Object>> rows = jdbcTemplate
				.queryForList("SELECT id, transaction, archive_days, purge_days, created_by, created_ts, modified_by, "
						+ "modified_ts FROM archive_purge WHERE id = ?", id);
		for (final Map<String, Object> row : rows) {
			archivePurge.setId(nonNull(row.get("id")) ? (Integer) row.get("id") : 0);
			archivePurge.setTransaction(nonNull(row.get("transaction")) ? (String) row.get("transaction") : "");
			archivePurge.setArchiveDays(nonNull(row.get("archive_days")) ? (Integer) row.get("archive_days") : 0);
			archivePurge.setPurgeDays(nonNull(row.get("purge_days")) ? (Integer) row.get("purge_days") : 0);
			archivePurge.setCreatedBy(nonNull(row.get("created_by")) ? (String) row.get("created_by") : "");
			archivePurge.setCreatedDate(nonNull(row.get("created_ts"))
					? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("created_ts"))
					: "");
			archivePurge.setModifiedBy(nonNull(row.get("modified_by")) ? (String) row.get("modified_by") : "");
			archivePurge.setModifiedDate(nonNull(row.get("modified_ts"))
					? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("modified_ts"))
					: "");
		}
		return archivePurge;
	}

	/**
	 * Find all.
	 *
	 * @param transaction the transaction
	 * @param archiveDays the archiveDays
	 * @param purgeDays   the purgeDays
	 * @return the list
	 * @throws Exception the exception
	 */
	public List<ArchivePurge> findAll(final String transaction, final int archiveDays, final int purgeDays)
			throws Exception {
		final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
		final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
		final List<ArchivePurge> archivePurges = new ArrayList<ArchivePurge>();
		final StringBuilder archivePurgeSearchQuery = new StringBuilder();
		archivePurgeSearchQuery.append("SELECT id, transaction, archive_days, purge_days, "
				+ "created_by, CONVERT_TZ(created_ts,@@session.time_zone, ?) as created_ts, modified_by, CONVERT_TZ(modified_ts,?,?) AS modified_ts FROM archive_purge WHERE 1=1 ");
		List<Object> params = new ArrayList<>();
		params.add(userTimeZone);
	    params.add(defaultTimeZone);
	    params.add(userTimeZone);
		if (nonNull(transaction) && !"-1".equals(transaction)) {
			archivePurgeSearchQuery.append(equalOperator("transaction"));
			params.add(transaction);
		}
		if (nonNull(archiveDays) && !"".equals(String.valueOf(archiveDays).trim())
				&& !"-1".equals(String.valueOf(archiveDays).trim())) {
			archivePurgeSearchQuery.append(equalOperator("archive_days"));
			params.add(archiveDays);
		}
		if (nonNull(purgeDays) && !"".equals(String.valueOf(purgeDays).trim())
				&& !"-1".equals(String.valueOf(purgeDays).trim())) {
			archivePurgeSearchQuery.append(equalOperator("purge_days"));
			params.add(purgeDays);
		}
		archivePurgeSearchQuery.append(" ORDER BY transaction ASC");
		final List<Map<String, Object>> rows = jdbcTemplate.queryForList(archivePurgeSearchQuery.toString(), params.toArray());
		for (final Map<String, Object> row : rows) {
			final ArchivePurge doc = new ArchivePurge();
			doc.setId(nonNull(row.get("id")) ? (Integer) row.get("id") : 0);
			doc.setTransaction(nonNull(row.get("transaction")) ? (String) row.get("transaction") : "");
			doc.setArchiveDays(nonNull(row.get("archive_days")) ? (Integer) row.get("archive_days") : 0);
			doc.setPurgeDays(nonNull(row.get("purge_days")) ? (Integer) row.get("purge_days") : 0);
			doc.setCreatedBy(nonNull(row.get("created_by")) ? (String) row.get("created_by") : "");
			doc.setCreatedDate(nonNull(row.get("created_ts"))
					? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("created_ts"))
					: "");
			doc.setModifiedBy(nonNull(row.get("modified_by")) ? (String) row.get("modified_by") : "");
			doc.setModifiedDate(nonNull(row.get("modified_ts"))
					? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("modified_ts"))
					: "");
			archivePurges.add(doc);
		}
		return archivePurges;
	}

	/**
	 * Checks if is archive purge exists.
	 *
	 * @param transaction the transaction
	 * @return true, if is archive purge exists
	 * @throws Exception the exception
	 */
	private boolean isArchivePurgeExists(final String transaction) throws Exception {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM archive_purge WHERE transaction = ?",
				new Object[] { transaction }, Integer.class) > 0 ? true : false;
	}

}
