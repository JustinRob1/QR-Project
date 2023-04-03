package com.example.qr_project.utils;

import static android.content.ContentValues.TAG;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Base64;
import android.util.Log;

import androidx.annotation.NonNull;

import com.example.qr_project.models.DatabaseResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;

public class QRCodeManager {
    FirestoreDBHelper dbHelper = new FirestoreDBHelper();
    String hash;

    UserManager userManager = UserManager.getInstance();

    public QRCodeManager(String hash){
        this.hash = hash;
    }

    public void getGlobalRanking(DatabaseResultCallback<Integer> callback) {
        dbHelper.getAllDocuments("users", new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int rank = 1;
                    boolean found = false;
                    int highestScore = -1;

                    for (QueryDocumentSnapshot document : task.getResult()) {
                        List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) document.get("qrcodes");

                        if (qrCodesArray != null) {
                            for (Map<String, Object> code : qrCodesArray) {
                                int score = Math.toIntExact((Long) code.get("score"));

                                if (String.valueOf(code.get("hash")).equals(QRCodeManager.this.hash)) {
                                    found = true;
                                    highestScore = score;
                                    break;
                                }
                            }
                        }

                        if (found) {
                            break;
                        }
                    }

                    if (found) {
                        // Calculate the rank based on the score
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) document.get("qrcodes");

                            if (qrCodesArray != null) {
                                for (Map<String, Object> code : qrCodesArray) {
                                    int score = Math.toIntExact((Long) code.get("score"));

                                    if (score > highestScore) {
                                        rank++;
                                    }
                                }
                            }
                        }
                        callback.onSuccess(rank);
                    } else {
                        callback.onFailure(new Exception("QR code not found"));
                    }
                } else {
                    callback.onFailure(new Exception("Error getting documents"));
                }
            }
        });
    }

    public void getFriendsQRCodeRanking(DatabaseResultCallback<Integer> callback) {
        dbHelper.getDocument("users", userManager.getUserID(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<Map<String, Object>> friendsData = (List<Map<String, Object>>) documentSnapshot.get("friends");
                    List<String> friendsList = new ArrayList<>();
                    for (Map<String, Object> friendData : friendsData) {
                        friendsList.add((String) friendData.get("userID"));
                    }
                    friendsList.add(userManager.getUserID()); // Add the user to the list for comparison

                    AtomicInteger counter = new AtomicInteger(friendsList.size());
                    AtomicInteger rank = new AtomicInteger(1);
                    int[] highestScore = {-1};

                    for (String friendId : friendsList) {
                        dbHelper.getDocument("users", friendId, new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot friendDocumentSnapshot) {
                                List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) friendDocumentSnapshot.get("qrcodes");

                                if (qrCodesArray != null && !qrCodesArray.isEmpty()) {
                                    for (Map<String, Object> code : qrCodesArray) {
                                        int score = Math.toIntExact((Long) code.get("score"));

                                        if (String.valueOf(code.get("hash")).equals(QRCodeManager.this.hash)) {
                                            highestScore[0] = score;
                                        }

                                        if (score > highestScore[0]) {
                                            rank.incrementAndGet();
                                        }
                                    }
                                }

                                if (counter.decrementAndGet() == 0) {
                                    if (highestScore[0] != -1) {
                                        callback.onSuccess(rank.get());
                                    } else {
                                        callback.onSuccess(0);
                                    }
                                }
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Log.w(TAG, "Error getting document", e);
                                callback.onFailure(e);
                            }
                        });
                    }
                } else {
                    callback.onFailure(new Exception("No document"));
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting document", e);
                callback.onFailure(e);
            }
        });
    }

    public void getUserQRCodeRanking(DatabaseResultCallback<Integer> callback) {
        dbHelper.getDocument("users", userManager.getUserID(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");

                    if (qrCodesArray != null && !qrCodesArray.isEmpty()) {
                        int rank = 1;
                        boolean found = false;
                        int highestScore = -1;

                        for (Map<String, Object> code : qrCodesArray) {
                            int score = Math.toIntExact((Long) code.get("score"));

                            if (String.valueOf(code.get("hash")).equals(QRCodeManager.this.hash)) {
                                found = true;
                                highestScore = score;
                                break;
                            }
                        }

                        if (found) {
                            // Calculate the rank based on the score
                            for (Map<String, Object> code : qrCodesArray) {
                                int score = Math.toIntExact((Long) code.get("score"));

                                if (score > highestScore) {
                                    rank++;
                                }
                            }
                            callback.onSuccess(rank);
                        } else {
                            callback.onSuccess(0);
                        }
                    } else {
                        callback.onFailure(new Exception("No QR codes in the array"));
                    }
                } else {
                    callback.onFailure(new Exception("No user"));
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting document", e);
                callback.onFailure(e);
            }
        });
    }

    public void getTotalScans(DatabaseResultCallback<Integer> callback){
        dbHelper.getDocument("qrcodes", QRCodeManager.this.hash, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot != null){
                    ArrayList<String> users = (ArrayList<String>) documentSnapshot.get("users");

                    // Update the TextView for the number of times the QR code was scanned
                    callback.onSuccess(users.size());
                } else {
                    callback.onFailure(new Exception("QR Code doesn't exist"));
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void addComment(Map<String, Object> comment) {

        Log.d(TAG, "Adding comment: " + comment);

        dbHelper.appendMapToArrayField("qrcodes", QRCodeManager.this.hash, "comments", comment,
            new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void unused) {
                    Log.d(TAG, "Comment added successfully");
                }
            },
            new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e(TAG, "Error adding comment: " + e.getMessage());
                }
            }
        );
    }

    public void removeComment(Map<String, Object> comment){
        dbHelper.removeMapFromArrayField("qrcodes", QRCodeManager.this.hash, "comments", comment, new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void unused) {
                Log.d(TAG, "Comment successfully removed from array field!");
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error removing comment from array field", e);
            }
        });
    }

    public void getAllComments(DatabaseResultCallback<List<Map<String, Object>>> callback){
        dbHelper.getDocument("qrcodes", QRCodeManager.this.hash, new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists() && documentSnapshot != null){
                    List<Map<String, Object>> comments = (List<Map<String, Object>>) documentSnapshot.get("comments");

                    // Update the TextView for the number of times the QR code was scanned
                    callback.onSuccess(comments);
                } else {
                    callback.onFailure(new Exception("QR Code doesn't exist"));
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void hasUserScanned(DatabaseResultCallback<Boolean> callback) {
        dbHelper.getDocument("users", userManager.getUserID(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                if (documentSnapshot.exists()) {
                    List<Map<String, Object>> qrCodesArray = (List<Map<String, Object>>) documentSnapshot.get("qrcodes");
                    boolean qrCodeFound = false;

                    if (qrCodesArray != null && !qrCodesArray.isEmpty()) {
                        for (Map<String, Object> code : qrCodesArray) {
                            if (String.valueOf(code.get("hash")).equals(QRCodeManager.this.hash)) {
                                qrCodeFound = true;
                                break;
                            }
                        }
                    }
                    callback.onSuccess(qrCodeFound);
                } else {
                    callback.onFailure(new Exception("No document"));
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting document", e);
                callback.onFailure(e);
            }
        });
    }

    public void getQRCode(DatabaseResultCallback<QR_Code> callback) {
        // First, try to get the QR code from the userManager.getUserId() document
        dbHelper.getDocument("users", userManager.getUserID(), new OnSuccessListener<DocumentSnapshot>() {
            @Override
            public void onSuccess(DocumentSnapshot documentSnapshot) {
                boolean qrCodeFound = false;
                if (documentSnapshot.exists()) {
                    qrCodeFound = processDocument(documentSnapshot, callback, true);
                }
                // If the QR code is not found in the userManager.getUserId() document, check all other documents
                if (!qrCodeFound) {
                    dbHelper.getAllDocuments("users", new OnCompleteListener<QuerySnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<QuerySnapshot> task) {
                            if (task.isSuccessful()) {
                                boolean qrCodeFoundInOtherDocs = false;
                                for (DocumentSnapshot document : task.getResult()) {
                                    if (!document.getId().equals(userManager.getUserID())) {
                                        qrCodeFoundInOtherDocs = processDocument(document, callback, false);
                                        if (qrCodeFoundInOtherDocs) {
                                            break;
                                        }
                                    }
                                }
                                if (!qrCodeFoundInOtherDocs) {
                                    callback.onFailure(new Exception("No QR Code with the given hash found"));
                                }
                            } else {
                                callback.onFailure(new Exception("Error getting documents", task.getException()));
                            }
                        }
                    });
                }
            }
        }, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.w(TAG, "Error getting document", e);
                callback.onFailure(e);
            }
        });
    }


    private boolean processDocument(DocumentSnapshot documentSnapshot, DatabaseResultCallback<QR_Code> callback, boolean foundWithUser) {
        List<Map<String, Object>> qrCodesArray = extractQRCodeArray(documentSnapshot);

        if (qrCodesArray != null && !qrCodesArray.isEmpty()) {
            Map<String, Object> selectedQRCode = findQRCodeByHash(qrCodesArray, QRCodeManager.this.hash);

            if (selectedQRCode != null) {
                QR_Code qrCode = createQRCodeObject(selectedQRCode, foundWithUser);
                callback.onSuccess(qrCode);
                return true;
            }
        }
        return false;
    }

    private List<Map<String, Object>> extractQRCodeArray(DocumentSnapshot documentSnapshot) {
        return (List<Map<String, Object>>) documentSnapshot.get("qrcodes");
    }

    private Map<String, Object> findQRCodeByHash(List<Map<String, Object>> qrCodesArray, String hash) {
        for (Map<String, Object> code : qrCodesArray) {
            if (String.valueOf(code.get("hash")).equals(hash)) {
                return code;
            }
        }
        return null;
    }

    private QR_Code createQRCodeObject(Map<String, Object> selectedQRCode, boolean foundWithUser) {
        int score = Math.toIntExact((Long) selectedQRCode.get("score"));
        String name = (String) selectedQRCode.get("name");
        String face = (String) selectedQRCode.get("face");
        String photoUrl = (String) selectedQRCode.get("photo");
        GeoPoint location = (GeoPoint) selectedQRCode.get("location");
        String hash = (String) selectedQRCode.get("hash");

        String photo;
        if (foundWithUser) {
            photo = photoUrl;
        } else {
            photo = null;
        }

        Bitmap faceBitmap = null;
        try {
            byte[] decodedString = Base64.decode(face, Base64.DEFAULT);
            faceBitmap = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return new QR_Code(score, name, faceBitmap, photo, location, hash, foundWithUser);
    }


}
