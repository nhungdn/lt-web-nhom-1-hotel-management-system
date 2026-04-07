package com.nhom1.hotelmanagement.services;

import com.nhom1.hotelmanagement.dto.LoginRequest;
import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.dto.SignUpRequest;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public LoginResponse login(LoginRequest request) {
        User user = userRepository.findByUsername(request.getUsername());

        if (user == null) {
            return null;
        }

        if (!passwordEncoder.matches(request.getPassword(), user.getPassword())) {
            return null;
        }

        // map Entity -> DTO
        return new LoginResponse(
                user.getUserId(),
                user.getUsername(),
                user.getFullName(),
                user.getRole());
    }

    public void signup(SignUpRequest request) {
        if (request == null || request.getUsername() == null || request.getPassword() == null) {
            throw new IllegalArgumentException("Thong tin dang ky khong hop le");
        }

        if (request.getUsername().isBlank() || request.getPassword().isBlank() || request.getConfirmPassword() == null
                || request.getConfirmPassword().isBlank()) {
            throw new IllegalArgumentException("Vui long nhap day du thong tin bat buoc");
        }

        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new IllegalArgumentException("Mat khau xac nhan khong khop");
        }

        if (userRepository.findByUsername(request.getUsername()) != null) {
            throw new IllegalArgumentException("Tai khoan da ton tai");
        }

        User user = new User();
        user.setUsername(request.getUsername());
        user.setPassword(passwordEncoder.encode(request.getPassword()));
        user.setFullName(request.getFullname());
        user.setPhoneNumber(request.getPhoneNumber());

        User.Role role = User.Role.STAFF;
        if (request.getRole() != null && !request.getRole().isBlank()) {
            try {
                role = User.Role.valueOf(request.getRole().trim().toUpperCase());
            } catch (IllegalArgumentException ex) {
                throw new IllegalArgumentException("Role khong hop le");
            }
        }
        user.setRole(role);

        userRepository.save(user);
    }
}