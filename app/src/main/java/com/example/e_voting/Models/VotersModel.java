package com.example.e_voting.Models;

public class VotersModel {
    String imageUrl;
    String fName;
    String sName;
    String idNumber;
    String gender;
    String Uid;

    public VotersModel() {
    }

    public VotersModel(String imageUrl, String fName, String sName, String idNumber, String gender, String uid) {
        this.imageUrl = imageUrl;
        this.fName = fName;
        this.sName = sName;
        this.idNumber = idNumber;
        this.gender = gender;
        Uid = uid;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getfName() {
        return fName;
    }

    public void setfName(String fName) {
        this.fName = fName;
    }

    public String getsName() {
        return sName;
    }

    public void setsName(String sName) {
        this.sName = sName;
    }

    public String getIdNumber() {
        return idNumber;
    }

    public void setIdNumber(String idNumber) {
        this.idNumber = idNumber;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getUid() {
        return Uid;
    }

    public void setUid(String uid) {
        Uid = uid;
    }
}
