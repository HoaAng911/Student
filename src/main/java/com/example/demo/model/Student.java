package com.example.demo.model;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.UuidGenerator;
import java.util.UUID;

@Entity
@Table(name = "n8_v3_students")
@Getter @Setter
@NoArgsConstructor @AllArgsConstructor
@Builder
public class Student {

    @Id
    @GeneratedValue
    @UuidGenerator
    private UUID id;

    @Column(nullable = false, length = 150, columnDefinition = "nvarchar(150)")
    private String name;

    @Column(unique = true, nullable = false, length = 50)
    private String studentCode;

    @Column(length = 150)
    private String email;

    @Builder.Default
    private Boolean isActive = true;
}
