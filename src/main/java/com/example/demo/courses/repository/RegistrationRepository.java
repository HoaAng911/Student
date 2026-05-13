package com.example.demo.courses.repository;

import com.example.demo.courses.model.entity.Registration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public interface RegistrationRepository extends JpaRepository<Registration, UUID> {
    List<Registration> findByCourseSectionId(UUID courseSectionId);
    List<Registration> findByCourseSectionIdAndIsActiveTrue(UUID courseSectionId);
    List<Registration> findByStudentId(UUID studentId);
    List<Registration> findByStudentIdAndIsActiveTrue(UUID studentId);
    long countByCourseSectionId(UUID courseSectionId);
}
