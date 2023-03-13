package com.example.qr_project.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.example.qr_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class LandingPageActivity extends AppCompatActivity {

    Button signUpButton;
    FirebaseFirestore db;
    ActivityResultLauncher<Intent> entryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {

                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        // Get the shared preferences object
        SharedPreferences sharedPref = getSharedPreferences("my_app_pref", Context.MODE_PRIVATE);

        // Retrieve the user's information
        String userId = sharedPref.getString("user_id", null);
        String userName = sharedPref.getString("user_name", null);
        // If the user is already signed in, go to the main activity
        if (userId != null && userName != null) {
            // Go to the UserHomeActivity
            Intent intent = new Intent(this, UserHomeActivity.class);
            startActivity(intent);
        } else {
            signUpButton = findViewById(R.id.sign_up_button);
            signUpButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(LandingPageActivity.this, SignUpActivity.class);
                    entryLauncher.launch(intent);
                }
            });
        }

    }
}