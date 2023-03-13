package com.example.qr_project;

import static org.junit.jupiter.api.Assertions.assertEquals;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.qr_project.*;
import com.example.qr_project.activities.SignUpActivity;
import com.example.qr_project.activities.UserHomeActivity;
import com.example.qr_project.utils.Player;
import com.google.firebase.firestore.FirebaseFirestore;

import org.junit.Test;

import java.util.UUID;

public class SignUpTesting {
    EditText usernameEditText;
    EditText emailEditText;
    EditText phoneNumberEditText;
    Button signUpButton;

    FirebaseFirestore db;

    private SignUpActivity mockUserList() {
        SignUpActivity userlist = new SignUpActivity();
        userlist.add(mockUserList());
        SignUpActivity SignUpActivity = new SignUpActivity();
        return SignUpActivity;
    }
    @Test
    void testSignUpClick(){
        SignUpActivity SignUpActivity = mockUserList();
        assertEquals(0, mockUserList().compareTo(SignUpActivity.getString().get(0)));
        SignUpActivity userlist = new SignUpActivity("User Name", "Email", "Phone");
        userlist.add(SignUpActivity);
        assertEquals(0, SignUpActivity.compareTo(SignUpActivity.getString().get(0)));
        assertEquals(0, mockUserList().compareTo(SignUpActivity.getString().get(1)));
    }

}
