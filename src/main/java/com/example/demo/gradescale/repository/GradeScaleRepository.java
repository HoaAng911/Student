package com.example.demo.gradescale.repository;

import java.util.UUID;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import com.example.demo.gradescale.model.GradeScale;

@Repository
public interface GradeScaleRepository extends JpaRepository<GradeScale, UUID> {
    // Bạn có thể thêm các phương thức tìm kiếm theo scaleCode nếu cần
    boolean existsByScaleCode(String scaleCode);
}
