package com.example.storekeeper.Models;

public class fromEmpReturnModel {
    String name;
    String date;
    String msg;
    String serial;

    public fromEmpReturnModel(String name, String date, String msg) {
        this.name = name;
        this.date = date;
        this.msg = msg;
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

    public String getMsg() {
        return msg;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
