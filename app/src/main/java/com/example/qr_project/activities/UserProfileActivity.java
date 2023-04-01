package com.example.qr_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.qr_project.R;
import com.example.qr_project.utils.Hash;
import com.example.qr_project.utils.QR_Adapter;
import com.example.qr_project.utils.QR_Code;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.NoSuchElementException;

public class UserProfileActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String userId;

    ArrayList<QR_Code> rankedQRCodes_list;
    ArrayAdapter<QR_Code> rankedQRCodes_adapter;
    ListView rankedQRCodes_view;

    AppCompatButton viewAllBtn;

    TextView username;
    TextView email;

    TextView totalScore;

    TextView globalRank;
    TextView friendRank;
    TextView totalQrCodes;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userId = getIntent().getStringExtra("userId");
        db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("users");
        DocumentReference docRef = collRef.document(userId);

        rankedQRCodes_list = new ArrayList<>();
        rankedQRCodes_adapter = new QR_Adapter(this, rankedQRCodes_list);
        rankedQRCodes_view = findViewById(R.id.user_top_qr_table);
        rankedQRCodes_view.setAdapter(rankedQRCodes_adapter);

        viewAllBtn = findViewById(R.id.view_all_btn);

        username = findViewById(R.id.username_txt);
        email = findViewById(R.id.user_email_txt);
        totalScore = findViewById(R.id.user_total_score_txt);
        globalRank = findViewById(R.id.global_user_rank);
        friendRank = findViewById(R.id.friend_user_rank);
        totalQrCodes = findViewById(R.id.user_total_qr_codes);

        /*
            Reworked snapshot listener for collRef
         */
        collRef.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) return;
                if (value == null) return;

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

                rankings.sort(Comparator.comparing(x -> x.entrySet().iterator().next().getValue()));
                String rank;
                int counter = rankings.size();
                for (Map<String, Integer> x : rankings) {
                    if (x.containsKey(userId)) {
                        globalRank.setText(String.valueOf(counter));
                        break;
                    }
                    counter--;
                }

            }
        });

        /*
            Reword snapshot listener for docRef
            fixes:
                -   new ranking method for the top 3 qr codes,
                    should be faster and more reliable
                -   identified and handled points of error,
                    still needs to be tested further
         */
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) return;
                if (value != null && value.exists()) {
                    Map<String, Object> data = value.getData();
                    if (data != null){
                        username.setText(String.valueOf(data.get("username")));
                        email.setText(String.valueOf(data.get("email")));
                        totalScore.setText(String.valueOf(data.get("totalScore")));

                        List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) data.get("qrcodes");
                        if (qrCodes != null) {
                            // Sort QR codes based on score
                            qrCodes.sort((a,b) -> ( (Long) b.get("score")).compareTo(( (Long) a.get("score")) ));
                            int rank = 1;

                            // Clear old version of the list
                            rankedQRCodes_list.clear();

                            for (Map<String, Object> qrCode : qrCodes) {
                                if (rank > 3) break;

                                // Creating QR_Code object for the adapter
                                String name = String.valueOf(qrCode.get("name"));

                                // POTENTIAL ERROR
                                int score = Math.toIntExact((Long) qrCode.get("score"));

                                // IDENTIFIED ERROR POINT
                                // Not all qr codes in the db have a face,
                                // so this call will fail for the older docs in docRef
                                String face = (String) qrCode.get("face");

                                Hash hash = new Hash((String) qrCode.get("hash"), name, face, score);

                                // adding QR_Code obj to the list
                                rankedQRCodes_list.add(new QR_Code(hash, score, name, face));

                                rank++;
                            }

                            // Make view all button visible
                            viewAllBtn.setVisibility(View.VISIBLE);

                            // Notify adapter to update dataset
                            rankedQRCodes_adapter.notifyDataSetChanged();

                            Log.d("DOCSNAP", qrCodes.toString());
                        }
                        totalScore.setText(String.valueOf(data.get("totalScore")));
                    }

                }

            }
        });

    }

    public void onViewAllClick(View view){
        Intent intent = new Intent(UserProfileActivity.this, LeaderboardActivity.class);
        intent.putExtra("filter", "user");
        startActivity(intent);
    }

    public void onClickBack(View view){
        //Intent intent = new Intent(UserProfileActivity.this, UserHomeActivity.class);
        //startActivity(intent);
        finish();
    }



    /**
     *For the use and feature of the map button
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
        Intent intent = new Intent(UserProfileActivity.this, LeaderboardActivity.class);
        startActivity(intent);
    }
}