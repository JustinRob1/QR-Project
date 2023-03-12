package com.example.qr_project.activities;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
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

        signUpButton = findViewById(R.id.sign_up_button);
        signUpButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LandingPageActivity.this, SignUpActivity.class);
                entryLauncher.launch(intent);
            }
        });
        //db = FirebaseFirestore.getInstance();
        //final CollectionReference collectionReference = db.collection("user");

        //Intent intent = new Intent(this, UserHomeActivity.class);
        //startActivity(intent);
    }
}