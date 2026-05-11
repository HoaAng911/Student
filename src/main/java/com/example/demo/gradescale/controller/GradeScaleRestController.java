package com.example.demo.gradescale.controller;

import java.util.UUID;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import com.example.demo.gradescale.model.GradeScale;
import com.example.demo.gradescale.service.GradeScaleService;

@RestController
@RequestMapping("/api/v1/grade-scales")
public class GradeScaleRestController {

    @Autowired
    private GradeScaleService gradeScaleService;

    @PostMapping
    public ResponseEntity<?> saveGradeScale(@RequestBody GradeScale gradeScale) {
        try {
            return ResponseEntity.ok(gradeScaleService.saveGradeScale(gradeScale));
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteGradeScale(@PathVariable UUID id) {
        try {
            gradeScaleService.deleteGradeScale(id);
            return ResponseEntity.noContent().build();
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
