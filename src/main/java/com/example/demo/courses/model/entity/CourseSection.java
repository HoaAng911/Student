package com.example.demo.courses.model.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "course_sections")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class CourseSection {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "code", length = 100)
    private String code; // Mã lớp học phần (VD: IT101-01)

    @Column(name = "lecturer_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID lecturerId; // Giảng viên phụ trách

    @Column(name = "name", length = 255, columnDefinition = "nvarchar(255)")
    private String name; // Tên lớp học phần

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "course_id")
    private Course course; // Liên kết tới môn học

    @Column(name = "semester_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID semesterId; // FK -> semesters.id

    @Column(name = "room_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID roomId; // Phòng học

    @Column(name = "building_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID buildingId; // Tòa nhà

    @Column(name = "max_students")
    private Integer maxStudents;

    @Column(name = "min_students")
    private Integer minStudents;

    @Column(name = "class_type", length = 255, columnDefinition = "nvarchar(255)")
    private String classType; // theory / lab / hybrid

    @Column(name = "status", length = 50, columnDefinition = "nvarchar(50)")
    private String status; // planned / open / closed / canceled

    @Column(name = "registration_start")
    private LocalDateTime registrationStart;

    @Column(name = "registration_end")
    private LocalDateTime registrationEnd;

    @Column(name = "note", length = 255, columnDefinition = "nvarchar(255)")
    private String note;

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
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = LocalDateTime.now();
    }
}

