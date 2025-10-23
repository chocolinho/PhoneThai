package controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;

@WebServlet(name = "LogoutController", urlPatterns = {"/logout"})
public class LogoutController extends HttpServlet {

    private void clearRememberCookies(HttpServletRequest req, HttpServletResponse resp) {
        // Xóa cookie userC / passC nếu bạn dùng Remember me
        Cookie userC = new Cookie("userC", "");
        Cookie passC = new Cookie("passC", "");
        userC.setMaxAge(0);
        passC.setMaxAge(0);
        // Quan trọng: đặt path để ghi đè đúng cookie cũ
        userC.setPath("/");
        passC.setPath("/");
        resp.addCookie(userC);
        resp.addCookie(passC);
    }

    private void doLogout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // Hủy session
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }

        // Xóa cookie Remember me
        clearRememberCookies(req, resp);

        // (tùy chọn) chống back-cache các trang cần login
        resp.setHeader("Cache-Control", "no-cache, no-store, must-revalidate"); // HTTP 1.1
        resp.setHeader("Pragma", "no-cache"); // HTTP 1.0
        resp.setDateHeader("Expires", 0); // Proxies

        // Chuyển hướng: về trang chủ
        resp.sendRedirect(req.getContextPath() + "/");
        // Nếu muốn về trang login: 
        // resp.sendRedirect(req.getContextPath() + "/auth/Login.jsp");
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doLogout(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        doLogout(req, resp);
    }
}
