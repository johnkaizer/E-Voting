package com.example.e_voting.Models;

public class User {
    public String fullName, email, phone, password;
    public boolean additionalInfoRegistered; // Flag indicating if additional information is registered

    public User() {
    }

    public User(String fullName, String email, String phone, String password) {
        this.fullName = fullName;
        this.email = email;
        this.phone = phone;
        this.password = password;
        this.additionalInfoRegistered = false; // Initialize the flag to false
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
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

    public boolean isAdditionalInfoRegistered() {
        return additionalInfoRegistered;
    }

    public void setAdditionalInfoRegistered(boolean additionalInfoRegistered) {
        this.additionalInfoRegistered = additionalInfoRegistered;
    }
}
