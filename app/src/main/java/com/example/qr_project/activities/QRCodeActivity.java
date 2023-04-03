package com.example.qr_project.activities;

import static android.content.ContentValues.TAG;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.example.qr_project.models.DatabaseResultCallback;
import com.example.qr_project.utils.Comment;
import com.example.qr_project.utils.CommentAdapter;
import com.example.qr_project.utils.QRCodeManager;
import com.example.qr_project.utils.QR_Code;
import com.example.qr_project.utils.UserManager;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.GeoPoint;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class QRCodeActivity extends AppCompatActivity {
    /**
     * This allows to fetch the id of the "activity_qrcode" button
     * After that, it will move on the next step/page
     * @param savedInstanceState   a package to be called to execute the QRCodeActivity
     */

    FirebaseFirestore db;

    private TextView totalScans;

    private TextView qrCodeName;

    private TextView qrCodeScore;

    private TextView yourRankNum;

    private TextView friendRankNum;

    private TextView globalRankNum;


    private TextView hasUserScannedCode;

    private TableRow RemoveQRRow;

    private TextView QRFace;
    private ListView commentListView;

    private LinearLayout qrRow1;
    private LinearLayout qrRow2;
    private LinearLayout qrRow3;
    private LinearLayout qrRow4;

    private TableRow addCommentRow;
    private TextView seeCommentsText;

    private TableRow seeCommentsRow;

    ArrayList<Comment> commentList;
    ArrayAdapter<Comment> commentList_adapter;

    private TableLayout bottomButtons;

    private LinearLayout qrDetailsBorder;

    QRCodeManager qrCodeManager;

    UserManager userManager;

    String qr_code_hash;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_qrcode);


        qr_code_hash = getIntent().getStringExtra("hash");
        qrCodeManager = new QRCodeManager(qr_code_hash);
        userManager = UserManager.getInstance();

        initViews();

        getComments();

        updateQRCode();
    }

    @Override
    protected void onResume (){
        super.onResume();

        updateQRCode();
    }

    private void initViews(){
        commentList = new ArrayList<>();
        commentList_adapter = new CommentAdapter(this, commentList, new Intent(QRCodeActivity.this, QRCodeActivity.class).putExtra("hash", qr_code_hash));
        commentListView = findViewById(R.id.comment_table);
        commentListView.setAdapter(commentList_adapter);
        commentListView.setVisibility(View.GONE);


        totalScans = findViewById(R.id.total_scans);
        qrCodeName = findViewById(R.id.qr_code_name);
        qrCodeScore = findViewById(R.id.qr_code_score);
        yourRankNum = findViewById(R.id.your_rank_num);
        friendRankNum = findViewById(R.id.friend_rank_num);
        globalRankNum = findViewById(R.id.global_rank_num);
        hasUserScannedCode = findViewById(R.id.user_scanned_already_txt);
        RemoveQRRow = findViewById(R.id.remove_qr_row);
        RemoveQRRow.setVisibility(View.GONE);
        QRFace = findViewById(R.id.image_face);
        commentListView = findViewById(R.id.comment_table);
        bottomButtons = findViewById(R.id.bottom_buttons);
        setBottomButtonsMargin(80);


        qrDetailsBorder = findViewById(R.id.qr_code_details_border);
        addCommentRow = findViewById(R.id.add_comment_row);
        addCommentRow.setVisibility(View.GONE);
        seeCommentsRow = findViewById(R.id.see_comment_row);

        seeCommentsRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onSeeComments(view);
            }
        });
        seeCommentsText= findViewById(R.id.see_comments_text);

        qrRow1= findViewById(R.id.row_1);
        qrRow2= findViewById(R.id.row_2);
        qrRow3= findViewById(R.id.row_3);
        qrRow4= findViewById(R.id.row_4);
    }

    private void setBottomButtonsMargin(int margin){
        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams) bottomButtons.getLayoutParams();

        int newTopMargin = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, margin, getResources().getDisplayMetrics());
        layoutParams.topMargin = newTopMargin;
        bottomButtons.setLayoutParams(layoutParams);
    }

    public void getComments(){
        qrCodeManager.getAllComments(new DatabaseResultCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                commentList.clear();
                if (result!= null && !result.isEmpty()){
                    for (Map<String, Object> comment: result){
                        String username = (String) comment.get("username");
                        String commentText = (String) comment.get("commentText");

                        Comment newcomment = new Comment(userManager.getUserID(), username, commentText, qr_code_hash);
                        commentList.add(newcomment);
                    }

                    commentList_adapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error getting comments", e);
            }
        });
    }


    /**
     * Called when the user clicks the back button
     * @param view
     * The text view which is pressed
     */
    public void onClickBack(View view){
        finish();
    }

    /**
     * When the user clicks the camera button, this method will be called
     * and will open the camera to scan a QR or barcode
     *
     * @param view The text view which is pressed
     */
    public void onCameraClick(View view) {
        Intent intent = new Intent(this, ScanActivity.class);
        startActivity(intent);
    }


    /**
     * For the use and the feature of the map button
     *
     * @param view The text view which is pressed
     */
    public void onMapClick(View view) {
        Intent intent = new Intent(this, MapActivity.class);
        startActivity(intent);
    }

    /**
     * Starts the LeaderboardActivity
     * @param view The text view which is pressed
     */
    public void onLeaderboardClick(View view) {
        Intent intent = new Intent(this, LeaderboardActivity.class);
        startActivity(intent);
    }

    public void onDeleteClick(View view) {
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("user_id", null);

        String deleteHash = getIntent().getStringExtra("hash");

        DocumentReference userRef = db.collection("users").document(userID);

        new AlertDialog.Builder(this)
                .setTitle("Delete QR code")
                .setMessage("Are you sure you want to delete this QR code?")
                .setPositiveButton("Delete", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        db.runTransaction(transaction -> {
                            DocumentSnapshot userSnapshot = transaction.get(userRef);

                            // Get the current QR codes array and total score from the user document
                            List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) userSnapshot.get("qrcodes");
                            int totalScore = ((Long) userSnapshot.get("totalScore")).intValue();

                            // Find the index of the QR code with the specified hash value in the array
                            int qrCodeIndex = -1;
                            int qrCodeScore = 0;
                            for (int j = 0; j < qrCodes.size(); j++) {
                                String hash = (String) qrCodes.get(j).get("hash");
                                if (hash.equals(deleteHash)) {
                                    qrCodeIndex = j;
                                    qrCodeScore = ((Long) qrCodes.get(j).get("score")).intValue(); // Get the score of the deleted QR code
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
                        }).addOnSuccessListener(result -> {
                            // The QR code was successfully deleted
                            Toast.makeText(getApplicationContext(), "QR code has been deleted", Toast.LENGTH_SHORT).show();
                            finish();
                        }).addOnFailureListener(e -> {
                            // An error occurred while deleting the QR code
                            Log.e("TAG", "Error deleting QR code from user's qrcodes array: " + e.getMessage());
                        });
                    }
                })
                .setNegativeButton("Cancel", null)
                .show();
    }

    public void seePhoto(View view) {
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("user_id", null);

        String qrCodeHash = getIntent().getStringExtra("hash");

        DocumentReference userRef = db.collection("users").document(userID);
        userRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");
                if (qrCodes != null) {
                    for (Map<String, Object> qrCode : qrCodes) {
                        String hash = (String) qrCode.get("hash");
                        if (hash != null && hash.equals(qrCodeHash)) {
                            String photoUrl = (String) qrCode.get("photo");
                            if (photoUrl != null) {
                                // Load the photo using Picasso
                                Picasso.get().load(photoUrl).into(new Target() {
                                    @Override
                                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                                        // Display the photo in a dialog
                                        AlertDialog.Builder builder = new AlertDialog.Builder(QRCodeActivity.this);
                                        ImageView imageView = new ImageView(QRCodeActivity.this);
                                        imageView.setImageBitmap(bitmap);
                                        builder.setView(imageView);
                                        AlertDialog dialog = builder.create();
                                        dialog.show();
                                    }

                                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                                        // Handle the error
                                        Toast.makeText(getApplicationContext(), "Failed to load photo", Toast.LENGTH_SHORT).show();
                                    }

                                    @Override
                                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                                        // Show a progress bar or placeholder image
                                    }
                                });
                            }
                            break;
                        }
                    }
                }
            }
        }).addOnFailureListener(e -> {
            // Handle the error
            Toast.makeText(getApplicationContext(), "Failed to retrieve data", Toast.LENGTH_SHORT).show();
        });
    }

    public void seeLocation(View view) {
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
        String userID = sharedPref.getString("user_id", null);

        String qrCodeHash = getIntent().getStringExtra("hash");

        DocumentReference userRef = db.collection("users").document(userID);
        userRef.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                DocumentSnapshot document = task.getResult();
                if (document != null && document.exists()) {
                    List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) document.get("qrcodes");
                    if (qrCodes != null) {
                        for (Map<String, Object> qrCode : qrCodes) {
                            String hash = (String) qrCode.get("hash");
                            if (hash.equals(qrCodeHash)) {
                                GeoPoint location = (GeoPoint) qrCode.get("location");
                                if (location != null) {
                                    Intent intent = new Intent(this, MapActivity.class);
                                    intent.putExtra("latitude", location.getLatitude());
                                    intent.putExtra("longitude", location.getLongitude());
                                    startActivity(intent);
                                    break;
                                }
                            }
                        }
                    }
                }
            } else {
                Log.d(TAG, "Error getting document: ", task.getException());
            }
        });
    }

    private void updateQRCode(){
        qrCodeManager.getQRCode(new DatabaseResultCallback<QR_Code>() {
            @Override
            public void onSuccess(QR_Code result) {
                qrCodeName.setText(result.getName());
                qrCodeScore.setText(String.valueOf(result.getScore()));
                //QRFace.setText(result.getFace());
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error: ", e);
            }
        });

        qrCodeManager.getGlobalRanking(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                globalRankNum.setText(String.valueOf(result));
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error with global rank: ", e);
            }
        });

        qrCodeManager.getUserQRCodeRanking(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                if (result == 0){
                    yourRankNum.setText("N/A");
                } else {
                    yourRankNum.setText(String.valueOf(result));
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error with user rank: ", e);
            }
        });

        qrCodeManager.getFriendsQRCodeRanking(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                if (result == 0){
                    friendRankNum.setText("N/A");
                } else {
                    friendRankNum.setText(String.valueOf(result));
                }
            }

            @Override
            public void onFailure(Exception e) {

            }
        });

        qrCodeManager.getTotalScans(new DatabaseResultCallback<Integer>() {
            @Override
            public void onSuccess(Integer result) {
                totalScans.setText(String.valueOf(result));
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error with total scans: ", e);
            }
        });

        qrCodeManager.hasUserScanned((new DatabaseResultCallback<Boolean>() {
            @Override
            public void onSuccess(Boolean result) {
                if (result == true){
                    hasUserScannedCode.setText("You have already scanned this code");
                    RemoveQRRow.setVisibility(View.VISIBLE);
                } else {
                    hasUserScannedCode.setText("You haven't scanned this code yet");
                    RemoveQRRow.setVisibility(View.GONE);
                }
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error with has User scanned: ", e);
            }
        }));

    }


    public void onSeeComments(View view){
        qrRow1.setVisibility(View.GONE);
        qrRow2.setVisibility(View.GONE);
        qrRow3.setVisibility(View.GONE);
        qrRow4.setVisibility(View.GONE);

        commentListView.setVisibility(View.VISIBLE);
        addCommentRow.setVisibility(View.VISIBLE);

        seeCommentsText.setText("See Stats");
        setBottomButtonsMargin(20);
        seeCommentsRow.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                reloadActivity();
            }
        });
    }

    public void addComment(View view) {
        // Inflate the custom layout for the dialog
        LayoutInflater inflater = getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_add_comment, null);
        EditText commentText = dialogView.findViewById(R.id.comment_text);

        // Create and show the AlertDialog
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Add Comment")
                .setView(dialogView)
                .setPositiveButton("Submit", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        // Get the entered comment text and call the method to save it
                        String text = commentText.getText().toString();
                        saveComment(text);
                    }
                })
                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // Close the dialog without doing anything
                        dialog.cancel();
                    }
                })
                .create()
                .show();
    }

    public void saveComment(String text) {
        // Your existing code to save the comment to the database
        Map<String, Object> comment = new HashMap<>();

        userManager.getUsername(new DatabaseResultCallback<String>() {
            @Override
            public void onSuccess(String result) {
                comment.put("username", result);
                comment.put("commentText", text);
                comment.put("userID", userManager.getUserID());
                qrCodeManager.addComment(comment);
                getComments();
            }

            @Override
            public void onFailure(Exception e) {
                Log.d(TAG, "Error getting username: ", e);
            }
        });
    }


    private void reloadActivity(){
        Intent intent = new Intent(this, QRCodeActivity.class);
        intent.putExtra("hash", qr_code_hash);
        finish();
        startActivity(intent);
    }

}




