package com.example.grocerypickbot.user.mappers;

import com.example.grocerypickbot.user.models.UserDb;
import com.example.grocerypickbot.user.models.UserDto;
import com.example.grocerypickbot.user.models.UserRegisterRequest;
import org.mapstruct.Mapper;
import org.mapstruct.MappingConstants;

/**
 * Mapper interface for converting between UserDto and UserDb entities.
 */
@Mapper(componentModel = MappingConstants.ComponentModel.SPRING)
public interface UserMapper {

  /**
   * Converts a UserDto to a UserDb entity.
   *
   * @param userDto the UserDto to convert
   * @return the corresponding UserDb entity
   */
  UserDb toEntity(UserDto userDto);

  /**
   * Converts a UserRegisterRequest to a UserDb entity.
   *
   * @param userDb the UserRegisterRequest to convert
   * @return the corresponding UserDb entity
   */
  UserDb toEntity(UserRegisterRequest userDb);
}
