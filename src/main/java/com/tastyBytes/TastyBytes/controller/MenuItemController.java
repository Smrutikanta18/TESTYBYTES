package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.entities.Admin;
import com.tastyBytes.TastyBytes.entities.MenuItem;
import com.tastyBytes.TastyBytes.repository.MenuItemRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

@Controller
public class MenuItemController {

    @Autowired
    private MenuItemRepository menuItemRepository;

    @RequestMapping("/menuitem")
    public String getMenuItems(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin != null) {
            List<MenuItem> menuItems = menuItemRepository.findAll();
            model.addAttribute("menuItems", menuItems);
            return "admin/menuItem";
        }
        return "redirect:/adminLogin";
    }


    @PostMapping("/menu/add")
    public String addMenuItem(@RequestParam("name") String name,
                              @RequestParam("price") int price,
                              @RequestParam("ingredients") String ingredients,
                              @RequestParam("image") MultipartFile file,
                              @RequestParam("category") String category,
                              @RequestParam("offer") int offer,
                              @RequestParam("abovePrice") int abovePrice) throws IOException {

        String fileName = file.getOriginalFilename();
        File uploadDir = new ClassPathResource("static/images").getFile();
        Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + fileName);

        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

        MenuItem menuItem = new MenuItem();
        menuItem.setName(name);
        menuItem.setPrice(price);
        menuItem.setIngredients(ingredients);
        menuItem.setImage(fileName);
        menuItem.setCategory(category);
        menuItem.setOffer(offer);
        menuItem.setAbovePrice(abovePrice);

        menuItemRepository.save(menuItem);
        return "redirect:/menuitem";
    }

    @PostMapping("/menu/update/{id}")
    public String updateMenuItem(@PathVariable("id") int id,
                                 @RequestParam("name") String name,
                                 @RequestParam("price") int price,
                                 @RequestParam("ingredients") String ingredients,
                                 @RequestParam("image") MultipartFile file,
                                 @RequestParam("category") String category,
                                 @RequestParam("offer") int offer,
                                 @RequestParam("abovePrice") int abovePrice) throws IOException {

        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid menu item ID"));

        if (!file.isEmpty()) {
            File uploadDir = new ClassPathResource("static/images").getFile();
            Path oldImagePath = Paths.get(uploadDir.getAbsolutePath() + File.separator + menuItem.getImage());
            Files.deleteIfExists(oldImagePath);

            String fileName = file.getOriginalFilename();
            Path newPath = Paths.get(uploadDir.getAbsolutePath() + File.separator + fileName);

            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            Files.copy(file.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);
            menuItem.setImage(fileName);
        }

        menuItem.setName(name);
        menuItem.setPrice(price);
        menuItem.setIngredients(ingredients);
        menuItem.setCategory(category);
        menuItem.setOffer(offer);
        menuItem.setAbovePrice(abovePrice);

        menuItemRepository.save(menuItem);
        return "redirect:/menuitem";
    }

    @GetMapping("/menu/delete/{id}")
    public String deleteMenuItem(@PathVariable("id") int id) {
        MenuItem menuItem = menuItemRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid menu item ID"));
        try {
            File uploadDir = new ClassPathResource("static/images").getFile();
            Path imagePath = Paths.get(uploadDir.getAbsolutePath() + File.separator + menuItem.getImage());
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
        }
        menuItemRepository.deleteById(id);
        return "redirect:/menuitem";
    }
}
