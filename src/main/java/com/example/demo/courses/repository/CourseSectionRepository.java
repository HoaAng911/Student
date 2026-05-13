package com.example.demo.courses.repository;

import com.example.demo.courses.model.entity.CourseSection;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CourseSectionRepository extends JpaRepository<CourseSection, UUID> {
    java.util.List<CourseSection> findByIsActiveTrue();
    java.util.List<CourseSection> findByLecturerIdAndIsActiveTrue(UUID lecturerId);
}
