package com.example.accountrequest.dto;

import com.example.accountrequest.entity.Role;

public class UserResponseDto {

    private final Long id;
    private final String name;
    private final String email;
    private final Role role;

    public UserResponseDto(Long id, String name, String email, Role role) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.role = role;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    public Role getRole() {
        return role;
    }
}