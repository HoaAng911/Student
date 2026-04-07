package com.example.demo.roles.service;

import java.util.UUID;

import org.springframework.stereotype.Service;

import com.example.demo.permissions.model.entity.Permission;
import com.example.demo.permissions.repository.PermissionRepository;
import com.example.demo.roles.model.entity.Role;
import com.example.demo.roles.repository.RoleRepository;

@Service
public class RoleService {

    private final RoleRepository roleRepo;

    private final PermissionRepository permRepo;

    public RoleService(RoleRepository roleRepo, PermissionRepository permRepo) {
        this.roleRepo = roleRepo;
        this.permRepo = permRepo;
    }

    public Role createRole(Role role) {
        return roleRepo.save(role);
    }

    public Role addPermission(UUID roleId, UUID permId) {
        Role r = roleRepo.findById(roleId).orElseThrow();
        Permission p = permRepo.findById(permId).orElseThrow();
        r.getPermissions().add(p);
        return roleRepo.save(r);
    }
}