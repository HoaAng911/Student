package com.example.demo.schedule_overrides.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ScheduleOverrideDto {

    private UUID id;

    @NotBlank(message = "Mã đặt phòng không được trống")
    @Size(max = 30, message = "Mã đặt phòng tối đa 30 ký tự")
    private String overrideCode;

    @NotNull(message = "Phòng không được trống")
    private UUID roomId;

    @NotNull(message = "Thời gian bắt đầu không được trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime startTime;

    @NotNull(message = "Thời gian kết thúc không được trống")
    @DateTimeFormat(pattern = "yyyy-MM-dd'T'HH:mm")
    private LocalDateTime endTime;

    @Size(max = 500, message = "Lý do tối đa 500 ký tự")
    private String reason;

    @Size(max = 20, message = "Trạng thái tối đa 20 ký tự")
    private String status;

    @NotNull(message = "Trạng thái hoạt động không được trống")
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
