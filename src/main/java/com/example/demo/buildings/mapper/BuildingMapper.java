package com.example.demo.buildings.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.buildings.dto.BuildingDto;
import com.example.demo.buildings.dto.BuildingListDto;
import com.example.demo.buildings.entity.Building;

@Component
public class BuildingMapper {

    public Building toEntity(BuildingDto dto) {
        if (dto == null) return null;
        return Building.builder()
                .id(dto.getId())
                .buildingCode(dto.getBuildingCode())
                .buildingName(dto.getBuildingName())
                .address(dto.getAddress())
                .description(dto.getDescription())
                .isActive(dto.getIsActive() != null ? dto.getIsActive() : true)
                .build();
    }

    public BuildingDto toDto(Building entity) {
        if (entity == null) return null;
        return BuildingDto.builder()
                .id(entity.getId())
                .buildingCode(entity.getBuildingCode())
                .buildingName(entity.getBuildingName())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .build();
    }

    public BuildingListDto toListDto(Building entity) {
        if (entity == null) return null;
        return BuildingListDto.builder()
                .id(entity.getId())
                .buildingCode(entity.getBuildingCode())
                .buildingName(entity.getBuildingName())
                .address(entity.getAddress())
                .description(entity.getDescription())
                .isActive(entity.getIsActive())
                .build();
    }
}
