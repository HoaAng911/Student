package com.example.demo.students.service;

import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import com.example.demo.students.model.entity.Student;
import com.example.demo.students.repository.StudentRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import java.util.UUID;

/**
 * Service xử lý logic nghiệp vụ cho module Sinh viên.
 * Tách biệt logic xử lý dữ liệu khỏi Controller.
 */
@Service
public class StudentService {
    private final StudentRepository repo;

    public StudentService(StudentRepository repo) {
        this.repo = repo;
    }

    /** Lấy danh sách toàn bộ sinh viên */
    public List<Student> getAll() {
        return repo.findAll();
    }

    /** Lấy danh sách sinh viên có phân trang (cho UI lớn) */
    public Page<Student> getAllPaged(Pageable pageable) {
        return repo.findAll(pageable);
    }

    /** Tìm một sinh viên theo ID */
    public Student getById(UUID id) {
        return repo.findById(id).orElse(null);
    }

    /** Lưu mới một sinh viên */
    @Transactional
    public Student create(Student student) {
        // JPA sẽ tự động gọi @PrePersist để gán ngày tạo
        return repo.save(student);
    }

    /** Cập nhật thông tin sinh viên */
    @Transactional
    @SuppressWarnings("unused")
    public Student update(UUID id, Student student) {
        Student existing = getById(id);
        if (existing == null) return null;

        // Cập nhật các trường thông tin cơ bản
        existing.setFullname(student.getFullname());
        existing.setCode(student.getCode());
        existing.setGender(student.getGender());
        existing.setDate_of_birth(student.getDate_of_birth());
        existing.setAddress(student.getAddress());
        existing.setCurrent_address(student.getCurrent_address());
        existing.setStatus(student.getStatus());
        
        // Cập nhật các trường bổ sung nếu có
        existing.setPersonal_identification_number(student.getPersonal_identification_number());
        existing.setCard_place(student.getCard_place());
        
        return repo.save(existing);
    }

    /** Xóa sinh viên theo ID (Soft Delete) */
    @Transactional
    public void delete(UUID id) {
        Student existing = getById(id);
        if (existing != null) {
            existing.setIsActive(false);
            existing.setDeletedAt(java.time.LocalDateTime.now());
            repo.save(existing);
        }
    }

    /** Tìm kiếm sinh viên theo tên (không phân biệt hoa thường) */
    public List<Student> search(String fullname) {
        return repo.findByFullnameContainingIgnoreCase(fullname);
    }

    /** Tìm kiếm sinh viên có phân trang */
    public org.springframework.data.domain.Page<Student> searchPaged(String fullname, org.springframework.data.domain.Pageable pageable) {
        return repo.findByFullnameContainingIgnoreCase(fullname, pageable);
    }
}