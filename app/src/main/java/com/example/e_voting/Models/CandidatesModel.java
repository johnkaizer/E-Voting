package com.example.e_voting.Models;

public class CandidatesModel {
    String image;
    String fullName;
    String party;
    String Category;

    public CandidatesModel() {
    }

    public CandidatesModel(String image, String fullName, String party, String category) {
        this.image = image;
        this.fullName = fullName;
        this.party = party;
        Category = category;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }


    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
