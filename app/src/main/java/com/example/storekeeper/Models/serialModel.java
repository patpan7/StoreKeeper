package com.example.storekeeper.Models;

public class serialModel {
    String serialnumber;
    int prod_code;

    int available;

    public serialModel(String serialnumber, int prod_code, int available) {
        this.prod_code = prod_code;
        this.serialnumber = serialnumber;
        this.available = available;
    }

    public int getProd_code() {
        return prod_code;
    }

    public String getSerialnumber() {
        return serialnumber;
    }

    public void setSerialnumber(String serialnumber) {
        this.serialnumber = serialnumber;
    }

    public int getAvailable() {
        return available;
    }

}
