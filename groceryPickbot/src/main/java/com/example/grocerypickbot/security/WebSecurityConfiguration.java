package com.example.grocerypickbot.security;

import com.example.grocerypickbot.exceptions.SecurityConfigurationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security configuration class for setting up authentication, authorization,
 * and security filters for the application.
 *
 * <p>Configures JWT-based authentication, password encoding, session management,
 * and endpoint access rules. Integrates custom authentication entry point and
 * token filter for securing REST APIs.
 * </p>
 */
@Configuration
public class WebSecurityConfiguration {

  private final AuthEntryPointJwt unauthorizedHandler;
  private final JwtUtils jwtUtils;
  private final UserDetailsService userDetailsService;

  /**
   * Constructor for WebSecurityConfiguration.
   *
   * @param unauthorizedHandler Handles unauthorized access attempts.
   * @param jwtUtils Utility class for JWT operations.
   * @param userDetailsService Service to load user-specific data.
   */
  public WebSecurityConfiguration(AuthEntryPointJwt unauthorizedHandler, JwtUtils jwtUtils,
                                  UserDetailsService userDetailsService) {
    this.unauthorizedHandler = unauthorizedHandler;
    this.jwtUtils = jwtUtils;
    this.userDetailsService = userDetailsService;
  }

  /**
   * Creates the authentication token filter bean.
   *
   * @return Token filter bean.
   */
  @Bean
  public AuthTokenFilter authenticationJwtTokenFilter() {
    return new AuthTokenFilter(jwtUtils, userDetailsService);
  }

  /**
   * Creates the authentication manager bean.
   *
   * @param authenticationConfiguration The authentication configuration.
   * @return The authentication manager bean.
   * @throws SecurityConfigurationException If an error occurs while creating the bean.
   */
  @Bean
  public AuthenticationManager authenticationManager(
      AuthenticationConfiguration authenticationConfiguration)
      throws SecurityConfigurationException {
    try {
      return authenticationConfiguration.getAuthenticationManager();
    } catch (Exception e) {
      throw new SecurityConfigurationException("Error creating AuthenticationManager bean", e);
    }
  }

  /**
   * Creates the password encoder bean.
   *
   * @return The password encoder bean.
   */
  @Bean
  public PasswordEncoder passwordEncoder() {
    return new BCryptPasswordEncoder();
  }

  /**
   * Configures the security filter chain.
   *
   * @param http The HttpSecurity object to configure.
   * @return The configured SecurityFilterChain.
   * @throws Exception If an error occurs during configuration.
   */
  @Bean
  public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
    http
        .csrf(AbstractHttpConfigurer::disable)
        .exceptionHandling(ex -> ex.authenticationEntryPoint(unauthorizedHandler))
        .sessionManagement(sess -> sess.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .authorizeHttpRequests(auth -> auth
            .requestMatchers("/", "/index.html", "/login.html", "/registration.html",
                "/api/auth/**", "/css/**", "/js/**", "/h2-console/**").permitAll()
            .anyRequest().authenticated()
        );

    http.addFilterBefore(authenticationJwtTokenFilter(),
        UsernamePasswordAuthenticationFilter.class);

    return http.build();
  }
}