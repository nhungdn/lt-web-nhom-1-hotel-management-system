package com.nhom1.hotelmanagement.config;

import com.nhom1.hotelmanagement.dto.LoginResponse;
import com.nhom1.hotelmanagement.entities.User;
import com.nhom1.hotelmanagement.repositories.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;
import org.springframework.security.web.csrf.CsrfTokenRequestAttributeHandler;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    private final UserDetailsService userDetailsService;
    private final UserRepository userRepository;

    public SecurityConfig(UserDetailsService userDetailsService, UserRepository userRepository) {
        this.userDetailsService = userDetailsService;
        this.userRepository = userRepository;
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf
                        // 1. Lưu Token vào Cookie và cho phép JS đọc (HttpOnly = false)
                        .csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
                        // 2. Ép Spring gửi Token về ngay từ request GET đầu tiên
                        .csrfTokenRequestHandler(new CsrfTokenRequestAttributeHandler()))
                // .csrf(csrf -> {
                // })
                .authorizeHttpRequests(auth -> auth
                        // Static resources & Public pages
                        .requestMatchers("/", "/login", "/css/**", "/js/**", "/images/**").permitAll()
                        .requestMatchers("/roomtypes", "/roomtypes/*/images", "/roomtypeimages/api/**")
                        .permitAll()
                        .requestMatchers("/services/api/all", "/filter/**", "/book").permitAll()
                        // Role-based access
                        .requestMatchers("/users/**").hasRole("ADMIN")

                        // Authenticated users
                        .requestMatchers("/roomtypes/**", "/rooms/**", "/booking/**", "/profile/**").authenticated()

                        .anyRequest().authenticated())
                .formLogin(form -> form
                        .loginPage("/login")
                        .loginProcessingUrl("/login")
                        .successHandler(successHandler())
                        .failureUrl("/login?error=true")
                        .permitAll())
                .logout(logout -> logout
                        .logoutUrl("/logout")
                        .logoutSuccessUrl("/login?logout=true")
                        .invalidateHttpSession(true)
                        .deleteCookies("JSESSIONID"))
                .userDetailsService(userDetailsService);

        return http.build();
    }

    @Bean
    public AuthenticationSuccessHandler successHandler() {
        return (request, response, authentication) -> {
            System.out.println("Dang nhap thanh cong cho user: " + authentication.getName());
            String username = authentication.getName();
            User user = userRepository.findByUsername(username);
            if (user != null) {
                request.getSession().setAttribute("user", new LoginResponse(
                        user.getUserId(),
                        user.getUsername(),
                        user.getFullName(),
                        user.getRole()));
                response.sendRedirect("/dashboard");
            } else {
                response.sendRedirect("/login?error=true");
            }
        };
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}