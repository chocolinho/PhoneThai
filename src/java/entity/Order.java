package entity;

import java.sql.Timestamp;

public class Order {
    private int orderId;
    private int userId;
    private int quantity;
    private double total;
    private Timestamp orderDate; // đổi sang Timestamp
    private String status;

    // các field phụ để hiển thị
    private String userFullName; // nếu JOIN users
    private String userEmail;    // nếu JOIN users
    private String orderDateLocal; // yyyy-MM-ddTHH:mm cho input datetime-local

    public Order() {}

    public Order(int orderId, int userId, int quantity, double total,
                 Timestamp orderDate, String status) {
        this.orderId = orderId;
        this.userId = userId;
        this.quantity = quantity;
        this.total = total;
        this.orderDate = orderDate;
        this.status = status;
    }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotal() { return total; }
    public void setTotal(double total) { this.total = total; }

    public Timestamp getOrderDate() { return orderDate; }
    public void setOrderDate(Timestamp orderDate) { this.orderDate = orderDate; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getUserFullName() { return userFullName; }
    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getUserEmail() { return userEmail; }
    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getOrderDateLocal() { return orderDateLocal; }
    public void setOrderDateLocal(String orderDateLocal) { this.orderDateLocal = orderDateLocal; }
}
