package dao;

import context.DBContext;
import entity.Order;
import entity.OrderDetail;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class OrderDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(OrderDAO.class.getName());

    /* ===================== MAP ===================== */
    private Order mapOrder(ResultSet rs) throws SQLException {
        Order o = new Order();
        o.setOrderId(rs.getInt("order_id"));
        o.setUserId(rs.getInt("user_id"));
        o.setQuantity(rs.getInt("quantity"));
        o.setTotal(rs.getBigDecimal("total").doubleValue()); 
        Timestamp ts = rs.getTimestamp("order_date");
        if (ts != null) o.setOrderDate(ts);
        o.setStatus(rs.getString("status"));
        // view fields từ JOIN users
        try {
            o.setUserFullName(rs.getString("full_name"));
            o.setUserEmail(rs.getString("email"));
        } catch (SQLException ignore) {}
        return o;
    }

    private OrderDetail mapDetail(ResultSet rs) throws SQLException {
        OrderDetail d = new OrderDetail();
        d.setDetailId(rs.getInt("detail_id"));
        d.setOrderId(rs.getInt("order_id"));
        d.setProductId(rs.getInt("product_id"));
        d.setQuantity(rs.getInt("quantity"));
        d.setPrice(rs.getBigDecimal("price").doubleValue());
        d.setSubtotal(rs.getBigDecimal("subtotal").doubleValue());
        try { d.setProductName(rs.getString("name")); } catch (SQLException ignore) {}
        return d;
    }

    /* ===================== LIST / SEARCH (có phân trang) ===================== */
    public List<Order> search(String q, String status, int page, int pageSize) {
        List<Order> list = new ArrayList<>();

        String base = """
            SELECT o.order_id, o.user_id, o.quantity, o.total, o.order_date, o.status,
                   u.full_name, u.email
              FROM orders o
              JOIN users u ON u.user_id = o.user_id
             WHERE 1=1
            """;
        StringBuilder sql = new StringBuilder(base);
        List<Object> params = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            sql.append(" AND (o.order_id LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
            String like = "%" + q.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND o.status = ? ");
            params.add(status);
        }
        sql.append(" ORDER BY o.order_date DESC, o.order_id DESC LIMIT ? OFFSET ? ");
        params.add(pageSize);
        params.add((page - 1) * pageSize);

        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int i = 1;
            for (Object p : params) ps.setObject(i++, p);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapOrder(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "search orders failed", ex);
        }
        return list;
    }

    public int count(String q, String status) {
        String base = """
            SELECT COUNT(*)
              FROM orders o
              JOIN users u ON u.user_id = o.user_id
             WHERE 1=1
            """;
        StringBuilder sql = new StringBuilder(base);
        List<Object> params = new ArrayList<>();
        if (q != null && !q.isBlank()) {
            sql.append(" AND (o.order_id LIKE ? OR u.full_name LIKE ? OR u.email LIKE ?) ");
            String like = "%" + q.trim() + "%";
            params.add(like); params.add(like); params.add(like);
        }
        if (status != null && !status.isBlank()) {
            sql.append(" AND o.status = ? ");
            params.add(status);
        }
        try (PreparedStatement ps = connection.prepareStatement(sql.toString())) {
            int i = 1;
            for (Object p : params) ps.setObject(i++, p);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return rs.getInt(1);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "count orders failed", ex);
        }
        return 0;
    }

    /* ===================== READ ONE ===================== */
    public Order findById(int id) {
        String sql = """
            SELECT o.order_id, o.user_id, o.quantity, o.total, o.order_date, o.status,
                   u.full_name, u.email
              FROM orders o
              JOIN users u ON u.user_id = o.user_id
             WHERE o.order_id = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOrder(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "findById failed", ex);
        }
        return null;
    }

    public List<OrderDetail> findDetailsByOrderId(int orderId) {
        List<OrderDetail> list = new ArrayList<>();
        String sql = """
            SELECT d.detail_id, d.order_id, d.product_id, d.quantity, d.price, d.subtotal, p.name
              FROM orderdetails d
              JOIN products p ON p.product_id = d.product_id
             WHERE d.order_id = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapDetail(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "findDetailsByOrderId failed", ex);
        }
        return list;
    }

    public List<Order> findByUser(int userId) {
        List<Order> list = new ArrayList<>();
        String sql = """
            SELECT order_id, user_id, quantity, total, order_date, status
              FROM orders
             WHERE user_id = ?
             ORDER BY order_date DESC, order_id DESC
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(mapOrder(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "findByUser failed", ex);
        }
        return list;
    }

    public Order findByIdAndUser(int orderId, int userId) {
        String sql = """
            SELECT order_id, user_id, quantity, total, order_date, status
              FROM orders
             WHERE order_id = ? AND user_id = ?
            """;
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, orderId);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return mapOrder(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "findByIdAndUser failed", ex);
        }
        return null;
    }

    /* ===================== CREATE / UPDATE / DELETE ===================== */
    public boolean insert(Order o, List<OrderDetail> details) {
        String sqlOrder  = "INSERT INTO orders(user_id, quantity, total, order_date, status) VALUES(?,?,?,?,?)";
        String sqlDetail = "INSERT INTO orderdetails(order_id, product_id, quantity, price, subtotal) VALUES(?,?,?,?,?)";

        boolean oldAuto = true;
        try {
            oldAuto = connection.getAutoCommit();
            connection.setAutoCommit(false);

            int newId;
            try (PreparedStatement ps = connection.prepareStatement(sqlOrder, Statement.RETURN_GENERATED_KEYS)) {
                ps.setInt(1, o.getUserId());
                ps.setInt(2, o.getQuantity());
                ps.setBigDecimal(3, java.math.BigDecimal.valueOf(o.getTotal()));
               ps.setTimestamp(4, o.getOrderDate() == null
        ? new Timestamp(System.currentTimeMillis())
        : new Timestamp(o.getOrderDate().getTime()));

                ps.setString(5, o.getStatus());
                ps.executeUpdate();
                try (ResultSet k = ps.getGeneratedKeys()) { k.next(); newId = k.getInt(1); }
            }
            try (PreparedStatement pd = connection.prepareStatement(sqlDetail)) {
                for (OrderDetail d : details) {
                    pd.setInt(1, newId);
                    pd.setInt(2, d.getProductId());
                    pd.setInt(3, d.getQuantity());
                    pd.setBigDecimal(4, java.math.BigDecimal.valueOf(d.getPrice()));
                    pd.setBigDecimal(5, java.math.BigDecimal.valueOf(d.getSubtotal()));
                    pd.addBatch();
                }
                pd.executeBatch();
            }

            connection.commit();
            return true;
        } catch (SQLException ex) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            LOGGER.log(Level.SEVERE, "insert order failed", ex);
            return false;
        } finally {
            try { connection.setAutoCommit(oldAuto); } catch (SQLException ignored) {}
        }
    }

    public boolean update(Order o, List<OrderDetail> details) {
        String sqlOrder  = "UPDATE orders SET user_id=?, quantity=?, total=?, order_date=?, status=? WHERE order_id=?";
        String delDetail = "DELETE FROM orderdetails WHERE order_id=?";
        String insDetail = "INSERT INTO orderdetails(order_id, product_id, quantity, price, subtotal) VALUES(?,?,?,?,?)";

        boolean oldAuto = true;
        try {
            oldAuto = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement ps = connection.prepareStatement(sqlOrder)) {
                ps.setInt(1, o.getUserId());
                ps.setInt(2, o.getQuantity());
                ps.setBigDecimal(3, java.math.BigDecimal.valueOf(o.getTotal()));
                ps.setTimestamp(4, o.getOrderDate() == null
        ? new Timestamp(System.currentTimeMillis())
        : new Timestamp(o.getOrderDate().getTime()));

                ps.setString(5, o.getStatus());
                ps.setInt(6, o.getOrderId());
                ps.executeUpdate();
            }
            try (PreparedStatement d1 = connection.prepareStatement(delDetail)) {
                d1.setInt(1, o.getOrderId());
                d1.executeUpdate();
            }
            try (PreparedStatement d2 = connection.prepareStatement(insDetail)) {
                for (OrderDetail d : details) {
                    d2.setInt(1, o.getOrderId());
                    d2.setInt(2, d.getProductId());
                    d2.setInt(3, d.getQuantity());
                    d2.setBigDecimal(4, java.math.BigDecimal.valueOf(d.getPrice()));
                    d2.setBigDecimal(5, java.math.BigDecimal.valueOf(d.getSubtotal()));
                    d2.addBatch();
                }
                d2.executeBatch();
            }

            connection.commit();
            return true;
        } catch (SQLException ex) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            LOGGER.log(Level.SEVERE, "update order failed", ex);
            return false;
        } finally {
            try { connection.setAutoCommit(oldAuto); } catch (SQLException ignored) {}
        }
    }

    public boolean delete(int orderId) {
        String delDetail = "DELETE FROM orderdetails WHERE order_id=?";
        String delOrder  = "DELETE FROM orders WHERE order_id=?";
        boolean oldAuto = true;
        try {
            oldAuto = connection.getAutoCommit();
            connection.setAutoCommit(false);

            try (PreparedStatement d1 = connection.prepareStatement(delDetail)) {
                d1.setInt(1, orderId);
                d1.executeUpdate();
            }
            try (PreparedStatement d2 = connection.prepareStatement(delOrder)) {
                d2.setInt(1, orderId);
                d2.executeUpdate();
            }

            connection.commit();
            return true;
        } catch (SQLException ex) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            LOGGER.log(Level.SEVERE, "delete order failed", ex);
            return false;
        } finally {
            try { connection.setAutoCommit(oldAuto); } catch (SQLException ignored) {}
        }
    }
    
    public double totalRevenue() {
    String sql = "SELECT COALESCE(SUM(total), 0) AS totalRevenue FROM orders WHERE status = 'completed'";
    try (PreparedStatement ps = connection.prepareStatement(sql);
         ResultSet rs = ps.executeQuery()) {
        if (rs.next()) {
            return rs.getDouble("totalRevenue");
        }
    } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, "totalRevenue failed", ex);
    }
    return 0.0;
}

}
