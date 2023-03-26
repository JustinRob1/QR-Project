package com.example.qr_project;

import com.example.qr_project.utils.QR_Code;
import com.google.firebase.firestore.GeoPoint;

import org.junit.Before;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.*;

import java.util.HashMap;
import java.util.Map;

public class QR_CodeTest {

    private String content1 = "Vox populi, vox dei";
    private String content2 = "Dura lex, sed lex";

    // Returns a QRCode w/o a photo & location
    private QR_Code mockQR_Code1(){
        return new QR_Code(content1);
    }

    // Returns a QRCode w/ photo & location
    private QR_Code mockQR_Code2(){
        // Bitmaps can't be created easily here, pass null instead.
        GeoPoint Point = new GeoPoint(34.5, 54.5);
        return new QR_Code(content2, null, Point);
    }
    @Test void testContentsNotSaved(){
        QR_Code qrCode1 = mockQR_Code1();
        QR_Code qrCode2 = mockQR_Code2();

        // Contents should never be stored in qrCodes
        assertNotEquals(content1, qrCode1.getHash());
        assertNotEquals(content2, qrCode2.getHash());

        assertNotEquals(content1, qrCode1.getName());
        assertNotEquals(content2, qrCode2.getName());
    }

    @Test
    void testGetScore(){
        // QR codes should not have a zero score, unless NoSuchAlgorithm
        // exception within Hash implementation is thrown
        QR_Code qrCode1 = mockQR_Code1();
        QR_Code qrCode2 = mockQR_Code2();

        assertNotEquals(0, qrCode1.getScore());
        assertNotEquals(0, qrCode2.getScore());
    }

    @Test
    void testToMap(){
        QR_Code qrCode1 = mockQR_Code1();
        QR_Code qrCode2 = mockQR_Code2();


        int score1 = qrCode1.getScore();
        int score2 = qrCode2.getScore();

        String hash1 = qrCode1.getHash();
        String hash2 = qrCode2.getHash();

        String name1 = qrCode1.getName();
        String name2 = qrCode2.getName();

        HashMap<String, Object> map1 = qrCode1.toMap();
        HashMap<String, Object> map2 = qrCode2.toMap();

        assertEquals(map1.get("id"), hash1);
        assertEquals(map1.get("score"), score1);
        assertEquals(map1.get("name"), name1);
        assertNull(map1.get("photo"));

        assertEquals(map2.get("id"), hash2);
        assertEquals(map2.get("score"), score2);
        assertEquals(map2.get("name"), name2);


    }

    @Test
    void testGetters(){
        QR_Code qrCode1 = mockQR_Code1();
        QR_Code qrCode2 = mockQR_Code2();

        System.out.println(qrCode1.getFace());
        System.out.println(qrCode2.getFace());

        System.out.println(qrCode1.getName());
        System.out.println(qrCode2.getName());

        System.out.println(qrCode1.getHash());
        System.out.println(qrCode2.getHash());
    }


}

