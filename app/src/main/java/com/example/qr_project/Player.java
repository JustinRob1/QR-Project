package com.example.qr_project;

import java.util.ArrayList;
import java.util.Collection;

public class Player {
    private String username; // Stores the username
    private String email; // Stores the users email
    private String userID; // Stores the userID
    private int phoneNumber; // Stores the phone number
    private ArrayList<QR_Code> QRCodes; // Array list to store the QR codes
    private int totalScore; // Stores the total scroe

    /**
     * Constructor for the player
     * @param username
     * @param email
     * @param phoneNumber
     * @param QRCodes
     * @param totalScore
     * @param userID
     */
    public Player(String username, String email, int phoneNumber, ArrayList<QR_Code> QRCodes, int totalScore,
                    String userID) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.QRCodes = QRCodes;
        this.totalScore = totalScore;
        this.userID = userID;
    }

    /**
     * @return
     *      Username of the QR code
     */
    public String getUsername() {
        return username;
    }

    /**
     * Sets the username
     * @param username
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @return
     *      Email of the user
     */
    public String getEmail() {
        return email;
    }

    /**
     * Sets the email of the user
     * @param email
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return
     *      Phone number of the user
     */
    public int getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number
     * @param phoneNumber
     */
    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * @return
     *      User's QR codes
     */
    public ArrayList<QR_Code> getQRCodes() {
        return QRCodes;
    }

    /**
     * @return
     *      Total score of all QR codes
     */
    public int getTotalScore() {
        return totalScore;
    }

    /**
     * Sets the total score of the QR codes
     * @param totalScore
     */
    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    /**
     * @return
     *      User's ID
     */
    public String getUserID() {
        return userID;
    }

    /**
     * Sets the user's ID
     * @param userID
     */
    public void setUserID(String userID) {
        this.userID = userID;
    }

    /**
     * Adds the qrCode to the array list and adds the QR code's score to total score
     * @param qrCode
     */
    public void addQrCode(QR_Code qrCode) {
        QRCodes.add(qrCode);
        totalScore = totalScore + qrCode.getScore();
    }

    /**
     * Deletes the qrCode from the array list and subtracts the QR code's score to total score
     * @param qrCode
     */
    public void deleteQrCode(QR_Code qrCode) {
        QRCodes.remove(qrCode);
        totalScore = totalScore - qrCode.getScore();
    }
}
