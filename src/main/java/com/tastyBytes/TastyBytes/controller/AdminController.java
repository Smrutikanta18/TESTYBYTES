package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.entities.Admin;
import com.tastyBytes.TastyBytes.repository.AdminRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class AdminController {

    @Autowired
    private AdminRepository adminRepository;

    @RequestMapping("/adminIndex")
    public String adminIndex(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin != null) {
            model.addAttribute("admin", admin);
            return "admin/index";
        }
        return "redirect:/adminLogin";
    }

    @RequestMapping("/adminProfile")
    public String adminProfile(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin != null) {
            model.addAttribute("admin", admin);
            return "admin/profile";
        }
        return "redirect:/adminLogin";
    }

    @RequestMapping("/adminLogin")
    public String adminLogin() {
        return "admin/login";
    }

    @RequestMapping("/adminRegistration")
    public String adminRegistration(Model model) {
        model.addAttribute("admin", new Admin());
        return "admin/signup";
    }

    @PostMapping("/processAdminLogin")
    public String processAdminLogin(
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            HttpSession session,
            Model model) {

        Admin admin = adminRepository.findByEmail(email);

        if (admin != null && admin.getPassword().equals(password)) {
            session.setAttribute("loggedInAdmin", admin);
            return "redirect:/adminIndex";
        }

        model.addAttribute("error", "Invalid email or password");
        return "admin/login";
    }
    @PostMapping("/processAdminRegistration")
    public String registerAdmin(@ModelAttribute Admin admin, Model model) {
        try {
            // Check if email already exists
            if (adminRepository.findByEmail(admin.getEmail()) != null) {
                model.addAttribute("error", "Email already registered!");
                return "redirect:/adminRegistration"; // Return the registration page with the error
            }

            // Save admin to database
            adminRepository.save(admin);
            return "redirect:/adminLogin"; // Redirect to login page after successful registration
        } catch (Exception e) {
            model.addAttribute("error", "An error occurred while registering!");
            return "redirect:/adminRegistration"; // Return the registration page with the error
        }
    }

    @PostMapping("/updateAdminDetails")
    public String updateAdminDetails(
            @RequestParam("id") Integer id,
            @RequestParam("email") String email,
            HttpSession session) {

        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin != null) {
            admin.setEmail(email);
            adminRepository.save(admin);
            session.setAttribute("loggedInAdmin", admin);
        }

        return "redirect:/adminProfile";
    }


    @PostMapping("/changeAdminPassword")
    public String changeAdminPassword(
            @RequestParam("id") Integer id,
            @RequestParam("newPassword") String newPassword,
            HttpSession session) {

        Admin admin = adminRepository.findById(id).orElse(null);
        if (admin != null) {
            admin.setPassword(newPassword);
            adminRepository.save(admin);
            session.setAttribute("loggedInAdmin", admin);
        }

        return "redirect:/adminProfile";
    }

    @RequestMapping("/adminLogout")
    public String adminLogout(HttpSession session) {
        session.invalidate();
        return "redirect:/adminLogin";
    }
}
