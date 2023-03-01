package com.example.qr_project;

import android.provider.ContactsContract;

public class QR_Code {
    // TODO
    // How to hash
    private String hash;
    private int score;
    private String name;
    private Object photo;

    public QR_Code(String hash, int score, String name, Object photo) {
        this.hash = hash;
        this.score = score;
        this.name = name;
        this.photo = photo;
    }

    public String getHash() {
        return hash;
    }

    public void setHash(String hash) {
        this.hash = hash;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getPhoto() {
        return photo;
    }

    public void setPhoto(Object photo) {
        this.photo = photo;
    }
}
