package com.siran.oauthpractice.controller;

import com.siran.oauthpractice.dto.TwoFactorSetupResponse;
import com.siran.oauthpractice.model.User;
import com.siran.oauthpractice.repository.UserRepository;
import com.siran.oauthpractice.service.QrCodeService;
import com.siran.oauthpractice.service.TwoFactorService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/2fa")
public class TwoFactorController {

    private final TwoFactorService twoFactorService;
    private final UserRepository userRepository;
    private final QrCodeService qrCodeService;

    public TwoFactorController(
            TwoFactorService twoFactorService,
            UserRepository userRepository,
            QrCodeService qrCodeService) {

        this.twoFactorService = twoFactorService;
        this.userRepository = userRepository;
        this.qrCodeService = qrCodeService;
    }

    @GetMapping("/setup")
    public TwoFactorSetupResponse setup(Authentication authentication) {

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String email = oauth2User.getAttribute("email");

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String otpAuthUri =
                twoFactorService.setupTwoFactor(user);

        String qrCode =
                "data:image/png;base64,"
                        + qrCodeService.generateQrCode(otpAuthUri);

        return new TwoFactorSetupResponse(
                qrCode,
                user.getTwoFactorSecret()
        );
    }

    @PostMapping("/verify")
    public String verify(
            @RequestParam int otp,
            Authentication authentication) {

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String email =
                oauth2User.getAttribute("email");

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String secret = user.getTwoFactorSecret();

        if (secret == null || secret.isBlank()) {
            return "2FA setup not started";
        }

        boolean valid =
                twoFactorService.verifyOtp(secret, otp);

        if (!valid) {
            return "Invalid OTP";
        }

        user.setTwoFactorEnabled(true);

        userRepository.save(user);

        return "2FA enabled successfully";
    }

    @PostMapping("/login/verify")
    public ResponseEntity<String> verifyLoginOtp(
            @RequestParam int otp,
            Authentication authentication,
            HttpServletRequest request) {

        OAuth2User oauth2User =
                (OAuth2User) authentication.getPrincipal();

        String email =
                oauth2User.getAttribute("email");

        User user = userRepository
                .findByEmail(email)
                .orElseThrow(() ->
                        new RuntimeException("User not found"));

        String secret = user.getTwoFactorSecret();

        if (secret == null || secret.isBlank()) {
            return ResponseEntity
                    .badRequest()
                    .body("2FA is not configured");
        }

        boolean valid =
                twoFactorService.verifyOtp(secret, otp);

        if (!valid) {
            return ResponseEntity
                    .badRequest()
                    .body("Invalid OTP");
        }

        request.getSession().setAttribute(
                "TWO_FACTOR_REQUIRED",
                false
        );

        return ResponseEntity.ok(
                "2FA verified successfully"
        );
    }
}