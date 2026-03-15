package com.example.demo.rome_types.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "room_types")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomType {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "room_type_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID roomTypeId;

    @Column(name = "room_type_code", nullable = false, unique = true,
            columnDefinition = "NVARCHAR(20)")
    private String roomTypeCode;

    @Column(name = "room_type_name", nullable = false,
            columnDefinition = "NVARCHAR(150)")
    private String roomTypeName;

    @Column(name = "description", columnDefinition = "NVARCHAR(255)")
    private String description;

    @Column(name = "max_capacity")
    private Integer maxCapacity;
}
