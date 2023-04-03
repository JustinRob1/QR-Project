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
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class FaceActivity extends AppCompatActivity {

    private static final int CAMERA_REQUEST_CODE = 1;

    private String qrHash;
    private String userID;
    private Uri faceUri;
    private Bitmap face;

    private StorageReference storageReference;
    private FirebaseFirestore db;

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
        // Get the byte array from the intent
        byte[] byteArray = getIntent().getByteArrayExtra("face");

        // Convert the byte array to a bitmap
        face = BitmapFactory.decodeByteArray(byteArray, 0, byteArray.length);

        storageReference = FirebaseStorage.getInstance().getReference();
        db = FirebaseFirestore.getInstance();

        // Upload the photo to storage
        faceUri = getImageUri(getApplicationContext(), face);
        StorageReference faceRef = storageReference.child(userID).child(qrHash).child("face");
        faceRef.putFile(faceUri)
                .addOnSuccessListener(taskSnapshot -> {
                    faceRef.getDownloadUrl()
                            .addOnSuccessListener(uri -> {
                                saveImageUrlToFirestore(uri.toString());
                                finish(); // move finish() here
                            })
                            .addOnFailureListener(e -> {
                                Toast.makeText(FaceActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                                finish(); // add a finish() call here as well
                            });
                })
                .addOnFailureListener(e -> {
                    Toast.makeText(FaceActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
                    finish(); // add a finish() call here as well
                });
    }

    private void saveImageUrlToFirestore(String imageUrl) {
        if (imageUrl != null) {
            Log.d("My Tag", "FaceActivity called with: imageUrl = [" + imageUrl + "]");
        } else {
            Toast.makeText(FaceActivity.this, "Image URI is null or empty", Toast.LENGTH_SHORT).show();
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
                        qrCode.put("face", imageUrl);

                        // Update the document in Firestore with the modified qrcodes array
                        db.collection("users").document(userID).update("qrcodes", qrCodes)
                                .addOnSuccessListener(aVoid -> {
                                    // do nothing here, or add a success message if you'd like
                                })
                                .addOnFailureListener(e -> {
                                    Toast.makeText(FaceActivity.this, "Failed to upload image", Toast.LENGTH_SHORT).show();
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
