package controller;

import dao.UserDAO;
import entity.User;
import java.io.IOException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet(name = "LoginController", urlPatterns = {"/login"})
public class LoginController extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // ✅ forward đúng đường dẫn JSP thực tế
        request.getRequestDispatcher("/auth/Login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String username = request.getParameter("username");
        String pass = request.getParameter("password");
        String remember = request.getParameter("remember");

        UserDAO dao = new UserDAO();
        User user = dao.login(username, pass);

        if (user == null) {
            request.setAttribute("errorMessage", "Sai tên đăng nhập hoặc mật khẩu!");
            request.getRequestDispatcher("/auth/Login.jsp").forward(request, response);
        } else {
            HttpSession session = request.getSession();
            session.setAttribute("user", user);

            // ✅ Cookie "Remember me"
            Cookie userCookie = new Cookie("userC", username);
            Cookie passCookie = new Cookie("passC", pass);
            userCookie.setPath("/");
            passCookie.setPath("/");

            if (remember != null) {
                userCookie.setMaxAge(60 * 60 * 24 * 30);
                passCookie.setMaxAge(60 * 60 * 24 * 30);
            } else {
                userCookie.setMaxAge(0);
                passCookie.setMaxAge(0);
            }

            response.addCookie(userCookie);
            response.addCookie(passCookie);

            // ✅ redirect chuẩn
            if (user.getRole() == 1) {
                response.sendRedirect(request.getContextPath() + "/admin/dashboard");
            } else {
                response.sendRedirect(request.getContextPath() + "/");
            }
        }
    }
}
