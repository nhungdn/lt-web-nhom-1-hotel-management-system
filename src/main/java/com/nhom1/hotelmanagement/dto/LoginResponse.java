package com.nhom1.hotelmanagement.dto;

import com.nhom1.hotelmanagement.entities.User.Role;

public class LoginResponse {
    private Long userId;
    private String username;
    private String fullName;
    private Role role;

    public LoginResponse(Long userId, String username, String fullName, Role role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
    }

    public Long getUserId() {
        return userId;
    }

    public String getUsername() {
        return username;
    }

    public String getFullName() {
        return fullName;
    }

    public Role getRole() {
        return role;
    }
}