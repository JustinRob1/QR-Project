package com.example.qr_project.utils;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.util.TypedValue;
import android.view.Gravity;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.example.qr_project.R;

public class UtilityFunctions {

    UtilityFunctions(){

    }

    public static TableRow createNewRow(Context context, String name, String score, int rank, String hash, int rowBackgroundDrawable, int qrLogoDrawable, int arrowDrawable, Intent intent){
        // Create a new TableRow

        TableRow row = new TableRow(context);
        row.setBackgroundResource(rowBackgroundDrawable);
        TableRow.LayoutParams layoutParams = new TableRow.LayoutParams(
                TableRow.LayoutParams.MATCH_PARENT,
                TableRow.LayoutParams.WRAP_CONTENT);

        layoutParams.setMargins(75, 30, 75, 0);
        row.setLayoutParams(layoutParams);

        // Create a new LinearLayout for the TableRow
        LinearLayout linearLayout = new LinearLayout(context);
        linearLayout.setOrientation(LinearLayout.HORIZONTAL);
        linearLayout.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT, 1.0f));
        linearLayout.setGravity(Gravity.CENTER);
        linearLayout.setOnClickListener(view -> {
            context.startActivity(intent);
        });


        // Create a new TextView for the TableRow
        TextView rankTextView = new TextView(context);
        rankTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        rankTextView.setText(rank+ ".");
        rankTextView.setTextColor(Color.BLACK);
        rankTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 22);
        rankTextView.setGravity(Gravity.CENTER);
        rankTextView.setPadding(10, 0, 0, 0);

        // Create a new ImageView for the TableRow
        ImageView qrImageView = new ImageView(context);
        qrImageView.setLayoutParams(new LinearLayout.LayoutParams(75, 75, 1.0f));
        qrImageView.setImageResource(qrLogoDrawable);

        // Create a new TextView for the TableRow
        TextView nameTextView = new TextView(context);
        nameTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        nameTextView.setText(name);
        nameTextView.setTextColor(Color.BLACK);
        nameTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 25);
        nameTextView.setGravity(Gravity.CENTER);
        nameTextView.setPadding(15, 0, 0, 0);

        // Create a new TextView for the TableRow
        TextView scoreTextView = new TextView(context);
        scoreTextView.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, LinearLayout.LayoutParams.WRAP_CONTENT, 1.0f));
        scoreTextView.setText(score);
        scoreTextView.setTextColor(Color.BLACK);
        scoreTextView.setTextSize(TypedValue.COMPLEX_UNIT_SP, 18);
        scoreTextView.setTypeface(null, Typeface.BOLD);
        scoreTextView.setGravity(Gravity.CENTER);
        scoreTextView.setPadding(25, 0, 0, 0);

        // Create a new ImageView for the TableRow
        ImageView arrowImageView = new ImageView(context);
        arrowImageView.setLayoutParams(new LinearLayout.LayoutParams(75, 75, 1.0f));
        arrowImageView.setImageResource(arrowDrawable);
        arrowImageView.setPadding(0, 0, 0, 0);


        linearLayout.addView(rankTextView);
        linearLayout.addView(qrImageView);
        linearLayout.addView(nameTextView);
        linearLayout.addView(scoreTextView);
        linearLayout.addView(arrowImageView);

        row.addView(linearLayout);

        row.setBackgroundResource(R.drawable.leaderboard_row_item);

        return row;
    }

}
