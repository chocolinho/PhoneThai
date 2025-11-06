package controller;

import dao.CartDAO;
import dao.ProductDAOS;
import entity.Cart;
import entity.Product;
import entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "CartController", urlPatterns = {"/cart"})
public class CartController extends HttpServlet {

    private final ProductDAOS pdao = new ProductDAOS();
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
            for (Cart c : items) {
                total += c.getSubtotal();
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
            String wantsJson = request.getHeader("Accept") != null && request.getHeader("Accept").contains("application/json");
            if ("XMLHttpRequest".equalsIgnoreCase(request.getHeader("X-Requested-With")) || wantsJson) {
                response.setContentType("application/json;charset=UTF-8");
                response.getWriter().write("{\"error\":\"Bạn cần đăng nhập để thực hiện thao tác này!\"}");
            } else {
                response.sendRedirect(request.getContextPath() + "/auth/Login.jsp?redirect=cart");
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
            default -> {
                response.sendRedirect(request.getContextPath() + "/cart");
            }
        }
    }

    private void handleAddToCart(HttpServletRequest request, HttpServletResponse response,
                                 HttpSession session, User user) throws IOException {

        response.setContentType("application/json;charset=UTF-8");

        int pid;
        try {
            pid = Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            response.getWriter().write("{\"error\":\"Mã sản phẩm không hợp lệ!\"}");
            return;
        }

        int quantity = 1;
        String quantityParam = request.getParameter("quantity");
        if (quantityParam != null) {
            try {
                quantity = Integer.parseInt(quantityParam);
                if (quantity <= 0) quantity = 1;
            } catch (NumberFormatException ignore) {
                quantity = 1;
            }
        }

        Product p = pdao.getProductByID(pid);
        if (p == null) {
            response.getWriter().write("{\"error\":\"Không tìm thấy sản phẩm!\"}");
            return;
        }

        cartDAO.addOrIncrement(user.getUserId(), p, quantity);
        int totalQty = cartDAO.countQuantityByUser(user.getUserId());
        session.setAttribute("cartCount", totalQty);
        response.getWriter().write("{\"count\":" + totalQty + "}");
    }

    private void handleUpdateQuantity(HttpServletRequest request, HttpServletResponse response,
                                      HttpSession session, User user) throws IOException {

        int pid = parseProductId(request);
        if (pid == -1) {
            response.sendRedirect(request.getContextPath() + "/cart");
            return;
        }

        int quantity = 1;
        try {
            quantity = Integer.parseInt(request.getParameter("quantity"));
        } catch (NumberFormatException ignore) {
        }

        cartDAO.setQuantity(user.getUserId(), pid, quantity);
        refreshCartCount(session, user);
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private void handleRemove(HttpServletRequest request, HttpServletResponse response,
                               HttpSession session, User user) throws IOException {

        int pid = parseProductId(request);
        if (pid != -1) {
            cartDAO.removeItem(user.getUserId(), pid);
            refreshCartCount(session, user);
        }
        response.sendRedirect(request.getContextPath() + "/cart");
    }

    private int parseProductId(HttpServletRequest request) {
        try {
            return Integer.parseInt(request.getParameter("id"));
        } catch (NumberFormatException e) {
            return -1;
        }
    }

    private void refreshCartCount(HttpSession session, User user) {
        if (session == null) return;
        int totalQty = cartDAO.countQuantityByUser(user.getUserId());
        session.setAttribute("cartCount", totalQty);
    }

    // (doGet để hiển thị trang giỏ hàng nên đặt ở servlet khác /cart/view
    // hoặc gộp vào servlet này nếu bạn không còn servlet /cart nào khác.)
}



