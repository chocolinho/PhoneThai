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

        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user");

        // bắt buộc đăng nhập
        if (user == null) {
            response.getWriter().write("{\"error\":\"Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng!\"}");
            return;
        }

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

        // ✅ tổng số lượng (không phải số mặt hàng)
        int totalQty = cartDAO.countQuantityByUser(user.getUserId());
        session.setAttribute("cartCount", totalQty);

        // trả về tổng quantity để JS cập nhật badge
        response.getWriter().write("{\"count\":" + totalQty + "}");
    }

    // (doGet để hiển thị trang giỏ hàng nên đặt ở servlet khác /cart/view
    // hoặc gộp vào servlet này nếu bạn không còn servlet /cart nào khác.)
}



