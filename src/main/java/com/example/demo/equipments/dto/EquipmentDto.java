package com.example.demo.equipments.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.*;

import java.time.LocalDate;
import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EquipmentDto {

    private UUID id;

    @NotBlank(message = "Mã thiết bị không được trống")
    @Size(max = 50, message = "Mã thiết bị tối đa 50 ký tự")
    private String equipmentCode;

    @NotBlank(message = "Tên thiết bị không được trống")
    @Size(max = 150, message = "Tên thiết bị tối đa 150 ký tự")
    private String equipmentName;

    @Size(max = 100, message = "Số serial tối đa 100 ký tự")
    private String serialNumber;

    private LocalDate purchaseDate;

    private String status;

    private UUID roomId;
}
