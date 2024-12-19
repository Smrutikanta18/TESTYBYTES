package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.entities.ContactForm;
import com.tastyBytes.TastyBytes.repository.ContactFormRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

@RestController
public class ContactController {

    @Autowired
    private ContactFormRepository contactFormRepository;

    @PostMapping("/addContact")
    public Map<String, Object> saveContactForm(@RequestBody ContactForm contactForm) {
        Map<String, Object> response = new HashMap<>();
        try {
            if (!contactForm.getEmail().matches("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$")) {
                response.put("success", false);
                response.put("error", "Invalid email address.");
                return response;
            }

            contactFormRepository.save(contactForm);
            response.put("success", true);
        } catch (Exception e) {
            response.put("success", false);
            response.put("error", "Failed to save data. Please try again.");
        }
        return response;
    }
}
