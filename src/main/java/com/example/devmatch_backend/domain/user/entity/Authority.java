package com.example.devmatch_backend.domain.user.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "authority")
@Data
@NoArgsConstructor
public class Authority {

    @Id
    private String authorityName;

    public static Authority of(String authorityName) {
        Authority authority = new Authority();
        authority.authorityName = authorityName;
        return authority;
    }
}