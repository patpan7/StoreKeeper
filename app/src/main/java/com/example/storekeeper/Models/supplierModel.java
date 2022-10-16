package com.example.storekeeper.Models;

public class supplierModel {
    int code;
    String name;
    String phone;
    String mobile;
    String mail;
    String afm;

    public supplierModel(int code, String name, String phone, String mobile, String mail, String afm) {
        this.code = code;
        this.name = name;
        this.phone = phone;
        this.mobile = mobile;
        this.mail = mail;
        this.afm = afm;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }


    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getMail() {
        return mail;
    }

    public void setMail(String mail) {
        this.mail = mail;
    }

    public String getAfm() {
        return afm;
    }

    public void setAfm(String afm) {
        this.afm = afm;
    }
}
