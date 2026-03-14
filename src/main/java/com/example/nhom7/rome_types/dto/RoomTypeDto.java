package com.example.nhom7.rome_types.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeDto {

    private UUID roomTypeId;

    @NotBlank(message = "Mã loại phòng không được trống")
    @Size(max = 20, message = "Mã loại phòng tối đa 20 ký tự")
    private String roomTypeCode;

    @NotBlank(message = "Tên loại phòng không được trống")
    @Size(max = 150, message = "Tên loại phòng tối đa 150 ký tự")
    private String roomTypeName;

    @Size(max = 255, message = "Mô tả tối đa 255 ký tự")
    private String description;

    private Integer maxCapacity;
}
