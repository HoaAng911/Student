package com.example.demo.room_block_times.mapper;

import com.example.demo.room_block_times.dto.RoomBlockTimeDto;
import com.example.demo.room_block_times.dto.RoomBlockTimeListDto;
import com.example.demo.room_block_times.entity.RoomBlockTime;
import org.springframework.stereotype.Component;

@Component
public class RoomBlockTimeMapper {

    public RoomBlockTimeDto toDto(RoomBlockTime entity) {
        return RoomBlockTimeDto.builder()
                .blockId(entity.getBlockId())
                .roomId(entity.getRoomId())
                .blockType(entity.getBlockType())
                .dayOfWeek(entity.getDayOfWeek())
                .timeSlotId(entity.getTimeSlotId())
                .startWeek(entity.getStartWeek())
                .endWeek(entity.getEndWeek())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public RoomBlockTimeListDto toListDto(RoomBlockTime entity) {
        return RoomBlockTimeListDto.builder()
                .blockId(entity.getBlockId())
                .roomId(entity.getRoomId())
                .blockType(entity.getBlockType())
                .dayOfWeek(entity.getDayOfWeek())
                .timeSlotId(entity.getTimeSlotId())
                .startWeek(entity.getStartWeek())
                .endWeek(entity.getEndWeek())
                .startDate(entity.getStartDate())
                .endDate(entity.getEndDate())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public RoomBlockTime toEntity(RoomBlockTimeDto dto) {
        return RoomBlockTime.builder()
                .blockId(dto.getBlockId())
                .roomId(dto.getRoomId())
                .blockType(dto.getBlockType())
                .dayOfWeek(dto.getDayOfWeek())
                .timeSlotId(dto.getTimeSlotId())
                .startWeek(dto.getStartWeek())
                .endWeek(dto.getEndWeek())
                .startDate(dto.getStartDate())
                .endDate(dto.getEndDate())
                .reason(dto.getReason())
                .status(dto.getStatus())
                .build();
    }
}
