package com.example.demo.room_block_times.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomBlockTimeDto {
    private UUID blockId;
    private UUID roomId;
    private String blockType;
    private Integer dayOfWeek;
    private UUID timeSlotId;
    private Integer startWeek;
    private Integer endWeek;
    private LocalDate startDate;
    private LocalDate endDate;
    private String reason;
    private String status;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
