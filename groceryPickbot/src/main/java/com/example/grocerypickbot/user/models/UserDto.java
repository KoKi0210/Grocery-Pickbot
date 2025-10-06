package com.example.grocerypickbot.user.models;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;

/**
 * Data Transfer Object for User information.
 *
 * @param id               the unique identifier of the user
 * @param username         the username of the user
 * @param password         the password of the user
 * @param matchingPassword the password confirmation for validation
 */
public record UserDto(
        Long id,

        @NotBlank
        String username,

        @NotBlank
        String password,

        @NotBlank
        String matchingPassword,

        @NotEmpty
        Role role
) {
}
