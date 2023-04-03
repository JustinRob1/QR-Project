package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;
import static com.google.zxing.integration.android.IntentIntegrator.REQUEST_CODE;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.app.ActivityCompat;

import com.example.qr_project.R;
import com.example.qr_project.models.DatabaseResultCallback;
import com.example.qr_project.utils.Friend;
import com.example.qr_project.utils.Hash;
import com.example.qr_project.utils.QR_Adapter;
import com.example.qr_project.utils.QR_Code;
import com.example.qr_project.utils.UserManager;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class UserHomeActivity extends AppCompatActivity {
    FirebaseFirestore db;
    String userId;

    ArrayList<QR_Code> rankedQRCodes_list;
    ArrayAdapter<QR_Code> rankedQRCodes_adapter;
    ListView rankedQRCodes_view;

    AppCompatButton viewAllBtn;

    TextView totalScore;

    TextView globalRank;
    TextView friendRank;
    TextView totalQrCodes;

    EditText searchUserTxt;

    ImageView searchUserBtn;

    UserManager userManager;
    LinearLayout friendCard1;
    TextView friendCard1Name;
    TextView friendCard1Score;
    TextView friendCard1Rank;
    LinearLayout friendCard2;
    TextView friendCard2Name;
    TextView friendCard2Score;
    TextView friendCard2Rank;
    LinearLayout friendCard3;
    TextView friendCard3Name;
    TextView friendCard3Score;
    TextView friendCard3Rank;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED &&
                ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_CODE);
        }

        userId = getIntent().getStringExtra("userId");
        userManager = UserManager.getInstance();
        userManager.setUserID(userId);

        initViews();

        // TODO
        // Since we have decided to do things in realtime
        // The current method of calling these functions
        // in the onResume lifecycle function
        // won't do
        // To fix we must implement snapshot listeners
        // within the current implementation
        // and remove these updates

        updateFriendRanking();

        //updateGlobalRanking();
        realtimeGlobalRanking();

        updateTop3Friends();

        //updateTop3QRCodes();
        realtimeTop3QRCodes();

        //updateTotalScore();
        realtimeTotalScore();

        //updateTotalQRCodes();
        realtimeTotalQRCodes();
    }


    @Override
    protected void onResume() {
        super.onResume();

        // See notes in onCreate method
        updateFriendRanking();
        //updateGlobalRanking();
        updateTop3Friends();
        //updateTop3QRCodes();
        //updateTotalScore();
        //updateTotalQRCodes();
    }

    private void initViews(){
        rankedQRCodes_list = new ArrayList<>();
        rankedQRCodes_adapter = new QR_Adapter(this, rankedQRCodes_list);
        rankedQRCodes_view = findViewById(R.id.user_top_qr_table);
        rankedQRCodes_view.setAdapter(rankedQRCodes_adapter);

        viewAllBtn = findViewById(R.id.view_all_btn);
        viewAllBtn.setVisibility(View.VISIBLE);

        totalScore = findViewById(R.id.user_total_score);
        globalRank = findViewById(R.id.global_rank);
        friendRank = findViewById(R.id.friend_rank);
        totalQrCodes = findViewById(R.id.total_qr_codes);

        searchUserTxt = findViewById(R.id.search_bar);
        searchUserBtn = findViewById(R.id.add_friend_btn);


        friendCard1 = findViewById(R.id.friend_1_card);
        friendCard1Name = findViewById(R.id.friend_1_name);
        friendCard1Score = findViewById(R.id.friend_1_score);
        friendCard1Rank = findViewById(R.id.friend_1_rank);
        friendCard2 = findViewById(R.id.friend_2_card);
        friendCard2Name = findViewById(R.id.friend_2_name);
        friendCard2Score = findViewById(R.id.friend_2_score);
        friendCard2Rank = findViewById(R.id.friend_2_rank);
        friendCard3 = findViewById(R.id.friend_3_card);
        friendCard3Name = findViewById(R.id.friend_3_name);
        friendCard3Score = findViewById(R.id.friend_3_score);
        friendCard3Rank = findViewById(R.id.friend_3_rank);
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
        intent.putExtra("filter", "user");
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

    private void updateTop3Friends(){
        friendCard1.setVisibility(View.GONE);
        friendCard2.setVisibility(View.GONE);
        friendCard3.setVisibility(View.GONE);
        userManager.getTop3FriendsSorted(new DatabaseResultCallback<List<Friend>>() {
            @Override
            public void onSuccess(List<Friend> result) {
                if (!result.isEmpty() && result.size() > 2){
                    friendCard1Name.setText((String) result.get(0).getName());
                    friendCard1Rank.setText(String.valueOf(1));
                    friendCard1Score.setText(String.valueOf(result.get(0).getScore()));

                    friendCard2Name.setText((String) result.get(1).getName());
                    friendCard2Rank.setText(String.valueOf(2));
                    friendCard2Score.setText(String.valueOf(result.get(1).getScore()));

                    friendCard3Name.setText((String) result.get(2).getName());
                    friendCard3Rank.setText(String.valueOf(3));
                    friendCard3Score.setText(String.valueOf(result.get(2).getScore()));

                    friendCard1.setVisibility(View.VISIBLE);
                    friendCard1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserHomeActivity.this, UserProfileActivity.class);
                            intent.putExtra("userId", result.get(0).getId());
                            startActivity(intent);
                        }
                    });
                    friendCard2.setVisibility(View.VISIBLE);
                    friendCard2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserHomeActivity.this, UserProfileActivity.class);
                            intent.putExtra("userId", result.get(1).getId());
                            startActivity(intent);
                        }
                    });
                    friendCard3.setVisibility(View.VISIBLE);
                    friendCard3.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserHomeActivity.this, UserProfileActivity.class);
                            intent.putExtra("userId", result.get(2).getId());
                            startActivity(intent);
                        }
                    });
                } else if (result.size() > 1){
                    friendCard1Name.setText((String) result.get(0).getName());
                    friendCard1Rank.setText(String.valueOf(1));
                    friendCard1Score.setText(String.valueOf(result.get(0).getScore()));

                    friendCard2Name.setText((String) result.get(1).getName());
                    friendCard2Rank.setText(String.valueOf(2));
                    friendCard2Score.setText(String.valueOf(result.get(1).getScore()));

                    friendCard1.setVisibility(View.VISIBLE);
                    friendCard1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserHomeActivity.this, UserProfileActivity.class);
                            intent.putExtra("userId", result.get(0).getId());
                            startActivity(intent);
                        }
                    });
                    friendCard2.setVisibility(View.VISIBLE);
                    friendCard2.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserHomeActivity.this, UserProfileActivity.class);
                            intent.putExtra("userId", result.get(1).getId());
                            startActivity(intent);
                        }
                    });
                    friendCard3.setVisibility(View.GONE);
                } else if (result.size() == 1) {
                    friendCard1Name.setText((String) result.get(0).getName());
                    friendCard1Rank.setText(String.valueOf(1));
                    friendCard1Score.setText(String.valueOf(result.get(0).getScore()));

                    friendCard1.setVisibility(View.VISIBLE);
                    friendCard1.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Intent intent = new Intent(UserHomeActivity.this, UserProfileActivity.class);
                            intent.putExtra("userId", result.get(0).getId());
                            startActivity(intent);
                        }
                    });
                    friendCard2.setVisibility(View.GONE);
                    friendCard3.setVisibility(View.GONE);
                } else {
                    friendCard1.setVisibility(View.GONE);
                    friendCard2.setVisibility(View.GONE);
                    friendCard3.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void updateTotalScore(){
        userManager.getTotalScore(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                totalScore.setText(String.valueOf(result));
            }

            @Override
            public void onFailure(Exception e) {
                totalScore.setText("N/A");
            }
        });
    }

    private void realtimeTotalScore() {
        userManager.getRealtimeTotalScore(new DatabaseResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                totalScore.setText(result);
            }

            @Override
            public void onFailure(Exception e) {
                totalScore.setText("N/A");
            }
        });
    }

    private void updateGlobalRanking(){
        userManager.getGlobalRanking(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                globalRank.setText("Global Rank: " + String.valueOf(result));
            }

            @Override
            public void onFailure(Exception e) {
                globalRank.setText("N/A");
            }
        });
    }

    private void realtimeGlobalRanking() {
        userManager.getRealtimeGlobalRanking(new DatabaseResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                globalRank.setText("Global Rank: " + result);
            }

            @Override
            public void onFailure(Exception e) {
                globalRank.setText("Global Rank: N/A");
            }
        });
    }

    private void updateFriendRanking(){
        userManager.getFriendRanking(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                friendRank.setText("Friend Rank: " + String.valueOf(result));
            }

            @Override
            public void onFailure(Exception e) {
                friendRank.setText("N/A");
            }
        });
    }

    private void updateTotalQRCodes(){
        userManager.getTotalQRCodes(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                totalQrCodes.setText("Total QR Codes: " + String.valueOf(result));
            }

            @Override
            public void onFailure(Exception e) {
                totalQrCodes.setText("Total QR Codes: N/A");
            }
        });
    }

    private void realtimeTotalQRCodes() {
        userManager.getRealtimeTotalQRCodes(new DatabaseResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                totalQrCodes.setText("Total QR Codes: " + result);
            }

            @Override
            public void onFailure(Exception e) {
                totalQrCodes.setText("Total QR Codes: N/A");
            }
        });
    }

    private void updateTop3QRCodes(){

        userManager.getTop3QRCodesSorted(new DatabaseResultCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                rankedQRCodes_list.clear();
                if (!result.isEmpty()) {
                    int rank = 1;
                    for (Map<String, Object> qrCode : result) {
                        if (rank > 3) break;

                        // Creating QR_Code object for the adapter
                        String name = String.valueOf(qrCode.get("name"));

                        // POTENTIAL ERROR
                        int score = qrCode.get("score") instanceof String ? Integer.parseInt((String) qrCode.get("score")) : Math.toIntExact((long) qrCode.get("score"));

                        // IDENTIFIED ERROR POINT
                        // Not all qr codes in the db have a face,
                        // so this call will fail for the older docs in docRef
//                        Bitmap face = (Bitmap) qrCode.get("face");
//
//                        Hash hash = new Hash((String) qrCode.get("hash"), name, face, score);

                        // adding QR_Code obj to the list
                        //rankedQRCodes_list.add(new QR_Code(hash, score, name, face));

                        rank++;
                    }

                    // Notify adapter to update dataset
                    rankedQRCodes_adapter.notifyDataSetChanged();

                    Log.d("DOCSNAP", result.toString());
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error getting top 3 codes: ", e);
            }
        });
    }

    private void realtimeTop3QRCodes() {
        userManager.getRealtimeTop3QRCodesSorted(new DatabaseResultCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                // Clear old version of the list
                rankedQRCodes_list.clear();

                for (Map<String, Object> qrCode : result) {

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
                }

                // Make view all button visible
                viewAllBtn.setVisibility(View.VISIBLE);

                // Notify adapter to update dataset
                rankedQRCodes_adapter.notifyDataSetChanged();
            }

            @Override
            public void onFailure(Exception e) {

            }
        });
    }
}