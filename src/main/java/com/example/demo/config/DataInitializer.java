package com.example.demo.config;

import com.example.demo.gradecomponent.model.GradeComponent;
import com.example.demo.gradecomponent.repository.GradeComponentRepository;
import com.example.demo.studentgrades.model.entity.StudentGrade;
import com.example.demo.studentgrades.repository.StudentGradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.UUID;

@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final GradeComponentRepository gradeComponentRepository;
    private final StudentGradeRepository studentGradeRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        if (gradeComponentRepository.count() > 0) {
            return; // Tránh insert trùng lặp khi khởi động lại
        }

        UUID dummyCourseSectionId = UUID.randomUUID();
        UUID dummyRegistrationId = UUID.randomUUID();
        Random random = new Random();

        List<GradeComponent> components = new ArrayList<>();
        String[] names = {"Chuyên cần", "Bài tập", "Kiểm tra giữa kỳ", "Đồ án", "Thi cuối kỳ", 
                          "Thực hành 1", "Thực hành 2", "Thảo luận", "Tiểu luận", "Kiểm tra miệng"};
        String[] codes = {"CC", "BT", "KTGK", "DA", "TCK", "TH1", "TH2", "TL", "TILL", "KTM"};

        for (int i = 0; i < 10; i++) {
            GradeComponent gc = new GradeComponent();
            gc.setCourseSectionId(dummyCourseSectionId);
            gc.setComponentCode(codes[i]);
            gc.setComponentName(names[i]);
            gc.setWeightPercentage(new BigDecimal(10 * (i + 1) / 2.0));
            gc.setIsRequired(true);
            gc.setIsActive(true);
            gc.setInputOrder(i);
            components.add(gradeComponentRepository.save(gc));
        }

        for (int i = 0; i < 10; i++) {
            StudentGrade sg = StudentGrade.builder()
                    .registrationId(dummyRegistrationId)
                    .gradeComponentId(components.get(i).getId())
                    .score(new BigDecimal(5 + random.nextDouble() * 5).setScale(2, RoundingMode.HALF_UP))
                    .isRetake(false)
                    .isLocked(false)
                    .note("Dữ liệu mẫu " + (i + 1))
                    .isActive(true)
                    .build();
            studentGradeRepository.save(sg);
        }

        System.out.println(">>> Đã khởi tạo 10 dữ liệu mẫu cho GradeComponent và StudentGrade.");
    }
}
