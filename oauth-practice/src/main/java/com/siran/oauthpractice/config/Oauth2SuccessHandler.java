package com.siran.oauthpractice.config;

import com.siran.oauthpractice.model.User;
import com.siran.oauthpractice.repository.UserRepository;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@Component
public class Oauth2SuccessHandler implements AuthenticationSuccessHandler {

    private final UserRepository userRepository;

    public Oauth2SuccessHandler(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public void onAuthenticationSuccess(
            HttpServletRequest request,
            HttpServletResponse response,
            Authentication authentication
    ) throws IOException, ServletException {

        OAuth2User oauth2User = (OAuth2User) authentication.getPrincipal();

        String name = oauth2User.getAttribute("name");
        String email = oauth2User.getAttribute("email");
        String picture = oauth2User.getAttribute("picture");

        User user = userRepository.findByEmail(email)
                .orElseGet(User::new);

        user.setName(name);
        user.setEmail(email);
        user.setPicture(picture);

        userRepository.save(user);

        if (user.isTwoFactorEnabled()) {

            request.getSession().setAttribute("TWO_FACTOR_REQUIRED", true);
            response.sendRedirect("/2fa-verify");
        } else {
            response.sendRedirect("/user");
        }
    }
}