package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.entities.Admin;
import com.tastyBytes.TastyBytes.entities.Instagram;
import com.tastyBytes.TastyBytes.repository.InstagramRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

@Controller
public class InstagramController {

    @Autowired
    private InstagramRepository instagramRepository;

    private static final String IMAGE_UPLOAD_DIR = "static/images/"; // Adjust to your folder path

    // Display Instagram Images
    @GetMapping("/insta")
    public String viewInstagram(HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin != null) {
            List<Instagram> instagrams = instagramRepository.findAll();
            model.addAttribute("instagram", instagrams);
            return "admin/insta";
        }
        return "redirect:/adminLogin";
    }

    // Add Instagram Image
    @PostMapping("/addInstagram")
    public String addInstagram(@RequestParam("image") MultipartFile imageFile, HttpSession session, Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/adminLogin";
        }

        if (!imageFile.isEmpty()) {
            try {
                // Generate unique file name
                String fileName = imageFile.getOriginalFilename();

                // Specify the directory to upload the image
                File uploadDir = new ClassPathResource(IMAGE_UPLOAD_DIR).getFile();

                // Ensure the directory exists
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Define the file path
                Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + fileName);

                // Copy the file to the specified location
                Files.copy(imageFile.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

                // Save the Instagram image information in the database
                Instagram instagram = new Instagram();
                instagram.setImage(fileName);
                instagramRepository.save(instagram);

            } catch (IOException e) {
                e.printStackTrace();
                model.addAttribute("error", "Error uploading the image. Please try again.");
            }
        } else {
            model.addAttribute("error", "Please select an image to upload.");
        }

        return "redirect:/insta";
    }

    // Delete Instagram Image
    @PostMapping("/deleteInstagram")
    public String deleteInstagram(@RequestParam("id") int id, HttpSession session) throws IOException {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin == null) {
            return "redirect:/adminLogin";
        }

        Instagram instagram = instagramRepository.findById(id).orElse(null);
        if (instagram != null) {
            // Construct the file path to delete
            File fileToDelete = new File(new ClassPathResource(IMAGE_UPLOAD_DIR).getFile(), instagram.getImage());

            // Delete the image file from the server
            if (fileToDelete.exists()) {
                fileToDelete.delete();
            }

            // Delete the record from the database
            instagramRepository.delete(instagram);
        }

        return "redirect:/insta";
    }
}
