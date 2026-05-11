package com.example.demo.studentgrades.controller;

import com.example.demo.studentgrades.model.entity.StudentGrade;
import com.example.demo.studentgrades.service.StudentGradeService;
import com.example.demo.gradecomponent.service.GradeComponentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/student-grades")
@RequiredArgsConstructor
public class StudentGradeMvcController {

  private final StudentGradeService gradeService;
  private final GradeComponentService gradeComponentService;

  @GetMapping
  public String list(Model model) {
    model.addAttribute("studentGrades", gradeService.getAllGrades());
    model.addAttribute("gradeComponents", gradeComponentService.getAll());
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

  @GetMapping("/data")
  @ResponseBody
  public List<StudentGrade> getData() {
    return gradeService.getAllGrades();
  }
}
