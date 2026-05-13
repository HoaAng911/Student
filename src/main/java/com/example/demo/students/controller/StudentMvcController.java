package com.example.demo.students.controller;

import com.example.demo.students.model.entity.Student;
import com.example.demo.students.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/students")
@RequiredArgsConstructor
public class StudentMvcController {

    private final StudentService studentService;

    @GetMapping
    public String list(Model model, 
                       @RequestParam(defaultValue = "0") int page,
                       @RequestParam(required = false) String keyword) {
        model.addAttribute("currentMenu", "students");
        
        Page<Student> studentPage;
        if (keyword != null && !keyword.isEmpty()) {
            studentPage = studentService.searchPaged(keyword, PageRequest.of(page, 10));
            model.addAttribute("keyword", keyword);
        } else {
            studentPage = studentService.getAllPaged(PageRequest.of(page, 10));
        }
        
        model.addAttribute("studentPage", studentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        return "admin/students/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("student", new Student());
        model.addAttribute("isEdit", false);
        model.addAttribute("currentMenu", "students");
        return "admin/students/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        Student student = studentService.getById(id);
        if (student == null) {
            return "redirect:/api/students";
        }
        model.addAttribute("student", student);
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "students");
        return "admin/students/form";
    }

    @PostMapping("/save")
    public String save(@ModelAttribute Student student, RedirectAttributes ra) {
        if (student.getId() != null) {
            studentService.update(student.getId(), student);
            ra.addFlashAttribute("message", "Cập nhật sinh viên thành công!");
        } else {
            studentService.create(student);
            ra.addFlashAttribute("message", "Thêm sinh viên mới thành công!");
        }
        return "redirect:/api/students";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes ra) {
        studentService.delete(id);
        ra.addFlashAttribute("message", "Đã xóa sinh viên khỏi hệ thống.");
        return "redirect:/api/students";
    }
}
