package com.example.demo.modules.personnel.repository;

import com.example.demo.modules.personnel.entity.Position;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public interface PositionRepository extends JpaRepository<Position, UUID> {

    // Tìm chức vụ theo mã code (Ví dụ: "TP", "GV")
    Optional<Position> findByCode(String code);

    // Lấy danh sách các chức vụ thuộc một khoa/phòng ban cụ thể
    List<Position> findByDepartmentId(UUID departmentId);

    // Lấy danh sách các chức vụ đang hoạt động
    List<Position> findByIsActiveTrue();

    // Kiểm tra xem mã chức vụ đã tồn tại chưa
    boolean existsByCode(String code);
}