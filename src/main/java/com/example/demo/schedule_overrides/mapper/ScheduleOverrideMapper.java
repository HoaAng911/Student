package com.example.demo.schedule_overrides.mapper;

import com.example.demo.schedule_overrides.dto.ScheduleOverrideDto;
import com.example.demo.schedule_overrides.dto.ScheduleOverrideListDto;
import com.example.demo.schedule_overrides.entity.ScheduleOverride;
import org.springframework.stereotype.Component;

@Component
public class ScheduleOverrideMapper {

    public ScheduleOverrideDto toDto(ScheduleOverride entity) {
        return ScheduleOverrideDto.builder()
                .id(entity.getId())
                .overrideCode(entity.getOverrideCode())
                .roomId(entity.getRoomId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public ScheduleOverrideListDto toListDto(ScheduleOverride entity) {
        return ScheduleOverrideListDto.builder()
                .id(entity.getId())
                .overrideCode(entity.getOverrideCode())
                .roomId(entity.getRoomId())
                .startTime(entity.getStartTime())
                .endTime(entity.getEndTime())
                .reason(entity.getReason())
                .status(entity.getStatus())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .build();
    }

    public ScheduleOverride toEntity(ScheduleOverrideDto dto) {
        return ScheduleOverride.builder()
                .id(dto.getId())
                .overrideCode(dto.getOverrideCode())
                .roomId(dto.getRoomId())
                .startTime(dto.getStartTime())
                .endTime(dto.getEndTime())
                .reason(dto.getReason())
                .status(dto.getStatus())
                .isActive(dto.getIsActive())
                .build();
    }
}
