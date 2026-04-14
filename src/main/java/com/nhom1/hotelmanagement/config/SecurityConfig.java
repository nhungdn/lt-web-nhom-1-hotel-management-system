package com.nhom1.hotelmanagement.config;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
public class SecurityConfig {

    private final UserRepository userRepository;
    private final UserDetailsService userDetailsService;

    public SecurityConfig(UserRepository userRepository, UserDetailsService userDetailsService) {
        this.userRepository = userRepository;
        this.userDetailsService = userDetailsService;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/login", "/css/**", "/js/**", "/images/**", "/signup").permitAll()
                    .requestMatchers("/users/**").hasRole("ADMIN") // Chi ADMIN moi duoc truy cap /users
                        .anyRequest().authenticated()) // Bat buoc dang nhap voi moi request khac
                .userDetailsService(userDetailsService)
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login") // POST /login de thuc hien dang nhap
                        .successHandler((request, response, authentication) -> {
                            String username = authentication.getName();
                            User user = userRepository.findByUsername(username);

                            if (user != null) {
                                request.getSession().setAttribute("user", new LoginResponse(
                                        user.getUserId(),
                                        user.getUsername(),
                                        user.getFullName(),
                                        user.getRole()));
                            }

                            response.sendRedirect("/");
                        })
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"));

        return http.build();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}