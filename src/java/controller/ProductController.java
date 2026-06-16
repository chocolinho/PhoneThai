package controller;

import dao.ProductDAOS;
import entity.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.List;

@MultipartConfig(maxFileSize = 10 * 1024 * 1024) // 10MB
@WebServlet(name = "ProductController", urlPatterns = {"/admin/products"})
public class ProductController extends HttpServlet {

    private final ProductDAOS productDAO = new ProductDAOS();

    private Integer parseIntOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Integer.valueOf(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    private Double parseDoubleOrNull(String s) {
        try { return (s == null || s.isBlank()) ? null : Double.valueOf(s.trim()); }
        catch (NumberFormatException e) { return null; }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setContentType("text/html;charset=UTF-8");

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "edit" -> {
                Integer id = parseIntOrNull(request.getParameter("id"));
                if (id == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/products");
                    return;
                }
                Product p = productDAO.getProductByID(id);
                if (p == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/products");
                    return;
                }
                request.setAttribute("product", p);
                request.getRequestDispatcher("/admin/product-form.jsp").forward(request, response);
            }
            case "new" -> request.getRequestDispatcher("/admin/product-form.jsp").forward(request, response);
            default -> {
                String q = request.getParameter("q");
                Integer code = parseIntOrNull(request.getParameter("pid"));

                List<Product> list;
                if ((q != null && !q.isBlank()) || code != null) {
                    list = productDAO.search(q, code);
                } else {
                    list = productDAO.getAllProducts();
                }

                request.setAttribute("q", q);
                request.setAttribute("pid", code);
                request.setAttribute("list", list);
                request.getRequestDispatcher("/admin/product-list.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());
        response.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "save" -> {
                Integer id = parseIntOrNull(request.getParameter("id"));
                String name = request.getParameter("name");
                String desc = request.getParameter("description");
                Double price = parseDoubleOrNull(request.getParameter("price"));
                Integer stock = parseIntOrNull(request.getParameter("stock"));
                String category = request.getParameter("category");
                String brand = request.getParameter("brand");
                String image = request.getParameter("image"); // existing image name
                Double oldPrice = parseDoubleOrNull(request.getParameter("oldPrice"));
                Double rating = parseDoubleOrNull(request.getParameter("rating"));

                if (name == null || name.isBlank() || price == null || stock == null) {
                    request.setAttribute("error", "Vui lòng nhập đầy đủ Tên, Giá và Tồn kho hợp lệ.");
                    Product fallback = new Product();
                    if (id != null) fallback.setProductId(id);
                    fallback.setName(name);
                    fallback.setDescription(desc);
                    fallback.setPrice(price);
                    fallback.setStock(stock);
                    fallback.setCategory(category);
                    fallback.setBrand(brand);
                    fallback.setImage(image);
                    fallback.setOldPrice(oldPrice);
                    fallback.setRating(rating);
                    request.setAttribute("product", fallback);
                    request.getRequestDispatcher("/admin/product-form.jsp").forward(request, response);
                    return;
                }

                // ── Handle image upload ──────────────────────────────────────
                Part filePart = null;
                try { filePart = request.getPart("imageFile"); } catch (Exception ignore) {}

                if (filePart != null && filePart.getSize() > 0) {
                    // Keep only the filename (strip any path the browser may send)
                    String original = Paths.get(filePart.getSubmittedFileName()).getFileName().toString();
                    String safeName = original.replaceAll("[^a-zA-Z0-9._-]", "_");

                    // Save to the deployed /images folder inside Tomcat's webroot
                    String uploadPath = getServletContext().getRealPath("/images");
                    if (uploadPath == null) {
                        uploadPath = System.getProperty("java.io.tmpdir") + File.separator + "images";
                    }
                    File uploadDir = new File(uploadPath);
                    if (!uploadDir.exists()) uploadDir.mkdirs();

                    filePart.write(new File(uploadDir, safeName).getAbsolutePath());

                    image = safeName; // store only the filename in DB
                }
                // ─────────────────────────────────────────────────────────────

                Product p = new Product();
                if (id != null) p.setProductId(id);
                p.setName(name.trim());
                p.setDescription(desc);
                p.setPrice(price);
                p.setStock(stock);
                p.setCategory(category);
                p.setBrand(brand);
                p.setImage(image);
                p.setOldPrice(oldPrice);
                p.setRating(rating);

                if (id != null && id > 0) productDAO.updateProduct(p);
                else productDAO.insertProduct(p);

                response.sendRedirect(request.getContextPath() + "/admin/products");
            }

            case "delete" -> {
                Integer id = parseIntOrNull(request.getParameter("id"));
                if (id != null) {
                    boolean ok = productDAO.deleteProduct(id);
                    if (!ok) {
                        request.getSession().setAttribute("flash",
                                "Không thể xoá vì sản phẩm đang có trong đơn hàng.");
                    } else {
                        request.getSession().setAttribute("flash", "Đã xoá sản phẩm #" + id);
                    }
                }
                response.sendRedirect(request.getContextPath() + "/admin/products");
            }

            default -> response.sendRedirect(request.getContextPath() + "/admin/products");
        }
    }
}
