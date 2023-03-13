package com.example.qr_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;

public class QRCodeActivity extends AppCompatActivity {
    /**
     * This allows to fetch the id of the "activity_qrcode" button
     * After that, it will move on the next step/page
     * @param savedInstanceState   a package to be called to execute the QRCodeActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);
    }

    /**
     * Called when the user clicks the back button
     * @param view
     * The text view which is pressed
     */
    public void onClickBack(View view){
        finish();
    }
}