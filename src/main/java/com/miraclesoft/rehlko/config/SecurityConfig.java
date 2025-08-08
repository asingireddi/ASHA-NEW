package com.miraclesoft.rehlko.config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.ReactiveAuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.SecurityWebFiltersOrder;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.authentication.AuthenticationWebFilter;
import org.springframework.security.web.server.authentication.ServerAuthenticationConverter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Mono;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.util.Collections;
import java.util.List;
import java.util.function.Function;

import static java.util.Objects.nonNull;

@Configuration
@EnableWebFluxSecurity
public class SecurityConfig {

    @Value("#{'${IP_ADDRESS:}'.split(',')}")
    private List<String> ipAddress;

    /**
     * The Constant TOKEN_PREFIX.
     */
    static final String TOKEN_PREFIX = "Bearer "; // Note the trailing space

    /**
     * The Constant HEADER_STRING.
     */
    static final String HEADER_STRING = "Authorization";

    public static String secret = Base64.getEncoder().encodeToString("9d4a8b45f2c34a1dbe6c472fa8d670fae2f5ce789f83c1e0211e648fd930ba27455d71f9b5677d04f132bb2dfe8cb4b9ae83d87fce4d14f302fc662fab640f7a".getBytes());
    static byte[] keyBytes = Base64.getDecoder().decode(secret);
    static SecretKey key = new SecretKeySpec(keyBytes, "HmacSHA512");


    public Boolean validateToken(String token) {
        try {
            final String user = Jwts.parser().setSigningKey(key).parseClaimsJws(token)
                    .getBody().getSubject();

            return nonNull(user);
        } catch (Exception e) {
            return false;

        }
    }

    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key)
                .build()
                .parseClaimsJws(token)
                .getBody();
    }

//    public String extractRole(String token) {
//        return extractClaim(token, claims -> claims.get("role", String.class));
//    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .cors(ServerHttpSecurity.CorsSpec::disable)
                .authorizeExchange(exchanges -> exchanges
                        .pathMatchers("/","/healthcheck","/bannerNotifications/**").permitAll()
                        .anyExchange().authenticated()
                )
                .addFilterAt(corsWebFilter(), SecurityWebFiltersOrder.CORS) // Add the CORS filter
                .addFilterAt(jwtAuthenticationWebFilter(), SecurityWebFiltersOrder.AUTHENTICATION)
                .build();
    }

    @Bean
    public CorsWebFilter corsWebFilter() {

        CorsConfiguration corsConfig = new CorsConfiguration();
        corsConfig.setAllowCredentials(false);
        corsConfig.setAllowedOrigins(ipAddress);
        corsConfig.addAllowedMethod(HttpMethod.GET);
        corsConfig.addAllowedMethod(HttpMethod.POST);
        corsConfig.addAllowedMethod(HttpMethod.PUT);
        corsConfig.addAllowedMethod(HttpMethod.DELETE);
        corsConfig.addAllowedMethod(HttpMethod.OPTIONS);
        corsConfig.addAllowedHeader(HttpHeaders.AUTHORIZATION);
        corsConfig.addAllowedHeader(HttpHeaders.CONTENT_TYPE);
        corsConfig.addAllowedHeader("*");
        // If you need to handle cookies or authorization headers in CORS

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig); // Apply this configuration to all paths

        return new CorsWebFilter(source);
    }

    @Bean
    public AuthenticationWebFilter jwtAuthenticationWebFilter() {
        AuthenticationWebFilter filter = new AuthenticationWebFilter(authenticationManager());
        filter.setServerAuthenticationConverter(jwtServerAuthenticationConverter());
        return filter;
    }

    @Bean
    public ReactiveAuthenticationManager authenticationManager() {
        return authentication -> {
            if (authentication instanceof UsernamePasswordAuthenticationToken) {
                String token = (String) authentication.getCredentials();
                if (validateToken(token)) {
                    String username = extractUsername(token);
                    UserDetails userDetails = User.withUsername(username)
                            .password("")
                            .roles("") // Assuming you have a 'role' claim in your token
                            .build();
                    UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
                            userDetails, null, userDetails.getAuthorities());
                    return Mono.just(auth);
                }
            }
            return Mono.empty(); // Return Mono.empty() for invalid or non-JWT based authentication
        };
    }

    @Bean
    public ServerAuthenticationConverter jwtServerAuthenticationConverter() {
        return exchange -> {
            String authHeader = exchange.getRequest().getHeaders().getFirst(HEADER_STRING);
            if (authHeader != null && authHeader.startsWith(TOKEN_PREFIX)) {
                String token = authHeader.substring(TOKEN_PREFIX.length()); // Extract token after "Bearer "
                if (validateToken(token)) {
                    String username = extractUsername(token);
                    return Mono.just(new UsernamePasswordAuthenticationToken(username, token,
                            Collections.emptyList()));
                }
            }
            return Mono.empty();
        };
    }
}