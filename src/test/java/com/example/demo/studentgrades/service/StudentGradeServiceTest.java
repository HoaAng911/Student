package com.example.demo.studentgrades.service;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

import com.example.demo.courses.model.entity.CourseSection;
import com.example.demo.courses.model.entity.Registration;
import com.example.demo.courses.repository.CourseSectionRepository;
import com.example.demo.courses.repository.RegistrationRepository;
import com.example.demo.gradecomponent.model.GradeComponent;
import com.example.demo.gradecomponent.repository.GradeComponentRepository;
import com.example.demo.gradescale.model.GradeScale;
import com.example.demo.gradescale.service.GradeScaleService;
import com.example.demo.studentgrades.model.dto.ClassGradeReportDTO;
import com.example.demo.studentgrades.model.entity.StudentGrade;
import com.example.demo.studentgrades.repository.StudentGradeRepository;
import com.example.demo.students.model.entity.Student;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.*;

@ExtendWith(MockitoExtension.class)
public class StudentGradeServiceTest {

    @Mock
    private StudentGradeRepository gradeRepository;
    @Mock
    private GradeComponentRepository componentRepository;
    @Mock
    private RegistrationRepository registrationRepository;
    @Mock
    private CourseSectionRepository courseSectionRepository;
    @Mock
    private GradeScaleService gradeScaleService;

    @InjectMocks
    private StudentGradeService studentGradeService;

    private UUID sectionId;
    private CourseSection section;
    private List<GradeComponent> components;
    private List<Registration> registrations;

    @BeforeEach
    void setUp() {
        sectionId = UUID.randomUUID();
        
        // 1. Giả lập Lớp học
        section = CourseSection.builder().id(sectionId).name("Java Unit Test Class").build();
        
        // 2. Giả lập 3 đầu điểm: CC(10%), GK(20%), CK(70%)
        components = new ArrayList<>();
        components.add(GradeComponent.builder().id(UUID.randomUUID()).componentCode("CC").componentName("Chuyên cần").weightPercentage(new BigDecimal("10")).inputOrder(0).build());
        components.add(GradeComponent.builder().id(UUID.randomUUID()).componentCode("GK").componentName("Giữa kỳ").weightPercentage(new BigDecimal("20")).inputOrder(1).build());
        components.add(GradeComponent.builder().id(UUID.randomUUID()).componentCode("CK").componentName("Cuối kỳ").weightPercentage(new BigDecimal("70")).inputOrder(2).build());

        // 3. Giả lập 2 sinh viên đăng ký
        registrations = new ArrayList<>();
        Student s1 = Student.builder().id(UUID.randomUUID()).code("SV001").fullname("Nguyen Van A").build();
        Student s2 = Student.builder().id(UUID.randomUUID()).code("SV002").fullname("Tran Thi B").build();
        
        registrations.add(Registration.builder().id(UUID.randomUUID()).student(s1).build());
        registrations.add(Registration.builder().id(UUID.randomUUID()).student(s2).build());
    }

    @Test
    @DisplayName("Test lấy báo cáo điểm lớp học phần - Đảm bảo hiển thị đủ sinh viên và tính đúng điểm")
    void testGetClassGradeReport() {
        // GIVEN
        when(componentRepository.findByCourseSection_IdOrderByInputOrderAsc(sectionId)).thenReturn(components);
        when(registrationRepository.findByCourseSectionIdAndIsActiveTrue(sectionId)).thenReturn(registrations);
        when(courseSectionRepository.findById(sectionId)).thenReturn(Optional.of(section));

        // Giả lập SV001 đã có 1 đầu điểm CC = 10
        StudentGrade g1 = StudentGrade.builder()
                .registration(registrations.get(0))
                .gradeComponent(components.get(0))
                .score(new BigDecimal("10.0"))
                .isActive(true)
                .build();
        when(gradeRepository.findByGradeComponent_IdInAndIsActiveTrue(any())).thenReturn(Collections.singletonList(g1));

        // Giả lập thang điểm quy đổi (A cho điểm >= 8.5)
        GradeScale scaleA = new GradeScale();
        scaleA.setLetterGrade("A");
        scaleA.setGpaValue(new BigDecimal("4.0"));
        when(gradeScaleService.getScaleForScore(any())).thenReturn(scaleA);

        // WHEN
        ClassGradeReportDTO report = studentGradeService.getClassGradeReport(sectionId);

        // THEN
        assertNotNull(report);
        assertEquals(2, report.getRows().size(), "Phải hiển thị đủ 2 sinh viên");
        assertEquals(3, report.getHeaders().size(), "Phải có đủ 3 đầu điểm header");

        // Kiểm tra SV001 (Người có điểm CC=10)
        var row1 = report.getRows().stream().filter(r -> r.getStudentCode().equals("SV001")).findFirst().get();
        assertEquals(1.0, row1.getFinalScore(), "Điểm tổng kết tạm tính: 10 * 10% = 1.0");

        // Kiểm tra SV002 (Người chưa có điểm)
        var row2 = report.getRows().stream().filter(r -> r.getStudentCode().equals("SV002")).findFirst().get();
        assertEquals(0.0, row2.getFinalScore(), "Chưa có điểm thì tổng kết phải là 0");
        assertNull(row2.getGrades().get(0).getScore(), "Ô điểm phải để trống (null)");

        verify(gradeScaleService, atLeastOnce()).getScaleForScore(any());
    }
}
