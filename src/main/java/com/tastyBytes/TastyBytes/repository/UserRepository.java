package com.tastyBytes.TastyBytes.repository;

import com.tastyBytes.TastyBytes.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User,Integer> {
    User findByEmail(String email);
}
