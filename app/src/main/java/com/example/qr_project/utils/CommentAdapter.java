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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class CommentAdapter extends ArrayAdapter<Comment> {

    private ArrayList<Comment> comments;
    private Context context;

    UserManager userManager = UserManager.getInstance();

    QRCodeManager qrCodeManager;

    Intent intent;

    public CommentAdapter(Context context, ArrayList<Comment> comments, Intent intent) {
        super(context, 0, comments);
        this.comments = comments;
        this.context = context;
        this.intent = intent;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        View view = convertView;
        if(view == null){
            view = LayoutInflater.from(context).inflate(R.layout.comment_content, parent,false);
        }

        Comment comment = comments.get(position);
        qrCodeManager = new QRCodeManager(comment.getHash_code());

        TextView commentUsername = view.findViewById(R.id.comment_username);
        TextView commentText = view.findViewById(R.id.comment_text);


        commentUsername.setText(comment.getUserName());
        commentText.setText(String.valueOf(comment.getComment()));

        if (userManager.getUserID().equals(comment.getUserID())){
            view.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    Map<String, Object> map = new HashMap<>();

                    map.put("username", comment.getUserName());
                    map.put("commentText", comment.getComment());
                    map.put("userID", comment.getUserID());

                    qrCodeManager.removeComment(map);
                    context.startActivity(intent);

                    return true;
                }
            });
        } else {
            // Disable the long-click listener
            view.setOnLongClickListener(null);
        }

        return view;
    }
}
