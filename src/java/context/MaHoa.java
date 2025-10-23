/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package context;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.Arrays;
import java.util.Base64;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Admin
 */
public class MaHoa {

    private static final int SALT_LEN = 16;
    private static final int ITERATIONS = 10000; // số lần lặp

    // Tạo salt
    public static byte[] generateSalt() {
        byte[] salt = new byte[SALT_LEN];
        //Tạo đối tượng sinh số ngẫu nhiên an toàn
        SecureRandom secureRandom = new SecureRandom();
        //Sinh dữ liệu ngẫu nhiên và ghi vào mảng salt
        secureRandom.nextBytes(salt);
        return salt;
    }

    // Hash (SHA-256)
    private static byte[] doHash(byte[] input) throws Exception {
        MessageDigest md = MessageDigest.getInstance("SHA-256");
        return md.digest(input);
    }
    // Hash password với salt & vòng lặp, trả về base64 hash

    public static String hashPassword(String password, byte[] salt, int iterations) throws Exception {
        // 1. Chuyển password thành byte
        byte[] passwordBytes = password.getBytes("UTF-8");

        // 2. Gộp salt + password: hash(salt + password)
        byte[] combined = new byte[salt.length + passwordBytes.length];
        System.arraycopy(salt, 0, combined, 0, salt.length);
        System.arraycopy(passwordBytes, 0, combined, salt.length, passwordBytes.length);

        // 3. Hash lần đầu
        byte[] hashed = doHash(combined);

        // 4. Lặp nhiều lần
        for (int i = 0; i < iterations; i++) {
            hashed = doHash(hashed);
        }

        // 5. Trả về Base64
        return Base64.getEncoder().encodeToString(hashed);
    }

//    public static String hashPassword(String password, byte[] salt, int iterations) throws Exception {
//        // ban đầu: hash(salt + password)
//        MessageDigest md = MessageDigest.getInstance("SHA-256");
//        md.update(salt);
//        byte[] hashed = md.digest(password.getBytes("UTF-8"));
//
//        // lặp
//        for (int i = 0; i < iterations; i++) {
//            md.reset();
//            hashed = md.digest(hashed);
//        }
//        return Base64.getEncoder().encodeToString(hashed);
//    }
    // Tạo chuỗi lưu: iterations:saltBase64:hashBase64
    public static String createStoredPassword(String password) throws Exception {
        byte[] salt = generateSalt();
        String hashB64 = hashPassword(password, salt, ITERATIONS);
        String saltB64 = Base64.getEncoder().encodeToString(salt);
        return ITERATIONS + ":" + saltB64 + ":" + hashB64;
    }
    // So sánh constant-time (thời gian thực) 2 mảng byte

    private static boolean constantTimeEquals(byte[] a, byte[] b) {
        if (a == null || b == null) {
            return false;
        }
        int diff = a.length ^ b.length;//phép XOR
        int len = Math.min(a.length, b.length);
        for (int i = 0; i < len; i++) {
            diff |= a[i] ^ b[i];
        }
        return diff == 0;
    }

    // Xác minh: parse stored string, lấy salt & iterations để hash lại
    public static boolean verifyPassword(String password, String stored) throws Exception {
        if (stored == null) {
            return false;
        }
        String[] parts = stored.split(":");
        if (parts.length != 3) {
            return false;
        }

        int iterations = Integer.parseInt(parts[0]);
        byte[] salt = Base64.getDecoder().decode(parts[1]);
        byte[] storedHash = Base64.getDecoder().decode(parts[2]);

        String hashB64 = hashPassword(password, salt, iterations);
        byte[] actualHash = Base64.getDecoder().decode(hashB64);

        boolean ok = constantTimeEquals(storedHash, actualHash);

        // clear sensitive data if needed
        Arrays.fill(actualHash, (byte) 0);

        return ok;
    }

    public static void main(String[] args) {
        try {
            String storedPass = createStoredPassword("123456");
            System.out.println("Stored pass: " + storedPass + " Len=" + storedPass.length());
            System.out.println(verifyPassword("123456", storedPass));
        } catch (Exception ex) {
            Logger.getLogger(MaHoa.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
