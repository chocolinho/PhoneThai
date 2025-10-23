package controller;

import entity.User;
import java.io.IOException;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Áp dụng Filter này cho tất cả các URL có dạng /admin/*
@WebFilter(urlPatterns = {"/admin/*"})
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;
        HttpSession session = req.getSession();
        
        // Lấy thông tin người dùng từ session
        User user = (User) session.getAttribute("user");
        
        // Kiểm tra điều kiện
        if (user != null && user.getRole() == 1) {
            // Nếu người dùng đã đăng nhập VÀ có role là 1 (Admin)
            // -> Cho phép đi tiếp đến trang họ yêu cầu
            chain.doFilter(request, response);
        } else {
            // Nếu chưa đăng nhập hoặc không phải admin
            // -> "Đá" về trang đăng nhập
            res.sendRedirect(req.getContextPath() + "/login");
        }
    }
}