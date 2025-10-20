package com.example.grocerypickbot.user.mappers;

import com.example.grocerypickbot.user.models.Role;
import com.example.grocerypickbot.user.models.UserDb;
import com.example.grocerypickbot.user.models.UserDto;
import com.example.grocerypickbot.user.models.UserRegisterRequest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@ExtendWith(SpringExtension.class)
@SpringBootTest
public class UserMapperTest {

    @Autowired
    private UserMapper userMapper;
    @Test
    void toEntity_shouldCorrectlyMapUserDtoToUserDbWithUserRole() {
        UserDto userDto = new UserDto(
                1L,
                "testuser",
                "password123",
                "password123",
                Role.USER
        );

        UserDb result = userMapper.toEntity(userDto);

        assertNotNull(result);
        assertEquals(userDto.id(), result.getId());
        assertEquals(userDto.username(), result.getUsername());
        assertEquals(userDto.password(), result.getPassword());
        assertEquals(userDto.role(), result.getRole());
    }

    @Test
    void toEntity_shouldCorrectlyMapUserDtoToUserDbWithAdminRole() {
        UserDto userDto = new UserDto(
                2L,
                "adminuser",
                "adminpass",
                "adminpass",
                Role.ADMIN
        );

        UserDb result = userMapper.toEntity(userDto);

        assertNotNull(result);
        assertEquals(userDto.id(), result.getId());
        assertEquals(userDto.username(), result.getUsername());
        assertEquals(userDto.password(), result.getPassword());
        assertEquals(userDto.role(), result.getRole());
    }

    @Test
    void toEntity_shouldCorrectlyMapUserRegisterRequestToUserDbWithAdminRole() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                1L,
                "registeruser",
                "registerpass",
                "registerpass",
                "ADMIN123",
                Role.ADMIN
        );

        // Act
        UserDb result = userMapper.toEntity(userRegisterRequest);

        assertNotNull(result);
        assertEquals(userRegisterRequest.id(), result.getId());
        assertEquals(userRegisterRequest.username(), result.getUsername());
        assertEquals(userRegisterRequest.password(), result.getPassword());
        assertEquals(userRegisterRequest.role(), result.getRole());
    }

    @Test
    void toEntity_shouldCorrectlyMapUserRegisterRequestToUserDbWithUserRole() {
        UserRegisterRequest userRegisterRequest = new UserRegisterRequest(
                3L,
                "normaluser",
                "userpass",
                "userpass",
                "",
                Role.USER
        );

        UserDb result = userMapper.toEntity(userRegisterRequest);

        assertNotNull(result);
        assertEquals(userRegisterRequest.id(), result.getId());
        assertEquals(userRegisterRequest.username(), result.getUsername());
        assertEquals(userRegisterRequest.password(), result.getPassword());
        assertEquals(userRegisterRequest.role(), result.getRole());
    }
}