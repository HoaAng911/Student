package com.example.demo.gradescale.controller;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import com.example.demo.gradescale.service.GradeScaleService;
import com.example.demo.gradescale.model.GradeScale;

@Controller
@RequestMapping("/api/grade-scales")
public class GradeScaleViewController {

    @Autowired
    private GradeScaleService gradeScaleService;

    @GetMapping
    public String listGradeScales(Model model) {
        model.addAttribute("gradeScales", gradeScaleService.getAllGradeScales());
        model.addAttribute("currentMenu", "gradeScales");
        return "admin/grade-scale/list";
    }

    @GetMapping("/new")
    public String showCreateForm(Model model) {
        model.addAttribute("gradeScale", new GradeScale());
        model.addAttribute("title", "Thêm thang điểm mới");
        model.addAttribute("currentMenu", "gradeScales");
        return "admin/grade-scale/form";
    }

    @GetMapping("/edit/{id}")
    public String showEditForm(@PathVariable UUID id, Model model) {
        GradeScale gradeScale = gradeScaleService.getGradeScaleById(id);
        if (gradeScale == null) {
            return "redirect:/api/grade-scales";
        }
        model.addAttribute("gradeScale", gradeScale);
        model.addAttribute("title", "Chỉnh sửa thang điểm");
        model.addAttribute("currentMenu", "gradeScales");
        return "admin/grade-scale/form";
    }
}
