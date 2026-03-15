package com.example.demo.rooms.dto;

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
public class RoomListDto {

    private UUID id;
    private String roomCode;
    private String roomName;
    private UUID buildingId;
    private String buildingName;
    private UUID roomTypeId;
    private String roomTypeName;
    private Integer floor;
    private Integer capacity;
    private Double area;
    private String status;
    private Boolean isActive;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

