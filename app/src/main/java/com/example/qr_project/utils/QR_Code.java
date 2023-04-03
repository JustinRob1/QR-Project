package com.example.qr_project.utils;

import android.graphics.Bitmap;

import com.google.firebase.firestore.GeoPoint;

import java.util.HashMap;

public class QR_Code {
    private Hash hash; // Stores the hash

    private String hash_code;
    private int score; // Stores the score of the QR code
    private String name; // Stores the QR codes name
    private Bitmap face; // Stores the QR code face
    private Bitmap photo; // Stores the photo taken
    private GeoPoint location;

    private String photo_url;

    private boolean scannedByUser;

    /**
     * Constructor for QR Code without photo and location.
     *
     * @param qrCodeContent: String contained in physical QRCode. It's used for generating hash only
     */
    public QR_Code(String qrCodeContent){
        this.hash = new Hash(qrCodeContent);
        this.score = hash.getScore();
        this.name = hash.getName();
        this.face = hash.getFace();
        this.photo = null;
        this.location = null;
    }

    public QR_Code(int score, String name, Bitmap face, String photo, GeoPoint location, String hash_code, boolean scannedByUser){
        this.score = score;
        this.name = name;
        this.face = face;
        this.photo_url = photo;
        this.location = location;
        this.hash_code = hash_code;
        this.scannedByUser = scannedByUser;
    }

    public QR_Code(int score, String name, String face, String hash_code){
        this.score = score;
        this.name = name;
        this.face = face;
        this.hash_code = hash_code;
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
        this.face = hash.getFace();
        this.photo = photo;
        this.location = location;
    }

    /**
     * TESTING: CONSTRUCTOR FOR QR CODE RETRIEVED FROM DB
     * TO BE REMOVED
     * Implemented by akhadeli
     */
    public QR_Code(Hash hash, int score, String name, Bitmap face) {
        this.hash = hash;
        this.score = score;
        this.name = name;
        this.face = face;
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

    public String getHash_code(){return this.hash_code;}

    /**
     *
     * @return generated face string from generated hash
     */
    public Bitmap getFace() {
        return face;
    }

    /**
     *
     */
    public void setFace() {
        this.face = null;
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