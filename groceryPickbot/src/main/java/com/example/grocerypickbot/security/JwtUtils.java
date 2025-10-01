package com.example.grocerypickbot.security;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import javax.crypto.SecretKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

/**
 * Utility class for handling JWT operations such as generation and validation.
 */
@Component
public class JwtUtils {
  @Value("${jwt.secret}")
  private String jwtSecret;
  @Value("${jwt.expiration}")
  private int jwtExpirationMs;
  private SecretKey key;

  /**
   * Initializes the secret key used for signing JWTs after the bean is constructed.
   */
  @PostConstruct
  public void init() {
    this.key = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
  }

  /**
   * Generates a JWT token for the given username.
   *
   * @param username the username for which the token is generated
   * @return a signed JWT token as a String
   */
  public String generateToken(String username) {
    return Jwts.builder()
            .subject(username)
            .issuedAt(new Date())
            .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
            .signWith(key) // The key itself implies the algorithm (HS256 in this case)
            .compact();
  }

  /**
   * Extracts the username from the given JWT token.
   *
   * @param token the JWT token
   * @return the username contained in the token
   */
  public String getUsernameFromToken(String token) {
    return Jwts.parser()
            .verifyWith(key)
            .build()
            .parseSignedClaims(token)
            .getPayload()
            .getSubject();
  }

  /**
   * Validates the given JWT token.
   *
   * @param token the JWT token to validate
   * @return true if the token is valid, false otherwise
   */
  public boolean validateJwtToken(String token) {
    try {
      Jwts.parser().verifyWith(key).build().parseSignedClaims(token);
      return true;
    } catch (SecurityException e) {
      System.out.println("Invalid JWT signature: " + e.getMessage());
    } catch (MalformedJwtException e) {
      System.out.println("Invalid JWT token: " + e.getMessage());
    } catch (ExpiredJwtException e) {
      System.out.println("JWT token is expired: " + e.getMessage());
    } catch (UnsupportedJwtException e) {
      System.out.println("JWT token is unsupported: " + e.getMessage());
    } catch (IllegalArgumentException e) {
      System.out.println("JWT claims string is empty: " + e.getMessage());
    } catch (NullPointerException e) {
      System.out.println("JWT token is null: " + e.getMessage());
    }
    return false;
  }
}
