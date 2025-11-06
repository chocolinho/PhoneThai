package controller;

import dao.UserAddressDAO;
import entity.User;
import entity.UserAddress;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet(name = "AddressesController", urlPatterns = {"/addresses"})
public class AddressesController extends HttpServlet {

    private final UserAddressDAO addressDAO = new UserAddressDAO();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/Login.jsp?redirect=addresses");
            return;
        }

        List<UserAddress> addresses = addressDAO.findByUser(user.getUserId());
        req.setAttribute("addresses", addresses);

        UserAddress form = new UserAddress();
        form.setDefault(addresses.isEmpty());

        String editId = req.getParameter("id");
        if (editId != null) {
            try {
                int id = Integer.parseInt(editId);
                UserAddress address = addressDAO.findById(id, user.getUserId());
                if (address != null) {
                    form = address;
                }
            } catch (NumberFormatException ignore) {
            }
        }

        req.setAttribute("editing", form);
        req.getRequestDispatcher("/addresses.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {

        HttpSession session = req.getSession(false);
        User user = session == null ? null : (User) session.getAttribute("user");
        if (user == null) {
            resp.sendRedirect(req.getContextPath() + "/auth/Login.jsp?redirect=addresses");
            return;
        }

        String action = req.getParameter("action");
        if ("delete".equals(action)) {
            int id = parseId(req.getParameter("id"));
            if (id > 0) {
                addressDAO.delete(id, user.getUserId());
            }
            resp.sendRedirect(req.getContextPath() + "/addresses?deleted=1");
            return;
        }
        if ("default".equals(action)) {
            int id = parseId(req.getParameter("id"));
            if (id > 0) {
                addressDAO.markDefault(id, user.getUserId());
            }
            resp.sendRedirect(req.getContextPath() + "/addresses?defaulted=1");
            return;
        }

        List<UserAddress> addresses = addressDAO.findByUser(user.getUserId());

        UserAddress form = new UserAddress();
        form.setFullName(trim(req.getParameter("fullName")));
        form.setPhone(trim(req.getParameter("phone")));
        form.setAddressLine(trim(req.getParameter("addressLine")));
        form.setWard(trim(req.getParameter("ward")));
        form.setDistrict(trim(req.getParameter("district")));
        form.setProvince(trim(req.getParameter("province")));
        boolean wantsDefault = "1".equals(req.getParameter("isDefault"));
        form.setDefault(wantsDefault);

        int id = parseId(req.getParameter("addressId"));
        if (id > 0) form.setAddressId(id);
        form.setUserId(user.getUserId());

        if (addresses.isEmpty()) {
            form.setDefault(true);
        }

        List<String> errors = validate(form);
        if (!errors.isEmpty()) {
            req.setAttribute("errors", errors);
            req.setAttribute("addresses", addresses);
            req.setAttribute("editing", form);
            req.getRequestDispatcher("/addresses.jsp").forward(req, resp);
            return;
        }

        boolean ok = addressDAO.save(form);
        if (ok) {
            resp.sendRedirect(req.getContextPath() + "/addresses?saved=1");
        } else {
            errors.add("Không thể lưu địa chỉ. Vui lòng thử lại");
            req.setAttribute("errors", errors);
            req.setAttribute("addresses", addresses);
            req.setAttribute("editing", form);
            req.getRequestDispatcher("/addresses.jsp").forward(req, resp);
        }
    }

    private List<String> validate(UserAddress address) {
        List<String> errors = new ArrayList<>();
        if (address.getFullName().isBlank()) errors.add("Vui lòng nhập họ tên người nhận");
        if (address.getPhone().isBlank()) errors.add("Vui lòng nhập số điện thoại");
        if (address.getAddressLine().isBlank()) errors.add("Vui lòng nhập địa chỉ cụ thể");
        if (address.getDistrict().isBlank()) errors.add("Vui lòng nhập quận/huyện");
        if (address.getProvince().isBlank()) errors.add("Vui lòng nhập tỉnh/thành phố");
        return errors;
    }

    private int parseId(String raw) {
        if (raw == null || raw.isBlank()) return 0;
        try {
            return Integer.parseInt(raw);
        } catch (NumberFormatException e) {
            return 0;
        }
    }

    private String trim(String value) {
        return value == null ? "" : value.trim();
    }
}
