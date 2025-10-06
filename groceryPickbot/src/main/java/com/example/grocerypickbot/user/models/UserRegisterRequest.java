package com.example.grocerypickbot.user.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

/**
 * Model representing a user registration request.
 */
public record UserRegisterRequest(
    Long id,

    @NotBlank
    String username,

    @NotBlank
    String password,

    @NotBlank
    String matchingPassword,

    @NotNull
    String adminCode,

    @NotNull
    Role role
) {
}
