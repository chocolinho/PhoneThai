package dao;

import context.DBContext;
import entity.UserAddress;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

public class UserAddressDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(UserAddressDAO.class.getName());

    private UserAddress map(ResultSet rs) throws SQLException {
        UserAddress address = new UserAddress();
        address.setAddressId(rs.getInt("address_id"));
        address.setUserId(rs.getInt("user_id"));
        address.setFullName(rs.getString("full_name"));
        address.setPhone(rs.getString("phone"));
        address.setAddressLine(rs.getString("address_line"));
        address.setWard(rs.getString("ward"));
        address.setDistrict(rs.getString("district"));
        address.setProvince(rs.getString("province"));
        address.setDefault(rs.getBoolean("is_default"));
        address.setCreatedAt(rs.getTimestamp("created_at"));
        address.setUpdatedAt(rs.getTimestamp("updated_at"));
        return address;
    }

    public List<UserAddress> findByUser(int userId) {
        List<UserAddress> list = new ArrayList<>();
        String sql = "SELECT * FROM user_addresses WHERE user_id = ? ORDER BY is_default DESC, updated_at DESC";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) list.add(map(rs));
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "findByUser failed", ex);
        }
        return list;
    }

    public UserAddress findById(int id, int userId) {
        String sql = "SELECT * FROM user_addresses WHERE address_id = ? AND user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "findById failed", ex);
        }
        return null;
    }

    public boolean save(UserAddress address) {
        if (address.getAddressId() > 0) {
            return update(address);
        }
        return insert(address);
    }

    private boolean insert(UserAddress address) {
        String sql = "INSERT INTO user_addresses(user_id, full_name, phone, address_line, ward, district, province, is_default) VALUES(?,?,?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setInt(1, address.getUserId());
            ps.setString(2, address.getFullName());
            ps.setString(3, address.getPhone());
            ps.setString(4, address.getAddressLine());
            ps.setString(5, address.getWard());
            ps.setString(6, address.getDistrict());
            ps.setString(7, address.getProvince());
            ps.setBoolean(8, address.isDefault());
            int affected = ps.executeUpdate();
            if (affected > 0) {
                try (ResultSet rs = ps.getGeneratedKeys()) {
                    if (rs.next()) address.setAddressId(rs.getInt(1));
                }
                if (address.isDefault()) {
                    markDefault(address.getAddressId(), address.getUserId());
                }
            }
            return affected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "insert address failed", ex);
        }
        return false;
    }

    private boolean update(UserAddress address) {
        String sql = "UPDATE user_addresses SET full_name=?, phone=?, address_line=?, ward=?, district=?, province=?, is_default=? WHERE address_id=? AND user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setString(1, address.getFullName());
            ps.setString(2, address.getPhone());
            ps.setString(3, address.getAddressLine());
            ps.setString(4, address.getWard());
            ps.setString(5, address.getDistrict());
            ps.setString(6, address.getProvince());
            ps.setBoolean(7, address.isDefault());
            ps.setInt(8, address.getAddressId());
            ps.setInt(9, address.getUserId());
            int affected = ps.executeUpdate();
            if (affected > 0 && address.isDefault()) {
                markDefault(address.getAddressId(), address.getUserId());
            }
            return affected > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "update address failed", ex);
        }
        return false;
    }

    public void delete(int id, int userId) {
        String sql = "DELETE FROM user_addresses WHERE address_id = ? AND user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, id);
            ps.setInt(2, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "delete address failed", ex);
        }
    }

    public void markDefault(int id, int userId) {
        String unset = "UPDATE user_addresses SET is_default = 0 WHERE user_id = ?";
        String set = "UPDATE user_addresses SET is_default = 1 WHERE address_id = ? AND user_id = ?";
        boolean oldAuto = true;
        try (PreparedStatement ps1 = connection.prepareStatement(unset);
             PreparedStatement ps2 = connection.prepareStatement(set)) {
            oldAuto = connection.getAutoCommit();
            connection.setAutoCommit(false);
            ps1.setInt(1, userId);
            ps1.executeUpdate();
            ps2.setInt(1, id);
            ps2.setInt(2, userId);
            ps2.executeUpdate();
            connection.commit();
        } catch (SQLException ex) {
            try { connection.rollback(); } catch (SQLException ignored) {}
            LOGGER.log(Level.SEVERE, "markDefault failed", ex);
        } finally {
            try { connection.setAutoCommit(oldAuto); } catch (SQLException ignored) {}
        }
    }
}
