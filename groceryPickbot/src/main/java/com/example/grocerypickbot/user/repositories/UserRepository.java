package com.example.grocerypickbot.user.repositories;

import com.example.grocerypickbot.user.models.UserDb;
import org.springframework.data.repository.CrudRepository;

/**
 * Repository interface for managing UserDb entities.
 */
public interface UserRepository extends CrudRepository<UserDb, Integer> {
  /**
   * Finds a user by their username.
   *
   * @param username the username to search for
   * @return the UserDb entity with the specified username, or null if not found
   */
  UserDb findByUsername(String username);

  /**
   * Checks if a user with the specified username exists.
   *
   * @param username the username to check
   * @return true if a user with the specified username exists, false otherwise
   */
  boolean existsByUsername(String username);
}
