package com.example.demo.gradestudent.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class GradeViewController {

    @GetMapping("/admin/dashboard")
    public String dashboard() {
        // Trỏ đến file trong static/admin/index.html
        return "redirect:/admin/index.html"; 
    }

    @GetMapping("/admin/grade-scale")
    public String gradeScale() {
        // Trỏ đến file templates/admin/grade-scale.html (nếu bạn đã tạo)
        return "admin/grade-scale";
    }
    
}