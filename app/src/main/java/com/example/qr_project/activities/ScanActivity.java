package com.example.qr_project.activities;


import static android.content.ContentValues.TAG;

import android.Manifest;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.qr_project.R;
import com.example.qr_project.utils.Player;
import com.example.qr_project.utils.QR_Code;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class ScanActivity extends AppCompatActivity {
    private static final int CAMERA_PERMISSION_REQUEST_CODE = 101;
    QR_Code qrCode;
    FirebaseFirestore db;
    private static final int LOCATION_REQUEST_CODE = 100;

    Bitmap face;

    /**
     * Defining the cameralauncher ready to scan the QR_Code
     * The camera can scan and take a photo of the QR_Code being presented to it
     * Getting the image presented to the camera
     * <p>
     * Here, it also creates a hashmap of the QR_Code properties you want to store
     * Also updates the location of the QR_Code being scanned
     * Connecting with the FireStore FireBase,
     * this is to fetch the user's id/ account to store it on the FireBase Cloud
     * under their account.
     * <p>
     * Then, the code will update the qrcodes array field with the new QR code
     * The scanned QR_Code will then be stored to the FireBase Cloud under the user's account/id
     *
     * @param savedInstanceState a package to execute the scanning job of the app
     * @see FirebaseFirestore
     * @see QRCodeActivity
     * @see Player
     */
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        View loadingScreen = getLayoutInflater().inflate(R.layout.loading_screen, null);
        setContentView(loadingScreen);
        db = FirebaseFirestore.getInstance();

        // Permission was not granted
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED) {
            // Request camera permissions if not granted
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA}, CAMERA_PERMISSION_REQUEST_CODE);
            // Permission was granted
        } else {
            // Start the QR code scanner
            initiateScanning();
        }
    }

    /**
     * After defining the cameraLauncher and the functionality of its
     * The ability of taking the photo and scanning an image as the QR_Code
     * The camera can scan and take a photo of the QR_Code being presented to it
     * Getting the image presented to the camera
     * <p>
     * Here, it also creates a hashmap of the QR_Code properties you want to store
     * Also updates the location of the QR_Code being scanned
     * Connecting with the FireStore FireBase,
     * this is to fetch the user's id/ account to store it on the FireBase Cloud
     * under their account.
     * <p>
     * Then, the code will update the qrcodes array field with the new QR code
     * The scanned QR_Code will then be stored to the FireBase Cloud under the user's account/id
     *
     * @param resultCode  displays the result
     * @param requestCode taking the code
     * @param data        the data of the QR_Code
     * @see FirebaseFirestore
     * @see QRCodeActivity
     * @see Player
     */

    // Handle the scanning of the QR code
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() != null) {
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
                    face = qrCode.getFace();
                    qrCode.setFace();
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
                                Intent faceIntent = new Intent(this, FaceActivity.class);
                                faceIntent.putExtra("qrHash", qrCodeHash);
                                faceIntent.putExtra("userID", userID);
                                // Convert the bitmap to a byte array
                                ByteArrayOutputStream stream = new ByteArrayOutputStream();
                                face.compress(Bitmap.CompressFormat.JPEG, 50, stream);
                                byte[] byteArray = stream.toByteArray();
                                faceIntent.putExtra("face", byteArray);
                                startActivity(faceIntent);
                                Intent photoIntent = new Intent(this, PictureActivity.class);
                                photoIntent.putExtra("qrHash", qrCodeHash);
                                photoIntent.putExtra("userID", userID);
                                startActivity(photoIntent);
                                finish();
                            }
                        }
                    });
                }
            } else {
                Toast.makeText(this, "Scanning was canceled", Toast.LENGTH_SHORT).show();
                finish();
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
     *
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
                            qrCode.setLocation(null);
                            db.collection("users").document(userID).update("qrcodes", FieldValue.arrayUnion(qrCode))
                                    .addOnSuccessListener(aVoid -> {
                                        Log.d("MY TAG", "QR code added to user's document in DB");
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.w("MY TAG", "Error adding QR code to user's document in DB", e);
                                    });
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
            qrCode.setLocation(null);
            db.collection("users").document(userID).update("qrcodes", FieldValue.arrayUnion(qrCode))
                    .addOnSuccessListener(aVoid -> {
                        Log.d("MY TAG", "QR code added to user's document in DB");
                    })
                    .addOnFailureListener(e -> {
                        Log.w("MY TAG", "Error adding QR code to user's document in DB", e);
                    });
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

        String qrCodeHash = qrCode.getHash();

        // Check if QR code already exists in the Firestore collection
        DocumentReference qrCodeRef = db.collection("qrcodes").document(qrCodeHash);
        qrCodeRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document.exists()) {
                    // QR code already exists, add the user ID to the 'users' array field
                    qrCodeRef.update("users", FieldValue.arrayUnion(userID));
                } else {
                    // QR code does not exist, add a new document with the 'users' array field
                    Map<String, Object> data = new HashMap<>();
                    data.put("users", Arrays.asList(userID));
                    data.put("comments", null);
                    qrCodeRef.set(data).addOnCompleteListener(setTask -> {
                        if (setTask.isSuccessful()) {
                            Log.d(TAG, "Added new QR code document with hash: " + qrCodeHash);
                        } else {
                            Log.e(TAG, "Failed to add new QR code document", setTask.getException());
                        }
                    });
                }
            } else {
                Log.e(TAG, "Failed to retrieve QR code document", task.getException());
            }
        });

    }


    /**
     * Handles the requestPermission dialog for Location & Camera permissions
     *
     * @param requestCode  The request code passed
     * @param permissions  The requested permissions. Never null.
     * @param grantResults The grant results for the corresponding permissions
     *                     which is either {@link android.content.pm.PackageManager#PERMISSION_GRANTED}
     *                     or {@link android.content.pm.PackageManager#PERMISSION_DENIED}. Never null.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        // Handle the permission dialog for location
        if (requestCode == LOCATION_REQUEST_CODE) {
            handleLocationRequestCode(grantResults);
            // Handle the permission dialog result for camera
        } else if (requestCode == CAMERA_PERMISSION_REQUEST_CODE) {
            handleCameraRequestCode(grantResults);
        }
    }

    /**
     * Initiates the scanning procedure using IntentIntegrator. Assumes that the app has Camera
     * permission
     *
     * @throws AssertionError when the app doesn't have cameraPermission.
     */
    public void initiateScanning() throws AssertionError {
        assert ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED;
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a QR code");
        integrator.setOrientationLocked(false);
        integrator.initiateScan();
    }


    /**
     * Handles the location request code
     *
     * @param grantResults: The grant results for the corresponding permissions
     */
    public void handleLocationRequestCode(@NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // If permissions are granted, call addQR() again to update the QR code's location
            addQR();
        } else {
            // Handle permission denied error
            Toast.makeText(this, "Location permissions denied.", Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Handles the camera request code
     *
     * @param grantResults: The grant results for the corresponding permissions
     */
    public void handleCameraRequestCode(@NonNull int[] grantResults) {
        if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Permission granted, start scanning
            initiateScanning();
            // Permission not granted. Ask user to manually change it in app settings and finish
            // this activity
        } else {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Camera permission required");
            builder.setMessage("Please grant camera permission manually in app settings and then try to scan again");
            builder.setPositiveButton("Go to settings", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    // Open app settings screen
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                    finish();
                }
            });
            builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Toast.makeText(ScanActivity.this, "Permission denied. Scanning canceled", Toast.LENGTH_SHORT).show();
                    finish();
                }
            });
            builder.show();
        }
    }
}


