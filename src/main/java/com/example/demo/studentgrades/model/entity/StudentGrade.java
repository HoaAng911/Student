package com.example.demo.studentgrades.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.hibernate.annotations.SQLDelete;
import org.hibernate.annotations.Where;

import jakarta.validation.constraints.DecimalMax;
import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "n8_v3_student_grades")
@SQLDelete(sql = "UPDATE n8_v3_student_grades SET deleted_at = CURRENT_TIMESTAMP WHERE id=?")
@Where(clause = "deleted_at IS NULL")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class StudentGrade {

  @Id
  @GeneratedValue
  @org.hibernate.annotations.UuidGenerator
  private UUID id;

  private UUID registrationId;

  private UUID gradeComponentId;

  @DecimalMin("0.0") @DecimalMax("10.0")
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

  @CreationTimestamp
  @Column(updatable = false)
  private LocalDateTime createdAt;

  @UpdateTimestamp
  private LocalDateTime updatedAt;

  private UUID createdBy;

  private UUID updatedBy;

  private LocalDateTime deletedAt;

  private UUID deletedBy;

  @Builder.Default
  private Boolean isActive = true;
}
