package com.example.devmatch_backend.domain.user.jwt;


import org.springframework.stereotype.Repository;

import java.util.HashMap;
import java.util.Map;

@Repository
public class MemoryAuthRepository implements AuthRepository {
    private static final Map<String,String> store = new HashMap<>();

    @Override
    public void saveToken(String uuid,String token) {
        store.put(uuid,token);
    }

    @Override
    public String findToken(String uuid) {
        return store.get(uuid);
    }

    @Override
    public void deleteToken(String uuid) {
        store.remove(uuid);
    }
}