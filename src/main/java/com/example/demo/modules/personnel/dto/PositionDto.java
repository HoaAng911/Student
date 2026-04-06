package com.example.demo.modules.personnel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PositionDto {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private String level;

    // Thay vì map cả Object Department, ta chỉ dùng ID để nhận dữ liệu từ Client (khi Create/Update)
    private UUID departmentId;

    // Thường dùng khi trả dữ liệu về (Read) để Client in ra màn hình dễ dàng
    private String departmentName;

    private Boolean isActive;
}