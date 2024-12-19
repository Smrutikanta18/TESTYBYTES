package com.tastyBytes.TastyBytes.repository;

import com.tastyBytes.TastyBytes.entities.Admin;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminRepository extends JpaRepository<Admin, Integer> {
    Admin findByEmail(String email);
}
