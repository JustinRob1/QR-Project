package com.example.qr_project.activities;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.example.qr_project.utils.Player;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    EditText usernameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    Button signUpButton;

    FirebaseFirestore db;

    public SignUpActivity(Comparable<String> user_name, String email, String phone) {
    }

    public SignUpActivity() {

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        signUpButton = findViewById(R.id.submit_sign_up_button);
        usernameEditText = findViewById(R.id.username_edit_text);
        emailEditText = findViewById(R.id.email_edit_text);
        phoneNumberEditText = findViewById(R.id.number_edit_text);

        db = FirebaseFirestore.getInstance();

        signUpButton.setOnClickListener(v -> {
            // Get the information that the user entered
            String userID = generateUserID();
            String username = usernameEditText.getText().toString();
            String email = emailEditText.getText().toString();
            String phoneNumber = phoneNumberEditText.getText().toString();

            // Ask if the user wants to share their location
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("Share Location");
            builder.setMessage("Would you like to share your location?");
            builder.setPositiveButton("Yes", (dialog, which) -> {
                // Store the user's location preference
                SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("location_pref", true);
                editor.apply();

                // Create a new user with the information
                Player user = new Player(username, email, phoneNumber, 0, userID);
                db.collection("users").document(userID).set(user);

                // Store userID
                Intent intent = new Intent();
                intent.putExtra("userId", userID);

                // Result code 0 indicating sign up complete
                setResult(0, intent);
                finish();
            });
            builder.setNegativeButton("No", (dialog, which) -> {
                // Store the user's location preference
                SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPref.edit();
                editor.putBoolean("location_pref", false);
                editor.apply();

                // Create a new user with the information
                Player user = new Player(username, email, phoneNumber, 0, userID);
                db.collection("users").document(userID).set(user);

                // Store userID
                Intent intent = new Intent();
                intent.putExtra("userId", userID);

                // Result code 0 indicating sign up complete
                setResult(0, intent);
                finish();
            });
            builder.show();

            // Get the shared preferences object
            SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);

            // Store the user's information
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("user_id", userID);
            editor.apply();
        });

    }


    /** 
        * Generates a new user ID
        * @return a new user ID
     */
    public String generateUserID() {
        // Generate a new UUID
        UUID uuid = UUID.randomUUID();
        // Convert the UUID to a string and return it
        return uuid.toString();
    }

    // Everything below this line could be deleted if necessary
    public void SignUpClick(View view) {
        Intent intent = new Intent(SignUpActivity.this, UserHomeActivity.class);
        startActivity(intent);
    }
}