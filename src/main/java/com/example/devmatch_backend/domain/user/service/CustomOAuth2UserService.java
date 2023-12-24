package com.example.devmatch_backend.domain.user.service;

import com.example.devmatch_backend.domain.user.dto.OAuthAttributes;
import com.example.devmatch_backend.domain.user.entity.User;
import com.example.devmatch_backend.domain.user.repository.UserRepository;
import jakarta.servlet.http.HttpSession;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.client.userinfo.DefaultOAuth2UserService;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserRequest;
import org.springframework.security.oauth2.client.userinfo.OAuth2UserService;
import org.springframework.security.oauth2.core.OAuth2AuthenticationException;
import org.springframework.security.oauth2.core.user.DefaultOAuth2User;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.stream.Collectors;
@RequiredArgsConstructor
@Service
public class CustomOAuth2UserService implements OAuth2UserService<OAuth2UserRequest, OAuth2User> {
    private final UserRepository userRepository;
    private final HttpSession httpSession;

    @Override
    public OAuth2User loadUser(OAuth2UserRequest userRequest) throws OAuth2AuthenticationException {
        OAuth2UserService<OAuth2UserRequest, OAuth2User> delegate = new DefaultOAuth2UserService();
        OAuth2User oAuth2User = delegate.loadUser(userRequest);

        String registrationId = userRequest.getClientRegistration().getRegistrationId();
        String userNameAttributeName = userRequest.getClientRegistration().getProviderDetails().getUserInfoEndpoint().getUserNameAttributeName();

        OAuthAttributes attributes = OAuthAttributes.of(registrationId, userNameAttributeName, oAuth2User.getAttributes());

        User user = saveOrUpdate(attributes);

        return new DefaultOAuth2User(
                user.getAuthorities().stream()
                        .map(authority -> new SimpleGrantedAuthority(authority.getAuthorityName()))
                        .collect(Collectors.toSet()),
                attributes.getAttributes(),
                attributes.getNameAttributeKey());
    }

    private User saveOrUpdate(OAuthAttributes attributes) {

        Optional<User> user = null;
        
        if (attributes.getType().equals("kakao")){
            user = userRepository.findByOauthIdAndOauthTypeAndActiveIsTrue(attributes.getOauthId() , "kakao");
        }
        else if (attributes.getType().equals("google")){
            user = userRepository.findByOauthIdAndOauthTypeAndActiveIsTrue(attributes.getOauthId() , "google");
        }
        if (!user.isPresent()) {
            return userRepository.save(attributes.toEntity());
        }
        return user.get();
    }
}