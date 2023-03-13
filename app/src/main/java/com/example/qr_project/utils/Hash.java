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
     * Returns score of the hash
     * @return: Score that
     */
    public int getScore() {
        return score;
    }

    /**
     * Returns a hash string
     * @return: Hash that is stored
     */
    public String getHash() {
        return hash;
    }

    /**
     * Returns a name string
     * @return: name
     */
    public String getName() {
        return name;
    }

    /**
     * Returns a face string
     * @return: face
     */
    public String getFace() {
        return face;
    }

    /**
     * Returns a hash for a given string
     * @param str: A string to be hashed
     * @return
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
     * @param hash
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
     * @param hashStr
     * @return
     */
    private static String generateFace(String hashStr) {
        // Convert hash string to binary representation
        String hashBin = new BigInteger(hashStr, 16).toString(2);
        hashBin = String.format("%256s", hashBin).replace(' ', '0');

        Map<Integer, String> bit0Dict = new HashMap<>();
        bit0Dict.put(0, "  ___\n /   \\\n|     |\n \\___/");
        bit0Dict.put(1, "  ___\n /   \\\n|  *  |\n \\___/");
        bit0Dict.put(2, "  ___\n /   \\\n|  ^  |\n \\___/");
        bit0Dict.put(3, "  ___\n /   \\\n|  o  |\n \\___/");
        bit0Dict.put(4, "  ___\n /   \\\n| \\ / |\n \\___/");
        bit0Dict.put(5, "  ___\n /   \\\n| / \\ |\n \\___/");
        bit0Dict.put(6, "  ___\n /   \\\n|  -  |\n \\___/");
        bit0Dict.put(7, "  ___\n /   \\\n|  =  |\n \\___/");
        bit0Dict.put(8, "  ___\n /   \\\n|  _  |\n \\___/");
        bit0Dict.put(9, "  ___\n /   \\\n|  .  |\n \\___/");
        bit0Dict.put(10, "  ___\n /   \\\n| /\\  |\n \\___/");
        bit0Dict.put(11, "  ___\n /   \\\n| \\/  |\n \\___/");
        bit0Dict.put(12, "  ___\n /   \\\n| o o |\n \\___/");
        bit0Dict.put(13, "  ___\n /   \\\n| ^ ^ |\n \\___/");
        bit0Dict.put(14, "  ___\n /   \\\n| /o\\ |\n \\___/");
        bit0Dict.put(15, "  ___\n /   \\\n| \\o/ |\n \\___/");

        Map<Integer, String> bit1Dict = new HashMap<>();
        bit1Dict.put(0, " /\\  /\\");
        bit1Dict.put(1, "  /__\\");
        bit1Dict.put(2, " |  --|");
        bit1Dict.put(3, "  |  |");
        bit1Dict.put(4, " |    |");
        bit1Dict.put(5, " |^   |");
        bit1Dict.put(6, " | v  |");
        bit1Dict.put(7, " | |  |");
        bit1Dict.put(8, " |..  |");
        bit1Dict.put(9, "  |__-|");
        bit1Dict.put(10, "  |~~~|");
        bit1Dict.put(11, "  |_ _|");
        bit1Dict.put(12, "  | ^ |");
        bit1Dict.put(13, "  | v |");
        bit1Dict.put(14, "  | o |");
        bit1Dict.put(15, "  | x |");

        Map<Integer, String> bit2Dict = new HashMap<>();
        bit2Dict.put(0, "  ____\n /    \\\n|  ()  |\n \\____/");
        bit2Dict.put(1, "  ____\n /    \\\n|  --  |\n \\____/");
        bit2Dict.put(2, "  ____\n /    \\\n|  <>  |\n \\____/");
        bit2Dict.put(3, "  ____\n /    \\\n|  ^^  |\n \\____/");
        bit2Dict.put(4, "  ____\n /    \\\n|  ><  |\n \\____/");
        bit2Dict.put(5, "  ____\n /    \\\n|  ..  |\n \\____/");
        bit2Dict.put(6, "  ____\n /    \\\n|  --  |\n \\__/\\/");
        bit2Dict.put(7, "  ____\n /    \\\n|  oo  |\n \\__/\\/");
        bit2Dict.put(8, "  ____\n /    \\\n|  ^^  |\n \\__--/");
        bit2Dict.put(9, "  ____\n /    \\\n|  ||  |\n \\__/\\/");
        bit2Dict.put(10, "  ____\n /    \\\n|  XX  |\n \\__/\\/");
        bit2Dict.put(11, "  ____\n /    \\\n|  **  |\n \\__--/");
        bit2Dict.put(12, "  ____\n /    \\\n|  ##  |\n \\__--/");

        Map<Integer, String> bit3Dict = new HashMap<>();
        bit3Dict.put(0, "");    // no nose
        bit3Dict.put(1, "  ,  ");   // small nose
        bit3Dict.put(2, "  |  ");   // straight nose
        bit3Dict.put(3, " /\\ ");   // curved nose
        bit3Dict.put(4, " || ");    // button nose
        bit3Dict.put(5, "(_|_)");   // pig nose
        bit3Dict.put(6, " \\/ ");   // flared nostrils
        bit3Dict.put(7, " /\\_");   // hawk nose
        bit3Dict.put(8, " \\__/");  // upturned nose
        bit3Dict.put(9, "  \\\\ ");  // pointed nose
        bit3Dict.put(10, "<=>");   // wide nose
        bit3Dict.put(11, "(((");   // bulbous nose
        bit3Dict.put(12, " 0  ");   // Circle nose
        bit3Dict.put(13, "(_) ");   // pudgy nose
        bit3Dict.put(14, "  V  ");  // ski slope nose
        bit3Dict.put(15, " +  ");   // cleft nose

        Map<Integer, String> bit4Dict = new HashMap<>();
        bit4Dict.put(0, "  ");      // no mouth
        bit4Dict.put(1, "  o  ");   // small mouth
        bit4Dict.put(2, "  O  ");   // oval mouth
        bit4Dict.put(3, "  ^  ");   // triangle mouth
        bit4Dict.put(4, "  U  ");   // square mouth
        bit4Dict.put(5, "  V  ");   // trapezoid mouth
        bit4Dict.put(6, "  |  ");   // vertical line mouth
        bit4Dict.put(7, "  -  ");   // horizontal line mouth
        bit4Dict.put(8, "  S  ");   // smile mouth
        bit4Dict.put(9, "  D  ");   // frown mouth
        bit4Dict.put(10, "  3  ");  // surprised mouth
        bit4Dict.put(11, "  P  ");  // puckered mouth
        bit4Dict.put(12, "  _  ");  // neutral mouth
        bit4Dict.put(13, "  @  ");  // kissing mouth
        bit4Dict.put(14, "  X  ");  // lips together mouth
        bit4Dict.put(15, "  +  ");  // smirk mouth

        Map<Integer, String> bit5Dict = new HashMap<>();
        bit5Dict.put(0, "");      // no ears
        bit5Dict.put(1, " oo");   // round ears
        bit5Dict.put(2, " ||");   // long ears
        bit5Dict.put(3, " []");   // bat ears
        bit5Dict.put(4, " /\\");   // pointy ears
        bit5Dict.put(5, " ()");   // elf ears
        bit5Dict.put(6, " !!");   // floppy ears
        bit5Dict.put(7, " @ ");   // antenna ears
        bit5Dict.put(8, " \\/");   // wing ears
        bit5Dict.put(9, " ~ ");   // cat ears
        bit5Dict.put(10, " >>");  // robot ears
        bit5Dict.put(11, " **");  // elephant ears
        bit5Dict.put(12, " <<");  // arrow ears
        bit5Dict.put(13, " = ");  // rabbit ears
        bit5Dict.put(14, " __");  // devil ears
        bit5Dict.put(15, " ## ");  // short ears


        // Lookup values in each dictionary based on corresponding bits in the hash
        int[] bits = new int[6];
        for (int i = 0; i < 6; i++) {
            bits[i] = Character.getNumericValue(hashBin.charAt(i));
        }

        String forehead = "    ___ \n   /   \\\\  ";
        String chin = "  \\___/";

        // Use the java replace function to get the character for the rest of the face
        // construct the string with | first and then replace it after

        return bit0Dict.get(bits[0]) + " " + bit1Dict.get(bits[1]) + bit2Dict.get(bits[2]) +
                bit3Dict.get(bits[3]) + bit4Dict.get(bits[4]) + bit5Dict.get(bits[5]);

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
        hex4Dict.put('0', "Tunderous");
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