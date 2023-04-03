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
    FirebaseFirestore db;
    String search_string;

    ArrayList<ArrayList<String>> search_results;

    TableLayout user_search_result_tbl;

    public interface OnSearchResultsListener {
        void onSearchResults(ArrayList<ArrayList<String>> searchResults);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user_search);

        user_search_result_tbl = findViewById(R.id.user_search_result_tbl);

        Intent intent = getIntent();

        search_string = intent.getStringExtra("userSearch");

        searchUsers(searchResults -> {
            search_results = searchResults;
            fill_table();
        });
    }

    private void searchUsers(OnSearchResultsListener callback) {
        db = FirebaseFirestore.getInstance();
        SharedPreferences sharedPref = getSharedPreferences("QR_pref", Context.MODE_PRIVATE);
        CollectionReference colRef = db.collection("users");
        Query query = colRef.whereGreaterThanOrEqualTo("username", search_string)
                .whereLessThanOrEqualTo("username", search_string + "\uf8ff");

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
                Log.w("Firestore", "Error getting matching documents", task.getException());
            }
        });
    }


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

    public void onClickBack(View view){
        //Intent intent = new Intent(UserProfileActivity.this, UserHomeActivity.class);
        //startActivity(intent);
        finish();
    }



}