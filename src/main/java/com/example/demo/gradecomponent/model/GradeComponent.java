package com.example.demo.gradecomponent.model;

import jakarta.persistence.*;
import org.hibernate.annotations.UuidGenerator;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

@Entity
@Table(name = "n8_v3_grade_components")
@SQLDelete(sql = "UPDATE n8_v3_grade_components SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@Where(clause = "deleted_at IS NULL")
public class GradeComponent {

    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(name = "id", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "course_section_id", nullable = false)
    private UUID courseSectionId;

    @Column(name = "component_code", length = 50, nullable = false, columnDefinition = "nvarchar(50)")
    private String componentCode;

    @Column(name = "component_name", length = 50, nullable = false, columnDefinition = "nvarchar(50)")
    private String componentName;

    @Column(name = "weight_percentage", precision = 5, scale = 2, nullable = false)
    private BigDecimal weightPercentage;

    @Column(name = "min_score", precision = 4, scale = 2)
    private BigDecimal minScore = BigDecimal.ZERO;

    @Column(name = "max_score", precision = 4, scale = 2)
    private BigDecimal maxScore = new BigDecimal("10.00");

    @Column(name = "is_required")
    private Boolean isRequired = true;

    @Column(name = "input_order")
    private Integer inputOrder = 0;

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

    // JPA Hooks
    @PrePersist
    protected void onCreate() { this.createdAt = LocalDateTime.now(); }

    @PreUpdate
    protected void onUpdate() { this.updatedAt = LocalDateTime.now(); }

    // GETTERS AND SETTERS
    public UUID getId() { return id; }
    public void setId(UUID id) { this.id = id; }

    public UUID getCourseSectionId() { return courseSectionId; }
    public void setCourseSectionId(UUID courseSectionId) { this.courseSectionId = courseSectionId; }

    public String getComponentCode() { return componentCode; }
    public void setComponentCode(String componentCode) { this.componentCode = componentCode; }

    public String getComponentName() { return componentName; }
    public void setComponentName(String componentName) { this.componentName = componentName; }

    public BigDecimal getWeightPercentage() { return weightPercentage; }
    public void setWeightPercentage(BigDecimal weightPercentage) { this.weightPercentage = weightPercentage; }

    public BigDecimal getMinScore() { return minScore; }
    public void setMinScore(BigDecimal minScore) { this.minScore = minScore; }

    public BigDecimal getMaxScore() { return maxScore; }
    public void setMaxScore(BigDecimal maxScore) { this.maxScore = maxScore; }

    public Boolean getIsRequired() { return isRequired; }
    public void setIsRequired(Boolean isRequired) { this.isRequired = isRequired; }

    public Integer getInputOrder() { return inputOrder; }
    public void setInputOrder(Integer inputOrder) { this.inputOrder = inputOrder; }

    public Boolean getIsActive() { return isActive; }
    public void setIsActive(Boolean isActive) { this.isActive = isActive; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public LocalDateTime getUpdatedAt() { return updatedAt; }

    public LocalDateTime getDeletedAt() { return deletedAt; }
    public void setDeletedAt(LocalDateTime deletedAt) { this.deletedAt = deletedAt; }

    public UUID getDeletedBy() { return deletedBy; }
    public void setDeletedBy(UUID deletedBy) { this.deletedBy = deletedBy; }
}