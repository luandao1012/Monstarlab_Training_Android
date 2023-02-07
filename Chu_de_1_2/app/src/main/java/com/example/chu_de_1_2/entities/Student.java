package com.example.chu_de_1_2.entities;

public class Student {
    private String name, year, phone, specialized, type;

    public Student(String name, String year, String phone, String specialized, String type) {
        this.name = name;
        this.year = year;
        this.phone = phone;
        this.specialized = specialized;
        this.type = type;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getSpecialized() {
        return specialized;
    }

    public void setSpecialized(String specialized) {
        this.specialized = specialized;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public boolean searchStudent(String word) {
        if (this.name.toLowerCase().contains(word) || this.phone.toLowerCase().contains(word)
                || this.year.toLowerCase().contains(word) || this.specialized.toLowerCase().contains(word)
                || this.type.toLowerCase().contains(word)) {
            return true;
        }
        return false;
    }
}
