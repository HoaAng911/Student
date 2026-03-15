package com.example.demo.rome_types.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.example.demo.rome_types.dto.RoomTypeDto;
import com.example.demo.rome_types.dto.RoomTypeListDto;
import com.example.demo.rome_types.entity.RoomType;
import com.example.demo.rome_types.mapper.RoomTypeMapper;
import com.example.demo.rome_types.service.RoomTypeService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/room-types")
@RequiredArgsConstructor
public class RoomTypeController {

    private final RoomTypeService roomTypeService;
    private final RoomTypeMapper roomTypeMapper;

    @ModelAttribute("currentMenu")
    public String currentMenu() {
        return "room-types";
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("roomTypeCode"));
        Page<RoomTypeListDto> roomTypePage = roomTypeService.search(keyword, pageable);
        model.addAttribute("roomTypePage", roomTypePage);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomTypePage.getTotalPages());
        return "admin/room-types/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("roomType", new RoomTypeDto());
        model.addAttribute("isEdit", false);
        return "admin/room-types/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, RedirectAttributes redirectAttributes, Model model) {
        return roomTypeService.findByIdAsDto(id)
                .map(dto -> {
                    model.addAttribute("roomType", dto);
                    model.addAttribute("isEdit", true);
                    return "admin/room-types/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy loại phòng.");
                    return "redirect:/admin/room-types";
                });
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("roomType") RoomTypeDto dto,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean isEdit,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "admin/room-types/form";
        }
        if (!isEdit && roomTypeService.findByCode(dto.getRoomTypeCode()).isPresent()) {
            result.rejectValue("roomTypeCode", "duplicate", "Mã loại phòng đã tồn tại.");
            model.addAttribute("isEdit", false);
            return "admin/room-types/form";
        }
        if (isEdit) {
            roomTypeService.findByIdAsDto(dto.getRoomTypeId()).ifPresent(existing -> {
                if (!existing.getRoomTypeCode().equals(dto.getRoomTypeCode())
                        && roomTypeService.findByCode(dto.getRoomTypeCode()).isPresent()) {
                    result.rejectValue("roomTypeCode", "duplicate", "Mã loại phòng đã tồn tại.");
                }
            });
            if (result.hasErrors()) {
                model.addAttribute("isEdit", true);
                return "admin/room-types/form";
            }
        }
        RoomType entity = roomTypeMapper.toEntity(dto);
        roomTypeService.save(entity);
        redirectAttributes.addFlashAttribute("success", isEdit ? "Cập nhật thành công." : "Thêm mới thành công.");
        return "redirect:/admin/room-types";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        roomTypeService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa loại phòng.");
        return "redirect:/admin/room-types";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        try {
            byte[] bytes = roomTypeService.exportToExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "room_types.xlsx");
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
            return "redirect:/admin/room-types";
        }
        List<String> errors = roomTypeService.importFromExcel(file);
        if (errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("success", "Import thành công.");
        } else {
            redirectAttributes.addFlashAttribute("importErrors", errors);
            redirectAttributes.addFlashAttribute("error", "Import có lỗi (" + errors.size() + ").");
        }
        return "redirect:/admin/room-types";
    }

    @GetMapping("/print")
    public String print(
            @RequestParam(required = false) String keyword,
            Model model) {
        List<RoomTypeListDto> list = keyword != null && !keyword.isBlank()
                ? roomTypeService.search(keyword, Pageable.unpaged()).getContent()
                : roomTypeService.findAllAsListDto();
        model.addAttribute("roomTypes", list);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "admin/room-types/print";
    }
}
