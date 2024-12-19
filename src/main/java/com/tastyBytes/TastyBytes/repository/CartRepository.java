package com.tastyBytes.TastyBytes.repository;

import com.tastyBytes.TastyBytes.entities.Cart;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CartRepository extends JpaRepository <Cart, Integer>{
}
