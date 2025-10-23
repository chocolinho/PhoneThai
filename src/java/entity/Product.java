package entity;

import java.sql.Timestamp;

public class Product {
    private int productId;
    private String name;
    private String description;
    private Double price;
    private Double oldPrice;     // nullable
    private Double rating;       // nullable
    private String brand;
    private int stock;
    private String category;
    private String image;
    private Timestamp createdAt; // đổi sang Timestamp
    private int discountPercent; // tính ở DAO

    public Product() {}

    public Product(int productId, String brand, String name, String description,
                   Double price, Double oldPrice, Double rating,
                   int stock, String category, String image, Timestamp createdAt) {
        this.productId = productId;
        this.brand = brand;
        this.name = name;
        this.description = description;
        this.price = price;
        this.oldPrice = oldPrice;
        this.rating = rating;
        this.stock = stock;
        this.category = category;
        this.image = image;
        this.createdAt = createdAt;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public Double getPrice() { return price; }
    public void setPrice(Double price) { this.price = price; }

    public Double getOldPrice() { return oldPrice; }
    public void setOldPrice(Double oldPrice) { this.oldPrice = oldPrice; }

    public Double getRating() { return rating; }
    public void setRating(Double rating) { this.rating = rating; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public int getStock() { return stock; }
    public void setStock(int stock) { this.stock = stock; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getImage() { return image; }
    public void setImage(String image) { this.image = image; }

    public Timestamp getCreatedAt() { return createdAt; }
    public void setCreatedAt(Timestamp createdAt) { this.createdAt = createdAt; }

    public int getDiscountPercent() { return discountPercent; }
    public void setDiscountPercent(int discountPercent) { this.discountPercent = discountPercent; }
}
