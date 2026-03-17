package com.example.demo.equipments.dto;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentListDto {

    private UUID id;
    private String equipmentCode;
    private String equipmentName;
    private String serialNumber;
    private LocalDate purchaseDate;
    private String status;
    private UUID roomId;
    private String roomName;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
