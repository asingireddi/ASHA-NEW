package com.miraclesoft.scvp.config;

import static java.util.Objects.nonNull;

import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import com.miraclesoft.scvp.model.Configurations;
import com.miraclesoft.scvp.security.CustomAuthenticationManager;
import com.miraclesoft.scvp.security.JWTAuthenticationFilter;
import com.miraclesoft.scvp.security.JWTLoginFilter;

/**
 * The Class WebSecurityConfig.
 *
 * @author Narendar Geesidi
 */
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {

	/** The custom authentication manager. */
	@Autowired
	private CustomAuthenticationManager customAuthenticationManager;

	@Autowired
	private Configurations configurations;

	@Autowired
	private JdbcTemplate jdbcTemplate;

	// @Value("${IP_ADDRESS}")
	// private String ipAddress;

	@Value("#{'${IP_ADDRESS:}'.split(',')}")
	private List<String> ipAddress;

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected void configure(final AuthenticationManagerBuilder authenticationManagerBuilder) throws Exception {
		authenticationManagerBuilder.authenticationProvider(customAuthenticationManager);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override

	protected void configure(final HttpSecurity httpSecurity) throws Exception {
		httpSecurity.cors().and().csrf().disable().authorizeRequests()
				.antMatchers("/", "/user/forgotPassword/**", "/user/register", "/user/registerd", "/swagger-ui.html",
						"/swagger.json", "/user/forgotUserName/**", "/healthcheck", "/key")
				.permitAll().antMatchers(HttpMethod.POST, "/login").permitAll().anyRequest().authenticated().and()
				// We filter the api /login requests
				.addFilterBefore(new JWTLoginFilter("/login", authenticationManager()),
						UsernamePasswordAuthenticationFilter.class)
				// And filter other requests to check the presence of JWT in header
				.addFilterBefore(new JWTAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
	}

	/**
	 * Cors configuration source.
	 *
	 * @return the cors configuration source
	 */
	@Bean
	CorsConfigurationSource corsConfigurationSource() {
		final UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
		// source.registerCorsConfiguration("/**", new
		// CorsConfiguration().applyPermitDefaultValues());
		// return source;
		boolean s = false;
		CorsConfiguration config = new CorsConfiguration();
		config.setAllowCredentials(s);
		System.out.println(ipAddress);
		config.addAllowedOrigin("*");
		config.addAllowedHeader("*");
		config.addAllowedMethod("OPTIONS");
		config.addAllowedMethod("GET");
		config.addAllowedMethod("POST");
		config.addAllowedMethod("PUT");
		config.addAllowedMethod("DELETE");
		config.addAllowedMethod("TRACE");
		config.addAllowedMethod("PATCH");
		config.addAllowedMethod("HEAD");
		config.addAllowedMethod("CONNECT");
		source.registerCorsConfiguration("/**", config);
		return source;

	}

	/**
	 * JavaMailSender configuration source.
	 *
	 * @return the javaMailSender configuration source
	 */
	@Bean
	public JavaMailSender getJavaMailSender() {
		final List<Map<String, Object>> rows = jdbcTemplate.queryForList("SELECT * from   backend_configurations");
		if (!rows.isEmpty()) {
			for (final Map<String, Object> row : rows) {
				configurations
						.setSmtpHostName(nonNull(row.get("smtp_host_name")) ? (String) row.get("smtp_host_name") : "-");
				configurations.setSmtpFromMailId(
						nonNull(row.get("smtp_from_mailid")) ? (String) row.get("smtp_from_mailid") : "-");
				if (row.get("smtp_port") != null) {
					configurations.setSmtpPort(nonNull(row.get("smtp_port")) ? (String) row.get("smtp_port") : "-");
				} else {
					configurations.setSmtpPort("25");
				}
				configurations.setSmtpFromidPwd(
						nonNull(row.get("smtp_fromid_pwd")) ? (String) row.get("smtp_fromid_pwd") : "-");
				configurations.setB2bReprocessUrl(
						nonNull(row.get("b2b_reprocess_url")) ? (String) row.get("b2b_reprocess_url") : "-");
				configurations.setB2bReprocessSfgUrl(
						nonNull(row.get("b2b_reprocess_sfg_url")) ? (String) row.get("b2b_reprocess_sfg_url") : "-");
				configurations.setS3BucketAccessKey(
						nonNull(row.get("s3_bucket_access_key")) ? (String) row.get("s3_bucket_access_key") : "-");
				configurations.setS3BucketSecretKey(
						nonNull(row.get("s3_bucket_sceret_key")) ? (String) row.get("s3_bucket_sceret_key") : "-");
				configurations.setS3BbucketRegion(
						nonNull(row.get("s3_bucket_region")) ? (String) row.get("s3_bucket_region") : "-");
				configurations.setS3BbucketName(
						nonNull(row.get("s3_bucket_name")) ? (String) row.get("s3_bucket_name") : "-");
			}
		}
		// System.out.print("rows"+ rows.size()+"printed"+rows);
		final JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
		mailSender.setHost(configurations.getSmtpHostName());
		mailSender.setPort(Integer.parseInt(configurations.getSmtpPort()));
		mailSender.setUsername(configurations.getSmtpFromMailId());
		mailSender.setPassword(configurations.getSmtpFromidPwd());
		final Properties props = mailSender.getJavaMailProperties();
		props.put("mail.transport.protocol", "smtp");
		props.put("mail.smtp.auth", false);
		props.put("mail.smtp.ssl.enable", "false");
		props.put("mail.debug", true);

		return mailSender;
	}

}
