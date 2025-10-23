package controller;

import dao.UserDAO;
import entity.User;
import jakarta.servlet.*;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.security.SecureRandom;
import java.util.List;

@WebServlet(name = "UserController", urlPatterns = {"/admin/users"})
public class UserController extends HttpServlet {

    private final UserDAO userDAO = new UserDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "edit": {
                int id;
                try { id = Integer.parseInt(request.getParameter("id")); }
                catch (Exception e) { response.sendRedirect(request.getContextPath() + "/admin/users"); return; }

                User formUser = userDAO.getUserById(id);
                if (formUser == null) { response.sendRedirect(request.getContextPath() + "/admin/users"); return; }

                request.setAttribute("formUser", formUser); // tránh đụng sessionScope.user
                request.getRequestDispatcher("/admin/user-form.jsp").forward(request, response);
                return;
            }
            case "new": {
                request.setAttribute("formUser", null);
                request.getRequestDispatcher("/admin/user-form.jsp").forward(request, response);
                return;
            }
            default: {
                // tìm kiếm: uid (id), q (tên), login (username)
                Integer uid = safeParseIntOrNull(request.getParameter("uid"));
                String q = nvl(request.getParameter("q"));
                String login = nvl(request.getParameter("login"));

                List<User> list = (uid != null || !q.isBlank() || !login.isBlank())
                        ? userDAO.search(uid, q, login)
                        : userDAO.getAllUsers();

                request.setAttribute("list", list);
                request.setAttribute("uid", uid);
                request.setAttribute("q", q);
                request.setAttribute("login", login);
                request.getRequestDispatcher("/admin/user-list.jsp").forward(request, response);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        String action = request.getParameter("action");
        if (action == null) action = "";

        switch (action) {
            case "save": {
                String idStr = nvl(request.getParameter("id"));
                boolean isUpdate = !idStr.isEmpty();

                String username = nvl(request.getParameter("username"));
                String rawPassword = nvl(request.getParameter("password")); // chỉ dùng khi thêm mới
                String email = nvl(request.getParameter("email"));
                String fullName = nvl(request.getParameter("full_name"));
                int role = 0; try { role = Integer.parseInt(request.getParameter("role")); } catch (Exception ignore) {}

                // validate cơ bản
                if (username.isBlank()) {
                    backToFormWithError(request, response, isUpdate, idStr,
                            "Username không được để trống.", username, email, fullName, role);
                    return;
                }
                if (!isUpdate && rawPassword.isBlank()) {
                    backToFormWithError(request, response, false, "",
                            "Mật khẩu bắt buộc khi thêm mới.", username, email, fullName, role);
                    return;
                }

                // kiểm tra trùng username
                User existed = userDAO.checkUserExist(username);
                if (!isUpdate) {
                    if (existed != null) {
                        backToFormWithError(request, response, false, "",
                                "Username đã tồn tại.", username, email, fullName, role);
                        return;
                    }
                } else {
                    int currentId = safeParseInt(idStr);
                    if (existed != null && existed.getUserId() != currentId) {
                        backToFormWithError(request, response, true, idStr,
                                "Username đã được dùng bởi user khác.", username, email, fullName, role);
                        return;
                    }
                }

                boolean ok;
                if (isUpdate) {
                    // KHÔNG cho sửa mật khẩu
                    User u = new User();
                    u.setUserId(safeParseInt(idStr));
                    u.setUsername(username);
                    u.setEmail(email);
                    u.setFullName(fullName);
                    u.setRole(role);
                    ok = userDAO.updateUserWithoutPassword(u);
                    if (!ok) {
                        backToFormWithError(request, response, true, idStr,
                                "Cập nhật thất bại. Vui lòng kiểm tra dữ liệu.",
                                username, email, fullName, role);
                        return;
                    }
                } else {
                    // thêm mới: bắt buộc có password
                    User u = new User();
                    u.setUsername(username);
                    u.setPassword(rawPassword);
                    u.setEmail(email);
                    u.setFullName(fullName);
                    u.setRole(role);
                    ok = userDAO.insertUser(u);
                    if (!ok) {
                        backToFormWithError(request, response, false, "",
                                "Thêm mới thất bại. Có thể do trùng username hoặc dữ liệu chưa hợp lệ.",
                                username, email, fullName, role);
                        return;
                    }
                }

                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }

            case "delete": {
                try {
                    int id = Integer.parseInt(request.getParameter("id"));
                    userDAO.deleteUser(id);
                } catch (Exception ignore) {}
                response.sendRedirect(request.getContextPath() + "/admin/users");
                return;
            }

            case "resetpw": {
                int id = safeParseInt(nvl(request.getParameter("id")));
                if (id <= 0) { response.sendRedirect(request.getContextPath()+"/admin/users"); return; }

                String tmp = generateTempPassword(10); // random 10 ký tự
                boolean ok = userDAO.resetPassword(id, tmp);
                if (ok) {
                    request.getSession().setAttribute("flash_pw", "Mật khẩu tạm thời: " + tmp + " (chỉ hiển thị 1 lần)");
                    response.sendRedirect(request.getContextPath()+"/admin/users?action=edit&id="+id);
                } else {
                    request.getSession().setAttribute("flash_pw", "Không đặt lại được mật khẩu.");
                    response.sendRedirect(request.getContextPath()+"/admin/users");
                }
                return;
            }

            default: {
                response.sendRedirect(request.getContextPath() + "/admin/users");
            }
        }
    }

    // ===== helpers =====
    private static String nvl(String s) { return s == null ? "" : s.trim(); }
    private static int safeParseInt(String s) { try { return Integer.parseInt(s); } catch (Exception e) { return 0; } }
    private static Integer safeParseIntOrNull(String s) {
        try { if (s == null || s.trim().isEmpty()) return null; return Integer.valueOf(s.trim()); }
        catch (Exception e) { return null; }
    }
    private static String generateTempPassword(int length){
        final String alphabet = "ABCDEFGHJKLMNPQRSTUVWXYZabcdefghijkmnopqrstuvwxyz23456789@#$%";
        SecureRandom r = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i=0;i<length;i++) sb.append(alphabet.charAt(r.nextInt(alphabet.length())));
        return sb.toString();
    }

    private void backToFormWithError(HttpServletRequest req, HttpServletResponse resp,
                                     boolean isUpdate, String idStr, String errorMsg,
                                     String username, String email, String fullName, int role)
            throws ServletException, IOException {
        User form = new User();
        if (isUpdate) form.setUserId(safeParseInt(idStr));
        form.setUsername(username);
        form.setEmail(email);
        form.setFullName(fullName);
        form.setRole(role);

        req.setAttribute("errorMsg", errorMsg);
        req.setAttribute("formUser", form);
        req.getRequestDispatcher("/admin/user-form.jsp").forward(req, resp);
    }
}
