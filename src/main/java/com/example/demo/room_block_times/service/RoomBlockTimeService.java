package com.example.demo.room_block_times.service;

import com.example.demo.room_block_times.dto.RoomBlockTimeDto;
import com.example.demo.room_block_times.dto.RoomBlockTimeListDto;
import com.example.demo.room_block_times.entity.RoomBlockTime;
import com.example.demo.room_block_times.mapper.RoomBlockTimeMapper;
import com.example.demo.room_block_times.repository.RoomBlockTimeRepository;
import com.example.demo.rooms.entity.Room;
import com.example.demo.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomBlockTimeService {

    private final RoomBlockTimeRepository repository;
    private final RoomBlockTimeMapper mapper;
    private final RoomRepository roomRepository;

    public Page<RoomBlockTimeListDto> search(String keyword,
                                    UUID roomId,
                                    String status,
                                    String blockType,
                                    Pageable pageable) {
        Page<RoomBlockTime> page = repository.search(
                keyword != null ? keyword.trim() : "",
                roomId,
                (status != null && !status.isBlank()) ? status.trim() : null,
                (blockType != null && !blockType.isBlank()) ? blockType.trim() : null,
                pageable
        );
        List<RoomBlockTimeListDto> dtos = page.getContent().stream()
                .map(mapper::toListDto)
                .peek(this::fillRoomName)
                .toList();
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    private void fillRoomName(RoomBlockTimeListDto dto) {
        if (dto.getRoomId() != null) {
            roomRepository.findById(dto.getRoomId())
                    .map(Room::getRoomName)
                    .ifPresent(dto::setRoomName);
        }
    }

    public boolean isRoomBlocked(UUID roomId, LocalDateTime startTime, LocalDateTime endTime) {
        if (roomId == null || startTime == null || endTime == null) return false;

        List<RoomBlockTime> blocks = repository.findByRoomIdAndStatus(roomId, "ACTIVE");
        for (RoomBlockTime block : blocks) {
            boolean isTimeOverlap = true;

            // 1. Check date overlap
            if (block.getStartDate() != null && block.getEndDate() != null) {
                LocalDate overStart = startTime.toLocalDate();
                LocalDate overEnd = endTime.toLocalDate();
                if (overEnd.isBefore(block.getStartDate()) || overStart.isAfter(block.getEndDate())) {
                    isTimeOverlap = false;
                }
            }

            // 2. Check Day Of Week overlap
            if (isTimeOverlap && block.getDayOfWeek() != null) {
                boolean dowMatch = false;
                LocalDate current = startTime.toLocalDate();
                while (!current.isAfter(endTime.toLocalDate())) {
                    if (current.getDayOfWeek().getValue() == 7 && block.getDayOfWeek() == 8) {
                        dowMatch = true;
                    } else if (current.getDayOfWeek().getValue() + 1 == block.getDayOfWeek()) {
                        dowMatch = true;
                    }
                    current = current.plusDays(1);
                }
                if (!dowMatch) {
                    isTimeOverlap = false;
                }
            }

            // (Assuming week check is implicit or not strictly required by current simple check)

            if (isTimeOverlap) {
                return true;
            }
        }
        return false;
    }

    public Optional<RoomBlockTimeDto> findByIdAsDto(UUID id) {
        return repository.findById(id).map(mapper::toDto);
    }

    public Optional<RoomBlockTime> findById(UUID id) {
        return repository.findById(id);
    }

    @Transactional
    public RoomBlockTime save(RoomBlockTime entity) {
        return repository.save(entity);
    }

    @Transactional
    public void deleteById(UUID id) {
        repository.deleteById(id);
    }
}
