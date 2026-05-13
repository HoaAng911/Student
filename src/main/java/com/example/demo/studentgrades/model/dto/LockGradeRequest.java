package com.example.demo.studentgrades.model.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class LockGradeRequest {
    private UUID componentId;
    private UUID lecturerId;
    private boolean lock;
}
