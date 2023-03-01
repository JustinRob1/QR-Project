package com.example.qr_project;

import java.util.ArrayList;
import java.util.Collection;

public class Player {
    private String username;
    private String email;
    private String userID;
    private int phoneNumber;
    private ArrayList<QR_Code> QRCodes;
    private int totalScore;

    public Player(String username, String email, int phoneNumber, ArrayList<QR_Code> QRCodes, int totalScore,
                    String userID) {
        this.username = username;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.QRCodes = QRCodes;
        this.totalScore = totalScore;
        this.userID = userID;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public int getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(int phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public ArrayList<QR_Code> getQRCodes() {
        return QRCodes;
    }

    public void setQRCodes(ArrayList<QR_Code> QRCodes) {
        this.QRCodes = QRCodes;
    }

    public int getTotalScore() {
        return totalScore;
    }

    public void setTotalScore(int totalScore) {
        this.totalScore = totalScore;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void addQrCode(QR_Code qrCode) {
        QRCodes.add(qrCode);
        totalScore = totalScore + qrCode.getScore();
    }

    public void deleteQrCode(QR_Code qrCode) {
        QRCodes.remove(qrCode);
        totalScore = totalScore - qrCode.getScore();
    }
}
