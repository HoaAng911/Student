package com.example.nhom7.rome_types.mapper;

import com.example.nhom7.rome_types.dto.RoomTypeDto;
import com.example.nhom7.rome_types.dto.RoomTypeListDto;
import com.example.nhom7.rome_types.entity.RoomType;
import org.springframework.stereotype.Component;

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
