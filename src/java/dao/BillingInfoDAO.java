package dao;

import context.DBContext;
import entity.BillingInfo;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class BillingInfoDAO extends DBContext {

    private static final Logger LOGGER = Logger.getLogger(BillingInfoDAO.class.getName());

    private BillingInfo map(ResultSet rs) throws SQLException {
        BillingInfo info = new BillingInfo();
        info.setBillingId(rs.getInt("billing_id"));
        info.setUserId(rs.getInt("user_id"));
        info.setCardName(rs.getString("card_name"));
        info.setCardNumber(rs.getString("card_number"));
        info.setBankName(rs.getString("bank_name"));
        info.setExpiryMonth(rs.getInt("expiry_month"));
        info.setExpiryYear(rs.getInt("expiry_year"));
        info.setCreatedAt(rs.getTimestamp("created_at"));
        info.setUpdatedAt(rs.getTimestamp("updated_at"));
        return info;
    }

    public BillingInfo findByUser(int userId) {
        String sql = "SELECT * FROM billing_info WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) return map(rs);
            }
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "findByUser failed", ex);
        }
        return null;
    }

    public boolean save(BillingInfo info) {
        String update = "UPDATE billing_info SET card_name=?, card_number=?, bank_name=?, expiry_month=?, expiry_year=? WHERE user_id=?";
        try (PreparedStatement ps = connection.prepareStatement(update)) {
            ps.setString(1, info.getCardName());
            ps.setString(2, info.getCardNumber());
            ps.setString(3, info.getBankName());
            ps.setInt(4, info.getExpiryMonth());
            ps.setInt(5, info.getExpiryYear());
            ps.setInt(6, info.getUserId());
            int updated = ps.executeUpdate();
            if (updated > 0) return true;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "update billing failed", ex);
            return false;
        }

        String insert = "INSERT INTO billing_info(user_id, card_name, card_number, bank_name, expiry_month, expiry_year) VALUES(?,?,?,?,?,?)";
        try (PreparedStatement ps = connection.prepareStatement(insert)) {
            ps.setInt(1, info.getUserId());
            ps.setString(2, info.getCardName());
            ps.setString(3, info.getCardNumber());
            ps.setString(4, info.getBankName());
            ps.setInt(5, info.getExpiryMonth());
            ps.setInt(6, info.getExpiryYear());
            return ps.executeUpdate() > 0;
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "insert billing failed", ex);
        }
        return false;
    }

    public void deleteByUser(int userId) {
        String sql = "DELETE FROM billing_info WHERE user_id = ?";
        try (PreparedStatement ps = connection.prepareStatement(sql)) {
            ps.setInt(1, userId);
            ps.executeUpdate();
        } catch (SQLException ex) {
            LOGGER.log(Level.SEVERE, "delete billing failed", ex);
        }
    }
}
