package com.example.qr_project.activities;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TableLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.example.qr_project.R;
import com.example.qr_project.utils.UtilityFunctions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;

import java.util.ArrayList;


public class UserSearchActivity extends AppCompatActivity {
    // Creating a database connected with FirebaseFirestore
    FirebaseFirestore db;
    String search_string;

    ArrayList<ArrayList<String>> search_results;

    TableLayout user_search_result_tbl;

    public interface OnSearchResultsListener {
        void onSearchResults(ArrayList<ArrayList<String>> searchResults);
    }

    /**
     * The UserSearchActivity page is where a user can do a search for friends and even the QRCodes
     * A player can search for their friends (other players) on the app through entering the other
     * players' userID or username on the search bar.
     * The system of an app will look on the database on FireStore to find the matching results
     * If there is a player with the desired userID or username on FireStore, the database will return
     * the result to the player
     * Otherwise, if there is no player matching the desired userID or username, the app will return
     * an error message to the player.
     * Then the player can try again with another userID or username.
     * @param savedInstanceState
     * @see           LeaderboardActivity
     * @see           SignUpActivity
     * @see           UserHomeActivity
     * @see           UserProfileActivity
     * @see           QRCodeActivity
     * @see           ScanActivity
     * @see           PictureActivity
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);



        Intent intent = getIntent();

        search_string = intent.getStringExtra("userSearch");

        searchUsers(searchResults -> {
            search_results = searchResults;
            fill_table();
        });
    }

    // User_search_result bar and icon on the app
    private void initViews(){
        user_search_result_tbl = findViewById(R.id.user_search_result_tbl);
    }

    /**
     * The UserSearchActivity page is where a user can do a search for friends and even the QRCodes
     * A player can search for their friends (other players) on the app through entering the other
     * players' userID or username on the search bar.
     * The system of an app will look on the database on FireStore to find the matching results
     * If there is a player with the desired userID or username on FireStore, the database will return
     * the result to the player
     * Otherwise, if there is no player matching the desired userID or username, the app will return
     * an error message to the player.
     * Then the player can try again with another userID or username.
     * @param callback
     * @see           LeaderboardActivity
     * @see           SignUpActivity
     * @see           UserHomeActivity
     * @see           UserProfileActivity
     * @see           QRCodeActivity
     * @see           ScanActivity
     * @see           PictureActivity
     */

    private void searchUsers(OnSearchResultsListener callback) {
        // Connect and Access the Firebase Firestore
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
        // Looking for the other players on the database collection
        CollectionReference colRef = db.collection("users");
        // Use query to find the matching other players with the desired username
        Query query = colRef.whereGreaterThanOrEqualTo("username", search_string)
                .whereLessThanOrEqualTo("username", search_string + "\uf8ff");

        // Use the connection with Firestore databse to retrieve the data of the other players
        // The user can search for other players by entering the other players' userID or username
        // on the search bar
        // Then the data of the other users will then be stored in an array list
        ArrayList<ArrayList<String>> search_results = new ArrayList();
        query.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                for (QueryDocumentSnapshot document : task.getResult()) {
                    ArrayList<String> userData = new ArrayList<>();
                    userData.add((String) document.get("userID"));
                    userData.add((String) document.get("username"));
                    userData.add(String.valueOf(document.get("totalScore")));
                    search_results.add(userData);
                }
                if (callback != null) {
                    callback.onSearchResults(search_results);
                }
            } else {
                // This is an exception when there is no player with the matched userId or username
                // The system will return a message of an error in fetching the other players' data
                // In this case, it means there is no player with that userId or username on the database
                Log.w("Firestore", "Error getting matching documents", task.getException());
            }
        });
    }

    /**
     * When the results has come and retrieved from the database collection
     * All of the results of the matching username or userID will then be added into a table list
     * in listview for user to find and look for their friends
     * @see           LeaderboardActivity
     * @see           SignUpActivity
     * @see           UserHomeActivity
     * @see           UserProfileActivity
     * @see           QRCodeActivity
     * @see           ScanActivity
     */
    private void fill_table(){
        for (int i = 0; i < search_results.size(); i++){
            ArrayList<String> userData = search_results.get(i);
            user_search_result_tbl.addView(UtilityFunctions.createNewRow(UserSearchActivity.this,
                    userData.get(1),
                    userData.get(2),
                    i+1,
                    null,
                    R.drawable.leaderboard_row_item,
                    null,
                    R.drawable.arrow_right_solid,
                    new Intent(UserSearchActivity.this, UserProfileActivity.class).putExtra("userId", userData.get(0))));
        }
    }
    /**
     * After finishing with the query and fetching the data
     * All the results with the matching desired username or userID will then be available on view
     * @param view
     * @see           LeaderboardActivity
     * @see           SignUpActivity
     * @see           UserHomeActivity
     * @see           UserProfileActivity
     * @see           QRCodeActivity
     * @see           ScanActivity
     * @return        finish()
     */

    public void onClickBack(View view){
        //Intent intent = new Intent(UserProfileActivity.this, UserHomeActivity.class);
        //startActivity(intent);
        finish();
    }



}