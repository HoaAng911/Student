package com.example.demo.controller;

import com.example.demo.courses.repository.CourseSectionRepository;
import com.example.demo.students.service.StudentService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/api")
@RequiredArgsConstructor
public class MainAdminController {

    private final StudentService studentService;
    private final CourseSectionRepository courseSectionRepository;
    private final com.example.demo.studentgrades.service.StudentGradeService studentGradeService;

    @GetMapping
    public String index(jakarta.servlet.http.HttpSession session, Model model) {
        com.example.demo.users.model.entity.User currentUser = (com.example.demo.users.model.entity.User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        String roleCode = currentUser.getRoles().stream()
                .map(com.example.demo.roles.model.entity.Role::getCode)
                .findFirst()
                .orElse("STUDENT");

        model.addAttribute("currentMenu", "home");
        model.addAttribute("userRole", roleCode);
        model.addAttribute("displayName", currentUser.getUsername());

        if ("ADMIN".equals(roleCode)) {
            // Dữ liệu cho Admin
            model.addAttribute("totalStudents", studentService.getAllPaged(org.springframework.data.domain.PageRequest.of(0, 1)).getTotalElements());
            model.addAttribute("totalSections", courseSectionRepository.count());
            
            courseSectionRepository.findAll().stream().findFirst().ifPresent(section -> {
                model.addAttribute("demoSectionId", section.getId());
            });
        } else if ("TEACHER".equals(roleCode)) {
            // Dữ liệu cho Giảng viên: Các lớp đang phụ trách
            model.addAttribute("mySections", studentGradeService.getCourseSectionsByLecturer(currentUser.getId()));
        }

        return "admin/index";
    }
}
