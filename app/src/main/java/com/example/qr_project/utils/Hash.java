package com.example.qr_project.utils;

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
    private String face;

    private int score;
    private final static String TAG = "HASH";

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
    public String getFace() {
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
     * @param hashStr a hash string
     * @return a face
     */
    private static String generateFace(String hashStr) {
        // Choose eyes
        Map<Character, String> hex2Eyes = new HashMap<>();
        hex2Eyes.put('0', "$ $");
        hex2Eyes.put('1', "* *");
        hex2Eyes.put('2', "O O");
        hex2Eyes.put('3', "^ ^");
        hex2Eyes.put('4', "~ ~");
        hex2Eyes.put('5', "U U");
        hex2Eyes.put('6', "' '");
        hex2Eyes.put('7', "X X");
        hex2Eyes.put('8', "> <");
        hex2Eyes.put('9', "# #");
        hex2Eyes.put('a', "- -");
        hex2Eyes.put('b', ". .");
        hex2Eyes.put('c', "F U");
        hex2Eyes.put('d', "= =");
        hex2Eyes.put('e', "$ $");
        hex2Eyes.put('f', "♥ ♥");



        // Choose ears
        Map<Character, String> hex2Ears = new HashMap<>();
        hex2Ears.put('0', "$$");      // dollar ears (high score)
        hex2Ears.put('1', "oo");   // round ears
        hex2Ears.put('2', "||");   // long ears
        hex2Ears.put('3', "[]");   // bat ears
        hex2Ears.put('4', "/\\");   // pointy ears
        hex2Ears.put('5', "()");   // elf ears
        hex2Ears.put('6', "!!");   // floppy ears
        hex2Ears.put('7', "@@");   // antenna ears
        hex2Ears.put('8', "\\/");   // wing ears
        hex2Ears.put('9', "~~");   // cat ears
        hex2Ears.put('a', "><");  // robot ears
        hex2Ears.put('b', "**");  // elephant ears
        hex2Ears.put('c', "<>");  // arrow ears
        hex2Ears.put('d', "==");  // rabbit ears
        hex2Ears.put('e', "__");  // devil ears
        hex2Ears.put('f', "##");  // short ears


        // Choose nose
        Map<Character, String> hex2Nose = new HashMap<>();
        hex2Nose.put('0', " $ ");    // dollar nose (high score)
        hex2Nose.put('1', " , ");   // small nose
        hex2Nose.put('2', " | ");   // straight nose
        hex2Nose.put('3', " /\\");   // curved nose
        hex2Nose.put('4', " | ");    // button nose
        hex2Nose.put('5', ". .");   // pig nose
        hex2Nose.put('6', "\\/ ");   // flared nostrils
        hex2Nose.put('7', "/\\_");   // hawk nose
        hex2Nose.put('8', "\\__");  // upturned nose
        hex2Nose.put('9', "\\\\\\");  // pointed nose
        hex2Nose.put('a', "<=>");   // wide nose
        hex2Nose.put('b', "(((");   // bulbous nose
        hex2Nose.put('c', " 0 ");   // Circle nose
        hex2Nose.put('d', "(_)");   // pudgy nose
        hex2Nose.put('e', " V ");  // ski slope nose
        hex2Nose.put('f', " + ");   // cleft nose

        // Choose mouth
        Map<Character, String> hex2Mouth = new HashMap<>();
        hex2Mouth.put('0', "$$$");      // dollar mouth (high score)
        hex2Mouth.put('1', " o ");   // small mouth
        hex2Mouth.put('2', " O ");   // oval mouth
        hex2Mouth.put('3', " ^ ");   // triangle mouth
        hex2Mouth.put('4', " U ");   // square mouth
        hex2Mouth.put('5', " V ");   // trapezoid mouth
        hex2Mouth.put('6', " | ");   // vertical line mouth
        hex2Mouth.put('7', "---");   // horizontal line mouth
        hex2Mouth.put('8', " S ");   // smile mouth
        hex2Mouth.put('9', " D ");   // frown mouth
        hex2Mouth.put('a', " 3 ");  // surprised mouth
        hex2Mouth.put('b', " P ");  // puckered mouth
        hex2Mouth.put('c', "___");  // neutral mouth
        hex2Mouth.put('d', " @ ");  // kissing mouth
        hex2Mouth.put('e', " X ");  // lips together mouth
        hex2Mouth.put('f', " + ");  // smirk mouth


        // Build head
        String eyes = hex2Eyes.get(hashStr.charAt(0));
        String ears = hex2Ears.get(hashStr.charAt(1));
        String nose = hex2Nose.get(hashStr.charAt(2));
        String mouth = hex2Mouth.get(hashStr.charAt(3));
        String head =
                " /‾‾‾‾‾\\ \n" +
                        ears.charAt(0) + "| " + eyes + " |" + ears.charAt(1) + " \n" +
                        " | " + nose + " | \n" +
                        "|  " + mouth + "  |\n" +
                        " \\_____/ ";
        return head;
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