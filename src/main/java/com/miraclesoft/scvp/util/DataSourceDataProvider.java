package com.miraclesoft.scvp.util;

import static java.util.Objects.nonNull;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import static com.miraclesoft.scvp.util.ColumnsMapping.SqlColumnsMapping;


import com.miraclesoft.scvp.security.TokenAuthenticationService;

/**
 * The Class DataSourceDataProvider.
 *
 * @author Narendar Geesidi
 */
@Component
public class DataSourceDataProvider {
    /** The jdbc template. */
    @Autowired
    private JdbcTemplate jdbcTemplate;
    
    @Autowired
	private TokenAuthenticationService tokenAuthenticationService;
    
    /**
     * Gets the role name by role id.
     *
     * @param roleId the role id
     * @return the role name by role id
     * @throws Exception the exception
     */
    public String getRoleNameByRoleId(final int roleId) throws Exception {
        final String roleName = jdbcTemplate.queryForObject("SELECT role_name FROM mscvp_roles WHERE id = ?",
                new Object[] { roleId }, String.class);
        return nonNull(roleName) ? roleName : "";
    }

    /**
     * Gets the flow name by flow id.
     *
     * @param flowId the flow id
     * @return the flow name by flow id
     * @throws Exception the exception
     */
    public String getFlowNameByFlowId(final int flowId) throws Exception {
        final String flowName = jdbcTemplate.queryForObject("SELECT flowname FROM mscvp_flows WHERE id = ?",
                new Object[] { flowId }, String.class);
        return nonNull(flowName) ? flowName : "";
    }

    /**
     * Gets the primary flow ID.
     *
     * @param userId the user id
     * @return the primary flow ID
     * @throws Exception the exception
     */
    public int getPrimaryFlowID(final Long userId) throws Exception {
        int flowId = 0;
        if (isAdmin(userId)) {
            flowId = 1;
        } else if (isAssignedFlowsTo(userId)) {
            final String primaryFlowIdQuery = "SELECT mf.id FROM mscvp_flows mf JOIN m_user_flows_action mufa"
                    + " ON (mf.id = mufa.flowid) WHERE mufa.priority = 1 AND mufa.user_id = ?";
            flowId = jdbcTemplate.queryForObject(primaryFlowIdQuery, new Object[] { userId }, Integer.class);
        }
        return flowId;
    }

    /**
     * Gets the flows.
     *
     * @param userId the user id
     * @return the flows
     * @throws Exception the exception
     */
    public Map<Integer, String> getFlows(final Long userId) throws Exception {
        final Map<Integer, String> flowsMap = new HashMap<Integer, String>();
        final String flowsQuery = "SELECT mufa.priority, mf.flowname, mf.id FROM mscvp_flows mf"
                + " JOIN m_user_flows_action mufa ON (mf.id = mufa.flowid) WHERE user_id = ? ORDER BY priority ";
        final List<Map<String, Object>> rows = jdbcTemplate.queryForList(flowsQuery, userId);
        for (final Map<String, Object> row : rows) {
            flowsMap.put((Integer) row.get("id"), (String) row.get("flowname"));
        }
        return flowsMap;
    }

    /**
     * Gets the states.
     *
     * @return the states
     * @throws Exception the exception
     */
    public List<String> getStates() throws Exception {
        final List<String> statesList = new ArrayList<String>();
        final List<Map<String, Object>> rows = jdbcTemplate
                .queryForList("SELECT name FROM tbllkstates ORDER BY description");
        for (final Map<String, Object> row : rows) {
            statesList.add((String) row.get("name"));
        }
        return statesList;
    }

    /**
     * Gets the partner name by id.
     *
     * @param partnerId the partner id
     * @return the partner name by id
     * @throws Exception the exception
     */
    public String getPartnerNameById(final String partnerId) throws Exception {
        final String partnerName = jdbcTemplate.queryForObject("SELECT name FROM tp WHERE id = ?",
                new Object[] { partnerId }, String.class);
        return nonNull(partnerName) ? partnerName : partnerId;
    }

    /**
     * Gets the assigned flows.
     *
     * @param userId the user id
     * @return the assigned flows
     * @throws Exception the exception
     */
    public Map<Integer, String> getAssignedFlows(final Long userId) throws Exception {
        final Map<Integer, String> flowsMap = new HashMap<Integer, String>();
        String flowsQuery = "SELECT mf.id, mf.flowname FROM mscvp_flows mf JOIN m_user_flows_action mufa"
                + " ON (mf.id = mufa.flowid) WHERE user_id = " + userId + " AND mf.id != 1 ORDER BY flowid";
        final List<Map<String, Object>> rows = jdbcTemplate.queryForList(flowsQuery);
        for (final Map<String, Object> row : rows) {
            flowsMap.put((Integer) row.get("id"), (String) row.get("flowname"));
        }
        return flowsMap;
    }

    /**
     * All trading partners.
     *
     * @return the map
     * @throws Exception the exception
     */
    public Map<String, String> allTradingPartners() throws Exception {
        final Map<String, String> partnersMap = new LinkedHashMap<String, String>();
        final List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, name FROM tp t ORDER BY t.name ASC");
        for (final Map<String, Object> row : rows) {
            partnersMap.put((String) row.get("id"), (String) row.get("name"));
        }
        return partnersMap;
    }

    /**
     * All trading partners.
     *
     * @return the map
     * @throws Exception the exception
     */
    public List<Map<String, String>> allTradingPartnersListOfMap() throws Exception {
        final List<Map<String,String>> responseList = new ArrayList<>();
        Map<String, String> partnersMap = null;
        final List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT id, name FROM tp t ORDER BY t.name ASC");
        for (final Map<String, Object> row : rows) {
            partnersMap = new HashMap<>();
            partnersMap.put("value", (String) row.get("id"));
            partnersMap.put("label", (String) row.get("name"));
            responseList.add(partnersMap);
        }
        return responseList;
    }
    
    /**
     * All trading partners.
     *
     * @param userId the userId
     * @return the map
     * @throws Exception the exception
     */
    public List<Map<String, String>> userTradingPartners(final int userId) throws Exception {
        final List<Map<String,String>> responseList = new ArrayList<>();
        Map<String, String> partnersMap = null;
        String joinQuery = partnersVisibilityWithTpJoinCondition().toString() + " where pv.user_id = ?";
        String query = "SELECT t.id, name FROM tp t " + joinQuery + " ORDER BY t.name ASC";
        final List<Map<String, Object>> rows = jdbcTemplate
                .queryForList(query, userId);
        for (final Map<String, Object> row : rows) {
            partnersMap = new HashMap<>();
            partnersMap.put("value", (String) row.get("id"));
            partnersMap.put("label", (String) row.get("name"));
            responseList.add(partnersMap);
        }
        return responseList;
    }

    /**
     * All trading partners.
     *
     * @return the map
     * @throws Exception the exception
     */
    public List<String> allTradingPartnersList1() throws Exception {
        final List<String> rows = jdbcTemplate.queryForList("SELECT id FROM tp ORDER BY name ASC", String.class);
        return rows;
    }
    public List<String> allTradingPartnersList() throws Exception {
        final List<String> rows = jdbcTemplate.queryForList("SELECT ID FROM tbxe75 ORDER BY TC_PartnerName ASC", String.class);
        return rows;
    }
    /**
     * All trading partners.
     *
     * @return the map
     * @throws Exception the exception
     */
    public List<String> allSfgTradingPartnersList() throws Exception {
        final List<String> rows = jdbcTemplate.queryForList("SELECT name FROM sfg_partner ORDER BY name ASC", String.class);
        return rows;
    }

    /**
     * Checks if is admin.
     *
     * @param userId the user id
     * @return true, if is admin
     * @throws Exception the exception
     */
    public boolean isAdmin(final Long userId) throws Exception {
        return jdbcTemplate.queryForObject("SELECT count(*) from m_user_flows_action WHERE flowid = 1 AND user_id=?",
                new Object[] { userId }, Integer.class) > 0 ? true : false;
    }

    /**
     * Checks if is assigned flows to.
     *
     * @param userId the user id
     * @return true, if is assigned flows to
     * @throws Exception the exception
     */
    public boolean isAssignedFlowsTo(final Long userId) throws Exception {
        return jdbcTemplate.queryForObject(
                "SELECT count(*) from m_user_flows_action WHERE priority = 1 AND user_id = ?", new Object[] { userId },
                Integer.class) > 0 ? true : false;
    }

    /**
     * Gets the user partners.
     *
     * @param userId the user id
     * @return the rows
     * @throws Exception the exception
     */
    public List<String> getUsersPartners(final Long userId) throws Exception {
        List<String> rows = new ArrayList<>();
        final String flowsQuery = "SELECT partner_id FROM partner_visibilty where user_id = ?";
        rows = jdbcTemplate.queryForList(flowsQuery, new Object[]{userId}, String.class);
        return rows;
    }
    
    /**
     * Gets the user partners.
     *
     * @param userId the user id
     * @return the rows
     * @throws Exception the exception
     */
    public List<String> getSfgUsersPartners(final Long userId) throws Exception {
        List<String> rows = new ArrayList<>();
        final String flowsQuery = "SELECT sfg_partner_name FROM sfg_partner_visibilty where user_id = ?";
        rows = jdbcTemplate.queryForList(flowsQuery, new Object[]{userId}, String.class);
        return rows;
    }

    /**
     * All trading partners count.
     *
     * @return the map
     * @throws Exception the exception
     */
    public int tradingPartnersCount() throws Exception {
        int numbersOfpartners = jdbcTemplate.queryForObject("SELECT COUNT(id) as noOfPartners FROM tp", Integer.class);
        return numbersOfpartners;
    }

    /**
     * the join condition.
     *
     * @param senderId   the senderId
     * @param receiverId the receiverId
     * @param userId     the userId
     * @param flag       the flag
     * @return the String
     * @throws Exception the exception
     */
    public StringBuilder partnersJoinCondition(final String senderId, final String receiverId, final int userId,
            final boolean flag) throws Exception {
        final StringBuilder joinQuery = new StringBuilder();
        boolean flag2 = false;
        if (nonNull(senderId)  && !"ALL".equals(senderId) && nonNull(receiverId)
                && "-1".equals(senderId) && "-1".equals(receiverId)
                && !"ALL".equals(receiverId)) {
            joinQuery
                    .append(" join partner_visibilty pv on (pv.partner_id=f.sender_id or pv.partner_id=f.receiver_id)");
            flag2 = true;
        }
        else if (nonNull(senderId)  && !"ALL".equals(senderId) && nonNull(receiverId)
                && !"-1".equals(senderId) && !"-1".equals(receiverId)
                && !"ALL".equals(receiverId)) {
            joinQuery
                    .append(" join partner_visibilty pv on (pv.partner_id=f.sender_id or pv.partner_id=f.receiver_id)");
            flag2 = true;
        }
        else if (nonNull(senderId) && !"".equals(senderId) && !"ALL".equals(senderId) && !"-1".equals(senderId)) {
            joinQuery.append(" join partner_visibilty pv on (pv.partner_id=f.sender_id)");
            flag2 = true;
        } else if (nonNull(receiverId) && !"".equals(receiverId) && !"ALL".equals(receiverId)
                && !"-1".equals(receiverId)) {
            joinQuery.append(" join partner_visibilty pv on (pv.partner_id=f.receiver_id)");
            flag2 = true;
        }
        if (flag) {
            joinQuery.append(" WHERE flowflag = 'M' ");
        }
        if (userId != 0 && flag2) {
            joinQuery.append(" AND pv.user_id=" + userId);
        }
        return joinQuery;
    }

    /**
     * the join condition.
     *
     * @return the String
     * @throws Exception the exception
     */
    public StringBuilder partnersJoinCondition() throws Exception {
        final StringBuilder joinQuery = new StringBuilder();
        joinQuery.append(" join partner_visibilty pv on (pv.partner_id=f.sender_id or pv.partner_id=f.receiver_id)");
        return joinQuery;
    }

    /**
     * the join condition.
     *
     * @return the String
     * @throws Exception the exception
     */
    public StringBuilder partnersSenderIdJoinCondition() throws Exception {
        final StringBuilder joinQuery = new StringBuilder();
        joinQuery.append(" join partner_visibilty pv on (pv.partner_id=f.sender_id)");
        return joinQuery;
    }

    /**
     * the join condition.
     *
     * @return the String
     * @throws Exception the exception
     */
    public StringBuilder partnersReceiverIdJoinCondition() throws Exception {
        final StringBuilder joinQuery = new StringBuilder();
        joinQuery.append(" join partner_visibilty pv on ( pv.partner_id=f.receiver_id)");
        return joinQuery;
    }

    /**
     * the join condition.
     *
     * @return the String
     * @throws Exception the exception
     */
    public StringBuilder partnersVisibilityWithTpJoinCondition() throws Exception {
        final StringBuilder joinQuery = new StringBuilder();
        joinQuery.append(" LEFT join partner_visibilty pv on (pv.partner_id=t.id)");
        return joinQuery;
    }

    
    /**
     * the join condition.
     *
     * @return the String
     * @throws Exception the exception
     */
    public StringBuilder partnersVisibilityWithSfgJoinCondition() throws Exception {
        final StringBuilder joinQuery = new StringBuilder();
        joinQuery.append(" LEFT join sfg_partner_visibilty pv on (pv.sfg_partner_name=sp.name)");
        return joinQuery;
    }
   
	public String getOffSetFromToken(String timezone) {
		// TODO Auto-generated method stub
		List<String> rows = new ArrayList<>();
        final String flowsQuery = "SELECT code FROM timezones where id= ?";
        rows = jdbcTemplate.queryForList(flowsQuery, new Object[]{timezone}, String.class);
        String data = rows.toString();
        String[] parts = data.split("\\["); 
		String offset = parts[1].substring(0, parts[1].length() - 1);
		ZoneId osloZone = ZoneId.of(offset);
    	ZonedDateTime osloTime = ZonedDateTime.now(osloZone);
    	ZoneOffset utcOffset = osloTime.getOffset();
		return utcOffset.toString();
	}

	public String getTimeZone() {
		ZoneId osloZone = ZoneId.of("America/New_York");
    	ZonedDateTime osloTime = ZonedDateTime.now(osloZone);
    	ZoneOffset utcOffset = osloTime.getOffset();
		return utcOffset.toString();
		
	}

	public String getCurrentDateOfUser() {
		final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
        OffsetDateTime dateTime = OffsetDateTime.now();
        ZoneOffset offset = ZoneOffset.of(userTimeZone);
        OffsetDateTime adjustedDateTime = dateTime.withOffsetSameInstant(offset);
        String formattedDate = adjustedDateTime.format(DateTimeFormatter.ISO_LOCAL_DATE);
		return formattedDate;
		
	}
	
	public String getCurrentDateTimeOfUser() {
		final String userTimeZone = tokenAuthenticationService.getTimeZonefromToken();
        LocalDateTime now = LocalDateTime.now(ZoneOffset.of(userTimeZone));
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        String formattedDateTime = now.format(formatter);
		return formattedDateTime;
		
	}
	
	public String criteriaForSortingAndPagination(String sortField, String sortOrder, int limit, int offSet) {
		final StringBuilder sortingQuery = new StringBuilder();
		String columnName = "";
		if (sortField.equals("partnerName")) {
			sortingQuery.append(" ORDER BY CASE WHEN LOWER(f.direction)='inbound' THEN sender_id ELSE receiver_id END ")
					.append(sortOrder);
		} else if (sortField.equals("fileId") || sortField.equals("primaryKeyValue")
				|| sortField.equals("transactionType") || sortField.equals("isaControlNumber")
				|| sortField.equals("depositorOrderNumber")) {
			columnName = SqlColumnsMapping(sortField);
			sortingQuery.append(" ORDER BY CAST(f.").append(columnName).append(" AS UNSIGNED) ").append(sortOrder);
		} else {
			columnName = SqlColumnsMapping(sortField);
			sortingQuery.append(" ORDER BY ").append(columnName).append(" ").append(sortOrder);
		}
		if (limit != 0) {
			sortingQuery.append(" LIMIT ").append(limit).append(" OFFSET ").append(offSet);
		}
		return sortingQuery.toString();
	}

	public String getPartnerSortingPaginationQuery(String sortField, String sortOrder, int limit, int offSet) {
		final StringBuilder sortingQuery = new StringBuilder();
		String columnName = "";
			columnName = SqlColumnsMapping(sortField);
			sortingQuery.append(" ORDER BY ").append(columnName).append(" ").append(sortOrder);
		if (limit != 0) {
			System.out.println("limit" +limit);
			sortingQuery.append(" LIMIT ").append(limit).append(" OFFSET ").append(offSet);
		}
		return sortingQuery.toString();
	}
}