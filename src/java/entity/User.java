package entity;

import java.sql.Date;
import java.sql.Timestamp;

public class User {
    private int userId;
    private String username;
    private String password;
    private String email;
    private String fullName;
    private int role;
    private Timestamp createdAt;          // thời điểm tạo

    // ====== Bổ sung cho luồng Forgot/Change Password ======
    private String phone;                 // SĐT đăng ký (nếu dùng)
    private int mustChangePassword;       // 1 = bắt buộc đổi PW sau khi login bằng PW tạm
    private Integer resetAttempts;        // số lần bấm "quên mật khẩu" trong ngày
    private Date resetAttemptsDate;       // ngày đang đếm attempts
    private Timestamp resetLockedUntil;   // khoá tự phục vụ đến thời điểm này

    public User() {}

    // Giữ nguyên constructor cũ để không phá code
    public User(int userId, String username, String password, String email,
                String fullName, int role, Timestamp createdAt) {
        this.userId = userId;
        this.username = username;
        this.password = password;
        this.email = email;
        this.fullName = fullName;
        this.role = role;
        this.createdAt = createdAt;
    }

    // Constructor đầy đủ (tuỳ bạn có cần dùng hay không)
    public User(int userId, String username, String password, String email,
                String fullName, int role, Timestamp createdAt,
                String phone, int mustChangePassword, Integer resetAttempts,
                Date resetAttemptsDate, Timestamp resetLockedUntil) {
        this(userId, username, password, email, fullName, role, createdAt);
        this.phone = phone;
        this.mustChangePassword = mustChangePassword;
        this.resetAttempts = resetAttempts;
        this.resetAttemptsDate = resetAttemptsDate;
        this.resetLockedUntil = resetLockedUntil;
    }

    // ===== Getters/Setters cơ bản =====
    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public String getFullName() { return fullName; }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public int getRole() { return role; }
    public void setRole(int role) { this.role = role; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    // ===== Getters/Setters bổ sung =====
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public int getMustChangePassword() { return mustChangePassword; }
    public void setMustChangePassword(int mustChangePassword) { this.mustChangePassword = mustChangePassword; }

    public Integer getResetAttempts() { return resetAttempts; }
    public void setResetAttempts(Integer resetAttempts) { this.resetAttempts = resetAttempts; }

    public Date getResetAttemptsDate() { return resetAttemptsDate; }
    public void setResetAttemptsDate(Date resetAttemptsDate) { this.resetAttemptsDate = resetAttemptsDate; }

    public Timestamp getResetLockedUntil() { return resetLockedUntil; }
    public void setResetLockedUntil(Timestamp resetLockedUntil) { this.resetLockedUntil = resetLockedUntil; }
}
