package com.miraclesoft.scvp.security;

import static java.util.Collections.emptyList;
import static java.util.Objects.nonNull;

import java.util.Base64;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

/**
 * The Class TokenAuthenticationService.
 *
 * @author Narendar Geesidi
 */
@Service
public class TokenAuthenticationService {

	/**
	 * The Constant EXPIRATIONTIME.
	 */
	static final long EXPIRATIONTIME = 28800000; // 8 hours in ms

	/**
	 * The Constant SECRET.
	 */
//    static final String SECRET = "Miracle";

	/**
	 * The Constant TOKEN_PREFIX.
	 */
	static final String TOKEN_PREFIX = "Bearer";

	/**
	 * The Constant HEADER_STRING.
	 */
	static final String HEADER_STRING = "Authorization";

	/**
	 * The logger.
	 */
	private static Logger logger = LogManager.getLogger(TokenAuthenticationService.class.getName());

	@Autowired
	private HttpServletRequest request;

	public static String secret = Base64.getEncoder().encodeToString(
			"9d4a8b45f2c34a1dbe6c472fa8d670fae2f5ce789f83c1e0211e648fd930ba27455d71f9b5677d04f132bb2dfe8cb4b9ae83d87fce4d14f302fc662fab640f7a"
					.getBytes());
	static byte[] keyBytes = Base64.getDecoder().decode(secret);
	static SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA512");

// Use this key for both signing and validation

	/**
	 * Adds the authentication.
	 *
	 * @param response the response
	 * @param userName the user name
	 */
	static void addAuthentication(final HttpServletResponse response, final String userName) {
		Map<String, Object> claims = new HashMap<>();
		final String jwt = generateToken(claims, userName);
		response.addHeader("Access-Control-Expose-Headers", HEADER_STRING);
		response.addHeader(HEADER_STRING, TOKEN_PREFIX + " " + jwt);
	}

	/**
	 * generates jwt token.
	 *
	 * @param claimss  the claims
	 * @param userName the user name
	 */
	public static String generateToken(Map<String, Object> claimss, String userName) {

		Map<String, Object> claims = null;
		try {
			ObjectMapper objectMapper = new ObjectMapper();
			claims = objectMapper.readValue(userName, Map.class);

		} catch (Exception e) {
			e.printStackTrace();
		}
		return Jwts.builder().setClaims(claims).setSubject(userName).setIssuedAt(new Date(System.currentTimeMillis()))
				.setExpiration(new Date(System.currentTimeMillis() + EXPIRATIONTIME))
				.signWith(SignatureAlgorithm.HS512, key).compact();
	}

	/**
	 * Gets the authentication.
	 *
	 * @param request the request
	 * @return the authentication
	 */
	static Authentication getAuthentication(final HttpServletRequest request) {
		final String token = request.getHeader(HEADER_STRING);
		if (nonNull(token)) {
			final String user = Jwts.parser().setSigningKey(key).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
					.getBody().getSubject();
			return nonNull(user) ? new UsernamePasswordAuthenticationToken(user, null, emptyList()) : null;
		}
		return null;
	}

	/**
	 * Gets the isTokenExpired.
	 *
	 * @param token the token
	 * @return the Boolean
	 */
	public static Boolean isTokenExpired(final String token) {
		final Date expiration = getExpirationDateFromToken(token);
		return expiration.before(new Date());
	}

	/**
	 * Gets the getExpirationDateFromToken.
	 *
	 * @param token the token
	 * @return the date
	 */
	public static Date getExpirationDateFromToken(final String token) {
		final Claims claims = getAllClaimsFromToken(token);
		if (claims == null) {
			return new Date(System.currentTimeMillis() - EXPIRATIONTIME);
		} else {
			return getClaimFromToken(token, Claims::getExpiration);
		}
	}

	/**
	 * Gets the getClaimFromToken.
	 *
	 * @param token          the token
	 * @param claimsResolver the claimsResolver
	 * @param <T>            the dynamic
	 * @return the T
	 */
	public static <T> T getClaimFromToken(final String token, final Function<Claims, T> claimsResolver) {
		final Claims claims = getAllClaimsFromToken(token);
		return claimsResolver.apply(claims);
	}

	/**
	 * Gets the getAllClaimsFromToken.
	 *
	 * @param token the token
	 * @return the claims
	 */
	private static Claims getAllClaimsFromToken(final String token) {
		Claims claims;
		try {
			claims = Jwts.parser().setSigningKey(key).parseClaimsJws(token).getBody();
		} catch (Exception e) {
			claims = null;
		}
		return claims;
	}

	/**
	 * Gets the authenticationFailure.
	 *
	 * @param response and username
	 * @return the authentication
	 */
	static void authenticationFailure(final HttpServletResponse response, final String userName) {
		response.addHeader("Access-Control-Expose-Headers", HEADER_STRING);
		response.addHeader(HEADER_STRING, "");
	}

	/**
	 * Gets the token from user.
	 *
	 * @return the String
	 */
	public String getUserIdfromToken() {
		String token = request.getHeader(HEADER_STRING);
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (nonNull(token)) {
				final String user = Jwts.parser().setSigningKey(key).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
						.getBody().getSubject();
				Map<String, String> map = mapper.readValue(user, Map.class);
				return map.get("id");
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " getUserIdfromToken :: " + exception.getMessage());
		}
		return "0";
	}

	public String getTimeZonefromToken() {
		String token = request.getHeader(HEADER_STRING);
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (nonNull(token)) {
				final String user = Jwts.parser().setSigningKey(key).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
						.getBody().getSubject();
				Map<String, String> map = mapper.readValue(user, Map.class);
				return map.get("timeZone");
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " getTimeZonefromToken :: " + exception.getMessage());
		}
		return "0";
	}

	public String getUserNamefromToken() {
		String token = request.getHeader(HEADER_STRING);
		ObjectMapper mapper = new ObjectMapper();
		try {
			if (nonNull(token)) {
				final String user = Jwts.parser().setSigningKey(key).parseClaimsJws(token.replace(TOKEN_PREFIX, ""))
						.getBody().getSubject();
				Map<String, String> map = mapper.readValue(user, Map.class);
				return map.get("username");
			}
		} catch (Exception exception) {
			logger.log(Level.ERROR, " getUserNamefromToken :: " + exception.getMessage());
		}
		return "0";
	}

}
