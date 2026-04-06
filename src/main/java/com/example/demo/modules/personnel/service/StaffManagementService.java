package com.example.demo.modules.personnel.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service tổng hợp (Facade/Orchestrator)
 * Chịu trách nhiệm xử lý các luồng nghiệp vụ (use-cases) phức tạp của module Nhân sự.
 */
@Service
@RequiredArgsConstructor
public class StaffManagementService {

    // Tiêm các service domain lẻ vào service tổng hợp
    private final EmployeeService employeeService;
    private final DepartmentService departmentService;
    private final PositionService positionService;

    /**
     * Nghiệp vụ: Tiếp nhận nhân sự mới (Onboarding)
     * Yêu cầu Transactional vì thay đổi dữ liệu ở nhiều domain khác nhau.
     */
    @Transactional
    public String onboardNewStaff(String fullName, Long departmentId, Long positionId) {
        System.out.println("--- Bắt đầu quy trình tiếp nhận nhân sự mới: " + fullName + " ---");

        // 1. Kiểm tra tính hợp lệ của Phòng ban
        departmentService.validateDepartment(departmentId);

        // 2. Kiểm tra tính khả dụng của Chức vụ
        positionService.validatePositionAvailability(positionId);

        // 3. Lấy thông tin lương cơ bản từ chức vụ
        double baseSalary = positionService.getBaseSalaryForPosition(positionId);

        // 4. Tạo hồ sơ nhân viên mới
        Long newEmpId = employeeService.createEmployee(fullName, departmentId, positionId, baseSalary);

        // 5. Cập nhật lại số lượng nhân sự của phòng ban
        departmentService.incrementEmployeeCount(departmentId);

        System.out.println("--- Hoàn tất quy trình tiếp nhận ---");
        return "Tiếp nhận thành công nhân viên mã số: " + newEmpId;
    }

    /**
     * Nghiệp vụ: Thuyên chuyển công tác (Transfer)
     */
    @Transactional
    public void transferStaff(Long employeeId, Long toDepartmentId, Long toPositionId) {
        System.out.println("--- Bắt đầu quy trình thuyên chuyển công tác ---");

        // Kiểm tra phòng ban và chức vụ mới
        departmentService.validateDepartment(toDepartmentId);
        positionService.validatePositionAvailability(toPositionId);

        // Cập nhật hồ sơ
        employeeService.updateEmployeeJobDetails(employeeId, toDepartmentId, toPositionId);

        // Có thể thêm logic: giảm số lượng nhân sự ở phòng cũ, tăng ở phòng mới...

        System.out.println("--- Hoàn tất quy trình thuyên chuyển ---");
    }
}