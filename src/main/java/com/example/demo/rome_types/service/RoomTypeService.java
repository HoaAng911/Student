package com.example.demo.rome_types.service;

import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.example.demo.rome_types.dto.RoomTypeDto;
import com.example.demo.rome_types.dto.RoomTypeListDto;
import com.example.demo.rome_types.entity.RoomType;
import com.example.demo.rome_types.mapper.RoomTypeMapper;
import com.example.demo.rome_types.repository.RoomTypeRepository;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class RoomTypeService {

    private final RoomTypeRepository roomTypeRepository;
    private final RoomTypeMapper roomTypeMapper;

    public Page<RoomTypeListDto> search(String keyword, Pageable pageable) {
        if (keyword == null) keyword = "";
        Page<RoomType> page = roomTypeRepository.searchByKeyword(keyword.trim(), pageable);
        List<RoomTypeListDto> dtoList = page.getContent().stream()
                .map(roomTypeMapper::toListDto)
                .toList();
        return new PageImpl<>(dtoList, page.getPageable(), page.getTotalElements());
    }

    public Optional<RoomTypeDto> findByIdAsDto(UUID id) {
        return roomTypeRepository.findById(id).map(roomTypeMapper::toDto);
    }

    public Optional<RoomType> findByCode(String code) {
        return roomTypeRepository.findByRoomTypeCode(code);
    }

    @Transactional
    public RoomType save(RoomType roomType) {
        return roomTypeRepository.save(roomType);
    }

    @Transactional
    public void deleteById(UUID id) {
        roomTypeRepository.deleteById(id);
    }

    public List<RoomTypeListDto> findAllAsListDto() {
        return roomTypeRepository.findAll().stream()
                .map(roomTypeMapper::toListDto)
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
                    String desc = getCellString(row.getCell(2));
                    Integer maxCap = getCellInt(row.getCell(3));
                    if (code == null || code.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": Mã loại phòng không được trống");
                        continue;
                    }
                    if (name == null || name.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": Tên loại phòng không được trống");
                        continue;
                    }
                    if (findByCode(code.trim()).isPresent()) {
                        errors.add("Dòng " + (i + 1) + ": Mã loại phòng đã tồn tại: " + code);
                        continue;
                    }
                    save(RoomType.builder()
                            .roomTypeCode(code.trim())
                            .roomTypeName(name.trim())
                            .description(desc != null && !desc.isBlank() ? desc.trim() : null)
                            .maxCapacity(maxCap)
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
        List<RoomType> list = roomTypeRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Loại phòng học");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            String[] headers = {"Mã loại phòng", "Tên loại phòng", "Mô tả", "Sức chứa tối đa"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }
            int rowNum = 1;
            for (RoomType r : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getRoomTypeCode());
                row.createCell(1).setCellValue(r.getRoomTypeName());
                row.createCell(2).setCellValue(r.getDescription() != null ? r.getDescription() : "");
                row.createCell(3).setCellValue(r.getMaxCapacity() != null ? r.getMaxCapacity() : 0);
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

    private static Integer getCellInt(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> (int) cell.getNumericCellValue();
            case STRING -> {
                String s = cell.getStringCellValue();
                if (s == null || s.isBlank()) yield null;
                try {
                    yield Integer.parseInt(s.trim());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }
}
