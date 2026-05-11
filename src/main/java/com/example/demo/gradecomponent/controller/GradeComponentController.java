package com.example.demo.gradecomponent.controller;

import com.example.demo.gradecomponent.model.GradeComponent;
import com.example.demo.gradecomponent.service.GradeComponentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/api/grade-components")
@CrossOrigin(origins = "*") 
public class GradeComponentController {

    @Autowired
    private GradeComponentService service;

    // --- VIEW METHODS (TRẢ VỀ HTML) ---

    @GetMapping
    public String list(Model model) {
        List<GradeComponent> list = service.getAll();
        model.addAttribute("gradeComponents", list);
        model.addAttribute("currentMenu", "gradeComponents");
        return "admin/grade-components/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("gradeComponent", new GradeComponent());
        model.addAttribute("isEdit", false);
        model.addAttribute("currentMenu", "gradeComponents");
        return "admin/grade-components/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        GradeComponent gc = service.getById(id);
        if (gc == null) return "redirect:/api/grade-components";
        model.addAttribute("gradeComponent", gc);
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "gradeComponents");
        return "admin/grade-components/form";
    }

    @PostMapping("/save-view")
    public String saveView(@ModelAttribute GradeComponent gradeComponent, RedirectAttributes ra) {
        try {
            service.save(gradeComponent);
            ra.addFlashAttribute("success", "Lưu thành phần điểm thành công!");
        } catch (Exception e) {
            ra.addFlashAttribute("error", "Lỗi khi lưu dữ liệu: " + e.getMessage());
        }
        return "redirect:/api/grade-components";
    }

    // --- REST API METHODS (TRẢ VỀ JSON) ---

    @GetMapping("/data") // Đổi path để tránh trùng với View GetMapping root
    @ResponseBody
    public List<GradeComponent> getAll() {
        return service.getAll();
    }

    @GetMapping("/section/{sectionId}")
    @ResponseBody
    public List<GradeComponent> getBySection(@PathVariable UUID sectionId) {
        return service.getBySection(sectionId);
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<UUID> save(@RequestBody GradeComponent data) {
        GradeComponent saved = service.save(data);
        return ResponseEntity.ok(saved.getId());
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}