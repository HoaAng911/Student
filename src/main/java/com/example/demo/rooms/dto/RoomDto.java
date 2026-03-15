package com.example.demo.rooms.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomDto {

    private UUID id;

    @NotBlank(message = "Mã phòng không được trống")
    @Size(max = 20, message = "Mã phòng tối đa 20 ký tự")
    private String roomCode;

    @NotBlank(message = "Tên phòng không được trống")
    @Size(max = 100, message = "Tên phòng tối đa 100 ký tự")
    private String roomName;

    @NotNull(message = "Tòa nhà không được trống")
    private UUID buildingId;

    @NotNull(message = "Loại phòng không được trống")
    private UUID roomTypeId;

    @Min(value = -10, message = "Tầng không hợp lệ")
    @Max(value = 200, message = "Tầng không hợp lệ")
    private Integer floor;

    @Min(value = 0, message = "Sức chứa phải >= 0")
    private Integer capacity;

    @Positive(message = "Diện tích phải > 0")
    private Double area;

    @Size(max = 20, message = "Trạng thái tối đa 20 ký tự")
    private String status;

    @NotNull(message = "Trạng thái hoạt động không được trống")
    private Boolean isActive;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}

