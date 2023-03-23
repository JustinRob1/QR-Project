package com.example.qr_project.activities;

import static org.junit.Assert.*;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.ViewAction;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.filters.LargeTest;
import static org.hamcrest.core.AllOf.allOf;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import com.example.qr_project.R;

import org.junit.Rule;
import org.junit.Test;

public class LandingPageActivityTest {

    // Get the sharedPreferences object using ApplicationProvider
    private final String name = "QR_pref";
    String key = "user_id";
    String userID = "myUserID";
    private SharedPreferences sharedPreferences = ApplicationProvider
            .getApplicationContext()
            .getSharedPreferences(name, Context.MODE_PRIVATE);


    // Launches the given activity before and after the tests.
    @Rule
    public ActivityScenarioRule<LandingPageActivity> activityScenarioRule
            = new ActivityScenarioRule<>(LandingPageActivity.class);


    // Rule for performing set up and tear down of Espresso-Intents API
    @Rule
    public IntentsRule intentsRule = new IntentsRule();

    // Deletes userID from the sharedPreferences file
    public void deleteUser(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(key);
        editor.commit();
    }

    // Creates userID in the sharedPreferences file
    public void createUser(){
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(key, userID);
        editor.commit();
    }

    // Tests whether SignUpActivity is invoked when user is not signed
    @Test
    public void testUserNotSigned(){

        Intent signUpIntent = new Intent(getInstrumentation().getTargetContext(), SignUpActivity.class);
        Intent resultIntent = new Intent();

        Intents.intending(allOf(
                hasComponent(SignUpActivity.class.getName()),
                toPackage(
                        getInstrumentation()
                        .getTargetContext()
                        .getPackageName())
        ))
                .respondWith(new Instrumentation.ActivityResult(0, resultIntent));

        // Check if SignUpActivity is invoked after clicking
        deleteUser();
        onView(withId(R.id.sign_up_button)).perform(scrollTo()).perform(click());
        Intents.intended(hasComponent(SignUpActivity.class.getName()));

        // Check if UserHomeActivity is invoked after result is received
        Intents.intended(hasComponent(UserHomeActivity.class.getName()));

    }

    // Tests whether UserHomeActivity is invoked when user is signed
    @Test
    public void testUserIsSigned(){
        createUser();
        Intents.intended(allOf(
                hasComponent(UserHomeActivity.class.getName()),
                hasExtra("userId", userID)
        ));
    }
}