package com.example.demo.studentgrades.controller;

import com.example.demo.courses.model.entity.CourseSection;
import com.example.demo.studentgrades.model.entity.StudentGrade;
import com.example.demo.studentgrades.service.StudentGradeService;
import com.example.demo.gradecomponent.service.GradeComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import com.example.demo.users.model.entity.User;
import com.example.demo.roles.model.entity.Role;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/student-grades")
@RequiredArgsConstructor
public class StudentGradeMvcController {

  private final StudentGradeService gradeService;
  private final GradeComponentService gradeComponentService;

  @GetMapping
  public String list(@RequestParam(required = false) String keyword, HttpSession session, Model model) {
    User currentUser = (User) session.getAttribute("currentUser");
    if (currentUser == null) return "redirect:/login";

    java.util.Set<String> roles = currentUser.getRoles().stream()
            .map(Role::getCode)
            .collect(java.util.stream.Collectors.toSet());

    List<CourseSection> sections;
    if (roles.contains("ADMIN")) {
        sections = gradeService.getAllCourseSections(); // Admin thấy tất cả
    } else if (roles.contains("TEACHER")) {
        sections = gradeService.getCourseSectionsByLecturer(currentUser.getId()); // Giảng viên thấy lớp mình dạy
    } else {
        return "redirect:/student/my-grades";
    }
    
    model.addAttribute("sections", sections);
    model.addAttribute("currentMenu", "studentGrades");
    return "admin/student-grades/list";
  }

  @GetMapping("/add")
  public String addForm(Model model) {
    model.addAttribute("grade", new StudentGrade());
    model.addAttribute("isEdit", false);
    model.addAttribute("gradeComponents", gradeComponentService.getAll());
    model.addAttribute("currentMenu", "studentGrades");
    return "admin/student-grades/form";
  }

  @GetMapping("/edit/{id}")
  public String editForm(@PathVariable UUID id, Model model) {
    StudentGrade grade = gradeService.getGradeById(id)
        .orElseThrow(() -> new RuntimeException("Grade not found"));
    
    model.addAttribute("grade", grade);
    model.addAttribute("isEdit", true);
    model.addAttribute("gradeComponents", gradeComponentService.getAll());
    model.addAttribute("currentMenu", "studentGrades");
    return "admin/student-grades/form";
  }

  @GetMapping("/course-section/{courseSectionId}")
  public String classReport(@PathVariable UUID courseSectionId, Model model) {
    model.addAttribute("report", gradeService.getClassGradeReport(courseSectionId));
    model.addAttribute("currentMenu", "studentGrades");
    return "admin/student-grades/class-report";
  }

  @GetMapping("/matrix-grading/{courseSectionId}")
  public String matrixGrading(@PathVariable UUID courseSectionId, Model model) {
    model.addAttribute("report", gradeService.getClassGradeReport(courseSectionId));
    model.addAttribute("currentMenu", "studentGrades");
    return "admin/student-grades/matrix-grading";
  }

  @GetMapping("/export/course-section/{id}")
  public ResponseEntity<InputStreamResource> exportToExcel(@PathVariable UUID id) throws IOException {
    ByteArrayInputStream bis = gradeService.exportClassGradeReportToExcel(id);
    
    HttpHeaders headers = new HttpHeaders();
    headers.add("Content-Disposition", "attachment; filename=BangDiemLop_" + id.toString().substring(0, 8) + ".xlsx");

    return ResponseEntity
        .ok()
        .headers(headers)
        .contentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"))
        .body(new InputStreamResource(bis));
  }
}
