package com.example.demo.students.model.entity;

import jakarta.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;
import org.hibernate.annotations.UuidGenerator;
import lombok.*;

/**
 * Entity đại diện cho bảng Sinh viên (students).
 * Phản ánh chính xác schema quản lý sinh viên chi tiết.
 */
@Entity
@Table(name = "students")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
@org.hibernate.annotations.Where(clause = "is_active = 1")
public class Student {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID userId;

    @Column(name = "code", length = 100, unique = true)
    private String code; // Mã số sinh viên (Duy nhất)

    @Column(name = "full_name", length = 255, columnDefinition = "nvarchar(255)")
    private String fullname; // Họ và tên sinh viên

    @Column(name = "date_of_birth")
    private LocalDate date_of_birth; // Ngày sinh (Kiểu DATE)

    @Column(name = "gender", length = 10, columnDefinition = "nvarchar(10)")
    private String gender; // 1: Nam, 2: Nữ, 0: Khác

    @Column(name = "personal_identification_number", length = 20)
    private String personal_identification_number; // CMND/CCCD

    @Column(name = "date_of_issue")
    private LocalDate date_of_issue; // Ngày cấp CMND/CCCD

    @Column(name = "card_place", length = 100, columnDefinition = "nvarchar(100)")
    private String card_place; // Nơi cấp CCCD

    @Column(name = "address", length = 300, columnDefinition = "nvarchar(300)")
    private String address; // Địa chỉ thường trú

    @Column(name = "current_address", length = 300, columnDefinition = "nvarchar(300)")
    private String current_address; // Địa chỉ hiện tại

    @Column(name = "academic_year_year", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID academic_year_year; // FK -> academic_years

    @Column(name = "department_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID department_id; // FK -> departments

    @Column(name = "major_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID major_id; // FK -> majors

    @Column(name = "training_program_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID training_program_id; // FK -> training_programs

    @Column(name = "status", length = 50)
    private String status; // Trạng thái hiện tại

    @Column(name = "student_classe_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID student_classe_id; // FK -> student_classess

    @Column(name = "admission_year")
    private LocalDateTime admission_year; // Ngày tháng nhập học (DATETIME2)

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID createdBy;

    @Column(name = "updated_by", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID deletedBy;

    @Column(name = "is_active")
    @Builder.Default
    private Boolean isActive = true;

    // --- JPA Lifecycle Hooks ---
    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }

    public void setName(String name) {
        this.fullname = name;
    }
}

