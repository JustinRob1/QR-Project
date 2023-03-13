package com.example.qr_project;

import android.graphics.Bitmap;

import com.example.qr_project.utils.Hash;
import com.example.qr_project.utils.QR_Code;
import com.google.firebase.firestore.GeoPoint;

import org.junit.jupiter.api.Test;

public class QR_CodeTest {

    // TODO:
    //  1. In future, add two QR_Code constructors so that it could be created with or without
    //  photo and location.
    //  2. Also, refactor QRCode s.t. Hash object is created inside it
    @Test void testConstructors(){
        // Case 1: QRContents are received, but no Photo and Location were given
        Hash mockHash = new Hash("0 == Work done during the Reading Week");
        QR_Code qrCode1 = new QR_Code(mockHash, null, null);

        // Will be tested once QRCode is refactored
        /*
        // Case 2: QRContents, Photo, and Location were given.
        Bitmap Photo = Bitmap.createBitmap(100, 100, Bitmap.Config.ARGB_8888);
        GeoPoint Point = new GeoPoint(34.5, 54.5);
        QR_Code qrCode2 = new QR_Code(mockHash, Photo, Point);
        */

    }
}

