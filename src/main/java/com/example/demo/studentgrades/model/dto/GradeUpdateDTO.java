package com.example.demo.studentgrades.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.UUID;

@Data
public class GradeUpdateDTO {
    private UUID registrationId;
    private UUID gradeComponentId;
    private BigDecimal score;
}
