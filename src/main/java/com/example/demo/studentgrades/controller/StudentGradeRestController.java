package com.example.demo.studentgrades.controller;

import com.example.demo.studentgrades.model.dto.BulkGradeRequest;
import com.example.demo.studentgrades.model.dto.ClassGradeReportDTO;
import com.example.demo.studentgrades.model.dto.LockGradeRequest;
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

  /** Chấm điểm hàng loạt */
  @PostMapping("/bulk-update")
  public ResponseEntity<String> bulkUpdateGrades(@RequestBody BulkGradeRequest request, jakarta.servlet.http.HttpSession session) {
    com.example.demo.users.model.entity.User currentUser = (com.example.demo.users.model.entity.User) session.getAttribute("currentUser");
    UUID lecturerId = (currentUser != null) ? currentUser.getId() : request.getLecturerId();
    
    gradeService.bulkUpdateComponentGrades(request.getComponentId(), request.getScores(), lecturerId);
    return ResponseEntity.ok("Cập nhật điểm hàng loạt thành công!");
  }

  /** Lưu điểm hàng loạt từ giao diện Ma trận */
  @PostMapping("/matrix")
  public ResponseEntity<String> saveMatrixGrades(@RequestBody List<com.example.demo.studentgrades.model.dto.GradeUpdateDTO> updates, jakarta.servlet.http.HttpSession session) {
    com.example.demo.users.model.entity.User currentUser = (com.example.demo.users.model.entity.User) session.getAttribute("currentUser");
    if (currentUser == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập!");
    
    gradeService.saveMatrixGrades(updates, currentUser.getId());
    return ResponseEntity.ok("Đã lưu bảng điểm ma trận thành công!");
  }

  /** Khóa/Mở khóa điểm */
  @PostMapping("/lock")
  public ResponseEntity<String> lockGrades(@RequestBody LockGradeRequest request, jakarta.servlet.http.HttpSession session) {
    com.example.demo.users.model.entity.User currentUser = (com.example.demo.users.model.entity.User) session.getAttribute("currentUser");
    UUID lecturerId = (currentUser != null) ? currentUser.getId() : request.getLecturerId();

    gradeService.lockComponentGrades(request.getComponentId(), lecturerId, request.isLock());
    String message = request.isLock() ? "Đã khóa điểm thành công!" : "Đã mở khóa điểm thành công!";
    return ResponseEntity.ok(message);
  }

  /** Chốt điểm tổng kết học phần (Chỉ ADMIN/Giáo vụ) */
  @PostMapping("/finalize/{sectionId}")
  public ResponseEntity<String> finalizeGrades(@PathVariable UUID sectionId, jakarta.servlet.http.HttpSession session) {
    com.example.demo.users.model.entity.User currentUser = (com.example.demo.users.model.entity.User) session.getAttribute("currentUser");
    if (currentUser == null) return ResponseEntity.status(401).body("Vui lòng đăng nhập!");
    
    gradeService.finalizeClassGrades(sectionId, currentUser.getId());
    return ResponseEntity.ok("Đã chốt điểm tổng kết cho toàn lớp thành công!");
  }

  /** Lấy bảng điểm ma trận của lớp (Matrix View) */
  @GetMapping("/report/section/{sectionId}")
  public ResponseEntity<ClassGradeReportDTO> getClassGradeReport(@PathVariable UUID sectionId) {
    return ResponseEntity.ok(gradeService.getClassGradeReport(sectionId));
  }
}
