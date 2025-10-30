package controller;

import dao.UserDAO;
import entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.sql.Date;
import java.time.*;

@WebServlet(name = "ForgotController", urlPatterns = {"/forgot"})
public class ForgotController extends HttpServlet {

    private static final int MAX_ATTEMPTS_PER_DAY = 5; // đạt 5 lần là khoá
    private static final int LOCK_HOURS = 24;          // khoá 24h
    private static final String ADMIN_EMAIL = "admin@phonethai.com";
    private static final String ADMIN_PHONE = "0912-345-678";

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.getRequestDispatcher("/auth/Forgot.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");

        String username = trim(req.getParameter("username"));
        String email    = trim(req.getParameter("email"));

        UserDAO dao = new UserDAO();
        User u = dao.findByUsername(username);
        if (u == null) {
            fail(req, resp, "Thông tin không khớp. Vui lòng kiểm tra lại.");
            return;
        }

        // Sang ngày mới -> reset counter
        LocalDate today = LocalDate.now();
        LocalDate attemptsDate = toLocal(u.getResetAttemptsDate());
        if (attemptsDate == null || !attemptsDate.equals(today)) {
            dao.resetForgotAttempts(u.getUserId(), Date.valueOf(today));
            u.setResetAttempts(0);
            u.setResetAttemptsDate(Date.valueOf(today));
        }

        // Đang bị khoá?
        if (u.getResetLockedUntil() != null
                && u.getResetLockedUntil().toInstant().isAfter(Instant.now())) {
            showAdmin(req, resp);
            return;
        }

        // Tăng lượt thử
        int attempts = (u.getResetAttempts() == null ? 0 : u.getResetAttempts()) + 1;
        dao.updateForgotAttempts(u.getUserId(), attempts, Date.valueOf(today));
        u.setResetAttempts(attempts);

        // Đạt/ngang ngưỡng -> khoá 24h (không import Timestamp, dùng fully-qualified)
        if (attempts >= MAX_ATTEMPTS_PER_DAY) {
            dao.lockForgotUntil(
                u.getUserId(),
                java.sql.Timestamp.valueOf(LocalDateTime.now().plusHours(LOCK_HOURS))
            );
            showAdmin(req, resp);
            return;
        }

        // Xác thực email
        if (!equalsIgnoreCase(email, u.getEmail())) {
            fail(req, resp, "Thông tin email không khớp. Lần thử: " + attempts + "/" + MAX_ATTEMPTS_PER_DAY);
            return;
        }

        // Sinh mật khẩu tạm & nhờ DAO hash + must_change_password=1
        String tempPass = generateTemp(10);
        if (!dao.resetPassword(u.getUserId(), tempPass)) {
            fail(req, resp, "Không thể tạo mật khẩu tạm. Vui lòng thử lại.");
            return;
        }

        // Mở khoá + reset đếm sau khi thành công
        dao.clearForgotLockAndAttempts(u.getUserId(), Date.valueOf(today));
        u.setResetAttempts(0);
        u.setResetAttemptsDate(Date.valueOf(today));
        u.setResetLockedUntil(null);

        req.setAttribute("newPassword", tempPass);
        req.getRequestDispatcher("/auth/Forgot.jsp").forward(req, resp);
    }

    /* ===== Helpers gọn ===== */
    private static String trim(String s) { return s == null ? "" : s.trim(); }

    private static boolean equalsIgnoreCase(String a, String b) {
        if (a == null) a = ""; if (b == null) b = "";
        return a.trim().equalsIgnoreCase(b.trim());
    }

    // Tránh lỗi java.sql.Date.toInstant()
    private static LocalDate toLocal(java.util.Date d) {
        if (d == null) return null;
        if (d instanceof java.sql.Date) return ((java.sql.Date) d).toLocalDate();
        return d.toInstant().atZone(ZoneId.systemDefault()).toLocalDate();
    }

    private void fail(HttpServletRequest req, HttpServletResponse resp, String msg)
            throws ServletException, IOException {
        req.setAttribute("error", msg);
        req.getRequestDispatcher("/auth/Forgot.jsp").forward(req, resp);
    }

    private void showAdmin(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        req.setAttribute("adminLock", true);
        req.setAttribute("adminEmail", ADMIN_EMAIL);
        req.setAttribute("adminPhone", ADMIN_PHONE);
        req.getRequestDispatcher("/auth/Forgot.jsp").forward(req, resp);
    }

    private static String generateTemp(int len) {
        String chars = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnpqrstuvwxyz23456789";
        StringBuilder sb = new StringBuilder(len);
        java.security.SecureRandom rnd = new java.security.SecureRandom();
        for (int i = 0; i < len; i++) sb.append(chars.charAt(rnd.nextInt(chars.length())));
        return sb.toString();
    }
}
