package controller;

import dao.ProductDAOS;
import entity.Product;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

            // 3. Xử lý trường hợp sản phẩm tồn tại
            if (product != null) {
                // Lấy category (String) và productId của sản phẩm hiện tại
                String category = product.getCategory();
                int productId = product.getProductId();

                // Gọi DAO để lấy danh sách sản phẩm liên quan
             
                
                // Đặt danh sách sản phẩm liên quan lên request
            
                
            } else {
                // Xử lý trường hợp không tìm thấy sản phẩm
                request.setAttribute("errorMessage", "Không tìm thấy sản phẩm bạn yêu cầu.");
            }

            // 4. Đặt sản phẩm chính (có thể là null) lên request
            request.setAttribute("productDetail", product);

            // 5. Chuyển sang trang JSP để hiển thị
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