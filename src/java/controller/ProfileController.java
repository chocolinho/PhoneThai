package controller;

import dao.UserDAO;
import entity.User;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;

import java.io.IOException;

@WebServlet(name="ProfileController", urlPatterns = {
        "/auth/profile",          // xem profile
        "/auth/profile/edit",     // GET + POST lưu thông tin
        "/auth/profile/password"  // POST đổi mật khẩu
})
public class ProfileController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    private User requireLogin(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        User u = (session == null) ? null : (User) session.getAttribute("user");
        if (u == null) { resp.sendRedirect(req.getContextPath() + "/auth/login"); return null; }
        return u;
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        User u = requireLogin(req, resp);
        if (u == null) return;

        String uri = req.getRequestURI();
        if (uri.endsWith("/profile")) {
            // load mới nhất từ DB rồi refresh session
            User fresh = userDAO.getUserById(u.getUserId());
            if (fresh != null) req.getSession().setAttribute("user", fresh);
            req.getRequestDispatcher("/auth/Profile.jsp").forward(req, resp);
        } else if (uri.endsWith("/edit")) {
            req.setAttribute("user", userDAO.getUserById(u.getUserId()));
            req.getRequestDispatcher("/auth/profile-edit.jsp").forward(req, resp);
        } else {
            resp.sendError(404);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        User u = requireLogin(req, resp);
        if (u == null) return;

        String uri = req.getRequestURI();

        if (uri.endsWith("/edit")) {
            String fullName = nvl(req.getParameter("fullName"));
            String email    = nvl(req.getParameter("email"));
            String phone    = nvl(req.getParameter("phone"));

            u.setFullName(fullName);
            u.setEmail(email);
            u.setPhone(phone);

            if (userDAO.updateProfileBasic(u)) {
                // reload session
                req.getSession().setAttribute("user", userDAO.getUserById(u.getUserId()));
                resp.sendRedirect(req.getContextPath()+"/auth/profile?ok=1");
            } else {
                req.setAttribute("user", u);
                req.setAttribute("error", "Lưu thất bại. Vui lòng thử lại.");
                req.getRequestDispatcher("/auth/profile-edit.jsp").forward(req, resp);
            }
            return;
        }

        if (uri.endsWith("/password")) {
            String oldPass = nvl(req.getParameter("oldPassword"));
            String newPass = nvl(req.getParameter("newPassword"));
            String rePass  = nvl(req.getParameter("rePassword"));

            if (newPass.isEmpty() || !newPass.equals(rePass)) {
                resp.sendRedirect(req.getContextPath()+"/auth/profile?perr=mismatch");
                return;
            }
            if (!userDAO.checkPasswordRaw(u.getUserId(), oldPass)) {
                resp.sendRedirect(req.getContextPath()+"/auth/profile?perr=old");
                return;
            }
            if (userDAO.updatePasswordRaw(u.getUserId(), newPass)) {
                resp.sendRedirect(req.getContextPath()+"/auth/profile?pok=1");
            } else {
                resp.sendRedirect(req.getContextPath()+"/auth/profile?perr=save");
            }
            return;
        }

        resp.sendError(404);
    }

    private static String nvl(String s){ return s==null? "": s.trim(); }
}
