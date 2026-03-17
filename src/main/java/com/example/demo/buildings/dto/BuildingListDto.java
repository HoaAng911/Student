package com.example.demo.buildings.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingListDto {

    private UUID id;
    private String buildingCode;
    private String buildingName;
    private String address;
    private String description;
    private Boolean isActive;
}
