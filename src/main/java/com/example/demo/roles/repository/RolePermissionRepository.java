package com.example.demo.roles.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.roles.model.entity.RolePermission;
import com.example.demo.roles.model.entity.RolePermissionId;

import java.util.UUID;
import java.util.List;

public interface RolePermissionRepository 
        extends JpaRepository<RolePermission, RolePermissionId> {

    List<RolePermission> findByRoleId(UUID roleId);

    void deleteByRoleIdAndPermissionId(UUID roleId, UUID permissionId);
}