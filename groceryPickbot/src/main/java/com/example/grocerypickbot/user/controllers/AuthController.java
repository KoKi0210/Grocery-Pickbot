package com.example.grocerypickbot.user.controllers;

import com.example.grocerypickbot.exceptions.InvalidUserRegistrationException;
import com.example.grocerypickbot.user.models.UserLoginRequest;
import com.example.grocerypickbot.user.models.UserRegisterRequest;
import com.example.grocerypickbot.user.services.UserService;
import jakarta.validation.Valid;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST controller for handling user authentication and registration endpoints.
 *
 * <p>Provides endpoints for user login and registration, delegating business logic
 * to the {@link UserService}. Handles validation and error responses for registration.
 * </p>
 */

@RestController
@RequestMapping("/api/auth")
public class AuthController {

  private final UserService userService;

  /**
   * Constructor for AuthController.
   *
   * @param userService the user service to handle authentication and registration
   */
  public AuthController(UserService userService) {
    this.userService = userService;
  }

  /**
   * Endpoint for user login.
   *
   * @param loginRequest the login request containing username and password
   * @return a ResponseEntity with authentication result
   */
  @PostMapping("/login")
  public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest) {
    return userService.authenticateUser(loginRequest);
  }

  /**
   * Endpoint for user registration.
   *
   * @param userRegisterRequest the user data transfer object containing registration details
   * @return a ResponseEntity indicating success or failure of registration
   */
  @PostMapping("/registration")
  public ResponseEntity<?> registerUserAccount(@Valid @RequestBody
                                                 UserRegisterRequest userRegisterRequest) {
    try {
      userService.registerNewUserAccount(userRegisterRequest);
    } catch (InvalidUserRegistrationException iureEx) {
      return ResponseEntity.badRequest().body(iureEx.getErrors());
    }
    return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
  }
}
