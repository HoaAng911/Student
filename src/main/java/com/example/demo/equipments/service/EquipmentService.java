package com.example.demo.equipments.service;

import com.example.demo.equipments.dto.EquipmentDto;
import com.example.demo.equipments.dto.EquipmentListDto;
import com.example.demo.equipments.entity.Equipment;
import com.example.demo.equipments.mapper.EquipmentMapper;
import com.example.demo.equipments.repository.EquipmentRepository;
import com.example.demo.rooms.entity.Room;
import com.example.demo.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class EquipmentService {

    private final EquipmentRepository equipmentRepository;
    private final EquipmentMapper equipmentMapper;
    private final RoomRepository roomRepository;

    public Page<EquipmentListDto> search(String keyword, String status, UUID roomId, Pageable pageable) {
        Page<Equipment> page = equipmentRepository.search(
                keyword != null ? keyword.trim() : "",
                (status != null && !status.isBlank()) ? status.trim() : null,
                roomId,
                pageable
        );
        List<EquipmentListDto> dtos = page.getContent().stream()
                .map(equipmentMapper::toListDto)
                .peek(this::fillRoomName)
                .toList();
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    public Optional<EquipmentDto> findByIdAsDto(UUID id) {
        return equipmentRepository.findById(id).map(equipmentMapper::toDto);
    }

    public Optional<Equipment> findByCode(String code) {
        return equipmentRepository.findByEquipmentCode(code);
    }

    @Transactional
    public Equipment save(Equipment equipment) {
        return equipmentRepository.save(equipment);
    }

    @Transactional
    public void deleteById(UUID id) {
        equipmentRepository.deleteById(id);
    }

    public List<EquipmentListDto> findAllAsListDto() {
        return equipmentRepository.findAll().stream()
                .map(equipmentMapper::toListDto)
                .peek(this::fillRoomName)
                .collect(Collectors.toList());
    }

    private void fillRoomName(EquipmentListDto dto) {
        if (dto.getRoomId() != null) {
            roomRepository.findById(dto.getRoomId())
                    .map(Room::getRoomName)
                    .ifPresent(dto::setRoomName);
        }
    }

    public byte[] exportToExcel() throws Exception {
        List<Equipment> list = equipmentRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Thiết bị");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);
            String[] headers = {"Mã thiết bị", "Tên thiết bị", "Số serial", "Ngày mua", "Trạng thái", "RoomId"};
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }
            int rowNum = 1;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
            for (Equipment e : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(e.getEquipmentCode());
                row.createCell(1).setCellValue(e.getEquipmentName());
                row.createCell(2).setCellValue(e.getSerialNumber() != null ? e.getSerialNumber() : "");
                row.createCell(3).setCellValue(e.getPurchaseDate() != null ? e.getPurchaseDate().format(fmt) : "");
                row.createCell(4).setCellValue(e.getStatus() != null ? e.getStatus() : "");
                row.createCell(5).setCellValue(e.getRoomId() != null ? e.getRoomId().toString() : "");
            }
            for (int i = 0; i < headers.length; i++) sheet.autoSizeColumn(i);
            workbook.write(out);
            return out.toByteArray();
        }
    }

    @Transactional
    public List<String> importFromExcel(MultipartFile file) {
        List<String> errors = new ArrayList<>();
        DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try (InputStream is = file.getInputStream(); Workbook workbook = new XSSFWorkbook(is)) {
            Sheet sheet = workbook.getSheetAt(0);
            for (int i = 1; i <= sheet.getLastRowNum(); i++) {
                Row row = sheet.getRow(i);
                if (row == null) continue;
                try {
                    String code = getCellString(row.getCell(0));
                    String name = getCellString(row.getCell(1));
                    String serial = getCellString(row.getCell(2));
                    String dateStr = getCellString(row.getCell(3));
                    String status = getCellString(row.getCell(4));
                    String roomIdStr = getCellString(row.getCell(5));

                    if (code == null || code.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": Mã thiết bị không được trống");
                        continue;
                    }
                    if (name == null || name.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": Tên thiết bị không được trống");
                        continue;
                    }
                    if (findByCode(code.trim()).isPresent()) {
                        errors.add("Dòng " + (i + 1) + ": Mã thiết bị đã tồn tại: " + code);
                        continue;
                    }
                    LocalDate purchaseDate = null;
                    if (dateStr != null && !dateStr.isBlank()) {
                        try {
                            purchaseDate = LocalDate.parse(dateStr.trim(), fmt);
                        } catch (Exception ex) {
                            errors.add("Dòng " + (i + 1) + ": Ngày mua không đúng định dạng yyyy-MM-dd");
                            continue;
                        }
                    }
                    UUID roomId = null;
                    if (roomIdStr != null && !roomIdStr.isBlank()) {
                        try {
                            roomId = UUID.fromString(roomIdStr.trim());
                        } catch (Exception ex) {
                            errors.add("Dòng " + (i + 1) + ": RoomId không hợp lệ");
                            continue;
                        }
                    }
                    save(Equipment.builder()
                            .equipmentCode(code.trim())
                            .equipmentName(name.trim())
                            .serialNumber(serial != null && !serial.isBlank() ? serial.trim() : null)
                            .purchaseDate(purchaseDate)
                            .status(status != null && !status.isBlank() ? status.trim() : null)
                            .roomId(roomId)
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

    private static String getCellString(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case STRING -> cell.getStringCellValue();
            case NUMERIC -> {
                if (DateUtil.isCellDateFormatted(cell)) {
                    yield cell.getLocalDateTimeCellValue().toLocalDate().toString();
                }
                yield String.valueOf((long) cell.getNumericCellValue());
            }
            case BOOLEAN -> String.valueOf(cell.getBooleanCellValue());
            default -> null;
        };
    }
}
