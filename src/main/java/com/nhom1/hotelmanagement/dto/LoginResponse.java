package com.nhom1.hotelmanagement.dto;

import com.nhom1.hotelmanagement.entities.User.Role;

public class LoginResponse {
    private Long userId;
    private String username;
    private String fullName;
    private Role role;
    private String accesstoken;
    private String refreshtoken;

    public LoginResponse(Long userId, String username, String fullName, Role role) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.accesstoken = null;
        this.refreshtoken = null;
    }

    public LoginResponse(Long userId, String username, String fullName, Role role, String accesstoken,
            String refreshtoken) {
        this.userId = userId;
        this.username = username;
        this.fullName = fullName;
        this.role = role;
        this.accesstoken = accesstoken;
        this.refreshtoken = refreshtoken;
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

    public String getAccesstoken() {
        return accesstoken;
    }

    public String getRefreshtoken() {
        return refreshtoken;
    }
}