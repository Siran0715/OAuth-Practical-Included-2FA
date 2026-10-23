package com.siran.oauthpractice.service;

import com.siran.oauthpractice.model.User;
import com.siran.oauthpractice.repository.UserRepository;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import org.springframework.stereotype.Service;

@Service
public class TwoFactorService {

    private final GoogleAuthenticator googleAuthenticator;
    private final UserRepository userRepository;

    public TwoFactorService(UserRepository userRepository) {
        this.googleAuthenticator = new GoogleAuthenticator();
        this.userRepository = userRepository;
    }

    public String generateSecret() {

        GoogleAuthenticatorKey key =
                googleAuthenticator.createCredentials();

        return key.getKey();
    }

    public int generateOtp(String secret) {

        return googleAuthenticator.getTotpPassword(secret);
    }

    public boolean verifyOtp(String secret, int otp) {

        return googleAuthenticator.authorize(secret, otp);
    }

    public String generateOtpAuthUri(
            String email,
            String secret) {

        return "otpauth://totp/"
                + "OAuthPractice:"
                + email
                + "?secret="
                + secret
                + "&issuer=OAuthPractice";
    }

    public String setupTwoFactor(User user) {

        String secret = user.getTwoFactorSecret();

        if (secret == null || secret.isBlank()) {

            secret = generateSecret();

            user.setTwoFactorSecret(secret);
            user.setTwoFactorEnabled(false);

            userRepository.save(user);
        }

        return generateOtpAuthUri(
                user.getEmail(),
                secret
        );
    }
}