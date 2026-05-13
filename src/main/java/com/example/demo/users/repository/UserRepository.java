package com.example.demo.users.repository;

import java.util.UUID;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.users.model.entity.User;

public interface UserRepository extends JpaRepository<User, UUID> {
    java.util.Optional<User> findByUsernameAndIsActiveTrue(String username);
}
