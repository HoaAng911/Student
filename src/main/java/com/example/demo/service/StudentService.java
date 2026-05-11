package com.example.demo.service;

import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class StudentService {

    private final StudentRepository studentRepository;

    public List<Student> getAll() {
        return studentRepository.findAll();
    }

    public Page<Student> getAllPaged(Pageable pageable) {
        return studentRepository.findAll(pageable);
    }

    public Student save(Student student) {
        return studentRepository.save(student);
    }

    public void delete(UUID id) {
        studentRepository.deleteById(id);
    }
}
