package controller;

import dao.CartDAO;
import dao.ProductDAOS;
import entity.Cart;
import entity.Product;
import entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CartController", urlPatterns = {"/cart"})
public class CartController extends HttpServlet {

    private final ProductDAOS productDAO = new ProductDAOS();
    private final CartDAO cartDAO = new CartDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        List<Cart> items = new ArrayList<>();
        double total = 0;
        request.setAttribute("mustLogin", false);

        if (user != null) {
            items = cartDAO.findByUser(user.getUserId());
            for (Cart item : items) {
                total += item.getSubtotal();
            }
        } else {
            request.setAttribute("mustLogin", true);
        }

        request.setAttribute("cartItems", items);
        request.setAttribute("total", total);
        request.getRequestDispatcher("/Cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");

        if (user == null) {
            if (isAjaxOrJson(request)) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Ban can dang nhap de thuc hien thao tac nay!\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/login?redirect=cart");
            }
            return;
        }

        String action = request.getParameter("action");
        if (action == null || action.isBlank() || "add".equals(action)) {
            handleAddToCart(request, response, session, user);
            return;
        }

        switch (action) {
            case "update" -> handleUpdateQuantity(request, response, session, user);
            case "remove" -> handleRemove(request, response, session, user);
            default -> response.sendRedirect(request.getContextPath() + "/cart");
        }
    }

    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User user) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        int productId = parsePositiveInt(request.getParameter("id"), -1);
        if (productId == -1) {
            response.getWriter().write("{\"error\":\"Ma san pham khong hop le!\"}");
            return;
        }

        int quantity = parsePositiveInt(request.getParameter("quantity"), 1);
        Product product = productDAO.getProductByID(productId);
        if (product == null) {
            response.getWriter().write("{\"error\":\"Khong tim thay san pham!\"}");
            return;
        }

        cartDAO.addOrIncrement(user.getUserId(), product, quantity);
        refreshCartCount(session, user);
        response.getWriter().write("{\"count\":" + session.getAttribute("cartCount") + "}");
    }

    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User user) throws IOException {

        int productId = parsePositiveInt(request.getParameter("id"), -1);
        if (productId != -1) {
            int quantity = parsePositiveInt(request.getParameter("quantity"), 1);
            cartDAO.setQuantity(user.getUserId(), productId, quantity);
            refreshCartCount(session, user);
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleRemove(HttpServletRequest request, HttpServletResponse response,
            HttpSession session, User user) throws IOException {

        int productId = parsePositiveInt(request.getParameter("id"), -1);
        if (productId != -1) {
            cartDAO.removeItem(user.getUserId(), productId);
            refreshCartCount(session, user);
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private boolean isAjaxOrJson(HttpServletRequest request) {
        String accepts = request.getHeader("Accept");
        boolean wantsJson = accepts != null && accepts.contains("application/json");
        boolean ajaxRequest = "XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With"));
        return wantsJson || ajaxRequest;
    }

    private int parsePositiveInt(String value, int defaultValue) {
        try {
            int parsed = Integer.parseInt(value);
            return parsed > 0 ? parsed : defaultValue;
        } catch (NumberFormatException ex) {
            return defaultValue;
        }
    }

    private void refreshCartCount(HttpSession session, User user) {
        if (session == null || user == null) {
            return;
        }
        int totalQuantity = cartDAO.countQuantityByUser(user.getUserId());
        session.setAttribute("cartCount", totalQuantity);
    }
}
