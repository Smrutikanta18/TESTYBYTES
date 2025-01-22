package com.tastyBytes.TastyBytes.controller;

import com.tastyBytes.TastyBytes.entities.Banner;
import com.tastyBytes.TastyBytes.entities.Data;
import com.tastyBytes.TastyBytes.entities.Instagram;
import com.tastyBytes.TastyBytes.entities.MenuItem;
import com.tastyBytes.TastyBytes.repository.BannerRepository;
import com.tastyBytes.TastyBytes.repository.DataRepository;
import com.tastyBytes.TastyBytes.repository.InstagramRepository;
import com.tastyBytes.TastyBytes.repository.MenuItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Controller
public class MainController {

    @Autowired
    private BannerRepository bannerRepository;

    @Autowired
    private DataRepository dataRepository;

    @Autowired
    private MenuItemRepository menuItemRepository;

    @Autowired
    private InstagramRepository instagramRepository;

    private void addAuthenticationStatus(Model model) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = authentication != null && authentication.isAuthenticated() &&
                !(authentication.getPrincipal() instanceof String &&
                        authentication.getPrincipal().equals("anonymousUser"));
        model.addAttribute("isAuthenticated", isAuthenticated);
    }

    @RequestMapping("/")
    public String home1(Model model) {
        addAuthenticationStatus(model);
        List<Banner> banners = bannerRepository.findAll();
        model.addAttribute("banners", banners);  // Add banners to the model
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        Data data = dataRepository.findById(1).orElse(null); // Assuming you want the record with ID 1
        model.addAttribute("data", data);
        List<MenuItem> menuItems = menuItemRepository.findAll(); // Fetch your menu items here

        // Group items by category
        Map<String, List<MenuItem>> groupedItems = menuItems.stream()
                .collect(Collectors.groupingBy(MenuItem::getCategory));

        model.addAttribute("groupedItems", groupedItems);
        return "index";
    }

    @RequestMapping("/index")
    public String home2(Model model) {
        addAuthenticationStatus(model);
        List<Banner> banners = bannerRepository.findAll();
        model.addAttribute("banners", banners);  // Add banners to the model
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        Data data = dataRepository.findById(1).orElse(null); // Assuming you want the record with ID 1
        model.addAttribute("data", data);
        List<MenuItem> menuItems = menuItemRepository.findAll(); // Fetch your menu items here

        // Group items by category
        Map<String, List<MenuItem>> groupedItems = menuItems.stream()
                .collect(Collectors.groupingBy(MenuItem::getCategory));

        model.addAttribute("groupedItems", groupedItems);
        return "index";
    }

    @RequestMapping("/about")
    public String about(Model model) {
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        Data data = dataRepository.findById(1).orElse(null); // Assuming you want the record with ID 1
        model.addAttribute("data", data);
        return "about";
    }

    @RequestMapping("/contact")
    public String contact(Model model) {
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        return "contact";
    }

    @RequestMapping("/cart")
    public String cart(Model model) {
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        return "cart";
    }

    @RequestMapping("/checkout")
    public String checkout(Model model) {
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        return "checkout";
    }

    @RequestMapping("/menu")
    public String menu(Model model) {
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        List<MenuItem> menuItems = menuItemRepository.findAll(); // Fetch your menu items here

        // Group items by category
        Map<String, List<MenuItem>> groupedItems = menuItems.stream()
                .collect(Collectors.groupingBy(MenuItem::getCategory));

        model.addAttribute("groupedItems", groupedItems);
        return "menu";
    }

    @RequestMapping("/reservation")
    public String reservation(Model model) {
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        return "reservation";
    }

    @RequestMapping("/address")
    public String address(Model model) {
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        return "address";
    }
    @RequestMapping("/payment")
    public String payment(Model model) {
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        return "payment";
    }

    @RequestMapping("/orderPlaced")
    public String orderPlaced(Model model) {
        addAuthenticationStatus(model);
        List<Instagram> instagramImages = instagramRepository.findAll();
        model.addAttribute("instagramImages", instagramImages);
        return "completed";
    }

    @RequestMapping("/login")
    public String login(Model model) {
        addAuthenticationStatus(model);
        return "userLogin";
    }

    @RequestMapping("/signup")
    public String signup(Model model){
        addAuthenticationStatus(model);
        return "signup";
    }


}
