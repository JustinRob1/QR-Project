package com.example.qr_project;

/*
   FireStore can store the QR Codes in the collection online and remotely.
   Getting the data from the user and then uploading the date online right away to the collection
   Every QR Code will be stored online on Cloud FireBase
 */


public class FireStore {

    private static FireStore getInstance() {
        return null;
    }

    // Access a Cloud Firestore instance from your Activity
    FireStore db = FireStore.getInstance();


}
