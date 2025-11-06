package controller;

import dao.OrderDAO;
import entity.Order;
import entity.OrderDetail;
import entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.Collections;
import java.util.List;

@WebServlet(name = "UserOrdersController", urlPatterns = {"/orders"})
public class UserOrdersController extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/Login.jsp?redirect=orders");
            return;
        }

        List<Order> orders = orderDAO.findByUser(user.getUserId());
        req.setAttribute("orders", orders);

        String orderIdParam = req.getParameter("orderId");
        if (orderIdParam != null) {
            try {
                int orderId = Integer.parseInt(orderIdParam);
                Order selected = orderDAO.findByIdAndUser(orderId, user.getUserId());
                if (selected != null) {
                    List<OrderDetail> details = orderDAO.findDetailsByOrderId(orderId);
                    req.setAttribute("selectedOrder", selected);
                    req.setAttribute("orderDetails", details);
                }
            } catch (NumberFormatException ignore) {
                req.setAttribute("orderDetails", Collections.emptyList());
            }
        }

        req.getRequestDispatcher("/orders.jsp").forward(req, resp);
    }
}
