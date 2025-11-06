package controller;

import dao.ProductDAOS;
import entity.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.List;

@WebServlet(name = "SearchController", urlPatterns = {"/search"})
public class SearchController extends HttpServlet {

    private final ProductDAOS productDAO = new ProductDAOS();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html;charset=UTF-8");

        String q = request.getParameter("q");
        if (q != null) q = q.trim();
        List<Product> products = productDAO.search(q, null);

        request.setAttribute("pageTitle", (q == null || q.isBlank())
                ? "Tất cả sản phẩm"
                : "Kết quả tìm kiếm cho \"" + q + "\"");
        request.setAttribute("query", q);
        request.setAttribute("products", products);
        request.setAttribute("categories", productDAO.getAllCategories());
        request.setAttribute("brands", productDAO.getAllBrands());
        request.setAttribute("selectedCategory", "");
        request.setAttribute("selectedBrand", "");

        request.getRequestDispatcher("/Products.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
