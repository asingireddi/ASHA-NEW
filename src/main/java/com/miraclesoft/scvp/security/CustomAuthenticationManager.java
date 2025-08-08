package com.miraclesoft.scvp.security;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.AuthenticationServiceException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.miraclesoft.scvp.util.DataSourceDataProvider;
import com.miraclesoft.scvp.util.PasswordUtil;
import com.miraclesoft.scvp.util.RsaKeyUtil;

/**
 * The Class CustomAuthenticationManager.
 *
 * @author Narendar Geesidi
 */
@Configuration
public class CustomAuthenticationManager implements AuthenticationProvider {

	/** The jdbc template. */
	@Autowired
	private JdbcTemplate jdbcTemplate;
	
	@Autowired
	private RsaKeyUtil rsaKeyUtil;

	@Autowired
	private DataSourceDataProvider dataSourceDataProvider;

	/** The logger. */
	private static Logger logger = LogManager.getLogger(CustomAuthenticationManager.class.getName());

	/**
	 * {@inheritDoc}
	 *
	 */
	@Override
    public Authentication authenticate(final Authentication authentication)  throws AuthenticationException{
        logger.info("Authentication {}",authentication);
        String decryptedUsername = null;
        String decryptedPassword = null;
        try {
            decryptedUsername = rsaKeyUtil.decryptUsername(authentication.getName());
            decryptedPassword = rsaKeyUtil.decryptPassword(authentication.getCredentials().toString());
        }catch (Exception e) {
            throw new AuthenticationServiceException("Decryption failed for username or password", e);
		}
        final String userName = authentication.getName();
        final String password = authentication.getCredentials().toString();
        logger.info("Username {} Passcode {}",decryptedUsername,decryptedPassword);
        final String authenticationQuery = "SELECT id,loginid,passwd,file_visibility,timezone FROM m_user WHERE BINARY loginid = ? and passwd = ?";
        logger.log(Level.INFO, " authenticate query :: " + authenticationQuery);
        final List<Map<String, Object>> rows = jdbcTemplate.queryForList(authenticationQuery, decryptedUsername, PasswordUtil.encryptPassword(decryptedPassword));
        // String sfgUsersPartners = "";
        String usersPartners = "";
        String timezone = "";
        String userZone = "";
        try {
            usersPartners = dataSourceDataProvider.getUsersPartners((Long) rows.get(0).get("id")).toString();
            timezone = rows.get(0).get("timezone").toString();
            userZone = dataSourceDataProvider.getOffSetFromToken(timezone);
        } catch (Exception e1) {
            e1.printStackTrace();
        }
        if (!rows.isEmpty()) {
            ObjectMapper objectMapper = new ObjectMapper();
            Map<String, String> details = new HashMap<>();
            details.put("username", userName);
            details.put("id", rows.get(0).get("id").toString());
            details.put("isAllPartnersVisible", usersPartners);
            details.put("timeZone", userZone);
            String json = null;
            try {
                json = objectMapper.writeValueAsString(details);
            } catch (JsonProcessingException e) {
                e.printStackTrace();
            }
            return new UsernamePasswordAuthenticationToken(json, password, Collections.emptyList());
        } else {
            throw new BadCredentialsException("Invalid username or password!!!");
        }
    }

	/**
	 * {@inheritDoc}
	 */
	@Override
	public boolean supports(final Class<?> authentication) {
		return authentication.equals(UsernamePasswordAuthenticationToken.class);
	}

}
