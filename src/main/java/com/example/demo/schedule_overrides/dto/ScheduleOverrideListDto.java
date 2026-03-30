package com.example.demo.schedule_overrides.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleOverrideListDto {

    private UUID id;
    private String overrideCode;
    private UUID roomId;
    private String roomCode;
    private String roomName;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private String reason;
    private String status;
    private Boolean isActive;
    private Boolean isBlocked;
    private LocalDateTime createdAt;
}
