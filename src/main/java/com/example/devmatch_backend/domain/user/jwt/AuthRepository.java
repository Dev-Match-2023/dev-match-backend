package com.example.devmatch_backend.domain.user.jwt;

public interface AuthRepository {


    void saveToken(String uuid,String phoneNumber);

    String findToken(String uuid);

    void deleteToken(String uuid);


}