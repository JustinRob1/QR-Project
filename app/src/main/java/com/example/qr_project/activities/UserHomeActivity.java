package com.example.qr_project.activities;

import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.qr_project.R;
import com.example.qr_project.utils.UserManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class UserHomeActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String userId;
    List<Map<String, Object>> qrCodes;

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

    EditText searchUserTxt;

    ImageView searchUserBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        userId = getIntent().getStringExtra("userId");
        UserManager.getInstance().setUserID(userId);
        db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("users");
        DocumentReference docRef = collRef.document(userId);

        totalScore = findViewById(R.id.user_total_score);
        globalRank = findViewById(R.id.global_rank);
        friendRank = findViewById(R.id.friend_rank);
        totalQrCodes = findViewById(R.id.total_qr_codes);
        qrCode1Name = findViewById(R.id.qr_code_name_1);
        qrCode2Name = findViewById(R.id.qr_code_name_2);
        qrCode3Name = findViewById(R.id.qr_code_name_3);
        qrCode1Score = findViewById(R.id.qr_code_score_1);
        qrCode2Score = findViewById(R.id.qr_code_score_2);
        qrCode3Score = findViewById(R.id.qr_code_score_3);
        searchUserTxt = findViewById(R.id.search_bar);
        searchUserBtn = findViewById(R.id.add_friend_btn);


        collRef.addSnapshotListener((value, error) -> {
            ArrayList<Map<String, Integer>> rankings = new ArrayList<>();
            for (QueryDocumentSnapshot doc : value) {
                Map<String, Integer> userId_score_pair = new HashMap<>();
                Object totalScoreObj = doc.get("totalScore");
                if (totalScoreObj != null) {
                    Long totalScoreLong = (Long) totalScoreObj;
                    userId_score_pair.put(doc.getId(), Math.toIntExact(totalScoreLong));
                    rankings.add(userId_score_pair);
                }
            }
            Collections.sort(rankings, Comparator.comparing(x -> x.entrySet().iterator().next().getValue()));
            String rank;
            int counter = rankings.size();
            for (Map<String, Integer> x : rankings) {
                if (x.containsKey(userId)) {
                    rank = "Global Rank: " + counter;
                    globalRank.setText(rank);
                    break;
                }
                counter--;
            }

        });

        docRef.addSnapshotListener((value, error) -> {
            totalScore.setText(value.get("totalScore").toString());

            qrCodes = (List<Map<String, Object>>) value.get("qrcodes");
            ArrayList<Integer> scores = new ArrayList<>();
            for (Map<String, Object> qrCode: qrCodes) {
                scores.add(Math.toIntExact((Long) qrCode.get("score")));
            }

            int maxScore = 0;
            int idx = 0;
            try {
                maxScore = Collections.max(scores);
                idx = scores.indexOf(maxScore);
                qrCode1Score.setText(String.valueOf(maxScore));
                qrCode1Name.setText((String) qrCodes.get(idx).get("name"));
                scores.remove(idx);

                maxScore = Collections.max(scores);
                idx = scores.indexOf(maxScore);
                qrCode2Score.setText(String.valueOf(maxScore));
                qrCode2Name.setText((String) qrCodes.get(idx).get("name"));
                scores.remove(idx);

                maxScore = Collections.max(scores);
                idx = scores.indexOf(maxScore);
                qrCode3Score.setText(String.valueOf(maxScore));
                qrCode3Name.setText((String) qrCodes.get(idx).get("name"));
            } catch (NoSuchElementException e) {

            }

            String totalQr;
            totalQr = "Total QR Codes: " + scores.size();
            totalQrCodes.setText(totalQr);
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

    public void onViewAllClick(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        intent.putExtra("filter", "user");
        startActivity(intent);
    }

    public void onViewUserProfile(View view) {
        Intent intent = new Intent(this, UserProfileActivity.class);
        intent.putExtra("userId", userId);
        startActivity(intent);
    }

    public void searchUser(View view){
        Intent intent = new Intent(this, UserSearchActivity.class);
        if (searchUserTxt.getText().toString().isEmpty()){
            Toast.makeText(this, "Must Enter Text To Search For User", Toast.LENGTH_SHORT).show();
        } else {
            intent.putExtra("userSearch", searchUserTxt.getText().toString());
            startActivity(intent);
        }
    }

}