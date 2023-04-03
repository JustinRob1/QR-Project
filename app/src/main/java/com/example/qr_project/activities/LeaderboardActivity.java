package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.qr_project.R;
import com.example.qr_project.models.DatabaseResultCallback;
import com.example.qr_project.utils.Friend;
import com.example.qr_project.utils.Hash;
import com.example.qr_project.utils.LeaderboardManager;
import com.example.qr_project.utils.QR_Adapter;
import com.example.qr_project.utils.QR_Code;
import com.example.qr_project.utils.Score_Adapter;
import com.example.qr_project.utils.UserManager;
import com.example.qr_project.utils.UtilityFunctions;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    public TextView qr_code_filter;
    public TextView ovr_score_filter;

    public ListView leaderboard_view;

//    public TextView user_codes_title;

    public TextView leaderboardScore;

    public LinearLayout leaderboard_dial_filters;

    public AppCompatButton btn_filter_user;

    public AppCompatButton btn_filter_friends;

    public AppCompatButton btn_filter_global;

    public ArrayList<QR_Code> userQRCodes_list;

    public ArrayAdapter<QR_Code> userQRCodes_adapter;

    // TODO
    // Implement friend filters
    public ArrayList<Friend> friendScores_list;
    public ArrayList<QR_Code> friendQRCodes_list;

    public ArrayAdapter<Friend> friendScores_adapter;
    public ArrayAdapter<QR_Code> friendQRCodes_adapter;

    public ArrayList<Friend> globalScores_list;
    public ArrayList<QR_Code> globalQRCodes_list;

    public ArrayAdapter<Friend> globalScores_adapter;
    public ArrayAdapter<QR_Code> globalQRCodes_adapter;

    public Boolean is_overall_scores;
    public Boolean is_qr_codes;
    public String curr_view;

    public String TAG = "LeaderboardActivity";

    UserManager userManager;
    LeaderboardManager leaderboardManager;


    /**
     * This LeaderBoardManager is to manage the leaderboard in ranking among friends and even global
     * Populate the leaderboard through filter
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Intent intent = getIntent();

        initViews();

        userManager = UserManager.getInstance();
        leaderboardManager = new LeaderboardManager();

        // starting listeners
        totalScoreListener();
        userTopQRCodesListener();
        globalTopQRCodesListener();
        globalTopTotalScoresListener();

        // TODO
        // Implement friend listeners

        String filter = intent.getStringExtra("filter");
        curr_view = filter;

        populateData(filter);
    }

    // This is to initalize the View in leaderboard
    // Fetching all the ids from the xml files
    private void initViews(){
        leaderboard_view = findViewById(R.id.leaderboard_listview);

        // Setting up lists and adapters
        userQRCodes_list = new ArrayList<>();
        userQRCodes_adapter = new QR_Adapter(this, userQRCodes_list);

        friendScores_list = new ArrayList<>();
        friendQRCodes_list = new ArrayList<>();
        friendScores_adapter = new Score_Adapter(this, friendScores_list);
        friendQRCodes_adapter = new QR_Adapter(this, friendQRCodes_list);

        globalScores_list = new ArrayList<>();
        globalQRCodes_list = new ArrayList<>();
        globalScores_adapter = new Score_Adapter(this, globalScores_list);
        globalQRCodes_adapter = new QR_Adapter(this, globalQRCodes_list);

        qr_code_filter = findViewById(R.id.QR_code_filter);
        ovr_score_filter =  findViewById(R.id.Overall_score_filter);

        leaderboard_dial_filters = findViewById(R.id.leaderboard_dial_filter_layout);
//        user_codes_title = findViewById(R.id.title_user_qr_codes);

        btn_filter_global = findViewById(R.id.btn_filter_global);
        btn_filter_friends = findViewById(R.id.btn_filter_friends);
        btn_filter_user = findViewById(R.id.btn_filter_you);
        leaderboardScore = findViewById(R.id.leaderboard_score);

        is_overall_scores = true;
        is_qr_codes = false;
    }


    private void populateData(String filter){
        if (filter.equals("user")) {
            onUserLeaderboardView(null);

        } else if (filter.equals("friends")){
            onFriendLeaderboardView(null);

        } else {
            onGlobalLeaderboardView(null);

        }

    }

    // Listener functions
    private void totalScoreListener() {
        userManager.getRealtimeTotalScore(new DatabaseResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                leaderboardScore.setText(result);
            }

            @Override
            public void onFailure(Exception e) {
                leaderboardScore.setText("N/A");
            }
        });  // does score at the top
    }

    private void userTopQRCodesListener() {
        leaderboardManager.getRealtimeTopUserQRCodes(new DatabaseResultCallback<List<Map<String, Object>>>() {
            @Override

            public void onSuccess(List<Map<String, Object>> result) {
                userQRCodes_list.clear();
                for (Map<String, Object> qrCode : result) {

                    // Creating QR_Code object for the adapter
                    String name = String.valueOf(qrCode.get("name"));

                    // POTENTIAL ERROR
                    int score = Math.toIntExact((Long) qrCode.get("score"));

                    String face = (String) qrCode.get("face");

                    Hash hash = new Hash((String) qrCode.get("hash"), name, face, score);

                    // adding QR_Code obj to the list
                    userQRCodes_list.add(new QR_Code(hash, score, name, face));

                }

                userQRCodes_adapter.notifyDataSetChanged();

            }
            /**
             * The function throws an exception if there exists an error with global's codes
             * @param e
             * @throws Exception
             */
            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void globalTopQRCodesListener() {
        leaderboardManager.getRealtimeTopGlobalQRCodes(new DatabaseResultCallback<List<Map<String, Object>>>() {

            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                for (Map<String, Object> qrCode : result) {

                    // Creating QR_Code object for the adapter
                    String name = String.valueOf(qrCode.get("name"));

                    // POTENTIAL ERROR
                    int score = Math.toIntExact((Long) qrCode.get("score"));

                    String face = (String) qrCode.get("face");

                    Hash hash = new Hash((String) qrCode.get("hash"), name, face, score);

                    // adding QR_Code obj to the list
                    globalQRCodes_list.add(new QR_Code(hash, score, name, face));
                }

                globalQRCodes_adapter.notifyDataSetChanged();
            }
            /**
             * The function throws an exception if there exists an error with friends' codes
             * @param e
             * @throws Exception
             */
            @Override
            public void onFailure(Exception e) {

            }
        });
    }

    private void globalTopTotalScoresListener() {
        leaderboardManager.getRealtimeGlobalTotalScores(new DatabaseResultCallback<List<Friend>>() {

            @Override
            public void onSuccess(List<Friend> result) {
                for (Friend score : result) {
                    globalScores_list.add(score);
                }

                globalScores_adapter.notifyDataSetChanged();
            }
            /**
             * The function throws an exception if there exists an error with globals codes
             * @param e
             * @throws Exception
             */
            @Override
            public void onFailure(Exception e) {

            }
        });
    }


    // Common functions
    
    /**
     * This is the camera function
     * This allows user to interact with the camera button on the app
     * This allows user to open the camera right on the app
     * @see ScanActivity
     * @see QRCodeActivity
     * @see MapActivity
     * @see PictureActivity
     * @param view
     */
    public void onCameraClick(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }


    /**
     * This is the map function
     * This allows user to interact with the map button on the app
     * This allows user to open the map location right on the app
     * @see MapActivity
     * @see PictureActivity
     * @param view
     */
    public void onMapClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    /**
     * This is when the user is done with their command and actions
     * Return finish() function
     * @param view
     * @return finish()
     */
    public void onClickBack(View view){
        finish();
    }

    /**
     * This function is to take the user back to the leaderboard scree
     * Once the user is done with the map and camera, they can always return to the leaderboard screen
     * If the user is already on the leaderboard screen, the app knows about it and will tell the user
     * that "Already at leaderboard".
     * @param view
     */
    public void onLeaderboardClick(View view){
        Toast.makeText(this, "Already at leaderboard", Toast.LENGTH_SHORT).show();
    }

    // View update functions
    /**
     * This is to enable the visibility functionality on the list view of the leaderboardactivity
     * This function sets the restriction on user's visibility of the app
     * This is for UserLeaderboardView
     * @param view
     */
    public void onUserLeaderboardView(View view) {
//        user_codes_title.setVisibility(View.VISIBLE);
        curr_view = "user";
        leaderboard_dial_filters.setVisibility(View.GONE);
        btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
        btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));

        leaderboard_view.setAdapter(userQRCodes_adapter);
        // Hide all other tables other than friends
//        user_codes_title.setVisibility(View.VISIBLE);
    }


    /**
     * This is to enable the visibility functionality on the list view of the leaderboardactivity
     * This function sets the restriction on user's visibility of the app
     * This is for FriendLeaderboardView
     * @param view
     */
    public void onFriendLeaderboardView(View view){
//        user_codes_title.setVisibility(View.GONE);
        curr_view = "friends";
        leaderboard_dial_filters.setVisibility(View.VISIBLE);
        btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
        btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));

//        user_codes_title.setVisibility(View.GONE);
        if (is_overall_scores) {
            ovr_score_filter.setTypeface(null, Typeface.BOLD);
            qr_code_filter.setTypeface(null, Typeface.NORMAL);
            leaderboard_view.setAdapter(friendScores_adapter);
        } else {
            ovr_score_filter.setTypeface(null, Typeface.NORMAL);
            qr_code_filter.setTypeface(null, Typeface.BOLD);
            leaderboard_view.setAdapter(friendQRCodes_adapter);
        }

    }


    /**
     * This is to enable the visibility functionality on the list view of the leaderboardactivity
     * This function sets the restriction on user's visibility of the app
     * This is for GlobalLeaderboardView
     * @param view
     */
    public void onGlobalLeaderboardView(View view) {
//        user_codes_title.setVisibility(View.GONE);
        curr_view = "global";
        leaderboard_dial_filters.setVisibility(View.VISIBLE);
        btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));

//        user_codes_title.setVisibility(View.GONE);

        if (is_overall_scores) {
            ovr_score_filter.setTypeface(null, Typeface.BOLD);
            qr_code_filter.setTypeface(null, Typeface.NORMAL);
            leaderboard_view.setAdapter(globalScores_adapter);
        } else {
            ovr_score_filter.setTypeface(null, Typeface.NORMAL);
            qr_code_filter.setTypeface(null, Typeface.BOLD);
            leaderboard_view.setAdapter(globalQRCodes_adapter);
        }
    }

    /**
     * This filterChange functionality is to change the setting of the visibility
     * @param view
     */
    public void onFilterChange(View view) {
        Log.d(TAG, "filter change");
        if (is_overall_scores) {
            is_overall_scores = false;
            is_qr_codes = true;
        } else {
            is_overall_scores = true;
            is_qr_codes = false;
        }
        populateData(curr_view);
    }


}