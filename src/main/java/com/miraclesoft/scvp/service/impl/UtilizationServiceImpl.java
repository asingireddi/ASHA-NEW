package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;
import static com.miraclesoft.scvp.util.SqlCondition.*;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import com.miraclesoft.scvp.model.SearchCriteria;
import com.miraclesoft.scvp.security.TokenAuthenticationService;
import com.miraclesoft.scvp.util.AwsS3Util;

/**
 * The Class UtilizationServiceImpl.
 *
 * @author Narendar Geesidi
 */
@Component
public class UtilizationServiceImpl {

	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;

	/** The aws S3 util. */
	@Autowired
	private AwsS3Util awsS3Util;

	@Autowired
	private HttpServletRequest httpServletRequest;

	@Autowired
	private TokenAuthenticationService tokenAuthenticationService;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(UtilizationServiceImpl.class.getName());

	/**
	 * Mscvp roles.
	 *
	 * @return the string
	 */
	public String mscvpRoles() {
		final JSONArray mscvpRolesJsonArray = new JSONArray();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, role_name FROM mscvp_roles");
			for (final Map<String, Object> row : rows) {
				final JSONObject obj = new JSONObject();
				obj.put("id", (Integer) row.get("id"));
				obj.put("name", (String) row.get("role_name"));
				mscvpRolesJsonArray.put(obj);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " mscvpRoles :: " + exception.getMessage());
		}
		return mscvpRolesJsonArray.toString();
	}

	/**
	 * Primary flows.
	 *
	 * @return the string
	 */
	public String primaryFlows() {
		final JSONArray primaryFlowJsonArray = new JSONArray();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate
					.queryForList("SELECT id, flowname FROM mscvp_flows WHERE id != 1");
			for (final Map<String, Object> row : rows) {
				final JSONObject obj = new JSONObject();
				obj.put("id", (Integer) row.get("id"));
				obj.put("name", (String) row.get("flowname"));
				primaryFlowJsonArray.put(obj);
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " primaryFlows :: " + exception.getMessage());
		}
		return primaryFlowJsonArray.toString();
	}

	/**
	 * Document types.
	 *
	 * @param database the database
	 * @return the list
	 */
	public List<String> documentTypes(final String database) {
		final List<String> documentTypeList = new ArrayList<>();
		try {

			final StringBuilder documentTypeListQuery = new StringBuilder();
			documentTypeListQuery.append("SELECT DISTINCT(f.transaction_type) AS transaction FROM ");
			documentTypeListQuery.append(database.equals("ARCHIVE") ? "archive_files f" : "files f ");
			documentTypeListQuery.append(" WHERE f.transaction_type IS NOT NULL ORDER BY transaction ASC");
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(documentTypeListQuery.toString());
			for (final Map<String, Object> row : rows) {
				documentTypeList.add((String) row.get("transaction"));
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " documentTypes :: " + exception.getMessage());
		}
		return documentTypeList;
	}

	/**
	 * Correlations.
	 *
	 * @param transaction the transaction
	 * @return the list
	 */
	public List<String> correlations(final String transaction) {
		final List<String> correlationNames = new ArrayList<String>();
		try {
			final List<Map<String, Object>> rows = jdbcTemplate
					.queryForList("SELECT DISTINCT(value) as name FROM correlation WHERE transaction =? ORDER BY name ", transaction);
			for (final Map<String, Object> row : rows) {
				correlationNames.add((String) row.get("name"));
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " correlations :: " + exception.getMessage());
		}
		return correlationNames;
	}

	/**
	 * Pre post file.
	 *
	 * @param filePath the file path
	 * @return the response entity
	 */
	public ResponseEntity<InputStreamResource> prePostFile(final String filePath) {
		return getInputStreamResource(new File(new String(new Base64(true).decode(filePath))));
	}

	/**
	 * Parent warehouses.
	 *
	 * @param database the database
	 * @return the list
	 */
	public List<String> parentWarehouses(final String database) {
		final List<String> parentWarehouses = new ArrayList<String>();
		try {
			final StringBuilder parentWarehousesQuery = new StringBuilder();
			parentWarehousesQuery.append("SELECT DISTINCT(f.warehouse) FROM ");
			parentWarehousesQuery.append(database.equals("ARCHIVE") ? "archive_files f" : "files f");
			parentWarehousesQuery.append(" WHERE f.warehouse IS NOT NULL ORDER BY warehouse ASC");
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(parentWarehousesQuery.toString());
			for (final Map<String, Object> row : rows) {
				parentWarehouses.add((String) row.get("warehouse"));
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " warehouses :: " + exception.getMessage());
		}
		return parentWarehouses;
	}

	/**
	 * List of warehouses under a parent warehouse.
	 *
	 * @param database        the database
	 * @param parentWarehouse the parent warehouse
	 * @return the list
	 */
	public List<String> warehousesFor(final SearchCriteria searchCriteria) {
		final List<String> warehouses = new ArrayList<String>();
		final String ParentWarehouse = listToString(searchCriteria.getParentWarehouse());
		final String database = searchCriteria.getDatabase();
		List<Object> params = new ArrayList<>();
		try {
			final StringBuilder warehousesForQuery = new StringBuilder();
			warehousesForQuery.append("SELECT DISTINCT(f.parent_warehouse) FROM ");
			warehousesForQuery.append(database.equals("ARCHIVE") ? "archive_files f WHERE " : "files f WHERE ");
			if (ParentWarehouse != "null" && !ParentWarehouse.equals("'All'")) {
				warehousesForQuery.append(inOperatorWithAnd("f.warehouse"));
				params.add(ParentWarehouse);
			}
			warehousesForQuery.append("f.parent_warehouse IS NOT NULL ORDER BY parent_warehouse ASC");
			System.out.println(warehousesForQuery.toString() );
			System.out.println(params);
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(warehousesForQuery.toString(), params.toArray());
			for (final Map<String, Object> row : rows) {
				warehouses.add((String) row.get("parent_warehouse"));
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " parentWarehouses :: " + exception.getMessage());
		}
		return warehouses;
	}

	public List<String> acknowledgementStatus() {
		List<String> response = jdbcTemplate.queryForList("SELECT DISTINCT ack_status FROM files WHERE 1=1", String.class);
		return response;
	}
	
	/**
	 * Gets the file.
	 *
	 * @param file the file
	 * @return the file
	 * @throws IOException Signals that an I/O exception has occurred.
	 */
	public ResponseEntity<byte[]> getFileFromAmazonS3(final String file) throws IOException {
		return awsS3Util.getFileFromAmazonS3(file);
	}

	public String tradingPartners(String database) {
		final JSONArray sendersListJsonArray = new JSONArray();
		final int userId = Integer.parseInt(tokenAuthenticationService.getUserIdfromToken());
		try {
			boolean all = Boolean.parseBoolean(httpServletRequest.getHeader("isAll").toString());
			final StringBuilder sendersListQuery = new StringBuilder();
			if (all) {
				sendersListQuery.append("SELECT DISTINCT(t.id), t.NAME FROM tp t ORDER BY t.name ");
			} else {
				sendersListQuery
						.append("SELECT DISTINCT t.id AS id, t.name AS name FROM ");
				sendersListQuery.append(
						"tp t JOIN partner_visibilty  pv ON (pv.partner_id= t.id) "
								+ "WHERE pv.user_id = ? ORDER BY name");
			}
			final List<Map<String, Object>> rows = jdbcTemplate.queryForList(sendersListQuery.toString(), userId);
			for (final Map<String, Object> row : rows) {
				final JSONObject obj = new JSONObject();
				obj.put("id", (String) row.get("id"));
				obj.put("name", (String) row.get("name") + "(" + (String) row.get("id") + ")");
				if (row.get("name") != null && row.get("id") != null) {
					sendersListJsonArray.put(obj);
				}
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " senders :: " + exception.getMessage());
		}
		return sendersListJsonArray.toString();
	}

	public ResponseEntity<byte[]> getFile(final String file) throws IOException {
		return awsS3Util.getFile(file);
	}
	
	public List<String> getStatus() {
		String sql = "SELECT DISTINCT status FROM files";
		return jdbcTemplate.queryForList(sql, String.class);
	}
	public List<String> getAckStatus() {
		String sql = "SELECT DISTINCT ack_status FROM m_functionalAck";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	public List<String> getTradingPartnerName() {
		String sql = "SELECT DISTINCT partnerName FROM files WHERE partnerName IS NOT NULL;";
		return jdbcTemplate.queryForList(sql, String.class);
}


	public List<Map<String, Object>> getGroupedPartners() {
		String sql = "SELECT  Distinct TC_PartnerName, TC_PARTNER_ISA_ID FROM TBXE75 where TC_Stream= 'WebSupplier'";
	    return jdbcTemplate.queryForList(sql);
	}

	public List<String> getStats() {
		String sql = "SELECT DISTINCT ack_status FROM files";
		return jdbcTemplate.queryForList(sql, String.class);
	
}

	public List<String> getdeliveredTo() {
		String sql = "SELECT DISTINCT delivered_To FROM files";
		return jdbcTemplate.queryForList(sql, String.class);
	}
	}
