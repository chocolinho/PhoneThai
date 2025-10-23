package entity;

public class OrderDetail {
    private int detailId;
    private int orderId;
    private int productId;
    private int quantity;
    private double price;
    private double subtotal;

    // field phục vụ hiển thị (JOIN products)
    private String productName;

    public OrderDetail() {}

    public OrderDetail(int detailId, int orderId, int productId,
                       int quantity, double price, double subtotal) {
        this.detailId = detailId;
        this.orderId = orderId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.subtotal = subtotal;
    }

    public int getDetailId() { return detailId; }
    public void setDetailId(int detailId) { this.detailId = detailId; }

    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public double getSubtotal() { return subtotal; }
    public void setSubtotal(double subtotal) { this.subtotal = subtotal; }

    public String getProductName() { return productName; }
    public void setProductName(String productName) { this.productName = productName; }
}
