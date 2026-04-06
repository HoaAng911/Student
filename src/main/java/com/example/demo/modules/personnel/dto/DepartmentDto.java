package com.example.demo.modules.personnel.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DepartmentDto {
    private UUID id;
    private String code;
    private String name;
    private String description;
    private LocalDate establishedYear;

    // Giữ lại trạng thái để client biết phòng ban này có đang hoạt động hay không
    private Boolean isActive;
}