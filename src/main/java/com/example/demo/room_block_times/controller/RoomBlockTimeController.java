package com.example.demo.room_block_times.controller;

import com.example.demo.room_block_times.dto.RoomBlockTimeDto;
import com.example.demo.room_block_times.dto.RoomBlockTimeListDto;
import com.example.demo.room_block_times.entity.RoomBlockTime;
import com.example.demo.room_block_times.mapper.RoomBlockTimeMapper;
import com.example.demo.room_block_times.service.RoomBlockTimeService;
import com.example.demo.rooms.dto.RoomListDto;
import com.example.demo.rooms.service.RoomService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/room-block-times")
@RequiredArgsConstructor
public class RoomBlockTimeController {

    private final RoomBlockTimeService roomBlockTimeService;
    private final RoomBlockTimeMapper roomBlockTimeMapper;
    private final RoomService roomService;

    @ModelAttribute("currentMenu")
    public String currentMenu() {
        return "room-block-times";
    }

    @ModelAttribute("rooms")
    public List<RoomListDto> rooms() {
        return roomService.findAllAsListDto();
    }

    // ─── LIST ────────────────────────────────────────────────────────────────

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID roomId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String blockType,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("createdAt").descending());
        Page<RoomBlockTimeListDto> timePage = roomBlockTimeService.search(
                keyword, roomId, status, blockType, pageable
        );

        model.addAttribute("timePage", timePage);
        model.addAttribute("keyword",     keyword  != null ? keyword  : "");
        model.addAttribute("roomId",      roomId);
        model.addAttribute("status",      status   != null ? status   : "");
        model.addAttribute("blockType",   blockType != null ? blockType : "");
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  timePage.getTotalPages());
        return "admin/room-block-times/list";
    }

    // ─── ADD FORM ─────────────────────────────────────────────────────────────

    @GetMapping("/add")
    public String addForm(Model model) {
        RoomBlockTimeDto dto = RoomBlockTimeDto.builder()
                .status("ACTIVE")
                .blockType("MAINTENANCE")
                .build();
        model.addAttribute("blockTime", dto);
        model.addAttribute("isEdit", false);
        return "admin/room-block-times/form";
    }

    // ─── EDIT FORM ────────────────────────────────────────────────────────────

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        Optional<RoomBlockTimeDto> opt = roomBlockTimeService.findByIdAsDto(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy dữ liệu khóa phòng.");
            return "redirect:/admin/room-block-times";
        }
        model.addAttribute("blockTime", opt.get());
        model.addAttribute("isEdit", true);
        return "admin/room-block-times/form";
    }

    // ─── SAVE ─────────────────────────────────────────────────────────────────

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("blockTime") RoomBlockTimeDto dto,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean isEdit,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        if (dto.getStartDate() != null && dto.getEndDate() != null
                && dto.getEndDate().isBefore(dto.getStartDate())) {
            result.rejectValue("endDate", "invalid", "Ngày kết thúc không được trước ngày bắt đầu.");
        }

        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "admin/room-block-times/form";
        }

        RoomBlockTime entity = roomBlockTimeMapper.toEntity(dto);
        
        // Use existing ID if it's an edit
        if (isEdit && dto.getBlockId() != null) {
            roomBlockTimeService.findById(dto.getBlockId()).ifPresent(existing -> {
                entity.setCreatedAt(existing.getCreatedAt()); // keep original creation time
            });
        }
        
        roomBlockTimeService.save(entity);
        redirectAttributes.addFlashAttribute("success",
                isEdit ? "Cập nhật khóa phòng thành công." : "Thêm mới khóa phòng thành công.");
        return "redirect:/admin/room-block-times";
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        roomBlockTimeService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa bản ghi khóa phòng.");
        return "redirect:/admin/room-block-times";
    }

    // ─── API OVERLAP CHECK ────────────────────────────────────────────────────

    @GetMapping("/api/check-overlap")
    @ResponseBody
    public boolean checkOverlap(
            @RequestParam UUID roomId,
            @RequestParam String startTime,
            @RequestParam String endTime) {
        try {
            LocalDateTime start = LocalDateTime.parse(startTime);
            LocalDateTime end = LocalDateTime.parse(endTime);
            return roomBlockTimeService.isRoomBlocked(roomId, start, end);
        } catch (Exception e) {
            return false;
        }
    }
}
