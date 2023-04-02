package com.example.qr_project.activities;


import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.qr_project.utils.Player;
import com.example.qr_project.utils.QR_Code;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    QR_Code qrCode;
    FirebaseFirestore db;
    private static final int LOCATION_REQUEST_CODE = 100;

    /**
     * Defining the cameralauncher ready to scan the QR_Code
     * The camera can scan and take a photo of the QR_Code being presented to it
     * Getting the image presented to the camera
     *
     * Here, it also creates a hashmap of the QR_Code properties you want to store
     * Also updates the location of the QR_Code being scanned
     * Connecting with the FireStore FireBase,
     * this is to fetch the user's id/ account to store it on the FireBase Cloud
     * under their account.
     *
     * Then, the code will update the qrcodes array field with the new QR code
     * The scanned QR_Code will then be stored to the FireBase Cloud under the user's account/id
     * @param savedInstanceState a package to execute the scanning job of the app
     * @see FirebaseFirestore
     * @see QRCodeActivity
     * @see Player
     *
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Start the QR code scanner
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a QR code");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();

        db = FirebaseFirestore.getInstance();
    }

    /**
     * After defining the cameraLauncher and the functionality of its
     * The ability of taking the photo and scanning an image as the QR_Code
     * The camera can scan and take a photo of the QR_Code being presented to it
     * Getting the image presented to the camera
     *
     * Here, it also creates a hashmap of the QR_Code properties you want to store
     * Also updates the location of the QR_Code being scanned
     * Connecting with the FireStore FireBase,
     * this is to fetch the user's id/ account to store it on the FireBase Cloud
     * under their account.
     *
     * Then, the code will update the qrcodes array field with the new QR code
     * The scanned QR_Code will then be stored to the FireBase Cloud under the user's account/id
     * @param resultCode   displays the result
     * @param requestCode   taking the code
     * @param data    the data of the QR_Code
     * @see FirebaseFirestore
     * @see QRCodeActivity
     * @see Player
     *
     */

    // Handle the scanning of the QR code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            qrCode = new QR_Code(result.getContents());

            // Check for camera permissions
            if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
                // Request camera permissions if not granted
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            } else {
                // Get the current user's ID
                SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);

                String qrCodeHash = qrCode.getHash();
                // Retrieve the user's information
                String userID = sharedPref.getString("user_id", null);
                db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
                    // Check if the document exists
                    if (documentSnapshot.exists()) {
                        // Get the qrcodes array from the document data
                        List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");
                        assert qrCodes != null;
                        boolean qrCodeScanned = false;
                        for (Map<String, Object> qrCode : qrCodes) {
                            String hash = (String) qrCode.get("hash");
                            if (Objects.equals(hash, qrCodeHash)) {
                                qrCodeScanned = true;
                                break;
                            }
                        }
                        if (qrCodeScanned) {
                            Toast.makeText(this, "You already scanned this QR code.", Toast.LENGTH_SHORT).show();
                            finish();
                        } else {
                            addQR();
                            Intent intent = new Intent(this, PictureActivity.class);
                            intent.putExtra("qrHash", qrCodeHash);
                            intent.putExtra("userID", userID);
                            startActivity(intent);
                            finish();
                        }
                    }
                });
            }
        }
    }

    /**
     * This is to handle the new QRCode being scanned
     * Then the QRCode will be automatically added to the FireStore database under the user's account
     * The QR_Code is also attached with the location
     * If the user wishes to add the location to the database on FireStore, they can do so;
     * however if they do not wish to add the location to the database on FireStore, they can opt out.
     * The user will be prompted to decide to add the location or not.
     * @see FirebaseFirestore
     * @see UserProfileActivity
     * @see UserHomeActivity
     */
    public void addQR() {
        // Get the current user's ID
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);

        // Retrieve the user's information
        String userID = sharedPref.getString("user_id", null);

        // Initialize the FusedLocationProviderClient
        FusedLocationProviderClient fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);

        // Check for location permissions
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            // If permissions are granted, set the QR code's location to the user's current location
            fusedLocationProviderClient.getLastLocation()
                    .addOnSuccessListener(location -> {
                        if (location != null) {
                            double latitude = location.getLatitude();
                            double longitude = location.getLongitude();
                            qrCode.setLocation(new GeoPoint(latitude, longitude));

                            // Update the user's document in the database
                            db.collection("users").document(userID).update("qrcodes", FieldValue.arrayUnion(qrCode))
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("MY TAG", "QR code added to user's document in DB");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("MY TAG", "Error adding QR code to user's document in DB", e);
                                    });
                        } else {
                            // Handle location not found error
                            Toast.makeText(this, "Unable to retrieve location.", Toast.LENGTH_SHORT).show();
                        }
                    })
                    .addOnFailureListener(e -> {
                        // Handle location service error
                        Toast.makeText(this, "Location services not available.", Toast.LENGTH_SHORT).show();
                        Log.w("MY TAG", "Error getting location", e);
                    });
        } else {
            // If permissions are not granted, request them from the user
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, LOCATION_REQUEST_CODE);
        }

        // Update the user's total score in the database
        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get the user's current score
                int currentScore = Objects.requireNonNull(documentSnapshot.getLong("totalScore")).intValue();
                int newScore = currentScore + qrCode.getScore();

                // Update the user's score in the database
                db.collection("users").document(userID).update("totalScore", newScore);
            }
        });
    }
}


