package com.example.qr_project.utils;

import static android.content.ContentValues.TAG;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qr_project.models.DatabaseResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;

import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestoreException;

import com.google.firebase.firestore.FieldValue;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.atomic.AtomicInteger;


/**
 * A singleton class for managing user data, such as username, email, user ID, phone number, and more.
 */
public class UserManager {

    // Constants from Firestore for collections/documents/fields etc

    // Collection names
    private final String USERS_COLLECTION = "users";
    // user fields
    private final String QRCODES_FIELD = "qrcodes";

    private final String TAG = "UserManager";

    private static volatile UserManager instance; // Singleton instance of UserManager


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

    // Private constructor to prevent instantiation
    private UserManager() {
    }

    /**
     * Returns the singleton instance of UserManager.
     * If the instance is null, creates a new UserManager.
     *
     * @return The UserManager instance.
     */
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

    /**
     * Creates a new UserManager instance with the specified user ID.
     * This method is used for creating a new UserManager instance without affecting the singleton instance.
     *
     * @param userID The user ID to be set in the new UserManager instance.
     * @return A new UserManager instance with the specified user ID.
     */
    public static UserManager newInstance(String userID) {
        UserManager newInstance = new UserManager();
        newInstance.setUserID(userID);
        return newInstance;
    }

    /**
     * Sets the singleton instance of UserManager.
     * This method is useful for replacing the singleton instance with a new UserManager instance.
     *
     * @param instance The UserManager instance to be set as the singleton instance.
     */
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

    /**
     * Retrieves the user's QR codes from the database and returns them through the provided callback.
     *
     * @param callback The callback that will receive the list of QR codes on success, or an exception on failure.
     */
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

    /**
     * Retrieves the user's QR codes, sorts them by score in descending order, and returns the top 3 QR codes through the provided callback.
     *
     * @param callback The callback that will receive the list of top 3 QR codes on success, or an exception on failure.
     */
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

    public void getRealtimeTop3QRCodesSorted(DatabaseResultCallback<List<Map<String, Object>>> callback) {
        dbHelper.setDocumentSnapshotListener("users", this.userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onFailure(error);
                }

                if (value != null && value.exists()) {
                    Map<String, Object> data = value.getData();
                    if (data != null) {
                        List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) data.get("qrcodes");
                        if (qrCodes != null) {
                            // Sort QR codes based on score
                            qrCodes.sort((a,b) -> ( (Long) b.get("score")).compareTo(( (Long) a.get("score")) ));

                            List<Map<String, Object>> topQRCodes = qrCodes.size() >= 3
                                    ? qrCodes.subList(0, 3)
                                    : qrCodes;
                            callback.onSuccess(topQRCodes);
                        }
                    } else {
                        callback.onFailure(new Exception("No data in given document"));
                    }
                } else {
                    callback.onFailure(new Exception("Document does not exist"));
                }
            }
        });
    }

    /**
     * Retrieves the user's friends list from the database and returns it through the provided callback.
     *
     * @param callback The callback that will receive the list of friends on success, or an exception on failure.
     */
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

    /**
     * Retrieves the user's friends list, sorts them by score in descending order, and returns the top 3 friends through the provided callback.
     *
     * @param callback The callback that will receive the list of top 3 friends on success, or an exception on failure.
     */
    public void getTop3FriendsSorted(DatabaseResultCallback<List<Friend>> callback){
        getDB("friends", new DatabaseResultCallback<Object>() {
            @Override
            public void onSuccess(Object result) {
                if (result instanceof List) {
                    List<Map<String, Object>> friendsData = (List<Map<String, Object>>) result;
                    List<Friend> friendsList = new ArrayList<>();
                    AtomicInteger remainingFriends = new AtomicInteger(friendsData.size());

                    for (Map<String, Object> friendData : friendsData) {
                        String userId = (String) friendData.get("userID");
                        dbHelper.getDocument("users", userId, new OnSuccessListener<DocumentSnapshot>() {
                            @Override
                            public void onSuccess(DocumentSnapshot documentSnapshot) {
                                Friend friend = new Friend((String) documentSnapshot.get("username"), Math.toIntExact((long) documentSnapshot.get("totalScore")), userId);
                                friendsList.add(friend);

                                if (remainingFriends.decrementAndGet() == 0) {
                                    Collections.sort(friendsList, new Comparator<Friend>() {
                                        @Override
                                        public int compare(Friend f1, Friend f2) {
                                            // Assuming the Friend class has a `getScore()` method
                                            return Integer.compare(f2.getScore(), f1.getScore());
                                        }
                                    });

                                    List<Friend> top3Friends = friendsList.subList(0, Math.min(3, friendsList.size()));
                                    callback.onSuccess(top3Friends);
                                }
                            }
                        }, new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                remainingFriends.decrementAndGet();
                                Log.e(TAG, "Failed to get friend data for user_id: " + userId, e);
                            }
                        });
                    }
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

    public void getRealtimeTotalScore(DatabaseResultCallback<String> callback) {
        dbHelper.setDocumentSnapshotListener("users", this.userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onFailure(error);
                }

                if (value != null && value.exists()) {
                    Map<String, Object> data = value.getData();
                    if (data != null) {
                        callback.onSuccess(String.valueOf(data.get("totalScore")));
                    } else {
                        callback.onFailure(new Exception("No data in given document"));
                    }
                } else {
                    callback.onFailure(new Exception("Document does not exist"));
                }
            }
        });
    }

    /**
     * Adds a friend to the user's friends list in the database, given the friend's user ID.
     *
     * @param UserId The user ID of the friend to be added.
     */
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

    /**
     * Removes a friend from the user's friends list in the database, given the friend's user ID.
     *
     * @param UserId The user ID of the friend to be removed.
     */
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

    /**
     * Retrieves the user's global ranking based on their total score and returns it through the provided callback.
     *
     * @param callback The callback that will receive the global ranking on success, or an exception on failure.
     */
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

    public void getRealtimeGlobalRanking(DatabaseResultCallback<String> callback) {
        dbHelper.setCollectionSnapshotListener("users", new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onFailure(error);
                }

                if (value != null && !value.isEmpty()) {
                    ArrayList<Map<String, Integer>> rankings = new ArrayList<>();

                    for (QueryDocumentSnapshot doc : value) {
                        Map<String, Integer> userId_score_pair = new HashMap<>();
                        Object totalScoreObj = doc.get("totalScore");
                        if (totalScoreObj != null) {
                            Long totalScoreLong = (Long) totalScoreObj;
                            userId_score_pair.put(doc.getId(), Math.toIntExact(totalScoreLong));
                            rankings.add(userId_score_pair);
                        }
                    }

                    rankings.sort(Comparator.comparing(x -> x.entrySet().iterator().next().getValue()));

                    int counter = rankings.size();
                    for (Map<String, Integer> code : rankings) {
                        if (code.containsKey(userID)) {
                            callback.onSuccess(String.valueOf(counter));
                            return;
                        }
                        counter--;
                    }

                    callback.onFailure(new Exception("userID not found in rank list"));

                } else {
                    callback.onFailure(new Exception("Users collection is empty"));
                }
            }
        });
    }

    /**
     * Retrieves the user's friend ranking based on their total score and returns it through the provided callback.
     *
     * @param callback The callback that will receive the friend ranking on success, or an exception on failure.
     */
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

    /**
     * Retrieves the total number of QR codes owned by the user and returns it through the provided callback.
     *
     * @param callback The callback that will receive the total number of QR codes on success, or an exception on failure.
     */
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

    public void getRealtimeTotalQRCodes(DatabaseResultCallback<String> callback) {
        dbHelper.setDocumentSnapshotListener("users", this.userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    callback.onFailure(error);
                }

                if (value != null && value.exists()) {
                    Map<String, Object> data = value.getData();
                    if (data != null) {
                        List<Map<String, Object>> qrCodes = (List<Map<String, Object>>) data.get("qrcodes");
                        int totalQRCodes = (qrCodes != null) ? qrCodes.size(): 0;
                        callback.onSuccess(String.valueOf(totalQRCodes));
                    } else {
                        callback.onFailure(new Exception("No data in given document"));
                    }
                } else {
                    callback.onFailure(new Exception("Document does not exist"));
                }
            }
        });
    }

    /**
     * Checks if the given list of maps contains a map with a "userID" key matching the target user ID.
     *
     * @param list         The list of maps to search.
     * @param targetUserID The target user ID to look for.
     * @return true if the target user ID is found, false otherwise.
     */
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

    /**
     * Retrieves the value of a specified field for the current user from the database and returns it through the provided callback.
     *
     * @param field    The field to retrieve the value from.
     * @param callback The callback that will receive the field value on success, or an exception on failure.
     */
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

    /**
     * Clears all QRCodes from account, if there are any
     */
    public void clearQRCodes(){
        Map<String, Object> updates = new HashMap<>();
        updates.put(QRCODES_FIELD, FieldValue.delete());
        dbHelper.updateDocument(
                USERS_COLLECTION,
                userID,
                updates,
                aVoid -> {
                    Log.d(TAG, "Deletion was successful");
                },
                e -> Log.d(TAG, "Error clearing QR Codes", e)
        );
    }

    /**
     * Adds the given qrCode to the user's account
     * @param qrCode: QR Code to be added
     */
    public void addQRCode(QR_Code qrCode){
        Map<String, Object> updates = new HashMap<>();
        updates.put(QRCODES_FIELD, FieldValue.arrayUnion(qrCode));
        dbHelper.updateDocument(
                USERS_COLLECTION,
                userID,
                updates,
                aVoid -> {
                    Log.d(TAG, "Addition was successful");
                },
                e -> Log.d(TAG, "Error adding a QR Code", e)
        );
    }
}
