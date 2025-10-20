package com.example.grocerypickbot.user.services;

import com.example.grocerypickbot.user.models.UserDb;
import com.example.grocerypickbot.user.models.UserRegisterRequest;
import com.example.grocerypickbot.user.models.Role;
import com.example.grocerypickbot.user.mappers.UserMapper;
import com.example.grocerypickbot.user.repositories.UserRepository;
import com.example.grocerypickbot.exceptions.InvalidUserRegistrationException;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class UserServiceTest {
    @Mock
    private UserRepository userRepository;
    @Mock
    private UserMapper userMapper;
    @Mock
    private PasswordEncoder passwordEncoder;
    @InjectMocks
    private UserService userService;

    @Test
    void registerNewUserAccount_whenValidUserRoleRequest_shouldCreateUserSuccessfully() {
        UserRegisterRequest request = new UserRegisterRequest(1L,"user", "Passw0rd!", "Passw0rd!","@dm!n",
                Role.USER);
        UserDb userEntity = new UserDb();
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("Passw0rd!")).thenReturn("encodedPass");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserDb result = userService.registerNewUserAccount(request);
        assertEquals(userEntity, result);
        assertEquals("encodedPass", userEntity.getPassword());
    }

    @Test
    void registerNewUserAccount_whenValidAdminRoleRequest_shouldCreateAdminUserSuccessfully() {
        UserRegisterRequest request = new UserRegisterRequest(1L,"user", "Passw0rd!", "Passw0rd!",
            "@dm!n",
            Role.ADMIN);
        UserDb userEntity = new UserDb();
        when(userMapper.toEntity(request)).thenReturn(userEntity);
        when(passwordEncoder.encode("Passw0rd!")).thenReturn("encodedPass");
        when(userRepository.existsByUsername("user")).thenReturn(false);
        when(userRepository.save(userEntity)).thenReturn(userEntity);

        UserDb result = userService.registerNewUserAccount(request);
        assertEquals(userEntity, result);
        assertEquals("encodedPass", userEntity.getPassword());
    }

    @Test
    void registerNewUserAccount_whenDuplicateUsername_shouldThrowInvalidUserRegistrationException() {
        UserRegisterRequest request = new UserRegisterRequest(1L,"user", "Passw0rd!", "Passw0rd!","@dm!n",
            Role.ADMIN);
        when(userRepository.existsByUsername("user")).thenReturn(true);
        assertThrows(InvalidUserRegistrationException.class, () -> userService.registerNewUserAccount(request));
    }

    @Test
    void registerNewUserAccount_whenPasswordMismatch_shouldThrowInvalidUserRegistrationException() {
        UserRegisterRequest request = new UserRegisterRequest(1L,"user", "Passw0rd!", "Passw0!","@dm" +
            "!n",
            Role.ADMIN);
        when(userRepository.existsByUsername("user")).thenReturn(false);
        assertThrows(InvalidUserRegistrationException.class, () -> userService.registerNewUserAccount(request));
    }

    @Test
    void registerNewUserAccount_whenInvalidAdminCode_shouldThrowInvalidUserRegistrationException() {
        UserRegisterRequest request = new UserRegisterRequest(1L,"admin", "Passw0rd!", "Passw0rd!","admin",
            Role.ADMIN);
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        assertThrows(InvalidUserRegistrationException.class, () -> userService.registerNewUserAccount(request));
    }

    @Test
    void registerNewUserAccount_whenBlankAdminCode_shouldThrowInvalidUserRegistrationException() {
        UserRegisterRequest request = new UserRegisterRequest(1L,"admin", "Passw0rd!", "Passw0rd!",
            " ", Role.ADMIN);
        when(userRepository.existsByUsername("admin")).thenReturn(false);
        assertThrows(InvalidUserRegistrationException.class, () -> userService.registerNewUserAccount(request));
    }
}
