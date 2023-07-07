package com.example.e_voting.Models;

public class CandidatesModel {
    String ImageUrl;
    String fullName;
    String party;
    String Category;

    public CandidatesModel() {
    }

    public CandidatesModel(String imageUrl, String fullName, String party, String category) {
        ImageUrl = imageUrl;
        this.fullName = fullName;
        this.party = party;
        Category = category;
    }

    public String getImageUrl() {
        return ImageUrl;
    }

    public void setImageUrl(String imageUrl) {
        ImageUrl = imageUrl;
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
