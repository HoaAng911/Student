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

    @Autowired
    private com.example.demo.gradeconfig.repository.GradeCategoryRepository categoryRepository;

    @Autowired
    private com.example.demo.courses.repository.CourseSectionRepository courseSectionRepository;

    @GetMapping("/categories")
    @ResponseBody
    public List<com.example.demo.gradeconfig.model.GradeCategory> getCategories() {
        return categoryRepository.findAll();
    }

    // --- VIEW METHODS (TRẢ VỀ HTML) ---

    @GetMapping
    public String list(@RequestParam(required = false) String keyword, Model model) {
        List<GradeComponent> list;
        if (keyword != null && !keyword.trim().isEmpty()) {
            list = service.search(keyword);
            model.addAttribute("keyword", keyword);
        } else {
            list = service.getAll();
        }
        model.addAttribute("gradeComponents", list);
        model.addAttribute("currentMenu", "gradeComponents");
        return "admin/grade-components/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("gradeComponent", new GradeComponent());
        model.addAttribute("courseSections", courseSectionRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("isEdit", false);
        model.addAttribute("currentMenu", "gradeComponents");
        return "admin/grade-components/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, Model model) {
        GradeComponent gc = service.getById(id);
        if (gc == null) return "redirect:/api/grade-components";
        model.addAttribute("gradeComponent", gc);
        model.addAttribute("courseSections", courseSectionRepository.findAll());
        model.addAttribute("categories", categoryRepository.findAll());
        model.addAttribute("isEdit", true);
        model.addAttribute("currentMenu", "gradeComponents");
        return "admin/grade-components/form";
    }

    @GetMapping("/config/section/{sectionId}")
    public String configPage(@PathVariable UUID sectionId, Model model) {
        model.addAttribute("sectionId", sectionId);
        model.addAttribute("currentMenu", "courseSections"); // Link back to sections
        return "admin/grade-components/section-config";
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

    // --- DTO ---
    public static class GradeComponentDTO {
        public UUID id;
        public String componentName;
        public String componentCode;
        public java.math.BigDecimal weightPercentage;
        public Boolean isRequired;
        public Boolean isFinal;
        public UUID categoryId;

        public GradeComponentDTO(GradeComponent gc) {
            this.id = gc.getId();
            this.componentName = gc.getComponentName();
            this.componentCode = gc.getComponentCode();
            this.weightPercentage = gc.getWeightPercentage();
            this.isRequired = gc.getIsRequired();
            this.isFinal = gc.getIsFinal();
            if (gc.getGradeCategory() != null) this.categoryId = gc.getGradeCategory().getId();
        }
    }

    @GetMapping("/data")
    @ResponseBody
    public List<GradeComponentDTO> getAll() {
        return service.getAll().stream().map(GradeComponentDTO::new).collect(java.util.stream.Collectors.toList());
    }

    @GetMapping("/section/{sectionId}")
    @ResponseBody
    public List<GradeComponentDTO> getBySection(@PathVariable UUID sectionId) {
        return service.getBySection(sectionId).stream().map(GradeComponentDTO::new).collect(java.util.stream.Collectors.toList());
    }

    @PostMapping
    @ResponseBody
    public ResponseEntity<UUID> save(@RequestBody GradeComponent data) {
        GradeComponent saved = service.save(data);
        return ResponseEntity.ok(saved.getId());
    }

    @PostMapping("/section/{sectionId}/bulk-update")
    @ResponseBody
    public ResponseEntity<?> bulkUpdate(@PathVariable UUID sectionId, @RequestBody List<GradeComponent> components) {
        try {
            service.saveAllForSection(sectionId, components);
            return ResponseEntity.ok().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    @ResponseBody
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}