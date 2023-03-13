package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.qr_project.R;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    public int qrCodesBoardFlag = 0;

    public TextView qr_code_filter;
    public TextView ovr_score_filter;

    FirebaseFirestore db;

    public TableLayout user_qr_leaderboard;
    public TableLayout friend_qr_leaderboard;
    public TableLayout global_qr_leaderboard;

    public TableLayout friend_ovr_leaderboard;
    public TableLayout global_ovr_leaderboard;

    public TextView user_codes_title;

    public LinearLayout leaderboard_dial_filters;

    public AppCompatButton btn_filter_user;

    public AppCompatButton btn_filter_friends;

    public AppCompatButton btn_filter_global;

    boolean isFilterChanged = false;
    boolean isUser= false;
    boolean isFriend = true;
    boolean isGlobal = false;

    boolean isUserAdded = false;
    boolean isFriendAdded=false;
    boolean isGlobalAdded= false;

    /**
     * Finds and fetches the right ID's for all the buttons
     * @param savedInstanceState   Fetching the ID's of the button and their function
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);

        Intent intent = getIntent();

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



        // Hide all other tables other than friends
        user_qr_leaderboard.setVisibility(View.GONE);
        global_qr_leaderboard.setVisibility(View.GONE);
        friend_ovr_leaderboard.setVisibility(View.GONE);
        global_ovr_leaderboard.setVisibility(View.GONE);
        user_codes_title.setVisibility(View.GONE);
        friend_qr_leaderboard.setVisibility(View.VISIBLE);





        String filter = intent.getStringExtra("filter");
        if (filter != null && filter.equals("user")){
            user_codes_title.setVisibility(View.VISIBLE);
            leaderboard_dial_filters.setVisibility(View.GONE);
            btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
            btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
            btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
            isUser = true;
            isFriend = false;
            isGlobal = false;
        }
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
     * Dummy method for map button
     * @param view
     * The text view which is pressed
     */
    public void onMapClick(View view) {
        Toast.makeText(this, "Map Button Click", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the user clicks the back button
     * @param view
     * The text view which is pressed
     */
    public void onClickBack(View view){
        finish();
    }

    public void onLeaderboardClick(View view){
        Toast.makeText(this, "Already at leaderboard", Toast.LENGTH_SHORT).show();
    }

    /**
     * Called when the user clicks the QR code button
     * Getting the user's ID and their information about the QR_Codes and their scores
     * Fetching the information stored on the FireBase FireStore Cloud
     *
     * After getting all the needed information, the code can now calculate
     * the points of the user's and also their friends' to populate the leaderboard.
     * @param view
     * The text view which is pressed
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

        db = FirebaseFirestore.getInstance();

        // Get the current user's ID
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);

        // Retrieve the user's information
        String userID = sharedPref.getString("user_id", null);

        // Get a reference to the user's document in Firestore
        DocumentReference userRef = db.collection("users").document(userID);


        // TODO: Make sure this works with real data

        // Populate the leaderboard
        if (!isUserAdded) {
            // Get the user's document data
            userRef.get().addOnSuccessListener(documentSnapshot -> {
                // Check if the document exists
                if (documentSnapshot.exists()) {
                    // Get the qrcodes array from the document data
                    List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");

                    // Use the qrCodes array to populate the leaderboard
                    // Check the contents of the qrCodes array


                    if (qrCodes != null) {
                        int rank = 1;
                        // Loop through the array
                        for (Map<String, Object> qrCode : qrCodes) {
                            String name = (String) qrCode.get("name");
                            Long score = (Long) qrCode.get("score");

                            user_qr_leaderboard.addView(createNewRow(name, score, rank));

                            rank++;

                            qrCodesBoardFlag = 1;
                        }

                    } else {
                        Log.d(TAG, "User has no QR codes");
                    }

                } else {

                    Log.d(TAG, "User document does not exist");
                }
            }).addOnFailureListener(e -> Log.e(TAG, "Error getting user document", e));

            isUserAdded= true;
        }
    }

    /**
     * Called when the user clicks the friends button
     * Allows the user to see their friends' profile(s)
     * @param view
     * The text view which is pressed
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

        // TODO: TESTING DATA FOR QR CODE
        if (!isFriendAdded){
            TableRow testRow = createNewRow("Fmpty", Long.valueOf(1232), 1);
            TableRow testRow2 = createNewRow("Fmpty2", Long.valueOf(1000), 2);

            TableRow testRow3 = createNewRow("Fmptyy", Long.valueOf(1232), 1);
            TableRow testRow4 = createNewRow("Fmptyy2", Long.valueOf(1000), 2);

            friend_qr_leaderboard.addView(testRow);
            friend_qr_leaderboard.addView(testRow2);

            friend_ovr_leaderboard.addView(testRow3);
            friend_ovr_leaderboard.addView(testRow4);
            isFriendAdded = true;
        }
    }

    /**
     * Called when the user clicks the global button
     * Moving on the global leaderboard afterward
     * @param view
     * The text view which is pressed
     */
    public void onGlobalLeaderboardView(View view) {
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


        // TODO: TESTING DATA FOR QR CODE
        if (!isGlobalAdded){
            TableRow testRow = createNewRow("Gmpty", Long.valueOf(1232), 1);
            TableRow testRow2 = createNewRow("Gmpty2", Long.valueOf(1000), 2);

            TableRow testRow3 = createNewRow("Gmptyyy", Long.valueOf(1232), 1);
            TableRow testRow4 = createNewRow("Gmptyyy2", Long.valueOf(1000), 2);

            global_qr_leaderboard.addView(testRow);
            global_qr_leaderboard.addView(testRow2);

            global_ovr_leaderboard.addView(testRow3);
            global_ovr_leaderboard.addView(testRow4);

            isGlobalAdded = true;
        }


        isUser = false;
        isFriend = false;
        isGlobal = true;
    }

    /**
     * Called when the user clicks the filter button
     * @param view
     * The text view which is pressed
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

    private TableRow createNewRow(String name, Long score, int rank){
        // Create a new TableRow
        TableRow row = new TableRow(LeaderboardActivity.this);
        row.setBackgroundResource(R.drawable.leaderboard_row_item);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(75, 30, 75, 0);
        row.setLayoutParams(layoutParams);

// Create a new LinearLayout for the TableRow
        LinearLayout linearLayout = new LinearLayout(LeaderboardActivity.this);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                Intent intent = new Intent(LeaderboardActivity.this, QRCodeActivity.class);
                intent.putExtra("qrName", "test");
                startActivity(intent);
            }
        });


// Create a new TextView for the TableRow
        TextView rankTextView = new TextView(LeaderboardActivity.this);
        rankTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        rankTextView.setText(rank+ ".");
        rankTextView.setTextColor(Color.BLACK);
        rankTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        rankTextView.setGravity(Gravity.CENTER);
        rankTextView.setPadding(10, 0, 0, 0);

// Create a new ImageView for the TableRow
        ImageView qrImageView = new ImageView(LeaderboardActivity.this);
        qrImageView.setLayoutParams(new LinearLayout.LayoutParams(75, 75, 1.0f));
        qrImageView.setImageResource(R.drawable.logo);

// Create a new TextView for the TableRow
        TextView nameTextView = new TextView(LeaderboardActivity.this);
        nameTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        nameTextView.setText(name);
        nameTextView.setTextColor(Color.BLACK);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        nameTextView.setGravity(Gravity.CENTER);
        nameTextView.setPadding(15, 0, 0, 0);

// Create a new TextView for the TableRow
        TextView scoreTextView = new TextView(LeaderboardActivity.this);
        scoreTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        scoreTextView.setText(String.valueOf(score));
        scoreTextView.setTextColor(Color.BLACK);
        scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        scoreTextView.setTypeface(null, Typeface.BOLD);
        scoreTextView.setGravity(Gravity.CENTER);
        scoreTextView.setPadding(25, 0, 0, 0);

// Create a new ImageView for the TableRow
        ImageView arrowImageView = new ImageView(LeaderboardActivity.this);
        arrowImageView.setLayoutParams(new LinearLayout.LayoutParams(75, 75, 1.0f));
        arrowImageView.setImageResource(R.drawable.arrow_right_solid);
        arrowImageView.setPadding(0, 0, 0, 0);


        linearLayout.addView(rankTextView);
        linearLayout.addView(qrImageView);
        linearLayout.addView(nameTextView);
        linearLayout.addView(scoreTextView);
        linearLayout.addView(arrowImageView);

        row.addView(linearLayout);

        row.setBackgroundResource(R.drawable.leaderboard_row_item);

        return row;
    }


}