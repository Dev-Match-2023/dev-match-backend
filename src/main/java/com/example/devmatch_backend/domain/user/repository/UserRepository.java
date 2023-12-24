package com.example.devmatch_backend.domain.user.repository;


import com.example.devmatch_backend.domain.user.entity.User;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;


public interface UserRepository extends JpaRepository<User, Long> {
    @EntityGraph(attributePaths = "authorities")
    Optional<User> findOneWithAuthoritiesByUserId(Integer userId);

    Optional<User> findByOauthIdAndActiveIsTrue(String oauthId);

    Optional<User> findByOauthIdAndOauthTypeAndActiveIsTrue(String oauthId, String oauthType);

    Optional<User> findByUserId(Long userId);

}