package com.example.demo.modules.personnel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class EmployeeDto {
    private UUID id;
    private String employeeCode;
    private String fullName;
    private LocalDate dateOfBirth;
    private String gender;
    private String email;
    private String phone;
    private String address;

    // Mapping phòng ban
    private UUID departmentId;
    private String departmentName; // Phục vụ hiển thị trên UI

    // Mapping chức vụ
    private UUID positionId;
    private String positionName;   // Phục vụ hiển thị trên UI

    private LocalDate hireDate;
    private String contractType;
    private BigDecimal salaryCoefficient;
    private String academicDegree;
    private String academicTitle;
    private String specialization;
    private LocalDateTime startDate;
    private LocalDateTime endDate;

    private Boolean isActive;
}