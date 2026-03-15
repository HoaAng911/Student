package com.example.demo.rooms.service;

import com.example.demo.rooms.dto.RoomDto;
import com.example.demo.rooms.dto.RoomListDto;
import com.example.demo.rooms.entity.Room;
import com.example.demo.rooms.mapper.RoomMapper;
import com.example.demo.rooms.repository.RoomRepository;
import lombok.RequiredArgsConstructor;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class RoomService {

    private final RoomRepository roomRepository;
    private final RoomMapper roomMapper;

    public Page<RoomListDto> search(String keyword,
                                    UUID buildingId,
                                    UUID roomTypeId,
                                    String status,
                                    Boolean isActive,
                                    Pageable pageable) {
        Page<Room> page = roomRepository.search(
                keyword != null ? keyword.trim() : "",
                buildingId,
                roomTypeId,
                (status != null && !status.isBlank()) ? status.trim() : null,
                isActive,
                pageable
        );
        List<RoomListDto> dtos = page.getContent().stream()
                .map(roomMapper::toListDto)
                .toList();
        return new PageImpl<>(dtos, page.getPageable(), page.getTotalElements());
    }

    public Optional<RoomDto> findByIdAsDto(UUID id) {
        return roomRepository.findById(id).map(roomMapper::toDto);
    }

    public Optional<Room> findByCode(String code) {
        return roomRepository.findByRoomCode(code);
    }

    @Transactional
    public Room save(Room room) {
        return roomRepository.save(room);
    }

    @Transactional
    public void deleteById(UUID id) {
        roomRepository.deleteById(id);
    }

    public List<RoomListDto> findAllAsListDto() {
        return roomRepository.findAll().stream()
                .map(roomMapper::toListDto)
                .collect(Collectors.toList());
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
                    String buildingIdStr = getCellString(row.getCell(2));
                    String roomTypeIdStr = getCellString(row.getCell(3));
                    Integer floor = getCellInt(row.getCell(4));
                    Integer capacity = getCellInt(row.getCell(5));
                    Double area = getCellDouble(row.getCell(6));
                    String status = getCellString(row.getCell(7));
                    String activeStr = getCellString(row.getCell(8));

                    if (code == null || code.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": Mã phòng không được trống");
                        continue;
                    }
                    if (name == null || name.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": Tên phòng không được trống");
                        continue;
                    }
                    if (buildingIdStr == null || buildingIdStr.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": BuildingId không được trống");
                        continue;
                    }
                    if (roomTypeIdStr == null || roomTypeIdStr.isBlank()) {
                        errors.add("Dòng " + (i + 1) + ": RoomTypeId không được trống");
                        continue;
                    }

                    UUID buildingId = UUID.fromString(buildingIdStr.trim());
                    UUID roomTypeId = UUID.fromString(roomTypeIdStr.trim());
                    Boolean isActive = (activeStr == null || activeStr.isBlank())
                            ? Boolean.TRUE
                            : activeStr.trim().equals("1") || activeStr.trim().equalsIgnoreCase("true");

                    if (findByCode(code.trim()).isPresent()) {
                        errors.add("Dòng " + (i + 1) + ": Mã phòng đã tồn tại: " + code);
                        continue;
                    }

                    Room room = Room.builder()
                            .roomCode(code.trim())
                            .roomName(name.trim())
                            .buildingId(buildingId)
                            .roomTypeId(roomTypeId)
                            .floor(floor)
                            .capacity(capacity)
                            .area(area)
                            .status(status != null ? status.trim() : null)
                            .isActive(isActive)
                            .build();

                    save(room);

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
        List<Room> list = roomRepository.findAll();
        try (Workbook workbook = new XSSFWorkbook(); ByteArrayOutputStream out = new ByteArrayOutputStream()) {
            Sheet sheet = workbook.createSheet("Phòng học");
            CellStyle headerStyle = workbook.createCellStyle();
            Font headerFont = workbook.createFont();
            headerFont.setBold(true);
            headerStyle.setFont(headerFont);

            String[] headers = {
                    "Mã phòng", "Tên phòng", "BuildingId", "RoomTypeId",
                    "Tầng", "Sức chứa", "Diện tích (m2)", "Trạng thái", "Active"
            };
            Row headerRow = sheet.createRow(0);
            for (int i = 0; i < headers.length; i++) {
                Cell c = headerRow.createCell(i);
                c.setCellValue(headers[i]);
                c.setCellStyle(headerStyle);
            }

            int rowNum = 1;
            for (Room r : list) {
                Row row = sheet.createRow(rowNum++);
                row.createCell(0).setCellValue(r.getRoomCode());
                row.createCell(1).setCellValue(r.getRoomName());
                row.createCell(2).setCellValue(r.getBuildingId() != null ? r.getBuildingId().toString() : "");
                row.createCell(3).setCellValue(r.getRoomTypeId() != null ? r.getRoomTypeId().toString() : "");
                row.createCell(4).setCellValue(r.getFloor() != null ? r.getFloor() : 0);
                row.createCell(5).setCellValue(r.getCapacity() != null ? r.getCapacity() : 0);
                row.createCell(6).setCellValue(r.getArea() != null ? r.getArea() : 0.0);
                row.createCell(7).setCellValue(r.getStatus() != null ? r.getStatus() : "");
                row.createCell(8).setCellValue(Boolean.TRUE.equals(r.getIsActive()) ? "1" : "0");
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

    private static Double getCellDouble(Cell cell) {
        if (cell == null) return null;
        return switch (cell.getCellType()) {
            case NUMERIC -> cell.getNumericCellValue();
            case STRING -> {
                String s = cell.getStringCellValue();
                if (s == null || s.isBlank()) yield null;
                try {
                    yield Double.parseDouble(s.trim());
                } catch (NumberFormatException e) {
                    yield null;
                }
            }
            default -> null;
        };
    }
}

