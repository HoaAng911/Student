package com.example.demo.gradeconfig.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(name = "grade_categories")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class GradeCategory {
    @Id
    @GeneratedValue
    @UuidGenerator
    @Column(columnDefinition = "UNIQUEIDENTIFIER", updatable = false, nullable = false)
    private UUID id;

    @Column(name = "code", length = 50, unique = true, nullable = false)
    private String code; // VD: CC, GK, CK, BT

    @Column(name = "name", length = 255, nullable = false, columnDefinition = "nvarchar(255)")
    private String name; // VD: Chuyên cần, Giữa kỳ, Cuối kỳ

    @Column(name = "description", columnDefinition = "nvarchar(MAX)")
    private String description;

    @Builder.Default
    @Column(name = "is_active")
    private Boolean isActive = true;
}
