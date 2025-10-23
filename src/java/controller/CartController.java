package controller;

import entity.CartItem;
import entity.Product;
import dao.ProductDAOS;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;
import java.util.*;

@WebServlet(name = "CartController", urlPatterns = {"/cart"})
public class CartController extends HttpServlet {

    private final ProductDAOS pdao = new ProductDAOS();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) {
            cart = new HashMap<>();
        }

        // ✅ chuyển CartItem → Product để JSP dùng được
        List<Product> products = new ArrayList<>();
        double total = 0;

        for (CartItem item : cart.values()) {
            Product p = item.getProduct();
            p.setStock(item.getQuantity()); // tạm dùng field stock làm quantity
            products.add(p);
            total += p.getPrice() * item.getQuantity();
        }

        request.setAttribute("cartItems", products);
        request.setAttribute("total", total);
        request.getRequestDispatcher("/Cart.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("application/json;charset=UTF-8");
        HttpSession session = request.getSession();

        // bắt buộc đăng nhập
        if (session.getAttribute("user") == null) {
            response.getWriter().write("{\"error\":\"Bạn cần đăng nhập để thêm sản phẩm vào giỏ hàng!\"}");
            return;
        }

        int pid = Integer.parseInt(request.getParameter("id"));
        ProductDAOS pdao = new ProductDAOS();
        Product p = pdao.getProductByID(pid);
        if (p == null) {
            response.getWriter().write("{\"error\":\"Không tìm thấy sản phẩm!\"}");
            return;
        }

        Map<Integer, CartItem> cart = (Map<Integer, CartItem>) session.getAttribute("cart");
        if (cart == null) cart = new HashMap<>();

        CartItem item = cart.get(pid);
        if (item == null) {
            item = new CartItem(p, 1);
        } else {
            item.setQuantity(item.getQuantity() + 1);  // tăng số lượng nếu đã có
        }
        cart.put(pid, item);

        // ✅ tổng số lượng (không phải số mặt hàng)
        int totalQty = cart.values().stream().mapToInt(CartItem::getQuantity).sum();
        session.setAttribute("cart", cart);
        session.setAttribute("cartCount", totalQty);

        // trả về tổng quantity để JS cập nhật badge
        response.getWriter().write("{\"count\":" + totalQty + "}");
    }

    // (doGet để hiển thị trang giỏ hàng nên đặt ở servlet khác /cart/view
    // hoặc gộp vào servlet này nếu bạn không còn servlet /cart nào khác.)
}



