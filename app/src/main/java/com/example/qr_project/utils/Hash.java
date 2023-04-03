package com.example.qr_project.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.graphics.drawable.shapes.Shape;
import android.util.Log;

import com.google.firestore.admin.v1.Index;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

/**
 * Contains hash from QRCode contents and score based on the hash value. Note that
 * the contents of QRCode are not stored in this class, but used only for generating hash.
 */
public class Hash {
    private String hash;
    private String name;
    private Bitmap face;

    private int score;
    private final static String TAG = "HASH";

    private static final int IMAGE_SIZE = 256; // the size of the generated image
    private static final int BACKGROUND_COLOR = Color.WHITE;
    private static final int EYE_COLOR = Color.BLACK;
    private static final int NOSE_COLOR = Color.RED;
    private static final int MOUTH_COLOR = Color.BLUE;
    private static final int EYEBROW_COLOR = Color.BLACK;

    /**
     * Creates an instance of Hash object.
     * @param qrCodeContent a String contained in the QRCode
     */
    public Hash(String qrCodeContent) {
        this.hash = generateHash(qrCodeContent);
        this.name = generateName(this.hash);
        this.face = generateFace(this.hash);
        this.score = calculateScore(this.hash);
    }

    /**
     * TESTING: CONSTRUCTOR FOR QR CODE RETRIEVED FROM DB
     * TO BE REMOVED
     * Implemented by akhadeli
     */
    public Hash(String hash, String name, Bitmap face, int score) {
        this.hash = hash;
        this.name = name;
        this.face = face;
        this.score = score;
    }

    /**
     * @return The score of the hash
     */
    public int getScore() {
        return score;
    }

    /**
     * @return Hash that is stored
     */
    public String getHash() {
        return hash;
    }

    /**
     * @return name generated from hash
     */
    public String getName() {
        return name;
    }

    /**
     * @return face generated from hash
     */
    public Bitmap getFace() {
        return face;
    }

    /**
     * Returns a hash for a given string
     * @param str A string to be hashed
     * @return A hash of the string
     */
    private static String generateHash(String str){
        try {
            /*
             * Comment: The following conversion from str to hash was taken from tutorial
             * Author: bilal-hungund
             * Website: https://www.geeksforgeeks.org/sha-256-hash-in-java/
             * */

            // Hash String to Bytes via MessageDigest
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] bytes = md.digest(str.getBytes(StandardCharsets.UTF_8));

            // Convert byte array into signum representation
            BigInteger number = new BigInteger(1, bytes);

            // Convert message digest into hex value
            StringBuilder hash = new StringBuilder(number.toString(16));

            // Pad with leading zeros
            while (hash.length() < 64) {
                hash.insert(0, '0');
            }

            return hash.toString();

        }
        catch (NoSuchAlgorithmException e){
            Log.e(TAG, "Exception was thrown for incorrect algorithm: " + e);
            return "";
        }

    }

    /**
     * Calculates scores of the hash
     * @param hash a hash string
     * @return integer value of score
     */
    private static int calculateScore(String hash){
        int n = hash.length();
        int i = 0;
        int j;
        int k; // # of occurrences of the same letter in a row. I.e. 1 for a, 3 for aaa etc
        int score = 0;
        int base;


        while (i < n){
            j = i + 1;
            k = 1;
            while (j < n && hash.charAt(i) == hash.charAt(j)) {
                j++;
                k++;
            }
            if (hash.charAt(i) == '0'){
                base = 20;
            } else{
                base = "0123456789abcdef".indexOf(hash.charAt(i));
            }
            score += Math.pow(base, k-1);
            i = j;
        }

        return score;
    }

    /**
     * Generates a face based on the hash
     * @return a face
     */
    public static Bitmap generateFace(String hash) {
        // Create a new bitmap for the image
        Bitmap bitmap = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // Draw the background
        canvas.drawColor(Color.WHITE);

        // Define the available face shapes
        String[] shapes = {"circle", "square"};

        // Determine the face shape based on the hash
        int shapeIndex = Math.abs(hash.hashCode()) % 2;
        String faceShape = shapes[shapeIndex];

        // Generate a random face color based on the hash
        Random random = new Random(hash.hashCode());
        int faceColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        int eyeColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        int mouthColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        int noseColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));
        int browColor = Color.rgb(random.nextInt(256), random.nextInt(256), random.nextInt(256));

        // Draw the face shape with the random face color and a hollow interior
        Paint facePaint = new Paint();
        facePaint.setColor(faceColor);
        facePaint.setStyle(Paint.Style.STROKE);
        facePaint.setStrokeWidth(20);

        if (faceShape.equals("circle")) {
            RectF oval = new RectF(0, 0, IMAGE_SIZE, IMAGE_SIZE);
            canvas.drawOval(oval, facePaint);
        } else if (faceShape.equals("square")) {
            RectF square = new RectF(0, 0, IMAGE_SIZE, IMAGE_SIZE);
            canvas.drawRect(square, facePaint);
        }

        String[] eyes = {"round", "almond", "hooded"};

        // Determine the eye shape based on the hash
        int eyesIndex = Math.abs(hash.hashCode()) % 3;
        String eyeShape = eyes[eyesIndex];

        // Define the eye paint with the random eye color
        Paint eyePaint = new Paint();
        eyePaint.setColor(eyeColor);
        eyePaint.setStyle(Paint.Style.FILL);

        // Draw the eyes based on the selected eye shape
        if (eyeShape.equals("round")) {
            // Draw round eyes
            float leftEyeX = IMAGE_SIZE * 0.3f;
            float leftEyeY = IMAGE_SIZE * 0.4f;
            float leftEyeRadius = IMAGE_SIZE * 0.1f;
            canvas.drawCircle(leftEyeX, leftEyeY, leftEyeRadius, eyePaint);

            float rightEyeX = IMAGE_SIZE * 0.7f;
            float rightEyeY = IMAGE_SIZE * 0.4f;
            float rightEyeRadius = IMAGE_SIZE * 0.1f;
            canvas.drawCircle(rightEyeX, rightEyeY, rightEyeRadius, eyePaint);
        } else if (eyeShape.equals("almond")) {
            // Draw almond eyes
            float leftEyeX = IMAGE_SIZE * 0.3f;
            float leftEyeY = IMAGE_SIZE * 0.4f;
            float leftEyeRadiusX = IMAGE_SIZE * 0.2f;
            float leftEyeRadiusY = IMAGE_SIZE * 0.1f;
            canvas.drawOval(leftEyeX - leftEyeRadiusX, leftEyeY - leftEyeRadiusY, leftEyeX + leftEyeRadiusX, leftEyeY + leftEyeRadiusY, eyePaint);

            float rightEyeX = IMAGE_SIZE * 0.7f;
            float rightEyeY = IMAGE_SIZE * 0.4f;
            float rightEyeRadiusX = IMAGE_SIZE * 0.2f;
            float rightEyeRadiusY = IMAGE_SIZE * 0.1f;
            canvas.drawOval(rightEyeX - rightEyeRadiusX, rightEyeY - rightEyeRadiusY, rightEyeX + rightEyeRadiusX, rightEyeY + rightEyeRadiusY, eyePaint);
        } else if (eyeShape.equals("hooded")) {
            // Draw hooded eyes
            float leftEyeX = IMAGE_SIZE * 0.3f;
            float leftEyeY = IMAGE_SIZE * 0.4f;
            float leftEyeRadiusX = IMAGE_SIZE * 0.2f;
            float leftEyeRadiusY = IMAGE_SIZE * 0.1f;
            canvas.drawOval(leftEyeX - leftEyeRadiusX, leftEyeY - leftEyeRadiusY, leftEyeX + leftEyeRadiusX, leftEyeY + leftEyeRadiusY, eyePaint);
            canvas.drawLine(leftEyeX - leftEyeRadiusX, leftEyeY, leftEyeX + leftEyeRadiusX, leftEyeY, eyePaint);

            float rightEyeX = IMAGE_SIZE * 0.7f;
            float rightEyeY = IMAGE_SIZE * 0.4f;
            float rightEyeRadiusX = IMAGE_SIZE * 0.2f;
            float rightEyeRadiusY = IMAGE_SIZE * 0.1f;
            canvas.drawOval(rightEyeX - rightEyeRadiusX, rightEyeY - rightEyeRadiusY, rightEyeX + rightEyeRadiusX, rightEyeY + rightEyeRadiusY, eyePaint);
            canvas.drawLine(leftEyeX - leftEyeRadiusX, leftEyeY, leftEyeX + leftEyeRadiusX, leftEyeY, eyePaint);
        }

        String[] mouths = {"smile", "frown", "surprised"};

        int mouthIndex = Math.abs(hash.hashCode()) % 3;
        String mouthShape = mouths[mouthIndex];

        // Define the mouth paint with the random mouth color
        Paint mouthPaint = new Paint();
        mouthPaint.setColor(mouthColor);
        mouthPaint.setStyle(Paint.Style.FILL);

        // Draw the mouth shape with the random mouth color
        if (mouthShape.equals("smile")) {
            Path smilePath = new Path();
            smilePath.moveTo(IMAGE_SIZE / 4, IMAGE_SIZE * 3 / 4);
            smilePath.quadTo(IMAGE_SIZE / 2, IMAGE_SIZE * 7 / 8, IMAGE_SIZE * 3 / 4, IMAGE_SIZE * 3 / 4);
            canvas.drawPath(smilePath, mouthPaint);
        } else if (mouthShape.equals("frown")) {
            Path frownPath = new Path();
            frownPath.moveTo(IMAGE_SIZE / 4, IMAGE_SIZE * 3 / 4);
            frownPath.quadTo(IMAGE_SIZE / 2, IMAGE_SIZE * 5 / 8, IMAGE_SIZE * 3 / 4, IMAGE_SIZE * 3 / 4);
            canvas.drawPath(frownPath, mouthPaint);
        } else if (mouthShape.equals("surprised")) {
            float mouthRadius = IMAGE_SIZE / 12;
            float mouthCenterX = IMAGE_SIZE / 2;
            float mouthCenterY = (float) (IMAGE_SIZE * 3 / 3.75);
            canvas.drawCircle(mouthCenterX, mouthCenterY, mouthRadius, mouthPaint);
        }

        String[] noses = {"pointed", "button"};

        // Determine the nose shape based on the hash
        int noseIndex = Math.abs(hash.hashCode()) % 2;
        String noseShape = noses[noseIndex];

        // Draw the nose shape with the random nose color and a hollow interior
        Paint nosePaint = new Paint();
        nosePaint.setColor(noseColor);
        nosePaint.setStyle(Paint.Style.STROKE);
        nosePaint.setStrokeWidth(5);

        if (noseShape.equals("pointed")) {
            Path path = new Path();
            path.moveTo(IMAGE_SIZE / 2, IMAGE_SIZE / 2);
            path.lineTo(IMAGE_SIZE * 3 / 8, IMAGE_SIZE * 5 / 8);
            path.lineTo(IMAGE_SIZE * 5 / 8, IMAGE_SIZE * 5 / 8);
            path.close();
            canvas.drawPath(path, nosePaint);
        } else if (noseShape.equals("button")) {
            canvas.drawCircle(IMAGE_SIZE / 2, IMAGE_SIZE * 5 / 8, IMAGE_SIZE / 20, nosePaint);
        }

        // Define the available eyebrow shapes
        String[] eyebrows = {"straight", "unibrow", "slanted"};

        // Determine the eyebrow shape based on the hash
        int eyebrowIndex = Math.abs(hash.hashCode()) % 3;
        String eyebrowShape = eyebrows[eyebrowIndex];

        // Define the eyebrow paint with the random eye color
        Paint eyebrowPaint = new Paint();
        eyebrowPaint.setColor(browColor);
        eyebrowPaint.setStyle(Paint.Style.STROKE);
        eyebrowPaint.setStrokeWidth(10);

        if (eyebrowShape.equals("straight")) {
            // Draw straight eyebrows
            Path straightEyebrow1 = new Path();
            straightEyebrow1.moveTo(IMAGE_SIZE * 0.2f, IMAGE_SIZE * 0.25f);
            straightEyebrow1.lineTo(IMAGE_SIZE * 0.4f, IMAGE_SIZE * 0.25f);
            Path straightEyebrow2 = new Path();
            straightEyebrow2.moveTo(IMAGE_SIZE * 0.6f, IMAGE_SIZE * 0.25f);
            straightEyebrow2.lineTo(IMAGE_SIZE * 0.8f, IMAGE_SIZE * 0.25f);
            canvas.drawPath(straightEyebrow1, eyebrowPaint);
            canvas.drawPath(straightEyebrow2, eyebrowPaint);
        } else if (eyebrowShape.equals("unibrow")) {
            // Draw arched eyebrows
            Path archedEyebrow = new Path();
            archedEyebrow.moveTo(IMAGE_SIZE * 0.2f, IMAGE_SIZE * 0.3f);
            archedEyebrow.quadTo(IMAGE_SIZE * 0.5f, IMAGE_SIZE * 0.15f, IMAGE_SIZE * 0.8f, IMAGE_SIZE * 0.3f);
            canvas.drawPath(archedEyebrow, eyebrowPaint);
        }
        else if (eyebrowShape.equals("slanted")) {
            // Draw slanted eyebrows
            Path slantedEyebrow1 = new Path();
            slantedEyebrow1.moveTo(IMAGE_SIZE * 0.1f, IMAGE_SIZE * 0.4f);
            slantedEyebrow1.lineTo(IMAGE_SIZE * 0.4f, IMAGE_SIZE * 0.2f);
            Path slantedEyebrow2 = new Path();
            slantedEyebrow2.moveTo(IMAGE_SIZE * 0.6f, IMAGE_SIZE * 0.2f);
            slantedEyebrow2.lineTo(IMAGE_SIZE * 0.9f, IMAGE_SIZE * 0.4f);
            canvas.drawPath(slantedEyebrow1, eyebrowPaint);
            canvas.drawPath(slantedEyebrow2, eyebrowPaint);
        }

        return bitmap;
    }



    /**
        * Generates a name for the QR Code based on the hash
        * @param hashStr the hash of the QR Code
        * @return the name of the QR Code
     */
    private static String generateName(String hashStr) {
        // Define dictionaries for each hexadecimal
        Map<Character, String> hex0Dict = new HashMap<>();
        hex0Dict.put('0', "ethereal");
        hex0Dict.put('1', "neon");
        hex0Dict.put('2', "icy");
        hex0Dict.put('3', "radiant");
        hex0Dict.put('4', "obsidian");
        hex0Dict.put('5', "gossamer");
        hex0Dict.put('6', "vermilion");
        hex0Dict.put('7', "mystic");
        hex0Dict.put('8', "dusky");
        hex0Dict.put('9', "cobalt");
        hex0Dict.put('a', "cerulean");
        hex0Dict.put('b', "crimson");
        hex0Dict.put('c', "golden");
        hex0Dict.put('d', "rustic");
        hex0Dict.put('e', "cosmic");
        hex0Dict.put('f', "emerald");

        Map<Character, String> hex1Dict = new HashMap<>();
        hex1Dict.put('0', "Fro");
        hex1Dict.put('1', "Glo");
        hex1Dict.put('2', "Blu");
        hex1Dict.put('3', "Sly");
        hex1Dict.put('4', "Mau");
        hex1Dict.put('5', "Fiz");
        hex1Dict.put('6', "Sky");
        hex1Dict.put('7', "Dew");
        hex1Dict.put('8', "Hue");
        hex1Dict.put('9', "Zap");
        hex1Dict.put('a', "Jaz");
        hex1Dict.put('b', "Jem");
        hex1Dict.put('c', "Lux");
        hex1Dict.put('d', "Aur");
        hex1Dict.put('e', "Flu");
        hex1Dict.put('f', "Nim");

        // Define dictionary for bit 2
        Map<Character, String> hex2Dict = new HashMap<>();
        hex2Dict.put('0', "Mo");
        hex2Dict.put('1', "Lyo");
        hex2Dict.put('2', "Dax");
        hex2Dict.put('3', "Bix");
        hex2Dict.put('4', "Jyn");
        hex2Dict.put('5', "Taz");
        hex2Dict.put('6', "Nyx");
        hex2Dict.put('7', "Rex");
        hex2Dict.put('8', "Giz");
        hex2Dict.put('9', "Vyn");
        hex2Dict.put('a', "Pax");
        hex2Dict.put('b', "Hix");
        hex2Dict.put('c', "Kaz");
        hex2Dict.put('d', "Wex");
        hex2Dict.put('e', "Yon");
        hex2Dict.put('f', "Zyx");

        // Define dictionary for bit 3
        Map<Character, String> hex3Dict = new HashMap<>();
        hex3Dict.put('0', "Omega");
        hex3Dict.put('1', "Giga");
        hex3Dict.put('2', "Tera");
        hex3Dict.put('3', "Eternal");
        hex3Dict.put('4', "Nova");
        hex3Dict.put('5', "Hyper");
        hex3Dict.put('6', "Endless");
        hex3Dict.put('7', "Epic");
        hex3Dict.put('8', "Titan");
        hex3Dict.put('9', "Myriad");
        hex3Dict.put('a', "Galactic");
        hex3Dict.put('b', "Supreme");
        hex3Dict.put('c', "Super");
        hex3Dict.put('d', "Ultimate");
        hex3Dict.put('e', "Legendary");
        hex3Dict.put('f', "Master");

        // Define dictionary for bit 4
        Map<Character, String> hex4Dict = new HashMap<>();
        hex4Dict.put('0', "Thunderous");
        hex4Dict.put('1', "Blazing");
        hex4Dict.put('2', "Divine");
        hex4Dict.put('3', "Infernal");
        hex4Dict.put('4', "Empyreal");
        hex4Dict.put('5', "Arcane");
        hex4Dict.put('6', "Exalted");
        hex4Dict.put('7', "Champion");
        hex4Dict.put('8', "Seraphic");
        hex4Dict.put('9', "Electric");
        hex4Dict.put('a', "Astral");
        hex4Dict.put('b', "Eclipse");
        hex4Dict.put('c', "Sonic");
        hex4Dict.put('d', "Spectral");
        hex4Dict.put('e', "Vanquished");
        hex4Dict.put('f', "Celestial");

        Map<Character, String> hex5Dict = new HashMap<>();
        hex5Dict.put('0', "Golem");
        hex5Dict.put('1', "Yeti");
        hex5Dict.put('2', "Gargoyle");
        hex5Dict.put('3', "Werewolf");
        hex5Dict.put('4', "Vampire");
        hex5Dict.put('5', "Gorgon");
        hex5Dict.put('6', "Chimera");
        hex5Dict.put('7', "Unicorn");
        hex5Dict.put('8', "Basilisk");
        hex5Dict.put('9', "Manticore");
        hex5Dict.put('a', "Wyvern");
        hex5Dict.put('b', "Cockatrice");
        hex5Dict.put('c', "Griffin");
        hex5Dict.put('d', "Wraith");
        hex5Dict.put('e', "Hydra");
        hex5Dict.put('f', "Leviathan");


        // Lookup values in each dictionary based on corresponding hexadecimals in the hash
        Character[] hexaDecimals = new Character[6];
        for (int i = 0; i < 6; i++) {
            hexaDecimals[i] = hashStr.charAt(i);
        }
        return hex0Dict.get(hexaDecimals[0]) + " " +
                hex1Dict.get(hexaDecimals[1]) +
                hex2Dict.get(hexaDecimals[2]) +
                hex3Dict.get(hexaDecimals[3]) +
                hex4Dict.get(hexaDecimals[4]) +
                hex5Dict.get(hexaDecimals[5]);
    }
}