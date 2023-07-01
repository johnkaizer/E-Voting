package com.example.e_voting.Models;

public class VotesModel {
    String name;
    String category;
    String party;
    String userUid;

    public VotesModel() {
    }

    public VotesModel( String name, String category, String party, String userUid) {
        this.name = name;
        this.category = category;
        this.party = party;
        this.userUid = userUid;
    }


    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getParty() {
        return party;
    }

    public void setParty(String party) {
        this.party = party;
    }

    public String getUserUid() {
        return userUid;
    }

    public void setUserUid(String userUid) {
        this.userUid = userUid;
    }
}
