package com.example.devmatch_backend.domain.user.dto;

import com.example.devmatch_backend.domain.user.entity.Authority;
import com.example.devmatch_backend.domain.user.entity.User;
import lombok.Builder;
import lombok.Getter;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Getter
public class OAuthAttributes {
    private Map<String, Object> attributes;
    private String nameAttributeKey;
    private String oauthId;
    private String name;
    private String profile_image;
    private String type;

    @Builder
    public OAuthAttributes(Map<String, Object> attributes, String nameAttributeKey, String name, String oauthId, String profile_image, String type) {
        this.attributes = attributes;
        this.nameAttributeKey = nameAttributeKey;
        this.name = name;
        this.oauthId = oauthId;
        this.profile_image = profile_image;
        this.type = type;
    }

    public static OAuthAttributes of(String socialName, String userNameAttributeName, Map<String, Object> attributes) {
        // 카카오
        if ("kakao".equals(socialName)) {
            return ofKakao(userNameAttributeName, attributes);
        }
        // 구글
        if ("google".equals(socialName)) {
            return ofGoogle(userNameAttributeName, attributes);
        }
        return null;
    }

    private static OAuthAttributes ofKakao(String userNameAttributeName, Map<String, Object> attributes) {

        System.out.println(attributes);
        Map<String, Object> kakaoAccount = (Map<String, Object>) attributes.get("kakao_account");
        Map<String, Object> kakaoProfile = (Map<String, Object>) kakaoAccount.get("profile");

        return OAuthAttributes.builder()
                .name((String) kakaoAccount.get("name"))
                .oauthId((String) attributes.get("id").toString())
                .profile_image((String) kakaoProfile.get("profile_image_url"))
                .nameAttributeKey(userNameAttributeName)
                .attributes(attributes)
                .type("kakao")
                .build();
    }

    private static OAuthAttributes ofGoogle(String userNameAttributeName,Map<String, Object> attributes ){

        return OAuthAttributes.builder()
                .name((String) attributes.get("name"))
                .oauthId((String)attributes.get("sub"))
                .profile_image((String)attributes.get("picture"))
                .attributes(attributes)
                .nameAttributeKey(userNameAttributeName)
                .type("google")
                .build();
    }

    public User toEntity() {
        Set<Authority> authoritiesSet = new HashSet<>();
        authoritiesSet.add(Authority.of("ROLE_USER"));
        return User.builder()
                .oauthId(oauthId)
                .name(name)
                .active(true)
                .profileImage(profile_image)
                .authorities(authoritiesSet)
                .oauthType(type)
                .build();
    }
}
