package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.qr_project.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.Map;

public class LeaderboardActivity extends AppCompatActivity {

    public int qrCodesBoardFlag = 0;

    public TextView qr_code_filter;
    public TextView ovr_score_filter;

    FirebaseFirestore db;

    public ListView codes_list;

    public TableLayout qr_leaderboard;

    public TableLayout ovr_leaderboard;

    public TextView user_codes_title;

    public LinearLayout leaderboard_dial_filters;

    public AppCompatButton btn_filter_user;

    public AppCompatButton btn_filter_friends;

    public AppCompatButton btn_filter_global;

    boolean isFilterChanged = false;

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
        qr_leaderboard = findViewById(R.id.qr_leaderboard_table);
        ovr_leaderboard= findViewById(R.id.ovr_leaderboard_table);
        leaderboard_dial_filters = findViewById(R.id.leaderboard_dial_filter_layout);
        user_codes_title = findViewById(R.id.title_user_qr_codes);
        ovr_leaderboard.setVisibility(View.GONE);
        user_codes_title.setVisibility(View.GONE);

        btn_filter_global = findViewById(R.id.btn_filter_global);
        btn_filter_friends = findViewById(R.id.btn_filter_friends);
        btn_filter_user = findViewById(R.id.btn_filter_you);

        String filter = intent.getStringExtra("filter");
        if (filter == "user"){
            user_codes_title.setVisibility(View.VISIBLE);
            leaderboard_dial_filters.setVisibility(View.GONE);
            btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
            btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
            btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
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

        db = FirebaseFirestore.getInstance();

        // Get the current user's ID
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);

        // Retrieve the user's information
        String userID = sharedPref.getString("user_id", null);

        // Get a reference to the user's document in Firestore
        DocumentReference userRef = db.collection("users").document(userID);

        TableLayout leaderboardTable = findViewById(R.id.qr_leaderboard_table);

        // Populate the leaderboard
        if (qrCodesBoardFlag == 0) {
            // Get the user's document data
            userRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
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

                                // Create a new row for each QR code
                                TableRow row = new TableRow(LeaderboardActivity.this); // Change MainActivity to your activity name
                                // Create and set the layout parameters for the row
                                TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                                        TableRow.LayoutParams.MATCH_PARENT,
                                        TableRow.LayoutParams.WRAP_CONTENT);
                                row.setLayoutParams(layoutParams);


                                TextView rankTextView = new TextView(LeaderboardActivity.this);
                                rankTextView.setText(String.valueOf(rank));
                                rankTextView.setTextColor(Color.BLACK);
                                rankTextView.setTextSize(22);
                                rankTextView.setGravity(Gravity.CENTER);
                                rankTextView.setLayoutParams(new TableRow.LayoutParams(50, TableRow.LayoutParams.WRAP_CONTENT));

                                TextView nameTextView = new TextView(LeaderboardActivity.this);
                                nameTextView.setText(name);
                                nameTextView.setTextColor(Color.BLACK);
                                nameTextView.setTextSize(18);
                                nameTextView.setGravity(Gravity.CENTER);
                                nameTextView.setLayoutParams(new TableRow.LayoutParams(0, TableRow.LayoutParams.WRAP_CONTENT, 1));
                                nameTextView.setMaxLines(1);

                                TextView scoreTextView = new TextView(LeaderboardActivity.this);
                                scoreTextView.setText(String.valueOf(score));
                                scoreTextView.setTextColor(Color.BLACK);
                                scoreTextView.setTextSize(18);
                                scoreTextView.setTypeface(null, Typeface.BOLD);
                                scoreTextView.setGravity(Gravity.CENTER);
                                TableRow.LayoutParams scoreParams = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
                                scoreParams.setMargins(30, 0, 10, 0);
                                scoreTextView.setLayoutParams(scoreParams);

                                ImageView arrowImageView = new ImageView(LeaderboardActivity.this);
                                arrowImageView.setImageResource(R.drawable.arrow_right_solid);
                                TableRow.LayoutParams arrowParams = new TableRow.LayoutParams(25, 25);
                                arrowParams.setMargins(10, 0, 0, 0);
                                arrowImageView.setLayoutParams(arrowParams);

                                row.addView(rankTextView);
                                row.addView(nameTextView);
                                row.addView(scoreTextView);
                                row.addView(arrowImageView);

                                row.setBackgroundResource(R.drawable.leaderboard_row_item);

                                rank++;

                                leaderboardTable.addView(row);
                                qrCodesBoardFlag = 1;
                            }

                        } else {
                            Log.d(TAG, "User has no QR codes");
                        }

                    } else {
                        Log.d(TAG, "User document does not exist");
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error getting user document", e);
                }
            });
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
            qr_leaderboard.setVisibility(View.VISIBLE);
            ovr_leaderboard.setVisibility(View.GONE);
            isFilterChanged = false;
        } else {
            qr_code_filter.setTypeface(null, Typeface.NORMAL);
            ovr_score_filter.setTypeface(null, Typeface.BOLD);
            qr_leaderboard.setVisibility(View.GONE);
            ovr_leaderboard.setVisibility(View.VISIBLE);
            isFilterChanged = true;
        }

    }
}