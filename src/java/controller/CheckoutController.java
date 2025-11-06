package controller;

import dao.CartDAO;
import dao.OrderDAO;
import entity.Cart;
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
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CheckoutController", urlPatterns = {"/checkout"})
public class CheckoutController extends HttpServlet {

    private final CartDAO cartDAO = new CartDAO();
    private final OrderDAO orderDAO = new OrderDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/Login.jsp");
            return;
        }

        List<Cart> items = cartDAO.findByUser(user.getUserId());
        double total = 0;
        int quantity = 0;
        for (Cart item : items) {
            total += item.getSubtotal();
            quantity += item.getQuantity();
        }

        request.setAttribute("cartItems", items);
        request.setAttribute("total", total);
        request.setAttribute("totalQuantity", quantity);
        request.getRequestDispatcher("/Checkout.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            response.sendRedirect(request.getContextPath() + "/auth/Login.jsp");
            return;
        }

        List<Cart> items = cartDAO.findByUser(user.getUserId());
        if (items.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        double total = 0;
        int quantity = 0;
        List<OrderDetail> details = new ArrayList<>();
        for (Cart item : items) {
            total += item.getSubtotal();
            quantity += item.getQuantity();

            OrderDetail detail = new OrderDetail();
            detail.setProductId(item.getProductId());
            detail.setQuantity(item.getQuantity());
            detail.setPrice(item.getPrice());
            detail.setSubtotal(item.getSubtotal());
            details.add(detail);
        }

        Order order = new Order();
        order.setUserId(user.getUserId());
        order.setQuantity(quantity);
        order.setTotal(total);
        order.setStatus("pending");
        order.setOrderDate(new Timestamp(System.currentTimeMillis()));

        boolean ok = orderDAO.insert(order, details);
        if (ok) {
            cartDAO.clearByUser(user.getUserId());
            if (session != null) {
                session.setAttribute("cartCount", 0);
            }
            response.sendRedirect(request.getContextPath() + "/checkout?success=1");
        } else {
            response.sendRedirect(request.getContextPath() + "/checkout?error=1");
        }
    }
}
