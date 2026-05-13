package com.example.demo.gradeconfig.repository;

import com.example.demo.gradeconfig.model.GradeCategory;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;
import java.util.UUID;

public interface GradeCategoryRepository extends JpaRepository<GradeCategory, UUID> {
    Optional<GradeCategory> findByCode(String code);
}
