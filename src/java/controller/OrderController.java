package controller;

import dao.OrderDAO;
import dao.ProductDAOS;
import dao.UserDAO;
import entity.Order;
import entity.OrderDetail;
import entity.Product;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

@WebServlet(name = "OrderController", urlPatterns = {"/admin/orders"})
public class OrderController extends HttpServlet {

    private final OrderDAO orderDAO = new OrderDAO();
    private final ProductDAOS productDAO = new ProductDAOS();
    private final UserDAO userDAO = new UserDAO();

    private static final List<String> STATUS = Arrays.asList(
            "pending","processing","shipped","completed","cancelled"
    );

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

        String action = Optional.ofNullable(request.getParameter("action")).orElse("list");

        switch (action) {
            case "new": {
                request.setAttribute("statusOptions", STATUS);
                request.setAttribute("products", productDAO.getAllProducts());
                request.setAttribute("users", userDAO.listAll()); 
                request.getRequestDispatcher("/admin/order-form.jsp").forward(request, response);
                break;
            }
            case "edit": {
                Integer id = parseIntOrNull(request.getParameter("id"));
                if (id == null) { response.sendRedirect(request.getContextPath()+"/admin/orders"); return; }
                Order order = orderDAO.findById(id);
                if (order == null) { response.sendRedirect(request.getContextPath()+"/admin/orders"); return; }
                List<OrderDetail> details = orderDAO.findDetailsByOrderId(id);

                // convert timestamp -> yyyy-MM-ddTHH:mm cho <input type="datetime-local">
                if (order.getOrderDate() != null) {
                    String local = order.getOrderDate().toString().replace(' ', 'T').substring(0,16);
                    order.setOrderDateLocal(local);
                }

                request.setAttribute("statusOptions", STATUS);
                request.setAttribute("order", order);
                request.setAttribute("orderDetails", details);
                request.setAttribute("products", productDAO.getAllProducts());
                request.setAttribute("users", userDAO.listAll()); 
                request.getRequestDispatcher("/admin/order-form.jsp").forward(request, response);
                break;
            }
            default: {
                String q = request.getParameter("q");
                String st = request.getParameter("status");
                Integer page = parseIntOrNull(request.getParameter("page"));
                int p = (page == null || page < 1) ? 1 : page;
                int pageSize = 10;

                List<Order> list = orderDAO.search(q, st, p, pageSize);
                int total = orderDAO.count(q, st);
                int pageCount = (int) Math.ceil(total * 1.0 / pageSize);

                request.setAttribute("statusOptions", STATUS);
                request.setAttribute("list", list);
                request.setAttribute("pageCount", pageCount);
                request.setAttribute("currentPage", p);
                request.getRequestDispatcher("/admin/order-list.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding(StandardCharsets.UTF_8.name());

        String action = Optional.ofNullable(request.getParameter("action")).orElse("list");

        switch (action) {
            case "delete": {
                Integer id = parseIntOrNull(request.getParameter("id"));
                if (id != null) orderDAO.delete(id);
                response.sendRedirect(request.getContextPath()+"/admin/orders");
                break;
            }
            case "create":
            case "update": {
                Integer orderId = parseIntOrNull(request.getParameter("order_id"));
                Integer userId  = parseIntOrNull(request.getParameter("user_id"));
                String status   = Optional.ofNullable(request.getParameter("status")).orElse("pending");
                String dateStr  = request.getParameter("order_date"); // yyyy-MM-ddTHH:mm

                String[] productIds = request.getParameterValues("product_id");
                String[] prices     = request.getParameterValues("price");
                String[] quantities = request.getParameterValues("quantity");

                List<OrderDetail> details = new ArrayList<>();
                int qtySum = 0; double total = 0;

                if (productIds != null) {
                    for (int i=0; i<productIds.length; i++) {
                        Integer pid = parseIntOrNull(productIds[i]);
                        Double  price = parseDoubleOrNull(prices[i]);
                        Integer qty = parseIntOrNull(quantities[i]);
                        if (pid == null || price == null || qty == null || qty <= 0) continue;
                        double sub = price * qty;

                        OrderDetail d = new OrderDetail();
                        d.setProductId(pid);
                        d.setPrice(price);
                        d.setQuantity(qty);
                        d.setSubtotal(sub);
                        details.add(d);

                        qtySum += qty;
                        total  += sub;
                    }
                }

                // map
                Order o = new Order();
                if (orderId != null) o.setOrderId(orderId);
                if (userId  != null) o.setUserId(userId);
                o.setQuantity(qtySum);
                o.setTotal(total);
                o.setStatus(status);
                if (dateStr != null && !dateStr.isBlank()) {
                    LocalDateTime ldt = LocalDateTime.parse(dateStr.replace('T',' '),
                            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm"));
                    o.setOrderDate(Timestamp.valueOf(ldt));
                }

                if ("update".equals(action) && orderId != null) {
                    orderDAO.update(o, details);
                } else {
                    orderDAO.insert(o, details);
                }

                response.sendRedirect(request.getContextPath()+"/admin/orders");
                break;
            }
            default:
                response.sendRedirect(request.getContextPath()+"/admin/orders");
        }
    }
}
