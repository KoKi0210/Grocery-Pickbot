package com.example.groceryPickbot.user.mappers;

import com.example.groceryPickbot.user.models.UserDB;
import com.example.groceryPickbot.user.models.UserDTO;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserDB toEntity(UserDTO userDTO);
}
