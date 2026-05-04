package com.example.demo.gradestudent.service;

import com.example.demo.gradestudent.model.GradeStudent;
import com.example.demo.gradestudent.repository.GradeStudentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class GradeStudentService {

    @Autowired
    private GradeStudentRepository repository;

    public List<GradeStudent> getGradesByStudentId(UUID studentId) {
        return repository.findByStudentId(studentId);
    }

    public GradeStudent createGrade(GradeStudent gradeStudent) {
        return repository.save(gradeStudent);
    }

    public void deleteGrade(UUID id) {
        repository.deleteById(id);
    }
}