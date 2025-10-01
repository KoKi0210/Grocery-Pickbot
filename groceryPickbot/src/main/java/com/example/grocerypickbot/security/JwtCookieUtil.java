package com.example.grocerypickbot.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

/**
 * Utility class for generating JWT cookies.
 */
@Component
public class JwtCookieUtil {
  @Value("${jwt.cookie.max-age}")
  private int jwtCookieMaxAge;

  /**
   * Generates a JWT cookie with the specified token.
   *
   * @param jwtToken the JWT token to be stored in the cookie
   * @return a ResponseCookie object representing the JWT cookie
   */
  public ResponseCookie generateJwtCookie(String jwtToken) {
    return ResponseCookie.from("jwt-token", jwtToken)
            .httpOnly(true) // Prevents JavaScript access
            .path("/") // Cookie is valid for the entire domain
            .maxAge(jwtCookieMaxAge) // Set cookie expiration
            .build();
  }
}

