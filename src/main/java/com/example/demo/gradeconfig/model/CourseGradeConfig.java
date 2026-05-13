package com.example.demo.gradeconfig.model;

import com.example.demo.courses.model.entity.Course;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.util.UUID;

@Entity
@Table(name = "course_grade_configs")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CourseGradeConfig {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id", nullable = false)
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_category_id", nullable = false)
    private GradeCategory gradeCategory;

    @Column(name = "default_weight_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal defaultWeightPercentage;

    @Column(name = "input_order")
    private Integer inputOrder = 0;

    @Builder.Default
    @Column(name = "is_required")
    private Boolean isRequired = true;
}
