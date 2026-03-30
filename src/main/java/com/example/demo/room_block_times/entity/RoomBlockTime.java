package com.example.demo.room_block_times.entity;

import com.example.demo.rooms.entity.Room;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "room_block_times")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomBlockTime {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "block_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID blockId;

    @Column(name = "room_id", nullable = false, columnDefinition = "UNIQUEIDENTIFIER")
    private UUID roomId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "room_id", insertable = false, updatable = false, nullable = false)
    private Room room;

    @Column(name = "block_type", columnDefinition = "NVARCHAR(30)")
    private String blockType;

    @Column(name = "day_of_week")
    private Integer dayOfWeek;

    @Column(name = "time_slot_id", columnDefinition = "UNIQUEIDENTIFIER")
    private UUID timeSlotId;

    @Column(name = "start_week")
    private Integer startWeek;

    @Column(name = "end_week")
    private Integer endWeek;

    @Column(name = "start_date")
    private LocalDate startDate;

    @Column(name = "end_date")
    private LocalDate endDate;

    @Column(name = "reason", columnDefinition = "NVARCHAR(255)")
    private String reason;

    @Column(name = "status", columnDefinition = "NVARCHAR(20)")
    private String status;

    @Column(name = "created_at", nullable = false, columnDefinition = "DATETIME2")
    private LocalDateTime createdAt;

    @Column(name = "updated_at", nullable = false, columnDefinition = "DATETIME2")
    private LocalDateTime updatedAt;

    @PrePersist
    public void prePersist() {
        LocalDateTime now = LocalDateTime.now();
        this.createdAt = now;
        this.updatedAt = now;
        if (this.status == null) {
            this.status = "ACTIVE"; // Default status
        }
    }

    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
