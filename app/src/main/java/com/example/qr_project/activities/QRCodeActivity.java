package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRCodeActivity extends AppCompatActivity {
    /**
     * This allows to fetch the id of the "activity_qrcode" button
     * After that, it will move on the next step/page
     * @param savedInstanceState   a package to be called to execute the QRCodeActivity
     */

    FirebaseFirestore db;

    private TextView totalScans;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);

        db = FirebaseFirestore.getInstance();

        String qr_code_hash = getIntent().getStringExtra("hash");
        DocumentReference qrCodeRef = db.collection("qrcodes").document(qr_code_hash);

        totalScans = findViewById(R.id.total_scans);

        // Attach a listener to the QR code document to update the number of times it was scanned
        qrCodeRef.addSnapshotListener((snapshot, e) -> {
            if (e != null) {
                Log.w("MY TAG", "Listen failed.", e);
                return;
            }

            if (snapshot != null && snapshot.exists()) {
                // Get the users array from the QR code document
                ArrayList<String> users = (ArrayList<String>) snapshot.get("users");

                // Update the TextView for the number of times the QR code was scanned
                totalScans.setText(String.valueOf(users.size()));
            } else {
                Log.d("MY TAG", "Current data: null");
            }
        });
    }

    /**
     * Called when the user clicks the back button
     * @param view
     * The text view which is pressed
     */
    public void onClickBack(View view){
        finish();
    }

    public void onDeleteClick(View view) {
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("user_id", null);

        String deleteHash = getIntent().getStringExtra("hash");

        DocumentReference userRef = db.collection("users").document(userID);

        new AlertDialog.Builder(this)
                .setTitle("Delete QR code")
                .setMessage("Are you sure you want to delete this QR code?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.runTransaction(transaction -> {
                            DocumentSnapshot userSnapshot = transaction.get(userRef);

                            // Get the current QR codes array and total score from the user document
                            List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) userSnapshot.get("qrcodes");
                            int totalScore = ((Long) userSnapshot.get("totalScore")).intValue();

                            // Find the index of the QR code with the specified hash value in the array
                            int qrCodeIndex = -1;
                            int qrCodeScore = 0;
                            for (int j = 0; j < qrCodes.size(); j++) {
                                String hash = (String) qrCodes.get(j).get("hash");
                                if (hash.equals(deleteHash)) {
                                    qrCodeIndex = j;
                                    qrCodeScore = ((Long) qrCodes.get(j).get("score")).intValue(); // Get the score of the deleted QR code
                                    break;
                                }
                            }

                            if (qrCodeIndex >= 0) {
                                // Remove the QR code from the array
                                qrCodes.remove(qrCodeIndex);

                                // Update the user document with the updated QR codes array and total score
                                transaction.update(userRef, "qrcodes", qrCodes);
                                transaction.update(userRef, "totalScore", totalScore - qrCodeScore); // Subtract the score of the deleted QR code
                            }

                            return null;
                        }).addOnSuccessListener(result -> {
                            // The QR code was successfully deleted
                            Toast.makeText(getApplicationContext(), "QR code has been deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }).addOnFailureListener(e -> {
                            // An error occurred while deleting the QR code
                            Log.e("TAG", "Error deleting QR code from user's qrcodes array: " + e.getMessage());
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void seePhoto(View view) {
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("user_id", null);

        String qrCodeHash = getIntent().getStringExtra("hash");

        DocumentReference userRef = db.collection("users").document(userID);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");
                if (qrCodes != null) {
                    for (Map<String, Object> qrCode : qrCodes) {
                        String hash = (String) qrCode.get("hash");
                        if (hash != null && hash.equals(qrCodeHash)) {
                            String photoUrl = (String) qrCode.get("photo");
                            if (photoUrl != null) {
                                // Load the photo using Picasso
                                Picasso.get().load(photoUrl).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        // Display the photo in a dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeActivity.this);
                                        ImageView imageView = new ImageView(QRCodeActivity.this);
                                        imageView.setImageBitmap(bitmap);
                                        builder.setView(imageView);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }

                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        // Handle the error
                                        Toast.makeText(getApplicationContext(), "Failed to load photo", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        // Show a progress bar or placeholder image
                                    }
                                });
                            }
                            break;
                        }
                    }
                }
            }
        }).addOnFailureListener(e -> {
            // Handle the error
            Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
        });
    }

    public void seeLocation(View view) {
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("user_id", null);

        String qrCodeHash = getIntent().getStringExtra("hash");

        DocumentReference userRef = db.collection("users").document(userID);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) document.get("qrcodes");
                    if (qrCodes != null) {
                        for (Map<String, Object> qrCode : qrCodes) {
                            String hash = (String) qrCode.get("hash");
                            if (hash.equals(qrCodeHash)) {
                                GeoPoint location = (GeoPoint) qrCode.get("location");
                                if (location != null) {
                                    Intent intent = new Intent(this, MapActivity.class);
                                    intent.putExtra("latitude", location.getLatitude());
                                    intent.putExtra("longitude", location.getLongitude());
                                    startActivity(intent);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "Error getting document: ", task.getException());
            }
        });
    }


    
    /*
    just a demo, not fully completed yet
    please build on
     */
    

    public void AddComment() {
        // Create a new QR code document
        Map<String, Object> qrCode = new HashMap<>();
        qrCode.put("comment", "This is a comment");
        qrCode.put("timestamp", new Date());
        qrCode.put("location", new GeoPoint(37.4219999, -122.0840575));
        db.collection("QR Codes").document("<QR code ID>").set(qrCode);
        FirebaseFirestore db = FirebaseFirestore.getInstance();

        // Query the QR code document by ID
        Task<DocumentSnapshot> documentSnapshotTask = db.collection("QR Codes").document("<QR code ID>").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        // Retrieve the comment and display it in the UI
                        String comment = document.getString("comment");
                        // AddComment().getText(comment);
                    } else {
                        Log.d(TAG, "No such document");
                    }
                } else {
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }
}


//        public void AddComment() {
//            // Create a new QR code document
//            Map<String, Object> qrCode = new HashMap<>();
//            qrCode.put("comment", "This is a comment");
//            qrCode.put("timestamp", new Date());
//            qrCode.put("location", new GeoPoint(37.4219999, -122.0840575));
//            db.collection("QR Codes").document("<QR code ID>").set(qrCode);
//            FirebaseFirestore db = FirebaseFirestore.getInstance();
//
//            // Query the QR code document by ID
//            Task<DocumentSnapshot> documentSnapshotTask = db.collection("QR Codes").document("<QR code ID>").get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
//                @Override
//                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
//                    if (task.isSuccessful()) {
//                        DocumentSnapshot document = task.getResult();
//                        if (document.exists()) {
//                            // Retrieve the comment and display it in the UI
//                            String comment = document.getString("comment");
//                            AddComment().getText(comment);
//                        } else {
//                            Log.d(TAG, "No such document");
//                        }
//                    } else {
//                        Log.d(TAG, "get failed with ", task.getException());
//                    }
//                }
//            });
//        }

