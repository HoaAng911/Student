package com.example.demo.gradestudent.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.gradestudent.model.GradeStudent;
import com.example.demo.gradestudent.service.GradeStudentService;

@RestController
@RequestMapping("/api/grade-students")
public class GradeStudentController {

    @Autowired
    private GradeStudentService service;

    @GetMapping("/student/{studentId}")
    public ResponseEntity<List<GradeStudent>> getByStudent(@PathVariable UUID studentId) {
        return ResponseEntity.ok(service.getGradesByStudentId(studentId));
    }

    @PostMapping
    public ResponseEntity<GradeStudent> create(@RequestBody GradeStudent gradeStudent) {
        return ResponseEntity.ok(service.createGrade(gradeStudent));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable UUID id) {
        service.deleteGrade(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/admin/grade-management")
    public String openGradePage() {
        return "admin/grade-student"; // Đảm bảo bạn có file grade-student.html trong templates/admin/
    }
    
    @GetMapping("/")
    public String home() {
        return "index";
    }
}