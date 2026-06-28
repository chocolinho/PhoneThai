package controller;

import dao.ProductDAOS;
import entity.Product;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

@WebServlet(name = "ProductDetailController", urlPatterns = {"/detail"})
public class ProductDetailController extends HttpServlet {

    private static final Logger LOGGER = Logger.getLogger(ProductDetailController.class.getName());

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        response.setContentType("text/html;charset=UTF-8");

        try {
            // 1. Lấy ID sản phẩm từ URL
            String id = request.getParameter("pid");

            // 2. Khởi tạo DAO và lấy thông tin sản phẩm chính
            ProductDAOS dao = new ProductDAOS();
            Product product = dao.getProductByID(id);

            if (product == null) {
                request.setAttribute("errorMessage", "Không tìm thấy sản phẩm bạn yêu cầu.");
                request.getRequestDispatcher("ProductDetail.jsp").forward(request, response);
                return;
            }

            String category = product.getCategory();
            int productId = product.getProductId();

            List<Product> related = new ArrayList<>();
            if (category != null && !category.isBlank()) {
                for (Product p : dao.getProductsByCategory(category)) {
                    if (p.getProductId() != productId) {
                        related.add(p);
                    }
                    if (related.size() >= 8) break;
                }
            }

            request.setAttribute("productDetail", product);
            request.setAttribute("relatedProducts", related);

            request.getRequestDispatcher("ProductDetail.jsp").forward(request, response);

        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi tải chi tiết sản phẩm", e);
            request.setAttribute("errorMessage", "Đã có lỗi xảy ra khi tải dữ liệu. Vui lòng thử lại sau.");
            request.getRequestDispatcher("error.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}