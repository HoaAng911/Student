package com.example.demo.service;

import com.example.demo.model.GradeComponent;
import com.example.demo.repository.GradeComponentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
public class GradeComponentService {

    @Autowired
    private GradeComponentRepository repository;

    public List<GradeComponent> getAll() {
        return repository.findAll();
    }

    public List<GradeComponent> getByClass(UUID classId) {
        return repository.findByCourseClassIdOrderByInputOrderAsc(classId);
    }

    public GradeComponent save(GradeComponent data) {
        return repository.save(data);
    }

    public void delete(UUID id) {
        repository.deleteById(id);
    }
}