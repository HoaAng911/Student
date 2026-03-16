package com.example.demo.equipments.controller;

import com.example.demo.equipments.dto.EquipmentDto;
import com.example.demo.equipments.dto.EquipmentListDto;
import com.example.demo.equipments.entity.Equipment;
import com.example.demo.equipments.mapper.EquipmentMapper;
import com.example.demo.equipments.service.EquipmentService;
import com.example.demo.rooms.dto.RoomListDto;
import com.example.demo.rooms.service.RoomService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.*;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/equipments")
@RequiredArgsConstructor
public class EquipmentController {

    private final EquipmentService equipmentService;
    private final EquipmentMapper equipmentMapper;
    private final RoomService roomService;

    @ModelAttribute("currentMenu")
    public String currentMenu() {
        return "equipments";
    }

    @ModelAttribute("rooms")
    public List<RoomListDto> rooms() {
        return roomService.findAllAsListDto();
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID roomId,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("equipmentCode"));
        Page<EquipmentListDto> equipmentPage = equipmentService.search(keyword, status, roomId, pageable);
        model.addAttribute("equipmentPage", equipmentPage);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("status", status != null ? status : "");
        model.addAttribute("roomId", roomId);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", equipmentPage.getTotalPages());
        return "admin/equipments/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("equipment", new EquipmentDto());
        model.addAttribute("isEdit", false);
        return "admin/equipments/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, RedirectAttributes redirectAttributes, Model model) {
        return equipmentService.findByIdAsDto(id)
                .map(dto -> {
                    model.addAttribute("equipment", dto);
                    model.addAttribute("isEdit", true);
                    return "admin/equipments/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy thiết bị.");
                    return "redirect:/admin/equipments";
                });
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("equipment") EquipmentDto dto,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean isEdit,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "admin/equipments/form";
        }
        if (!isEdit && equipmentService.findByCode(dto.getEquipmentCode()).isPresent()) {
            result.rejectValue("equipmentCode", "duplicate", "Mã thiết bị đã tồn tại.");
            model.addAttribute("isEdit", false);
            return "admin/equipments/form";
        }
        if (isEdit) {
            equipmentService.findByIdAsDto(dto.getId()).ifPresent(existing -> {
                if (!existing.getEquipmentCode().equals(dto.getEquipmentCode())
                        && equipmentService.findByCode(dto.getEquipmentCode()).isPresent()) {
                    result.rejectValue("equipmentCode", "duplicate", "Mã thiết bị đã tồn tại.");
                }
            });
            if (result.hasErrors()) {
                model.addAttribute("isEdit", true);
                return "admin/equipments/form";
            }
        }
        Equipment entity = equipmentMapper.toEntity(dto);
        equipmentService.save(entity);
        redirectAttributes.addFlashAttribute("success", isEdit ? "Cập nhật thiết bị thành công." : "Thêm mới thiết bị thành công.");
        return "redirect:/admin/equipments";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        equipmentService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa thiết bị.");
        return "redirect:/admin/equipments";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        try {
            byte[] bytes = equipmentService.exportToExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "equipments.xlsx");
            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/import/excel")
    public String importExcel(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file Excel.");
            return "redirect:/admin/equipments";
        }
        List<String> errors = equipmentService.importFromExcel(file);
        if (errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("success", "Import thiết bị thành công.");
        } else {
            redirectAttributes.addFlashAttribute("importErrors", errors);
            redirectAttributes.addFlashAttribute("error", "Import có lỗi (" + errors.size() + ").");
        }
        return "redirect:/admin/equipments";
    }

    @GetMapping("/print")
    public String print(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) UUID roomId,
            Model model) {
        List<EquipmentListDto> list = equipmentService.search(keyword, status, roomId, Pageable.unpaged()).getContent();
        model.addAttribute("equipments", list);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "admin/equipments/print";
    }
}
