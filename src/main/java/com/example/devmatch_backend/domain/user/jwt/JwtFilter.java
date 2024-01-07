package com.example.devmatch_backend.domain.user.jwt;

import com.example.devmatch_backend.domain.user.entity.User;
import com.example.devmatch_backend.domain.user.repository.UserRepository;
import com.example.devmatch_backend.exception.CustomException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

import static com.example.devmatch_backend.exception.ErrorCode.INVALID_TOKEN;


@Slf4j
@RequiredArgsConstructor
public class JwtFilter extends OncePerRequestFilter {
    private final TokenProvider tokenProvider;
    private final UserRepository userRepository;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain) throws ServletException, IOException {

        // 토큰의 인증 정보를 Security Context에 저장하는 역할 수행

        // 현재 요청과 관련된 고유한 식별자
        String traceId = (String) request.getAttribute("traceId");

        try {
            String jwt = tokenProvider.resolveToken(request);
            Long userId = 0L;

            tokenProvider.validate(jwt);
            if (jwt != null) {
                Authentication authentication = tokenProvider.resolveFrom(jwt);
                System.out.println(authentication);
                SecurityContextHolder.getContext().setAuthentication(authentication);

                log.info("valid authentication: {}, uri: {}", authentication,
                        request.getRequestURI());

                if (authentication == null) {
                    log.info("no authentication info found");
                }

                Object principal = authentication.getPrincipal(); // 사용자의 식별 정보를 추출
                if (principal instanceof UserDetails) {
                    User user = userRepository.findByOauthIdAndActiveIsTrue(authentication.getName())
                            .orElseThrow(
                                    () -> new CustomException(INVALID_TOKEN, String.format("'%s' not found", authentication.getName())));
                    userId = user.getUserId();

                }


            } else {
                throw new CustomException(INVALID_TOKEN, "token is invalid in jwt filter");
            }

            // set userId in request
            request.setAttribute("id", userId);
        } catch (Exception e) {
            throw new CustomException(INVALID_TOKEN, e.getMessage());
        }

        chain.doFilter(request, response);

    }


}