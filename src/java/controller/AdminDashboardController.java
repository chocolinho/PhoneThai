package controller;

import dao.OrderDAO;
import dao.ProductDAOS;
import dao.UserDAO;
import entity.Order;
import entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "AdminDashboardController", urlPatterns = {"/admin/dashboard"})
public class AdminDashboardController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        // Bảo vệ: bắt buộc đăng nhập + role admin
        HttpSession session = req.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        if (u == null || u.getRole() != 1) {
            resp.sendRedirect(req.getContextPath() + "/auth/Login.jsp");
            return;
        }

        // DAOs
        UserDAO userDAO = new UserDAO();
        ProductDAOS productDAO = new ProductDAOS();
        OrderDAO orderDAO = new OrderDAO();

        // ==== KPIs ====
        int totalUsers = 0;
        int totalProducts = 0;
        int totalOrders = 0;
        double totalRevenue = 0.0;

        // totalUsers
        try {
            // Nếu UserDAO có hàm countAll() thì dùng; nếu chưa có -> fallback listAll().size()
            try {
                totalUsers = (int) UserDAO.class.getMethod("countAll").invoke(userDAO);
            } catch (NoSuchMethodException ignore) {
                totalUsers = userDAO.listAll().size();
            }
        } catch (Exception e) {
            totalUsers = 0;
        }

        // totalProducts
        try {
            // Nếu ProductDAOS có countAll() thì dùng; nếu chưa có -> getAllProducts().size()
            try {
                totalProducts = (int) ProductDAOS.class.getMethod("countAll").invoke(productDAO);
            } catch (NoSuchMethodException ignore) {
                totalProducts = productDAO.getAllProducts().size();
            }
        } catch (Exception e) {
            totalProducts = 0;
        }

        // totalOrders
        try {
            // Nếu OrderDAO có countAll() thì dùng; nếu chưa -> count(null, null)
            try {
                totalOrders = (int) OrderDAO.class.getMethod("countAll").invoke(orderDAO);
            } catch (NoSuchMethodException ignore) {
                totalOrders = orderDAO.count(null, null);
            }
        } catch (Exception e) {
            totalOrders = 0;
        }

        // totalRevenue
        try {
            // Nếu OrderDAO có totalRevenue() thì dùng; nếu chưa -> 0 (có thể tự viết thêm trong DAO sau)
            try {
                Object v = OrderDAO.class.getMethod("totalRevenue").invoke(orderDAO);
                if (v instanceof Number) totalRevenue = ((Number) v).doubleValue();
            } catch (NoSuchMethodException ignore) {
                totalRevenue = 0.0;
            }
        } catch (Exception e) {
            totalRevenue = 0.0;
        }

        // ==== Đơn hàng gần đây ====
        List<Order> recentOrders;
        try {
            // Lấy 5 đơn mới nhất: page=1, pageSize=5
            recentOrders = orderDAO.search(null, null, 1, 5);
        } catch (Exception e) {
            recentOrders = Collections.emptyList();
        }

        // Set attributes cho JSP
        req.setAttribute("totalUsers", totalUsers);
        req.setAttribute("totalProducts", totalProducts);
        req.setAttribute("totalOrders", totalOrders);
        req.setAttribute("totalRevenue", totalRevenue);
        req.setAttribute("recentOrders", recentOrders);

        // Forward
        req.getRequestDispatcher("/admin/dashboard.jsp").forward(req, resp);
    }
}
