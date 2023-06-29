package com.example.e_voting.Models;

public class CandidatesModel {
    String image;
    String fullName;
    String party;
    String partyLogo;
    String votes;
    String Category;

    public CandidatesModel() {
    }

    public CandidatesModel(String image, String fullName, String party, String partyLogo, String votes, String category) {
        this.image = image;
        this.fullName = fullName;
        this.party = party;
        this.partyLogo = partyLogo;
        this.votes = votes;
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

    public String getPartyLogo() {
        return partyLogo;
    }

    public void setPartyLogo(String partyLogo) {
        this.partyLogo = partyLogo;
    }

    public String getVotes() {
        return votes;
    }

    public void setVotes(String votes) {
        this.votes = votes;
    }

    public String getCategory() {
        return Category;
    }

    public void setCategory(String category) {
        Category = category;
    }
}
