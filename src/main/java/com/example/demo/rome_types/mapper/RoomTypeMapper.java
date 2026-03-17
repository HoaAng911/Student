package com.example.demo.rome_types.mapper;

import org.springframework.stereotype.Component;

import com.example.demo.rome_types.dto.RoomTypeDto;
import com.example.demo.rome_types.dto.RoomTypeListDto;
import com.example.demo.rome_types.entity.RoomType;

@Component
public class RoomTypeMapper {

    public RoomType toEntity(RoomTypeDto dto) {
        if (dto == null) return null;
        return RoomType.builder()
                .roomTypeId(dto.getRoomTypeId())
                .roomTypeCode(dto.getRoomTypeCode())
                .roomTypeName(dto.getRoomTypeName())
                .description(dto.getDescription())
                .maxCapacity(dto.getMaxCapacity())
                .build();
    }

    public RoomTypeDto toDto(RoomType entity) {
        if (entity == null) return null;
        return RoomTypeDto.builder()
                .roomTypeId(entity.getRoomTypeId())
                .roomTypeCode(entity.getRoomTypeCode())
                .roomTypeName(entity.getRoomTypeName())
                .description(entity.getDescription())
                .maxCapacity(entity.getMaxCapacity())
                .build();
    }

    public RoomTypeListDto toListDto(RoomType entity) {
        if (entity == null) return null;
        return RoomTypeListDto.builder()
                .roomTypeId(entity.getRoomTypeId())
                .roomTypeCode(entity.getRoomTypeCode())
                .roomTypeName(entity.getRoomTypeName())
                .description(entity.getDescription())
                .maxCapacity(entity.getMaxCapacity())
                .build();
    }
}
