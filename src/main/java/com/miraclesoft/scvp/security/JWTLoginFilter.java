package com.miraclesoft.scvp.security;

import java.io.IOException;
import java.util.Collections;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.AbstractAuthenticationProcessingFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import com.fasterxml.jackson.databind.ObjectMapper;

/**
 * The Class JWTLoginFilter.
 *
 * @author Narendar Geesidi
 */
public class JWTLoginFilter extends AbstractAuthenticationProcessingFilter {

	/**
	 * Instantiates a new JWT login filter.
	 *
	 * @param url                   the url
	 * @param authenticationManager the authentication manager
	 */
	public JWTLoginFilter(final String url, final AuthenticationManager authenticationManager) {
		super(new AntPathRequestMatcher(url));
		setAuthenticationManager(authenticationManager);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Authentication attemptAuthentication(final HttpServletRequest request, final HttpServletResponse response)
			throws AuthenticationException, IOException, ServletException {
		final CredentialsBean credentials = new ObjectMapper().readValue(request.getInputStream(),
				CredentialsBean.class);
		return getAuthenticationManager().authenticate(new UsernamePasswordAuthenticationToken(credentials.getLoginId(),
				credentials.getPassword(), Collections.emptyList()));
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void successfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
			final FilterChain filterChain, final Authentication authentication) throws IOException, ServletException {
		TokenAuthenticationService.addAuthentication(response, authentication.getName());
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void unsuccessfulAuthentication(final HttpServletRequest request, final HttpServletResponse response,
			final AuthenticationException failed) throws IOException, ServletException {
		TokenAuthenticationService.authenticationFailure(response, "Invalid username or password");
	}
}
