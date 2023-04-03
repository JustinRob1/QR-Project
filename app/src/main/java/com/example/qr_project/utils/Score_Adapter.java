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
import com.example.qr_project.activities.QRCodeActivity;

import java.util.ArrayList;

public class Score_Adapter extends ArrayAdapter<Friend> {
    private ArrayList<Friend> scores;
    private Context context;

    public Score_Adapter(Context context, ArrayList<Friend> scores) {
        super(context, 0, scores);
        this.scores = scores;
        this.context = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.score_content, parent,false);
        }

        Friend score = scores.get(position);

        TextView qrCodeNameTextView = view.findViewById(R.id.qr_code_name_1);
        TextView scoreTextView = view.findViewById(R.id.qr_code_score_1);
        TextView rankTextView = view.findViewById(R.id.qr_code_rank_1);


//        qrCodeNameTextView.setText(qrCode.getName());
//        scoreTextView.setText(String.valueOf(qrCode.getScore()));
//        String rank = (position + 1) +".";
//        rankTextView.setText(rank);
//
//        view.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(context, QRCodeActivity.class);
//                intent.putExtra("hash", qrCode.getHash());
//                context.startActivity(intent);
//            }
//        });

        return view;
    }
}
