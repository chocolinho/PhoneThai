package dao;

import context.DBContext;
import entity.Cart;
import entity.Product;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class CartDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(CartDAO.class.getName());

    private Cart map(ResultSet rs) throws SQLException {
        Cart c = new Cart();
        c.setId(rs.getInt("id"));
        c.setUserId(rs.getInt("user_id"));
        c.setProductId(rs.getInt("product_id"));
        c.setProductName(rs.getString("product_name"));
        c.setImage(rs.getString("image"));
        c.setQuantity(rs.getInt("soluong"));
        c.setPrice(rs.getBigDecimal("gia").doubleValue());
        return c;
    }

    public List<Cart> findByUser(int userId) {
        List<Cart> list = new ArrayList<>();
        String sql = "SELECT id, user_id, product_id, product_name, image, soluong, gia " +
                     "FROM cart WHERE user_id = ? ORDER BY id DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "findByUser failed", ex);
        }
        return list;
    }

    public void addOrIncrement(int userId, Product product, int quantity) {
        String checkSql = "SELECT id, soluong FROM cart WHERE user_id = ? AND product_id = ?";
        String insertSql = "INSERT INTO cart(user_id, product_id, product_name, image, soluong, gia) " +
                           "VALUES(?,?,?,?,?,?)";
        String updateSql = "UPDATE cart SET soluong = ? WHERE id = ?";
        try (PreparedStatement check = connection.prepareStatement(checkSql)) {
            check.setInt(1, userId);
            check.setInt(2, product.getProductId());
            try (ResultSet rs = check.executeQuery()) {
                if (rs.next()) {
                    int id = rs.getInt("id");
                    int currentQty = rs.getInt("soluong");
                    try (PreparedStatement update = connection.prepareStatement(updateSql)) {
                        update.setInt(1, currentQty + quantity);
                        update.setInt(2, id);
                        update.executeUpdate();
                    }
                } else {
                    try (PreparedStatement insert = connection.prepareStatement(insertSql, Statement.RETURN_GENERATED_KEYS)) {
                        insert.setInt(1, userId);
                        insert.setInt(2, product.getProductId());
                        insert.setString(3, product.getName());
                        insert.setString(4, product.getImage());
                        insert.setInt(5, quantity);
                        insert.setBigDecimal(6, java.math.BigDecimal.valueOf(product.getPrice()));
                        insert.executeUpdate();
                    }
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "addOrIncrement failed", ex);
        }
    }

    public int countQuantityByUser(int userId) {
        String sql = "SELECT COALESCE(SUM(soluong),0) FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1);
                }
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "countQuantityByUser failed", ex);
        }
        return 0;
    }

    public void updateQuantity(int userId, int productId, int quantity) {
        String sql = "UPDATE cart SET soluong = ? WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, quantity);
            ps.setInt(2, userId);
            ps.setInt(3, productId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "updateQuantity failed", ex);
        }
    }

    public void removeItem(int userId, int productId) {
        String sql = "DELETE FROM cart WHERE user_id = ? AND product_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.setInt(2, productId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "removeItem failed", ex);
        }
    }

    public void clearByUser(int userId) {
        String sql = "DELETE FROM cart WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "clearByUser failed", ex);
        }
    }
}
