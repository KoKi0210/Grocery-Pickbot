package com.example.groceryPickbot.user.controllers;

import com.example.groceryPickbot.exceptions.InvalidUserRegistrationException;
import com.example.groceryPickbot.user.models.UserDTO;
import com.example.groceryPickbot.user.models.UserLoginRequest;
import com.example.groceryPickbot.user.services.UserService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final UserService userService;

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    @PostMapping("/login")
    public ResponseEntity<?> login(@RequestBody UserLoginRequest loginRequest) {
        return userService.authenticateUser(loginRequest);
    }

    @PostMapping("/registration")
    public ResponseEntity<?> registerUserAccount(@Valid @RequestBody UserDTO userDto) {
        try {
            userService.registerNewUserAccount(userDto);
        } catch (InvalidUserRegistrationException iureEx) {
            return ResponseEntity.badRequest().body(iureEx.getErrors());
        }
        return ResponseEntity.ok(Map.of("message", "User registered successfully!"));
    }
}
