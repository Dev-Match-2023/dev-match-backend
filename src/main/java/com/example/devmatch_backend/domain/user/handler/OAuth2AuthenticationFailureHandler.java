package com.example.devmatch_backend.domain.user.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationFailureHandler;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationFailureHandler extends SimpleUrlAuthenticationFailureHandler {

    @Override
    public void onAuthenticationFailure(HttpServletRequest request, HttpServletResponse response, AuthenticationException authenticationException) throws IOException {

        System.out.println(authenticationException);
        String encodedErrorMessage = URLEncoder.encode(authenticationException.getLocalizedMessage(), StandardCharsets.UTF_8);
        String targetUrl = "/oauth2/redirect?error=" + encodedErrorMessage;

        getRedirectStrategy().sendRedirect(request, response, targetUrl);
    }
}