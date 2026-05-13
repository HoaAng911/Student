package com.example.demo.students.controller;

import com.example.demo.courses.model.entity.Registration;
import com.example.demo.courses.repository.RegistrationRepository;
import com.example.demo.studentgrades.model.entity.StudentGrade;
import com.example.demo.studentgrades.service.StudentGradeService;
import com.example.demo.students.model.entity.Student;
import com.example.demo.students.repository.StudentRepository;
import com.example.demo.users.model.entity.User;
import com.example.demo.gradecomponent.repository.GradeComponentRepository;
import com.example.demo.gradeconfig.model.GradeCategory;
import com.example.demo.gradeconfig.repository.GradeCategoryRepository;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Controller
@RequestMapping("/student")
public class StudentPortalController {

    private final StudentRepository studentRepository;
    private final RegistrationRepository registrationRepository;
    private final StudentGradeService gradeService;
    private final GradeCategoryRepository categoryRepository;
    private final GradeComponentRepository gradeComponentRepository;

    public StudentPortalController(StudentRepository studentRepository,
                                   RegistrationRepository registrationRepository,
                                   StudentGradeService gradeService,
                                   GradeCategoryRepository categoryRepository,
                                   GradeComponentRepository gradeComponentRepository) {
        this.studentRepository = studentRepository;
        this.registrationRepository = registrationRepository;
        this.gradeService = gradeService;
        this.categoryRepository = categoryRepository;
        this.gradeComponentRepository = gradeComponentRepository;
    }

    @GetMapping("/dashboard")
    public String dashboard(HttpSession session, Model model) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        Student student = studentRepository.findByUserId(currentUser.getId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin sinh viên!"));

        List<Registration> registrations = registrationRepository.findByStudentIdAndIsActiveTrue(student.getId());
        
        double totalGpa = 0;
        int gradedCount = 0;
        int totalCredits = 0;

        for (Registration reg : registrations) {
            List<StudentGrade> grades = gradeService.getGradesByRegistration(reg.getId());
            StudentGrade totalGrade = grades.stream()
                    .filter(g -> Boolean.TRUE.equals(g.getIsTotal()) && g.getGpaValue() != null)
                    .findFirst()
                    .orElse(null);
            
            if (totalGrade != null) {
                totalGpa += totalGrade.getGpaValue().doubleValue();
                gradedCount++;
                totalCredits += (reg.getCourseSection().getCourse().getCredits() != null ? reg.getCourseSection().getCourse().getCredits() : 3); 
            }
        }

        double averageGpa = (gradedCount > 0) ? totalGpa / gradedCount : 0.0;

        model.addAttribute("student", student);
        model.addAttribute("averageGpa", Math.round(averageGpa * 100.0) / 100.0);
        model.addAttribute("totalCredits", totalCredits);
        model.addAttribute("registrationCount", registrations.size());
        model.addAttribute("currentMenu", "home");
        
        return "student/dashboard";
    }

    @GetMapping("/my-grades")
    public String myGrades(HttpSession session, Model model, @RequestParam(required = false) UUID studentId) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        boolean isAdminOrTeacher = currentUser.getRoles().stream()
                .anyMatch(r -> r.getCode().equals("ADMIN") || r.getCode().equals("TEACHER"));

        Student student;
        if (studentId != null && isAdminOrTeacher) {
            student = studentRepository.findById(studentId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sinh viên!"));
        } else {
            student = studentRepository.findByUserId(currentUser.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin sinh viên!"));
        }

        List<Registration> registrations = registrationRepository.findByStudentIdAndIsActiveTrue(student.getId());
        List<GradeCategory> allCategories = categoryRepository.findAll();

        List<StudentCourseGradeDTO> courseGrades = registrations.stream().map(reg -> {
            List<StudentGrade> grades = gradeService.getGradesByRegistration(reg.getId());

            StudentGrade totalGrade = grades.stream()
                    .filter(g -> Boolean.TRUE.equals(g.getIsTotal()))
                    .findFirst()
                    .orElse(null);

            // 1. Xác định các Category từ cấu hình lớp học
            java.util.Set<String> activeCategoryIds = gradeComponentRepository.findByCourseSection_IdOrderByInputOrderAsc(reg.getCourseSection().getId()).stream()
                    .filter(c -> c.getGradeCategory() != null)
                    .map(c -> c.getGradeCategory().getId().toString())
                    .collect(java.util.stream.Collectors.toSet());

            // Gom điểm vào Map
            java.util.Map<String, String> categoryGrades = new java.util.HashMap<>();
            StringBuilder otherGrades = new StringBuilder();

            for (StudentGrade sg : grades) {
                if (Boolean.FALSE.equals(sg.getIsTotal()) && sg.getScore() != null && sg.getGradeComponent() != null) {
                    String compName = sg.getGradeComponent().getComponentName().toLowerCase().trim();
                    boolean matched = false;

                    // Nếu có Category, ưu tiên hiển thị ở cột chuẩn
                    if (sg.getGradeComponent().getGradeCategory() != null) {
                        String catId = sg.getGradeComponent().getGradeCategory().getId().toString();
                        categoryGrades.put(catId, sg.getScore().toString());
                        // Tự động kích hoạt cột này nếu sinh viên đã có điểm (phòng trường hợp cấu hình lớp bị mất)
                        activeCategoryIds.add(catId);
                        matched = true;
                    } 
                    
                    // Dự phòng: Khớp theo tên
                    if (!matched) {
                        for (GradeCategory cat : allCategories) {
                            String catName = cat.getName().toLowerCase().trim();
                            if (compName.contains(catName) || catName.contains(compName)) {
                                String catId = cat.getId().toString();
                                categoryGrades.put(catId, sg.getScore().toString());
                                activeCategoryIds.add(catId);
                                matched = true;
                                break;
                            }
                        }
                    }

                    if (!matched) {
                        if (otherGrades.length() > 0) otherGrades.append(", ");
                        otherGrades.append(sg.getGradeComponent().getComponentName()).append(": ").append(sg.getScore());
                    }
                }
            }
            
            double calcScore = 0;
            if (totalGrade != null && totalGrade.getScore() != null) {
                calcScore = totalGrade.getScore().doubleValue();
            } else {
                for (StudentGrade sg : grades) {
                    if (Boolean.FALSE.equals(sg.getIsTotal()) && sg.getScore() != null && sg.getGradeComponent() != null) {
                        calcScore += sg.getScore().doubleValue() * sg.getGradeComponent().getWeightPercentage().doubleValue() / 100.0;
                    }
                }
            }

            return new StudentCourseGradeDTO(reg, categoryGrades, activeCategoryIds, otherGrades.toString(), totalGrade, calcScore);
        }).collect(Collectors.toList());

        model.addAttribute("student", student);
        model.addAttribute("courseGrades", courseGrades);
        model.addAttribute("categories", allCategories);
        model.addAttribute("currentMenu", "myGrades");
        model.addAttribute("viewOnly", studentId != null);
        return "student/my-grades";
    }

    public static class StudentCourseGradeDTO {
        private Registration registration;
        private java.util.Map<String, String> categoryGrades;
        private java.util.Set<String> activeCategoryIds;
        private String otherGradesText;
        private StudentGrade totalGrade;
        private double calculatedScore;

        public StudentCourseGradeDTO(Registration registration, java.util.Map<String, String> categoryGrades, java.util.Set<String> activeCategoryIds, String otherGradesText, StudentGrade totalGrade, double calculatedScore) {
            this.registration = registration;
            this.categoryGrades = categoryGrades;
            this.activeCategoryIds = activeCategoryIds;
            this.otherGradesText = otherGradesText;
            this.totalGrade = totalGrade;
            this.calculatedScore = calculatedScore;
        }

        public Registration getRegistration() { return registration; }
        public java.util.Map<String, String> getCategoryGrades() { return categoryGrades; }
        public java.util.Set<String> getActiveCategoryIds() { return activeCategoryIds; }
        public String getOtherGradesText() { return otherGradesText; }
        public StudentGrade getTotalGrade() { return totalGrade; }
        public double getCalculatedScore() { return calculatedScore; }
    }
}
