package com.example.demo.buildings.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.buildings.dto.BuildingDto;
import com.example.demo.buildings.dto.BuildingListDto;
import com.example.demo.buildings.entity.Building;
import com.example.demo.buildings.mapper.BuildingMapper;
import com.example.demo.buildings.repository.BuildingRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class BuildingService {

    private final BuildingRepository buildingRepository;
    private final BuildingMapper buildingMapper;

    public Page<BuildingListDto> search(String keyword, Pageable pageable) {
        if (keyword == null) keyword = "";
        Page<Building> page = buildingRepository.searchByKeyword(keyword.trim(), pageable);
        List<BuildingListDto> dtoList = page.getContent().stream()
                .map(buildingMapper::toListDto)
                .toList();
        return new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
    }

    public Optional<BuildingDto> findByIdAsDto(UUID id) {
        return buildingRepository.findById(id).map(buildingMapper::toDto);
    }

    public Optional<Building> findByCode(String code) {
        return buildingRepository.findByBuildingCode(code);
    }

    @Transactional
    public Building save(Building building) {
        return buildingRepository.save(building);
    }

    @Transactional
    public void deleteById(UUID id) {
        buildingRepository.deleteById(id);
    }

    public List<BuildingListDto> findAllAsListDto() {
        return buildingRepository.findAll().stream()
                .map(buildingMapper::toListDto)
                .toList();
    }

    @Transactional
    public List<String> importFromExcel(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    String code = getCellString(row.getCell(0));
                    String name = getCellString(row.getCell(1));
                    String address = getCellString(row.getCell(2));
                    String desc = getCellString(row.getCell(3));
                    if (code == null || code.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": Mã toà nhà không được trống");
                        continue;
                    }
                    if (name == null || name.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": Tên toà nhà không được trống");
                        continue;
                    }
                    if (findByCode(code.trim()).isPresent()) {
                        errors.add("Dòng " + (i + 1) + ": Mã toà nhà đã tồn tại: " + code);
                        continue;
                    }
                    save(Building.builder()
                            .buildingCode(code.trim())
                            .buildingName(name.trim())
                            .address(address != null && !address.isBlank() ? address.trim() : null)
                            .description(desc != null && !desc.isBlank() ? desc.trim() : null)
                            .isActive(true)
                            .build());
                } catch (Exception e) {
                    errors.add("Dòng " + (i + 1) + ": " + e.getMessage());
                }
            }
        } catch (Exception e) {
            errors.add("Lỗi đọc file: " + e.getMessage());
        }
        return errors;
    }

    public byte[] exportToExcel() throws Exception {
        List<Building> list = buildingRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Toà nhà");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            String[] headers = {"Mã toà nhà", "Tên toà nhà", "Địa chỉ", "Mô tả", "Trạng thái"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }
            int rowNum = 1;
            for (Building b : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(b.getBuildingCode());
                row.createCell(1).setCellValue(b.getBuildingName());
                row.createCell(2).setCellValue(b.getAddress() != null ? b.getAddress() : "");
                row.createCell(3).setCellValue(b.getDescription() != null ? b.getDescription() : "");
                row.createCell(4).setCellValue(Boolean.TRUE.equals(b.getIsActive()) ? "Hoạt động" : "Tắt");
            }
            for (int i = 0; i < headers.length; i++) {
                sheet.autoSizeColumn(i);
            }
            workbook.write(out);
            return out.toByteArray();
        }
    }

    private static String getCellString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> String.valueOf((long) cell.getNumericCellValue());
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}
