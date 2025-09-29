package com.example.groceryPickbot.user.models;

import jakarta.validation.constraints.NotBlank;

public record UserDTO(
        Long id,

        @NotBlank
        String username,

        @NotBlank
        String password,

        @NotBlank
        String matchingPassword
) {}
