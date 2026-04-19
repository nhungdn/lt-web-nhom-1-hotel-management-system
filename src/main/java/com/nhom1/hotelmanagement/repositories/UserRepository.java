package com.nhom1.hotelmanagement.repositories;

import com.nhom1.hotelmanagement.entities.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    User findByUsername(String username);

    java.util.List<User> findByUsernameContainingIgnoreCaseOrderByUsernameAsc(String username);

    User findByUserId(Long userId);
}