package com.example.demo.gradecomponent.model;

import com.example.demo.courses.model.entity.CourseSection;
import com.example.demo.courses.model.entity.Course;
import com.example.demo.gradeconfig.model.GradeCategory;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "n8_v3_grade_components")
@SQLDelete(sql = "UPDATE n8_v3_grade_components SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GradeComponent {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_section_id", nullable = false)
    private CourseSection courseSection;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "grade_category_id")
    private GradeCategory gradeCategory;

    @Builder.Default
    @Column(name = "is_final")
    private Boolean isFinal = false;

    @Column(name = "component_code", length = 50, nullable = false, columnDefinition = "nvarchar(50)")
    private String componentCode;

    @Column(name = "component_name", length = 50, nullable = false, columnDefinition = "nvarchar(50)")
    private String componentName;

    @Column(name = "weight_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal weightPercentage;

    @Builder.Default
    @Column(name = "min_score", precision = 4, scale = 2)
    private BigDecimal minScore = BigDecimal.ZERO;

    @Builder.Default
    @Column(name = "max_score", precision = 4, scale = 2)
    private BigDecimal maxScore = new BigDecimal("10.00");

    @Builder.Default
    @Column(name = "is_required")
    private Boolean isRequired = true;

    @Builder.Default
    @Column(name = "input_order")
    private Integer inputOrder = 0;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "created_by")
    private UUID createdBy;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "updated_by")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by")
    private UUID deletedBy;

    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }
}