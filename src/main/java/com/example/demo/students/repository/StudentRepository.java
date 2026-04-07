package com.example.demo.students.repository;

import com.example.demo.students.model.entity.Student;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;

public interface StudentRepository extends JpaRepository<Student, UUID> {
    List<Student> findByFullnameContainingIgnoreCase(String fullname);
}
