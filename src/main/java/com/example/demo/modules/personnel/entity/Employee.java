package com.example.demo.modules.personnel.entity;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "employees")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Employee extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "user_id")
    private UUID userId; // Liên kết với bảng User

    @Column(name = "employee_code", length = 20, nullable = false, unique = true)
    private String employeeCode;

    @Column(name = "full_name", length = 100, nullable = false)
    private String fullName;

    @Column(name = "date_of_birth")
    private LocalDate dateOfBirth;

    // Giới tính (1: Nam, 2: Nữ, 0: Khác). Database bạn thiết kế là NVARCHAR(50) nên
    // map là String
    @Column(name = "gender", length = 50)
    private String gender;

    @Column(name = "email", length = 100)
    private String email;

    @Column(name = "phone", length = 20)
    private String phone;

    @Column(name = "address", length = 255)
    private String address;

    // Phòng ban / Khoa
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "department_id")
    private Department department;

    // Chức vụ (Đã đổi sang join với bảng Position thay vì NVARCHAR)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "position_id")
    private Position position;

    @Column(name = "hire_date")
    private LocalDate hireDate;

    @Column(name = "contract_type", length = 255)
    private String contractType;

    @Column(name = "salary_coefficient", precision = 4, scale = 2)
    private BigDecimal salaryCoefficient;

    @Column(name = "academic_degree", length = 100)
    private String academicDegree; // Học vị (ThS, TS)

    @Column(name = "academic_title", length = 100)
    private String academicTitle; // Học hàm (GS, PGS)

    @Column(name = "specialization", length = 255)
    private String specialization; // Chuyên môn

    @Column(name = "start_date")
    private LocalDateTime startDate;

    @Column(name = "end_date")
    private LocalDateTime endDate; // NULL nếu chưa kết thúc
}