package com.example.demo.modules.personnel.service;

import org.springframework.stereotype.Service;

@Service
public class DepartmentService {

    /**
     * Kiểm tra xem phòng ban có tồn tại và còn chỉ tiêu nhân sự hay không
     */
    public void validateDepartment(Long departmentId) {
        // Logic truy vấn database kiểm tra phòng ban
        System.out.println("Kiểm tra tính hợp lệ của phòng ban có ID: " + departmentId);
        // Ném exception nếu phòng ban không hợp lệ (ví dụ: DepartmentNotFoundException)
    }

    /**
     * Tăng số lượng nhân viên hiện tại của phòng ban lên 1
     */
    public void incrementEmployeeCount(Long departmentId) {
        // Logic cập nhật số lượng nhân sự trong bảng Department
        System.out.println("Đã tăng số lượng nhân sự cho phòng ban ID: " + departmentId);
    }

    // Các phương thức CRUD khác: createDepartment, getDepartmentById, v.v.
}