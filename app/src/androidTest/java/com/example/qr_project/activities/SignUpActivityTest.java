package com.example.qr_project.activities;

import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.action.ViewActions;
import androidx.test.espresso.intent.rule.IntentsRule;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import static org.junit.Assert.*;
import static org.hamcrest.core.AllOf.allOf;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.clearText;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Intent;

import com.example.qr_project.R;

import org.junit.Rule;
import org.junit.Test;

public class SignUpActivityTest {
    private String username = "User Name";
    private String email = "email";
    private String phoneNumber = "88005553535";

    @Rule
    public IntentsRule intentsRule = new IntentsRule();

    // Checks if LandingPage is invoked after filling all the entries
    @Test
    public void testAllEntiresFilled(){
        // Fill all the entries, press the button and check if LandingPage is invoked
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                SignUpActivity.class);

        ActivityScenario<Activity> activityScenario = ActivityScenario.launchActivityForResult(intent);

        fillEntries(true, true, true);
        onView(withId(R.id.submit_sign_up_button)).perform(click());

        // FIXME: This dialog should be changed later.
        onView(withText("Yes")).perform(click());
        Instrumentation.ActivityResult activityResult = activityScenario
                .getResult();

        assertEquals(0, activityResult.getResultCode());
    }

    // Clears all entries on SignUpActivity
    public void clearEntries(){
        onView(withId(R.id.username_edit_text)).perform(clearText());
        onView(withId(R.id.email_edit_text)).perform(clearText());
        onView(withId(R.id.number_edit_text)).perform(clearText());

    }

    // Fills the specified text entries
    public void fillEntries(boolean fillUsername, boolean fillEmail, boolean fillPhoneNumber){
        if (fillUsername){
            onView(withId(R.id.username_edit_text))
                    .perform(typeText(username))
                    .perform(closeSoftKeyboard());
        }
        if (fillEmail) {
            onView(withId(R.id.email_edit_text))
                    .perform(typeText(email))
                    .perform(closeSoftKeyboard());
        }
        if (fillPhoneNumber) {
            onView(withId(R.id.number_edit_text))
                    .perform(typeText(phoneNumber))
                    .perform(closeSoftKeyboard());
        }
    }

    @Test
    public void testNotAllEntriesFilled(){
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                SignUpActivity.class);

        ActivityScenario<Activity> activityScenario = ActivityScenario.launch(intent);

        boolean[][] tests = {
                {false, false, false},
                {true, false, false},
                {false, true, false},
                {false, false, true},
                {true, true, false},
                {false, true, true},
                {true, false, true}
        };

        // Checks all cases
        for (boolean[] test : tests){
            // Fill the given entries
            fillEntries(test[0], test[1], test[2]);
            onView(withId(R.id.submit_sign_up_button)).perform(click());
            // Check SignUpActivity is still active
            activityScenario.onActivity(activity -> {
                assertTrue(activity instanceof SignUpActivity);
            });
            clearEntries();
        }

        activityScenario.close();
    }
}