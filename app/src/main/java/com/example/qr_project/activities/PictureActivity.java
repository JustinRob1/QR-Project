package com.example.qr_project.activities;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PictureActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;

    private String qrHash;
    private String userID;
    private Uri photoUri;

    private StorageReference storageReference;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Inflate the loading screen layout
        View loadingScreen = getLayoutInflater().inflate(R.layout.loading_screen, null);
        setContentView(loadingScreen);

        // get QR code hash and user ID from intent extras
        Intent intent = getIntent();
        qrHash = intent.getStringExtra("qrHash");
        userID = intent.getStringExtra("userID");

        mAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        if (mAuth.getCurrentUser() != null) {
            takePicture();
        } else {
            mAuth.signInAnonymously()
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            takePicture();
                        } else {
                            Toast.makeText(PictureActivity.this, "Failed to sign in anonymously", Toast.LENGTH_SHORT).show();
                            finish();
                        }
                    });
        }
    }

    private void takePicture() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST_CODE);
    }

    @Override
    public void onBackPressed() {
        // Get the QR code hash and user ID from the intent
        Log.d("MyActivity", "onBackPressed() called");
        Intent intent = getIntent();
        String qrCodeHash = intent.getStringExtra("qrHash");
        String userID = intent.getStringExtra("userID");

        // Delete the QR code from the database
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        DocumentReference userRef = db.collection("users").document(userID);
        db.runTransaction(transaction -> {
                    DocumentSnapshot userSnapshot = transaction.get(userRef);

                    // Get the current QR codes array and total score from the user document
                    List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) userSnapshot.get("qrcodes");
                    int totalScore = ((Long) Objects.requireNonNull(userSnapshot.get("totalScore"))).intValue();

                    // Find the index of the QR code with the specified hash value in the array
                    int qrCodeIndex = -1;
                    int qrCodeScore = 0;
                    for (int j = 0; j < Objects.requireNonNull(qrCodes).size(); j++) {
                        String hash = (String) qrCodes.get(j).get("hash");
                        assert hash != null;
                        if (hash.equals(qrCodeHash)) {
                            qrCodeIndex = j;
                            qrCodeScore = ((Long) Objects.requireNonNull(qrCodes.get(j).get("score"))).intValue(); // Get the score of the deleted QR code
                            break;
                        }
                    }

                    if (qrCodeIndex >= 0) {
                        // Remove the QR code from the array
                        qrCodes.remove(qrCodeIndex);

                        // Update the user document with the updated QR codes array and total score
                        transaction.update(userRef, "qrcodes", qrCodes);
                        transaction.update(userRef, "totalScore", totalScore - qrCodeScore); // Subtract the score of the deleted QR code
                    }

                    return null;
                })
                .addOnSuccessListener(aVoid -> {
                    Log.d("My Tag", "QR code removed from database.");
                    finish();
                })
                .addOnFailureListener(e -> {
                    Log.e("My Tag", "Error removing QR code from database.", e);
                    finish();
                });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");

            // Upload the photo to storage
            photoUri = getImageUri(getApplicationContext(), imageBitmap);
            StorageReference photoRef = storageReference.child(userID).child(qrHash).child("photo");
            photoRef.putFile(photoUri)
                    .addOnSuccessListener(taskSnapshot -> {
                        photoRef.getDownloadUrl()
                                .addOnSuccessListener(uri -> {
                                    saveImageUrlToFirestore(uri.toString());
                                    finish(); // Finish activity once the image is uploaded
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PictureActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                    finish();
                                });
                    })
                    .addOnFailureListener(e -> {
                        Toast.makeText(PictureActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                        finish();
                    });
        } else {
            Toast.makeText(this, "Error: Image capture failed.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }


    private void saveImageUrlToFirestore(String imageUrl) {
        if (imageUrl != null) {
            Log.d("My Tag", "Picture Activity called with: imageUrl = [" + imageUrl + "]");
        } else {
            Toast.makeText(PictureActivity.this, "Image URI is null or empty", Toast.LENGTH_SHORT).show();
            return;
        }

        db.collection("users").document(userID).get().addOnSuccessListener(documentSnapshot -> {
            // Check if the document exists
            if (documentSnapshot.exists()) {
                // Get the qrcodes array from the document data
                List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");
                assert qrCodes != null;
                for (Map<String, Object> qrCode : qrCodes) {
                    String hash = (String) qrCode.get("hash");
                    if (Objects.equals(hash, qrHash)) {
                        // Update the photo or image of the QR code with the imageUrl
                        qrCode.put("photo", imageUrl);

                        // Update the document in Firestore with the modified qrcodes array
                        db.collection("users").document(userID).update("qrcodes", qrCodes)
                                .addOnSuccessListener(aVoid -> {

                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PictureActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                });
                    }
                }
            }
        });
    }

    private Uri getImageUri(Context context, Bitmap imageBitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 640, 480, true);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        boolean success = resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        if (!success) {
            Log.e("MY tag", "Failed to compress bitmap");
            return null;
        }
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "Title");
        values.put(MediaStore.Images.Media.DESCRIPTION, "Description");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        Uri uri = context.getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);
        if (uri == null) {
            Log.e("MY tag", "Failed to insert image into media store");
            return null;
        }
        OutputStream outputStream;
        try {
            outputStream = context.getContentResolver().openOutputStream(uri);
            if (outputStream == null) {
                Log.e("MY tag", "Failed to get output stream for uri");
                return null;
            }
            bytes.writeTo(outputStream);
            outputStream.close();
            bytes.close();
        } catch (IOException e) {
            Log.e("MY tag", "Failed to write image to media store");
            return null;
        }
        return uri;
    }
}
