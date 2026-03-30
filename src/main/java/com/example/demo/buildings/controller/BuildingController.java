package com.example.demo.buildings.controller;

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

import com.example.demo.buildings.dto.BuildingDto;
import com.example.demo.buildings.dto.BuildingListDto;
import com.example.demo.buildings.entity.Building;
import com.example.demo.buildings.mapper.BuildingMapper;
import com.example.demo.buildings.service.BuildingService;

import jakarta.validation.Valid;
import java.util.List;
import java.util.UUID;

@Controller
@RequestMapping("/admin/buildings")
@RequiredArgsConstructor
public class BuildingController {

    private final BuildingService buildingService;
    private final BuildingMapper buildingMapper;

    @ModelAttribute("currentMenu")
    public String currentMenu() {
        return "buildings";
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("buildingCode"));
        Page<BuildingListDto> buildingPage = buildingService.search(keyword, pageable);
        model.addAttribute("buildingPage", buildingPage);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", buildingPage.getTotalPages());
        return "admin/buildings/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        model.addAttribute("building", new BuildingDto());
        model.addAttribute("isEdit", false);
        return "admin/buildings/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id, RedirectAttributes redirectAttributes, Model model) {
        return buildingService.findByIdAsDto(id)
                .map(dto -> {
                    model.addAttribute("building", dto);
                    model.addAttribute("isEdit", true);
                    return "admin/buildings/form";
                })
                .orElseGet(() -> {
                    redirectAttributes.addFlashAttribute("error", "Không tìm thấy toà nhà.");
                    return "redirect:/admin/buildings";
                });
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("building") BuildingDto dto,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean isEdit,
            RedirectAttributes redirectAttributes,
            Model model) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "admin/buildings/form";
        }
        if (!isEdit && buildingService.findByCode(dto.getBuildingCode()).isPresent()) {
            result.rejectValue("buildingCode", "duplicate", "Mã toà nhà đã tồn tại.");
            model.addAttribute("isEdit", false);
            return "admin/buildings/form";
        }
        if (isEdit) {
            buildingService.findByIdAsDto(dto.getId()).ifPresent(existing -> {
                if (!existing.getBuildingCode().equals(dto.getBuildingCode())
                        && buildingService.findByCode(dto.getBuildingCode()).isPresent()) {
                    result.rejectValue("buildingCode", "duplicate", "Mã toà nhà đã tồn tại.");
                }
            });
            if (result.hasErrors()) {
                model.addAttribute("isEdit", true);
                return "admin/buildings/form";
            }
        }
        Building entity = buildingMapper.toEntity(dto);
        buildingService.save(entity);
        redirectAttributes.addFlashAttribute("success", isEdit ? "Cập nhật thành công." : "Thêm mới thành công.");
        return "redirect:/admin/buildings";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        buildingService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa toà nhà.");
        return "redirect:/admin/buildings";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        try {
            byte[] bytes = buildingService.exportToExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "buildings.xlsx");
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
            return "redirect:/admin/buildings";
        }
        List<String> errors = buildingService.importFromExcel(file);
        if (errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("success", "Import thành công.");
        } else {
            redirectAttributes.addFlashAttribute("importErrors", errors);
            redirectAttributes.addFlashAttribute("error", "Import có lỗi (" + errors.size() + ").");
        }
        return "redirect:/admin/buildings";
    }

    @GetMapping("/print")
    public String print(
            @RequestParam(required = false) String keyword,
            Model model) {
        List<BuildingListDto> list = keyword != null && !keyword.isBlank()
                ? buildingService.search(keyword, Pageable.unpaged()).getContent()
                : buildingService.findAllAsListDto();
        model.addAttribute("buildings", list);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "admin/buildings/print";
    }
}
