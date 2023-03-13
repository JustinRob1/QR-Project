package com.example.qr_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;

public class UserHomeActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

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
     * Dummy method for map button
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
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

}