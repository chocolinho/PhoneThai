package context;

import context.DBContext;
import context.MaHoa; // nếu MaHoa ở package khác (vd util) đổi tương ứng: import util.MaHoa;
import java.sql.*;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Chạy để migrate mật khẩu plaintext -> chuỗi dạng iterations:salt:hash
 * WARNING: sẽ in ra mật khẩu thô (!) chỉ dùng trên môi trường local.
 */
public class PasswordMigration extends DBContext {

    private static final Logger LOG = Logger.getLogger(PasswordMigration.class.getName());

    public void migrateAllPlaintext() {
        String selectSql = "SELECT user_id, username, password FROM users WHERE password NOT LIKE '%:%'";
        String updateSql = "UPDATE users SET password=? WHERE user_id=?";

        try (PreparedStatement ps = connection.prepareStatement(selectSql);
             ResultSet rs = ps.executeQuery()) {

            int migrated = 0;
            System.out.println("=== START PASSWORD MIGRATION (Will print plaintext passwords) ===");
            System.out.println("Make sure you run this locally and remove logs after use!");

            while (rs.next()) {
                int userId = rs.getInt("user_id");
                String username = rs.getString("username");
                String plain = rs.getString("password"); // mật khẩu thô hiện có

                if (plain == null) {
                    System.out.printf("Skipping user %s (id=%d): password is NULL%n", username, userId);
                    continue;
                }

                // tạo hash
                String hashed;
                try {
                    hashed = MaHoa.createStoredPassword(plain);
                } catch (Exception e) {
                    LOG.log(Level.SEVERE, "Error hashing password for user " + username + " (id=" + userId + ")", e);
                    continue;
                }

                // cập nhật DB
                try (PreparedStatement up = connection.prepareStatement(updateSql)) {
                    up.setString(1, hashed);
                    up.setInt(2, userId);
                    int rows = up.executeUpdate();
                    if (rows > 0) {
                        migrated++;
                        // **DEBUG OUTPUT**: in ra userId, username, plain password và hash mới
                        System.out.printf("Migrated user: id=%d, username=%s, plain=%s, hashed=%s%n",
                                userId, username, plain, hashed);
                    } else {
                        System.out.printf("Failed to update user id=%d (username=%s)%n", userId, username);
                    }
                } catch (SQLException e) {
                    LOG.log(Level.SEVERE, "DB update failed for user " + username + " (id=" + userId + ")", e);
                }
            }

            System.out.println("DONE. Total migrated = " + migrated);
            System.out.println("=== END PASSWORD MIGRATION ===");
        } catch (Exception e) {
            LOG.log(Level.SEVERE, "Migrate failed", e);
        }
    }

    public static void main(String[] args) {
        PasswordMigration m = new PasswordMigration();
        // In thêm catalog để xác nhận connect tới DB đúng
        try {
            System.out.println("Connected to DB: " + (m.connection != null ? m.connection.getCatalog() : "null"));
        } catch (SQLException ignore) {}
        m.migrateAllPlaintext();
    }
}
