package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.entities.Admin;
import com.tastyBytes.TastyBytes.entities.Banner;
import com.tastyBytes.TastyBytes.repository.BannerRepository;
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
public class BannerController {

    @Autowired
    private BannerRepository bannerRepository;

    private static final String UPLOAD_DIR = "images/";

    @RequestMapping("/banners")
    public String viewBanners(HttpSession session,Model model) {
        Admin admin = (Admin) session.getAttribute("loggedInAdmin");
        if (admin != null) {
            List<Banner> banners = bannerRepository.findAll();
            model.addAttribute("banners", banners);
            return "admin/bannerImage";
        }
        return "redirect:/adminLogin";

    }

    @PostMapping("/banners/add")
    public String addBanner(@RequestParam("bImage") MultipartFile file,
                            @RequestParam("content") String content) throws IOException {
            // Get the original file name
            String fileName = file.getOriginalFilename();

            // Specify the directory where the image will be uploaded
            File uploadDir = new ClassPathResource("static/images").getFile();

            // Create the path for the file
            Path path = Paths.get(uploadDir.getAbsolutePath() + File.separator + fileName);

            // Ensure the directory exists
            if (!uploadDir.exists()) {
                uploadDir.mkdirs();
            }

            // Copy the file content to the directory
            Files.copy(file.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

            // Create a Banner object and set the image file name
            Banner banner = new Banner();
            banner.setBImage(fileName);
            banner.setContent(content);

            // Save the banner to the database
            bannerRepository.save(banner);

            // Redirect to the banners page
            return "redirect:/banners";

    }


    @PostMapping("/banners/edit/{id}")
    public String editBanner(@PathVariable("id") int id, @RequestParam("bImage") MultipartFile file) throws IOException {
        // Fetch the banner object from the repository
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid banner ID"));

            // Check if a new image has been uploaded
            if (!file.isEmpty()) {
                // Delete the old image if it exists
                if (banner.getBImage() != null && !banner.getBImage().isEmpty()) {
                    // Specify the path for the old image
                    File uploadDir = new ClassPathResource("static/images").getFile();
                    Path oldImagePath = Paths.get(uploadDir.getAbsolutePath() + File.separator + banner.getBImage());

                    // Delete the old image if it exists
                    Files.deleteIfExists(oldImagePath);
                }

                // Save the new image
                String fileName = file.getOriginalFilename();
                // Specify the directory for saving the new image
                File uploadDir = new ClassPathResource("static/images").getFile();
                Path newPath = Paths.get(uploadDir.getAbsolutePath() + File.separator + fileName);

                // Ensure the directory exists
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Copy the new image to the directory
                Files.copy(file.getInputStream(), newPath, StandardCopyOption.REPLACE_EXISTING);

                // Update the banner with the new image file name
                banner.setBImage(fileName);
            }

            // Save the updated banner details to the database
            bannerRepository.save(banner);

            // Redirect to the banners page
            return "redirect:/banners";
    }



    @GetMapping("/banners/delete/{id}")
    public String deleteBanner(@PathVariable("id") int id) {
        // Retrieve the banner to get the image name
        Banner banner = bannerRepository.findById(id).orElseThrow(() -> new IllegalArgumentException("Invalid banner ID"));

        // Construct the file path
        Path imagePath = Paths.get(UPLOAD_DIR + banner.getBImage());

        try {
            // Delete the image file
            Files.deleteIfExists(imagePath);
        } catch (IOException e) {
            e.printStackTrace();
            // Optionally, log this error or handle it in a way suitable for your application
        }

        // Delete the banner record from the database
        bannerRepository.deleteById(id);

        return "redirect:/banners";
    }

}
