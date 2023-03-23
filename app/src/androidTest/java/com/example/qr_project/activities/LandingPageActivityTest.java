package com.example.qr_project.activities;

import static org.junit.Assert.*;

import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import androidx.test.core.app.ActivityScenario;
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

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

public class LandingPageActivityTest {

    // Get the sharedPreferences object using ApplicationProvider
    private final String name = "QR_pref";
    String key = "user_id";
    String userID = "myUserID";
    private SharedPreferences sharedPreferences;

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


    @Before
    public void setUp(){
        sharedPreferences = ApplicationProvider
                .getApplicationContext()
                .getSharedPreferences(name, Context.MODE_PRIVATE);
    }

    // Tests whether SignUpActivity is invoked when user is not signed
    @Test
    public void testUserNotSigned(){
        // Deletes the user from sharedPreferences BEFORE launching the activity
        deleteUser();
        assertNull(sharedPreferences.getString(key, null));

        // Create a stub intent for this activity
        Intent resultIntent = new Intent();
        resultIntent.putExtra("userId", userID);
        Intents.intending(allOf(
                hasComponent(SignUpActivity.class.getName()),
                toPackage(getInstrumentation().getTargetContext().getPackageName())
        )).respondWith(new Instrumentation.ActivityResult(0, resultIntent));

        // Creates a new intent for LandingPageActivity
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                LandingPageActivity.class
        );

        // Launches an activity by a given intent
        ActivityScenario<LandingPageActivity> activityScenario = ActivityScenario.launch(intent);

        // Clicks sign up button
        onView(withId(R.id.sign_up_button))
                .perform(scrollTo())
                .perform(click());

        // Checks SignUpActivity is invoked
        Intents.intended(hasComponent(SignUpActivity.class.getName()));

        // The intent is intercepted with result above, check if UserHomeActivity is now invoked
        Intents.intended(hasComponent(UserHomeActivity.class.getName()));
        activityScenario.close();
    }

    // Tests whether UserHomeActivity is invoked when user is signed
    @Test
    public void testUserIsSigned(){
        createUser();
        assertNotNull(sharedPreferences.getString(key, null));

        // Creates a new intent for LandingPageActivity
        Intent intent = new Intent(
                ApplicationProvider.getApplicationContext(),
                LandingPageActivity.class
        );

        // Launches an activity by a given intent
        ActivityScenario<LandingPageActivity> activityScenario = ActivityScenario.launch(intent);

        // Check if UserHomeActivity is invoked
        Intents.intended(hasComponent(UserHomeActivity.class.getName()));
        activityScenario.close();
    }
}