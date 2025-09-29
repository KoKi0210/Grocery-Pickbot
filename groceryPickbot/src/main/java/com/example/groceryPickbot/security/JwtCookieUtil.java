package com.example.groceryPickbot.security;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

@Component
public class JwtCookieUtil {
    @Value("${jwt.cookie.max-age}")
    private int jwtCookieMaxAge;

    public ResponseCookie generateJwtCookie(String jwtToken) {
        return ResponseCookie.from("jwt-token", jwtToken)
                .httpOnly(true) // Prevents JavaScript access
                .secure(false) // false in development, set to true in production with HTTPS
                .path("/") // Cookie is valid for the entire domain
                .maxAge(jwtCookieMaxAge) // Set cookie expiration
                .build();
    }
}

