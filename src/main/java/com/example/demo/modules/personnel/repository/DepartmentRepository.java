package com.example.demo.modules.personnel.repository;

import com.example.demo.modules.personnel.entity.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {

    // Tìm khoa/phòng ban theo mã code (Ví dụ: "CNTT")
    Optional<Department> findByCode(String code);

    // Lấy danh sách các khoa/phòng ban đang hoạt động (is_active = true)
    List<Department> findByIsActiveTrue();

    // Kiểm tra xem mã khoa đã tồn tại chưa
    boolean existsByCode(String code);
}