package com.example.grocerypickbot.security;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collections;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.util.WebUtils;

/**
 * Servlet filter for authenticating requests using JWT tokens.
 *
 * <p>Extracts JWT tokens from cookies or headers, validates them, and sets the
 * authentication in the Spring Security context if valid. Integrates with
 * {@link JwtUtils} for token operations and {@link UserDetailsService} for loading user details.
 */
@Component
public class AuthTokenFilter extends OncePerRequestFilter {

  private static final String AUTH_TOKEN_HEADER = "Authorization";

  private static final String AUTH_TOKEN_PREFIX = "Bearer ";

  private final JwtUtils jwtUtils;

  private final UserDetailsService userDetailsService;

  /**
   * Constructor for AuthTokenFilter.
   *
   * @param jwtUtils           used to handle JWT operations.
   * @param userDetailsService used to load user-specific data.
   */
  public AuthTokenFilter(
      JwtUtils jwtUtils,
      UserDetailsService userDetailsService) {
    this.jwtUtils = jwtUtils;
    this.userDetailsService = userDetailsService;
  }

  @Override
  protected void doFilterInternal(HttpServletRequest request,
                                  HttpServletResponse response,
                                  FilterChain filterChain)
      throws ServletException, IOException {
    try {
      String jwt = extractJwtToken(request);
      if (jwtUtils.validateJwtToken(jwt)) {
        String username = jwtUtils.getUsernameFromToken(jwt);
        UserDetails userDetails = userDetailsService.loadUserByUsername(username);
        String userRole = jwtUtils.getRoleFromToken(jwt);
        SimpleGrantedAuthority
            authority =
            new SimpleGrantedAuthority("ROLE_" + userRole.toUpperCase());
        UsernamePasswordAuthenticationToken authentication =
            new UsernamePasswordAuthenticationToken(
                userDetails, null, Collections.singletonList(authority));
        SecurityContextHolder.getContext().setAuthentication(authentication);
      }
    } catch (Exception e) {
      logger.error("Cannot set user authentication: {}", e);
    }

    filterChain.doFilter(request, response);
  }

  private String extractJwtToken(HttpServletRequest request) {
    Cookie cookie = WebUtils.getCookie(request, "jwt-token");
    if (cookie != null) {
      return cookie.getValue();
    }
    String headerAuth = request.getHeader(AUTH_TOKEN_HEADER);
    if (StringUtils.hasText(headerAuth) && headerAuth.startsWith(AUTH_TOKEN_PREFIX)) {
      return headerAuth.substring(7);
    }
    return null;
  }
}
