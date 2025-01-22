package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.config.CustomUserDetail;
import com.tastyBytes.TastyBytes.entities.Cart;
import com.tastyBytes.TastyBytes.entities.User;
import com.tastyBytes.TastyBytes.repository.CartRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import jakarta.servlet.http.HttpSession;
import java.util.*;

@RestController
@RequestMapping("/cart")
public class CartController {

    @Autowired
    private CartRepository cartRepository;

    private User getAuthenticatedUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication != null && authentication.isAuthenticated() &&
                authentication.getPrincipal() instanceof CustomUserDetail) {
            CustomUserDetail userDetails = (CustomUserDetail) authentication.getPrincipal();
            return userDetails.getUser();
        }
        return null;
    }

    @PostMapping("/add-to-cart")
    public ResponseEntity<Map<String, Object>> addToCart(@RequestBody Map<String, String> itemData, HttpSession session) {
        int itemId = Integer.parseInt(itemData.get("id"));
        String itemName = itemData.get("name");
        String itemImage = itemData.get("image");
        int itemOffer = Integer.parseInt(itemData.get("offer"));
        int itemAbovePrice = Integer.parseInt(itemData.get("abovePrice"));
        int itemPrice = Integer.parseInt(itemData.get("itemPrice"));

        User loggedInUser = getAuthenticatedUser();

        int totalQuantity = 0;

        if (loggedInUser == null) {
            // Handle unauthenticated user using the session
            @SuppressWarnings("unchecked")
            Map<Integer, Cart> sessionCart = (Map<Integer, Cart>) session.getAttribute("cart");
            if (sessionCart == null) {
                sessionCart = new HashMap<>();
            }

            // Add or update item in session cart
            Cart cartItem = sessionCart.getOrDefault(itemId, new Cart());
            cartItem.setItemId(itemId);
            cartItem.setName(itemName);
            cartItem.setImage(itemImage);
            cartItem.setQuantity(cartItem.getQuantity() + 1);
            cartItem.setOffer(itemOffer);
            cartItem.setAbovePrice(itemAbovePrice);
            cartItem.setPrice(itemPrice);
            sessionCart.put(itemId, cartItem);

            session.setAttribute("cart", sessionCart);

            // Calculate total quantity in the session cart
            totalQuantity = sessionCart.values().stream().mapToInt(Cart::getQuantity).sum();
        } else {
            // Handle authenticated user using the database
            Optional<Cart> existingCartItem = cartRepository.findByUserAndItemId(loggedInUser, itemId);
            if (existingCartItem.isPresent()) {
                Cart cartItem = existingCartItem.get();
                cartItem.setQuantity(cartItem.getQuantity() + 1);
                cartRepository.save(cartItem);
            } else {
                Cart newCartItem = new Cart();
                newCartItem.setItemId(itemId);
                newCartItem.setName(itemName);
                newCartItem.setImage(itemImage);
                newCartItem.setQuantity(1);
                newCartItem.setOffer(itemOffer);
                newCartItem.setAbovePrice(itemAbovePrice);
                newCartItem.setPrice(itemPrice);
                newCartItem.setUser(loggedInUser);
                cartRepository.save(newCartItem);
            }

            // Calculate total quantity from database for the logged-in user
            totalQuantity = cartRepository.findByUser(loggedInUser).stream().mapToInt(Cart::getQuantity).sum();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("totalQuantity", totalQuantity); // Add the total quantity to the response
        return ResponseEntity.ok(response);
    }


    @GetMapping("/get-cart-items")
    public ResponseEntity<Map<String, Object>> getCartItems(HttpSession session) {
        User loggedInUser = getAuthenticatedUser();

        List<Cart> cartItems;
        if (loggedInUser == null) {
            @SuppressWarnings("unchecked")
            Map<Integer, Cart> sessionCart = (Map<Integer, Cart>) session.getAttribute("cart");
            cartItems = sessionCart == null ? new ArrayList<>() : new ArrayList<>(sessionCart.values());
        } else {
            cartItems = cartRepository.findByUser(loggedInUser);
        }

        Map<String, Object> response = new HashMap<>();
        response.put("status", "success");
        response.put("cartItems", cartItems);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/update-quantity")
    public ResponseEntity<Map<String, String>> updateCartItemQuantity(@RequestBody Map<String, Object> requestData, HttpSession session) {
        int itemId = Integer.parseInt(requestData.get("itemId").toString());
        int quantity = Integer.parseInt(requestData.get("quantity").toString());

        User loggedInUser = getAuthenticatedUser();

        if (loggedInUser == null) {
            // Update quantity in session for non-logged-in user
            @SuppressWarnings("unchecked")
            Map<Integer, Cart> sessionCart = (Map<Integer, Cart>) session.getAttribute("cart");
            if (sessionCart != null) {
                Cart cartItem = sessionCart.get(itemId);
                if (cartItem != null) {
                    cartItem.setQuantity(quantity);
                    sessionCart.put(itemId, cartItem);
                }
                session.setAttribute("cart", sessionCart);
            }
        } else {
            // Update quantity in the database for logged-in user
            Optional<Cart> cartItem = cartRepository.findByUserAndItemId(loggedInUser, itemId);
            cartItem.ifPresent(cart -> {
                cart.setQuantity(quantity);
                cartRepository.save(cart);
            });
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    // Remove an item from the cart
    @DeleteMapping("/remove-item/{itemId}")
    public ResponseEntity<Map<String, String>> removeItemFromCart(@PathVariable int itemId, HttpSession session) {
        User loggedInUser = getAuthenticatedUser();

        if (loggedInUser == null) {
            // Handle unauthenticated user (session-based cart)
            @SuppressWarnings("unchecked")
            Map<Integer, Cart> sessionCart = (Map<Integer, Cart>) session.getAttribute("cart");
            if (sessionCart != null) {
                sessionCart.remove(itemId); // Remove the item from the session cart
                session.setAttribute("cart", sessionCart);
            }
        } else {
            // Handle authenticated user (database-based cart)
            Optional<Cart> cartItem = cartRepository.findByUserAndItemId(loggedInUser, itemId);
            cartItem.ifPresent(cart -> {
                cartRepository.delete(cart); // Remove the item from the cart table
            });
        }

        Map<String, String> response = new HashMap<>();
        response.put("status", "success");
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cart-quantity")
    public ResponseEntity<Map<String, Object>> getCartQuantity(HttpSession session) {
        User loggedInUser = getAuthenticatedUser();
        int totalQuantity = 0;

        if (loggedInUser == null) {
            // Session-based cart
            @SuppressWarnings("unchecked")
            Map<Integer, Cart> sessionCart = (Map<Integer, Cart>) session.getAttribute("cart");
            totalQuantity = sessionCart == null ? 0 : sessionCart.values().stream().mapToInt(Cart::getQuantity).sum();
        } else {
            // Database-based cart
            totalQuantity = cartRepository.findByUser(loggedInUser).stream().mapToInt(Cart::getQuantity).sum();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalQuantity", totalQuantity);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/cart-total-price")
    public ResponseEntity<Map<String, Object>> getCartTotalPrice(HttpSession session) {
        User loggedInUser = getAuthenticatedUser();
        double totalPrice = 0;

        if (loggedInUser == null) {
            // Session-based cart
            @SuppressWarnings("unchecked")
            Map<Integer, Cart> sessionCart = (Map<Integer, Cart>) session.getAttribute("cart");
            if (sessionCart != null) {
                totalPrice = sessionCart.values().stream()
                        .mapToDouble(item -> item.getPrice() * item.getQuantity())
                        .sum();
            }
        } else {
            // Database-based cart
            List<Cart> userCart = cartRepository.findByUser(loggedInUser);
            totalPrice = userCart.stream()
                    .mapToDouble(item -> item.getPrice() * item.getQuantity())
                    .sum();
        }

        Map<String, Object> response = new HashMap<>();
        response.put("totalPrice", totalPrice);
        return ResponseEntity.ok(response);
    }


}
