package com.example.chatappdemo;

public class User {
    private String email_signup;
    private String password_signup;
    private String gioitinh_signup;
    private String name_signup;
    private String phone_signup;
    private String image;

    public  User(){}
    public User(String email, String password) {
        this.email_signup = email;
        this.password_signup =password;
    }

    public User(String email, String password, String gioitinh, String name, String phone, String image) {
        this.email_signup = email;
        this.password_signup = password;
        this.gioitinh_signup = gioitinh;
        this.name_signup = name;
        this.phone_signup = phone;
        this.image = image;
    }

    public String getEmail_signup() {
        return email_signup;
    }

    public void setEmail_signup(String email_signup) {
        this.email_signup = email_signup;
    }

    public String getPassword_signup() {
        return password_signup;
    }

    public void setPassword_signup(String password_signup) {
        this.password_signup = password_signup;
    }

    public String getGioitinh_signup() {
        return gioitinh_signup;
    }

    public void setGioitinh_signup(String gioitinh_signup) {
        this.gioitinh_signup = gioitinh_signup;
    }

    public String getName_signup() {
        return name_signup;
    }

    public void setName_signup(String name_signup) {
        this.name_signup = name_signup;
    }

    public String getPhone_signup() {
        return phone_signup;
    }

    public void setPhone_signup(String phone_signup) {
        this.phone_signup = phone_signup;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }
}
