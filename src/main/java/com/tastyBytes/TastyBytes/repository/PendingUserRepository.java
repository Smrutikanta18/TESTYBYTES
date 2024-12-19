package com.tastyBytes.TastyBytes.repository;

import com.tastyBytes.TastyBytes.entities.PendingUser;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PendingUserRepository extends JpaRepository<PendingUser , Integer> {
    PendingUser findByVerificationToken(String verificationToken); // Method to find by token
    PendingUser findByEmail(String email);
}
