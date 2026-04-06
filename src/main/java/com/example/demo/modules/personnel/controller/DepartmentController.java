package com.example.demo.modules.personnel.controller;

import com.example.demo.modules.personnel.service.DepartmentService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor
public class DepartmentController {

    private final DepartmentService departmentService;

    /**
     * API: Lấy thông tin phòng ban
     */
    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable Long id) {
        // Logic thực tế: Gọi departmentService.getDepartmentById(id) trả về Entity
        // Sau đó map Entity -> DTO
        DepartmentDTO response = new DepartmentDTO();
        response.setId(id);
        response.setName("Phòng IT (Mock)");

        return ResponseEntity.ok(response);
    }

    /**
     * DTO: Data Transfer Object dùng để giao tiếp với Frontend
     */
    @Data
    public static class DepartmentDTO {
        private Long id;
        private String name;
        private int currentEmployeeCount;
    }
}