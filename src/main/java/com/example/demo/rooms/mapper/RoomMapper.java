package com.example.demo.rooms.mapper;

import com.example.demo.rooms.dto.RoomDto;
import com.example.demo.rooms.dto.RoomListDto;
import com.example.demo.rooms.entity.Room;
import org.springframework.stereotype.Component;

@Component
public class RoomMapper {

    public Room toEntity(RoomDto dto) {
        if (dto == null) return null;
        return Room.builder()
                .id(dto.getId())
                .roomCode(dto.getRoomCode())
                .roomName(dto.getRoomName())
                .buildingId(dto.getBuildingId())
                .roomTypeId(dto.getRoomTypeId())
                .floor(dto.getFloor())
                .capacity(dto.getCapacity())
                .area(dto.getArea())
                .status(dto.getStatus())
                .isActive(dto.getIsActive())
                .createdAt(dto.getCreatedAt())
                .updatedAt(dto.getUpdatedAt())
                .build();
    }

    public RoomDto toDto(Room entity) {
        if (entity == null) return null;
        return RoomDto.builder()
                .id(entity.getId())
                .roomCode(entity.getRoomCode())
                .roomName(entity.getRoomName())
                .buildingId(entity.getBuildingId())
                .roomTypeId(entity.getRoomTypeId())
                .floor(entity.getFloor())
                .capacity(entity.getCapacity())
                .area(entity.getArea())
                .status(entity.getStatus())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }

    public RoomListDto toListDto(Room entity) {
        if (entity == null) return null;
        return RoomListDto.builder()
                .id(entity.getId())
                .roomCode(entity.getRoomCode())
                .roomName(entity.getRoomName())
                .buildingId(entity.getBuildingId())
                .roomTypeId(entity.getRoomTypeId())
                .floor(entity.getFloor())
                .capacity(entity.getCapacity())
                .area(entity.getArea())
                .status(entity.getStatus())
                .isActive(entity.getIsActive())
                .createdAt(entity.getCreatedAt())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}

