package com.example.qr_project;

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
    private int score;
    private final static String TAG = "HASH";

    /**
     * Creates an instance of Hash object.
     * @param qrCodeContent a String contained in the QRCode
     */
    public Hash(String qrCodeContent) {
        this.hash = getHash(qrCodeContent);
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
     * Returns a String hash
     * @return: Hash that is stored
     */
    public String getHash() {
        return hash;
    }


    /**
     * Returns a hash for a given string
     * @param str: A string to be hashed
     * @return
     */
    private static String getHash(String str){
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

    public static String face(String hashStr) {
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
        String name = bit0Dict.get(bits[0]) + " " + bit1Dict.get(bits[1]) + bit2Dict.get(bits[2]) +
                bit3Dict.get(bits[3]) + bit4Dict.get(bits[4]) + bit5Dict.get(bits[5]);

        return name;
    }

    public static String generateName(String hashStr) {
        // Convert hash string to binary representation
        String hashBin = new BigInteger(hashStr, 16).toString(2);
        hashBin = String.format("%256s", hashBin).replace(' ', '0');

        // Define dictionaries for each bit
        Map<Integer, String> bit0Dict = new HashMap<>();
        bit0Dict.put(0, "ethereal");
        bit0Dict.put(1, "neon");
        bit0Dict.put(2, "icy");
        bit0Dict.put(3, "radiant");
        bit0Dict.put(4, "obsidian");
        bit0Dict.put(5, "gossamer");
        bit0Dict.put(6, "vermilion");
        bit0Dict.put(7, "mystic");
        bit0Dict.put(8, "dusky");
        bit0Dict.put(9, "cobalt");
        bit0Dict.put(10, "cerulean");
        bit0Dict.put(11, "crimson");
        bit0Dict.put(12, "golden");
        bit0Dict.put(13, "rustic");
        bit0Dict.put(14, "cosmic");
        bit0Dict.put(15, "emerald");

        Map<Integer, String> bit1Dict = new HashMap<>();
        bit1Dict.put(0, "Fro");
        bit1Dict.put(1, "Glo");
        bit1Dict.put(2, "Blu");
        bit1Dict.put(3, "Sly");
        bit1Dict.put(4, "Mau");
        bit1Dict.put(5, "Fiz");
        bit1Dict.put(6, "Sky");
        bit1Dict.put(7, "Dew");
        bit1Dict.put(8, "Hue");
        bit1Dict.put(9, "Zap");
        bit1Dict.put(10, "Jaz");
        bit1Dict.put(11, "Jem");
        bit1Dict.put(12, "Lux");
        bit1Dict.put(13, "Aur");
        bit1Dict.put(14, "Flu");
        bit1Dict.put(15, "Nim");

        // Define dictionary for bit 2
        Map<Integer, String> bit2Dict = new HashMap<>();
        bit2Dict.put(0, "Mo");
        bit2Dict.put(1, "Lyo");
        bit2Dict.put(2, "Dax");
        bit2Dict.put(3, "Bix");
        bit2Dict.put(4, "Jyn");
        bit2Dict.put(5, "Taz");
        bit2Dict.put(6, "Nyx");
        bit2Dict.put(7, "Rex");
        bit2Dict.put(8, "Giz");
        bit2Dict.put(9, "Vyn");
        bit2Dict.put(10, "Pax");
        bit2Dict.put(11, "Hix");
        bit2Dict.put(12, "Kaz");
        bit2Dict.put(13, "Wex");
        bit2Dict.put(14, "Yon");
        bit2Dict.put(15, "Zyx");

        // Define dictionary for bit 3
        Map<Integer, String> bit3Dict = new HashMap<>();
        bit3Dict.put(0, "Omega");
        bit3Dict.put(1, "Giga");
        bit3Dict.put(2, "Tera");
        bit3Dict.put(3, "Eternal");
        bit3Dict.put(4, "Nova");
        bit3Dict.put(5, "Hyper");
        bit3Dict.put(6, "Endless");
        bit3Dict.put(7, "Epic");
        bit3Dict.put(8, "Titan");
        bit3Dict.put(9, "Myriad");
        bit3Dict.put(10, "Galactic");
        bit3Dict.put(11, "Supreme");
        bit3Dict.put(12, "Super");
        bit3Dict.put(13, "Ultimate");
        bit3Dict.put(14, "Legendary");
        bit3Dict.put(15, "Master");

        // Define dictionary for bit 4
        Map<Integer, String> bit4Dict = new HashMap<>();
        bit4Dict.put(0, "Thunderous");
        bit4Dict.put(1, "Blazing");
        bit4Dict.put(2, "Divine");
        bit4Dict.put(3, "Infernal");
        bit4Dict.put(4, "Empyreal");
        bit4Dict.put(5, "Arcane");
        bit4Dict.put(6, "Exalted");
        bit4Dict.put(7, "Champion");
        bit4Dict.put(8, "Seraphic");
        bit4Dict.put(9, "Electric");
        bit4Dict.put(10, "Astral");
        bit4Dict.put(11, "Eclipse");
        bit4Dict.put(12, "Sonic");
        bit4Dict.put(13, "Spectral");
        bit4Dict.put(14, "Vanquished");
        bit4Dict.put(15, "Celestial");

        Map<Integer, String> bit5Dict = new HashMap<>();
        bit5Dict.put(0, "Golem");
        bit5Dict.put(1, "Yeti");
        bit5Dict.put(2, "Gargoyle");
        bit5Dict.put(3, "Werewolf");
        bit5Dict.put(4, "Vampire");
        bit5Dict.put(5, "Gorgon");
        bit5Dict.put(6, "Chimera");
        bit5Dict.put(7, "Unicorn");
        bit5Dict.put(8, "Basilisk");
        bit5Dict.put(9, "Manticore");
        bit5Dict.put(10, "Wyvern");
        bit5Dict.put(11, "Cockatrice");
        bit5Dict.put(12, "Griffin");
        bit5Dict.put(13, "Wraith");
        bit5Dict.put(14, "Hydra");
        bit5Dict.put(15, "Leviathan");


        // Lookup values in each dictionary based on corresponding bits in the hash
        int[] bits = new int[6];
        for (int i = 0; i < 6; i++) {
            bits[i] = Character.getNumericValue(hashBin.charAt(i));
        }
        String name = bit0Dict.get(bits[0]) + " " + bit1Dict.get(bits[1]) + bit2Dict.get(bits[2]) +
                bit3Dict.get(bits[3]) + bit4Dict.get(bits[4]) + bit5Dict.get(bits[5]);

        return name;
        }
}