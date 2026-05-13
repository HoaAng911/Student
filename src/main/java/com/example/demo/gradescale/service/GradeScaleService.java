package com.example.demo.gradescale.service;

import java.util.List;
import java.util.UUID;
import java.math.BigDecimal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.demo.gradescale.model.GradeScale;
import com.example.demo.gradescale.repository.GradeScaleRepository;

/**
 * Service quản lý Thang điểm (Quy đổi điểm số sang điểm chữ).
 * Vd: 8.5 -> A, 4.0 -> D.
 */
@Service
public class GradeScaleService {

    @Autowired
    private GradeScaleRepository gradeScaleRepository;

    /** Lấy toàn bộ danh sách thang điểm cấu hình */
    public List<GradeScale> getAllGradeScales() {
        return gradeScaleRepository.findAll();
    }

    /** Tìm thang điểm theo ID */
    public GradeScale getGradeScaleById(UUID id) {
        return gradeScaleRepository.findById(id).orElse(null);
    }

    /** Lưu cấu hình thang điểm */
    @Transactional
    public GradeScale saveGradeScale(GradeScale gradeScale) {
        return gradeScaleRepository.save(gradeScale);
    }

    /** Xóa cấu hình thang điểm */
    @Transactional
    public void deleteGradeScale(UUID id) {
        gradeScaleRepository.deleteById(id);
    }

    /** 
     * Tìm thang điểm phù hợp cho một mức điểm số.
     * Vd: 8.5 nằm trong khoảng [8.5, 10.0] -> Trả về GradeScale loại A.
     */
    public GradeScale getScaleForScore(BigDecimal score) {
        if (score == null) return null;
        List<GradeScale> scales = gradeScaleRepository.findAll();
        return scales.stream()
            .filter(s -> score.compareTo(s.getMinScore()) >= 0 && score.compareTo(s.getMaxScore()) <= 0)
            .findFirst()
            .orElse(null);
    }
}

