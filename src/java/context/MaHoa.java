package context;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

public class MaHoa {

    private static final Logger LOG = Logger.getLogger(MaHoa.class.getName());

    private static final int SALT_LEN   = 16;
    private static final int ITERATIONS = 10000; // số lần lặp

    // Tạo salt ngẫu nhiên an toàn
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LEN];
        new SecureRandom().nextBytes(salt);
        return salt;
    }

    // Hash một mảng byte bằng SHA-256 (tiện ích)
    private static byte[] sha256(byte[] input, MessageDigest md) {
        md.reset();
        return md.digest(input);
    }

    /**
     * Hash mật khẩu với salt & số vòng lặp, trả về Base64(hash).
     * Công thức:
     *   hashed = SHA256(salt || password)      // 1 lần đầu
     *   lặp (ITERATIONS - 1) lần tiếp theo: hashed = SHA256(hashed)
     * Tổng số SHA256 đúng bằng ITERATIONS.
     */
    public static String hashPassword(String password, byte[] salt, int iterations) throws Exception {
        if (password == null) password = "";
        if (salt == null || salt.length == 0) throw new IllegalArgumentException("salt empty");
        if (iterations < 1) throw new IllegalArgumentException("iterations must be >= 1");

        MessageDigest md = MessageDigest.getInstance("SHA-256");

        // Lần đầu: SHA256(salt + password)
        byte[] passBytes = password.getBytes(StandardCharsets.UTF_8);
        byte[] combined = new byte[salt.length + passBytes.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(passBytes, 0, combined, salt.length, passBytes.length);

        byte[] hashed = sha256(combined, md);

        // Lặp thêm (iterations - 1) lần
        for (int i = 1; i < iterations; i++) {
            hashed = sha256(hashed, md);
        }

        return Base64.getEncoder().encodeToString(hashed);
    }

    /** Tạo chuỗi lưu: iterations:saltBase64:hashBase64 */
    public static String createStoredPassword(String password) throws Exception {
        byte[] salt = generateSalt();
        String hashB64 = hashPassword(password, salt, ITERATIONS);
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        return ITERATIONS + ":" + saltB64 + ":" + hashB64;
    }

    // So sánh hằng thời gian
    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) return false;
        int diff = a.length ^ b.length; // XOR độ dài
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) diff |= a[i] ^ b[i];
        return diff == 0;
    }

    /** Xác minh mật khẩu: parse stored -> hash lại -> so sánh constant-time */
    public static boolean verifyPassword(String password, String stored) throws Exception {
        if (stored == null) return false;
        String[] parts = stored.split(":");
        if (parts.length != 3) return false;

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt     = Base64.getDecoder().decode(parts[1]);
        byte[] storedHash = Base64.getDecoder().decode(parts[2]);

        String computedB64 = hashPassword(password, salt, iterations);
        byte[] actualHash  = Base64.getDecoder().decode(computedB64);

        boolean ok = constantTimeEquals(storedHash, actualHash);

        // Xóa dữ liệu nhạy cảm khỏi heap (optional)
        Arrays.fill(actualHash, (byte) 0);

        return ok;
    }

    // Demo nhỏ
    public static void main(String[] args) {
        try {
            String storedPass = createStoredPassword("123456");
            System.out.println("Stored pass: " + storedPass);
            System.out.println("Verify ok? " + verifyPassword("123456", storedPass));
        } catch (Exception ex) {
            LOG.log(Level.SEVERE, null, ex);
        }
    }
}
