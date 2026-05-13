package com.example.demo.studentgrades.model.entity;

import com.example.demo.courses.model.entity.Registration;
import com.example.demo.gradecomponent.model.GradeComponent;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "n8_v3_student_grades")
@SQLDelete(sql = "UPDATE n8_v3_student_grades SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StudentGrade {

  @Id
  @GeneratedValue
  @org.hibernate.annotations.UuidGenerator
  private UUID id;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "registration_id")
  private Registration registration;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "grade_component_id")
  private GradeComponent gradeComponent;

  @DecimalMin("0.0")
  @DecimalMax("10.0")
  @Column(precision = 4, scale = 2)
  private BigDecimal score;

  private Boolean isTotal;

  private Boolean isRetake;

  private Boolean isLocked;

  @Column(length = 255, columnDefinition = "nvarchar(255)")
  private String note;

  @Column(length = 2, columnDefinition = "nvarchar(2)")
  private String letterGrade;

  @Column(precision = 3, scale = 2)
  private BigDecimal gpaValue;

  @Column(length = 10, columnDefinition = "nvarchar(10)")
  private String result;

  private UUID scaleId;

  private Boolean isFinalized;

  @Column(name = "status", length = 20)
  @Builder.Default
  private String status = "DRAFT"; // DRAFT, SUBMITTED, LOCKED

  @Column(name = "updated_by", columnDefinition = "UNIQUEIDENTIFIER")
  private UUID updatedBy;

  @Column(name = "history_note", length = 500, columnDefinition = "nvarchar(500)")
  private String historyNote;

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private UUID createdBy;

  private UUID gradedBy;

  private LocalDateTime gradedAt;

  private UUID lockedBy;

  private LocalDateTime lockedAt;

  private LocalDateTime deletedAt;

  private UUID deletedBy;

  @Builder.Default
  private Boolean isActive = true;

  // Helper methods for Thymeleaf binding
  public UUID getGradeComponentId() {
    return gradeComponent != null ? gradeComponent.getId() : null;
  }

  public void setGradeComponentId(UUID id) {
    if (id == null) {
      this.gradeComponent = null;
    } else {
      this.gradeComponent = GradeComponent.builder().id(id).build();
    }
  }

  public UUID getRegistrationId() {
    return registration != null ? registration.getId() : null;
  }

  public void setRegistrationId(UUID id) {
    if (id == null) {
      this.registration = null;
    } else {
      this.registration = Registration.builder().id(id).build();
    }
  }
}
