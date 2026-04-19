package com.nhom1.hotelmanagement.services;

import java.util.List;

import org.springframework.stereotype.Service;

import com.nhom1.hotelmanagement.dto.ProfileUpdateRequest;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.util.StringUtils;

@Service
public class UserService {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public List<User> getAllUsers() {
        return userRepository.findAll();
    }

    public User getUserByUsername(String username) {
        return userRepository.findByUsername(username);
    }

    public User getUserById(Long id) {
        return userRepository.findById(id).orElse(null);
    }

    public void createUser(User user) {
        userRepository.save(user);
    }

    public void updateUser(Long id, String fullName, String phoneNumber, User.Role role) {
        User user = getUserById(id);
        if (user != null) {
            user.setFullName(fullName);
            user.setPhoneNumber(phoneNumber);
            user.setRole(role);
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
    }

    public void deleteUser(Long id) {
        User user = getUserById(id);
        if (user != null) {
            userRepository.delete(user);
        } else {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
    }

    public void resetToDefaultPassword(Long id) {
        User user = getUserById(id);
        if (user != null) {
            user.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(user);
        } else {
            throw new IllegalArgumentException("User not found with id: " + id);
        }
    }

    public void updateProfile(String username, ProfileUpdateRequest request) {
        User user = getUserByUsername(username);
        if (user == null)
            throw new RuntimeException("user_not_found");

        // Kiểm tra mật khẩu hiện tại
        if (!passwordEncoder.matches(request.getCurrentPassword(), user.getPassword())) {
            throw new RuntimeException("current_password_invalid");
        }

        // Cập nhật thông tin cơ bản
        user.setFullName(request.getFullName());
        user.setPhoneNumber(request.getPhoneNumber());

        // Kiểm tra và cập nhật mật khẩu mới (nếu có)
        if (StringUtils.hasText(request.getNewPassword())) {
            if (!request.getNewPassword().equals(request.getConfirmPassword())) {
                throw new RuntimeException("password_mismatch");
            }
            user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        }

        userRepository.save(user);
    }
}
