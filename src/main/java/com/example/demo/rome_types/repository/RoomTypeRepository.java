package com.example.demo.rome_types.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.rome_types.entity.RoomType;

import java.util.Optional;
import java.util.UUID;

public interface RoomTypeRepository extends JpaRepository<RoomType, UUID> {

    Optional<RoomType> findByRoomTypeCode(String roomTypeCode);

    @Query("SELECT r FROM RoomType r WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(r.roomTypeCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(r.roomTypeName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(COALESCE(r.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<RoomType> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
