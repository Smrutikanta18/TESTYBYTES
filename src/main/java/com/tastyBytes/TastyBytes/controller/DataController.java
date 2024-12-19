package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.entities.Data;
import com.tastyBytes.TastyBytes.repository.DataRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
public class DataController {

    @Autowired
    private DataRepository dataRepository;

    @GetMapping("/data")
    public String viewData(HttpSession session, Model model) {
        if (session.getAttribute("loggedInAdmin") != null) {
            model.addAttribute("data", dataRepository.findById(1).orElse(null));
            return "admin/data";
        }
        return "redirect:/adminLogin";
    }

    @PostMapping("/updateData")
    public String updateData(@RequestParam("id") int id,
                             @RequestParam("customers") int customers,
                             @RequestParam("experience") int experience,
                             @RequestParam("menus") int menus,
                             @RequestParam("staffs") int staffs) {
        // Retrieve the existing record
        Data existingData = dataRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid data ID: " + id));

        // Update fields
        existingData.setCustomers(customers);
        existingData.setExperience(experience);
        existingData.setMenus(menus);
        existingData.setStaffs(staffs);

        // Save updated record
        dataRepository.save(existingData);

        return "redirect:/data";
    }

}
