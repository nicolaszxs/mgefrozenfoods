package com.example.mgefrozenfoods;

public class AdminProduct {
    private String name;
    private String brand;
    private String price;
    private int stock;
    private int imageResId;

    public AdminProduct(String name, String price, String brand, int stock, int imageResId) {
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.stock = stock;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getBrand() { return brand; }
    public int getStock() { return stock; }
    public int getImageResId() { return imageResId; }
}