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
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.qr_project.R;
import com.example.qr_project.models.DatabaseResultCallback;
import com.example.qr_project.utils.QRCodeManager;
import com.example.qr_project.utils.QR_Code;
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

    private TextView qrCodeName;

    private TextView qrCodeScore;

    private TextView yourRankNum;

    private TextView friendRankNum;

    private TextView globalRankNum;

    private TextView codeDescription;

    private TextView hasUserScannedCode;

    private TableRow RemoveQRRow;

    private TextView QRFace;

    QRCodeManager qrCodeManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);


        String qr_code_hash = getIntent().getStringExtra("hash");
        qrCodeManager = new QRCodeManager(qr_code_hash);

        initViews();


        updateQRCode();
    }

    @Override
    protected void onResume (){
        super.onResume();

        updateQRCode();
    }

    private void initViews(){
        totalScans = findViewById(R.id.total_scans);
        qrCodeName = findViewById(R.id.qr_code_name);
        qrCodeScore = findViewById(R.id.qr_code_score);
        yourRankNum = findViewById(R.id.your_rank_num);
        friendRankNum = findViewById(R.id.friend_rank_num);
        globalRankNum = findViewById(R.id.global_rank_num);
        codeDescription = findViewById(R.id.code_description);
        hasUserScannedCode = findViewById(R.id.user_scanned_already_txt);
        RemoveQRRow = findViewById(R.id.remove_qr_row);
        RemoveQRRow.setVisibility(View.GONE);
        QRFace = findViewById(R.id.image_face);
    }


    /**
     * Called when the user clicks the back button
     * @param view
     * The text view which is pressed
     */
    public void onClickBack(View view){
        finish();
    }

    /**
     * When the user clicks the camera button, this method will be called
     * and will open the camera to scan a QR or barcode
     *
     * @param view The text view which is pressed
     */
    public void onCameraClick(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }


    /**
     * For the use and the feature of the map button
     *
     * @param view The text view which is pressed
     */
    public void onMapClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    /**
     * Starts the LeaderboardActivity
     * @param view The text view which is pressed
     */
    public void onLeaderboardClick(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
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

    private void updateQRCode(){
        qrCodeManager.getQRCode(new DatabaseResultCallback<QR_Code>() {
            @Override
            public void onSuccess(QR_Code result) {
                qrCodeName.setText(result.getName());
                qrCodeScore.setText(String.valueOf(result.getScore()));
                QRFace.setText(result.getFace());
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error: ", e);
            }
        });

        qrCodeManager.getGlobalRanking(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                globalRankNum.setText(String.valueOf(result));
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error with global rank: ", e);
            }
        });

        qrCodeManager.getUserQRCodeRanking(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                if (result == 0){
                    yourRankNum.setText("N/A");
                } else {
                    yourRankNum.setText(String.valueOf(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error with user rank: ", e);
            }
        });

        qrCodeManager.getFriendsQRCodeRanking(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                if (result == 0){
                    friendRankNum.setText("N/A");
                } else {
                    friendRankNum.setText(String.valueOf(result));
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        qrCodeManager.getTotalScans(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                totalScans.setText(String.valueOf(result));
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error with total scans: ", e);
            }
        });

        qrCodeManager.hasUserScanned((new DatabaseResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result == true){
                    hasUserScannedCode.setText("You have already scanned this code");
                    RemoveQRRow.setVisibility(View.VISIBLE);
                } else {
                    hasUserScannedCode.setText("You haven't scanned this code yet");
                    RemoveQRRow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error with has User scanned: ", e);
            }
        }));

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

