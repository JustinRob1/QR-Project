package com.example.qr_project.utils;

import static android.content.ContentValues.TAG;

import android.util.Log;

import androidx.annotation.NonNull;

import com.example.qr_project.models.DatabaseResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserManager {
    private static volatile UserManager instance;
    private String username; // Stores the username
    private String email; // Stores the users email
    private String userID; // Stores the userID
    private String phoneNumber; // Stores the phone number

    private List<Map<String, Object>> qrCodes;

    private List<Map<String, Object>> friends;

    private int totalScore;

    private int globalRanking;

    private int friendRanking;

    FirestoreDBHelper dbHelper = new FirestoreDBHelper();

    private UserManager() {
    }

    public static UserManager getInstance() {
        if (instance == null) {
            synchronized (UserManager.class){
                if (instance == null){
                    instance = new UserManager();
                }
            }
        }
        return instance;
    }

    public static UserManager newInstance(String userID) {
        UserManager newInstance = new UserManager();
        newInstance.setUserID(userID);
        return newInstance;
    }


    public static void setInstance(UserManager instance) {
        UserManager.instance = instance;
    }

    public void getUsername(DatabaseResultCallback<String> callback) {
        getDB("username", new DatabaseResultCallback<Object>() {
            @Override
            public void onSuccess(Object result){
                if (result instanceof String){
                    username = (String) result;
                    callback.onSuccess(username);
                } else {
                    username = null;
                    callback.onFailure(new Exception("Username is not a string"));
                }
            }

            @Override
            public void onFailure(Exception e){
                Log.d(TAG, "Error", e);
                username = null;
                callback.onFailure(e);
            }
        });
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public void getEmail(DatabaseResultCallback<String> callback) {
        getDB("email", new DatabaseResultCallback<Object>() {
            @Override
            public void onSuccess(Object result){
                if (result instanceof String){
                    email = (String) result;
                    callback.onSuccess(email);
                } else {
                    email = null;
                    callback.onFailure(new Exception("Email is not a string"));
                }
            }

            @Override
            public void onFailure(Exception e){
                Log.d(TAG, "Error", e);
                email = null;
                callback.onFailure(e);
            }
        });
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUserID() {
        return userID;
    }

    public void setUserID(String userID) {
        this.userID = userID;
    }

    public void getPhoneNumber(DatabaseResultCallback<String> callback) {
        getDB("phoneNumber", new DatabaseResultCallback<Object>() {
            @Override
            public void onSuccess(Object result){
                if (result instanceof String){
                    phoneNumber = (String) result;
                    callback.onSuccess(phoneNumber);
                } else {
                    phoneNumber = null;
                    callback.onFailure(new Exception("Phone number is not a valid string"));
                }
            }

            @Override
            public void onFailure(Exception e){
                Log.d(TAG, "Error", e);
                phoneNumber = null;
                callback.onFailure(e);
            }
        });
    }


    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public void getQRCodes(DatabaseResultCallback<List<Map<String, Object>>> callback) {
        getDB("qrcodes", new DatabaseResultCallback<Object>() {
            @Override
            public void onSuccess(Object result){
                if (result instanceof List){
                    callback.onSuccess(qrCodes);
                } else {
                    callback.onFailure(new Exception("QR codes are not in a valid list format"));
                }
            }

            @Override
            public void onFailure(Exception e){
                callback.onFailure(e);
            }
        });
    }

    public void getTop3QRCodesSorted(DatabaseResultCallback<List<Map<String, Object>>> callback) {
        getDB("qrcodes", new DatabaseResultCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                if (result != null && result instanceof List) {
                    List<Map<String, Object>> qrCodesList = (List<Map<String, Object>>) result;

                    // Sort the list of QR codes by score in descending order
                    Collections.sort(qrCodesList, new Comparator<Map<String, Object>>() {
                        @Override
                        public int compare(Map<String, Object> o1, Map<String, Object> o2) {
                            return ((Long) o2.get("score")).compareTo((Long) o1.get("score"));
                        }
                    });

                    // Return the top 3 QR codes, if available
                    List<Map<String, Object>> topQRCodes = qrCodesList.size() >= 3
                            ? qrCodesList.subList(0, 3)
                            : qrCodesList;

                    callback.onSuccess(topQRCodes);
                } else {
                    callback.onFailure(new Exception("QR codes are not in a valid list format"));
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public void getFriends(DatabaseResultCallback<List<Map<String, Object>>> callback) {
        getDB("friends", new DatabaseResultCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                if (result instanceof List) {
                    callback.onSuccess((List<Map<String, Object>>) result);
                } else {
                    callback.onFailure(new Exception("Friends list is not a valid List type."));
                }
            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }


    public void getTotalScore(DatabaseResultCallback<Integer> callback) {
        getDB("totalScore", new DatabaseResultCallback<Object>() {
            @Override
            public void onSuccess(Object result){
                if (result instanceof Long || result instanceof Integer){
                    callback.onSuccess(((Number) result).intValue());
                } else if (result instanceof String || result instanceof Object){
                    callback.onSuccess(Integer.parseInt((String) result));
                }
                else {
                    totalScore = 0;
                    callback.onFailure(new Exception("Total score is not a valid number: " + result.getClass().getName()));
                }
            }

            @Override
            public void onFailure(Exception e){
                Log.d(TAG, "Error", e);
                totalScore = 0;
                callback.onFailure(e);
            }
        });
    }

    public void addFriend(String UserId){
        Map<String, Object> newMapObject = new HashMap<>();
        newMapObject.put("userID", UserId);

        dbHelper.appendMapToArrayField("users", this.userID, "friends", newMapObject,
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Map object successfully appended to array field!");
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error appending map object to array field", e);
                    }
                });

        // Gets friends
        dbHelper.getDocument("users", this.userID,
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });

    }

    public void removeFriend(String UserId){
        Map<String, Object> mapToRemove = new HashMap<>();
        mapToRemove.put("userID", UserId);

        dbHelper.removeMapFromArrayField("users", this.userID, "friends", mapToRemove,
                new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.d(TAG, "Map object successfully removed from array field!");
                    }
                },
                new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error removing map object from array field", e);
                    }
                });

        // Gets friends
        dbHelper.getDocument("users", this.userID,
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });
    }

    public void getGlobalRanking(DatabaseResultCallback<Integer> callback){
        dbHelper.getAllDocumentsOrdered("users", "totalScore", false, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    int rank = 1;
                    boolean userFound = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals(userID)) {
                            userFound = true;
                            break;
                        }
                        rank++;
                    }
                    if (userFound){
                        globalRanking = rank;
                        callback.onSuccess(rank);
                    }
                    else {
                        globalRanking = 0;
                        Log.d(TAG, "User not found", new Exception("User Not Found Error"));
                        callback.onFailure(new Exception("User Not Found Error"));
                    }
                } else {
                    Log.d(TAG, "Unsuccessful query", task.getException());
                    globalRanking = 0;
                    callback.onFailure(task.getException());
                }
            }
        });
    }


    public void getFriendRanking(DatabaseResultCallback<Integer> callback){

        // Gets friends
        dbHelper.getDocument("users", this.userID,
                new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        if (documentSnapshot.exists()) {
                            friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                        } else {
                            Log.d(TAG, "No such document");
                        }
                    }
                }, new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "Error getting document", e);
                    }
                });

        dbHelper.getAllDocumentsOrdered("users", "totalScore", false, new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (friends == null){
                    callback.onSuccess(0);
                }
                if (task.isSuccessful()) {
                    int rank = 1;
                    boolean userFound = false;
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        if (document.getId().equals(userID)) {
                            userFound = true;
                            break;
                        } else if (friends != null && containsUserID(friends, document.getId())){
                            rank++;
                        }
                    }
                    if (friends != null && userFound){
                        callback.onSuccess(rank);
                    }
                    else {
                        Log.d(TAG, "User not found", new Exception("User Not Found Error"));
                        callback.onFailure(new Exception("User Not Found Error"));
                    }
                } else {
                    Log.d(TAG, "Unsuccessful query", task.getException());
                    callback.onFailure(task.getException());
                }
            }
        });
    }

    public void getTotalQRCodes(DatabaseResultCallback<Integer> callback){
        getQRCodes(new DatabaseResultCallback<List<Map<String, Object>>>() {
            @Override
            public void onSuccess(List<Map<String, Object>> result) {
                if (result != null){
                    callback.onSuccess(result.size());
                } else {
                    callback.onSuccess(0);
                }

            }

            @Override
            public void onFailure(Exception e) {
                callback.onFailure(e);
            }
        });
    }

    public static boolean containsUserID(List<Map<String, Object>> list, String targetUserID) {
        for (Map<String, Object> map : list) {
            if (map.containsKey("userID")) {
                String userID = (String) map.get("userID");
                if (userID.equals(targetUserID)) {
                    return true;
                }
            }
        }
        return false;
    }

    public void getDB(String field, DatabaseResultCallback<Object> callback) {
        dbHelper.getDocument("users", this.userID,
            new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    if (documentSnapshot.exists()) {
                        Object value = documentSnapshot.get(field);
                        callback.onSuccess(value);
                    } else {
                        Log.d(TAG, "No such document");
                        callback.onFailure(new Exception("No such document"));
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
}
