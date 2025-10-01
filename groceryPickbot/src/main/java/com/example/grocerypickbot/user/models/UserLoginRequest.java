package com.example.grocerypickbot.user.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * Model representing a user login request.
 */
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class UserLoginRequest {
  private String username;
  private String password;
}
