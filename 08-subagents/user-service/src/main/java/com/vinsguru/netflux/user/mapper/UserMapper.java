package com.vinsguru.netflux.user.mapper;

import com.vinsguru.netflux.user.dto.RegisterRequest;
import com.vinsguru.netflux.user.entity.User;

public class UserMapper {

    private UserMapper() {
    }

    public static User toEntity(RegisterRequest request, String hashedPassword) {
        var user = new User();
        user.setUsername(request.username());
        user.setPassword(hashedPassword);
        user.setName(request.name());
        return user;
    }

}
