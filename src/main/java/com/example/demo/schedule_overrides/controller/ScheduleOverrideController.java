package com.example.demo.schedule_overrides.controller;

import com.example.demo.rooms.entity.Room;
import com.example.demo.schedule_overrides.dto.ScheduleOverrideDto;
import com.example.demo.schedule_overrides.dto.ScheduleOverrideListDto;
import com.example.demo.schedule_overrides.entity.ScheduleOverride;
import com.example.demo.schedule_overrides.mapper.ScheduleOverrideMapper;
import com.example.demo.schedule_overrides.service.ScheduleOverrideService;
import com.example.demo.room_block_times.service.RoomBlockTimeService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Controller
@RequestMapping("/admin/schedule-overrides")
@RequiredArgsConstructor
public class ScheduleOverrideController {

    private final ScheduleOverrideService scheduleOverrideService;
    private final ScheduleOverrideMapper scheduleOverrideMapper;
    private final RoomBlockTimeService roomBlockTimeService;

    @ModelAttribute("currentMenu")
    public String currentMenu() {
        return "schedule-overrides";
    }

    @ModelAttribute("rooms")
    public List<Room> rooms() {
        return scheduleOverrideService.findAllRooms();
    }

    // ─── LIST ────────────────────────────────────────────────────────────────

    @GetMapping
    public String list(
            @RequestParam(required = false) String keyword,
            @RequestParam(required = false) UUID roomId,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Boolean isActive,
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            Model model
    ) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("startTime").descending());
        Page<ScheduleOverrideListDto> overridePage = scheduleOverrideService.search(
                keyword, roomId, status, isActive, pageable
        );

        model.addAttribute("overridePage", overridePage);
        model.addAttribute("keyword",     keyword  != null ? keyword  : "");
        model.addAttribute("roomId",      roomId);
        model.addAttribute("status",      status   != null ? status   : "");
        model.addAttribute("isActive",    isActive);
        model.addAttribute("currentPage", page);
        model.addAttribute("totalPages",  overridePage.getTotalPages());
        return "admin/schedule-overrides/list";
    }

    // ─── ADD FORM ─────────────────────────────────────────────────────────────

    @GetMapping("/add")
    public String addForm(Model model) {
        ScheduleOverrideDto dto = ScheduleOverrideDto.builder()
                .isActive(true)
                .status("PENDING")
                .build();
        model.addAttribute("override", dto);
        model.addAttribute("isEdit", false);
        return "admin/schedule-overrides/form";
    }

    // ─── EDIT FORM ────────────────────────────────────────────────────────────

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable UUID id,
                           RedirectAttributes redirectAttributes,
                           Model model) {
        Optional<ScheduleOverrideDto> opt = scheduleOverrideService.findByIdAsDto(id);
        if (opt.isEmpty()) {
            redirectAttributes.addFlashAttribute("error", "Không tìm thấy bản ghi đặt phòng.");
            return "redirect:/admin/schedule-overrides";
        }
        model.addAttribute("override", opt.get());
        model.addAttribute("isEdit", true);
        return "admin/schedule-overrides/form";
    }

    // ─── SAVE ─────────────────────────────────────────────────────────────────

    @PostMapping("/save")
    public String save(
            @Valid @ModelAttribute("override") ScheduleOverrideDto dto,
            BindingResult result,
            @RequestParam(defaultValue = "false") boolean isEdit,
            RedirectAttributes redirectAttributes,
            Model model
    ) {
        // Validate endTime > startTime
        if (dto.getStartTime() != null && dto.getEndTime() != null
                && !dto.getEndTime().isAfter(dto.getStartTime())) {
            result.rejectValue("endTime", "invalid", "Thời gian kết thúc phải sau thời gian bắt đầu.");
        }

        // Check against completely blocked room times
        if (dto.getRoomId() != null && dto.getStartTime() != null && dto.getEndTime() != null) {
            if (!result.hasErrors()) { // Only do this check if times are basically valid
                if (roomBlockTimeService.isRoomBlocked(dto.getRoomId(), dto.getStartTime(), dto.getEndTime())) {
                    result.rejectValue("roomId", "blocked", "Phòng này đã bị khóa trong khoảng thời gian đã chọn!");
                }
            }
        }

        if (result.hasErrors()) {
            model.addAttribute("isEdit", isEdit);
            return "admin/schedule-overrides/form";
        }

        // Duplicate code check on create
        if (!isEdit && scheduleOverrideService.findByCode(dto.getOverrideCode()).isPresent()) {
            result.rejectValue("overrideCode", "duplicate", "Mã đặt phòng đã tồn tại.");
            model.addAttribute("isEdit", false);
            return "admin/schedule-overrides/form";
        }

        // Duplicate code check on edit (only if code changed)
        if (isEdit) {
            scheduleOverrideService.findByIdAsDto(dto.getId()).ifPresent(existing -> {
                if (!existing.getOverrideCode().equals(dto.getOverrideCode())
                        && scheduleOverrideService.findByCode(dto.getOverrideCode()).isPresent()) {
                    result.rejectValue("overrideCode", "duplicate", "Mã đặt phòng đã tồn tại.");
                }
            });
            if (result.hasErrors()) {
                model.addAttribute("isEdit", true);
                return "admin/schedule-overrides/form";
            }
        }

        ScheduleOverride entity = scheduleOverrideMapper.toEntity(dto);
        scheduleOverrideService.save(entity);
        redirectAttributes.addFlashAttribute("success",
                isEdit ? "Cập nhật lịch đặt phòng thành công." : "Thêm mới lịch đặt phòng thành công.");
        return "redirect:/admin/schedule-overrides";
    }

    // ─── DELETE ───────────────────────────────────────────────────────────────

    @PostMapping("/delete/{id}")
    public String delete(@PathVariable UUID id, RedirectAttributes redirectAttributes) {
        scheduleOverrideService.deleteById(id);
        redirectAttributes.addFlashAttribute("success", "Đã xóa lịch đặt phòng.");
        return "redirect:/admin/schedule-overrides";
    }
}
