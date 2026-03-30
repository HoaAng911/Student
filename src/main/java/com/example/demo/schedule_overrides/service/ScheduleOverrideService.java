package com.example.demo.schedule_overrides.service;

import com.example.demo.rooms.entity.Room;
import com.example.demo.rooms.repository.RoomRepository;
import com.example.demo.schedule_overrides.dto.ScheduleOverrideDto;
import com.example.demo.schedule_overrides.dto.ScheduleOverrideListDto;
import com.example.demo.schedule_overrides.entity.ScheduleOverride;
import com.example.demo.schedule_overrides.mapper.ScheduleOverrideMapper;
import com.example.demo.schedule_overrides.repository.ScheduleOverrideRepository;
import com.example.demo.room_block_times.service.RoomBlockTimeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class ScheduleOverrideService {

    private final ScheduleOverrideRepository scheduleOverrideRepository;
    private final ScheduleOverrideMapper scheduleOverrideMapper;
    private final RoomRepository roomRepository;
    private final RoomBlockTimeService roomBlockTimeService;

    public Page<ScheduleOverrideListDto> search(String keyword,
                                                UUID roomId,
                                                String status,
                                                Boolean isActive,
                                                Pageable pageable) {
        Page<ScheduleOverride> page = scheduleOverrideRepository.search(
                keyword != null ? keyword.trim() : "",
                roomId,
                (status != null && !status.isBlank()) ? status.trim() : null,
                isActive,
                pageable
        );
        List<ScheduleOverrideListDto> dtos = page.getContent().stream()
                .map(scheduleOverrideMapper::toListDto)
                .peek(this::fillRoomInfo)
                .peek(dto -> {
                    if (dto.getRoomId() != null && dto.getStartTime() != null && dto.getEndTime() != null) {
                        dto.setIsBlocked(roomBlockTimeService.isRoomBlocked(dto.getRoomId(), dto.getStartTime(), dto.getEndTime()));
                    }
                })
                .toList();
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    private void fillRoomInfo(ScheduleOverrideListDto dto) {
        if (dto.getRoomId() != null) {
            roomRepository.findById(dto.getRoomId()).ifPresent(room -> {
                dto.setRoomCode(room.getRoomCode());
                dto.setRoomName(room.getRoomName());
            });
        }
    }

    public Optional<ScheduleOverrideDto> findByIdAsDto(UUID id) {
        return scheduleOverrideRepository.findById(id).map(scheduleOverrideMapper::toDto);
    }

    public Optional<ScheduleOverride> findByCode(String code) {
        return scheduleOverrideRepository.findByOverrideCode(code);
    }

    @Transactional
    public ScheduleOverride save(ScheduleOverride entity) {
        return scheduleOverrideRepository.save(entity);
    }

    @Transactional
    public void deleteById(UUID id) {
        scheduleOverrideRepository.deleteById(id);
    }

    public List<Room> findAllRooms() {
        return roomRepository.findAll();
    }
}
