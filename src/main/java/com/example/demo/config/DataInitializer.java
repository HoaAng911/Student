package com.example.demo.config;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Configuration;
import com.example.demo.users.model.entity.User;
import com.example.demo.roles.model.entity.Role;
import org.springframework.transaction.annotation.Transactional;

/**
 * DataInitializer: Đã chuyển sang sử dụng SQL Script để quản lý dữ liệu mẫu.
 * Việc loại bỏ code Java giúp ứng dụng khởi động nhanh hơn và tránh xung đột dữ liệu.
 */
@Configuration
@RequiredArgsConstructor
public class DataInitializer implements CommandLineRunner {

    private final com.example.demo.users.repository.UserRepository userRepository;
    private final com.example.demo.roles.repository.RoleRepository roleRepository;
    private final com.example.demo.students.repository.StudentRepository studentRepository;
    private final com.example.demo.gradeconfig.repository.GradeCategoryRepository gradeCategoryRepository;

    @Override
    @Transactional
    public void run(String... args) throws Exception {
        System.out.println(">>> Đang kiểm tra và khởi tạo dữ liệu tài khoản...");

        // 1. Khởi tạo Roles nếu chưa có
        Role adminRole = getOrCreateRole("ADMIN", "Quản trị viên");
        Role teacherRole = getOrCreateRole("TEACHER", "Giảng viên");
        Role studentRole = getOrCreateRole("STUDENT", "Sinh viên");

        // 2. Khởi tạo Users mẫu
        createUserIfNotExist("admin", "admin123", "admin@edu.vn", adminRole);
        createUserIfNotExist("teacher", "teacher123", "teacher@edu.vn", teacherRole);
        User stdUser = createUserIfNotExist("student", "student123", "student@edu.vn", studentRole);

        // 3. Khởi tạo dữ liệu Sinh viên mẫu nếu là account student
        if (stdUser != null && studentRepository.findByUserId(stdUser.getId()).isEmpty()) {
            com.example.demo.students.model.entity.Student student = new com.example.demo.students.model.entity.Student();
            student.setUserId(stdUser.getId());
            student.setCode("SV001");
            student.setName("Nguyễn Văn Sinh Viên");
            student.setIsActive(true);
            studentRepository.save(student);
        }

        // 4. Khởi tạo Grade Categories mẫu
        System.out.println(">>> Đang khởi tạo danh mục điểm mẫu...");
        getOrCreateCategory("CC", "Chuyên cần");
        getOrCreateCategory("GK", "Giữa kỳ");
        getOrCreateCategory("CK", "Cuối kỳ");
        getOrCreateCategory("BT", "Bài tập (Assignment)");
        getOrCreateCategory("TH", "Thực hành (Lab)");
        getOrCreateCategory("DA", "Đồ án (Project)");
        getOrCreateCategory("TX", "Kiểm tra thường xuyên (Quiz)");

        System.out.println(">>> Khởi tạo dữ liệu thành công! Bạn có thể đăng nhập với các tài khoản admin/admin123, teacher/teacher123, student/student123");
    }

    private Role getOrCreateRole(String code, String name) {
        return roleRepository.findByCode(code).orElseGet(() -> {
            Role role = new Role();
            role.setCode(code);
            role.setName(name);
            role.setIsActive(true);
            role.setIsSystem(true);
            role.setCreatedAt(java.time.LocalDateTime.now());
            return roleRepository.save(role);
        });
    }

    private User createUserIfNotExist(String username, String password, String email, Role role) {
        return userRepository.findByUsernameAndIsActiveTrue(username).orElseGet(() -> {
            User user = new User();
            user.setUsername(username);
            user.setPassword(password); // Đơn giản: lưu text thuần
            user.setEmail(email);
            user.setIsActive(true);
            user.setCreatedAt(java.time.LocalDateTime.now());
            user.getRoles().add(role);
            return userRepository.save(user);
        });
    }

    private com.example.demo.gradeconfig.model.GradeCategory getOrCreateCategory(String code, String name) {
        return gradeCategoryRepository.findByCode(code).orElseGet(() -> {
            com.example.demo.gradeconfig.model.GradeCategory cat = new com.example.demo.gradeconfig.model.GradeCategory();
            cat.setCode(code);
            cat.setName(name);
            cat.setIsActive(true);
            return gradeCategoryRepository.save(cat);
        });
    }
}
