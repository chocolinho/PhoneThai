package controller;

import context.MaHoa;
import dao.UserDAO;
import entity.User;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name = "RegisterController", urlPatterns = {"/register"})
public class RegisterController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/auth/Register.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        response.setCharacterEncoding("UTF-8");

        String fullName = request.getParameter("full_name");
        String username = request.getParameter("username");
        String email = request.getParameter("email");
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        // 🔸 Kiểm tra nhập lại mật khẩu
        if (!password.equals(confirm)) {
            request.setAttribute("errorMessage", "Mật khẩu nhập lại không khớp!");
            request.getRequestDispatcher("/auth/Register.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();

        // 🔸 Kiểm tra username đã tồn tại
        if (dao.checkUserExist(username) != null) {
            request.setAttribute("errorMessage", "Tên đăng nhập đã tồn tại!");
            request.getRequestDispatcher("/auth/Register.jsp").forward(request, response);
            return;
        }

        try {
            // 🔒 Mã hóa mật khẩu trước khi lưu
            String hashed = MaHoa.createStoredPassword(password);

            User u = new User();
            u.setUsername(username);
            u.setPassword(hashed);
            u.setEmail(email);
            u.setFullName(fullName);
            u.setRole(0); // user thường

            boolean ok = dao.insertUser(u);

            if (ok) {
                // ✅ Gửi thông báo đăng ký thành công
                request.setAttribute("successMessage", "🎉 Đăng ký thành công! Bạn có thể đăng nhập ngay.");
            } else {
                request.setAttribute("errorMessage", "Đăng ký thất bại! Vui lòng thử lại.");
            }

            // Luôn quay lại cùng trang Register.jsp để hiển thị thông báo
            request.getRequestDispatcher("/auth/Register.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            request.setAttribute("errorMessage", "Lỗi xử lý đăng ký: " + e.getMessage());
            request.getRequestDispatcher("/auth/Register.jsp").forward(request, response);
        }
    }
}
