package com.example.demo.buildings.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BuildingDto {

    private UUID id;

    @NotBlank(message = "Mã toà nhà không được trống")
    @Size(max = 20, message = "Mã toà nhà tối đa 20 ký tự")
    private String buildingCode;

    @NotBlank(message = "Tên toà nhà không được trống")
    @Size(max = 100, message = "Tên toà nhà tối đa 100 ký tự")
    private String buildingName;

    @Size(max = 255, message = "Địa chỉ tối đa 255 ký tự")
    private String address;

    @Size(max = 500, message = "Mô tả tối đa 500 ký tự")
    private String description;

    private Boolean isActive;
}
