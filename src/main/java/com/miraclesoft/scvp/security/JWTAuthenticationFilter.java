package com.miraclesoft.scvp.security;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.GenericFilterBean;

/**
 * The Class JWTAuthenticationFilter.
 *
 * @author Narendar Geesidi
 */
public class JWTAuthenticationFilter extends GenericFilterBean {

	/**
	 * {@inheritDoc}
	 */
	@Override

	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain filterChain)
			throws IOException, ServletException {
		String[] urls = { "/v2/api-docs", "/user/forgotPassword", "/swagger", "/webjars", "/user/register",
				"/user/registerd", "/user/forgotUserName", "/healthcheck", "/key" };
		List<String> urlsArray = Arrays.asList(urls);
		HttpServletRequest httpServeletRequest = (HttpServletRequest) request;
		String authHeader = httpServeletRequest.getHeader("Authorization");
		HttpServletResponse httpServeltresponse = (HttpServletResponse) response;
		boolean isExists = false;
		for (int i = 0; i < urls.length; i++) {
			if (httpServeletRequest.getRequestURL().toString().toLowerCase().contains(urlsArray.get(i).toLowerCase())) {
				isExists = true;
				break;
			}
		}
		String jwtToken = "";
		final int index = 7;
		if (authHeader != null && authHeader.startsWith("Bearer")) {
			jwtToken = authHeader.substring(index);
		}
		if ((authHeader == null || authHeader.length() == 0 || !authHeader.startsWith("Bearer")) && !isExists) {
			httpServeltresponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
		} else if (authHeader != null && jwtToken.length() > 0 && TokenAuthenticationService.isTokenExpired(jwtToken)) {
			httpServeltresponse.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Invalid token.");
		} else {
			Authentication authentication = TokenAuthenticationService.getAuthentication(httpServeletRequest);
			SecurityContextHolder.getContext().setAuthentication(authentication);
		}
		filterChain.doFilter(request, response);
	}
}
