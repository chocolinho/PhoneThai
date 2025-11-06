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
import java.util.Optional;
import java.util.stream.Collectors;

@WebServlet(name = "ProductsController", urlPatterns = {"/products"})
public class ProductsController extends HttpServlet {

    private final ProductDAOS productDAO = new ProductDAOS();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html;charset=UTF-8");

        String category = Optional.ofNullable(request.getParameter("cat")).orElse("").trim();
        String brand = Optional.ofNullable(request.getParameter("brand")).orElse("").trim();
        String keyword = Optional.ofNullable(request.getParameter("q")).orElse("").trim();

        List<Product> products;
        if (!brand.isBlank()) {
            products = productDAO.getProductsByBrandAndCategory(brand, category, 0, 200);
            if (!keyword.isBlank()) {
                final String lower = keyword.toLowerCase();
                products = products.stream()
                        .filter(p -> p.getName() != null && p.getName().toLowerCase().contains(lower))
                        .collect(Collectors.toList());
            }
        } else if (!category.isBlank()) {
            products = productDAO.getProductsByCategoryAndKeyword(category, keyword);
        } else if (!keyword.isBlank()) {
            products = productDAO.search(keyword, null);
        } else {
            products = productDAO.getAllProducts();
        }

        StringBuilder title = new StringBuilder("Tất cả sản phẩm");
        if (!category.isBlank()) {
            title = new StringBuilder("Danh mục: ").append(category);
        }
        if (!brand.isBlank()) {
            if (title.length() > 0) title.append(" · ");
            title.append("Thương hiệu: ").append(brand);
        }
        if (!keyword.isBlank()) {
            if (title.length() > 0) title.append(" · ");
            title.append("Từ khoá: \"").append(keyword).append("\"");
        }

        request.setAttribute("pageTitle", title.toString());
        request.setAttribute("products", products);
        request.setAttribute("categories", productDAO.getAllCategories());
        request.setAttribute("brands", productDAO.getAllBrands());
        request.setAttribute("selectedCategory", category);
        request.setAttribute("selectedBrand", brand);
        request.setAttribute("query", keyword);

        request.getRequestDispatcher("/Products.jsp").forward(request, response);
    }
}
