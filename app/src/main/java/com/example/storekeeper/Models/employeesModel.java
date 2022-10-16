package com.example.storekeeper.Models;

public class employeesModel {
    int code;
    String name;
    String surname;
    String phone;
    String mobile;
    String mail;
    String work;
    String id;

    public employeesModel(int code, String name, String surname, String phone, String mobile, String mail, String work, String id) {
        this.code = code;
        this.name = name;
        this.surname = surname;
        this.phone = phone;
        this.mobile = mobile;
        this.mail = mail;
        this.work = work;
        this.id = id;
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

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
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

    public String getWork() {
        return work;
    }

    public void setWork(String work) {
        this.work = work;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
