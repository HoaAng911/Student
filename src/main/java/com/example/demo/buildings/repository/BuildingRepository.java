package com.example.demo.buildings.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.demo.buildings.entity.Building;

import java.util.Optional;
import java.util.UUID;

public interface BuildingRepository extends JpaRepository<Building, UUID> {

    Optional<Building> findByBuildingCode(String buildingCode);

    @Query("SELECT b FROM Building b WHERE " +
            "(:keyword IS NULL OR :keyword = '' OR " +
            "LOWER(b.buildingCode) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(b.buildingName) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(COALESCE(b.address, '')) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(COALESCE(b.description, '')) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Building> searchByKeyword(@Param("keyword") String keyword, Pageable pageable);
}
