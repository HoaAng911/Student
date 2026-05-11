package com.example.demo.studentgrades.repository;

import com.example.demo.studentgrades.model.entity.StudentGrade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface StudentGradeRepository extends JpaRepository<StudentGrade, UUID> {

  List<StudentGrade> findByRegistrationIdAndIsActiveTrue(UUID registrationId);

  @Query("SELECT s FROM StudentGrade s WHERE s.isActive = true")
  List<StudentGrade> findActiveGrades();

  Optional<StudentGrade> findByIdAndIsActiveTrue(UUID id);

  @Query("SELECT s FROM StudentGrade s WHERE s.registrationId = :registrationId AND s.gradeComponentId = :gradeComponentId AND s.isActive = true")
  Optional<StudentGrade> findByRegistrationIdAndGradeComponentId(UUID registrationId, UUID gradeComponentId);
}
