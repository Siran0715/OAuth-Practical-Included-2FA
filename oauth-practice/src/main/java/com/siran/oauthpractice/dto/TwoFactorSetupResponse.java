package com.siran.oauthpractice.dto;

public class TwoFactorSetupResponse {

    private String qrcode;
    private String secret;

    public TwoFactorSetupResponse(String qrcode, String secret) {
        this.qrcode = qrcode;
        this.secret = secret;
    }

    public String getQrcode() {
        return qrcode;
    }

    public String getSecret() {
        return secret;
    }
}
