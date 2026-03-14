package com.example.nhom7.rome_types.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class RoomTypeListDto {

    private UUID roomTypeId;
    private String roomTypeCode;
    private String roomTypeName;
    private String description;
    private Integer maxCapacity;
}
