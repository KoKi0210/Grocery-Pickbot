package com.example.grocerypickbot.user.services;

import com.example.grocerypickbot.exceptions.InvalidUserRegistrationException;
import com.example.grocerypickbot.security.JwtCookieUtil;
import com.example.grocerypickbot.security.JwtUtils;
import com.example.grocerypickbot.user.mappers.UserMapper;
import com.example.grocerypickbot.user.models.Role;
import com.example.grocerypickbot.user.models.UserDb;
import com.example.grocerypickbot.user.models.UserLoginRequest;
import com.example.grocerypickbot.user.models.UserRegisterRequest;
import com.example.grocerypickbot.user.repositories.UserRepository;
import jakarta.transaction.Transactional;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Pattern;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

/**
 * Service class for user registration and authentication.
 *
 * <p>Handles business logic for registering new users, validating registration data,
 * and authenticating users during login. Utilizes JWT for session management.
 * </p>
 */
@Service
@Transactional
public class UserService {

  private static final String ADMIN_CODE = "@dm!n";
  private static final Pattern PASSWORD_PATTERN = Pattern.compile(
      "^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./])(?=.*\\d).{8,}$");
  private final UserRepository userRepository;
  private final PasswordEncoder passwordEncoder;
  private final UserMapper userMapper;
  private final AuthenticationManager authenticationManager;
  private final JwtUtils jwtUtils;
  private final JwtCookieUtil jwtCookieUtil;

  /**
   * Constructor for UserService.
   *
   * @param userRepository        the user repository to access user data
   * @param passwordEncoder       the password encoder for hashing passwords
   * @param userMapper            the user mapper for converting between DTOs and entities
   * @param authenticationManager the authentication manager for handling login
   * @param jwtUtils              the JWT utility for token generation and validation
   * @param jwtCookieUtil         the JWT cookie utility for managing JWT cookies
   */
  public UserService(UserRepository userRepository,
                     PasswordEncoder passwordEncoder,
                     UserMapper userMapper,
                     AuthenticationManager authenticationManager,
                     JwtUtils jwtUtils,
                     JwtCookieUtil jwtCookieUtil) {
    this.userRepository = userRepository;
    this.passwordEncoder = passwordEncoder;
    this.userMapper = userMapper;
    this.authenticationManager = authenticationManager;
    this.jwtUtils = jwtUtils;
    this.jwtCookieUtil = jwtCookieUtil;
  }

  /**
   * Registers a new user account.
   *
   * @param userRegisterRequest the user data transfer object containing registration details
   * @return the registered UserDb entity
   * @throws InvalidUserRegistrationException if validation fails
   */
  public UserDb registerNewUserAccount(UserRegisterRequest userRegisterRequest) {
    validateUserRegistration(userRegisterRequest);

    UserDb user = userMapper.toEntity(userRegisterRequest);
    user.setPassword(passwordEncoder.encode(userRegisterRequest.password()));

    return userRepository.save(user);
  }

  private void validateUserRegistration(UserRegisterRequest userRegisterRequest) {
    Map<String, String> errors = new HashMap<>();

    if (!PASSWORD_PATTERN.matcher(userRegisterRequest.password()).matches()) {
      errors.put("password",
          "Password must be at least 8 characters, contain one uppercase letter,"
              + " one special symbol, and one number.");
    }
    if (!Objects.equals(userRegisterRequest.password(), userRegisterRequest.matchingPassword())) {
      errors.put("matchingPassword", "Passwords don't match");
    }
    if (userRepository.existsByUsername(userRegisterRequest.username())) {
      errors.put("username", "An account for that username already exists.");
    }
    if (Role.ADMIN.equals(userRegisterRequest.role())) {
      if (!userRegisterRequest.adminCode().equals(ADMIN_CODE)) {
        errors.put("adminCode", "Admin code is incorrect.");
      }
      if (userRegisterRequest.adminCode().isBlank()) {
        errors.put("adminCode", "Admin code is required for admin registration.");
      }
    }
    if (!errors.isEmpty()) {
      throw new InvalidUserRegistrationException(errors);
    }
  }

  /**
   * Authenticates a user with the provided login request.
   *
   * @param loginRequest the login request containing username and password
   * @return a ResponseEntity with authentication result
   */
  public ResponseEntity<?> authenticateUser(@RequestBody UserLoginRequest loginRequest) {
    try {
      Authentication authentication = authenticationManager.authenticate(
          new UsernamePasswordAuthenticationToken(loginRequest.getUsername(),
              loginRequest.getPassword())
      );

      UserDb userDb = userRepository.findByUsername(loginRequest.getUsername());
      if (userDb == null) {
        throw new UsernameNotFoundException(loginRequest.getUsername());
      }

      String
          jwtToken =
          jwtUtils.generateToken(authentication.getName(), userDb.getRole().toString());
      ResponseCookie cookie = jwtCookieUtil.generateJwtCookie(jwtToken);

      return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
          .body(Map.of("message", "Login successful!"));

    } catch (AuthenticationException e) {
      return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
          .body(Map.of("error", "Invalid username or password"));
    }
  }
}
