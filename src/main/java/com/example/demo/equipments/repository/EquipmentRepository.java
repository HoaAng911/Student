package com.example.demo.equipments.repository;

import com.example.demo.equipments.entity.Equipment;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface EquipmentRepository extends JpaRepository<Equipment, UUID> {

    Optional<Equipment> findByEquipmentCode(String equipmentCode);

    @Query("""
           SELECT e FROM Equipment e
           WHERE
             (:keyword IS NULL OR :keyword = '' OR
               LOWER(e.equipmentCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(e.equipmentName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR
               LOWER(COALESCE(e.serialNumber, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))
             AND (:status IS NULL OR :status = '' OR e.status = :status)
             AND (:roomId IS NULL OR e.roomId = :roomId)
           """)
    Page<Equipment> search(
            @Param("keyword") String keyword,
            @Param("status") String status,
            @Param("roomId") UUID roomId,
            Pageable pageable
    );
}
