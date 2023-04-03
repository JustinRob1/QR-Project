package com.example.qr_project.utils;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.qr_project.R;
import com.example.qr_project.activities.UserProfileActivity;

import java.util.List;

public class ScannersAdapter extends ArrayAdapter<Friend> {

    private List<Friend> friends;

    public ScannersAdapter(Context context, List<Friend> friends) {
        super(context, 0, friends);
        this.friends = friends;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if (view == null) {
            view = LayoutInflater.from(getContext()).inflate(R.layout.scanner_content, parent, false);
        }

        Friend friend = friends.get(position);

        TextView scannerName = view.findViewById(R.id.scanner_name);
        TextView scannerScore = view.findViewById(R.id.scanner_score);

        scannerName.setText(friend.getName());
        scannerScore.setText(String.valueOf(friend.getScore()));
        view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getContext(), UserProfileActivity.class);
                intent.putExtra("userId", friend.getId());
                getContext().startActivity(intent);
            }
        });

        return view;
    }
}

