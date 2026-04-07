package com.example.demo.roles.controller;

import java.util.UUID;

import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.roles.model.entity.Role;
import com.example.demo.roles.service.RoleService;

import io.swagger.v3.oas.annotations.parameters.RequestBody;

@RestController
@RequestMapping("/api/roles")
public class RoleApiController {

    private final RoleService roleService;

    public RoleApiController(RoleService roleService) {
        this.roleService = roleService;
    }

    @PostMapping
    public Role create(@RequestBody Role role) {
        return roleService.createRole(role);
    }

    @PostMapping("/{roleId}/permissions/{permId}")
    public Role addPermission(@PathVariable UUID roleId,
                              @PathVariable UUID permId) {
        return roleService.addPermission(roleId, permId);
    }
}