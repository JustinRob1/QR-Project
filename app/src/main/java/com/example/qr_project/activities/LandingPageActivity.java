package com.example.qr_project.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.google.firebase.firestore.FirebaseFirestore;

public class LandingPageActivity extends AppCompatActivity {

    Button signUpButton;
    FirebaseFirestore db;
    String userID;

    /**
     * A Landing Page when an user opens an app
     * The entryLauncher allows the user to move on the sign up him/herself
     * The code also checks the results and userId (giving out the unique userId)
     * This allows the app to go to the UserHomeActivity and call finish to kill SignUpActivity
     * Also retrieving the unique userId
     * @param entryLauncher  a registration for the user
     * @param Intent  a package to go to the UserHomeActivity and call finish to kill SignUpActivity
     * @param userID  an unique ID to identify the user
     * @return        the userID if the user wants to know on their profile
     * @see           LeaderboardActivity
     * @see           SignUpActivity
     * @see           UserHomeActivity
     * @see           UserProfileActivity
     */

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

    /**
     * Connecting to the FireBase FireStore cloud to store the data
     * This code allows the app to get the shared object and retrieve the user's information stored on FireBase
     * At this, the app knows where to take the user to the next step.
     * If the user is already signed in, the app will take the user go to UserHomeActivity
     * If the user is not signed in, the app will take the user go to SignUpActivity
     * Checking the condition if the user already has an account or not
     * @param    savedInstanceState
     * @param    sharedPref
     * @see           LeaderboardActivity
     * @see           SignUpActivity
     * @see           UserHomeActivity
     * @see           UserProfileActivity
     * @see           FirebaseFirestore
     */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.landing_page);

        db = FirebaseFirestore.getInstance();

        // Get the shared preferences object
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);

        // Retrieve the user's information
        userID = sharedPref.getString("user_id", null);

        // If the user is already signed in, go to UserHomeActivity
        if (userID != null) {
            Intent intent = new Intent(LandingPageActivity.this, UserHomeActivity.class);
            intent.putExtra("userId", userID);
            startActivity(intent);
            finish();
        // If the user is not signed in, go to SignUpActivity
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