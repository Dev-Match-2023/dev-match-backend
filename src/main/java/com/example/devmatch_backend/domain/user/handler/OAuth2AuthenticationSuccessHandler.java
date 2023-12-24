package com.example.devmatch_backend.domain.user.handler;

import com.example.devmatch_backend.domain.user.dto.TokenResponse;
import com.example.devmatch_backend.domain.user.jwt.TokenProvider;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.util.UriComponentsBuilder;

import java.io.IOException;


@Slf4j
@Component
@RequiredArgsConstructor
public class OAuth2AuthenticationSuccessHandler extends SimpleUrlAuthenticationSuccessHandler {

    private final TokenProvider tokenProvider;

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
                                        Authentication authentication) throws IOException {

        String targetUrl = determineTargetUrl(request, response, authentication);

        if (response.isCommitted()) {
            log.debug("Response has already been committed.");
            return;
        }

        clearAuthenticationAttributes(request);
        getRedirectStrategy().sendRedirect(request, response, targetUrl);

    }


    protected String determineTargetUrl(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {

        //JWT 생성
        TokenResponse jwt = tokenProvider.createFrom(authentication);

        return UriComponentsBuilder.fromUriString("/oauth2/redirect")
                .queryParam("accessToken", jwt.getAccessToken())
                .queryParam("refreshToken", jwt.getRefreshToken())
                .build().toUriString();
    }

}
