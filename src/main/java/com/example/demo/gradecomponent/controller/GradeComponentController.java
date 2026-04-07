package com.example.demo.gradecomponent.controller;

import com.example.demo.gradecomponent.model.GradeComponent;
import com.example.demo.gradecomponent.service.GradeComponentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/grade-components")
@CrossOrigin(origins = "*") 
public class GradeComponentController {

    @Autowired
    private GradeComponentService service;

    @GetMapping
    public List<GradeComponent> getAll() {
        return service.getAll();
    }

    @GetMapping("/class/{classId}")
    public List<GradeComponent> getByClass(@PathVariable UUID classId) {
        return service.getByClass(classId);
    }

    @PostMapping
    public GradeComponent save(@RequestBody GradeComponent data) {
        return service.save(data);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }
}