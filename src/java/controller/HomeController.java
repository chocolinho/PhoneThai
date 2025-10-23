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

@WebServlet(name = "HomeController", urlPatterns = {"/home", ""})
public class HomeController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        response.setContentType("text/html;charset=UTF-8");

        // Không try-catch nữa để Tomcat hiện lỗi thật
        ProductDAOS dao = new ProductDAOS();

        // Gọi DAO lấy danh sách sản phẩm
        List<Product> list = dao.getAllProducts();

        // Gửi sang JSP
        request.setAttribute("products", list);

        // Forward tới trang JSP (đường dẫn tuyệt đối)
        request.getRequestDispatcher("/Home.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
