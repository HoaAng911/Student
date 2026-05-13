package com.example.demo.config;

import com.example.demo.users.model.entity.User;
import com.example.demo.roles.model.entity.Role;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

import java.util.Set;
import java.util.stream.Collectors;

@Component
public class AuthInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String path = request.getRequestURI();
        
        // Bỏ qua các path công khai
        if (path.equals("/login") || path.equals("/logout") || path.startsWith("/css/") || path.startsWith("/js/")) {
            return true;
        }

        HttpSession session = request.getSession();
        User currentUser = (User) session.getAttribute("currentUser");

        if (currentUser == null) {
            response.sendRedirect("/login");
            return false;
        }

        Set<String> roles = currentUser.getRoles().stream()
                .map(Role::getCode)
                .collect(Collectors.toSet());

        // Kiểm tra quyền cơ bản
        if (path.startsWith("/admin/") || path.startsWith("/api/grade-components")) {
            if (!roles.contains("ADMIN")) {
                response.sendError(403, "Bạn không có quyền truy cập vùng này!");
                return false;
            }
        }
        
        if (path.startsWith("/api/student-grades")) {
            if (!roles.contains("TEACHER") && !roles.contains("ADMIN")) {
                response.sendError(403, "Chỉ dành cho Giảng viên hoặc Admin!");
                return false;
            }
        }

        return true;
    }
}
