package com.example.groceryPickbot.user.services;

import com.example.groceryPickbot.exceptions.InvalidUserRegistrationException;
import com.example.groceryPickbot.security.JwtCookieUtil;
import com.example.groceryPickbot.security.JwtUtils;
import com.example.groceryPickbot.user.mappers.UserMapper;
import com.example.groceryPickbot.user.models.UserDB;
import com.example.groceryPickbot.user.models.UserDTO;
import com.example.groceryPickbot.user.models.UserLoginRequest;
import com.example.groceryPickbot.user.repositories.UserRepository;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestBody;

@Service
@Transactional
public class UserService {

    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[A-Z])(?=.*[!@#$%^&*()_+\\-={}|\\[\\]:\";'<>?,./])(?=.*\\d).{8,}$");
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final UserMapper userMapper;
    private final AuthenticationManager authenticationManager;
    private final JwtUtils jwtUtils;
    private final JwtCookieUtil jwtCookieUtil;

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

    public UserDB registerNewUserAccount(UserDTO userDto) {
        validateUserRegistration(userDto);

        UserDB user = userMapper.toEntity(userDto);
        user.setPassword(passwordEncoder.encode(userDto.password()));

        return userRepository.save(user);
    }

    private void validateUserRegistration(UserDTO userDto) {
        Map<String, String> errors = new HashMap<>();

        if (!PASSWORD_PATTERN.matcher(userDto.password()).matches()) {
            errors.put("password", "Password must be at least 8 characters, contain one uppercase letter, one special symbol, and one number.");
        }
        if (!Objects.equals(userDto.password(), userDto.matchingPassword())) {
            errors.put("matchingPassword", "Passwords don't match");
        }
        if (userRepository.existsByUsername(userDto.username())) {
            errors.put("username", "An account for that username already exists.");
        }
        if (!errors.isEmpty()) {
            throw new InvalidUserRegistrationException(errors);
        }
    }

    public ResponseEntity<?> authenticateUser(@RequestBody UserLoginRequest loginRequest) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getUsername(), loginRequest.getPassword())
            );

            String jwtToken = jwtUtils.generateToken(authentication.getName());
            ResponseCookie cookie = jwtCookieUtil.generateJwtCookie(jwtToken);

            return ResponseEntity.ok().header(HttpHeaders.SET_COOKIE, cookie.toString())
                    .body(Map.of("message", "Login successful!"));

        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(Map.of("error", "Invalid username or password"));
        }
    }
}
