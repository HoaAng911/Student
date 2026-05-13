package com.example.demo.students.controller;

import java.util.List;
import java.util.UUID;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.students.model.entity.Student;
import com.example.demo.students.service.StudentService;

/**
 * Controller cung cấp các API RESTful để quản lý Sinh viên.
 * Base Path: /api/v1/students
 */
@RestController
@RequestMapping("/api/v1/students")
@CrossOrigin // Cho phép các ứng dụng Frontend (React, Vue, HTML tĩnh) gọi API này
public class StudentController {

    private final StudentService service;

    public StudentController(StudentService service) {
        this.service = service;
    }

    /** [GET] Lấy danh sách tất cả sinh viên */
    @GetMapping
    public List<Student> getAll() {
        return service.getAll();
    }

    /** [GET] Lấy chi tiết một sinh viên theo ID */
    @GetMapping("/{id}")
    public Student getById(@PathVariable UUID id) {
        return service.getById(id);
    }

    /** [POST] Thêm mới một sinh viên */
    @PostMapping
    public Student create(@RequestBody Student student) {
        return service.create(student);
    }

    /** [PUT] Cập nhật thông tin sinh viên theo ID */
    @PutMapping("/{id}")
    public Student update(@PathVariable UUID id,
                          @RequestBody Student student) {
        return service.update(id, student);
    }

    /** [DELETE] Xóa một sinh viên khỏi hệ thống */
    @DeleteMapping("/{id}")
    public void delete(@PathVariable UUID id) {
        service.delete(id);
    }

    /** [GET] Tìm kiếm sinh viên theo họ tên */
    @GetMapping("/search")
    public List<Student> search(@RequestParam String full_name) {
        return service.search(full_name);
    }

}