package com.example.demo.studentgrades.model.dto;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

@Data
public class BulkGradeRequest {
    private UUID componentId;
    private Map<UUID, BigDecimal> scores;
    private UUID lecturerId;
}
