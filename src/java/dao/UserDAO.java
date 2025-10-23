package dao;

import context.DBContext;
import context.MaHoa;              // dùng class mã hoá trong package context
import entity.User;

import java.sql.*;
import java.util.*;
import java.util.logging.*;

public class UserDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /* ========== Đăng nhập (verify hash) ========== */
    public User login(String username, String rawPassword) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) {
                    String stored = rs.getString("password"); // iterations:salt:hash
                    if (MaHoa.verifyPassword(rawPassword, stored)) {
                        return map(rs);
                    }
                }
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi đăng nhập", e);
        }
        return null;
    }

    /** Lấy danh sách user đủ cho dropdown */
    public List<User> listAll() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT user_id, username, full_name, email, role, created_at FROM users ORDER BY COALESCE(full_name, username)";
        try (PreparedStatement ps = connection.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                User u = new User();
                u.setUserId(rs.getInt("user_id"));
                u.setUsername(rs.getString("username"));
                try { u.setFullName(rs.getString("full_name")); } catch (SQLException ignore) {}
                try { u.setEmail(rs.getString("email")); } catch (SQLException ignore) {}
                try { u.setRole(rs.getInt("role")); } catch (SQLException ignore) {}
                try { u.setCreatedAt(rs.getTimestamp("created_at")); } catch (SQLException ignore) {}
                list.add(u);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "listAll failed", e);
        }
        return list;
    }

    /* ========== Kiểm tra tồn tại username ========== */
    public User checkUserExist(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi kiểm tra người dùng tồn tại", e);
        }
        return null;
    }

    /* ========== Lấy danh sách user ========== */
    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id DESC";
        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy danh sách người dùng", e);
        }
        return list;
    }

    /* ========== INSERT: hash password trước khi lưu ========== */
    public boolean insertUser(User u) {
        String sql = """
            INSERT INTO users (username, password, email, full_name, role, created_at)
            VALUES (?, ?, ?, ?, ?, NOW())
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, u.getUsername());
            String stored = MaHoa.createStoredPassword(u.getPassword()); // iterations:salt:hash
            st.setString(2, stored);
            st.setString(3, u.getEmail());
            st.setString(4, u.getFullName());
            st.setInt(5, u.getRole());
            int rows = st.executeUpdate();
            try {
                if (connection != null && !connection.getAutoCommit()) connection.commit();
            } catch (SQLException ignore) {}
            return rows > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[insertUser] Lỗi khi thêm user", e);
            return false;
        }
    }

    /* ========== UPDATE: KHÔNG đổi mật khẩu ========== */
    public boolean updateUserWithoutPassword(User u) {
        String sql = """
            UPDATE users
               SET username=?, email=?, full_name=?, role=?
             WHERE user_id=?
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, u.getUsername());
            st.setString(2, u.getEmail());
            st.setString(3, u.getFullName());
            st.setInt(4, u.getRole());
            st.setInt(5, u.getUserId());
            int rows = st.executeUpdate();
            try {
                if (connection != null && !connection.getAutoCommit()) connection.commit();
            } catch (SQLException ignore) {}
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[updateUserWithoutPassword] Lỗi khi cập nhật user (no-pw)", e);
            return false;
        }
    }

    /* ========== SEARCH: theo id / full_name / username (LIKE) ========== */
    public List<User> search(Integer id, String nameKeyword, String usernameKeyword) {
        List<User> list = new ArrayList<>();
        StringBuilder sql = new StringBuilder("SELECT * FROM users WHERE 1=1 ");
        List<Object> params = new ArrayList<>();

        if (id != null) { sql.append(" AND user_id = ? "); params.add(id); }
        if (nameKeyword != null && !nameKeyword.isBlank()) {
            sql.append(" AND full_name LIKE ? "); params.add("%" + nameKeyword.trim() + "%");
        }
        if (usernameKeyword != null && !usernameKeyword.isBlank()) {
            sql.append(" AND username LIKE ? "); params.add("%" + usernameKeyword.trim() + "%");
        }
        sql.append(" ORDER BY user_id DESC");

        try (PreparedStatement st = connection.prepareStatement(sql.toString())) {
            int idx = 1;
            for (Object p : params) {
                if (p instanceof Integer) st.setInt(idx++, (Integer) p);
                else st.setString(idx++, String.valueOf(p));
            }
            try (ResultSet rs = st.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[search] Lỗi khi tìm kiếm user", e);
        }
        return list;
    }

    /* ========== RESET PASSWORD (random đã mã hoá) ========== */
    public boolean resetPassword(int userId, String rawTempPassword) {
        String sql = "UPDATE users SET password=? WHERE user_id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            String stored = MaHoa.createStoredPassword(rawTempPassword);
            st.setString(1, stored);
            st.setInt(2, userId);
            int rows = st.executeUpdate();
            try {
                if (connection != null && !connection.getAutoCommit()) connection.commit();
            } catch (SQLException ignore) {}
            return rows > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[resetPassword] Lỗi khi reset mật khẩu", e);
            return false;
        }
    }

    /* ========== GET/DELETE ========== */
    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "Lỗi khi lấy user theo ID", e);
        }
        return null;
    }
    public User getById(int id) { return getUserById(id); }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            int rows = st.executeUpdate();
            try {
                if (connection != null && !connection.getAutoCommit()) connection.commit();
            } catch (SQLException ignore) {}
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[deleteUser] Lỗi khi xóa user", e);
            return false;
        }
    }

    /* ========== map() ========== */
    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password")); // chuỗi hash stored
        u.setEmail(rs.getString("email"));
        u.setFullName(rs.getString("full_name"));
        u.setRole(rs.getInt("role"));
        try { u.setCreatedAt(rs.getTimestamp("created_at")); } catch (SQLException ignore) {}
        return u;
    }
}
