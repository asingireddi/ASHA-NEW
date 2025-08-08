package com.miraclesoft.scvp.service.impl;

import static com.miraclesoft.scvp.util.FileUtility.getInputStreamResource;

import java.io.File;

import java.io.IOException;
import java.sql.ResultSet;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ResponseEntity;
import org.springframework.jdbc.BadSqlGrammarException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Service;

import com.miraclesoft.scvp.model.Delivery;
import com.miraclesoft.scvp.model.TradingPartner;
import com.miraclesoft.scvp.reports.Report;
import com.miraclesoft.scvp.util.DataSourceDataProvider;

@Service
public class TradingPartnerServiceImpl {
	@Autowired
	private Report report;
	@Autowired
	private JdbcTemplate jdbcTemplate;

	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;

	@Autowired
	private NamedParameterJdbcTemplate namedParameterJdbcTemplate;

	public List<String> getDirections() {
		String sql = "SELECT DISTINCT TC_DIRECTION FROM TBXE75";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	public List<String> getTrasactionCode() {
		String sql = "SELECT DISTINCT TC_TransactionCode FROM TBXE75 WHERE TC_TransactionCode IS NOT NULL AND TC_TransactionCode <> '' ";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	public List<String> getPartnerName() {
		String sql = "SELECT DISTINCT TC_PartnerName FROM TBXE75";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	public List<String> getStream() {
		String sql = "SELECT DISTINCT TC_Stream FROM TBXE75";
		return jdbcTemplate.queryForList(sql, String.class);
	}

	public List<String> getAllColumnNames() {
		String sql = "SELECT * FROM TBXE75 LIMIT 1";
		return jdbcTemplate.query(sql, (ResultSet rs) -> {
			List<String> columnNames = new ArrayList<>();
			if (rs.next()) {
				int columnCount = rs.getMetaData().getColumnCount();
				for (int i = 1; i <= columnCount; i++) {
					columnNames.add(rs.getMetaData().getColumnName(i));
				}
			}
			return columnNames;
		});
	}

	public Map<String, Object> search(TradingPartner tradingPartner) {
		Map<String, Object> response = new HashMap<String, Object>();
		String selectedColumns = String.join(", ", tradingPartner.getSelectedColumns());
		System.out.println("selectedColumns" + selectedColumns);
		final StringBuilder queryBuilder = new StringBuilder();
		queryBuilder.append("SELECT distinct ");
		queryBuilder.append(selectedColumns.isEmpty() ? "*" : selectedColumns);
		queryBuilder.append(" FROM TBXE75 WHERE 1=1 ");

		Map<String, Object> params = new HashMap<>();
		if (tradingPartner.getTcDirection() != null && !tradingPartner.getTcDirection().isEmpty()) {
			queryBuilder.append(" AND TC_DIRECTION = :direction");
			params.put("direction", tradingPartner.getTcDirection());
		}
		if (tradingPartner.getTcStream() != null && !tradingPartner.getTcStream().isEmpty()) {
			queryBuilder.append(" AND TC_Stream = :stream");
			params.put("stream", tradingPartner.getTcStream());
		}
		if (tradingPartner.getTcTransactionCode() != null && !tradingPartner.getTcTransactionCode().isEmpty()) {
			queryBuilder.append(" AND TC_TransactionCode = :transactionCode");
			params.put("transactionCode", tradingPartner.getTcTransactionCode());
		}
		if (tradingPartner.getTcPartnerName() != null && !tradingPartner.getTcPartnerName().isEmpty()) {
			queryBuilder.append(" AND TC_PartnerName = :partnerName");
			params.put("partnerName", tradingPartner.getTcPartnerName());

		}
		if (tradingPartner.getTcSapId() != null && !tradingPartner.getTcSapId().isEmpty()) {
		    queryBuilder.append(" AND TC_SAP_ID = :tcSapId");
		    params.put("tcSapId", tradingPartner.getTcSapId());
		}
		int totalCount = getTotalCount(tradingPartner, selectedColumns);

		Integer limit = tradingPartner.getLimit() != null ? tradingPartner.getLimit() : 10;

		Integer offset = tradingPartner.getOffset() != null ? tradingPartner.getOffset() : 0;

		queryBuilder.append(dataSourceDataProvider.getPartnerSortingPaginationQuery(tradingPartner.getSortField(),
				tradingPartner.getSortOrder(), limit, offset));
		System.out.println("queryBuilder" + queryBuilder);
		List<Map<String, Object>> result = namedParameterJdbcTemplate.queryForList(queryBuilder.toString(), params);

		response.put("data", result);
		response.put("totalRecordsCount", totalCount);
		
		System.out.println("queryBuilder" + queryBuilder);
		return response;
	}

	public int getTotalCount(TradingPartner tradingPartner, String selectedColumns) {

		final StringBuilder queryBuilder = new StringBuilder();
		int lengthColumn = selectedColumns.length();
		if (lengthColumn > 0) {
			// queryBuilder.append("SELECT COUNT(distinct flowtype) FROM TBXE75 WHERE 1=1
			// ");
			queryBuilder.append("SELECT COUNT(distinct ");
			queryBuilder.append(selectedColumns.isEmpty() ? "*" : selectedColumns);
			queryBuilder.append(" ) FROM TBXE75 WHERE 1=1 ");
		} else {
			queryBuilder.append("SELECT COUNT(*) FROM TBXE75 WHERE 1=1 ");

		}
		// Map to hold named parameters
		Map<String, Object> params = new HashMap<>();
		if (tradingPartner.getTcDirection() != null && !tradingPartner.getTcDirection().isEmpty()) {
			queryBuilder.append(" AND TC_DIRECTION = :direction");
			params.put("direction", tradingPartner.getTcDirection());
		}
		if (tradingPartner.getTcStream() != null && !tradingPartner.getTcStream().isEmpty()) {
			queryBuilder.append(" AND TC_Stream = :stream");
			params.put("stream", tradingPartner.getTcStream());
		}
		if (tradingPartner.getTcTransactionCode() != null && !tradingPartner.getTcTransactionCode().isEmpty()) {
			queryBuilder.append(" AND TC_TransactionCode = :transactionCode");
			params.put("transactionCode", tradingPartner.getTcTransactionCode());
		}
		if (tradingPartner.getTcPartnerName() != null && !tradingPartner.getTcPartnerName().isEmpty()) {
			queryBuilder.append(" AND TC_PartnerName = :partnerName");
			params.put("partnerName", tradingPartner.getTcPartnerName());
		}
		if (tradingPartner.getTcSapId() != null && !tradingPartner.getTcSapId().isEmpty()) {
		    queryBuilder.append(" AND TC_SAP_ID = :tcSapId");
		    params.put("tcSapId", tradingPartner.getTcSapId());
		}
		System.out.println("queryBuilder"+queryBuilder);
		return namedParameterJdbcTemplate.queryForObject(queryBuilder.toString(), params, Integer.class);
	}

	public List<Map<String, Object>> executeQuery1(String sqlQuery) {

		sqlQuery = sqlQuery.trim();
		if (!sqlQuery.toLowerCase().startsWith("select")) {
			throw new IllegalArgumentException("Only SELECT queries are allowed.");
		}

		try {
			return jdbcTemplate.queryForList(sqlQuery);
		} catch (BadSqlGrammarException e) {
			throw new BadSqlGrammarException("SQL Syntax Error", sqlQuery, e.getSQLException());
		}
	}

	public String updatePartnerDetails(TradingPartner tradingPartner) {
		String responseString = "";

		try {

			int updateCount = jdbcTemplate.update(
					"UPDATE tbxe75 SET TC_DIRECTION = ?, TC_COMPANY_CODE = ?, TC_DIVISION = ?, TC_PURCH_ORG = ?, "
							+ "TC_SAP_ID_QUALIF = ?, TC_SAP_ID = ?, TC_IDOC_TYPE = ?, TC_MESSAGE_CODE = ?, TC_MESSAGE_PORT = ?, "
							+ "TC_MESSAGE_FUNCTION = ?, TC_MESSAGE_TYPE = ?, TC_VERSION = ?, TC_REHLKO_ISA_ID_QUALF = ?, TC_REHLKO_ISA_ID = ?, TC_REHLKO_GS = ?, "
							+ "TC_PARTNER_ISA_ID_QUALF = ?, TC_PARTNER_ISA_ID = ?, TC_PARTNER_GS = ?, TC_GS_FunctionalCode = ?, "
							+ "TC_MapName = ?, TC_PartnerName = ?, TC_ListVersion = ?, TC_Stream = ?, TC_DivisionName = ?, FlowType = ?, "
							+ "TC_TransactionCode = ?, TC_DeliveryType = ?, Active = ?, GenericRID = ?, GenericSID = ?, Test_REHLKO_ID = ?, WHERE ID = ?",
					new Object[] { tradingPartner.getTcDirection(), tradingPartner.getTcCompanyCode(),
							tradingPartner.getTcDivision(), tradingPartner.getTcPurchOrg(),
							tradingPartner.getTcSapIdQualif(), tradingPartner.getTcSapId(),
							tradingPartner.getTcIdocType(), tradingPartner.getTcMessageCode(),
							tradingPartner.getTcMessagePort(), tradingPartner.getTcMessageFunction(),
							tradingPartner.getTcMessageType(), tradingPartner.getTcVersion(),
							tradingPartner.getTcKohlerIsaIdQualf(), tradingPartner.getTcKohlerIsaId(),
							tradingPartner.getTcKohlerGs(), tradingPartner.getTcPartnerIsaIdQualf(),
							tradingPartner.getTcPartnerIsaId(), tradingPartner.getTcPartnerGs(),
							tradingPartner.getTcGsFunctionalCode(), tradingPartner.getTcMapName(),
							tradingPartner.getTcPartnerName(), tradingPartner.getTcListVersion(),
							tradingPartner.getTcStream(), tradingPartner.getTcDivisionName(),
							tradingPartner.getFlowType(), tradingPartner.getTcTransactionCode(),
							tradingPartner.getTcDeliveryType(), tradingPartner.getId() });

			System.out.println("Update Count: " + updateCount);

			if (updateCount > 0) {
				responseString = "Trading partner updated successfully.";
			} else {
				responseString = "No rows were updated.";
			}
		}

		catch (Exception e) {
			e.printStackTrace();
			responseString = "Error while updating trading partner details.";
		}
		return responseString;
	}
 
	@SuppressWarnings("unchecked")
	public ResponseEntity<InputStreamResource> download(final TradingPartner tradingPartner) throws IOException {
		// Perform the search operation to get data
		Map<String, Object> searchResult = search(tradingPartner);
		List<Map<String, Object>> data = (List<Map<String, Object>>) searchResult.get("data");

		// Generate the Excel file
		String filePath = report.tradingPartnerExcelDownload(data);

		// Return the file as a response
		return getInputStreamResource(new File(filePath));
	}
	public List<String> getTcSapId() {
		String sql = "SELECT DISTINCT TC_SAP_ID FROM TBXE75";
		return jdbcTemplate.queryForList(sql, String.class);
	}
     
        public List<String> getDelivery() {
		String sql = "SELECT DISTINCT TC_DeliveryType FROM TBXE75_DELIVERY";
		return jdbcTemplate.queryForList(sql, String.class);
	}

        public Map<String, Object> getFilteredData(Delivery delivery) {
            StringBuilder baseQuery = new StringBuilder("SELECT * FROM TBXE75_DELIVERY");
            StringBuilder countQuery = new StringBuilder("SELECT COUNT(*) FROM TBXE75_DELIVERY");

            List<Object> params = new ArrayList<>();
            List<Object> countParams = new ArrayList<>();
            List<String> conditions = new ArrayList<>();

            // Filtering
            if (delivery.getTcDeliveryType() != null && !delivery.getTcDeliveryType().isEmpty()) {
                conditions.add("TC_DeliveryType = ?");
                params.add(delivery.getTcDeliveryType());
                countParams.add(delivery.getTcDeliveryType());
            }

            // Add WHERE clause if conditions exist
            if (!conditions.isEmpty()) {
                String whereClause = " WHERE " + String.join(" AND ", conditions);
                baseQuery.append(whereClause);
                countQuery.append(whereClause);
            }

            // Pagination: limit and offset
            int limit = delivery.getLimit() != null ? delivery.getLimit() : 10;
            int offset = delivery.getOffset() != null ? delivery.getOffset() : 0;

            baseQuery.append(" LIMIT ? OFFSET ?");
            params.add(limit);
            params.add(offset);

            // Run queries
            List<Map<String, Object>> data = jdbcTemplate.queryForList(baseQuery.toString(), params.toArray());
            int totalCount = jdbcTemplate.queryForObject(countQuery.toString(), countParams.toArray(), Integer.class);

            // Build response
            Map<String, Object> response = new HashMap<>();
            response.put("total", totalCount);
            response.put("limit", limit);
            response.put("offset", offset);
            response.put("data", data);

            return response;
        }

        public List<Map<String, Object>> getIdAndName() {
            String sql = "SELECT  Distinct TC_PARTNER_ISA_ID,  TC_PartnerName FROM TBXE75 where TC_Stream='WebSupplier'";
            return jdbcTemplate.queryForList(sql);
        }

	}

	

	
	

