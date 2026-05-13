package com.example.demo.gradecomponent.service;

import com.example.demo.gradecomponent.model.GradeComponent;
import com.example.demo.gradecomponent.repository.GradeComponentRepository;
import com.example.demo.gradeconfig.model.CourseGradeConfig;
import com.example.demo.gradeconfig.repository.CourseGradeConfigRepository;
import com.example.demo.courses.model.entity.CourseSection;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

/**
 * Service quản lý các Thành phần điểm (Vd: Chuyên cần 10%, Giữa kỳ 20%...).
 * Định nghĩa cấu trúc điểm cho từng lớp học phần.
 */
@Service
public class GradeComponentService {

    @Autowired
    private GradeComponentRepository repository;

    @Autowired
    private CourseGradeConfigRepository configRepository;

    /** Tự động tạo các thành phần điểm cho lớp học phần dựa trên cấu hình của môn học */
    @Transactional
    public void initializeComponentsFromTemplate(CourseSection section) {
        if (section == null || section.getCourse() == null) return;

        // Kiểm tra xem lớp này đã có thành phần điểm chưa
        List<GradeComponent> existing = repository.findByCourseSection_IdOrderByInputOrderAsc(section.getId());
        if (!existing.isEmpty()) return;

        List<CourseGradeConfig> configs = configRepository.findByCourseId(section.getCourse().getId());
        
        for (CourseGradeConfig config : configs) {
            GradeComponent component = new GradeComponent();
            component.setCourseSection(section);
            component.setCourse(section.getCourse());
            component.setGradeCategory(config.getGradeCategory());
            component.setComponentCode(config.getGradeCategory().getCode());
            component.setComponentName(config.getGradeCategory().getName());
            component.setWeightPercentage(config.getDefaultWeightPercentage());
            component.setInputOrder(config.getInputOrder());
            component.setIsRequired(config.getIsRequired());
            component.setIsActive(true);
            
            if ("CK".equalsIgnoreCase(config.getGradeCategory().getCode())) {
                component.setIsFinal(true);
            }
            
            repository.save(component);
        }
    }

    /** Lấy danh sách toàn bộ thành phần điểm */
    @Transactional(readOnly = true)
    public List<GradeComponent> getAll() {
        return repository.findAll();
    }

    /** Lấy các thành phần điểm của một lớp học phần cụ thể */
    @Transactional(readOnly = true)
    public List<GradeComponent> getBySection(UUID sectionId) {
        return repository.findByCourseSection_IdOrderByInputOrderAsc(sectionId);
    }


    @Transactional(readOnly = true)
    public boolean validateTotalWeight(UUID sectionId) {
        List<GradeComponent> components = getBySection(sectionId);
        double total = components.stream()
                .mapToDouble(c -> c.getWeightPercentage().doubleValue())
                .sum();
        return Math.abs(total - 100.0) < 0.01; // Cho phép sai số nhỏ do kiểu double
    }

    /** Lấy thông tin chi tiết thành phần điểm theo ID */
    public GradeComponent getById(UUID id) {
        return repository.findById(id).orElse(null);
    }

    /** Lưu mới hoặc cập nhật thành phần điểm */
    @Transactional
    public GradeComponent save(GradeComponent data) {
        return repository.save(data);
    }

    /** Xóa thành phần điểm */
    @Transactional
    public void delete(UUID id) {
        repository.deleteById(id);
    }

    /** Cập nhật hàng loạt thành phần điểm cho một lớp học phần (Logic Upsert để bảo toàn ID) */
    @Transactional
    public void saveAllForSection(UUID sectionId, List<GradeComponent> components) {
        // 1. Validate tổng trọng số
        double totalWeight = components.stream()
                .filter(c -> c.getIsActive() != null && c.getIsActive())
                .mapToDouble(c -> c.getWeightPercentage() != null ? c.getWeightPercentage().doubleValue() : 0.0)
                .sum();
        
        if (Math.abs(totalWeight - 100.0) > 0.01) {
            throw new RuntimeException("Tổng trọng số phải bằng 100% (Hiện tại: " + totalWeight + "%)");
        }

        // 2. Lấy danh sách hiện tại từ DB
        List<GradeComponent> existing = repository.findByCourseSection_IdOrderByInputOrderAsc(sectionId);
        
        // 3. Xử lý đồng bộ
        // Những cái nào không có trong danh sách mới gửi lên -> Xóa
        for (GradeComponent old : existing) {
            boolean stillExists = components.stream().anyMatch(c -> c.getId() != null && c.getId().equals(old.getId()));
            if (!stillExists) {
                repository.delete(old);
            }
        }

        // 4. Lưu hoặc Cập nhật
        CourseSection section = new CourseSection();
        section.setId(sectionId);

        for (int i = 0; i < components.size(); i++) {
            GradeComponent incoming = components.get(i);
            GradeComponent toSave;

            if (incoming.getId() != null) {
                // Cập nhật cái cũ
                toSave = repository.findById(incoming.getId()).orElse(new GradeComponent());
            } else {
                // Tạo mới hoàn toàn
                toSave = new GradeComponent();
            }

            toSave.setCourseSection(section);
            toSave.setComponentName(incoming.getComponentName());
            toSave.setComponentCode(incoming.getComponentCode());
            toSave.setWeightPercentage(incoming.getWeightPercentage());
            toSave.setIsRequired(incoming.getIsRequired());
            toSave.setIsFinal(incoming.getIsFinal());
            toSave.setGradeCategory(incoming.getGradeCategory()); // Có thể null nếu chọn "Khác"
            toSave.setInputOrder(i);
            toSave.setIsActive(true);

            repository.save(toSave);
        }
    }

    /** Tìm kiếm thành phần điểm theo keyword */
    @Transactional(readOnly = true)
    public List<GradeComponent> search(String keyword) {
        if (keyword == null || keyword.trim().isEmpty()) {
            return getAll();
        }
        return repository.findByComponentNameContainingIgnoreCaseOrComponentCodeContainingIgnoreCase(keyword, keyword);
    }
}