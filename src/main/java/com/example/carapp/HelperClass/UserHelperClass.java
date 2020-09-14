package com.example.carapp.HelperClass;

public class UserHelperClass {

    String name,email,phone,password;
    String profile;

    public UserHelperClass() {
    }

    public UserHelperClass(String name, String email, String phone, String password, String profile) {
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.profile = profile;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }
}
