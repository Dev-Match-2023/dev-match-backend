package com.example.devmatch_backend.domain.user.dto;

import lombok.*;

@Data
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Builder
public class TokenResponse {

    private String accessToken;
    private String refreshToken;

}