package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.example.qr_project.utils.Hash;
import com.example.qr_project.utils.Player;
import com.example.qr_project.utils.QR_Code;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.auth.User;
import com.google.zxing.integration.android.IntentIntegrator;
import com.google.zxing.integration.android.IntentResult;
import com.journeyapps.barcodescanner.CaptureActivity;

import java.util.HashMap;

// TODO: 1) Change layout from temporary once it's done
//       2) Implement add QRCode
//       3) Implement remove QRCode
//       4) Figure out how Player account interacts with UserHomeActivity
//            4.1) Stored as a variable of UserHomeActivity?

// TODO akhadeli
// Get scores linked

public class UserHomeActivity extends AppCompatActivity {
    QR_Code qrCode;
    Player user;
    Hash hash;
    FirebaseFirestore db;
    String userID;

    TextView totalScore;

    private ActivityResultLauncher<Intent> cameraLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_home);

        totalScore = findViewById(R.id.user_total_score);

        db = FirebaseFirestore.getInstance();


        // Define cameraLauncher
        cameraLauncher = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {
            if (result.getResultCode() == Activity.RESULT_OK) {
                // Get the image from the camera
                Intent data = result.getData();
                Bitmap image = (Bitmap) data.getExtras().get("data");

                // Add the image to the QRCode
                qrCode.setPhoto(image);
                qrCode.setName("Test name");

                // Create a hashmap of the QR_Code properties you want to store
                HashMap<String, Object> qrCodeDB = new HashMap<>();
                qrCodeDB.put("id", qrCode.getName());
                qrCodeDB.put("score", qrCode.getScore());

                db.collection("QR_Codes")
                        .add(qrCodeDB)
                        .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                            @Override
                            public void onSuccess(DocumentReference documentReference) {
                                Log.d(TAG, "DocumentSnapshot added with ID: " + documentReference.getId());
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error adding document", e);
                            }
                        });
            }
        });

        // Get userID
        userID = getIntent().getStringExtra("userId");

        //final CollectionReference collectionReference = db.collection("users");
        final DocumentReference docRef = db.collection("users").document(userID);
        docRef.get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                user = documentSnapshot.toObject(Player.class);
                totalScore.setText(String.valueOf(user.getTotalScore()));
            }
        });

    }

    /**
     * When the user clicks the camera button, this method will be called
     * and will open the camera to scan a QR or barcode
     *
     * @param view The text view which is pressed
     */
    public void onCameraClick(View view) {
        //Toast.makeText(this, "Camera Button Click", Toast.LENGTH_SHORT).show();
        IntentIntegrator integrator = new IntentIntegrator(this);
        integrator.setCaptureActivity(CaptureActivity.class);
        integrator.setOrientationLocked(false);
        integrator.setDesiredBarcodeFormats(IntentIntegrator.ALL_CODE_TYPES);
        integrator.setPrompt("Scan a QR code");
        integrator.initiateScan();
    }


    /**
     * Dummy method for map button
     *
     * @param view The text view which is pressed
     */
    public void onMapClick(View view) {
        //Toast.makeText(this, "Map Button Click", Toast.LENGTH_SHORT).show();
        TextView test = findViewById(R.id.user_total_score);
        test.setText(Integer.toString(qrCode.getScore()));
    }

    /**
     * Dummy method for leaderboard button
     */
    public void onLeaderboardClick(View view) {
        Toast.makeText(this, "Leaderboard Button Click", Toast.LENGTH_SHORT).show();
    }

    // Handle the scanning of the QR code
    // ERROR: The user is not being prompted to take a picture
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        IntentResult result = IntentIntegrator.parseActivityResult(requestCode, resultCode, data);
        if (result != null) {
            if (result.getContents() == null) {
                return;
            } else {
                // Create a hash object and pass hash into the constructor of QR_Code
                hash = new Hash(result.getContents());
                //String name = hash.generateName(result.getContents()); Fix the name
                qrCode = new QR_Code(hash, null, null);

                // Ask the user if they want to take a picture
                AlertDialog.Builder builder = new AlertDialog.Builder(this);
                builder.setMessage("Would you like to take a picture?");
                builder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Open the camera
                        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                        cameraLauncher.launch(takePictureIntent);
                    }
                });
                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        // Do nothing
                    }
                });
                builder.show();

            }
        }
    }
}

