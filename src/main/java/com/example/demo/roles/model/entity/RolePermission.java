package com.example.demo.roles.model.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "role_permissions")
@IdClass(RolePermissionId.class)
public class RolePermission {

    @Id
    @Column(name = "role_id")
    private UUID roleId;

    @Id
    @Column(name = "permission_id")
    private UUID permissionId;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @Column(name = "is_active")
    private Boolean isActive;
    
    public RolePermission() {}
    public RolePermission(UUID roleId, UUID permissionId) {
        this.roleId = roleId;
        this.permissionId = permissionId;
    }

    public RolePermission(UUID roleId, UUID permissionId, LocalDateTime createdAt, LocalDateTime updatedAt,
            UUID createdBy, UUID updatedBy, LocalDateTime deletedAt, UUID deletedBy, Boolean isActive) {
        this.roleId = roleId;
        this.permissionId = permissionId;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.createdBy = createdBy;
        this.updatedBy = updatedBy;
        this.deletedAt = deletedAt;
        this.deletedBy = deletedBy;
        this.isActive = isActive;
    }

    public UUID getRoleId() {
        return roleId;
    }
    public void setRoleId(UUID roleId) {
        this.roleId = roleId;
    }
    public UUID getPermissionId() {
        return permissionId;
    }
    public void setPermissionId(UUID permissionId) {
        this.permissionId = permissionId;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    public UUID getCreatedBy() {
        return createdBy;
    }
    public void setCreatedBy(UUID createdBy) {
        this.createdBy = createdBy;
    }
    public UUID getUpdatedBy() {
        return updatedBy;
    }
    public void setUpdatedBy(UUID updatedBy) {
        this.updatedBy = updatedBy;
    }
    public LocalDateTime getDeletedAt() {
        return deletedAt;
    }
    public void setDeletedAt(LocalDateTime deletedAt) {
        this.deletedAt = deletedAt;
    }
    public UUID getDeletedBy() {
        return deletedBy;
    }
    public void setDeletedBy(UUID deletedBy) {
        this.deletedBy = deletedBy;
    }
    public Boolean getIsActive() {
        return isActive;
    }
    public void setIsActive(Boolean isActive) {
        this.isActive = isActive;
    }

    // ===== Getter Setter =====
    
}