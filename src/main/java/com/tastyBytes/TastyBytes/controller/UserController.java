package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.entities.Instagram;
import com.tastyBytes.TastyBytes.entities.PendingUser;
import com.tastyBytes.TastyBytes.entities.User;
import com.tastyBytes.TastyBytes.repository.InstagramRepository;
import com.tastyBytes.TastyBytes.repository.PendingUserRepository;
import com.tastyBytes.TastyBytes.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Controller
public class UserController {

    @Autowired
    private BCryptPasswordEncoder bCryptPasswordEncoder;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private PendingUserRepository pendingUserRepository;
    @Autowired
    private InstagramRepository instagramRepository;
    @Autowired
    private JavaMailSender javaMailSender;
    private void addAuthenticationStatus(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String &&
                        authentication.getPrincipal().equals("anonymousUser"));
        model.addAttribute("isAuthenticated", isAuthenticated);
    }

    @RequestMapping("/profile")
    public String profile(Model model) {
        // Get the authenticated user's username (email or another unique field)
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = authentication.getName();

        // Fetch the user data from the repository using the username (email)
        User user = userRepository.findByEmail(username); // Adjust this method as per your UserRepository

        // Add user data to the model to pass to the view
        model.addAttribute("user", user);

        // Call the method to check if the user is authenticated
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);

        return "profile"; // Returning the profile view
    }
    @PostMapping("/profile/changePassword")
    public ResponseEntity<?> changePassword(
            @RequestParam("userId") Integer userId,
            @RequestParam("email") String email,
            @RequestParam("currentPassword") String currentPassword,
            @RequestParam("newPassword") String newPassword,
            @RequestParam("confirmPassword") String confirmPassword) {

        // Fetch user by ID
        User user = userRepository.findById(userId).orElse(null);

        if (user == null || !user.getEmail().equals(email)) {
            return ResponseEntity.badRequest().body(Map.of("message", "Invalid email or user not found."));
        }

        // Check if current password matches
        if (!bCryptPasswordEncoder.matches(currentPassword, user.getPassword())) {
            return ResponseEntity.badRequest().body(Map.of("message", "Current password is incorrect."));
        }

        // Check if new passwords match
        if (!newPassword.equals(confirmPassword)) {
            return ResponseEntity.badRequest().body(Map.of("message", "New passwords do not match."));
        }

        // Update password
        user.setPassword(bCryptPasswordEncoder.encode(newPassword));
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("message", "Password changed successfully."));
    }

    @PostMapping("/profile/update")
    public ResponseEntity<?> updateProfile(
            @RequestParam("userId") Integer userId,
            @RequestParam("firstName") String firstName,
            @RequestParam("lastName") String lastName,
            @RequestParam("email") String email,
            @RequestParam("phone") String phone) {

        // Fetch user by ID
        User user = userRepository.findById(userId).orElse(null);
        if (user == null) {
            return ResponseEntity.badRequest().body(Map.of("success", false, "message", "User not found."));
        }

        // Update user details
        user.setFirstname(firstName);
        user.setLastname(lastName);
        user.setEmail(email);
        user.setNumber(phone);
        userRepository.save(user);

        return ResponseEntity.ok(Map.of("success", "Profile updated successfully."));
    }

    @PostMapping("/register")
    public ResponseEntity<?> registerUser(
            @RequestParam("firstname") String firstname,
            @RequestParam("lastname") String lastname,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam("number") String number,
            @RequestParam("gender") String gender) {

        // Validate phone number
        if (number.length() != 10 || !number.matches("\\d+")) {
            return ResponseEntity.badRequest().body(Map.of("message", "Phone number must be 10 digits."));
        }

        // Check if email already exists in 'users' or 'pending_users'
        if (userRepository.findByEmail(email) != null || pendingUserRepository.findByEmail(email) != null) {
            return ResponseEntity.badRequest().body(Map.of("message", "Email already exists."));
        }

        // Generate a verification token
        String verificationToken = UUID.randomUUID().toString();

        // Save user data temporarily in 'pending_users'
        PendingUser pendingUser = new PendingUser(
                firstname,
                lastname,
                email,
                bCryptPasswordEncoder.encode(password),
                number,
                gender,
                verificationToken,
                Timestamp.valueOf(LocalDateTime.now())
        );
        pendingUserRepository.save(pendingUser);

        // Send email verification link
        try {
            String verificationLink = "http://localhost:8086/verify-email?token=" + verificationToken;

            // Build email message
            SimpleMailMessage mailMessage = new SimpleMailMessage();
            mailMessage.setTo(email);
            mailMessage.setSubject("Email Verification - Your App");
            mailMessage.setText("Thank you for registering! Please verify your email by clicking the link: " + verificationLink);

            // Send email
            javaMailSender.send(mailMessage);

            return ResponseEntity.ok(Map.of("message", "Registration successful. Please check your email for verification."));
        } catch (Exception ex) {
            // Rollback by deleting from 'pending_users' if email fails to send
            pendingUserRepository.deleteById(pendingUser.getId());
            return ResponseEntity.internalServerError().body(Map.of("message", "Failed to send verification email. Please try again."));
        }
    }


    @GetMapping("/verify-email")
    public String verifyEmail(@RequestParam("token") String token, Model model) {
        // Find user in 'pending_users' by token
        PendingUser pendingUser = pendingUserRepository.findByVerificationToken(token);

        if (pendingUser == null) {
            model.addAttribute("message", "Invalid or expired verification token.");
            return "verification-failure";
        }

        // Move user to 'users' table
        User user = new User();
        user.setFirstname(pendingUser.getFirstname());
        user.setLastname(pendingUser.getLastname());
        user.setEmail(pendingUser.getEmail());
        user.setPassword(pendingUser.getPassword());
        user.setNumber(pendingUser.getNumber());
        user.setGender(pendingUser.getGender());
        user.setRole("USER");
        user.setCreated_at(Timestamp.valueOf(LocalDateTime.now()));
        userRepository.save(user);

        // Remove user from 'pending_users'
        pendingUserRepository.delete(pendingUser);

        model.addAttribute("message", "Email verified successfully. You can now log in.");
        return "login";
    }


}
