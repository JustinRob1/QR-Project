package com.example.qr_project;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.zxing.integration.android.IntentIntegrator;

import java.util.Map;

// TODO: 1) Change layout from temporary once it's done
//       2) Implement add QRCode
//       3) Implement remove QRCode
//       4) Figure out how Player account interacts with UserHomeActivity
//            4.1) Stored as a variable of UserHomeActivity?

public class UserHomeActivity extends AppCompatActivity {
    private final QR_Code qrCode = new QR_Code(null, 0, "Test", null );
    private final Player user = new Player(null, null, null, 0, null);

    FirebaseFirestore db;

    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        db = FirebaseFirestore.getInstance();
        final CollectionReference qrCodeCollection = db.collection("qr_codes");

        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        // Get the image data from the camera activity
                        assert result.getData() != null;
                        Bundle extras = result.getData().getExtras();
                        Bitmap image = (Bitmap) extras.get("data");

                        qrCode.setPhoto(image);
                        Map<String, Object> qrCodeMap = qrCode.toMap();
                        Log.d(TAG, "QR code map: " + qrCodeMap);
                        user.addQRCode(qrCode);
                        qrCodeCollection.add(qrCodeMap)
                                .addOnSuccessListener(documentReference -> Log.d(TAG, "QR code added with ID: " + documentReference.getId()))
                                .addOnFailureListener(e -> Log.w(TAG, "Error adding QR code", e));
                    }
                });
    }

    /**
     * Handles Camera Icon being clicked
     * @param view
     * The text view which is pressed
     */
    public void onCameraClick(View view) {
        IntentIntegrator integrator = new IntentIntegrator(UserHomeActivity.this);
        integrator.setOrientationLocked(false);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();

        new Handler().postDelayed(() -> {
            AlertDialog.Builder builder = new AlertDialog.Builder(UserHomeActivity.this);
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
        Toast.makeText(this, "Leaderboard Button Click", Toast.LENGTH_SHORT).show();
    }


    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == IntentIntegrator.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            // Scan result is available
            assert data != null;
            String scanResult = data.getStringExtra("SCAN_RESULT");
            Hash hash = new Hash(scanResult);
            int score = hash.getScore();
            qrCode.setHash(hash);
            qrCode.setScore(score);

        }
    }
}