package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.qr_project.R;
import com.example.qr_project.models.DatabaseResultCallback;
import com.example.qr_project.utils.Friend;
import com.example.qr_project.utils.LeaderboardManager;
import com.example.qr_project.utils.QR_Code;
import com.example.qr_project.utils.UserManager;
import com.example.qr_project.utils.UtilityFunctions;

import java.util.List;

public class LeaderboardActivity extends AppCompatActivity {

    public TextView qr_code_filter;
    public TextView ovr_score_filter;

    public TableLayout user_qr_leaderboard;
    public TableLayout friend_qr_leaderboard;
    public TableLayout global_qr_leaderboard;

    public TableLayout friend_ovr_leaderboard;
    public TableLayout global_ovr_leaderboard;

    public TextView user_codes_title;

    public TextView leaderboardScore;

    public LinearLayout leaderboard_dial_filters;

    public AppCompatButton btn_filter_user;

    public AppCompatButton btn_filter_friends;

    public AppCompatButton btn_filter_global;

    boolean isFilterChanged = false;
    boolean isUser= false;
    boolean isFriend = true;
    boolean isGlobal = false;

    boolean isUserAdded = false;
    boolean isFriendCodeAdded=false;
    boolean isFriendOvrAdded=false;
    boolean isGlobalCodeAdded= false;
    boolean isGlobalOvrAdded=false;

    List<QR_Code> userCodes;

    List<QR_Code> friendCodes;

    List<QR_Code> globalCodes;

    List<Friend> friendScores;

    List<Friend> globalScores;

    UserManager userManager;
    LeaderboardManager leaderboardManager;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Intent intent = getIntent();


        initViews();

        userManager = UserManager.getInstance();
        leaderboardManager = new LeaderboardManager();

        populateData();

        String filter = intent.getStringExtra("filter");

        populateInitialLeaderboard(filter);
    }


    private void initViews(){
        qr_code_filter = findViewById(R.id.QR_code_filter);
        ovr_score_filter =  findViewById(R.id.Overall_score_filter);
        user_qr_leaderboard = findViewById(R.id.user_qr_leaderboard_table);
        friend_qr_leaderboard = findViewById(R.id.friend_qr_leaderboard_table);
        global_qr_leaderboard = findViewById(R.id.global_qr_leaderboard_table);
        friend_ovr_leaderboard= findViewById(R.id.friend_ovr_leaderboard_table);
        global_ovr_leaderboard= findViewById(R.id.global_ovr_leaderboard_table);

        leaderboard_dial_filters = findViewById(R.id.leaderboard_dial_filter_layout);
        user_codes_title = findViewById(R.id.title_user_qr_codes);

        btn_filter_global = findViewById(R.id.btn_filter_global);
        btn_filter_friends = findViewById(R.id.btn_filter_friends);
        btn_filter_user = findViewById(R.id.btn_filter_you);
        leaderboardScore = findViewById(R.id.leaderboard_score);
    }


    private void populateInitialLeaderboard(String filter){
        userManager.getTotalScore(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                leaderboardScore.setText(String.valueOf(result));
            }

            @Override
            public void onFailure(Exception e) {
                leaderboardScore.setText("N/A");
            }
        }); // Does score at top

        // Hide all other tables other than friends
        user_qr_leaderboard.setVisibility(View.GONE);
        global_qr_leaderboard.setVisibility(View.GONE);
        friend_ovr_leaderboard.setVisibility(View.GONE);
        global_ovr_leaderboard.setVisibility(View.GONE);
        user_codes_title.setVisibility(View.GONE);
        friend_qr_leaderboard.setVisibility(View.VISIBLE);

        if (filter != null && filter.equals("user")){
            user_codes_title.setVisibility(View.VISIBLE);
            leaderboard_dial_filters.setVisibility(View.GONE);
            btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
            btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
            btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
            isUser = true;
            isFriend = false;
            isGlobal = false;
            onUserLeaderboardView(null);
        } else {
            onFriendLeaderboardView(null);
        }

    }


    private void populateData(){
        leaderboardManager.getTopUserQRCodes(new DatabaseResultCallback<List<QR_Code>>() {
            @Override
            public void onSuccess(List<QR_Code> result) {
                int rank = 1;

                if (isUserAdded ==false){
                    for (QR_Code code : result){
                        user_qr_leaderboard.addView(UtilityFunctions.createNewRow(LeaderboardActivity.this,
                                code.getName(),
                                Long.toString(code.getScore()),
                                rank,
                                code.getHash_code(),
                                R.drawable.leaderboard_row_item,
                                code.getFace(),
                                R.drawable.arrow_right_solid,
                                new Intent(LeaderboardActivity.this, QRCodeActivity.class).putExtra("hash", code.getHash_code())));

                        rank++;
                    }
                    isUserAdded = true;
                }

            }

            @Override
            public void onFailure(Exception e) {
                userCodes = null;
                Log.d(TAG, "Error with user codes", e);
            }
        });

        leaderboardManager.getTopFriendsQRCodes(new DatabaseResultCallback<List<QR_Code>>() {
            @Override
            public void onSuccess(List<QR_Code> result) {
                if (isFriendCodeAdded== false){
                    int rank = 1;
                    for (QR_Code code : result){
                        friend_qr_leaderboard.addView(UtilityFunctions.createNewRow(LeaderboardActivity.this,
                                code.getName(),
                                Integer.toString(code.getScore()),
                                rank,
                                code.getHash_code(),
                                R.drawable.leaderboard_row_item,
                                code.getFace(),
                                R.drawable.arrow_right_solid,
                                new Intent(LeaderboardActivity.this, QRCodeActivity.class).putExtra("hash", code.getHash_code())));
                        rank++;
                    }

                    isFriendCodeAdded = true;
                }
            }

            @Override
            public void onFailure(Exception e) {
                friendCodes = null;
                Log.d(TAG, "Error with friends codes", e);
            }
        });

        leaderboardManager.getTopGlobalQRCodes(new DatabaseResultCallback<List<QR_Code>>() {
            @Override
            public void onSuccess(List<QR_Code> result) {
                int rank = 1;
                if (isGlobalCodeAdded == false){
                    for (QR_Code code : result){
                        global_qr_leaderboard.addView(UtilityFunctions.createNewRow(LeaderboardActivity.this,
                                code.getName(),
                                Integer.toString(code.getScore()),
                                rank,
                                code.getHash_code(),
                                R.drawable.leaderboard_row_item,
                                code.getFace(),
                                R.drawable.arrow_right_solid,
                                new Intent(LeaderboardActivity.this, QRCodeActivity.class).putExtra("hash", code.getHash_code())));
                        rank++;
                    }
                    isGlobalCodeAdded = true;
                }


            }

            @Override
            public void onFailure(Exception e) {
                globalCodes = null;
                Log.d(TAG, "Error with global codes", e);
            }
        });

        leaderboardManager.getTopFriendsTotalScores(new DatabaseResultCallback<List<Friend>>() {
            @Override
            public void onSuccess(List<Friend> result) {
                if (isFriendOvrAdded == false){
                    int rank = 1;
                    for (Friend friend : result){
                        friend_ovr_leaderboard.addView(UtilityFunctions.createNewRow(LeaderboardActivity.this,
                                friend.getName(),
                                Integer.toString(friend.getScore()),
                                rank,
                                null,
                                R.drawable.leaderboard_row_item,
                                null,
                                R.drawable.arrow_right_solid,
                                new Intent(LeaderboardActivity.this, UserProfileActivity.class).putExtra("userId", friend.getId())));

                        rank++;
                    }
                    isFriendOvrAdded = true;
                }


            }

            @Override
            public void onFailure(Exception e) {
                friendScores = null;
                Log.d(TAG, "Error with friends scores", e);
            }
        });

        leaderboardManager.getTopGlobalTotalScores(new DatabaseResultCallback<List<Friend>>() {
            @Override
            public void onSuccess(List<Friend> result) {
                int rank = 1;
                if (isGlobalOvrAdded == false){
                    for (Friend friend : result){
                        global_ovr_leaderboard.addView(UtilityFunctions.createNewRow(LeaderboardActivity.this,
                                friend.getName(),
                                Integer.toString(friend.getScore()),
                                rank,
                                null,
                                R.drawable.leaderboard_row_item,
                                null,
                                R.drawable.arrow_right_solid,
                                new Intent(LeaderboardActivity.this, QRCodeActivity.class).putExtra("userId", friend.getId())));
                        rank++;
                    }
                    isGlobalOvrAdded = true;
                }


            }

            @Override
            public void onFailure(Exception e) {
                globalScores = null;
                Log.d(TAG, "Error with global scores", e);
            }
        });
    }

    public void onCameraClick(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }


    public void onMapClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }


    public void onClickBack(View view){
        finish();
    }

    public void onLeaderboardClick(View view){
        Toast.makeText(this, "Already at leaderboard", Toast.LENGTH_SHORT).show();
    }


    public void onUserLeaderboardView(View view) {
        user_codes_title.setVisibility(View.VISIBLE);
        leaderboard_dial_filters.setVisibility(View.GONE);
        btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
        btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));


        // Hide all other tables other than friends
        user_qr_leaderboard.setVisibility(View.VISIBLE);
        global_qr_leaderboard.setVisibility(View.GONE);
        friend_ovr_leaderboard.setVisibility(View.GONE);
        global_ovr_leaderboard.setVisibility(View.GONE);
        friend_qr_leaderboard.setVisibility(View.GONE);
        user_codes_title.setVisibility(View.VISIBLE);
        leaderboard_dial_filters.setVisibility(View.GONE);

        isUser = true;
        isFriend = false;
        isGlobal = false;

    }


    public void onFriendLeaderboardView(View view){
        user_codes_title.setVisibility(View.GONE);
        leaderboard_dial_filters.setVisibility(View.VISIBLE);
        btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
        btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));

        // Hide all other tables other than friends
        user_qr_leaderboard.setVisibility(View.GONE);
        global_qr_leaderboard.setVisibility(View.GONE);
        friend_ovr_leaderboard.setVisibility(View.GONE);
        global_ovr_leaderboard.setVisibility(View.GONE);
        friend_qr_leaderboard.setVisibility(View.VISIBLE);
        user_codes_title.setVisibility(View.GONE);
        leaderboard_dial_filters.setVisibility(View.VISIBLE);

        isUser = false;
        isFriend = true;
        isGlobal = false;

        onFilterChange(view);

    }


    public void onGlobalLeaderboardView(View view) {
        Log.d(TAG, "onGlobalLeaderboardView() called with: view = [" + view + "]");
        user_codes_title.setVisibility(View.GONE);
        leaderboard_dial_filters.setVisibility(View.VISIBLE);
        btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));

        // Hide all other tables other than friends
        user_qr_leaderboard.setVisibility(View.GONE);
        global_qr_leaderboard.setVisibility(View.VISIBLE);
        friend_ovr_leaderboard.setVisibility(View.GONE);
        global_ovr_leaderboard.setVisibility(View.GONE);
        friend_qr_leaderboard.setVisibility(View.GONE);
        user_codes_title.setVisibility(View.GONE);
        leaderboard_dial_filters.setVisibility(View.VISIBLE);

        isUser = false;
        isFriend = false;
        isGlobal = true;

        onFilterChange(view);
    }


    public void onFilterChange(View view) {
        if (isFilterChanged) {
            qr_code_filter.setTypeface(null, Typeface.BOLD);
            ovr_score_filter.setTypeface(null, Typeface.NORMAL);

            if (isFriend){
                friend_qr_leaderboard.setVisibility(View.VISIBLE);
                friend_ovr_leaderboard.setVisibility(View.GONE);
            } else if(isGlobal){
                global_qr_leaderboard.setVisibility(View.VISIBLE);
                global_ovr_leaderboard.setVisibility(View.GONE);
            }
            isFilterChanged = false;
        } else {
            qr_code_filter.setTypeface(null, Typeface.NORMAL);
            ovr_score_filter.setTypeface(null, Typeface.BOLD);

            if (isFriend){
                friend_qr_leaderboard.setVisibility(View.GONE);
                friend_ovr_leaderboard.setVisibility(View.VISIBLE);
            } else if(isGlobal){
                global_qr_leaderboard.setVisibility(View.GONE);
                global_ovr_leaderboard.setVisibility(View.VISIBLE);
            }

            isFilterChanged = true;
        }

    }


}