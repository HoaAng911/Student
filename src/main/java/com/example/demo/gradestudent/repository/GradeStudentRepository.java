package com.example.demo.gradestudent.repository;

import com.example.demo.gradestudent.model.GradeStudent;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface GradeStudentRepository extends JpaRepository<GradeStudent, UUID> {
    
    // Tìm điểm theo ID sinh viên
    List<GradeStudent> findByStudentId(UUID studentId);
}