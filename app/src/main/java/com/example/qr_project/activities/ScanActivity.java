package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
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
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {
    private static final int MY_CAMERA_REQUEST_CODE = 100;
    QR_Code qrCode;
    FirebaseFirestore db;
    private ActivityResultLauncher<Intent> cameraLauncher;


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

        if (checkSelfPermission(Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(new String[]{Manifest.permission.CAMERA}, MY_CAMERA_REQUEST_CODE);
        }

        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a QR/Barcode");
        integrator.initiateScan();

        db = FirebaseFirestore.getInstance();
        // Define cameraLauncher
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                Intent data = result.getData();
                assert data != null;
                Bitmap image = (Bitmap) data.getExtras().get("data");
                addQR();
            }
        });
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
            if (result.getContents() != null) {
                // String name = hash.generateName(result.getContents()); Fix the name
                qrCode = new QR_Code(result.getContents());

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
                        for (Map<String, Object> qrCode : qrCodes) {
                            String hash = (String) qrCode.get("hash");
                            if (Objects.equals(hash, qrCodeHash)) {
                                Toast.makeText(this, "You already scanned this QR code.", Toast.LENGTH_SHORT).show();
                                finish();
                            }
                        }
                    }
                });

                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                cameraLauncher.launch(takePictureIntent);

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
     * @see com.example.qr_project.FireStore
     * @see UserProfileActivity
     * @see UserHomeActivity
     */
    public void addQR() {
        // Get the current user's ID
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);

        // Retrieve the user's information
        String userID = sharedPref.getString("user_id", null);

        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Get the user's current score
                int currentScore = documentSnapshot.getLong("totalScore").intValue();

                int newScore = currentScore + qrCode.getScore();

                // Update the user's score in the database
                db.collection("users").document(userID).update("totalScore", newScore);
            }
        });

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is granted
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            LocationListener locationListener = location -> {
                GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                Log.d(TAG, "Geopoint" + geoPoint);
                qrCode.setLocation(geoPoint);
            };
            locationManager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListener);
        } else {
            // Permission is not granted, set location to null
            qrCode.setLocation(null);
        }

        // Update the qrcodes array field with the new QR code
        db.collection("users").document(userID).update("qrcodes", FieldValue.arrayUnion(qrCode))
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "QR code added to user's document in DB");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.w(TAG, "Error adding QR code to user's document in DB", e);
                    finish();
                });

//        // Get the user's current location
//        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
//        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
//                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
//            return;
//        }
//        fusedLocationClient.getLastLocation()
//                .addOnSuccessListener(location -> {
//                    if (location != null) {
//                        GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
//                        qrCode.setLocation(geoPoint);
//
//                        // Update the qrcodes array field with the new QR code
//                        db.collection("users").document(userID).update("qrcodes", FieldValue.arrayUnion(qrCode))
//                                .addOnSuccessListener(aVoid -> {
//                                    Log.d(TAG, "QR code added to user's document in DB");
//                                    finish();
//                                })
//                                .addOnFailureListener(e -> {
//                                    Log.w(TAG, "Error adding QR code to user's document in DB", e);
//                                    finish();
//                                });
//                    } else {
//                        Log.w(TAG, "Unable to retrieve location");
//                        finish();
//                        }
//                    });
    }
}



