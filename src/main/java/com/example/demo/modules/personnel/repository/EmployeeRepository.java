package com.example.demo.modules.personnel.repository;

import com.example.demo.modules.personnel.entity.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

    // Tìm nhân viên theo mã nhân viên
    Optional<Employee> findByEmployeeCode(String employeeCode);

    // Tìm nhân viên theo Email
    Optional<Employee> findByEmail(String email);

    // Tìm nhân viên theo user_id (tài khoản đăng nhập)
    Optional<Employee> findByUserId(UUID userId);

    // Lấy danh sách nhân viên thuộc một khoa/phòng ban cụ thể
    List<Employee> findByDepartmentId(UUID departmentId);

    // Lấy danh sách nhân viên có một chức vụ cụ thể
    List<Employee> findByPositionId(UUID positionId);

    // Lấy danh sách nhân viên đang làm việc (is_active = true)
    List<Employee> findByIsActiveTrue();

    // Kiểm tra xem mã nhân viên hoặc email đã tồn tại chưa (phục vụ validate khi thêm mới)
    boolean existsByEmployeeCode(String employeeCode);

    boolean existsByEmail(String email);
}