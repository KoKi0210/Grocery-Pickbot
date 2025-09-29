package com.example.groceryPickbot.user.repositories;

import com.example.groceryPickbot.user.models.UserDB;
import org.springframework.data.repository.CrudRepository;

public interface UserRepository extends CrudRepository<UserDB, Integer> {
    UserDB findByUsername(String username);
    boolean existsByUsername(String username);
}
