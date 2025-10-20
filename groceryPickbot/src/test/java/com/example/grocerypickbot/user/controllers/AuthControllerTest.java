package com.example.grocerypickbot.user.controllers;

import com.example.grocerypickbot.exceptions.InvalidUserRegistrationException;
import com.example.grocerypickbot.user.models.Role;
import com.example.grocerypickbot.user.models.UserLoginRequest;
import com.example.grocerypickbot.user.models.UserRegisterRequest;
import com.example.grocerypickbot.user.services.UserService;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import java.util.Map;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@ExtendWith(SpringExtension.class)
public class AuthControllerTest {

  @Mock
  private UserService userService;

  @InjectMocks
  private AuthController authController;

  private MockMvc mockMvc;
  private ObjectMapper objectMapper;

  @BeforeEach
  void setUp() {
    mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    objectMapper = new ObjectMapper();
  }

  @Test
  void login_whenValidCredentials_shouldReturnOk() throws Exception {
    UserLoginRequest userLoginRequest = new UserLoginRequest("testuser", "Test@1234");

    doReturn(ResponseEntity.ok(Map.of("message", "Login successful!")))
        .when(userService).authenticateUser(any(UserLoginRequest.class));

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userLoginRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("Login successful!"));

    verify(userService).authenticateUser(any(UserLoginRequest.class));
  }

  @Test
  void login_whenInvalidCredentials_shouldReturnUnauthorized() throws Exception {
    UserLoginRequest userLoginRequest = new UserLoginRequest("wronguser", "wrongpass");

    doReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Invalid username or password")))
        .when(userService).authenticateUser(any(UserLoginRequest.class));

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userLoginRequest)))
        .andExpect(status().isUnauthorized())
        .andExpect(jsonPath("$.error").value("Invalid username or password"));

    verify(userService).authenticateUser(any(UserLoginRequest.class));
  }

  @Test
  void login_whenEmptyUsername_shouldReturnUnauthorized() throws Exception {
    UserLoginRequest userLoginRequest = new UserLoginRequest("", "Test@1234");

    doReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Invalid username or password")))
        .when(userService).authenticateUser(any(UserLoginRequest.class));

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userLoginRequest)))
        .andExpect(status().isUnauthorized());

    verify(userService).authenticateUser(any(UserLoginRequest.class));
  }

  @Test
  void login_whenEmptyPassword_shouldReturnUnauthorized() throws Exception {
    UserLoginRequest userLoginRequest = new UserLoginRequest("testuser", "");

    doReturn(ResponseEntity.status(HttpStatus.UNAUTHORIZED)
            .body(Map.of("error", "Invalid username or password")))
        .when(userService).authenticateUser(any(UserLoginRequest.class));

    mockMvc.perform(post("/api/auth/login")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(userLoginRequest)))
        .andExpect(status().isUnauthorized());

    verify(userService).authenticateUser(any(UserLoginRequest.class));
  }

  @Test
  void registration_whenValidAdminRole_shouldReturnOk() throws Exception {
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        null, "newuser", "Password@123", "Password@123", "@dm!n", Role.ADMIN);

    mockMvc.perform(post("/api/auth/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("User registered successfully!"));

    verify(userService).registerNewUserAccount(any(UserRegisterRequest.class));
  }

  @Test
  void registration_whenValidUserRole_shouldReturnOk() throws Exception {
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        null, "newuser", "Password@123", "Password@123", "@dm!n", Role.USER);

    mockMvc.perform(post("/api/auth/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("User registered successfully!"));

    verify(userService).registerNewUserAccount(any(UserRegisterRequest.class));
  }

  @Test
  void registration_whenPasswordsDoNotMatch_shouldReturnBadRequest() throws Exception {
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        null, "newuser", "Password@123", "DifferentPassword@123", "@dm!n", Role.USER);

    Map<String, String> errors = Map.of("matchingPassword", "Passwords do not match");
    doThrow(new InvalidUserRegistrationException(errors))
        .when(userService).registerNewUserAccount(any(UserRegisterRequest.class));

    mockMvc.perform(post("/api/auth/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.matchingPassword").value("Passwords do not match"));

    verify(userService).registerNewUserAccount(any(UserRegisterRequest.class));
  }

  @Test
  void registration_whenUsernameAlreadyExists_shouldReturnBadRequest() throws Exception {
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        null, "existinguser", "Password@123", "Password@123", "@dm!n", Role.USER);

    Map<String, String> errors = Map.of("username", "Username already exists");
    doThrow(new InvalidUserRegistrationException(errors))
        .when(userService).registerNewUserAccount(any(UserRegisterRequest.class));

    mockMvc.perform(post("/api/auth/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.username").value("Username already exists"));

    verify(userService).registerNewUserAccount(any(UserRegisterRequest.class));
  }

  @Test
  void registration_whenPasswordTooWeak_shouldReturnBadRequest() throws Exception {
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        null, "newuser", "weak", "weak", "@dm!n", Role.USER);

    Map<String, String> errors = Map.of("password",
        "Password must be at least 8 characters, contain one uppercase letter, one special symbol, and one number.");
    doThrow(new InvalidUserRegistrationException(errors))
        .when(userService).registerNewUserAccount(any(UserRegisterRequest.class));

    mockMvc.perform(post("/api/auth/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.password").exists());

    verify(userService).registerNewUserAccount(any(UserRegisterRequest.class));
  }

  @Test
  void registration_whenRegisteringAdminUser_shouldReturnOk() throws Exception {
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        null, "adminuser", "Admin@123", "Admin@123", "@dm!n", Role.ADMIN);

    mockMvc.perform(post("/api/auth/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isOk())
        .andExpect(jsonPath("$.message").value("User registered successfully!"));

    verify(userService).registerNewUserAccount(any(UserRegisterRequest.class));
  }

  @Test
  void registration_whenMultipleValidationErrors_shouldReturnBadRequest() throws Exception {
    UserRegisterRequest registerRequest = new UserRegisterRequest(
        null, "existinguser", "weak", "different", "@dm!n", Role.USER);

    Map<String, String> errors = Map.of(
        "username", "Username already exists",
        "password", "Password must be at least 8 characters, contain one uppercase letter, one special symbol, and one number.",
        "matchingPassword", "Passwords do not match"
    );
    doThrow(new InvalidUserRegistrationException(errors))
        .when(userService).registerNewUserAccount(any(UserRegisterRequest.class));

    mockMvc.perform(post("/api/auth/registration")
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(registerRequest)))
        .andExpect(status().isBadRequest())
        .andExpect(jsonPath("$.username").value("Username already exists"))
        .andExpect(jsonPath("$.password").exists())
        .andExpect(jsonPath("$.matchingPassword").value("Passwords do not match"));

    verify(userService).registerNewUserAccount(any(UserRegisterRequest.class));
  }
}

