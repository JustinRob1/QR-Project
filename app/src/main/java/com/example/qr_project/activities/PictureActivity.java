package com.example.qr_project.activities;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class PictureActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;

    private String qrHash;
    private String userID;
    private Uri imageUri;

    private StorageReference storageReference;
    private FirebaseFirestore db;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

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
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            imageUri = getImageUri(getApplicationContext(), imageBitmap);
            uploadImageToStorage();
        } else {
            Toast.makeText(this, "Error: Image capture failed.", Toast.LENGTH_SHORT).show();
            finish();
        }
    }

    private void uploadImageToStorage() {
        StorageReference imageRef = storageReference.child(userID).child(qrHash + ".jpg");
        imageRef.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    imageRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                saveImageUrlToFirestore(uri.toString());
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(PictureActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(PictureActivity.this, "Failed to upload image line 93", Toast.LENGTH_SHORT).show();
                });
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        if (imageUrl != null) {
            Log.d("My Tag", "saveImageUrlToFirestore() called with: imageUrl = [" + imageUrl + "]");
        } else {
            Toast.makeText(PictureActivity.this, "Image URI is null or empty", Toast.LENGTH_SHORT).show();
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
                        // Update the photo of the QR code with the imageUrl
                        qrCode.put("photo", imageUrl);

                        // Update the document in Firestore with the modified qrcodes array
                        db.collection("users").document(userID).update("qrcodes", qrCodes)
                                .addOnSuccessListener(aVoid -> {
                                    Toast.makeText(PictureActivity.this, "Image uploaded successfully", Toast.LENGTH_SHORT).show();
                                    finish();
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(PictureActivity.this, "Failed to upload image, line 105", Toast.LENGTH_SHORT).show();
                                });

                        break;
                    }
                }
            }
        });

    }

    private Uri getImageUri(Context context, Bitmap imageBitmap) {
        Bitmap resizedBitmap = Bitmap.createScaledBitmap(imageBitmap, 640, 480, true);
        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 50, bytes);
        String path = MediaStore.Images.Media.insertImage(context.getContentResolver(), resizedBitmap, "Title", null);
        return Uri.parse(path);
    }
}

