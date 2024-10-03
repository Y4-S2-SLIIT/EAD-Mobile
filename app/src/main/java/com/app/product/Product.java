package com.app.product;

public class Product {
    private String id;
    private String name;
    private String brand;
    private String description;
    private String category;
    private double price;
    private String imageUrl;
    private int stock;

    // Constructor
    public Product(String id, String name, String brand, String description, String category, double price, String imageUrl, int stock) {
        this.id = id;
        this.name = name;
        this.brand = brand;
        this.description = description;
        this.category = category;
        this.price = price;
        this.imageUrl = imageUrl;
        this.stock = stock;
    }

    // Getters
    public String getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getBrand() {
        return brand;
    }

    public String getDescription() {
        return description;
    }

    public String getCategory() {
        return category;
    }

    public double getPrice() {
        return price;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public int getStock() {
        return stock;
    }
}
