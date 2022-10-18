package com.example.storekeeper.Models;

public class incomeModel {
    String date;
    String supplier;

    public incomeModel(String date, String supplier) {
        this.date = date;
        this.supplier = supplier;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getSupplier() {
        return supplier;
    }

    public void setSupplier(String supplier) {
        this.supplier = supplier;
    }
}
