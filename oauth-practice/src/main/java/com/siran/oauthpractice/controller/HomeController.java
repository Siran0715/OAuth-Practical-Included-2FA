package com.siran.oauthpractice.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class HomeController {

    // "/" is permitAll in SecurityConfig but had no mapping, so home.html was never served.
    @GetMapping("/")
    public String home() {
        return "home";
    }
}
