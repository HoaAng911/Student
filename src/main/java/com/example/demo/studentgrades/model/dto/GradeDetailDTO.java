package com.example.demo.studentgrades.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GradeDetailDTO {
    private UUID gradeComponentId;
    private String componentCode;
    private String componentName;
    private BigDecimal score;
    private Boolean isLocked;
    private String status;
}
