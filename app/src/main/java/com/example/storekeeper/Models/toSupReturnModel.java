package com.example.storekeeper.Models;

public class toSupReturnModel {
    String name;
    String date;
    String msg;
    String serial;

    public toSupReturnModel(String name, String date, String msg, String serial) {
        this.name = name;
        this.date = date;
        this.msg = msg;
        this.serial = serial;
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

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }
}
