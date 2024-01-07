package com.example.devmatch_backend.domain.user.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Data
@Table(name = "users")
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long userId;

    @Column(name = "user_type")
    private String userType;

    private String nickname;

    private String name;

    @Column(name = "oauth_id")
    private String oauthId;

    @Column(name = "active", columnDefinition = "boolean default true")
    private Boolean active;

    @Column(name = "last_login_date")
    private Date lastLoginDate;

    @Column(name = "profile_image")
    private String profileImage;

    @Column(name = "last_profile_update")
    private Date lastProfileUpdate;

    @Column(name = "fcm_token")
    private String fcmToken;

    @Column(name = "oauth_type")
    private String oauthType;


    @Column(name = "marketing_agreement")
    private Boolean marketingAgreement;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_authority",
            joinColumns = {
                    @JoinColumn(name = "user_id", referencedColumnName = "user_id")
            },
            inverseJoinColumns = {
                    @JoinColumn(name = "authority_name", referencedColumnName = "authority_name")
            }
    )
    private Set<Authority> authorities;

    @Builder
    public User(String nickname, String name, String oauthId, Boolean active, Date lastLoginDate, String profileImage, Date lastProfileUpdate, String fcmToken, Boolean marketingAgreement, Set<Authority> authorities,  String oauthType) {
        this.nickname = nickname;
        this.name = name;
        this.oauthId = oauthId;
        this.active = active;
        this.lastLoginDate = lastLoginDate;
        this.profileImage = profileImage;
        this.lastProfileUpdate = lastProfileUpdate;
        this.fcmToken = fcmToken;
        this.marketingAgreement = marketingAgreement;
        this.authorities = authorities;
        this.oauthType = oauthType;
    }


}
