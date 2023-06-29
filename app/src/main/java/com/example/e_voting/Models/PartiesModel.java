package com.example.e_voting.Models;

public class PartiesModel {
    int logo;
    String title;
    int description;

    public PartiesModel(int logo, String title, int description) {
        this.logo = logo;
        this.title = title;
        this.description = description;
    }

    public int getLogo() {
        return logo;
    }

    public void setLogo(int logo) {
        this.logo = logo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public int getDescription() {
        return description;
    }

    public void setDescription(int description) {
        this.description = description;
    }
}
