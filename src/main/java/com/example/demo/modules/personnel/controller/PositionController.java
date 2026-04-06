package com.example.demo.modules.personnel.controller;

import com.example.demo.modules.personnel.service.PositionService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/positions")
@RequiredArgsConstructor
public class PositionController {

    private final PositionService positionService;

    /**
     * API: Lấy mức lương cơ bản của chức vụ
     */
    @GetMapping("/{id}/salary")
    public ResponseEntity<PositionSalaryDTO> getPositionSalary(@PathVariable Long id) {
        double salary = positionService.getBaseSalaryForPosition(id);

        PositionSalaryDTO response = new PositionSalaryDTO();
        response.setPositionId(id);
        response.setBaseSalary(salary);

        return ResponseEntity.ok(response);
    }

    /**
     * DTO trả về thông tin lương chức vụ
     */
    @Data
    public static class PositionSalaryDTO {
        private Long positionId;
        private double baseSalary;
    }
}