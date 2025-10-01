package com.example.grocerypickbot.user.mappers;

import com.example.grocerypickbot.user.models.UserDb;
import com.example.grocerypickbot.user.models.UserDto;
import org.mapstruct.Mapper;

/**
 * Mapper interface for converting between UserDto and UserDb entities.
 */
@Mapper(componentModel = "spring")
public interface UserMapper {

  /**
   * Converts a UserDto to a UserDb entity.
   *
   * @param userDto the UserDto to convert
   * @return the corresponding UserDb entity
   */
  UserDb toEntity(UserDto userDto);
}
