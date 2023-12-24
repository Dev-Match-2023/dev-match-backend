package com.example.devmatch_backend.domain.user.dto;

import lombok.Data;

import java.util.Date;

@Data
public class UserResponseDto {

    private Long userId;

    private String nickname;

    private String email;

    private Boolean active;

    private Date lastLoginDate;

    private String profileImage;

}
