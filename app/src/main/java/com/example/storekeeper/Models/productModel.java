package com.example.storekeeper.Models;

public class productModel {
    int code;
    String name;
    String barcode;
    int warranty;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public int getWarranty() {
        return warranty;
    }

    public void setWarranty(int warranty) {
        this.warranty = warranty;
    }

    public String getName() {
        return name;
    }

    public productModel(String productName) {
        this.name = productName;
    }

    public productModel(int productId, String productName, String productBarcode, int productWarranty) {
        this.code = productId;
        this.name = productName;
        this.barcode = productBarcode;
        this.warranty = productWarranty;
    }

    public productModel(int productId, String productName, int warranty) {
        this.code = productId;
        this.name = productName;
        this.warranty = warranty;
    }
}
