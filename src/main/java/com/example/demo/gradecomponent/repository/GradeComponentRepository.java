package com.example.demo.gradecomponent.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.example.demo.gradecomponent.model.GradeComponent;

import java.util.List;
import java.util.UUID;

@Repository
public interface GradeComponentRepository extends JpaRepository<GradeComponent, UUID> {
    List<GradeComponent> findByCourseClassIdOrderByInputOrderAsc(UUID courseClassId);
}