package com.example.demo.roles.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.roles.model.entity.Role;

public interface RoleRepository extends JpaRepository<Role, UUID> {
    java.util.Optional<Role> findByCode(String code);
}
