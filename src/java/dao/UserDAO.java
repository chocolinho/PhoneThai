package dao;

import context.DBContext;
import context.MaHoa;              // PBKDF2: iterations:salt:hash
import entity.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(UserDAO.class.getName());

    /* ================== Login (verify hash) ================== */
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
            LOGGER.log(Level.SEVERE, "[login] Lỗi khi đăng nhập", e);
        }
        return null;
    }

    /* ================== Tìm theo username ================== */
    public User findByUsername(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[findByUsername] failed", e);
        }
        return null;
    }

    /* ================== Forgot password: đếm & khóa ================== */

    /** reset_attempts = 0, reset_attempts_date = today */
    public void resetForgotAttempts(int userId, java.sql.Date today) {
        String sql = "UPDATE users SET reset_attempts = 0, reset_attempts_date = ? WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setDate(1, today);
            st.setInt(2, userId);
            st.executeUpdate();
            commitIfNeeded();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[resetForgotAttempts] failed", e);
        }
    }

    /** set reset_attempts=?, reset_attempts_date=today */
    public void updateForgotAttempts(int userId, int attempts, java.sql.Date today) {
        String sql = "UPDATE users SET reset_attempts = ?, reset_attempts_date = ? WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, attempts);
            st.setDate(2, today);
            st.setInt(3, userId);
            st.executeUpdate();
            commitIfNeeded();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[updateForgotAttempts] failed", e);
        }
    }

    /** khóa tới thời điểm until */
    public void lockForgotUntil(int userId, java.sql.Timestamp until) {
        String sql = "UPDATE users SET reset_locked_until = ? WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setTimestamp(1, until);
            st.setInt(2, userId);
            st.executeUpdate();
            commitIfNeeded();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[lockForgotUntil] failed", e);
        }
    }

    /** mở khóa + reset attempts (sau khi cấp mật khẩu tạm thành công) */
    public void clearForgotLockAndAttempts(int userId, java.sql.Date today) {
        String sql = """
            UPDATE users
               SET reset_locked_until = NULL,
                   reset_attempts = 0,
                   reset_attempts_date = ?
             WHERE user_id = ?
        """;
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setDate(1, today);
            st.setInt(2, userId);
            st.executeUpdate();
            commitIfNeeded();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[clearForgotLockAndAttempts] failed", e);
        }
    }

    /* ================== Đổi/Reset mật khẩu ================== */

    /** cập nhật password (hash sẵn) + must_change_password */
    public void updatePasswordAndForceChange(int userId, String storedHash, boolean mustChange) {
        String sql = "UPDATE users SET password = ?, must_change_password = ? WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, storedHash);
            st.setInt(2, mustChange ? 1 : 0);
            st.setInt(3, userId);
            st.executeUpdate();
            commitIfNeeded();
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[updatePasswordAndForceChange] failed", e);
        }
    }

    /** Reset bằng raw password (hash qua MaHoa) + set must_change_password=1 */
    public boolean resetPassword(int userId, String rawTempPassword) {
        String sql = "UPDATE users SET password=?, must_change_password=1 WHERE user_id=?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            String stored = MaHoa.createStoredPassword(rawTempPassword); // iterations:salt:hash
            st.setString(1, stored);
            st.setInt(2, userId);
            int rows = st.executeUpdate();
            commitIfNeeded();
            return rows > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[resetPassword] failed", e);
            return false;
        }
    }

    /* ================== CRUD & tiện ích khác ================== */

    /** Lấy ds rút gọn cho dropdown */
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
            LOGGER.log(Level.SEVERE, "[listAll] failed", e);
        }
        return list;
    }

    public User checkUserExist(String username) {
        String sql = "SELECT * FROM users WHERE username = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setString(1, username);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[checkUserExist] failed", e);
        }
        return null;
    }

    public List<User> getAllUsers() {
        List<User> list = new ArrayList<>();
        String sql = "SELECT * FROM users ORDER BY user_id DESC";
        try (PreparedStatement st = connection.prepareStatement(sql);
             ResultSet rs = st.executeQuery()) {
            while (rs.next()) list.add(map(rs));
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[getAllUsers] failed", e);
        }
        return list;
    }

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
            commitIfNeeded();
            return rows > 0;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "[insertUser] failed", e);
            return false;
        }
    }

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
            commitIfNeeded();
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[updateUserWithoutPassword] failed", e);
            return false;
        }
    }

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
            LOGGER.log(Level.SEVERE, "[search] failed", e);
        }
        return list;
    }

    public User getUserById(int id) {
        String sql = "SELECT * FROM users WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            try (ResultSet rs = st.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[getUserById] failed", e);
        }
        return null;
    }
    public User getById(int id) { return getUserById(id); }

    public boolean deleteUser(int id) {
        String sql = "DELETE FROM users WHERE user_id = ?";
        try (PreparedStatement st = connection.prepareStatement(sql)) {
            st.setInt(1, id);
            int rows = st.executeUpdate();
            commitIfNeeded();
            return rows > 0;
        } catch (SQLException e) {
            LOGGER.log(Level.SEVERE, "[deleteUser] failed", e);
            return false;
        }
    }

    /* ================== map() – đọc đầy đủ cột ================== */
    private User map(ResultSet rs) throws SQLException {
        User u = new User();
        u.setUserId(rs.getInt("user_id"));
        u.setUsername(rs.getString("username"));
        u.setPassword(rs.getString("password")); // stored hash

        // Info
        try { u.setEmail(rs.getString("email")); } catch (SQLException ignore) {}
        try { u.setFullName(rs.getString("full_name")); } catch (SQLException ignore) {}
        try { u.setRole(rs.getInt("role")); } catch (SQLException ignore) {}
        try { u.setPhone(rs.getString("phone")); } catch (SQLException ignore) {}

        // Forgot flow fields
        try { u.setMustChangePassword(rs.getInt("must_change_password")); } catch (SQLException ignore) {}
        try { u.setResetAttempts(rs.getInt("reset_attempts")); } catch (SQLException ignore) {}
        try { u.setResetAttemptsDate(rs.getDate("reset_attempts_date")); } catch (SQLException ignore) {}
        try { u.setResetLockedUntil(rs.getTimestamp("reset_locked_until")); } catch (SQLException ignore) {}

        // Audit
        try { u.setCreatedAt(rs.getTimestamp("created_at")); } catch (SQLException ignore) {}

        return u;
    }

    private void commitIfNeeded() {
        try {
            if (connection != null && !connection.getAutoCommit()) {
                connection.commit();
            }
        } catch (SQLException ignore) {}
    }
}
