package com.example.demo.rooms.repository;

import com.example.demo.rooms.entity.Room;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface RoomRepository extends JpaRepository<Room, UUID> {

    Optional<Room> findByRoomCode(String roomCode);

    @Query("""
           SELECT r FROM Room r
           WHERE
             (:keyword IS NULL OR :keyword = '' OR
               LOWER(r.roomCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(r.roomName) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND (:buildingId IS NULL OR r.buildingId = :buildingId)
             AND (:roomTypeId IS NULL OR r.roomTypeId = :roomTypeId)
             AND (:status IS NULL OR :status = '' OR r.status = :status)
             AND (:isActive IS NULL OR r.isActive = :isActive)
           """)
    Page<Room> search(
            @Param("keyword") String keyword,
            @Param("buildingId") UUID buildingId,
            @Param("roomTypeId") UUID roomTypeId,
            @Param("status") String status,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}

