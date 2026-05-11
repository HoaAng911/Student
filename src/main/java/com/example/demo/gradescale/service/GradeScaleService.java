package com.example.demo.gradescale.service;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.gradescale.model.GradeScale;
import com.example.demo.gradescale.repository.GradeScaleRepository;

@Service
public class GradeScaleService {

    @Autowired
    private GradeScaleRepository gradeScaleRepository;

    public List<GradeScale> getAllGradeScales() {
        return gradeScaleRepository.findAll();
    }

    public GradeScale getGradeScaleById(UUID id) {
        return gradeScaleRepository.findById(id).orElse(null);
    }

    public GradeScale saveGradeScale(GradeScale gradeScale) {
        return gradeScaleRepository.save(gradeScale);
    }

    public void deleteGradeScale(UUID id) {
        gradeScaleRepository.deleteById(id);
    }
}
