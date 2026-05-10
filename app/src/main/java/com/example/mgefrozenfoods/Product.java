package com.example.mgefrozenfoods;

public class Product {
    private String name;
    private String price;
    private String brand;
    private int imageResId;

    public Product(String name, String price, String brand, int imageResId) {
        this.name = name;
        this.price = price;
        this.brand = brand;
        this.imageResId = imageResId;
    }

    public String getName() { return name; }
    public String getPrice() { return price; }
    public String getBrand() { return brand; }
    public int getImageResId() { return imageResId; }
}