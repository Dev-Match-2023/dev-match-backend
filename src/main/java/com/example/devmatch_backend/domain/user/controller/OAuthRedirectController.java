package com.example.devmatch_backend.domain.user.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class OAuthRedirectController {

    @GetMapping("/oauth2/redirect")
    public String handleOAuth2Redirect() {
        return "";
    }
}