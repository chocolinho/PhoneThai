package controller;

import dao.BillingInfoDAO;
import entity.BillingInfo;
import entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.time.Year;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "BillingController", urlPatterns = {"/billing"})
public class BillingController extends HttpServlet {

    private final BillingInfoDAO billingInfoDAO = new BillingInfoDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/Login.jsp?redirect=billing");
            return;
        }

        BillingInfo info = billingInfoDAO.findByUser(user.getUserId());
        req.setAttribute("billingInfo", info);
        req.setAttribute("currentYear", java.time.Year.now().getValue());
        req.getRequestDispatcher("/billing.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/Login.jsp?redirect=billing");
            return;
        }

        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            billingInfoDAO.deleteByUser(user.getUserId());
            resp.sendRedirect(req.getContextPath() + "/billing?deleted=1");
            return;
        }

        List<String> errors = new ArrayList<>();
        String cardName = safe(req.getParameter("cardName"));
        String cardNumber = safe(req.getParameter("cardNumber"));
        String bankName = safe(req.getParameter("bankName"));
        String monthParam = req.getParameter("expiryMonth");
        String yearParam = req.getParameter("expiryYear");

        if (cardName.isBlank()) errors.add("Vui lòng nhập tên in trên thẻ");
        if (cardNumber.isBlank()) {
            errors.add("Vui lòng nhập số thẻ");
        } else {
            cardNumber = cardNumber.replaceAll("\\s+", "");
            if (!cardNumber.matches("\\d{12,19}")) {
                errors.add("Số thẻ không hợp lệ");
            }
        }
        if (bankName.isBlank()) errors.add("Vui lòng nhập ngân hàng phát hành");

        int month = parseInt(monthParam, errors, "Tháng hết hạn không hợp lệ");
        int year = parseInt(yearParam, errors, "Năm hết hạn không hợp lệ");
        if (errors.isEmpty()) {
            if (month < 1 || month > 12) {
                errors.add("Tháng hết hạn phải từ 1 đến 12");
            }
            int currentYear = Year.now().getValue();
            if (year < currentYear) {
                errors.add("Thẻ đã hết hạn");
            }
        }

        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            BillingInfo form = new BillingInfo();
            form.setCardName(cardName);
            form.setCardNumber(cardNumber);
            form.setBankName(bankName);
            form.setExpiryMonth(month);
            form.setExpiryYear(year);
            req.setAttribute("billingInfo", form);
            req.setAttribute("currentYear", java.time.Year.now().getValue());
            req.getRequestDispatcher("/billing.jsp").forward(req, resp);
            return;
        }

        BillingInfo info = new BillingInfo();
        info.setUserId(user.getUserId());
        info.setCardName(cardName);
        info.setCardNumber(cardNumber);
        info.setBankName(bankName);
        info.setExpiryMonth(month);
        info.setExpiryYear(year);

        boolean ok = billingInfoDAO.save(info);
        if (ok) {
            resp.sendRedirect(req.getContextPath() + "/billing?saved=1");
        } else {
            errors.add("Không thể lưu thông tin thanh toán. Vui lòng thử lại");
            req.setAttribute("errors", errors);
            req.setAttribute("billingInfo", info);
            req.setAttribute("currentYear", java.time.Year.now().getValue());
            req.getRequestDispatcher("/billing.jsp").forward(req, resp);
        }
    }

    private int parseInt(String value, List<String> errors, String message) {
        if (value == null || value.isBlank()) {
            errors.add(message);
            return 0;
        }
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            errors.add(message);
            return 0;
        }
    }

    private String safe(String input) {
        return input == null ? "" : input.trim();
    }
}
