package com.mulungushi.lodgebilling;

public class Bill {
    private String billId;
    private String guestName;
    private String roomType;
    private int nights;
    private double extras;
    private double amountPaid;
    private String notes;
    private double total;
    private double balance;

    public Bill(String billId, String guestName, String roomType, int nights,
                double extras, double amountPaid, String notes, double total, double balance) {
        this.billId = billId;
        this.guestName = guestName;
        this.roomType = roomType;
        this.nights = nights;
        this.extras = extras;
        this.amountPaid = amountPaid;
        this.notes = notes;
        this.total = total;
        this.balance = balance;
    }

    // Getters
    public String getBillId() { return billId; }
    public String getGuestName() { return guestName; }
    public String getRoomType() { return roomType; }
    public int getNights() { return nights; }
    public double getExtras() { return extras; }
    public double getAmountPaid() { return amountPaid; }
    public String getNotes() { return notes; }
    public double getTotal() { return total; }
    public double getBalance() { return balance; }

    // Formatted getters for the TableView
    public String getTotalFormatted() {
        return String.format("K %.2f", total);
    }

    public String getPaidFormatted() {
        return String.format("K %.2f", amountPaid);
    }

    public String getBalanceFormatted() {
        return String.format("K %.2f", balance);
    }
}