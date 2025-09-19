package com.example.groceryPickbot.user.repositories;

import com.example.groceryPickbot.user.models.User;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<User, Integer> {
    User findByUsername(String username);
    boolean existsByUsername(String username);
}
