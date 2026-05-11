package com.example.mgefrozenfoods;

public class Order {
    private String orderId;
    private String date;
    private String status;
    private String itemsSummary;
    private double total;

    public Order(String orderId, String date, String status, String itemsSummary, double total) {
        this.orderId = orderId;
        this.date = date;
        this.status = status;
        this.itemsSummary = itemsSummary;
        this.total = total;
    }

    public String getOrderId() { return orderId; }
    public String getDate() { return date; }
    public String getStatus() { return status; }
    public String getItemsSummary() { return itemsSummary; }
    public double getTotal() { return total; }
}