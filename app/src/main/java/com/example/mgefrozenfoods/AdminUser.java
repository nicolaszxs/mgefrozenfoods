package com.example.mgefrozenfoods;

public class AdminUser {
    private String orderId;
    private String customerName;
    private String contactAddress;
    private String itemsOrdered;
    private String paymentTotal;
    private String status;

    public AdminUser(String orderId, String customerName, String contactAddress, String itemsOrdered, String paymentTotal, String status) {
        this.orderId = orderId;
        this.customerName = customerName;
        this.contactAddress = contactAddress;
        this.itemsOrdered = itemsOrdered;
        this.paymentTotal = paymentTotal;
        this.status = status;
    }

    public String getOrderId() { return orderId; }
    public String getCustomerName() { return customerName; }
    public String getContactAddress() { return contactAddress; }
    public String getItemsOrdered() { return itemsOrdered; }
    public String getPaymentTotal() { return paymentTotal; }
    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }
}