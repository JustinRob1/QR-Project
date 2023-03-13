package com.example.qr_project.activities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;

public class SignUpActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);
    }

    public void SignUpClick(View view) {
        Intent intent = new Intent(SignUpActivity.this, UserHomeActivity.class);
        startActivity(intent);
    }



}