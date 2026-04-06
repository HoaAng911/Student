package com.example.demo.modules.personnel.controller;

import com.example.demo.modules.personnel.service.EmployeeService;
import com.example.demo.modules.personnel.service.StaffManagementService;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

    // Tiêm các service cần thiết
    // Tác vụ đơn giản (CRUD) -> Gọi trực tiếp EmployeeService
    private final EmployeeService employeeService;

    // Tác vụ phức tạp (Onboard, Transfer) -> Gọi StaffManagementService (Facade)
    private final StaffManagementService staffManagementService;

    /**
     * API: Tiếp nhận nhân sự mới (Gọi đến Orchestrator Service)
     */
    @PostMapping("/onboard")
    public ResponseEntity<OnboardResponseDTO> onboardEmployee(@RequestBody OnboardRequestDTO request) {
        // Gọi service tổng hợp xử lý luồng phức tạp
        String resultMsg = staffManagementService.onboardNewStaff(
                request.getFullName(),
                request.getDepartmentId(),
                request.getPositionId()
        );

        OnboardResponseDTO response = new OnboardResponseDTO(resultMsg);
        return ResponseEntity.ok(response);
    }

    /**
     * API: Thuyên chuyển nhân sự (Gọi đến Orchestrator Service)
     */
    @PostMapping("/{id}/transfer")
    public ResponseEntity<String> transferEmployee(
            @PathVariable Long id,
            @RequestBody TransferRequestDTO request) {

        staffManagementService.transferStaff(id, request.getToDepartmentId(), request.getToPositionId());

        return ResponseEntity.ok("Thuyên chuyển công tác thành công!");
    }

    // --- CÁC CLASS DTO ---

    @Data
    public static class OnboardRequestDTO {
        private String fullName;
        private Long departmentId;
        private Long positionId;
    }

    @Data
    public static class TransferRequestDTO {
        private Long toDepartmentId;
        private Long toPositionId;
    }

    @Data
    public static class OnboardResponseDTO {
        private String message;

        public OnboardResponseDTO(String message) {
            this.message = message;
        }
    }
}