package com.example.demo.studentgrades.model.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StudentGradeRowDTO {
    private UUID registrationId;
    private String studentCode;
    private String firstName;
    private String lastName;
    private String dob;
    private String className;
    private List<GradeDetailDTO> grades;
    private Double finalScore;
    private Double t4Score;
    private String gradeLetter;
}
