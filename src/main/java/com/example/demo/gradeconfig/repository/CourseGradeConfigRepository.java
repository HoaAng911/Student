package com.example.demo.gradeconfig.repository;

import com.example.demo.gradeconfig.model.CourseGradeConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.UUID;

public interface CourseGradeConfigRepository extends JpaRepository<CourseGradeConfig, UUID> {
    List<CourseGradeConfig> findByCourseId(UUID courseId);
}
