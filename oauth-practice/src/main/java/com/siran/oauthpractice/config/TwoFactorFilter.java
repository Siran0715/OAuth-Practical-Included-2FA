package com.siran.oauthpractice.config;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

public class TwoFactorFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(
            HttpServletRequest request,
            HttpServletResponse response,
            FilterChain filterChain
    ) throws ServletException, IOException {

        Boolean twoFactorRequired =
                (Boolean) request.getSession()
                        .getAttribute("TWO_FACTOR_REQUIRED");

        String uri = request.getRequestURI();

        boolean isTwoFactorPage =
                uri.equals("/2fa-verify")
                        || uri.equals("/2fa/login/verify");

        if (Boolean.TRUE.equals(twoFactorRequired)
                && !isTwoFactorPage) {

            response.sendRedirect("/2fa-verify");
            return;
        }

        filterChain.doFilter(request, response);
    }
}