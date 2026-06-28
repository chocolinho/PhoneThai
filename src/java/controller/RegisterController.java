package controller;

import dao.UserDAO;
import entity.User;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

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

        String fullName = safe(request.getParameter("full_name"));
        String username = safe(request.getParameter("username"));
        String email = safe(request.getParameter("email"));
        String password = request.getParameter("password");
        String confirm = request.getParameter("confirm");

        if (username.isBlank() || password == null || password.isBlank()) {
            request.setAttribute("errorMessage", "Vui long nhap ten dang nhap va mat khau!");
            request.getRequestDispatcher("/auth/Register.jsp").forward(request, response);
            return;
        }

        if (!password.equals(confirm)) {
            request.setAttribute("errorMessage", "Mat khau nhap lai khong khop!");
            request.getRequestDispatcher("/auth/Register.jsp").forward(request, response);
            return;
        }

        UserDAO dao = new UserDAO();
        if (dao.checkUserExist(username) != null) {
            request.setAttribute("errorMessage", "Ten dang nhap da ton tai!");
            request.getRequestDispatcher("/auth/Register.jsp").forward(request, response);
            return;
        }

        User user = new User();
        user.setUsername(username);
        user.setPassword(password);
        user.setEmail(email);
        user.setFullName(fullName);
        user.setRole(0);

        boolean ok = dao.insertUser(user);
        if (ok) {
            request.setAttribute("successMessage", "Dang ky thanh cong! Ban co the dang nhap ngay.");
        } else {
            request.setAttribute("errorMessage", "Dang ky that bai! Vui long thu lai.");
        }
        request.getRequestDispatcher("/auth/Register.jsp").forward(request, response);
    }

    private String safe(String value) {
        return value == null ? "" : value.trim();
    }
}
