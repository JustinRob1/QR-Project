package com.example.qr_project.activities;

import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;

import com.example.qr_project.R;
import com.example.qr_project.utils.Player;
import com.example.qr_project.utils.QR_Code;
import com.google.zxing.integration.android.IntentIntegrator;

public class LeaderboardActivity extends AppCompatActivity {

    public TextView qr_code_filter;
    public TextView ovr_score_filter;

    public TableLayout qr_leaderboard;

    public TableLayout ovr_leaderboard;

    public TextView user_codes_title;

    public LinearLayout leaderboard_dial_filters;

    public AppCompatButton btn_filter_user;

    public AppCompatButton btn_filter_friends;

    public AppCompatButton btn_filter_global;

    boolean isFilterChanged = false;
    // TODO: 1) These 3 fields below were copy and pasted and might need a better implmentation

    ActivityResultLauncher<Intent> cameraLauncher;

    private final QR_Code qrCode = new QR_Code(null,  null, null );

    private final Player user = new Player(null, null, null, 0, null);



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leaderboard);


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
    }

    /**
     * Handles Camera Icon being clicked
     * @param view
     * The text view which is pressed
     */
    public void onCameraClick(View view) {
        IntentIntegrator integrator = new IntentIntegrator(LeaderboardActivity.this);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();

        new Handler().postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(LeaderboardActivity.this);
            builder.setMessage("Do you want to take a picture?");
            builder.setCancelable(false);
            builder.setPositiveButton("Yes", (dialog, id) -> {
                // Open the camera app
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                cameraLauncher.launch(takePictureIntent);
            });
            builder.setNegativeButton("No", (dialog, id) -> {
                // Create a new QR_code object with the scanned result and null photo
                user.addQRCode(qrCode);
            });
            AlertDialog alert = builder.create();
            alert.show();
        }, 2000);
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
     * Dummy method for leaderboard button
     */
    public void onLeaderboardClick(View view) {
        Intent intent = new Intent(LeaderboardActivity.this, LeaderboardActivity.class);
        startActivity(intent);
    }

    public void onUserLeaderboardView(View view){
        user_codes_title.setVisibility(View.VISIBLE);
        leaderboard_dial_filters.setVisibility(View.GONE);
        btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
        btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
    }

    public void onFriendLeaderboardView(View view){
        user_codes_title.setVisibility(View.GONE);
        leaderboard_dial_filters.setVisibility(View.VISIBLE);
        btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
        btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
    }

    public void onGlobalLeaderboardView(View view){
        user_codes_title.setVisibility(View.GONE);
        leaderboard_dial_filters.setVisibility(View.VISIBLE);
        btn_filter_user.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_friends.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns));
        btn_filter_global.setBackground(ContextCompat.getDrawable(getApplicationContext(), R.drawable.leaderboard_filter_btns_selected));
    }


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