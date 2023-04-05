package com.example.qr_project.utils;

import android.util.Log;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qr_project.models.DatabaseResultCallback;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
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
                        if (qrCodes != null && !qrCodes.isEmpty()) {
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
    public void getFriendsRealtime(DatabaseResultCallback<List<Map<String, Object>>> callback) {
        dbHelper.setDocumentSnapshotListener("users", this.userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Object result = documentSnapshot.get("friends");
                    if (result instanceof List) {
                        callback.onSuccess((List<Map<String, Object>>) result);
                    } else {
                        callback.onFailure(new Exception("Friends list is not a valid List type."));
                    }
                } else {
                    Log.d(TAG, "No such document");
                    callback.onFailure(new Exception("No such document"));
                }
            }
        });
    }


    /**
     * Retrieves the user's friends list, sorts them by score in descending order, and returns the top 3 friends through the provided callback.
     *
     * @param callback The callback that will receive the list of top 3 friends on success, or an exception on failure.
     */
    public void getTop3FriendsSorted(DatabaseResultCallback<List<Friend>> callback) {
        dbHelper.setDocumentSnapshotListener("users", this.userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    List<Map<String, Object>> friendsData = (List<Map<String, Object>>) documentSnapshot.get("friends");
                    if (friendsData != null) {
                        ConcurrentHashMap<String, Friend> friendsMap = new ConcurrentHashMap<>();
                        AtomicInteger remainingFriends = new AtomicInteger(friendsData.size());

                        for (Map<String, Object> friendData : friendsData) {
                            String userId = (String) friendData.get("userID");
                            dbHelper.setDocumentSnapshotListener("users", userId, new EventListener<DocumentSnapshot>() {
                                @Override
                                public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                                    if (error != null) {
                                        Log.w(TAG, "Error getting friend document", error);
                                        return;
                                    }

                                    if (documentSnapshot != null && documentSnapshot.exists()) {
                                        Friend friend = new Friend((String) documentSnapshot.get("username"), Math.toIntExact((long) documentSnapshot.get("totalScore")), userId);
                                        friendsMap.put(userId, friend);

                                        if (remainingFriends.decrementAndGet() == 0) {
                                            List<Friend> friendsList = new ArrayList<>(friendsMap.values());
                                            friendsList.sort((f1, f2) -> Integer.compare(f2.getScore(), f1.getScore()));
                                            List<Friend> top3Friends = friendsList.subList(0, Math.min(3, friendsList.size()));
                                            callback.onSuccess(top3Friends);
                                        }
                                    }
                                }
                            });
                        }
                    } else {
                        callback.onFailure(new Exception("Friends list is null."));
                    }
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }

    public void getRealtimeTotalScore(DatabaseResultCallback<String> callback) {
        if (this.userID == null) {
            callback.onFailure(new Exception("userID is null"));
            return;
        }
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
    public void addFriendRealtime(String UserId) {
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

        // Sets a snapshot listener for the user's document
        dbHelper.setDocumentSnapshotListener("users", this.userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }


    /**
     * Removes a friend from the user's friends list in the database, given the friend's user ID.
     *
     * @param UserId The user ID of the friend to be removed.
     */
    public void removeFriendRealtime(String UserId) {
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

        // Sets a snapshot listener for the user's document
        dbHelper.setDocumentSnapshotListener("users", this.userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    friends = (List<Map<String, Object>>) documentSnapshot.get("friends");
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }


    /**
     * Retrieves the user's global ranking based on their total score and returns it through the provided callback.
     *
     * @param callback The callback that will receive the global ranking on success, or an exception on failure.
     */
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
    public void getRealtimeFriendRanking(DatabaseResultCallback<Integer> callback) {

        dbHelper.setDocumentSnapshotListener("users", this.userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    friends = (List<Map<String, Object>>) documentSnapshot.get("friends");

                    dbHelper.setCollectionSnapshotListener("users", new EventListener<QuerySnapshot>() {
                        @Override
                        public void onEvent(@Nullable QuerySnapshot querySnapshot, @Nullable FirebaseFirestoreException error) {
                            if (error != null) {
                                Log.w(TAG, "Error getting documents", error);
                                return;
                            }

                            if (friends == null) {
                                callback.onSuccess(0);
                                return;
                            }

                            List<DocumentSnapshot> sortedDocs = querySnapshot.getDocuments();
                            sortedDocs.sort((doc1, doc2) -> {
                                long score1 = doc1.getLong("totalScore") != null ? doc1.getLong("totalScore") : 0;
                                long score2 = doc2.getLong("totalScore") != null ? doc2.getLong("totalScore") : 0;
                                return Long.compare(score2, score1);
                            });

                            int rank = 1;
                            boolean userFound = false;
                            for (DocumentSnapshot document : sortedDocs) {
                                if (document.getId().equals(userID)) {
                                    userFound = true;
                                    break;
                                } else if (containsUserID(friends, document.getId())) {
                                    rank++;
                                }
                            }

                            if (userFound) {
                                callback.onSuccess(rank);
                            } else {
                                Log.d(TAG, "User not found", new Exception("User Not Found Error"));
                                callback.onFailure(new Exception("User Not Found Error"));
                            }
                        }
                    });
                } else {
                    Log.d(TAG, "No such document");
                }
            }
        });
    }



    /**
     * Retrieves the total number of QR codes owned by the user and returns it through the provided callback.
     *
     * @param callback The callback that will receive the total number of QR codes on success, or an exception on failure.
     */
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
            if (map != null && map.containsKey("userID")) {
                String userID = (String) map.get("userID");
                if (userID != null && userID.equals(targetUserID)) {
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
        dbHelper.setDocumentSnapshotListener("users", this.userID, new EventListener<DocumentSnapshot>() {
            @Override
            public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                if (error != null) {
                    Log.w(TAG, "Error getting document", error);
                    callback.onFailure(error);
                    return;
                }

                if (documentSnapshot != null && documentSnapshot.exists()) {
                    Object value = documentSnapshot.get(field);
                    callback.onSuccess(value);
                } else {
                    Log.d(TAG, "No such document");
                    callback.onFailure(new Exception("No such document"));
                }
            }
        });
    }



    /**
     *
     * @param onCompleteListener: a listener called when task is completed. Used by Espresso
     *                            idling resources.
     */
    public void clearQRCodes(OnCompleteListener<Void> onCompleteListener){
        Map<String, Object> updates = new HashMap<>();
        updates.put(QRCODES_FIELD, new ArrayList<>());
        dbHelper.updateDocument(
                USERS_COLLECTION,
                userID,
                updates,
                aVoid -> {
                    Log.d(TAG, "Deletion was successful");
                },
                e -> Log.d(TAG, "Error clearing QR Codes", e),
                onCompleteListener
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
                e -> Log.d(TAG, "Error adding a QR Code", e),
                task -> {
                    Log.d(TAG, "Task completed");
                }
        );
    }

    /**
     * Adds the given qrCode to the user's account
     * @param qrCode: QR Code to be added
     * @param onCompleteListener: a listener called when task is completed. Used by Espresso
     *                            idling resources.
     */
    public void addQRCode(QR_Code qrCode, OnCompleteListener<Void> onCompleteListener){
        Map<String, Object> updates = new HashMap<>();
        updates.put(QRCODES_FIELD, FieldValue.arrayUnion(qrCode));
        dbHelper.updateDocument(
                USERS_COLLECTION,
                userID,
                updates,
                aVoid -> {
                    Log.d(TAG, "Addition was successful");
                },
                e -> Log.d(TAG, "Error adding a QR Code", e),
                onCompleteListener
        );
    }
}
