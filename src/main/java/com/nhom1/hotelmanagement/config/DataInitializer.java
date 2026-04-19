package com.nhom1.hotelmanagement.config;

import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.repositories.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) throws Exception {
        // Kiểm tra nếu chưa có tài khoản sys_admin thì mới tạo
        if (userRepository.findByUsername("sys_admin") == null) {
            User admin = new User();
            admin.setUsername("sys_admin");
            // Dùng chính passwordEncoder của hệ thống để mã hóa
            admin.setPassword(passwordEncoder.encode("123456"));
            admin.setFullName("Sys Admin");

            // Set role là ADMIN
            admin.setRole(User.Role.ADMIN);

            userRepository.save(admin);
            System.out.println(">>> ĐÃ TẠO TÀI KHOẢN SYS_ADMIN THÀNH CÔNG!");
        } else {
            // Nếu đã có rồi nhưng mật khẩu sai, hãy cập nhật lại mật khẩu mới
            User existingAdmin = userRepository.findByUsername("sys_admin");
            existingAdmin.setPassword(passwordEncoder.encode("123456"));
            userRepository.save(existingAdmin);
            System.out.println(">>> ĐÃ CẬP NHẬT LẠI MẬT KHẨU CHO SYS_ADMIN!");
        }
    }
}