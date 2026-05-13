package com.example.demo.courses.controller;

import com.example.demo.courses.model.entity.CourseSection;
import com.example.demo.courses.repository.CourseSectionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/api/course-sections")
@RequiredArgsConstructor
public class CourseSectionMvcController {

    private final CourseSectionRepository courseSectionRepository;

    @GetMapping
    public String list(Model model) {
        List<CourseSection> sections = courseSectionRepository.findAll();
        model.addAttribute("sections", sections);
        model.addAttribute("currentMenu", "courseSections");
        return "admin/course-sections/list";
    }
}
