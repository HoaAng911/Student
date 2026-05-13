package com.example.demo.courses.model.entity;

import com.example.demo.students.model.entity.Student;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "course_registrations")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Registration {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "student_id")
    private Student student;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_section_id")
    private CourseSection courseSection; // Liên kết với lớp học phần (course_class_id)

    @Column(name = "registration_period_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID registrationPeriodId;

    @Column(name = "registration_type")
    private Integer registrationType; // 1: Học mới; 2: Học lại; 3: Cải thiện

    @Column(name = "replaced_grade_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID replacedGradeId;

    @Column(name = "registered_at")
    @Builder.Default
    private LocalDateTime registeredAt = LocalDateTime.now();

    @Column(name = "status")
    private Integer status; // 1: Thành công; 2: Chờ thanh toán; 3: Đã hủy

    @Column(name = "is_paid")
    @Builder.Default
    private Boolean isPaid = false;

    @Version
    @Column(name = "row_version", insertable = false, updatable = false, columnDefinition = "ROWVERSION")
    private byte[] rowVersion;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @Column(name = "updated_at")
    private LocalDateTime updatedAt;

    @Column(name = "created_by", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID createdBy;

    @Column(name = "updated_by", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID updatedBy;

    @Column(name = "deleted_at")
    private LocalDateTime deletedAt;

    @Column(name = "deleted_by", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID deletedBy;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;

    @PrePersist
    protected void onCreate() {
        createdAt = LocalDateTime.now();
        updatedAt = LocalDateTime.now();
        if (registeredAt == null) registeredAt = LocalDateTime.now();
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

