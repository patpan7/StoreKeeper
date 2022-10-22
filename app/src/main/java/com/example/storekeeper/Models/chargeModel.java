package com.example.storekeeper.Models;

public class chargeModel
{
    String date;
    String name;
    String surname;

    public chargeModel(String date, String name, String surname) {
        this.date = date;
        this.name = name;
        this.surname = surname;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
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
}
