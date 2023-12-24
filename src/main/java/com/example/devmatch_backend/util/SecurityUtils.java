package com.example.devmatch_backend.util;

import com.example.devmatch_backend.domain.user.entity.User;
import com.example.devmatch_backend.domain.user.repository.UserRepository;
import com.example.devmatch_backend.exception.CustomException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.Optional;

import static com.example.devmatch_backend.exception.ErrorCode.MEMBER_NOT_FOUND;


/*
 Spring Security와 함께 사용할 때 현재 사용자의 이름을 가져오는 유틸리티 클래스인 SecurityUtils를 정의
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class SecurityUtils {

    private final UserRepository userRepository;
    public static Optional<String> getCurrentUsername() {

        // Spring Security의 SecurityContextHolder 클래스를 사용하여 현재 인증 된 사용자의 정보를 가져옴
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null) {
            log.info("no authentication info found");
            return Optional.empty();
        }

        Object principal = authentication.getPrincipal(); // 사용자의 식별 정보를 추출
        if (principal instanceof UserDetails) {
            UserDetails userDetails = (UserDetails) principal;
            return Optional.ofNullable(userDetails.getUsername());
        }
        if (principal instanceof String) {
            return Optional.of(principal.toString());
        }

        throw new IllegalStateException("invalid authentication");
    }

    public Long getCurrentUserId() {
        Optional<String> currentUsername = getCurrentUsername();
        if (!currentUsername.isPresent()) {
            throw new CustomException(MEMBER_NOT_FOUND, "User not found");
        }
        Optional<User> user = userRepository.findByOauthIdAndActiveIsTrue(currentUsername.get());
        if (user.isPresent()) {
            return user.get().getUserId();
        } else {
            throw new CustomException(MEMBER_NOT_FOUND, "User not found");
        }
    }

    public boolean checkAuthor(Long authorId) {
        Long currentUserId = getCurrentUserId();

        return currentUserId.equals(authorId);
    }
}
