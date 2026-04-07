package com.example.demo.permissions.repository;

import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.permissions.model.entity.Permission;

public interface PermissionRepository extends JpaRepository<Permission, UUID> {
}
