package com.example.demo.studentgrades.model.dto;

import com.example.demo.gradecomponent.model.GradeComponent;
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
public class ClassGradeReportDTO {
    private UUID courseSectionId;
    private String courseName;
    private List<GradeComponent> headers; // Các cột điểm động
    private List<StudentGradeRowDTO> rows; // Dữ liệu từng sinh viên
    
    // Thống kê
    private long totalStudents;
    private long passCount;
    private long failCount;
    private java.util.Map<String, Long> letterDistribution;
}
