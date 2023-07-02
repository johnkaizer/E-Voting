package com.example.e_voting.Models;

public class Polls {
    String title;
    String pTime;
    String pDate;

    public Polls() {
    }

    public Polls(String title, String pTime, String pDate) {
        this.title = title;
        this.pTime = pTime;
        this.pDate = pDate;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getpTime() {
        return pTime;
    }

    public void setpTime(String pTime) {
        this.pTime = pTime;
    }

    public String getpDate() {
        return pDate;
    }

    public void setpDate(String pDate) {
        this.pDate = pDate;
    }
}
