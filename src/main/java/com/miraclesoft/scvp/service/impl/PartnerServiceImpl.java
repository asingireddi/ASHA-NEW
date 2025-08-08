package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;
import static com.miraclesoft.scvp.util.SqlCondition.equalOperator;
import static com.miraclesoft.scvp.util.SqlCondition.likeOperator;
import static java.util.Objects.nonNull;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.dao.DataAccessException;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.Partner;
import com.miraclesoft.scvp.reports.Report;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.DataSourceDataProvider;
import static com.miraclesoft.scvp.util.SqlCondition.likeOperatorStartWith;

/**
 * The Class PartnerServiceImpl.
 *
 * @author Priyanka Kolla
 */
@Component
@SuppressWarnings("PMD.TooManyStaticImports")
public class PartnerServiceImpl {

	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	@Autowired
	private HttpServletRequest httpServletRequest;

	/** The data source data provider. */
	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;

	@Autowired
	private Report report;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(PartnerServiceImpl.class.getName());

	/**
	 * Find all.
	 *
	 * @param partner the partner
	 * @return the list
	 */
	public CustomResponse findAll(final Partner partner) {
		final List<Partner> partners = new ArrayList<Partner>();
		int count = 0;
		try {
			final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
			final String partnerIdentifier = partner.getPartnerIdentifier();
			final String partnerName = partner.getPartnerName();
			final String status = partner.getStatus();
			final String countryCode = partner.getCountryCode();
			final StringBuilder partnerSearchQuery = new StringBuilder();
			final StringBuilder criteriaForPartnerSearchQuery = new StringBuilder();
			final StringBuilder sortingAndPaginationQuery = new StringBuilder();
			String partnerJoinQuery = "";
			String userIdRequired = "";
			List<Object> params = new ArrayList<>();
			final int userId = Integer.parseInt(tokenAuthenticationService.getUserIdfromToken());
			boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAll").toString());
			partnerJoinQuery = !all ? dataSourceDataProvider.partnersVisibilityWithTpJoinCondition().toString() : " ";
			userIdRequired = !all ? " and pv.user_id = ?" : " ";
			if (!all) {
				params.add(userId);
				System.out.println(userId);
			}
			partnerSearchQuery
					.append("SELECT t.id, t.name,  td.state,"
							+ " t.status, t.created_by, CONVERT_TZ(t.created_ts,@@session.time_zone,'")
					.append(userTimeZone)
					.append("') as created_ts FROM tp t LEFT JOIN tp_details td ON" + " (td.tp_id = t.id) ")
					.append(partnerJoinQuery).append(" WHERE 1 = 1").append(userIdRequired);
			if (nonNull(partnerIdentifier) && !"".equals(partnerIdentifier.trim())) {
				criteriaForPartnerSearchQuery.append(likeOperatorStartWith("td.tp_id"));
				params.add(partnerIdentifier.trim() + " % ");
			}
			if (nonNull(partnerName) && !"".equals(partnerName.trim())) {
				criteriaForPartnerSearchQuery.append(likeOperator("td.tp_name"));
				params.add(partnerName.trim() + " % ");
			}
			if (nonNull(status) && !"-1".equals(status) && !"".equals(status.trim())) {
				criteriaForPartnerSearchQuery.append(equalOperator("t.status"));
				params.add(status.trim());
			}
			if (nonNull(countryCode) && !"".equals(countryCode.trim())) {
				criteriaForPartnerSearchQuery.append(likeOperatorStartWith("td.state"));
				params.add(countryCode.trim() + " % ");
			}
			String sortField = partner.getSortField();
			sortField = sortField.equals("partnerName") ? "partner_Name" : sortField;
			if (nonNull(sortField) && nonNull(partner.getSortOrder())) {
				sortingAndPaginationQuery.append(dataSourceDataProvider.criteriaForSortingAndPagination(sortField,
						partner.getSortOrder(), partner.getLimit(), partner.getOffSet()));
			}
			partnerSearchQuery.append(criteriaForPartnerSearchQuery).append(sortingAndPaginationQuery);
			if (partner.getCountFlag()) {
				String countQuery = "SELECT COUNT(t.id) FROM tp t LEFT JOIN tp_details td ON (td.tp_id = t.id)"
						+ partnerJoinQuery + " WHERE 1 = 1" + userIdRequired + criteriaForPartnerSearchQuery;
				List<Object> countParams = new ArrayList<>(params);
				count = jdbcTemplate.queryForObject(countQuery, Integer.class, countParams.toArray());
			}
			System.out.println(partnerSearchQuery.toString());
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(partnerSearchQuery.toString(),
					params.toArray());
			for (final Map<String, Object> row : rows) {
				final Partner doc = new Partner();
				doc.setPartnerIdentifier(nonNull(row.get("id")) ? (String) row.get("id") : "");
				doc.setPartnerName(nonNull(row.get("name")) ? (String) row.get("name") : "");
				doc.setCountryCode(nonNull(row.get("state")) ? (String) row.get("state") : "");
				doc.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "");
				doc.setCreatedBy(nonNull(row.get("created_by")) ? (String) row.get("created_by") : "");
				doc.setCreatedDate(nonNull(row.get("created_ts"))
						? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("created_ts"))
						: "");
				partners.add(doc);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " findAll :: " + exception.getMessage());
		}
		return new CustomResponse(partners, count);
	}

	/**
	 * Find one.
	 *
	 * @param partnerId the partner id
	 * @return the partner
	 */
	public Partner findOne(final String partnerId) {
		final Partner partner = new Partner();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate
					.queryForList("SELECT t.id, t.name, " + " td.state, t.status, t.created_ts FROM tp t"
							+ " JOIN tp_details td ON (td.tp_id = t.id) WHERE t.id = ?", partnerId);
			for (final Map<String, Object> row : rows) {
				partner.setPartnerIdentifier(nonNull(row.get("id")) ? (String) row.get("id") : "-");
				partner.setPartnerName(nonNull(row.get("name")) ? (String) row.get("name") : "-");
				partner.setCountryCode(nonNull(row.get("state")) ? (String) row.get("state") : "-");
				partner.setStatus(nonNull(row.get("status")) ? (String) row.get("status") : "-");
				partner.setCreatedBy(nonNull(row.get("created_by")) ? (String) row.get("created_by") : "-");
				partner.setCreatedDate(nonNull(row.get("created_ts"))
						? new SimpleDateFormat("MM/dd/yyyy HH:mm").format(row.get("created_ts"))
						: "-");
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " findOne :: " + exception.getMessage());
		}
		return partner;
	}

	/**
	 * Update.
	 *
	 * @param partner the partner
	 * @return the string
	 * @throws DataAccessException the data access exception
	 */
	public String update(final Partner partner) throws DataAccessException {
		String responseString = "";
		int tpCount = 0;
		int tpDetailsCount = 0;
		try {
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			tpCount = jdbcTemplate.update(
					"UPDATE tp SET name = ?, modified_by = ?, modified_ts = convert_tz(current_timestamp, @@session.time_zone, '"
							+ defaultTimeZone + "'), status = ? WHERE id = ?",
					new Object[] { partner.getPartnerName(), partner.getCreatedBy(), partner.getStatus(),
							partner.getPartnerIdentifier() });
			if (tpCount > 0) {
				tpDetailsCount = jdbcTemplate.update("UPDATE tp_details SET tp_name = ?,  state = ? WHERE tp_id = ?",
						new Object[] { partner.getPartnerName(), partner.getCountryCode(),
								partner.getPartnerIdentifier() });
			}
			responseString = tpCount > 0 && tpDetailsCount > 0 ? "Partner updated succesfully."
					: "Something went wrong !";
		} catch (Exception exception) {
			logger.log(Level.ERROR, " update :: " + exception.getMessage());
		}
		return responseString;
	}

	/**
	 * Save.
	 *
	 * @param partner the partner
	 * @return the string
	 */
	public String save(final Partner partner) {
		String responseString = "";
		try {
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			if (!isPartnerExists(partner.getPartnerIdentifier())) {
				final int tpInsertCount = jdbcTemplate.update(
						"INSERT INTO tp (id, name, created_by, status, modified_by, modified_ts) VALUES (?, ?, ?, ?,?,convert_tz(current_timestamp, @@session.time_zone, '"
								+ defaultTimeZone + "'))",
						new Object[] { partner.getPartnerIdentifier(), partner.getPartnerName(), partner.getCreatedBy(),
								partner.getStatus(), partner.getCreatedBy() });
				// getCurrentTimestamp();
				if (tpInsertCount > 0) {
					final int tpDetailsInsertCount = jdbcTemplate.update(
							"INSERT INTO tp_details (tp_id, tp_name,  state) VALUES(?, ?, ?)",
							new Object[] { partner.getPartnerIdentifier(), partner.getPartnerName(),
									partner.getCountryCode() });
					if (tpDetailsInsertCount > 0) {
						responseString = "Partner added succesfully.";
					}
				} else {
					responseString = "Please try again!";
				}
			} else {
				responseString = "Partner Id already existed!";
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " save :: " + exception.getMessage());
		}
		return responseString;
	}

	/**
	 * All partners.
	 *
	 * @return the map
	 * @throws Exception the exception
	 */
	public List<Map<String, String>> allPartners() throws Exception {
		final int userId = Integer.parseInt(tokenAuthenticationService.getUserIdfromToken());
		boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAll").toString());
		return all ? dataSourceDataProvider.allTradingPartnersListOfMap()
				: dataSourceDataProvider.userTradingPartners(userId);
	}

	/**
	 * Checks if is partner exists.
	 *
	 * @param id the id
	 * @return true, if is partner exists
	 * @throws Exception the exception
	 */
	private boolean isPartnerExists(final String id) throws Exception {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM tp WHERE id = ?", new Object[] { id },
				Integer.class) > 0 ? true : false;
	}

	@SuppressWarnings("unchecked")
	public ResponseEntity<InputStreamResource> download(Partner partner) throws IOException {
		return getInputStreamResource(
				new File(report.downloadPartnersData((List<Partner>) findAll(partner).getData())));

	}

	public String deletePartner(String partnerId) {
		int tpDetailsDeleteCount = 0;
		try {
			final int tpDeleteCount = jdbcTemplate.update("DELETE FROM tp WHERE id = ?", partnerId);
			if (tpDeleteCount > 0) {
				tpDetailsDeleteCount = jdbcTemplate.update("DELETE FROM tp_details WHERE tp_id = ?", partnerId);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " deletePartner :: " + exception.getMessage());
		}
		return tpDetailsDeleteCount > 0 ? "Partner deleted seccessfully" : "Failed to delete partner";
	}
}
