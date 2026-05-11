package com.example.demo.config;

import com.example.demo.gradecomponent.model.GradeComponent;
import com.example.demo.gradecomponent.repository.GradeComponentRepository;
import com.example.demo.gradescale.model.GradeScale;
import com.example.demo.gradescale.repository.GradeScaleRepository;
import com.example.demo.model.Student;
import com.example.demo.repository.StudentRepository;
import com.example.demo.studentgrades.model.entity.StudentGrade;
import com.example.demo.studentgrades.repository.StudentGradeRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

// @Configuration
@RequiredArgsConstructor
@Slf4j
public class DataSeeder implements CommandLineRunner {

    private final StudentRepository studentRepository;
    private final GradeScaleRepository gradeScaleRepository;
    private final GradeComponentRepository gradeComponentRepository;
    private final StudentGradeRepository studentGradeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        log.info("--- B\u1eaft \u0111\u1ea7u qu\u00e1 tr\u00ecnh Seed d\u1eef li\u1ec7u m\u1eabu ---");
        
        try {
            // X\u00f3a d\u1eef li\u1ec7u c\u0169 theo th\u1ee9 t\u1ef1 \u0111\u00fang \u0111\u1ec3 tr\u00e1nh l\u1ed7i kh\u00f3a ngo\u1ea1i
            studentGradeRepository.deleteAllInBatch();
            gradeComponentRepository.deleteAllInBatch();
            gradeScaleRepository.deleteAllInBatch();
            studentRepository.deleteAllInBatch();
            
            // Flush \u0111\u1ec3 \u0111\u1ea3m b\u1ea3o DB tr\u1ed1ng tr\u01b0\u1edbc khi ch\u00e8n
            log.info("\u0110\u00e3 l\u00e0m s\u1ea1ch d\u1eef li\u1ec7u c\u0169.");

            seedStudents();
            seedGradeScales();
            seedGradeComponents();
            seedStudentGrades();
            
            log.info("--- Ho\u00e0n t\u1ea5t Seed d\u1eef li\u1ec7u m\u1eabu th\u00e0nh c\u00f4ng! ---");
        } catch (Exception e) {
            log.error("L\u1ed7i trong qu\u00e1 tr\u00ecnh seed: " + e.getMessage());
            throw e;
        }
    }

    private void seedStudents() {
        // S\u1eed d\u1ee5ng Unicode Escape \u0111\u1ec3 \u0111\u1ea3m b\u1ea3o kh\u00f4ng b\u1ecb l\u1ed7i encoding d\u00f9 compile \u1edf \u0111\u00e2u
        String[] names = {
            "Nguy\u1ec5n V\u0103n An", "Tr\u1ea7n Th\u1ecb B\u00ecnh", "L\u00ea V\u0103n C\u01b0\u1eddng", "Ph\u1ea1m Th\u1ecb Dung", 
            "Ho\u00e0ng V\u0103n Em", "V\u0169 Th\u1ecb Ph\u01b0\u01a1ng", "\u0110\u1ed7 V\u0103n Giang", "B\u00f9i Th\u1ecb H\u1ea1nh", 
            "L\u00fd V\u0103n H\u00f9ng", "Ng\u00f4 Th\u1ecb Lan"
        };
        List<Student> students = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            students.add(Student.builder()
                .name(names[i])
                .studentCode("SV" + (1000 + i))
                .email("student" + (i + 1) + "@school.edu.vn")
                .isActive(true)
                .build());
        }
        studentRepository.saveAll(students);
    }

    private void seedGradeScales() {
        Object[][] scales = {
            {"A", 8.5, 10.0, "A", 4.0, true},
            {"B+", 8.0, 8.4, "B+", 3.5, true},
            {"B", 7.0, 7.9, "B", 3.0, true},
            {"C+", 6.5, 6.9, "C+", 2.5, true},
            {"C", 5.5, 6.4, "C", 2.0, true},
            {"D+", 5.0, 5.4, "D+", 1.5, true},
            {"D", 4.0, 4.9, "D", 1.0, true},
            {"F", 0.0, 3.9, "F", 0.0, false},
            {"I", 0.0, 0.0, "I", 0.0, false},
            {"X", 0.0, 0.0, "X", 0.0, false}
        };

        List<GradeScale> gradeScales = new ArrayList<>();
        for (Object[] s : scales) {
            GradeScale gs = new GradeScale();
            gs.setScaleCode((String) s[0]);
            gs.setMinScore(new BigDecimal(s[1].toString()));
            gs.setMaxScore(new BigDecimal(s[2].toString()));
            gs.setLetterGrade((String) s[3]);
            gs.setGpaValue(new BigDecimal(s[4].toString()));
            gs.setIsPass((Boolean) s[5]);
            gs.setIsActive(true);
            gradeScales.add(gs);
        }
        gradeScaleRepository.saveAll(gradeScales);
    }

    private void seedGradeComponents() {
        String[] components = {
            "Chuy\u00ean c\u1ea7n", "B\u00e0i t\u1eadp", "Gi\u1eefa k\u1ef3", "Th\u1ef1c h\u00e0nh 1", 
            "Th\u1ef1c h\u00e0nh 2", "Ti\u1ec3u lu\u1eadn", "Th\u1ea3o lu\u1eadn", "\u0110\u1ed3 \u00e1n", 
            "Cu\u1ed1i k\u1ef3", "Kh\u00f3a lu\u1eadn"
        };
        String[] codes = {"CC", "BT", "GK", "TH1", "TH2", "TL", "TLN", "DA", "CK", "KL"};
        double[] weights = {10, 10, 20, 10, 10, 15, 5, 20, 50, 100};

        List<GradeComponent> gradeComponents = new ArrayList<>();
        UUID sectionId = UUID.randomUUID();
        for (int i = 0; i < 10; i++) {
            GradeComponent gc = new GradeComponent();
            gc.setCourseSectionId(sectionId);
            gc.setComponentCode(codes[i]);
            gc.setComponentName(components[i]);
            gc.setWeightPercentage(new BigDecimal(weights[i]));
            gc.setIsRequired(true);
            gc.setIsActive(true);
            gradeComponents.add(gc);
        }
        gradeComponentRepository.saveAll(gradeComponents);
    }

    private void seedStudentGrades() {
        List<Student> students = studentRepository.findAll();
        List<GradeComponent> components = gradeComponentRepository.findAll();
        List<StudentGrade> grades = new ArrayList<>();
        Random random = new Random();

        for (int i = 0; i < 10; i++) {
            Student student = students.get(i % students.size());
            GradeComponent component = components.get(i % components.size());
            
            BigDecimal score = new BigDecimal(4.0 + (random.nextDouble() * 6.0)).setScale(2, RoundingMode.HALF_UP);
            
            grades.add(StudentGrade.builder()
                .registrationId(student.getId())
                .gradeComponentId(component.getId())
                .score(score)
                .isRetake(false)
                .isLocked(false)
                .isActive(true)
                .note("Ghi ch\u00fa m\u1eabu cho " + student.getName())
                .build());
        }
        studentGradeRepository.saveAll(grades);
    }
}
