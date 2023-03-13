package com.example.qr_project;

/*
   FireStore can store the QR Codes in the collection online and remotely.
   Getting the data from the user and then uploading the date online right away to the collection
   Every QR Code will be stored online on Cloud FireBase
 */

public class FireStore {

    /** The initialization of FireBase FireStore.
     * Linking the FireBase FireStore to this android app,
     * so that the data can be stored online and secured.
     * From the data of the user to the data of the QR_Code, they are stored on the FireBase Cloud.
     * Also the FireStore has a functionality of getting, storing, and retrieving data
     * This FireStore FireBase also has a function of getting and storing the data RealTime Database
     * @return null
     */
    private static FireStore getInstance() {
        return null;
    }

    // Access a Cloud Firestore instance from your Activity
    FireStore db = FireStore.getInstance();


}
