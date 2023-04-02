package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
        String qr_code = getIntent().getStringExtra("qr_code_hash");
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
//    }

