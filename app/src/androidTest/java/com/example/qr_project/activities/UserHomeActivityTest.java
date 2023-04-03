package com.example.qr_project.activities;


import androidx.test.core.app.ActivityScenario;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.espresso.IdlingRegistry;
import androidx.test.espresso.IdlingResource;
import androidx.test.espresso.intent.Intents;
import androidx.test.espresso.intent.rule.IntentsRule;
import static org.hamcrest.core.AllOf.allOf;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.intent.matcher.IntentMatchers.*;
import static androidx.test.espresso.matcher.ViewMatchers.hasChildCount;
import static androidx.test.espresso.matcher.ViewMatchers.isRoot;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.platform.app.InstrumentationRegistry.getInstrumentation;

import android.app.Activity;
import android.content.Intent;
import android.widget.ListView;

import com.example.qr_project.R;
import com.example.qr_project.utils.QR_Code;
import com.example.qr_project.utils.UserManager;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;

import java.util.concurrent.atomic.AtomicInteger;

public class UserHomeActivityTest {
    private UserManager userManager;

    // Rule for performing set up and tear down of Espresso-Intents API
    @Rule
    public IntentsRule intentsRule = new IntentsRule();
    ActivityScenario<Activity> activityScenario;
    private FirestoreIdlingResource firestoreIdlingResource;


    @Before
    public void setUp(){
        // Testing user ID (hardcoded, may not work if database is changed manually)
        String userID = "59b75ac7-3361-4e78-86fd-bb9d8f30f820";
        Intent intent = new Intent(ApplicationProvider.getApplicationContext(),
                UserHomeActivity.class)
                .putExtra("userId", userID);

        activityScenario = ActivityScenario.launch(intent);
        userManager = UserManager.newInstance(userID);

        firestoreIdlingResource = new FirestoreIdlingResource();
    }

    @After
    public void tearDown(){
        activityScenario.close();
    }

    // Tests if ScanActivity is initiated after the button is clicked
    @Test
    public void testScanActivityStarted(){
        // Click the camera icon
        onView(withId(R.id.camera_icon_img))
                .perform(scrollTo())
                .perform(click());

        // Check that ScanActivity is now invoked
        Intents.intended(hasComponent(ScanActivity.class.getName()));
    }

    // Tests if LeaderboardActivity is initiated after the button is clicked
    @Test
    public void testLeaderBoardStarted(){
        // Click the camera icon
        onView(withId(R.id.leaderboard_icon_img))
                .perform(scrollTo())
                .perform(click());

        // Check that ScanActivity is now invoked
        Intents.intended(hasComponent(LeaderboardActivity.class.getName()));
    }

    // Tests if UserProfileActivity is initiated after the button is clicked
    @Test
    public void testUserProfileStarted(){
        // Click the camera icon
        onView(withId(R.id.user_icon))
                .perform(scrollTo())
                .perform(click());

        // Check that ScanActivity is now invoked
        Intents.intended(hasComponent(UserProfileActivity.class.getName()));
    }

    // Tests how listView is populated by manually adding QRCodes to DB (and skipping ScanActivity)
    // Userful link: https://developer.android.com/training/testing/espresso/lists#adapter-view-list-items
    @Test
    public void testListViewAdd(){
        // Mock data
        int count = 0;
        QR_Code qrCode1 = new QR_Code("Natus");
        QR_Code qrCode2 = new QR_Code("Vincere");
        QR_Code qrCode3 = new QR_Code("Vae");
        QR_Code qrCode4 = new QR_Code("Victis");
        QR_Code[] qrCodes = {qrCode1, qrCode2, qrCode3, qrCode4};


        IdlingRegistry.getInstance().register(firestoreIdlingResource);

        // Increment the operation count before making a FireStore call
        firestoreIdlingResource.increment();
        // Clear all qrCodes before testing
        userManager.clearQRCodes(
                // Decrement the operation count after the call
                task -> {firestoreIdlingResource.decrement();}
        );

        // Check if listView is empty
        onView(withId(R.id.user_top_qr_table))
                .check(matches(hasChildCount(0)));

        // Check if total qr codes label has 0
        onView(withId(R.id.total_qr_codes))
                .check(matches(withText(String.format("Total QR Codes: %d", count))));


        // Increment the operation count before making a FireStore call
        firestoreIdlingResource.increment();

        // Add qrCode1
        userManager.addQRCode(
                qrCode1,
                task -> {firestoreIdlingResource.decrement();}
        );

        count++;
//        onView(withId(R.id.user_top_qr_table))
//                .check(matches(hasChildCount(count)));

        // Check if total qr codes label has 0
        onView(withId(R.id.total_qr_codes))
                .check(matches(withText(String.format("Total QR Codes: %d", count))));

        // Unregister the Idling Resource
        IdlingRegistry.getInstance().unregister(firestoreIdlingResource);

        //

    }


    /**
     * Idling resource for working with Firestore. It tracks firestore operations. Before using
     * any firestore operations, increments its operation count. Once a firestore operation is done
     * and onCompleteListener is called, the operation counter is decremented to signal. The
     * operation count should be 0 before Espresso will continue with testing
     */
    public class FirestoreIdlingResource implements IdlingResource {
        private ResourceCallback resourceCallback;
        private AtomicInteger operationCount = new AtomicInteger(0);

        @Override
        public String getName() {
            return FirestoreIdlingResource.class.getName();
        }

        @Override
        public boolean isIdleNow() {
            boolean isIdle = operationCount.get() == 0;
            if (isIdle && resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }
            return isIdle;
        }

        @Override
        public void registerIdleTransitionCallback(ResourceCallback callback) {
            this.resourceCallback = callback;
        }

        public void increment() {
            operationCount.getAndIncrement();
        }

        public void decrement() {
            int count = operationCount.decrementAndGet();
            if (count < 0) {
                throw new IllegalArgumentException("Invalid operation count");
            }
            if (count == 0 && resourceCallback != null) {
                resourceCallback.onTransitionToIdle();
            }
        }
    }

}
