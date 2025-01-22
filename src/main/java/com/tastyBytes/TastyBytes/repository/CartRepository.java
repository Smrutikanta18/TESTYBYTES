package com.tastyBytes.TastyBytes.repository;

import com.tastyBytes.TastyBytes.entities.Cart;
import com.tastyBytes.TastyBytes.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Integer> {
    List<Cart> findByUser(User user);

    Optional<Cart> findByUserAndItemId(User user, int itemId);
}
