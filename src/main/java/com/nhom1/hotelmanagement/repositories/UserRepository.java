package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);
}