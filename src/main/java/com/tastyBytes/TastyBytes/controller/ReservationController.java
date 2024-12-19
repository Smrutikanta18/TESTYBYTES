package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.config.CustomUserDetail;
import com.tastyBytes.TastyBytes.entities.Reservation;
import com.tastyBytes.TastyBytes.entities.User;
import com.tastyBytes.TastyBytes.repository.ReservationRepository;
import com.tastyBytes.TastyBytes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import java.sql.Timestamp;
import java.util.HashMap;
import java.util.Map;

@RestController
public class ReservationController {

    @Autowired
    private ReservationRepository reservationRepository;

    @Autowired
    private UserRepository userRepository;

    // Method to handle reservation form submission
    @PostMapping("/addReservation")
    @ResponseBody
    public Map<String, Object> addReservation(@RequestBody Reservation reservation) {
        // Get the authentication object
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        // Get the principal, which is your custom UserDetails object
        Object principal = authentication.getPrincipal();

        Map<String, Object> response = new HashMap<>();

        // Check if the principal is an instance of your custom UserDetails class
        if (principal instanceof CustomUserDetail) {
            // Cast to CustomUserDetail
            CustomUserDetail customUserDetail = (CustomUserDetail) principal;

            // Get the User entity from your CustomUserDetail
            User user = customUserDetail.getUser();  // Assuming your CustomUserDetail has a getUser() method

            if (user != null && authentication.isAuthenticated()) {
                // Associate the logged-in user with the reservation
                reservation.setUser(user);
                reservation.setCreated_at(new Timestamp(System.currentTimeMillis())); // Set created timestamp
                reservation.setStatus("pending"); // Set default status
                reservationRepository.save(reservation); // Save the reservation

                // Success response
                response.put("success", true);
                response.put("message", "Reservation successful!");
            } else {
                // Failure response (User not logged in)
                response.put("success", false);
                response.put("error", "User not logged in.");
            }
        } else {
            // Failure response (Invalid user data)
            response.put("success", false);
            response.put("error", "Invalid user data.");
        }

        return response; // Return JSON response
    }


}
