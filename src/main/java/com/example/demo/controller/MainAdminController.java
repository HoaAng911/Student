package com.example.demo.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
@lombok.RequiredArgsConstructor
public class MainAdminController {

    private final com.example.demo.service.StudentService studentService;

    @GetMapping
    public String index(Model model) {
        model.addAttribute("currentMenu", "home");
        return "admin/index";
    }

    @GetMapping("/students")
    public String students(Model model, 
                           @org.springframework.web.bind.annotation.RequestParam(defaultValue = "0") int page) {
        model.addAttribute("currentMenu", "students");
        org.springframework.data.domain.Page<com.example.demo.model.Student> studentPage = 
            studentService.getAllPaged(org.springframework.data.domain.PageRequest.of(page, 10));
        
        model.addAttribute("studentPage", studentPage);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", studentPage.getTotalPages());
        return "admin/students/list";
    }
}
