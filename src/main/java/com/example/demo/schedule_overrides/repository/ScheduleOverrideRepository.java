package com.example.demo.schedule_overrides.repository;

import com.example.demo.schedule_overrides.entity.ScheduleOverride;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface ScheduleOverrideRepository extends JpaRepository<ScheduleOverride, UUID> {

    Optional<ScheduleOverride> findByOverrideCode(String overrideCode);

    @Query("""
            SELECT s FROM ScheduleOverride s
            WHERE (:keyword = '' OR LOWER(s.overrideCode) LIKE LOWER(CONCAT('%', :keyword, '%'))
                                 OR LOWER(s.reason)       LIKE LOWER(CONCAT('%', :keyword, '%')))
              AND (:roomId   IS NULL OR s.roomId   = :roomId)
              AND (:status   IS NULL OR s.status   = :status)
              AND (:isActive IS NULL OR s.isActive = :isActive)
            """)
    Page<ScheduleOverride> search(
            @Param("keyword")  String keyword,
            @Param("roomId")   UUID roomId,
            @Param("status")   String status,
            @Param("isActive") Boolean isActive,
            Pageable pageable
    );
}
