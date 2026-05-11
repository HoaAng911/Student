package com.example.demo.studentgrades.service;

import com.example.demo.studentgrades.model.entity.StudentGrade;
import com.example.demo.studentgrades.repository.StudentGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentGradeService {

  private final StudentGradeRepository gradeRepository;

  public List<StudentGrade> getAllGrades() {
    return gradeRepository.findActiveGrades();
  }

  public Optional<StudentGrade> getGradeById(UUID id) {
    return gradeRepository.findByIdAndIsActiveTrue(id);
  }

  public List<StudentGrade> getGradesByRegistration(UUID registrationId) {
    return gradeRepository.findByRegistrationIdAndIsActiveTrue(registrationId);
  }

  public StudentGrade createGrade(StudentGrade grade) {
    return gradeRepository.save(grade);
  }

  public StudentGrade updateGrade(UUID id, StudentGrade details) {
    return gradeRepository.findById(id).map(grade -> {
      grade.setRegistrationId(details.getRegistrationId());
      grade.setGradeComponentId(details.getGradeComponentId());
      grade.setScore(details.getScore());
      grade.setIsTotal(details.getIsTotal());
      grade.setIsRetake(details.getIsRetake());
      grade.setIsLocked(details.getIsLocked());
      grade.setNote(details.getNote());
      grade.setLetterGrade(details.getLetterGrade());
      grade.setGpaValue(details.getGpaValue());
      grade.setResult(details.getResult());
      grade.setScaleId(details.getScaleId());
      grade.setIsFinalized(details.getIsFinalized());
      grade.setUpdatedBy(details.getUpdatedBy());
      grade.setIsActive(details.getIsActive());
      return gradeRepository.save(grade);
    }).orElseThrow(() -> new RuntimeException("StudentGrade not found with id " + id));
  }

  public void deleteGrade(UUID id) {
    gradeRepository.deleteById(id);
  }

  public void softDeleteGrade(UUID id, UUID deletedBy) {
    gradeRepository.findById(id).ifPresent(grade -> {
      grade.setIsActive(false);
      grade.setDeletedBy(deletedBy);
      grade.setDeletedAt(java.time.LocalDateTime.now());
      gradeRepository.save(grade);
    });
  }

  public Optional<StudentGrade> getByRegistrationAndComponent(UUID registrationId, UUID gradeComponentId) {
    return gradeRepository.findByRegistrationIdAndGradeComponentId(registrationId, gradeComponentId);
  }
}
