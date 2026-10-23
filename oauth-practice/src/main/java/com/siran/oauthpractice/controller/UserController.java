package com.siran.oauthpractice.controller;

import com.siran.oauthpractice.model.User;
import com.siran.oauthpractice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class UserController {

    private final UserRepository userRepository;

    public UserController(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @GetMapping("/user")
    public String user(Authentication authentication, Model model) {

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String email =
                oauth2User.getAttribute("email");

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        model.addAttribute("name", user.getName());
        model.addAttribute("email", user.getEmail());
        model.addAttribute("picture", user.getPicture());
        model.addAttribute("twoFactorEnabled", user.isTwoFactorEnabled());

        return "user";
    }

    @GetMapping("/2fa-setup")
    public String twoFactorSetup() {
        return "2fa-setup";
    }

    @GetMapping("/2fa-verify")
    public String twoFactorVerify() {
        return "2fa-verify";
    }
}