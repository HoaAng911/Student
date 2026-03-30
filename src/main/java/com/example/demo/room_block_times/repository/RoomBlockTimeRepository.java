package com.example.demo.room_block_times.repository;

import com.example.demo.room_block_times.entity.RoomBlockTime;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.UUID;

public interface RoomBlockTimeRepository extends JpaRepository<RoomBlockTime, UUID> {

    List<RoomBlockTime> findByRoomIdAndStatus(UUID roomId, String status);

    @Query("""
           SELECT b FROM RoomBlockTime b
           LEFT JOIN b.room r
           WHERE
             (:keyword IS NULL OR :keyword = '' OR
               LOWER(b.reason) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(r.roomName) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND (:roomId IS NULL OR b.roomId = :roomId)
             AND (:status IS NULL OR :status = '' OR b.status = :status)
             AND (:blockType IS NULL OR :blockType = '' OR b.blockType = :blockType)
           """)
    Page<RoomBlockTime> search(
            @Param("keyword") String keyword,
            @Param("roomId") UUID roomId,
            @Param("status") String status,
            @Param("blockType") String blockType,
            Pageable pageable
    );
}
