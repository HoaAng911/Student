package com.example.demo.users.controller;

import com.example.demo.users.model.entity.User;
import com.example.demo.users.service.AuthService;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.Optional;

@Controller
public class AuthController {

    @Autowired
    private AuthService authService;

    @GetMapping("/login")
    public String loginPage() {
        return "login";
    }

    @PostMapping("/login")
    public String login(@RequestParam String username, 
                        @RequestParam String password, 
                        HttpSession session, 
                        Model model) {
        Optional<User> userOpt = authService.login(username, password);
        
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            session.setAttribute("currentUser", user);
            
            // Điều hướng dựa trên Role (đơn giản)
            String roleCode = user.getRoles().stream()
                    .findFirst()
                    .map(r -> r.getCode())
                    .orElse("STUDENT");
            
            if ("ADMIN".equals(roleCode) || "TEACHER".equals(roleCode)) {
                return "redirect:/api"; // Đổ chung về Dashboard
            } else {
                return "redirect:/student/dashboard"; // Sinh viên vào Dashboard riêng
            }
        }
        
        model.addAttribute("error", "Username hoặc Password không đúng!");
        return "login";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session) {
        session.invalidate();
        return "redirect:/login";
    }
}
