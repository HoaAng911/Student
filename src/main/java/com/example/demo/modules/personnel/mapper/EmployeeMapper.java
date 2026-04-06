package com.example.demo.modules.personnel.mapper;

import com.example.demo.modules.personnel.dto.EmployeeDto;
import com.example.demo.modules.personnel.entity.Employee;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.factory.Mappers;
import org.mapstruct.Builder;

import java.util.List;

/**
 * Mapper để convert giữa Employee Entity và EmployeeDto sử dụng MapStruct.
 * `componentModel = "spring"` giúp Spring tự động tạo ra một Bean (như @Component) để bạn có thể @Autowired.
 */
@Mapper(componentModel = "spring", builder = @Builder(disableBuilder = true))
public interface EmployeeMapper {

    // Instance mặc định nếu bạn muốn gọi trực tiếp không qua Spring Bean (không bắt buộc nếu đã dùng @Autowired)
    EmployeeMapper INSTANCE = Mappers.getMapper(EmployeeMapper.class);

    /**
     * Convert từ Entity sang DTO
     * Ánh xạ các thuộc tính từ object lồng nhau (Department, Position) ra các trường trên DTO
     */
    @Mapping(source = "department.id", target = "departmentId")
    @Mapping(source = "department.name", target = "departmentName")
    @Mapping(source = "position.id", target = "positionId")
    @Mapping(source = "position.name", target = "positionName")
    EmployeeDto toDto(Employee employee);

    /**
     * Convert từ DTO về Entity
     * Từ ID gửi lên, MapStruct sẽ tự động tạo một object Department/Position chứa ID đó (dùng để lưu khóa ngoại).
     * Bỏ qua các trường Audit (BaseEntity) vì thường sẽ được xử lý tự động bởi JPA (@PrePersist, @PreUpdate).
     */
    @Mapping(source = "departmentId", target = "department.id")
    @Mapping(source = "positionId", target = "position.id")
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "createdBy", ignore = true)
    @Mapping(target = "updatedBy", ignore = true)
    @Mapping(target = "deletedAt", ignore = true)
    @Mapping(target = "deletedBy", ignore = true)
    @Mapping(target = "userId", ignore = true)
    // userId có thể được map riêng theo logic phân quyền
    Employee toEntity(EmployeeDto employeeDto);

    // Hỗ trợ map theo dạng List (tự động áp dụng toDto cho từng phần tử)
    List<EmployeeDto> toDtoList(List<Employee> employees);

    List<Employee> toEntityList(List<EmployeeDto> employeeDtos);
}