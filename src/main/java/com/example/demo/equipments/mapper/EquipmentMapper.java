package com.example.demo.equipments.mapper;

import com.example.demo.equipments.dto.EquipmentDto;
import com.example.demo.equipments.dto.EquipmentListDto;
import com.example.demo.equipments.entity.Equipment;
import org.springframework.stereotype.Component;

@Component
public class EquipmentMapper {

    public Equipment toEntity(EquipmentDto dto) {
        if (dto == null) return null;
        return Equipment.builder()
                .id(dto.getId())
                .equipmentCode(dto.getEquipmentCode())
                .equipmentName(dto.getEquipmentName())
                .serialNumber(dto.getSerialNumber())
                .purchaseDate(dto.getPurchaseDate())
                .status(dto.getStatus())
                .roomId(dto.getRoomId())
                .build();
    }

    public EquipmentDto toDto(Equipment entity) {
        if (entity == null) return null;
        return EquipmentDto.builder()
                .id(entity.getId())
                .equipmentCode(entity.getEquipmentCode())
                .equipmentName(entity.getEquipmentName())
                .serialNumber(entity.getSerialNumber())
                .purchaseDate(entity.getPurchaseDate())
                .status(entity.getStatus())
                .roomId(entity.getRoomId())
                .build();
    }

    public EquipmentListDto toListDto(Equipment entity) {
        if (entity == null) return null;
        return EquipmentListDto.builder()
                .id(entity.getId())
                .equipmentCode(entity.getEquipmentCode())
                .equipmentName(entity.getEquipmentName())
                .serialNumber(entity.getSerialNumber())
                .purchaseDate(entity.getPurchaseDate())
                .status(entity.getStatus())
                .roomId(entity.getRoomId())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}
