package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qr_project.R;
import com.example.qr_project.utils.Hash;
import com.example.qr_project.utils.QR_Code;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.HashMap;

public class ScanActivity extends AppCompatActivity {
    QR_Code qrCode;
    Hash hash;
    FirebaseFirestore db;

    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
                // Get the image from the camera
                Intent data = result.getData();
                Bitmap image = (Bitmap) data.getExtras().get("data");

                // Create a hashmap of the QR_Code properties you want to store
                HashMap<String, Object> qrCodeDB = new HashMap<>();
                qrCodeDB.put("id", qrCode.getHash());
                qrCodeDB.put("name", qrCode.getName());
                qrCodeDB.put("photo", qrCode.getPhoto());
                qrCodeDB.put("score", qrCode.getScore());

                // Get the current user's ID
                SharedPreferences sharedPref = getSharedPreferences("my_app_pref", Context.MODE_PRIVATE);

                // Retrieve the user's information
                String userId = sharedPref.getString("user_id", null);
                String userName = sharedPref.getString("user_name", null);

                // Get a reference to the user's document in Firestore
                DocumentReference userRef = db.collection("users").document(userName);

                FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
                if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                        && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                    // TODO: Consider calling
                    //    ActivityCompat#requestPermissions
                    // here to request the missing permissions, and then overriding
                    //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                    //                                          int[] grantResults)
                    // to handle the case where the user grants the permission. See the documentation
                    // for ActivityCompat#requestPermissions for more details.
                    return;
                }
                fusedLocationClient.getLastLocation()
                        .addOnSuccessListener(new OnSuccessListener<Location>() {
                            @Override
                            public void onSuccess(Location location) {
                                if (location != null) {
                                    GeoPoint geoPoint = new GeoPoint(location.getLatitude(), location.getLongitude());
                                    qrCode.setLocation(geoPoint);
                                    qrCodeDB.put("location", geoPoint);

                                    // Update the qrcodes array field with the new QR code
                                    userRef.update("qrcodes", FieldValue.arrayUnion(qrCodeDB))
                                            .addOnSuccessListener(new OnSuccessListener<Void>() {
                                                @Override
                                                public void onSuccess(Void aVoid) {
                                                    Log.d(TAG, "QR code added to user's document in DB");
                                                    finish();
                                                }
                                            })
                                            .addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception e) {
                                                    Log.w(TAG, "Error adding QR code to user's document in DB", e);
                                                    finish();
                                                }
                                            });

                                } else {
                                    Log.w(TAG, "Unable to retrieve location");
                                    finish();
                                }
                            }
                        });
            }
        });

    }

    // Handle the scanning of the QR code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                return;
            } else {
                // Create a hash object and pass hash into the constructor of QR_Code
                hash = new Hash(result.getContents());
                //String name = hash.generateName(result.getContents()); Fix the name
                qrCode = new QR_Code(hash, null, null);

                // Ask the user if they want to take a picture
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Would you like to take a picture?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open the camera
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraLauncher.launch(takePictureIntent);
                        finish();
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // If the user doesn't want to take a picture, then just add the QR code
                        // to the user's document in Firestore

                        // Create a hashmap of the QR_Code properties you want to store
                        HashMap<String, Object> qrCodeDB = new HashMap<>();
                        qrCodeDB.put("id", qrCode.getHash());
                        qrCodeDB.put("name", qrCode.getName());
                        qrCodeDB.put("photo", qrCode.getPhoto());
                        qrCodeDB.put("score", qrCode.getScore());

                        // Get the current user's ID
                        SharedPreferences sharedPref = getSharedPreferences("my_app_pref", Context.MODE_PRIVATE);

                        // Retrieve the user's information
                        String userId = sharedPref.getString("user_id", null);
                        String userName = sharedPref.getString("user_name", null);

                        // Get a reference to the user's document in Firestore
                        DocumentReference userRef = db.collection("users").document(userName);

                        qrCodeDB.put("location", null);

                        // Update the qrcodes array field with the new QR code
                        userRef.update("qrcodes", FieldValue.arrayUnion(qrCodeDB))
                                .addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void aVoid) {
                                        Log.d(TAG, "QR code added to user's document in DB");
                                        finish();
                                    }
                                })
                                .addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Log.w(TAG, "Error adding QR code to user's document in DB", e);
                                        finish();
                                    }
                                });
                    }
                });
                builder.show();
            }
        }
    }

    @Override
    public void onBackPressed() {
        setResult(RESULT_CANCELED);
        super.onBackPressed();
    }
}


