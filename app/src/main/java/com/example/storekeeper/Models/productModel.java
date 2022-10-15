package com.example.storekeeper.Models;

public class productModel {
    int productId;
    String productName;
    String productBarcode;
    int productWarranty;

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getProductBarcode() {
        return productBarcode;
    }

    public void setProductBarcode(String productBarcode) {
        this.productBarcode = productBarcode;
    }

    public int getProductWarranty() {
        return productWarranty;
    }

    public void setProductWarranty(int productWarranty) {
        this.productWarranty = productWarranty;
    }

    public String getProductName() {
        return productName;
    }

    public productModel(String productName) {
        this.productName = productName;
    }

    public productModel(int productId, String productName, String productBarcode, int productWarranty) {
        this.productId = productId;
        this.productName = productName;
        this.productBarcode = productBarcode;
        this.productWarranty = productWarranty;
    }

    public productModel(int productId, String productName, String productBarcode) {
        this.productId = productId;
        this.productName = productName;
        this.productBarcode = productBarcode;
    }
}
