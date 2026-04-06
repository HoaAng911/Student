package com.example.demo.modules.personnel.service;

import org.springframework.stereotype.Service;

@Service
public class EmployeeService {

    /**
     * Lưu thông tin nhân viên mới vào cơ sở dữ liệu
     */
    public Long createEmployee(String fullName, Long departmentId, Long positionId, double startingSalary) {
        // Logic lưu Entity Employee vào database
        System.out.println("Đang lưu nhân viên mới: " + fullName);

        // Trả về ID của nhân viên sau khi lưu thành công (Mock ID: 999)
        Long newEmployeeId = 999L;
        return newEmployeeId;
    }

    /**
     * Cập nhật thông tin phòng ban và chức vụ mới cho nhân viên
     */
    public void updateEmployeeJobDetails(Long employeeId, Long newDepartmentId, Long newPositionId) {
        // Logic cập nhật thông tin nhân viên
        System.out.println("Đã cập nhật chức vụ/phòng ban mới cho nhân viên ID: " + employeeId);
    }

    // Các phương thức CRUD khác: findEmployeeById, updatePersonalInfor, v.v.
}