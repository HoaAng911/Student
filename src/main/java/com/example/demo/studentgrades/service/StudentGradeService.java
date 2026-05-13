package com.example.demo.studentgrades.service;

import com.example.demo.studentgrades.model.dto.ClassGradeReportDTO;
import com.example.demo.studentgrades.model.dto.GradeDetailDTO;
import com.example.demo.studentgrades.model.dto.StudentGradeRowDTO;
import com.example.demo.studentgrades.model.entity.StudentGrade;
import com.example.demo.studentgrades.repository.StudentGradeRepository;
import com.example.demo.gradecomponent.model.GradeComponent;
import com.example.demo.gradecomponent.repository.GradeComponentRepository;
import com.example.demo.students.model.entity.Student;
import com.example.demo.courses.model.entity.Registration;
import com.example.demo.courses.model.entity.CourseSection;
import com.example.demo.students.repository.StudentRepository;
import com.example.demo.courses.repository.CourseSectionRepository;
import com.example.demo.courses.repository.RegistrationRepository;
import com.example.demo.gradescale.service.GradeScaleService;
import com.example.demo.gradescale.model.GradeScale;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.*;
import java.util.stream.Collectors;
import java.util.Optional;
import java.util.UUID;

import jakarta.persistence.EntityNotFoundException;
import org.hibernate.ObjectNotFoundException;

/**
 * Service quản lý Điểm số của sinh viên.
 * Chịu trách nhiệm tính toán và lưu trữ điểm chi tiết.
 */
@Service
@RequiredArgsConstructor
public class StudentGradeService {

  private final StudentGradeRepository gradeRepository;
  private final GradeComponentRepository componentRepository;
  private final StudentRepository studentRepository;
  private final CourseSectionRepository courseSectionRepository;
  private final RegistrationRepository registrationRepository;
  private final GradeScaleService gradeScaleService;

  /** Lấy danh sách toàn bộ điểm (chỉ lấy các bản ghi đang hoạt động) */
  public List<StudentGrade> getAllGrades() {
    return gradeRepository.findActiveGrades();
  }

  public List<CourseSection> getAllCourseSections() {
    return courseSectionRepository.findByIsActiveTrue();
  }

  public List<CourseSection> getCourseSectionsByLecturer(UUID lecturerId) {
    return courseSectionRepository.findByLecturerIdAndIsActiveTrue(lecturerId);
  }

  /** Lấy thông tin điểm theo ID */
  public Optional<StudentGrade> getGradeById(UUID id) {
    return gradeRepository.findByIdAndIsActiveTrue(id);
  }

  /** Lấy bảng điểm của một lượt đăng ký học (sinh viên trong lớp học phần) */
  public List<StudentGrade> getGradesByRegistration(UUID registrationId) {
    return gradeRepository.findByRegistration_IdAndIsActiveTrue(registrationId);
  }

  /** Nhập điểm mới */
  @Transactional
  public StudentGrade createGrade(StudentGrade grade) {
    StudentGrade saved = gradeRepository.save(grade);
    if (saved.getRegistration() != null) {
      recalculateTotalGrade(saved.getRegistration().getId());
    }
    return saved;
  }

  /** Cập nhật điểm số hiện có */
  @Transactional
  public StudentGrade updateGrade(UUID id, StudentGrade details) {
    return gradeRepository.findById(id).map(grade -> {
      // KIỂM TRA KHÓA: Nếu điểm đã khóa thì không cho sửa
      if (Boolean.TRUE.equals(grade.getIsLocked())) {
        throw new RuntimeException("Điểm đã bị khóa, không thể chỉnh sửa!");
      }

      grade.setRegistration(details.getRegistration());
      grade.setGradeComponent(details.getGradeComponent());
      grade.setScore(details.getScore());

      grade.setIsTotal(details.getIsTotal());
      grade.setIsRetake(details.getIsRetake());
      grade.setIsLocked(details.getIsLocked());
      grade.setNote(details.getNote());
      grade.setLetterGrade(details.getLetterGrade());
      grade.setGpaValue(details.getGpaValue());
      grade.setResult(details.getResult());
      grade.setScaleId(details.getScaleId());
      grade.setIsFinalized(details.getIsFinalized());
      grade.setUpdatedBy(details.getUpdatedBy());
      grade.setIsActive(details.getIsActive());
      
      // Vết giảng viên chấm
      if (details.getScore() != null) {
        grade.setGradedBy(details.getGradedBy());
        grade.setGradedAt(java.time.LocalDateTime.now());
      }

      StudentGrade updated = gradeRepository.save(grade);
      
      // Nếu không phải là bản ghi Tổng kết, thì tính lại điểm tổng kết
      if (Boolean.FALSE.equals(updated.getIsTotal()) && updated.getRegistration() != null) {
        recalculateTotalGrade(updated.getRegistration().getId());
      }
      return updated;
    }).orElseThrow(() -> new RuntimeException("Không tìm thấy bản ghi điểm với ID: " + id));
  }

  /** Xóa vĩnh viễn (Physical Delete) */
  @Transactional
  public void deleteGrade(UUID id) {
    gradeRepository.deleteById(id);
  }

  /** Xóa logic (Soft Delete) bằng cách đánh dấu isActive = false */
  @Transactional
  public void softDeleteGrade(UUID id, UUID deletedBy) {
    gradeRepository.findById(id).ifPresent(grade -> {
      grade.setIsActive(false);
      grade.setDeletedBy(deletedBy);
      grade.setDeletedAt(java.time.LocalDateTime.now());
      gradeRepository.save(grade);
      
      if (grade.getRegistration() != null) {
        recalculateTotalGrade(grade.getRegistration().getId());
      }
    });
  }

  /**
   * Tính toán lại điểm tổng kết cho một sinh viên trong một lớp.
   * Dựa trên trọng số của các thành phần điểm và quy đổi sang thang điểm chữ.
   */
  @Transactional
  public void recalculateTotalGrade(UUID registrationId) {
    // 1. Lấy tất cả các điểm thành phần (không phải điểm tổng kết)
    List<StudentGrade> componentGrades = gradeRepository.findByRegistration_IdAndIsActiveTrue(registrationId)
        .stream()
        .filter(g -> Boolean.FALSE.equals(g.getIsTotal()))
        .collect(Collectors.toList());

    if (componentGrades.isEmpty()) return;

    // 2. Tính điểm trung bình có trọng số
    BigDecimal totalScore = BigDecimal.ZERO;
    for (StudentGrade sg : componentGrades) {
      if (sg.getScore() != null && sg.getGradeComponent() != null) {
        try {
          // Kiểm tra xem Component có thực sự tồn tại trong DB không (tránh lỗi Proxy mồ côi)
          BigDecimal weight = sg.getGradeComponent().getWeightPercentage();
          BigDecimal weightedScore = sg.getScore().multiply(weight).divide(new BigDecimal("100"), 2, RoundingMode.HALF_UP);
          totalScore = totalScore.add(weightedScore);
        } catch (jakarta.persistence.EntityNotFoundException | org.hibernate.ObjectNotFoundException e) {
          // Nếu Component đã bị xóa trước đó, bỏ qua bản ghi điểm này
          System.err.println("Cảnh báo: Phát hiện điểm mồ côi cho Registration " + registrationId + ". Đang bỏ qua...");
          continue;
        }
      }
    }

    // 3. Tìm hoặc tạo bản ghi Tổng kết (isTotal = true)
    StudentGrade totalGradeRecord = gradeRepository.findByRegistration_IdAndIsActiveTrue(registrationId)
        .stream()
        .filter(g -> Boolean.TRUE.equals(g.getIsTotal()))
        .findFirst()
        .orElse(new StudentGrade());

    totalGradeRecord.setRegistration(componentGrades.get(0).getRegistration());
    totalGradeRecord.setIsTotal(true);
    totalGradeRecord.setScore(totalScore);
    totalGradeRecord.setIsActive(true);

    // 4. Quy đổi sang thang điểm (A, B, C... và GPA 4.0)
    GradeScale scale = gradeScaleService.getScaleForScore(totalScore);
    if (scale != null) {
      totalGradeRecord.setLetterGrade(scale.getLetterGrade());
      totalGradeRecord.setGpaValue(scale.getGpaValue());
      totalGradeRecord.setResult(scale.getIsPass() ? "PASSED" : "FAILED");
      totalGradeRecord.setScaleId(scale.getId());
    }

    gradeRepository.save(totalGradeRecord);
  }

  /**
   * Chấm điểm hàng loạt cho một thành phần điểm trong lớp.
   * @param componentId ID của thành phần điểm (Chuyên cần, Giữa kỳ...)
   * @param scores Map [RegistrationID -> Score]
   * @param lecturerId ID giảng viên chấm
   */
  @Transactional
  public void bulkUpdateComponentGrades(UUID componentId, Map<UUID, BigDecimal> scores, UUID lecturerId) {
    GradeComponent component = componentRepository.findById(componentId)
        .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phần điểm"));

    for (Map.Entry<UUID, BigDecimal> entry : scores.entrySet()) {
      UUID regId = entry.getKey();
      BigDecimal scoreValue = entry.getValue();

      Registration registration = registrationRepository.findById(regId)
          .orElseThrow(() -> new RuntimeException("Không tìm thấy thông tin đăng ký: " + regId));

      // 1. Tìm bản ghi điểm đã có hoặc tạo mới
      StudentGrade grade = gradeRepository.findByRegistrationIdAndGradeComponentId(regId, componentId)
          .orElseGet(() -> {
            StudentGrade newGrade = new StudentGrade();
            newGrade.setRegistration(registration);
            newGrade.setGradeComponent(component);
            newGrade.setIsTotal(false);
            return newGrade;
          });

      // 2. Kiểm tra khóa
      if (Boolean.TRUE.equals(grade.getIsLocked())) continue;

      // 3. Cập nhật thông tin & Audit
      grade.setScore(scoreValue);
      grade.setGradedBy(lecturerId);
      grade.setGradedAt(java.time.LocalDateTime.now());
      grade.setIsActive(true);
      grade.setStatus("SUBMITTED");

      // 4. Logic isRetake: Nếu loại đăng ký là Học lại (2) hoặc Cải thiện (3)
      if (registration.getRegistrationType() != null && registration.getRegistrationType() > 1) {
        grade.setIsRetake(true);
      }

      gradeRepository.save(grade);

      // 5. Tính lại điểm tổng kết cho sinh viên này
      recalculateTotalGrade(regId);
    }
  }

  /**
   * Khóa hoặc Mở khóa điểm cho toàn bộ sinh viên của một thành phần điểm.
   */
  @Transactional
  public void lockComponentGrades(UUID componentId, UUID lecturerId, boolean lock) {
    List<StudentGrade> grades = gradeRepository.findByGradeComponent_IdAndIsActiveTrue(componentId);
    for (StudentGrade grade : grades) {
      grade.setIsLocked(lock);
      if (lock) {
        grade.setLockedBy(lecturerId);
        grade.setLockedAt(java.time.LocalDateTime.now());
      }
      gradeRepository.save(grade);
    }
  }

  /** Tìm điểm theo sinh viên và thành phần điểm cụ thể */
  public Optional<StudentGrade> getByRegistrationAndComponent(UUID registrationId, UUID gradeComponentId) {
    return gradeRepository.findByRegistrationIdAndGradeComponentId(registrationId, gradeComponentId);
  }
  /** Lưu điểm hàng loạt từ giao diện Ma trận */
  @Transactional
  public void saveMatrixGrades(List<com.example.demo.studentgrades.model.dto.GradeUpdateDTO> updates, UUID lecturerId) {
    if (updates == null || updates.isEmpty()) return;

    // Sử dụng Set để lưu các Registration ID cần tính lại điểm (tránh tính lại nhiều lần cho cùng 1 SV)
    Set<UUID> registrationsToRecalculate = new HashSet<>();

    for (var update : updates) {
      if (update.getRegistrationId() == null || update.getGradeComponentId() == null) continue;

      Registration registration = registrationRepository.findById(update.getRegistrationId())
          .orElseThrow(() -> new RuntimeException("Không tìm thấy đăng ký sinh viên: " + update.getRegistrationId()));

      Optional<StudentGrade> gradeOpt = gradeRepository.findByRegistrationIdAndGradeComponentId(
          update.getRegistrationId(), update.getGradeComponentId());

      StudentGrade grade;
      if (gradeOpt.isPresent()) {
        grade = gradeOpt.get();
        if (Boolean.TRUE.equals(grade.getIsLocked())) continue;
      } else {
        grade = StudentGrade.builder()
            .registration(registration)
            .gradeComponent(componentRepository.findById(update.getGradeComponentId())
                .orElseThrow(() -> new RuntimeException("Không tìm thấy thành phần điểm: " + update.getGradeComponentId())))
            .isTotal(false)
            .isLocked(false)
            .isActive(true)
            .build();
      }

      grade.setScore(update.getScore());
      grade.setStatus("SUBMITTED");
      grade.setGradedBy(lecturerId);
      grade.setGradedAt(java.time.LocalDateTime.now());

      // Logic isRetake
      if (registration.getRegistrationType() != null && registration.getRegistrationType() > 1) {
        grade.setIsRetake(true);
      }

      gradeRepository.save(grade);
      
      // Thêm vào danh sách chờ tính lại điểm tổng kết
      registrationsToRecalculate.add(update.getRegistrationId());
    }

    // Tính lại điểm tổng kết cho mỗi sinh viên bị ảnh hưởng (chỉ gọi 1 lần duy nhất)
    for (UUID regId : registrationsToRecalculate) {
      recalculateTotalGrade(regId);
    }
  }

  /**
   * Chốt điểm tổng kết học phần (Finalize)
   * Tính toán lần cuối và đánh dấu isFinalized = true cho điểm tổng kết.
   */
  @Transactional
  public void finalizeClassGrades(UUID courseSectionId, UUID adminId) {
    List<Registration> registrations = registrationRepository.findByCourseSectionIdAndIsActiveTrue(courseSectionId);
    for (Registration reg : registrations) {
      // Đảm bảo điểm tổng kết đã được tính
      recalculateTotalGrade(reg.getId());

      // Tìm bản ghi tổng kết và khóa lại
      gradeRepository.findByRegistration_IdAndIsActiveTrue(reg.getId())
          .stream()
          .filter(g -> Boolean.TRUE.equals(g.getIsTotal()))
          .findFirst()
          .ifPresent(totalGrade -> {
            totalGrade.setIsFinalized(true);
            totalGrade.setIsLocked(true);
            totalGrade.setLockedBy(adminId);
            totalGrade.setLockedAt(java.time.LocalDateTime.now());
            gradeRepository.save(totalGrade);
          });
          
      // Đồng thời khóa luôn các điểm thành phần
      List<StudentGrade> components = gradeRepository.findByRegistration_IdAndIsActiveTrue(reg.getId())
          .stream()
          .filter(g -> Boolean.FALSE.equals(g.getIsTotal()))
          .collect(Collectors.toList());
      
      for (StudentGrade comp : components) {
        comp.setIsLocked(true);
        comp.setLockedBy(adminId);
        comp.setLockedAt(java.time.LocalDateTime.now());
        gradeRepository.save(comp);
      }
    }
  }

  /** Tìm kiếm điểm theo ghi chú */
  public List<StudentGrade> searchGrades(String keyword) {
    if (keyword == null || keyword.trim().isEmpty()) {
      return getAllGrades();
    }
    return gradeRepository.findByNoteContainingIgnoreCaseAndIsActiveTrue(keyword);
  }

  /**
   * Tổng hợp bảng điểm của một lớp học phần (Matrix View)
   */
  public ClassGradeReportDTO getClassGradeReport(UUID courseSectionId) {
    // 1. Lấy danh sách thành phần điểm (Headers)
    List<GradeComponent> components = componentRepository.findByCourseSection_IdOrderByInputOrderAsc(courseSectionId);
    if (components.isEmpty()) {
      return ClassGradeReportDTO.builder()
          .courseSectionId(courseSectionId)
          .headers(Collections.emptyList())
          .rows(Collections.emptyList())
          .build();
    }

    // 2. Lấy danh sách tất cả sinh viên đăng ký lớp này (GỐC)
    // LỌC THÔNG MINH: Chỉ lấy những sinh viên còn tồn tại và không bị xóa
    List<Registration> registrations = registrationRepository.findByCourseSectionIdAndIsActiveTrue(courseSectionId)
        .stream()
        .filter(reg -> {
            try {
                Student s = reg.getStudent();
                // Kiểm tra sinh viên có tồn tại và không bị xóa (soft-delete)
                return s != null && s.getDeletedAt() == null;
            } catch (Exception e) {
                // Nếu bị lỗi Proxy (EntityNotFound) nghĩa là sinh viên đã bị xóa vật lý
                return false;
            }
        })
        .collect(Collectors.toList());

    if (registrations.isEmpty()) {
        return ClassGradeReportDTO.builder()
            .courseSectionId(courseSectionId)
            .headers(components)
            .rows(Collections.emptyList())
            .build();
    }

    // 3. Lấy tất cả điểm hiện có của lớp này để map vào
    List<UUID> componentIds = components.stream().map(GradeComponent::getId).collect(Collectors.toList());
    List<StudentGrade> allGrades = gradeRepository.findByGradeComponent_IdInAndIsActiveTrue(componentIds);
    
    // Nhóm điểm theo registrationId để truy xuất nhanh
    Map<UUID, List<StudentGrade>> gradesByRegMap = allGrades.stream()
        .filter(g -> g.getRegistration() != null)
        .collect(Collectors.groupingBy(g -> g.getRegistration().getId()));

    // 4. Xây dựng dòng dữ liệu cho TẤT CẢ sinh viên đã đăng ký
    List<StudentGradeRowDTO> rows = registrations.stream().map(reg -> {
      UUID registrationId = reg.getId();
      List<StudentGrade> studentGrades = gradesByRegMap.getOrDefault(registrationId, Collections.emptyList());

      List<GradeDetailDTO> gradeDetails = components.stream().map(comp -> {
        StudentGrade sg = studentGrades.stream()
            .filter(g -> g.getGradeComponent() != null && g.getGradeComponent().getId().equals(comp.getId()))
            .findFirst()
            .orElse(null);

        return GradeDetailDTO.builder()
            .gradeComponentId(comp.getId())
            .componentCode(comp.getComponentCode())
            .componentName(comp.getComponentName())
            .score(sg != null ? sg.getScore() : null)
            .isLocked(sg != null && sg.getIsLocked())
            .status(sg != null ? sg.getStatus() : "DRAFT")
            .build();
      }).collect(Collectors.toList());

      // Tính điểm tổng kết tạm tính (Xử lý an toàn hơn)
      double finalScore = gradeDetails.stream()
          .filter(d -> d.getScore() != null)
          .mapToDouble(d -> {
            return components.stream()
                .filter(c -> c.getId().equals(d.getGradeComponentId()))
                .findFirst()
                .map(comp -> d.getScore().doubleValue() * comp.getWeightPercentage().doubleValue() / 100.0)
                .orElse(0.0);
          }).sum();

      // Xử lý thông tin sinh viên an toàn (Tránh lỗi Proxy khi sinh viên bị xóa vật lý)
      Student student = null;
      String firstName = "";
      String lastName = "N/A (Đã xóa)";
      String studentCode = "N/A";
      String dob = "";

      try {
          student = reg.getStudent();
          if (student != null) {
              // Truy cập một thuộc tính để ép Hibernate load dữ liệu
              studentCode = (student.getCode() != null) ? student.getCode() : "N/A";
              dob = (student.getDate_of_birth() != null) ? student.getDate_of_birth().toString() : "";
              
              if (student.getFullname() != null) {
                  String fullName = student.getFullname().trim();
                  int lastSpaceIndex = fullName.lastIndexOf(' ');
                  if (lastSpaceIndex != -1) {
                      firstName = fullName.substring(0, lastSpaceIndex);
                      lastName = fullName.substring(lastSpaceIndex + 1);
                  } else {
                      firstName = "";
                      lastName = fullName;
                  }
              }
          }
      } catch (EntityNotFoundException | ObjectNotFoundException e) {
          student = null;
          studentCode = "N/A";
          lastName = "N/A (Đã xóa)";
      }

      // Quy đổi sang thang điểm từ Database (Dynamic)
      BigDecimal finalScoreBD = BigDecimal.valueOf(Math.round(finalScore * 10.0) / 10.0);
      GradeScale scale = gradeScaleService.getScaleForScore(finalScoreBD);

      return StudentGradeRowDTO.builder()
          .registrationId(registrationId)
          .studentCode(studentCode)
          .firstName(firstName)
          .lastName(lastName)
          .dob(dob)
          .className("N/A") 
          .grades(gradeDetails)
          .finalScore(finalScoreBD.doubleValue())
          .t4Score(scale != null ? scale.getGpaValue().doubleValue() : 0.0)
          .gradeLetter(scale != null && gradeDetails.stream().anyMatch(d -> d.getScore() != null) ? scale.getLetterGrade() : "-") 
          .build();
    }).collect(Collectors.toList());

    // 5. Lấy thông tin Lớp học phần để đặt tên tiêu đề
    CourseSection section = courseSectionRepository.findById(courseSectionId).orElse(null);
    String sectionName = (section != null) ? section.getName() : "Lớp học phần " + courseSectionId.toString().substring(0, 8);

    // 6. Tính toán thống kê - Chỉ tính những SV đã có ít nhất một đầu điểm
    List<StudentGradeRowDTO> gradedRows = rows.stream()
            .filter(r -> r.getGrades().stream().anyMatch(g -> g.getScore() != null))
            .collect(Collectors.toList());

    long passCount = gradedRows.stream().filter(r -> r.getFinalScore() >= 4.0).count();
    long failCount = gradedRows.size() - passCount;
    
    Map<String, Long> letterDistribution = gradedRows.stream()
            .collect(Collectors.groupingBy(StudentGradeRowDTO::getGradeLetter, Collectors.counting()));

    return ClassGradeReportDTO.builder()
        .courseSectionId(courseSectionId)
        .courseName(sectionName)
        .headers(components)
        .rows(rows)
        .totalStudents(rows.size())
        .passCount(passCount)
        .failCount(failCount)
        .letterDistribution(letterDistribution)
        .build();
  }

  /** Xuất báo cáo điểm ra luồng dữ liệu Excel */
  public ByteArrayInputStream exportClassGradeReportToExcel(UUID courseSectionId) throws IOException {
    ClassGradeReportDTO report = getClassGradeReport(courseSectionId);

    try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
      Sheet sheet = workbook.createSheet("Bảng điểm");

      // Header Style
      CellStyle headerStyle = workbook.createCellStyle();
      headerStyle.setBorderBottom(BorderStyle.THIN);
      headerStyle.setBorderTop(BorderStyle.THIN);
      headerStyle.setBorderLeft(BorderStyle.THIN);
      headerStyle.setBorderRight(BorderStyle.THIN);
      headerStyle.setAlignment(HorizontalAlignment.CENTER);
      headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
      headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
      Font headerFont = workbook.createFont();
      headerFont.setBold(true);
      headerStyle.setFont(headerFont);

      // Row 0: Headers
      Row headerRow = sheet.createRow(0);
      String[] fixedHeaders = {"STT", "Mã SV", "Họ và Tên lót", "Tên", "Ngày sinh", "Lớp"};
      for (int i = 0; i < fixedHeaders.length; i++) {
        Cell cell = headerRow.createCell(i);
        cell.setCellValue(fixedHeaders[i]);
        cell.setCellStyle(headerStyle);
      }

      // Dynamic Headers (Components)
      int colIndex = fixedHeaders.length;
      for (GradeComponent comp : report.getHeaders()) {
        Cell cell = headerRow.createCell(colIndex++);
        cell.setCellValue(comp.getComponentCode());
        cell.setCellStyle(headerStyle);
      }

      Cell finalCell = headerRow.createCell(colIndex);
      finalCell.setCellValue("Tổng kết");
      finalCell.setCellStyle(headerStyle);

      // Data Rows
      int rowIndex = 1;
      for (int i = 0; i < report.getRows().size(); i++) {
        StudentGradeRowDTO reportRow = report.getRows().get(i);
        Row row = sheet.createRow(rowIndex++);

        row.createCell(0).setCellValue(103 + i);
        row.createCell(1).setCellValue(reportRow.getStudentCode());
        row.createCell(2).setCellValue(reportRow.getFirstName());
        row.createCell(3).setCellValue(reportRow.getLastName());
        row.createCell(4).setCellValue(reportRow.getDob());
        row.createCell(5).setCellValue(reportRow.getClassName());

        int gCol = fixedHeaders.length;
        for (GradeDetailDTO grade : reportRow.getGrades()) {
          if (grade.getScore() != null) {
            row.createCell(gCol).setCellValue(grade.getScore().doubleValue());
          }
          gCol++;
        }

        if (reportRow.getFinalScore() != null) {
          row.createCell(gCol).setCellValue(reportRow.getFinalScore());
        }
      }

      // Auto-size columns
      for (int i = 0; i <= colIndex; i++) {
        sheet.autoSizeColumn(i);
      }

      workbook.write(out);
      return new ByteArrayInputStream(out.toByteArray());
    }
  }
}
