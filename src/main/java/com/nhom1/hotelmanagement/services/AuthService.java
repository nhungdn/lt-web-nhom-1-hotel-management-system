package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.LoginRequest;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    public LoginResponse login(LoginRequest request) {

        User user = userRepository.findByUsername(request.getUsername());

        if (user == null) {
            return null;
        }

        if (!user.getPassword().equals(request.getPassword())) {
            return null;
        }

        // map Entity -> DTO
        return new LoginResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole());
    }
}