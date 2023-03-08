package com.example.qr_project;

public class QR_Code {
    // TODO
    // How to hash
    private String hash; // Stores the hash
    private int score; // Stores the score of the QR code
    private String name; // Stores the QR codes name
    private Object photo; // Stores the photo taken

    /**
     * Constructor for QR Code
     *
     * @param hash
     * @param score
     * @param name
     * @param photo
     */
    public QR_Code(String hash, int score, String name, Object photo) {
        this.hash = hash;
        this.score = score;
        this.name = name;
        this.photo = photo;
    }

    /**
     * @return
     *      Hash of the QR code
     */
    public String getHash() {
        return hash;
    }

    /**
     * Sets the hash
     * @param hash
     */
    public void setHash(String hash) {
        this.hash = hash;
    }

    /**
     * @return
     *      Score of the QR Code
     */
    public int getScore() {
        return score;
    }

    /**
     * Sets the score of the QR code
     * @param score
     */
    public void setScore(int score) {
        this.score = score;
    }

    /**
     * @return
     *      Name of the QR code
     */
    public String getName() {
        return name;
    }

    /**
     * Sets the name of the QR code
     * @param name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return
     *      Photo of QR code
     */
    public Object getPhoto() {
        return photo;
    }

    /**
     * Sets the photo
     * @param photo
     */
    public void setPhoto(Object photo) {
        this.photo = photo;
    }
}
