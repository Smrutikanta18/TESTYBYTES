package com.tastyBytes.TastyBytes.repository;


import com.tastyBytes.TastyBytes.entities.MenuItem;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MenuItemRepository extends JpaRepository<MenuItem, Integer> {
}
