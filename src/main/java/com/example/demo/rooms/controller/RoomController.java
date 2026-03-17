package com.example.demo.rooms.controller;

import com.example.demo.buildings.entity.Building;
import com.example.demo.buildings.repository.BuildingRepository;
import com.example.demo.rooms.dto.RoomDto;
import com.example.demo.rooms.dto.RoomListDto;
import com.example.demo.rooms.entity.Room;
import com.example.demo.rooms.mapper.RoomMapper;
import com.example.demo.rooms.service.RoomService;
import com.example.demo.rome_types.dto.RoomTypeListDto;
import com.example.demo.rome_types.service.RoomTypeService;

import jakarta.validation.Valid;
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
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/rooms")
@RequiredArgsConstructor
public class RoomController {

    private final RoomService roomService;
    private final RoomMapper roomMapper;
    private final RoomTypeService roomTypeService;
    private final BuildingRepository buildingRepository;

    @ModelAttribute("currentMenu")
    public String currentMenu() {
        return "rooms";
    }

    @ModelAttribute("roomTypes")
    public List<RoomTypeListDto> roomTypes() {
        return roomTypeService.findAllAsListDto();
    }

    @ModelAttribute("buildings")
    public List<Building> buildings() {
        return buildingRepository.findAll();
    }

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID buildingId,
            @RequestParam(required = false) UUID roomTypeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("roomCode"));
        Page<RoomListDto> roomPage = roomService.search(
                keyword, buildingId, roomTypeId, status, isActive, pageable
        );

        model.addAttribute("roomPage", roomPage);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        model.addAttribute("buildingId", buildingId);
        model.addAttribute("roomTypeId", roomTypeId);
        model.addAttribute("status", status != null ? status : "");
        model.addAttribute("isActive", isActive);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages", roomPage.getTotalPages());
        return "admin/rooms/list";
    }

    @GetMapping("/add")
    public String addForm(Model model) {
        RoomDto dto = RoomDto.builder()
                .isActive(true)
                .build();
        model.addAttribute("room", dto);
        model.addAttribute("isEdit", false);
        return "admin/rooms/form";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        Optional<RoomDto> opt = roomService.findByIdAsDto(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy phòng.");
            return "redirect:/admin/rooms";
        }
        model.addAttribute("room", opt.get());
        model.addAttribute("isEdit", true);
        return "admin/rooms/form";
    }

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("room") RoomDto dto,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean isEdit,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "admin/rooms/form";
        }

        if (!isEdit && roomService.findByCode(dto.getRoomCode()).isPresent()) {
            result.rejectValue("roomCode", "duplicate", "Mã phòng đã tồn tại.");
            model.addAttribute("isEdit", false);
            return "admin/rooms/form";
        }

        if (isEdit) {
            roomService.findByIdAsDto(dto.getId()).ifPresent(existing -> {
                if (!existing.getRoomCode().equals(dto.getRoomCode())
                        && roomService.findByCode(dto.getRoomCode()).isPresent()) {
                    result.rejectValue("roomCode", "duplicate", "Mã phòng đã tồn tại.");
                }
            });
            if (result.hasErrors()) {
                model.addAttribute("isEdit", true);
                return "admin/rooms/form";
            }
        }

        Room entity = roomMapper.toEntity(dto);
        roomService.save(entity);
        redirectAttributes.addFlashAttribute("success", isEdit ? "Cập nhật phòng thành công." : "Thêm mới phòng thành công.");
        return "redirect:/admin/rooms";
    }

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        roomService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa phòng.");
        return "redirect:/admin/rooms";
    }

    @GetMapping("/export/excel")
    public ResponseEntity<byte[]> exportExcel() {
        try {
            byte[] bytes = roomService.exportToExcel();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.parseMediaType(
                    "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"));
            headers.setContentDispositionFormData("attachment", "rooms.xlsx");
            return ResponseEntity.ok().headers(headers).body(bytes);
        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @PostMapping("/import/excel")
    public String importExcel(
            @RequestParam("file") MultipartFile file,
            RedirectAttributes redirectAttributes
    ) {
        if (file.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Vui lòng chọn file Excel.");
            return "redirect:/admin/rooms";
        }
        List<String> errors = roomService.importFromExcel(file);
        if (errors.isEmpty()) {
            redirectAttributes.addFlashAttribute("success", "Import phòng thành công.");
        } else {
            redirectAttributes.addFlashAttribute("importErrors", errors);
            redirectAttributes.addFlashAttribute("error", "Import có lỗi (" + errors.size() + ").");
        }
        return "redirect:/admin/rooms";
    }

    @GetMapping("/print")
    public String print(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID buildingId,
            @RequestParam(required = false) UUID roomTypeId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive,
            Model model
    ) {
        List<RoomListDto> list = roomService.search(
                keyword, buildingId, roomTypeId, status, isActive, Pageable.unpaged()
        ).getContent();
        model.addAttribute("rooms", list);
        model.addAttribute("keyword", keyword != null ? keyword : "");
        return "admin/rooms/print";
    }
}

