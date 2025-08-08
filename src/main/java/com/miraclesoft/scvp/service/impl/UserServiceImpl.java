package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.SqlCondition.equalOperator;
import static com.miraclesoft.scvp.util.SqlCondition.likeOperatorStartWith;
import static java.util.Objects.nonNull;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;
import java.util.TreeMap;

import javax.mail.MessagingException;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.mail.MailManager;
import com.miraclesoft.scvp.model.CustomResponse;
import com.miraclesoft.scvp.model.User;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.DataSourceDataProvider;
import com.miraclesoft.scvp.util.PasswordUtil;

/**
 * The Class UserServiceImpl.
 *
 * @author Priyanka Kolla
 */
@Component
public class UserServiceImpl {

	/** The mail manager. */
	@Autowired
	private MailManager mailManager;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** The data source data provider. */
	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;
	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;
	/** The logger. */
	private static Logger logger = LogManager.getLogger(UserServiceImpl.class.getName());

	/** The Constant ONE. */
	private static final int ONE = 1;

	/** The Constant TWO. */
	private static final int TWO = 2;

	/** The Constant THREE. */
	private static final int THREE = 3;

	/** The Constant FOUR. */
	private static final int FOUR = 4;

	/** The Constant FIVE. */
	private static final int FIVE = 5;

	/** The Constant SIX. */
	private static final int SIX = 6;

	/** The Constant SEVEN. */
	private static final int SEVEN = 7;

	/** The Constant EIGHT. */
	private static final int EIGHT = 8;

	/** The Constant NINE. */
	private static final int NINE = 9;

	/** The Constant TEN. */
	private static final int TEN = 10;

	/** The Constant ELEVEN. */
	private static final int ELEVEN = 11;

	/** The Constant TWELVE. */
	private static final int TWELVE = 12;

	/** The Constant THIRTEEN. */
	private static final int THIRTEEN = 13;

	private static final int FOURTEEN = 14;

	private static final int FIFTEEN = 15;

	private static final int SIXTEEN = 16;

	/**
	 * Adds the user.
	 *
	 * @param user the user
	 * @return the string
	 * @throws SQLException       the SQL exception
	 * @throws MessagingException the messaging exception
	 */
	public String addUser(final User user) throws SQLException, MessagingException {
		String responseString = null;
		Connection connection = null;
		PreparedStatement statement = null;
		try {
			final String email = user.getEmail();
			if (!isUserExists(email)) {
				final String createdBy = user.getCreatedBy();
				final String generatedPassword = PasswordUtil.encryptPassword(generatePassword());
				final String generatedUserLoginId = loginIdFromEmail(email);
				connection = jdbcTemplate.getDataSource().getConnection();
				statement = connection
						.prepareStatement("CALL create_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
				statement.setString(ONE, generatedUserLoginId);
				statement.setString(TWO, generatedPassword);
				statement.setString(THREE, user.getFirstName());
				statement.setString(FOUR, user.getLastName());
				statement.setString(FIVE, email);
				statement.setString(SIX, user.getOfficePhone());
				statement.setInt(SEVEN, user.getDepartmentId());
				statement.setString(EIGHT, user.getStatus());
				statement.setString(NINE, createdBy);
				statement.setString(TEN, createdBy);
				statement.setInt(ELEVEN, user.getRoleId());
				statement.setString(TWELVE, createdBy);
				statement.setInt(THIRTEEN, 1);
				statement.setInt(FOURTEEN, user.isFileVisibility() ? 1 : 0);
				statement.setString(FIFTEEN, user.getTimeZone());
				statement.setInt(SIXTEEN, user.isAddPartnersAccess() ? 1 : 0);
				final int isUserCreated = statement.executeUpdate();
				mailManager.sendUserLoginIdAndPassword(email, generatedUserLoginId,
						user.getFirstName() + " " + user.getLastName(), PasswordUtil.decryptPassword(generatedPassword),
						user.getPartnerId(), user.getPartnerName());
				if (isUserCreated >= 1) {
					String query = "SELECT id FROM m_user where loginid='" + generatedUserLoginId + "'";
					List<Map<String, Object>> response = jdbcTemplate.queryForList(query);
					if (!response.isEmpty() && response.size() > 0) {
						user.setUserId(Long.parseLong(response.get(0).get("id").toString()));
						String responseForUserFlow = assignUserFlows(user);
						if (responseForUserFlow.equals("Successfully assigned flows.")) {
							String createPartnersResponse = createPartnersForUser(user);
							String sfgPartnersResponse = createSfgPartners(user);
							if (createPartnersResponse.equals("partners added successfully")
									&& sfgPartnersResponse.equals("partners added successfully")) {
								responseString = "User created successfully.";
							} else {
								responseString = "User created but failed to add partners.";
							}
						} else {
							responseString = "User Not created.";
						}
					} else {
						responseString = "User Not created.";
					}

				} else {
					responseString = "User Not created.";
				}
			} else {
				responseString = "User already registered with this email Id!";

			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " addUser :: " + exception.getMessage());
		} finally {
			if (nonNull(statement)) {
				statement.close();
				statement = null;
			}
			if (nonNull(connection)) {
				connection.close();
				connection = null;
			}
		}
		return responseString;
	}

	private String createSfgPartners(User user) {
		String response = null;
		String insertPartners = "insert into  sfg_partner_visibilty(user_id,sfg_partner_name) values (?,?)";
		try {
			final long userId = user.getUserId();
			int[] result = jdbcTemplate.batchUpdate(insertPartners, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(final PreparedStatement pStmt, final int j) throws SQLException {
					pStmt.setLong(1, userId);
					pStmt.setString(2, user.getSfgPartnersNames()[j]);
				}

				@Override
				public int getBatchSize() {
					return user.getSfgPartnersNames().length;
				}

			});

			response = "partners added successfully";
		} catch (Exception exception) {
			response = "failed to add partners";
			logger.log(Level.ERROR, " createPartnersForUser :: " + exception.getMessage());
		}
		return response;
	}

	/**
	 * User search.
	 *
	 * @param user the user
	 * @return the list
	 */
	public CustomResponse userSearch(final User user) {
		final List<User> usersList = new ArrayList<User>();
		int count = 0;
		List<Object> params = new ArrayList<>();
		try {
			String firstName = user.getFirstName();
			String lastName = user.getLastName();
			String loginId = user.getLoginId();
			String status = user.getStatus();
			String sortField = user.getSortField();
			final StringBuilder userSearchQuery = new StringBuilder();
			final StringBuilder searchCriteriaQuery = new StringBuilder();
			final StringBuilder sortingAndPaginationQuery = new StringBuilder();
			userSearchQuery.append(
					"SELECT mu.id, concat(fnme,' ',lnme) AS username, email, file_visibility, office_phone, active, loginid, timezone"
							+ " FROM m_user mu WHERE 1= 1 ");
			if (nonNull(firstName) && !"".equals(firstName.trim())) {
				searchCriteriaQuery.append(likeOperatorStartWith("fnme"));
				params.add(firstName.trim() + "%");
			}
			if (nonNull(lastName) && !"".equals(lastName.trim())) {
				searchCriteriaQuery.append(likeOperatorStartWith("lnme"));
				params.add(lastName.trim() + "%");
			}
			if (nonNull(loginId) && !"".equals(loginId.trim())) {
				searchCriteriaQuery.append(likeOperatorStartWith("loginid"));
				params.add(loginId.trim() + "%");
			}
			if (nonNull(status) && !"-1".equals(status)) {
				searchCriteriaQuery.append(equalOperator("active"));
				params.add(status);
			}
			sortField = sortField.equals("status") ? "active" : sortField;
			sortField = sortField.isEmpty() ? "createdDate" : sortField;
			if (nonNull(sortField) && nonNull(user.getSortOrder())) {
				sortingAndPaginationQuery.append(dataSourceDataProvider.criteriaForSortingAndPagination(sortField,
						user.getSortOrder(), user.getLimit(), user.getOffSet()));
			}
			userSearchQuery.append(searchCriteriaQuery).append(sortingAndPaginationQuery);
			if (user.getCountFlag()) {
			    String countQuery = "SELECT COUNT(id) FROM m_user WHERE 1=1" + searchCriteriaQuery.toString();
			    count = jdbcTemplate.queryForObject(countQuery, params.toArray(), Integer.class);
			}
	
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(userSearchQuery.toString(), params.toArray());
			for (final Map<String, Object> row : rows) {
				final User doc = new User();
				doc.setUserId(nonNull(row.get("id")) ? (Long) row.get("id") : 0L);
				// doc.setRoleId(nonNull(row.get("role_id")) ? (Integer) row.get("role_id") :
				// 0);
				doc.setLoginId(nonNull(row.get("loginid")) ? (String) row.get("loginid") : "");
				doc.setTimeZone(nonNull(row.get("timezone")) ? (String) row.get("timezone").toString() : "");
				doc.setUserName(nonNull(row.get("username")) ? (String) row.get("username") : "");
				doc.setEmail(nonNull(row.get("email")) ? (String) row.get("email") : "-");
				doc.setOfficePhone(nonNull(row.get("office_phone")) ? (String) row.get("office_phone") : "");
				// doc.setPartnerIds(nonNull(row.get("role_id")) &&
				// row.get("role_id").toString().equals("1")
				// ?
				// dataSourceDataProvider.allTradingPartnersList().stream().toArray(String[]::new)
				// :
				// dataSourceDataProvider.getUsersPartners(doc.getUserId()).stream().toArray(String[]::new));
				if (nonNull(row.get("active"))) {
					if (((String) row.get("active")).equals("A")) {
						doc.setStatus("Active");
					} else if (((String) row.get("active")).equals("I")) {
						doc.setStatus("InActive");
					} else if (((String) row.get("active")).equals("T")) {
						doc.setStatus("Terminated");
					}
				} else {
					doc.setStatus("-");
				}
//				boolean fileVisibility = false;
//				if (row.get("file_visibility").equals(true)) {
//					fileVisibility = true;
//				}
//				doc.setFileVisibility(fileVisibility);
				usersList.add(doc);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " userSearch :: " + exception.getMessage());
		}
		return new CustomResponse(usersList, count);
	}

	/**
	 * User information.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	public User userInformation(final Long userId) {
		final User user = new User();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"SELECT mu.id AS userId, mu.fnme, mu.lnme, mu.email, mu.office_phone, mu.file_visibility, "
							+ "mu.dept_id, mu.active, mur.role_id, mu.timezone FROM m_user mu "
							+ "JOIN m_user_roles mur ON (mu.id = mur.user_id) WHERE mu.id = ?", userId);
			for (final Map<String, Object> row : rows) {
				user.setUserId(nonNull(row.get("userId")) ? (Long) row.get("userId") : 0L);
				user.setFirstName(nonNull(row.get("fnme")) ? (String) row.get("fnme") : "");
				user.setLastName(nonNull(row.get("lnme")) ? (String) row.get("lnme") : "");
				user.setEmail(nonNull(row.get("email")) ? (String) row.get("email") : "");
				user.setOfficePhone(nonNull(row.get("office_phone")) ? (String) row.get("office_phone") : "");
				user.setDepartmentId(nonNull(row.get("dept_id")) ? (Integer) row.get("dept_id") : -1);
				user.setStatus(nonNull(row.get("active")) ? (String) row.get("active") : "-1");
				user.setRoleId(nonNull(row.get("role_id")) ? (Integer) row.get("role_id") : 0);
				user.setTimeZone(nonNull(row.get("timezone")) ? (String) row.get("timezone") : "");
				user.setPartnerIds(nonNull(row.get("role_id")) && row.get("role_id").toString().equals("1")
						? dataSourceDataProvider.allTradingPartnersList().stream().toArray(String[]::new)
						: dataSourceDataProvider.getUsersPartners(userId).stream().toArray(String[]::new));
				user.setSfgPartnersNames(nonNull(row.get("role_id")) && row.get("role_id").toString().equals("1")
						? dataSourceDataProvider.allSfgTradingPartnersList().stream().toArray(String[]::new)
						: dataSourceDataProvider.getSfgUsersPartners(userId).stream().toArray(String[]::new));
				boolean fileVisibility = false;
				if (row.get("file_visibility").equals(true)) {
					fileVisibility = true;
				}
				user.setFileVisibility(fileVisibility);
				boolean addPartnersAccess = false;
				if (row.get("add_partners").equals(true)) {
					addPartnersAccess = true;
				}
				user.setAddPartnersAccess(addPartnersAccess);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " userInformation :: " + exception.getMessage());
		}
		return user;
	}

	/**
	 * User flows information.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	public User userFlowsInformation(final long userId) {
		final User user = new User();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"SELECT mu.id AS userId, mu.loginid, mur.role_id,partner_id,partner_name,webforms,mscvp,tpm,user_type,concat(fnme,' ',lnme) AS username FROM m_user mu "
							+ "JOIN m_user_roles mur ON (mu.id = mur.user_id) WHERE id = ?", userId);
			for (final Map<String, Object> row : rows) {
				user.setUserId(nonNull(row.get("userId")) ? (Long) row.get("userId") : 0L);
				user.setUserName(nonNull(row.get("username")) ? (String) row.get("username") : "-");
				user.setLoginId(nonNull(row.get("loginid")) ? (String) row.get("loginid") : "-");
				user.setRoleId(nonNull(row.get("role_id")) ? (int) row.get("role_id") : 0);
				user.setRoleName(dataSourceDataProvider.getRoleNameByRoleId((Integer) row.get("role_id")));
				user.setPartnerName(nonNull(row.get("partner_name")) ? (String) row.get("partner_name") : "-");
				user.setPartnerId(nonNull(row.get("partner_id")) ? (String) row.get("partner_id") : "-");
				user.setWebForms(nonNull(row.get("webforms")) ? (String) row.get("webforms") : "false");
				user.setTpm(nonNull(row.get("tpm")) ? (String) row.get("tpm") : "false");
				user.setMscvp(nonNull(row.get("mscvp")) ? (String) row.get("mscvp") : "false");
				user.setUserType(nonNull(row.get("user_type")) ? (String) row.get("user_type") : "-");
			}
			user.setPrimaryFlowId(dataSourceDataProvider.getPrimaryFlowID(userId));
			final Map<Integer, String> assigneFlows = dataSourceDataProvider.getAssignedFlows(userId);
			for (final Map.Entry<Integer, String> entry : assigneFlows.entrySet()) {
				String value = entry.getValue();
				if (value.equals("Manufacturing")) {
					user.setManufacturing(true);
				}
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " userFlowsInformation :: " + exception.getMessage());
		}
		return user;
	}

	/**
	 * Assign user flows.
	 *
	 * @param user the user
	 * @return the string
	 */
	public String assignUserFlows(final User user) {
		String responseString = null;
		int updatedRows = 0;
		try {
			final long userId = user.getUserId();
			jdbcTemplate.update("DELETE FROM m_user_flows_action WHERE user_id = ?", userId);
			responseString = (jdbcTemplate
					.update("INSERT INTO m_user_flows_action (priority, flowid, user_id) VALUES (1, 2, ?)", userId)) > 0
							? "Successfully assigned flows."
							: "Failed to assign flows!";
			// String insertquery = "INSERT INTO m_user_roles(user_id, role_id, priority,
			// date_activated, activated_by) VALUES(last_id, temp_role_id, temp_priority,
			// CURRENT_TIMESTAMP, temp_activated_by)";

			if (responseString.equalsIgnoreCase("Successfully assigned flows.")) {
				updatedRows = jdbcTemplate.update(
						"UPDATE m_user SET active = ?,  partner_id = ?,  partner_name = ?,  webforms = ?,  tpm = ?,  mscvp = ?, user_type = ? WHERE id = ?",
						new Object[] { user.getActive(), user.getPartnerId(), user.getPartnerName(), user.getWebForms(),
								user.getTpm(), user.getMscvp(), user.getUserType(), userId });
				responseString = updatedRows > 0 ? "Partner updated succesfully." : "Something went wrong !";
			}

			String query = "SELECT id,passwd,fnme,lnme,loginid,email,secondary_email FROM m_user where id=" + userId;
			List<Map<String, Object>> response = jdbcTemplate.queryForList(query);
			if (!response.isEmpty() && response.size() > 0) {
				user.setUserId(Long.parseLong(response.get(0).get("id").toString()));
				user.setPassword(response.get(0).get("passwd").toString());
				user.setFirstName(response.get(0).get("fnme").toString());
				user.setLastName(response.get(0).get("lnme").toString());
				user.setLoginId(response.get(0).get("loginid").toString());
				user.setEmail(response.get(0).get("email").toString());
				if (response.get(0).get("secondary_email") != null) {
					user.setSecondaryEmail(response.get(0).get("secondary_email").toString());
				}
				mailManager.sendUserLoginIdAndPassword(user.getEmail(), user.getLoginId(),
						user.getFirstName() + " " + user.getLastName(),
						PasswordUtil.decryptPassword(user.getPassword()), user.getPartnerId(), user.getPartnerName());

			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " assignUserFlows :: " + exception.getMessage());
		}

		return responseString;
	}

	/**
	 * Create partners for user.
	 *
	 * @param user the user
	 * @return the string
	 */
	public String createPartnersForUser(final User user) {
		String response = null;
		String insertPartners = "insert into partner_visibilty (user_id,partner_id)values (?,?)";
		try {
			final long userId = user.getUserId();
			int[] result = jdbcTemplate.batchUpdate(insertPartners, new BatchPreparedStatementSetter() {
				@Override
				public void setValues(final PreparedStatement pStmt, final int j) throws SQLException {
					pStmt.setLong(1, userId);
					pStmt.setString(2, user.getPartnerIds()[j]);
				}

				@Override
				public int getBatchSize() {
					return user.getPartnerIds().length;
				}

			});

			response = "partners added successfully";
		} catch (Exception exception) {
			response = "failed to add partners";
			logger.log(Level.ERROR, " createPartnersForUser :: " + exception.getMessage());
		}
		return response;
	}

	/**
	 * Update user info.
	 *
	 * @param user the user
	 * @return the string
	 * @throws SQLException the SQL exception @ the service locator exception
	 */
	public String updateUserInfo(final User user) throws SQLException {
		Connection connection = null;
		PreparedStatement statement = null;
		String responseString = null;
		try {
			final String modifiedBy = user.getCreatedBy();
			connection = jdbcTemplate.getDataSource().getConnection();
			statement = connection.prepareStatement("CALL update_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
			statement.setLong(ONE, user.getUserId());
			statement.setString(TWO, user.getFirstName());
			statement.setString(THREE, user.getLastName());
			statement.setString(FOUR, user.getOfficePhone());
			statement.setInt(FIVE, user.getDepartmentId());
			statement.setString(SIX, user.getStatus());
			statement.setInt(SEVEN, user.getRoleId());
			statement.setString(EIGHT, modifiedBy);
			statement.setInt(NINE, user.isFileVisibility() ? 1 : 0);
			statement.setString(TEN, user.getTimeZone());
			statement.setInt(ELEVEN, user.isAddPartnersAccess() ? 1 : 0);
			statement.execute();
			deletePartnersForUser(user.getUserId());
			createPartnersForUser(user);
			deleteSfgPartnersForUser(user.getUserId());
			createSfgPartners(user);
			responseString = "User updated successfully.";
		} catch (Exception exception) {
			logger.log(Level.ERROR, " updateUserInfo :: " + exception.getMessage());
		} finally {
			if (nonNull(statement)) {
				statement.close();
				statement = null;
			}
			if (nonNull(connection)) {
				connection.close();
				connection = null;
			}
		}
		return responseString;
	}

	private String deleteSfgPartnersForUser(Long userId) {
		String responseString = null;
		try {
			int jdbcResponse = jdbcTemplate.update("DELETE FROM sfg_partner_visibilty where user_id=?", userId);
			if (jdbcResponse > 0) {
				responseString = "Records deleted successfully";
			} else {
				responseString = "Failed to delete";
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " deletePartnersForUser :: " + exception.getMessage());
		}
		return responseString;

	}

	/**
	 * delete user info.
	 *
	 * @param userId the userId
	 * @return the string
	 */
	public String deletePartnersForUser(final Long userId) {
		String responseString = null;
		try {
			int jdbcResponse = jdbcTemplate.update("DELETE FROM partner_visibilty where user_id=?", userId);
			if (jdbcResponse > 0) {
				responseString = "Records deleted successfully";
			} else {
				responseString = "Failed to delete";
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " deletePartnersForUser :: " + exception.getMessage());
		}
		return responseString;
	}

	/**
	 * User profile.
	 *
	 * @param userId the user id
	 * @return the user
	 */
	public User userProfile(final long userId) {
		final User user = new User();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"SELECT concat(fnme,' ',lnme) AS username, email, location, designation, organization, office_phone, education, file_visibility, buyer_contacts, timezone "
							+ " FROM m_user WHERE id = ?", userId);
			for (final Map<String, Object> row : rows) {
				user.setUserName(nonNull(row.get("username")) ? (String) row.get("username") : "");
				user.setEmail(nonNull(row.get("email")) ? (String) row.get("email") : "");
				user.setLocation(nonNull(row.get("location")) ? (String) row.get("location") : "");
				user.setDesignation(nonNull(row.get("designation")) ? (String) row.get("designation") : "");
				user.setOrganization(nonNull(row.get("organization")) ? (String) row.get("organization") : "");
				user.setOfficePhone(nonNull(row.get("office_phone")) ? (String) row.get("office_phone") : "");
				user.setEducation(nonNull(row.get("education")) ? (String) row.get("education") : "");
				user.setBuyerContacts(nonNull(row.get("buyer_contacts")) ? (String) row.get("buyer_contacts") : "");
				user.setTimeZone(nonNull(row.get("timezone")) ? (String) row.get("timezone") : "");
				boolean fileVisibility = Objects.equals(row.get("file_visibility"), true);
				user.setFileVisibility(fileVisibility);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " userProfile :: " + exception.getMessage());
		}
		return user;
	}

	/**
	 * Update user profile.
	 *
	 * @param user the user
	 * @return the string
	 */
	public String updateUserProfile(final User user) {
		int updatedRows = 0;
		try {
			final String updateUserProfileQuery = "UPDATE m_user SET email = ?, designation = ?, location = ?,"
					+ " organization = ?, office_phone = ?, buyer_contacts = ?, timezone = ? WHERE id = ?";
			updatedRows = jdbcTemplate.update(updateUserProfileQuery,
					new Object[] { user.getEmail(), user.getDesignation(), user.getLocation(), user.getOrganization(),
							user.getOfficePhone(), user.getBuyerContacts(), user.getTimeZone(), user.getUserId() });
		} catch (Exception exception) {
			logger.log(Level.ERROR, " updateUserProfile :: " + exception.getMessage());
		}
		return updatedRows > 0 ? "Profile updated successfully" : "Please try again!";
	}

	/**
	 * Reset my password.
	 *
	 * @param loginId     the login id
	 * @param oldPassword the old password
	 * @param newPassword the new password
	 * @return the string
	 */
	public String resetMyPassword(final String loginId, final String oldPassword, final String newPassword) {
		String response = "Please try again!";
		try {
			int updatedRows = 0;
			final User user = userByLoginId(loginId);
			if (oldPassword.equals(PasswordUtil.decryptPassword(user.getPassword()))) {
				updatedRows = jdbcTemplate.update("UPDATE m_user SET passwd = ? WHERE loginid = ?", PasswordUtil.encryptPassword(newPassword), loginId);
				if (updatedRows > 0) {
					response = "You have changed password succesfully. Please login again.";
				} else {
					response = "Something went wrong! Please try again.";
				}
			} else {
				response = "Please enter valid old password!";
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " resetMyPassword :: " + exception.getMessage());
		}
		return response;
	}

	/**
	 * User by email.
	 *
	 * @param email the email
	 * @return the user
	 */
	public User userByEmail(final String email) {
		final User user = new User();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"SELECT id, loginid, passwd, concat(fnme,' ',lnme) AS username, email, office_phone, active FROM m_user WHERE email = ?", email);
			for (final Map<String, Object> row : rows) {
				user.setUserId(nonNull(row.get("id")) ? (Long) row.get("id") : 0L);
				user.setLoginId(nonNull(row.get("loginid")) ? (String) row.get("loginid") : "");
				user.setPassword(nonNull(row.get("passwd")) ? (String) row.get("passwd") : "");
				user.setUserName(nonNull(row.get("username")) ? (String) row.get("username") : "");
				user.setEmail(nonNull(row.get("email")) ? (String) row.get("email") : "");
				user.setOfficePhone(nonNull(row.get("office_phone")) ? (String) row.get("office_phone") : "");
				if (nonNull(row.get("active"))) {
					if (((String) row.get("active")).equals("A")) {
						user.setStatus("Active");
					} else if (((String) row.get("active")).equals("I")) {
						user.setStatus("InActive");
					} else if (((String) row.get("active")).equals("T")) {
						user.setStatus("Terminated");
					}
				} else {
					user.setStatus("");
				}
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " userByEmail :: " + exception.getMessage());
		}
		return user;
	}

	/**
	 * User by loginId.
	 *
	 * @param loginId the loginId
	 * @return the user
	 */
	public User userByLoginId(final String loginId) {
		final User user = new User();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(
					"SELECT id, loginid, passwd, concat(fnme,' ',lnme) AS username, email, office_phone, active FROM m_user WHERE loginid = ?", loginId);
			for (final Map<String, Object> row : rows) {
				user.setUserId(nonNull(row.get("id")) ? (Long) row.get("id") : 0L);
				user.setLoginId(nonNull(row.get("loginid")) ? (String) row.get("loginid") : "");
				user.setPassword(nonNull(row.get("passwd")) ? (String) row.get("passwd") : "");
				user.setUserName(nonNull(row.get("username")) ? (String) row.get("username") : "");
				user.setEmail(nonNull(row.get("email")) ? (String) row.get("email") : "");
				user.setOfficePhone(nonNull(row.get("office_phone")) ? (String) row.get("office_phone") : "");
				if (nonNull(row.get("active"))) {
					if (((String) row.get("active")).equals("A")) {
						user.setStatus("Active");
					} else if (((String) row.get("active")).equals("I")) {
						user.setStatus("InActive");
					} else if (((String) row.get("active")).equals("T")) {
						user.setStatus("Terminated");
					}
				} else {
					user.setStatus("");
				}
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " userByLoginId :: " + exception.getMessage());
		}
		return user;
	}

	/**
	 * Forgot password.
	 *
	 * @param email the email
	 * @return the string
	 */
	public String forgotPassword(final String email) {
		String response = "Email is not existed. Please try again.";
		try {
			if (isUserExists(email)) {
				final String generatedPassword = PasswordUtil.encryptPassword(generatePassword());
				final int updatedRows = jdbcTemplate
						.update("UPDATE m_user SET passwd = ? WHERE email = ?", generatedPassword, email);
				if (updatedRows > 0) {
					mailManager.sendUpdatedPassword(email, userByEmail(email).getUserName(),
							PasswordUtil.decryptPassword(generatedPassword));
					response = "Password updated and sent to mail.";
				}
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " forgotPassword :: " + exception.getMessage());
		}
		return response;
	}

	/**
	 * Active users.
	 *
	 * @return the map
	 */
	public Map<String, String> activeUsers() {
		final Map<String, String> userMap = new TreeMap<String, String>();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate
					.queryForList("SELECT email, CONCAT(fnme,' ',lnme) AS fullname FROM m_user mu JOIN m_user_roles mur"
							+ " ON (mu.id = mur.user_id) WHERE mur.role_id != 1 AND mu.active = 'A' ");
			for (final Map<String, Object> row : rows) {
				userMap.put((String) row.get("email"), (String) row.get("fullname"));
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " activeUsers :: " + exception.getMessage());
		}
		return userMap;
	}

	/**
	 * Checks if is user exists.
	 *
	 * @param email the email
	 * @return true, if is user exists
	 * @throws Exception the exception
	 */
	public boolean isUserExists(final String email) throws Exception {
		return jdbcTemplate.queryForObject("SELECT COUNT(*) FROM m_user WHERE email = ?", new Object[] { email },
				Integer.class) > 0 ? true : false;
	}

	/**
	 * Login id from email.
	 *
	 * @param mailId the mail id
	 * @return the string
	 */
	private String loginIdFromEmail(final String mailId) {
		String baseLoginId = mailId.substring(0, mailId.indexOf('@')).toLowerCase();
		String query = "SELECT loginid FROM m_user WHERE loginid LIKE ?";
		List<String> existingIds = jdbcTemplate.queryForList(query,new Object[]{baseLoginId + "%"}, String.class);
		
		
		if (!existingIds.contains(baseLoginId)) {
			return baseLoginId;
		}

		int suffix = 1;
		String newLoginId;
		do {
			newLoginId = baseLoginId + suffix;
			suffix++;
		} while (existingIds.contains(newLoginId));

		return newLoginId;
	}

//	private String loginIdFromEmail(final String mailId) {
//		final int atOccurance = mailId.indexOf('@');
//		return mailId.substring(0, atOccurance).toLowerCase();
//	}

	/**
	 * Generate password.
	 *
	 * @return the string
	 * @throws Exception the exception
	 */
	private String generatePassword() throws Exception {
		String generatedPassword = "";
		final int noOfCharacters = 8;
		final Random random = new Random(System.currentTimeMillis());
		final long randomOne = random.nextLong();
		final long randomTwo = random.nextLong();
		final String hashCodeOne = Long.toHexString(randomOne);
		final String hashCodeTwo = Long.toHexString(randomTwo);
		generatedPassword = hashCodeOne + hashCodeTwo;
		if (generatedPassword.length() > noOfCharacters) {
			generatedPassword = generatedPassword.substring(0, noOfCharacters);
		}
		return generatedPassword.toUpperCase();
	}

	/**
	 * Forgot username.
	 *
	 * @param email the email
	 * @return the string
	 */
	public String forgotUserName(final String email) {
		String response = "Email is not existed. Please try again.";
		try {
			if (nonNull(email) && isUserExists(email)) {
				mailManager.sendUserName(email, userByEmail(email).getLoginId());
				response = "Username sent to mail.";
			}
		} catch (Exception exception) {
			exception.printStackTrace();
			logger.log(Level.ERROR, " forgotUserName :: " + exception.getMessage());
		}
		return response;
	}

	/**
	 * delete user.
	 *
	 * @param userId the userId
	 * @return the string
	 */
//	public String deleteUser(final Long userId) throws SQLException {
//		String message = "oops something went wrong";
//		Connection connection = null;
//		PreparedStatement statement = null;
//		try {
//			if (!(userId.toString()).equals(tokenAuthenticationService.getUserIdfromToken())) {
//				connection = jdbcTemplate.getDataSource().getConnection();
//				statement = connection.prepareStatement("CALL delete_user(?)");
//				statement.setLong(ONE, userId);
//				statement.executeUpdate();
//				message = "User deleted successfully";
//				logger.log(Level.INFO,
//						userId + " --- user was deleted by ---> " + tokenAuthenticationService.getUserIdfromToken());
//			} else {
//				message = "Can't delete your self";
//			}
//		} catch (Exception exception) {
//			logger.log(Level.ERROR, " deleteUser :: " + exception.getMessage());
//		} finally {
//			if (nonNull(statement)) {
//				statement.close();
//				statement = null;
//			}
//			if (nonNull(connection)) {
//				connection.close();
//				connection = null;
//			}
//		}
//		return message;
//	}
	public String deleteUser(final Long userId) throws SQLException {
		String message = "Oops, something went wrong";
		Connection connection = null;
		PreparedStatement statement = null;

		try {
			if (!(userId.toString()).equals(tokenAuthenticationService.getUserIdfromToken())) {
				connection = jdbcTemplate.getDataSource().getConnection();

				String sql = "DELETE FROM m_user WHERE id = ?";
				statement = connection.prepareStatement(sql);
				statement.setLong(1, userId);
				int rowsAffected = statement.executeUpdate();

				if (rowsAffected > 0) {
					message = "User deleted successfully";
					logger.log(Level.INFO, userId + " --- user was deleted by ---> "
							+ tokenAuthenticationService.getUserIdfromToken());
				} else {
					message = "User not found";
				}
			} else {
				message = "Can't delete yourself";
			}
		} catch (SQLException exception) {
			logger.log(Level.ERROR, "deleteUser :: " + exception.getMessage());
		} finally {
			if (nonNull(statement)) {
				statement.close();
				statement = null;
			}
			if (nonNull(connection)) {
				connection.close();
				connection = null;
			}
		}

		return message;
	}

	public String registerdUser(final User user) throws SQLException, MessagingException {
		String responseString = null;
		Connection connection = null;
		String response1 = null;
		int insertquery = 0;
		PreparedStatement statement = null;
		final String email = user.getEmail();
		final String createdBy = user.getCreatedBy();
		final String generatedUserLoginId = loginIdFromEmail(email);
		try {
			if (emailExists(user.getEmail())==0) {
				
			
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			final String generatedPassword = PasswordUtil.encryptPassword(generatePassword());
			String insertQuery = "INSERT INTO m_user(loginid, passwd, fnme, lnme, email, created_by, authorized_by, authorized_ts ) values"
					+ " (?,?,?,?,?,?,?,convert_tz(current_timestamp, @@session.time_zone, ?))";
			int isUserCreated = jdbcTemplate.update(insertQuery, generatedUserLoginId, generatedPassword,
					user.getFirstName(), user.getLastName(), email, createdBy, createdBy, defaultTimeZone);
			String selectQuery = "SELECT ID FROM m_user WHERE email = ?";
			String userId = jdbcTemplate.queryForObject(selectQuery, new Object[] { email }, String.class);
			insertquery = jdbcTemplate.update(
					"INSERT INTO m_user_roles(user_id, role_id, priority, date_activated, activated_by) VALUES(?,?,?,convert_tz(current_timestamp, @@session.time_zone, '"
							+ defaultTimeZone + "'),?)",
					new Object[] { userId, 1, 1, user.getCreatedBy() });
			response1 = insertquery > 0 ? " roles inserted succesfully." : "Something went wrong !";
			logger.info("User Flow Assignment Result: {}", response1);
			if (isUserCreated >= 1) {
				responseString = "Registartion successfull.You will get an email with user/password once admin approves";
				mailManager.sendRegistrationSuccessToUser(email, generatedUserLoginId,
						user.getFirstName() + " " + user.getLastName());
			} else {
				responseString = "User Not created.";
			}}
			else {
				responseString ="user already exists";
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " addUser :: " + exception.getMessage());
		} finally {
			if (nonNull(statement)) {
				statement.close();
				statement = null;
			}
			if (nonNull(connection)) {
				connection.close();
				connection = null;
			}
		}
		return responseString;
	}
//	public String registerdUser(final User user) throws SQLException, MessagingException {
//		String responseString = null;
//		Connection connection = null;
//		String response1 = null;
//		int insertquery = 0;
//		PreparedStatement statement = null;
//		final String email = user.getEmail();
//		final String createdBy = user.getCreatedBy();
//		final String generatedUserLoginId = loginIdFromEmail(email);
//		try {
//			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
//			final String generatedPassword = PasswordUtil.encryptPassword(generatePassword());
//			String insertQuery = "INSERT INTO m_user(loginid, passwd, fnme, lnme, email, created_by, authorized_by, authorized_ts ) values (?,?,?,?,?,?,?,convert_tz(current_timestamp, @@session.time_zone, '"
//					+ defaultTimeZone + "'))";
//			int isUserCreated = jdbcTemplate.update(insertQuery, generatedUserLoginId, generatedPassword,
//					user.getFirstName(), user.getLastName(), email, createdBy, createdBy);
//			String selectQuery = "SELECT ID FROM m_user WHERE email = ?";
//			String userId = jdbcTemplate.queryForObject(selectQuery, new Object[] { email }, String.class);
//			insertquery = jdbcTemplate.update(
//					"INSERT INTO m_user_roles(user_id, role_id, priority, date_activated, activated_by) VALUES(?,?,?,convert_tz(current_timestamp, @@session.time_zone, '"
//							+ defaultTimeZone + "'),?)",
//					new Object[] { userId, 1, 1, user.getCreatedBy() });
//			response1 = insertquery > 0 ? " roles inserted succesfully." : "Something went wrong !";
//			if (isUserCreated >= 1) {
//				responseString = "Registartion successfull.You will get an email with user/password once admin approves";
//				mailManager.sendRegistrationSuccessToUser(email, generatedUserLoginId,
//						user.getFirstName() + " " + user.getLastName());
//			} else {
//				responseString = "User Not created.";
//			}
//		} catch (Exception exception) {
//			logger.log(Level.ERROR, " addUser :: " + exception.getMessage());
//		} finally {
//			if (nonNull(statement)) {
//				statement.close();
//				statement = null;
//			}
//			if (nonNull(connection)) {
//				connection.close();
//				connection = null;
//			}
//		}
//		return responseString;
//	}

//	public String registerUser1(final User user) throws SQLException, MessagingException {
//		String responseString = null;
//		Connection connection = null;
//		PreparedStatement statement = null;
//		try {
//			final String email = user.getEmail();
//			if (!isUserExists(email)) {
//				final String createdBy = user.getCreatedBy();
//				final String generatedPassword = PasswordUtil.encryptPassword(generatePassword());
//				final String generatedUserLoginId = loginIdFromEmail(email);
//				connection = jdbcTemplate.getDataSource().getConnection();
//				statement = connection
//						.prepareStatement("CALL create_user(?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
//				statement.setString(ONE, generatedUserLoginId);
//				statement.setString(TWO, generatedPassword);
//				statement.setString(THREE, user.getFirstName());
//				statement.setString(FOUR, user.getLastName());
//				statement.setString(FIVE, email);
//				statement.setString(SIX, user.getOfficePhone());
//				statement.setInt(SEVEN, user.getDepartmentId());
//				statement.setString(EIGHT, user.getStatus());
//				statement.setString(NINE, createdBy);
//				statement.setString(TEN, createdBy);
//				statement.setInt(ELEVEN, user.getRoleId());
//				statement.setString(TWELVE, createdBy);
//				statement.setInt(THIRTEEN, 1);
//				statement.setInt(FOURTEEN, user.isFileVisibility() ? 1 : 0);
//				statement.setString(FIFTEEN, user.getTimeZone());
//				// statement.setInt(SIXTEEN,user.isAddPartnersAccess()?1:0);
//				statement.setString(SIXTEEN, user.getSecondaryEmail());
//
////                statement.setString(EIGHTEEN, user.getPartnerId());
////                statement.setString(NINETEEN, user.getPartnerName());
//				final int isUserCreated = statement.executeUpdate();
//
//				if (isUserCreated >= 1) {
//					String query = "SELECT id FROM m_user where loginid='" + generatedUserLoginId + "'";
//					List<Map<String, Object>> response = jdbcTemplate.queryForList(query);
//					if (!response.isEmpty() && response.size() > 0) {
//						user.setUserId(Long.parseLong(response.get(0).get("id").toString()));
//						String responseForUserFlow = assignRegisteredUserFlows(user);
//						if (responseForUserFlow.equals("Partner updated succesfully.")) {
//							String createPartnersResponse = createPartnersForUser(user);
//							String sfgPartnersResponse = createSfgPartners(user);
//							if (createPartnersResponse.equals("partners added successfully")
//									&& sfgPartnersResponse.equals("partners added successfully")) {
//								responseString = "Registartion successfull.You will get an email with user/password once admin approves";
//								mailManager.sendRegistrationSuccessToUser(email, generatedUserLoginId,
//										user.getFirstName() + " " + user.getLastName());
//							} else {
//								responseString = "User created but failed to add partners.";
//							}
//						} else {
//							responseString = "User Not created.";
//						}
//					} else {
//						responseString = "User Not created.";
//					}
//
//				} else {
//					responseString = "User Not created.";
//				}
//			} else {
//				responseString = "User already registered with this email Id!";
//
//			}
//		} catch (Exception exception) {
//			logger.log(Level.ERROR, " addUser :: " + exception.getMessage());
//		} finally {
//			if (nonNull(statement)) {
//				statement.close();
//				statement = null;
//			}
//			if (nonNull(connection)) {
//				connection.close();
//				connection = null;
//			}
//		}
//		return responseString;
//	}

	public String assignRegisteredUserFlows(final User user) {
		String responseString = null;
		String response1 = null;
		int insertquery = 0;
		int updatedRows = 0;

		try {
			final String defaultTimeZone = dataSourceDataProvider.getTimeZone();
			final long userId = user.getUserId();
			jdbcTemplate.update("DELETE FROM m_user_flows_action WHERE user_id = ?", userId);
			responseString = (jdbcTemplate
					.update("INSERT INTO m_user_flows_action (priority, flowid, user_id) VALUES (1, 2, ?)", userId)) > 0
							? "Successfully assigned flows."
							: "Failed to assign flows!";
			insertquery = jdbcTemplate.update("UPDATE m_user_roles "
					+ "SET role_id = ?, date_activated = CONVERT_TZ(CURRENT_TIMESTAMP, @@session.time_zone, ?), activated_by = ? "
					+ "WHERE user_id = ?", user.getRoleId(), defaultTimeZone, user.getCreatedBy(), userId);

			response1 = insertquery > 0 ? "Updated successfully." : "Something went wrong!";

			if (responseString.equalsIgnoreCase("Successfully assigned flows.")) {
				updatedRows = jdbcTemplate.update(
						"UPDATE m_user SET active = ?,  partner_id = ?,  partner_name = ?,  webforms = ?,  tpm = ?,  mscvp = ?, user_type = ? WHERE id = ?",
						new Object[] { user.getActive(), user.getPartnerId(), user.getPartnerName(), user.getWebForms(),
								user.getTpm(), user.getMscvp(), user.getUserType(), userId });
				responseString = updatedRows > 0 ? "Partner updated succesfully." : "Something went wrong !";
			}

			logger.info("User Flow Assignment Result: {}", response1);

			String query = "SELECT id,passwd,fnme,lnme,loginid,email,secondary_email FROM m_user where id=" + userId;
			List<Map<String, Object>> response = jdbcTemplate.queryForList(query);
			if (!response.isEmpty() && response.size() > 0) {
				user.setUserId(Long.parseLong(response.get(0).get("id").toString()));
				user.setPassword(response.get(0).get("passwd").toString());
				user.setFirstName(response.get(0).get("fnme").toString());
				user.setLastName(response.get(0).get("lnme").toString());
				user.setLoginId(response.get(0).get("loginid").toString());
				user.setEmail(response.get(0).get("email").toString());
				// user.setSecondaryEmail(response.get(0).get("email").toString());
				if (response.get(0).get("email") != null) {
					user.setSecondaryEmail(response.get(0).get("email").toString());
				}
				mailManager.sendUserLoginIdAndPassword(user.getEmail(), user.getLoginId(),
						user.getFirstName() + " " + user.getLastName(),
						PasswordUtil.decryptPassword(user.getPassword()), user.getPartnerId(), user.getPartnerName());

			}

		} catch (Exception exception) {
			logger.log(Level.ERROR, " assignUserFlows :: " + exception.getMessage());
		}

		return responseString;
	}

	public String validateAndRegister(String email) {

		Long count = emailExists(email);
		if (count != null && count > 0) {
			return "You have already registered with this email ID.";
		}

		return "success";
	}

	private Long emailExists(String email) {
		String emailCheckSql = "SELECT COUNT(*) FROM m_user WHERE email = :email";

		Map<String, Object> params = new HashMap<>();
		params.put("email", email);

		return namedParameterJdbcTemplate.queryForObject(emailCheckSql, params, Long.class);
	}
}