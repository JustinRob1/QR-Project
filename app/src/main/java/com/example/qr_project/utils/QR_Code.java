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
     * Constructor for QR Code without photo and location.
     *
     * @param qrCodeContent: String contained in physical QRCode. It's used for generating hash only
     */
    public QR_Code(String qrCodeContent){
        this.hash = new Hash(qrCodeContent);
        this.score = hash.getScore();
        this.name = hash.getName();
        this.photo = null;
        this.location = null;
    }

    /**
     * Constructor for QR Code with photo and location.
     *
     * @param qrCodeContent: String contained in physical QRCode. It's used for generating hash only
     * @param photo: Bitmap of the whereabouts of the physical QRCode
     * @param location: Location of the photo
     */
    public QR_Code(String qrCodeContent, Bitmap photo, GeoPoint location) {
        this.hash = new Hash(qrCodeContent);
        this.score = hash.getScore();
        this.name = hash.getName();
        this.photo = photo;
        this.location = location;
    }

    /**
     * @return Score of the QR Code
     */
    public int getScore() {
        return score;
    }

    /**
     *
     * @return generated hash string from QRCode content.
     */
    public String getHash(){
        return this.hash.getHash();
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


    public HashMap<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", hash.getHash());
        result.put("score", score);
        result.put("name", name);
        result.put("photo", photo);
        result.put("Location", location);
        return result;
    }
}