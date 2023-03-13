package com.example.qr_project.activities;



import android.view.View;


import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.widget.Button;
import android.widget.EditText;


import com.example.qr_project.R;
import com.example.qr_project.utils.Player;
import com.example.qr_project.utils.QR_Code;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.List;
import java.util.UUID;

public class SignUpActivity extends AppCompatActivity {
    EditText usernameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    Button signUpButton;

    FirebaseFirestore db;

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

            // TODO
            // Check if that username already exist
            // Check for valid input (valid characters)
            // Fix the phone number input

            // Create a new user with the information
            Player user = new Player(username, email, phoneNumber, 0, userID);

            db.collection("users").document(userID).set(user);

            // Get the shared preferences object
            SharedPreferences sharedPref = getSharedPreferences("my_app_pref", Context.MODE_PRIVATE);

            // Store the user's information
            SharedPreferences.Editor editor = sharedPref.edit();
            editor.putString("user_id", userID);
            editor.apply();

            // Store userID
            Intent intent = new Intent();
            intent.putExtra("userId", userID);

            // Result code 0 indicating sign up complete
            setResult(0, intent);

            finish();
        });
    }


    public String generateUserID() {
        // Generate a new UUID
        UUID uuid = UUID.randomUUID();
        // Convert the UUID to a string and return it
        return uuid.toString();
    }

    public void SignUpClick(View view) {
        Intent intent = new Intent(SignUpActivity.this, UserHomeActivity.class);
        startActivity(intent);
    }



}