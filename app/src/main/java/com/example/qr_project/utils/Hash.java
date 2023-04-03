package com.example.qr_project.utils;

import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.Log;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;

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
        // convert the hash into a set of parameters for the face-like image
        int eyeSize = Math.abs(hash.hashCode()) % 50 + 50;
        int noseSize = Math.abs(hash.hashCode() / 3) % 30 + 20;
        int mouthSize = Math.abs(hash.hashCode() / 7) % 50 + 20;
        int eyeOffsetX = Math.abs(hash.hashCode() / 11) % 50 - 25;
        int eyeOffsetY = Math.abs(hash.hashCode() / 13) % 50 - 25;
        int noseOffsetX = Math.abs(hash.hashCode() / 17) % 50 - 25;
        int noseOffsetY = Math.abs(hash.hashCode() / 19) % 50 - 25;
        int mouthOffsetX = Math.abs(hash.hashCode() / 23) % 50 - 25;
        int mouthOffsetY = Math.abs(hash.hashCode() / 29) % 50 - 25;

        // create a new bitmap for the image
        Bitmap bitmap = Bitmap.createBitmap(IMAGE_SIZE, IMAGE_SIZE, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);

        // draw the background
        canvas.drawColor(BACKGROUND_COLOR);

        Paint eyePaint = new Paint();
        eyePaint.setColor(EYE_COLOR);

        Paint nosePaint = new Paint();
        nosePaint.setColor(NOSE_COLOR);

        Paint mouthPaint = new Paint();
        mouthPaint.setColor(MOUTH_COLOR);

        // draw the eyes
        canvas.drawCircle(IMAGE_SIZE / 2 + eyeOffsetX - eyeSize, IMAGE_SIZE / 2 + eyeOffsetY, eyeSize, eyePaint);
        canvas.drawCircle(IMAGE_SIZE / 2 + eyeOffsetX + eyeSize, IMAGE_SIZE / 2 + eyeOffsetY, eyeSize, eyePaint);

        // draw the nose
        canvas.drawCircle(IMAGE_SIZE / 2 + noseOffsetX, IMAGE_SIZE / 2 + noseOffsetY, noseSize, nosePaint);

        // draw the mouth
        canvas.drawRect(IMAGE_SIZE / 2 + mouthOffsetX - mouthSize, IMAGE_SIZE / 2 + mouthOffsetY,
                IMAGE_SIZE / 2 + mouthOffsetX + mouthSize, IMAGE_SIZE / 2 + mouthOffsetY + mouthSize / 2,
                mouthPaint);

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