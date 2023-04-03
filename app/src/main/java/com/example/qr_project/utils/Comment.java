package com.example.qr_project.utils;

public class Comment {

    String userName;
    String comment;

    String userID;

    String hash_code;

    public Comment(String userID, String userName, String comment, String hash_code){
        this.userID = userID;
        this.userName = userName;
        this.comment = comment;
        this.hash_code = hash_code;
    }

    public String getHash_code(){
        return hash_code;
    }

    public String getUserName() {
        return userName;
    }


    public String getUserID() {
        return userID;
    }

    public String getComment() {
        return comment;
    }

}
