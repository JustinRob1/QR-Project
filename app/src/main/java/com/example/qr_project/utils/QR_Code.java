package com.example.qr_project.utils;

import android.graphics.Bitmap;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;
import java.util.Map;

public class QR_Code {
    private Hash hash; // Stores the hash
    private int score; // Stores the score of the QR code
    private String name; // Stores the QR codes name
    private Bitmap photo; // Stores the photo taken
    private GeoPoint location;

    /**
     * Constructor for QR Code
     *
     * @param hash
     * @param photo
     */
    public QR_Code(Hash hash, Bitmap photo, GeoPoint location) {
        this.hash = hash;
        this.score = (hash == null) ? 0 : hash.getScore();
        this.name = (hash == null) ? "" : hash.getName();
        this.photo = photo;
        this.location = location;
    }

    /**
     * @return Hash object of the QR code
     */
    public Hash getHash() {
        return hash;
    }

    /**
     * Sets the hash object
     *
     * @param hash
     */
    public void setHash(Hash hash) {
        this.hash = hash;
    }

    /**
     * @return Score of the QR Code
     */
    public int getScore() {
        return score;
    }

    /**
     * @return score
     */
    public GeoPoint getLocation() {
        return location;
    }

    /** 
        * Sets the location
        * @param location
     */
    public void setLocation(GeoPoint location) {
        this.location = location;
    }

    /**
     * @return Name of the QR code
     */
    public String getName() {
        return name;
    }

    /**
     * @return Photo of QR code
     */
    public Bitmap getPhoto() {
        return photo;
    }

    /**
     * Sets the photo
     *
     * @param photo
     */
    public void setPhoto(Bitmap photo) {
        this.photo = photo;
    }


    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("hash", hash);
        result.put("score", score);
        result.put("name", name);
        result.put("photo", photo);
        return result;
    }
}