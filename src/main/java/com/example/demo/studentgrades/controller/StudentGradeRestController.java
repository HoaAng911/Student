package com.example.demo.studentgrades.controller;

import com.example.demo.studentgrades.model.entity.StudentGrade;
import com.example.demo.studentgrades.service.StudentGradeService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/student-grades")
@RequiredArgsConstructor
public class StudentGradeRestController {

  private final StudentGradeService gradeService;

  @GetMapping
  public ResponseEntity<List<StudentGrade>> getAllGrades() {
    return ResponseEntity.ok(gradeService.getAllGrades());
  }

  @GetMapping("/{id}")
  public ResponseEntity<StudentGrade> getGradeById(@PathVariable UUID id) {
    return gradeService.getGradeById(id)
        .map(ResponseEntity::ok)
        .orElse(ResponseEntity.notFound().build());
  }

  @GetMapping("/registration/{registrationId}")
  public ResponseEntity<List<StudentGrade>> getGradesByRegistration(@PathVariable UUID registrationId) {
    return ResponseEntity.ok(gradeService.getGradesByRegistration(registrationId));
  }

  @PostMapping
  public ResponseEntity<StudentGrade> createGrade(@Valid @RequestBody StudentGrade grade) {
    return new ResponseEntity<>(gradeService.createGrade(grade), HttpStatus.CREATED);
  }

  @PutMapping("/{id}")
  public ResponseEntity<StudentGrade> updateGrade(@PathVariable UUID id, @Valid @RequestBody StudentGrade grade) {
    try {
      return ResponseEntity.ok(gradeService.updateGrade(id, grade));
    } catch (RuntimeException e) {
      return ResponseEntity.notFound().build();
    }
  }

  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteGrade(@PathVariable UUID id) {
    gradeService.deleteGrade(id);
    return ResponseEntity.noContent().build();
  }

  @PatchMapping("/{id}/soft-delete")
  public ResponseEntity<Void> softDeleteGrade(@PathVariable UUID id, @RequestParam UUID deletedBy) {
    gradeService.softDeleteGrade(id, deletedBy);
    return ResponseEntity.noContent().build();
  }
}
