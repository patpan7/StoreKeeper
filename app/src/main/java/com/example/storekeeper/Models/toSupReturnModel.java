package com.example.storekeeper.Models;

public class toSupReturnModel {
    String name;
    String date;
    String msn;

    public toSupReturnModel(String name, String date, String msn) {
        this.name = name;
        this.date = date;
        this.msn = msn;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMsn() {
        return msn;
    }

    public void setMsn(String msn) {
        this.msn = msn;
    }
}
