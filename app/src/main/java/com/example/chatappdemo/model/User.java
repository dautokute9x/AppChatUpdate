package com.example.chatappdemo.model;


public class User{
    private String imgAnhBia, imgAnhDD, name, status, gioiTinh, phone;

    public User() {}

    public User(String imgAnhBia, String imgAnhDD, String name, String status, String gioiTinh, String phone) {
        this.imgAnhBia = imgAnhBia;
        this.imgAnhDD = imgAnhDD;
        this.name = name;
        this.status = status;
        this.gioiTinh = gioiTinh;
        this.phone = phone;
    }

    public String getImgAnhBia() {
        return imgAnhBia;
    }

    public void setImgAnhBia(String imgAnhBia) {
        this.imgAnhBia = imgAnhBia;
    }

    public String getImgAnhDD() {
        return imgAnhDD;
    }

    public void setImgAnhDD(String imgAnhDD) {
        this.imgAnhDD = imgAnhDD;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getGioiTinh() {
        return gioiTinh;
    }

    public void setGioiTinh(String gioiTinh) {
        this.gioiTinh = gioiTinh;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }
}
