package com.example.demo.courses.repository;

import com.example.demo.courses.model.entity.Course;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.UUID;

@Repository
public interface CourseRepository extends JpaRepository<Course, UUID> {
    java.util.Optional<Course> findByCode(String code);
}
