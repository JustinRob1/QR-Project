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

    // Array List of QR_Code of user
    List<QR_Code> userCodes;

    // Array List of QR_Code of user's friends
    List<QR_Code> friendCodes;

    // Array List of QR_Code of global
    List<QR_Code> globalCodes;

    // Array List of the scores of player's friend(s)
    List<Friend> friendScores;

    // Array List of the scores of player's friend(s) in global ranking
    List<Friend> globalScores;

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

        populateData();

        String filter = intent.getStringExtra("filter");

        populateInitialLeaderboard(filter);
    }

    // This is to initalize the View in leaderboard
    // Fetching all the ids from the xml files
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

    /**
     * This getTotalScore function is to get and calculate of the total score of the player(s)
     * Getting all the number then calculate the total score and return the total score to the user
     * @param filter
     */
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
            /**
             * This is the ranking system in FRIENDS in the leaderboard
             * Ranking
             * The ranking uses hash function to get the user with the highest score
             * Then returns the user's name and user's score on the app
             * Putting the information in a row
             * And then the rank will increase from the top 1 to top 2 and to top 3 and keep going
             * @param result
             */
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

            /**
             * The function throws an exception if there exists an error with friends' codes
             * @param e
             * @throws Exception
             */
            @Override
            public void onFailure(Exception e) {
                friendCodes = null;
                Log.d(TAG, "Error with friends codes", e);
            }
        });

        leaderboardManager.getTopGlobalQRCodes(new DatabaseResultCallback<List<QR_Code>>() {
            @Override
            /**
             * This is the ranking system in GLOBAL in the leaderboard
             * Ranking
             * The ranking uses hash function to get the user with the highest score
             * Then returns the user's name and user's score on the app
             * Putting the information in a row
             * And then the rank will increase from the top 1 to top 2 and to top 3
             * @param result
             */
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
            /**
             * The function throws an exception if there exists an error with global's codes
             * @param e
             * @throws Exception
             */
            @Override
            public void onFailure(Exception e) {
                globalCodes = null;
                Log.d(TAG, "Error with global codes", e);
            }
        });
        // The leaderboardManager gets the data and put in an array list.
        // This includes friends and their scores
        // Then the app will return in the ranking from highest on top to the lowest below.
        /**
         * The result of the ranking is now returned
         * This is for leaderboard and ranking among FRIENDS
         * @param result
         */
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
            /**
             * The function throws an exception if there exists an error with friends' codes
             * @param e
             * @throws Exception
             */
            @Override
            public void onFailure(Exception e) {
                friendScores = null;
                Log.d(TAG, "Error with friends scores", e);
            }
        });

        // The leaderboardManager gets the data and put in an array list.
        // This includes global players and their scores
        // Then the app will return in the ranking from highest on top to the lowest below.
        /**
         * The result of the ranking is now returned
         * This is for leaderboard and ranking in GLOBAL
         * @param result
         */
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
            /**
             * The function throws an exception if there exists an error with globals codes
             * @param e
             * @throws Exception
             */
            @Override
            public void onFailure(Exception e) {
                globalScores = null;
                Log.d(TAG, "Error with global scores", e);
            }
        });
    }

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

    /**
     * This is to enable the visibility functionality on the list view of the leaderboardactivity
     * This function sets the restriction on user's visibility of the app
     * This is for UserLeaderboardView
     * @param view
     */
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

    /**
     * This is to enable the visibility functionality on the list view of the leaderboardactivity
     * This function sets the restriction on user's visibility of the app
     * This is for FriendLeaderboardView
     * @param view
     */
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

    /**
     * This is to enable the visibility functionality on the list view of the leaderboardactivity
     * This function sets the restriction on user's visibility of the app
     * This is for GlobalLeaderboardView
     * @param view
     */
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

    /**
     * This filterChange functionality is to change the setting of the visibility
     * @param view
     */
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