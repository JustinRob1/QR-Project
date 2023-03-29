package com.example.qr_project.utils;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String username; // Stores the username
    private String email; // Stores the users email
    private String userID; // Stores the userID
    private String phoneNumber; // Stores the phone number
    private List<QR_Code> QRCodes; // Array list to store the QR codes
    private List<Player> friends; // Array list to store the friends
    private int totalScore; // Stores the total score

    /**
     * Constructor for the player. Note that the constructor does not check for the right format
     * of each of the parameters.
     * Getting and initialising the parameters needede to create an account for the user
     *
     * @param username: Player's nickname
     * @param email: Player's email address
     * @param phoneNumber: Player's phone number
     * @param userID: Player's ID generated for DB
     */
    public Player(String username, String email, String phoneNumber, String userID) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.userID = userID;

        this.QRCodes = new ArrayList<>();
        // Array list to store the friends list
        this.friends = new ArrayList<>();
        this.totalScore = 0;
    }

    /** Setting up the username of the user
     * The username with the type of String
     * @return
     *      Username of the QR code
     */
    public String getUsername() {
        return username;
    }

    /** Setting up the username of the user
     * The user is prompted to type in their
     * Sets the username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /** Setting up the email of the user
     * The email with the type of String
     * @return
     *      Email of the user
     */
    public String getEmail() {
        return email;
    }

    /** The user is prompted to type in their email
     * Sets the email of the user
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * Getting the retrieving the user's phone number
     * @return
     *      Phone number of the user
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * The phone number has a type of String
     * The user can enter their phone number
     * Sets the user's phone number
     * @param phoneNumber
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    // Note: A reference to the original QRCodes array list is returned.
    /**
     * The QRCodes displaying in List
     * The QRCodes can scanned when the player wishes to do so
     * Sets the QR_CODES
     * @return
     *      User's QR codes
     */
    public List<QR_Code> getQRCodes() {
        return QRCodes;
    }

    /**
     * Calculate the total score of all the QR_Codes that the player has
     * Re-Calculate the total score when a QR_Codes is added or removed
     * @return
     *      Total score of all QR codes
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * The ID has the type of String
     * This is to initalise the ID of the user
     * @return
     *      User's ID
     */
    public String getUserID() {
        return userID;
    }

    public List<Player> getFriends() {
        return friends;
    }

    /**
     * Setting up the email of the user
     * The user's ID  with the type of String
     * Sets the user's ID
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Adds the qrCode to the array list and adds the QR code's score to total score
     * This will also re-calculate the total score of the player's
     * @param qrCode
     */
    public void addQRCode(QR_Code qrCode) {
        QRCodes.add(qrCode);
        totalScore = totalScore + qrCode.getScore();
    }

    /**
     * Allows the user to delete their unwanted QR_Code
     * Deletes the qrCode from the array list and subtracts the QR code's score to total score
     * This will also re-calculate the total score of the player's
     * @param qrCode
     */
    public void deleteQrCode(QR_Code qrCode) {
        QRCodes.remove(qrCode);
        totalScore = totalScore - qrCode.getScore();
    }
}