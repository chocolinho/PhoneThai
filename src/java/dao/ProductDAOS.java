package dao;

import context.DBContext;
import entity.Product;

import java.sql.*;
import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

public class ProductDAOS extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(ProductDAOS.class.getName());

    /* ===================== MAP ===================== */
    private Product map(ResultSet rs) throws SQLException {
        Product p = new Product();
        p.setProductId(rs.getInt("product_id"));
        p.setName(rs.getString("name"));
        p.setDescription(rs.getString("description"));
        p.setPrice(rs.getDouble("price"));

        // nullable
        Object oldObj = rs.getObject("old_price");
        if (oldObj != null) p.setOldPrice(rs.getDouble("old_price"));

        Object ratingObj = rs.getObject("rating");
        if (ratingObj != null) p.setRating(rs.getDouble("rating"));

        p.setBrand(rs.getString("brand"));
        p.setStock(rs.getInt("stock"));
        p.setCategory(rs.getString("category"));
        p.setImage(rs.getString("image"));

        try {
            p.setCreatedAt(rs.getTimestamp("created_at"));
        } catch (SQLException ignore) {
            // một số SELECT có thể không lấy cột này
        }

        // tính % giảm
        if (p.getOldPrice() != null && p.getOldPrice() > p.getPrice()) {
            int percent = (int) Math.round((p.getOldPrice() - p.getPrice()) * 100.0 / p.getOldPrice());
            p.setDiscountPercent(percent);
        } else {
            p.setDiscountPercent(0);
        }
        return p;
    }

    /* ===================== READ ALL ===================== */
    public List<Product> getAllProducts() {
        List<Product> list = new ArrayList<>();
        String sql = """
            SELECT product_id, name, description, price, old_price, rating, brand,
                   stock, category, image, created_at
            FROM products
            ORDER BY created_at DESC, product_id DESC
        """;
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "getAllProducts failed", ex);
        }
        return list;
    }

    /* alias để dùng ở controller khác */
    public List<Product> getAll() { return getAllProducts(); }

    /* ===================== READ BY ID ===================== */
    public Product getProductByID(int id) {
        String sql = "SELECT * FROM products WHERE product_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "getProductByID failed", ex);
        }
        return null;
    }

    public Product getProductByID(String id) {
        try { return getProductByID(Integer.parseInt(id)); }
        catch (NumberFormatException e) { return null; }
    }

    /* alias */
    public Product getById(int id) { return getProductByID(id); }

    /* ===================== CREATE ===================== */
    public boolean insertProduct(Product p) {
        String sql = """
            INSERT INTO products
                (name, description, price, old_price, rating, brand, stock, category, image, created_at)
            VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, NOW())
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, p.getName());
            st.setString(2, p.getDescription());
            st.setDouble(3, p.getPrice());

            if (p.getOldPrice() != null) st.setDouble(4, p.getOldPrice()); else st.setNull(4, Types.DOUBLE);
            if (p.getRating()   != null) st.setDouble(5, p.getRating());   else st.setNull(5, Types.DOUBLE);

            st.setString(6, p.getBrand());
            st.setInt(7, p.getStock());
            st.setString(8, p.getCategory());
            st.setString(9, p.getImage());
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "insertProduct failed", ex);
            return false;
        }
    }
    
    // ProductDAOS.java
public List<Product> search(String nameKeyword, Integer id) {
    List<Product> list = new ArrayList<>();
    StringBuilder sql = new StringBuilder("""
        SELECT product_id, name, description, price, old_price, rating, brand,
               stock, category, image, created_at
        FROM products
        WHERE 1=1
    """);
    List<Object> params = new ArrayList<>();

    if (id != null) {
        sql.append(" AND product_id = ? ");
        params.add(id);
    }
    if (nameKeyword != null && !nameKeyword.isBlank()) {
        sql.append(" AND name LIKE ? ");
        params.add("%" + nameKeyword.trim() + "%");
    }

    sql.append(" ORDER BY created_at DESC, product_id DESC ");

    try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
        int idx = 1;
        for (Object v : params) {
            if (v instanceof Integer) st.setInt(idx++, (Integer) v);
            else st.setString(idx++, String.valueOf(v));
        }
        try (ResultSet rs = st.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        }
    } catch (SQLException ex) {
        LOGGER.log(Level.SEVERE, "search failed", ex);
    }
    return list;
}


    /* alias */
    public boolean insert(Product p) { return insertProduct(p); }

    /* ===================== UPDATE ===================== */
    public boolean updateProduct(Product p) {
        String sql = """
            UPDATE products
               SET name=?, description=?, price=?, old_price=?, rating=?,
                   brand=?, stock=?, category=?, image=?
             WHERE product_id=?
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, p.getName());
            st.setString(2, p.getDescription());
            st.setDouble(3, p.getPrice());
            if (p.getOldPrice() != null) st.setDouble(4, p.getOldPrice()); else st.setNull(4, Types.DOUBLE);
            if (p.getRating()   != null) st.setDouble(5, p.getRating());   else st.setNull(5, Types.DOUBLE);
            st.setString(6, p.getBrand());
            st.setInt(7, p.getStock());
            st.setString(8, p.getCategory());
            st.setString(9, p.getImage());
            st.setInt(10, p.getProductId());
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "updateProduct failed", ex);
            return false;
        }
    }

    /* alias */
    public boolean update(Product p) { return updateProduct(p); }

    /* ===================== DELETE ===================== */
    public boolean deleteProduct(int id) {
        String sql = "DELETE FROM products WHERE product_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            return st.executeUpdate() > 0;
        } catch (SQLException ex) {
            // Nếu dính khoá ngoại từ orderdetails → products sẽ ném lỗi tại đây
            LOGGER.log(Level.SEVERE, "deleteProduct failed (FK constraint?)", ex);
            return false;
        }
    }

    /* alias */
    public boolean delete(int id) { return deleteProduct(id); }

    /* ===================== FILTER ===================== */
    public List<Product> getProductsByBrandAndCategory(String brand, String category, int offset, int limit) {
        List<Product> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM products WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (brand != null && !brand.isBlank()) {
            sql.append("AND brand = ? ");
            params.add(brand);
        }
        if (category != null && !category.isBlank()) {
            sql.append("AND category = ? ");
            params.add(category);
        }
        sql.append("ORDER BY created_at DESC, product_id DESC LIMIT ? OFFSET ?");
        params.add(limit);
        params.add(offset);

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object v : params) {
                if (v instanceof Integer) st.setInt(idx++, (Integer) v);
                else st.setString(idx++, String.valueOf(v));
            }
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "getProductsByBrandAndCategory failed", ex);
        }
        return list;
    }

    /* ===================== BRANDS ===================== */
    public List<String> getAllBrands() {
        List<String> brands = new ArrayList<>();
        String sql = "SELECT DISTINCT brand FROM products WHERE brand IS NOT NULL AND brand <> '' ORDER BY brand";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) brands.add(rs.getString("brand"));
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "getAllBrands failed", ex);
        }
        return brands;
    }

    /* ===================== TEST ===================== */
    public static void main(String[] args) {
        ProductDAOS dao = new ProductDAOS();
        for (Product p : dao.getAll()) {
            System.out.println(p.getProductId() + " | " + p.getName() + " | " + p.getBrand());
        }
    }
}
 