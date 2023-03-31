package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;
import static android.view.View.GONE;

import static com.example.qr_project.utils.UserManager.containsUserID;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.example.qr_project.R;
import com.example.qr_project.models.DatabaseResultCallback;
import com.example.qr_project.utils.UserManager;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class UserProfileActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String userId;
    List<Map<String, Object>> qrCodes;

    List<Map<String, Object>> friends;

    TextView usernameTxt;
    TextView emailTxt;

    TextView totalScoreTxt;

    TextView globalRankTxt;
    TextView friendRankTxt;
    TextView totalQrCodesTxt;

    TextView qrCode1NameTxt;
    TextView qrCode1ScoreTxt;
    TextView qrCode2NameTxt;
    TextView qrCode2ScoreTxt;
    TextView qrCode3NameTxt;
    TextView qrCode3ScoreTxt;

    TextView tableTitleText;

    AppCompatButton addFriendBtn;

    UserManager userManager;

    UserManager otherUserManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_profile);

        userId = getIntent().getStringExtra("userId");
        //String currentUserInstance = UserManager.getInstance().getUserId(); // User who is signed in
        db = FirebaseFirestore.getInstance();
        CollectionReference collRef = db.collection("users");
        DocumentReference docRef = collRef.document(userId);

        // Setup User classes
        userManager = UserManager.getInstance();
        otherUserManager = UserManager.newInstance(getIntent().getStringExtra("userId"));


        usernameTxt = findViewById(R.id.username_txt);
        emailTxt = findViewById(R.id.user_email_txt);
        totalScoreTxt = findViewById(R.id.user_total_score_txt);
        globalRankTxt = findViewById(R.id.global_user_rank);
        friendRankTxt = findViewById(R.id.friend_user_rank);
        totalQrCodesTxt = findViewById(R.id.user_total_qr_codes);
        qrCode1NameTxt = findViewById(R.id.qr_code_name_1);
        qrCode2NameTxt = findViewById(R.id.qr_code_name_2);
        qrCode3NameTxt = findViewById(R.id.qr_code_name_3);
        qrCode1ScoreTxt = findViewById(R.id.qr_code_score_1);
        qrCode2ScoreTxt = findViewById(R.id.qr_code_score_2);
        qrCode3ScoreTxt = findViewById(R.id.qr_code_score_3);
        tableTitleText = findViewById(R.id.table_header);
        addFriendBtn = findViewById(R.id.add_friend_btn);




        userManager.getFriends(new DatabaseResultCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                friends = result;
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error getting friends", e);
            }
        });


        if (userManager.getUserID().equals(otherUserManager.getUserID())) {
            // Your own profile
            tableTitleText.setText("Your Top Codes");
            addFriendBtn.setVisibility(GONE);
            userManager.getGlobalRanking(new DatabaseResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    globalRankTxt.setText(String.valueOf(result));
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "Error finding global ranking", e);
                }
            });
            userManager.getFriendRanking(new DatabaseResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    friendRankTxt.setText(String.valueOf(result));
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "Error finding friend ranking", e);
                }
            });
            userManager.getUsername(new DatabaseResultCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    usernameTxt.setText(result);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to get username", e);
                }
            });
            userManager.getEmail(new DatabaseResultCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    emailTxt.setText(result);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to get email", e);
                }
            });
            userManager.getTotalScore(new DatabaseResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    totalScoreTxt.setText(String.valueOf(result));
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to get total score", e);
                }
            });

            userManager.getTotalQRCodes(new DatabaseResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    totalQrCodesTxt.setText(String.valueOf(result));
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "Error getting total QR Codes", e);
                }
            });

        } else {
            //tableTitleText.setText(otherUserManager.getUsername() + " Top Codes");
            addFriendBtn.setVisibility(View.VISIBLE);
            otherUserManager.getGlobalRanking(new DatabaseResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    globalRankTxt.setText(String.valueOf(result));
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "Error finding global ranking", e);
                }
            });

            otherUserManager.getFriendRanking(new DatabaseResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    if (result == 0){
                        friendRankTxt.setText("NA");
                    } else {
                        friendRankTxt.setText(String.valueOf(result));
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "Error finding friend ranking", e);
                }
            });

            otherUserManager.getUsername(new DatabaseResultCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    usernameTxt.setText(result);
                    tableTitleText.setText(result + "s Top Codes");
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to get username", e);
                }
            });
            otherUserManager.getEmail(new DatabaseResultCallback<String>() {
                @Override
                public void onSuccess(String result) {
                    emailTxt.setText(result);
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to get email", e);
                }
            });

            otherUserManager.getTotalScore(new DatabaseResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    totalScoreTxt.setText(String.valueOf(result));
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to get total score", e);
                }
            });

            otherUserManager.getTop3QRCodesSorted(new DatabaseResultCallback<List<Map<String, Object>>>() {
                @Override
                public void onSuccess(List<Map<String, Object>> result) {
                    if (!result.isEmpty()) {
                        if (result.size() > 2){
                            Map<String, Object> firstQRCode = result.get(0);
                            qrCode1NameTxt.setText(firstQRCode.get("name").toString().length() > 6 ? firstQRCode.get("name").toString().substring(0, 7)+ "..": firstQRCode.get("name").toString());
                            qrCode1ScoreTxt.setText(String.valueOf(firstQRCode.get("score")));

                            Map<String, Object> secondQRCode = result.get(1);
                            qrCode2NameTxt.setText(secondQRCode.get("name").toString().length() > 6 ? secondQRCode.get("name").toString().substring(0, 7)+ "..": secondQRCode.get("name").toString());
                            qrCode2ScoreTxt.setText(String.valueOf(secondQRCode.get("score")));

                            Map<String, Object> thirdQRCode = result.get(2);
                            qrCode2NameTxt.setText(thirdQRCode.get("name").toString().length() > 6 ? thirdQRCode.get("name").toString().substring(0, 7)+ "..": thirdQRCode.get("name").toString());
                            qrCode2ScoreTxt.setText(String.valueOf(thirdQRCode.get("score")));
                        } else if (result.size() == 2){
                            Map<String, Object> firstQRCode = result.get(0);
                            qrCode1NameTxt.setText(firstQRCode.get("name").toString().length() > 6 ? firstQRCode.get("name").toString().substring(0, 7)+ "..": firstQRCode.get("name").toString());
                            qrCode1ScoreTxt.setText(String.valueOf(firstQRCode.get("score")));

                            Map<String, Object> secondQRCode = result.get(1);
                            qrCode2NameTxt.setText(secondQRCode.get("name").toString().length() > 6 ? secondQRCode.get("name").toString().substring(0, 7)+ "..": secondQRCode.get("name").toString());
                            qrCode2ScoreTxt.setText(String.valueOf(secondQRCode.get("score")));
                        } else if (result.size() == 1){
                            Map<String, Object> firstQRCode = result.get(0);
                            qrCode1NameTxt.setText(firstQRCode.get("name").toString().length() > 6 ? firstQRCode.get("name").toString().substring(0, 7)+ "..": firstQRCode.get("name").toString());
                            qrCode1ScoreTxt.setText(String.valueOf(firstQRCode.get("score")));
                        }

                    } else {
                        Log.d(TAG, "No QR codes found");
                    }
                }

                @Override
                public void onFailure(Exception e) {
                    Log.e(TAG, "Failed to get QR codes", e);
                }
            });
            otherUserManager.getTotalQRCodes(new DatabaseResultCallback<Integer>() {
                @Override
                public void onSuccess(Integer result) {
                    totalQrCodesTxt.setText(String.valueOf(result));
                }

                @Override
                public void onFailure(Exception e) {
                    Log.d(TAG, "Error getting total QR Codes", e);
                }
            });

            if (friends != null && containsUserID(friends, otherUserManager.getUserID())) {
                addFriendBtn.setBackgroundResource(R.drawable.red_round_btn);
                addFriendBtn.setText("Remove friend");
            } else {
                addFriendBtn.setBackgroundResource(R.drawable.green_round_btn);
                addFriendBtn.setText("Add friend");
            }
        }
    }
    /*
        docRef.addSnapshotListener(new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                username.setText((String) value.get("username"));
                email.setText((String) value.get("email"));
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
                totalQr = "Total QR Codes: " + String.valueOf(scores.size());
                totalQrCodes.setText(totalQr);
            }
        });

    }
    */


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