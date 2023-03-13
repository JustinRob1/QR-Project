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
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.qr_project.R;
import com.example.qr_project.utils.Player;
import com.google.firebase.firestore.FirebaseFirestore;

public class LandingPageActivity extends AppCompatActivity {

    Button signUpButton;
    FirebaseFirestore db;
    String userID;

    ActivityResultLauncher<Intent> entryLauncher = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == 0) {
                        Intent intent = result.getData();
                        if (intent != null) {
                            userID = intent.getStringExtra("userId");
                        }

                        // Go to the UserHomeActivity and call finish to kill SignUpActivity
                        intent = new Intent(LandingPageActivity.this, UserHomeActivity.class);
                        intent.putExtra("userId", userID);
                        startActivity(intent);
                        finish();
                    }
                }
            }
    );

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        db = FirebaseFirestore.getInstance();

        // Get the shared preferences object
        SharedPreferences sharedPref = getSharedPreferences("my_app_pref", Context.MODE_PRIVATE);

        // Retrieve the user's information
        userID = sharedPref.getString("user_id", null);

        // If the user is already signed in, go to UserHomeActivity
        if (userID != null) {
            Intent intent = new Intent(LandingPageActivity.this, UserHomeActivity.class);
            intent.putExtra("userId", userID);
            startActivity(intent);
            finish();
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