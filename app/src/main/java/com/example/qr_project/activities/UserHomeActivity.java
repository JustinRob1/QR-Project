package com.example.qr_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.example.qr_project.utils.QR_Code;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.text.CollationElementIterator;
import java.util.List;

public class UserHomeActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String userId;
    List<QR_Code> QRCodes;

    TextView totalScore;

    TextView globalRank;
    TextView friendRank;
    TextView totalQrCodes;

    TextView qrCode1Name;
    TextView qrCode1Score;
    TextView qrCode2Name;
    TextView qrCode2Score;
    TextView qrCode3Name;
    TextView qrCode3Score;

    Button viewAll;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        userId = getIntent().getStringExtra("userId");
        db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("users");
        DocumentReference docRef = collRef.document(userId);

        totalScore = findViewById(R.id.user_total_score);
        globalRank = findViewById(R.id.global_rank);
        friendRank = findViewById(R.id.friend_rank);
        totalQrCodes = findViewById(R.id.total_qr_codes);

        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                totalScore.setText(value.get("totalScore").toString());
            }
        });

    }

    /**
     * When the user clicks the camera button, this method will be called
     * and will open the camera to scan a QR or barcode
     *
     * @param view The text view which is pressed
     */
    public void onCameraClick(View view) {
        //Toast.makeText(this, "Camera Button Click", Toast.LENGTH_SHORT).show();
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }


    /**
     * For the use and the feature of the map button
     *
     * @param view The text view which is pressed
     */
    public void onMapClick(View view) {
        Toast.makeText(this, "Map Button Click", Toast.LENGTH_SHORT).show();
    }

    /**
     * Starts the LeaderboardActivity
     * @param view The text view which is pressed
     */
    public void onLeaderboardClick(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

}