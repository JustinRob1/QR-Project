package com.example.qr_project.activities;


import static androidx.test.espresso.Espresso.onData;
import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.pressImeActionButton;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.scrollTo;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.isDisplayed;
import static androidx.test.espresso.matcher.ViewMatchers.withClassName;
import static androidx.test.espresso.matcher.ViewMatchers.withContentDescription;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withParent;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.anything;
import static org.hamcrest.Matchers.is;

import android.view.View;
import android.view.ViewGroup;
import android.view.ViewParent;

import androidx.test.espresso.DataInteraction;
import androidx.test.espresso.ViewInteraction;
import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;
import androidx.test.filters.LargeTest;
import androidx.test.rule.GrantPermissionRule;

import com.example.qr_project.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.hamcrest.core.IsInstanceOf;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@LargeTest
@RunWith(AndroidJUnit4.class)
public class ProjectTest {

    @Rule
    public ActivityScenarioRule<LandingPageActivity> mActivityScenarioRule =
            new ActivityScenarioRule<>(LandingPageActivity.class);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant(
                    "android.permission.ACCESS_FINE_LOCATION",
                    "android.permission.CAMERA");

    @Test
    public void projectTest() {
        ViewInteraction appCompatButton = onView(
                allOf(withId(R.id.sign_up_button), withText("SIGN UP"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatButton.perform(scrollTo(), click());

        ViewInteraction appCompatEditText = onView(
                allOf(withId(R.id.username_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                2)));
        appCompatEditText.perform(scrollTo(), replaceText("Khoi#12345"), closeSoftKeyboard());

        ViewInteraction appCompatEditText2 = onView(
                allOf(withId(R.id.email_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatEditText2.perform(scrollTo(), replaceText("Hash$$99"), closeSoftKeyboard());

        ViewInteraction appCompatEditText3 = onView(
                allOf(withId(R.id.email_edit_text), withText("Hash$$99"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                3)));
        appCompatEditText3.perform(pressImeActionButton());

        ViewInteraction appCompatEditText4 = onView(
                allOf(withId(R.id.number_edit_text),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatEditText4.perform(scrollTo(), replaceText("13"), closeSoftKeyboard());

        ViewInteraction appCompatEditText5 = onView(
                allOf(withId(R.id.number_edit_text), withText("13"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatEditText5.perform(pressImeActionButton());

        ViewInteraction appCompatEditText6 = onView(
                allOf(withId(R.id.number_edit_text), withText("13"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatEditText6.perform(scrollTo(), replaceText("1369"));

        ViewInteraction appCompatEditText7 = onView(
                allOf(withId(R.id.number_edit_text), withText("1369"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText7.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton2 = onView(
                allOf(withId(R.id.submit_sign_up_button), withText("SIGN UP"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                5)));
        appCompatButton2.perform(scrollTo(), click());

        ViewInteraction appCompatEditText8 = onView(
                allOf(withId(R.id.number_edit_text), withText("(136) 9"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatEditText8.perform(scrollTo(), click());

        ViewInteraction appCompatEditText9 = onView(
                allOf(withId(R.id.number_edit_text), withText("(136) 9"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatEditText9.perform(scrollTo(), replaceText("(136) 9254"));

        ViewInteraction appCompatEditText10 = onView(
                allOf(withId(R.id.number_edit_text), withText("(136) 9254"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText10.perform(closeSoftKeyboard());

        ViewInteraction appCompatEditText11 = onView(
                allOf(withId(R.id.number_edit_text), withText("(136) 925-4"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4)));
        appCompatEditText11.perform(scrollTo(), replaceText("(136) 925-4368"));

        ViewInteraction appCompatEditText12 = onView(
                allOf(withId(R.id.number_edit_text), withText("(136) 925-4368"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                4),
                        isDisplayed()));
        appCompatEditText12.perform(closeSoftKeyboard());

        ViewInteraction appCompatButton3 = onView(
                allOf(withId(R.id.submit_sign_up_button), withText("SIGN UP"),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                5)));
        appCompatButton3.perform(scrollTo(), click());

        ViewInteraction appCompatImageView = onView(
                allOf(withId(R.id.camera_icon_img), withContentDescription("Camera icon: Tap to scan a new QR Code"),
                        childAtPosition(
                                allOf(withId(R.id.icon_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                9)),
                                1)));
        appCompatImageView.perform(scrollTo(), click());

        ViewInteraction appCompatImageView2 = onView(
                allOf(withId(R.id.camera_icon_img), withContentDescription("Camera icon: Tap to scan a new QR Code"),
                        childAtPosition(
                                allOf(withId(R.id.icon_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                9)),
                                1)));
        appCompatImageView2.perform(scrollTo(), click());

        ViewInteraction appCompatImageView3 = onView(
                allOf(withId(R.id.camera_icon_img), withContentDescription("Camera icon: Tap to scan a new QR Code"),
                        childAtPosition(
                                allOf(withId(R.id.icon_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                9)),
                                1)));
        appCompatImageView3.perform(scrollTo(), click());

        DataInteraction linearLayout = onData(anything())
                .inAdapterView(allOf(withId(R.id.user_top_qr_table),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(0);
        linearLayout.perform(scrollTo(), click());

        ViewInteraction tableRow = onView(
                allOf(withId(R.id.see_comment_row),
                        childAtPosition(
                                allOf(withId(R.id.bottom_buttons),
                                        childAtPosition(
                                                withId(R.id.qr_code_details_border),
                                                6)),
                                4)));
        tableRow.perform(scrollTo(), click());

        ViewInteraction tableRow2 = onView(
                allOf(withId(R.id.add_comment_row),
                        childAtPosition(
                                allOf(withId(R.id.bottom_buttons),
                                        childAtPosition(
                                                withId(R.id.qr_code_details_border),
                                                6)),
                                0)));
        tableRow2.perform(scrollTo(), click());

        ViewInteraction appCompatEditText13 = onView(
                allOf(withId(R.id.comment_text),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.custom),
                                        0),
                                0),
                        isDisplayed()));
        appCompatEditText13.perform(replaceText("good\n"), closeSoftKeyboard());

        ViewInteraction materialButton = onView(
                allOf(withId(android.R.id.button1), withText("Submit"),
                        childAtPosition(
                                childAtPosition(
                                        withId(com.google.android.material.R.id.buttonPanel),
                                        0),
                                3)));
        materialButton.perform(scrollTo(), click());

        ViewInteraction appCompatImageView4 = onView(
                allOf(withId(R.id.go_back_btn),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                0)));
        appCompatImageView4.perform(scrollTo(), click());

        DataInteraction linearLayout2 = onData(anything())
                .inAdapterView(allOf(withId(R.id.user_top_qr_table),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(0);
        linearLayout2.perform(scrollTo(), click());

        ViewInteraction appCompatImageView5 = onView(
                allOf(withId(R.id.go_back_btn),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                0)));
        appCompatImageView5.perform(scrollTo(), click());

        ViewInteraction appCompatImageView6 = onView(
                allOf(withId(R.id.leaderboard_icon_img), withContentDescription("Leaderboard icon: Tap to view the leaderboard"),
                        childAtPosition(
                                allOf(withId(R.id.icon_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                9)),
                                2)));
        appCompatImageView6.perform(scrollTo(), click());

        ViewInteraction appCompatImageView7 = onView(
                allOf(withId(R.id.circle_left),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                0)));
        appCompatImageView7.perform(scrollTo(), click());

        ViewInteraction appCompatEditText14 = onView(
                allOf(withId(R.id.search_bar),
                        childAtPosition(
                                allOf(withId(R.id.Header_Layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1)));
        appCompatEditText14.perform(scrollTo(), replaceText("khoi"), closeSoftKeyboard());

        ViewInteraction appCompatImageView8 = onView(
                allOf(withId(R.id.add_friend_btn),
                        childAtPosition(
                                allOf(withId(R.id.Header_Layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                2)));
        appCompatImageView8.perform(scrollTo(), click());

        ViewInteraction appCompatImageView9 = onView(
                allOf(withId(R.id.back_btn),
                        childAtPosition(
                                childAtPosition(
                                        withClassName(is("android.widget.ScrollView")),
                                        0),
                                0)));
        appCompatImageView9.perform(scrollTo(), click());

        ViewInteraction appCompatEditText15 = onView(
                allOf(withId(R.id.search_bar), withText("khoi"),
                        childAtPosition(
                                allOf(withId(R.id.Header_Layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1)));
        appCompatEditText15.perform(scrollTo(), replaceText("Khoi"));

        ViewInteraction appCompatEditText16 = onView(
                allOf(withId(R.id.search_bar), withText("Khoi"),
                        childAtPosition(
                                allOf(withId(R.id.Header_Layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                1),
                        isDisplayed()));
        appCompatEditText16.perform(closeSoftKeyboard());

        ViewInteraction appCompatImageView10 = onView(
                allOf(withId(R.id.add_friend_btn),
                        childAtPosition(
                                allOf(withId(R.id.Header_Layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                0)),
                                2)));
        appCompatImageView10.perform(scrollTo(), click());

        ViewInteraction linearLayout3 = onView(
                childAtPosition(
                        childAtPosition(
                                withId(R.id.user_search_result_tbl),
                                0),
                        0));
        linearLayout3.perform(scrollTo(), click());

        DataInteraction linearLayout4 = onData(anything())
                .inAdapterView(allOf(withId(R.id.top_qr_table),
                        childAtPosition(
                                withClassName(is("android.widget.LinearLayout")),
                                0)))
                .atPosition(1);
        linearLayout4.perform(scrollTo(), click());

        ViewInteraction tableRow3 = onView(
                allOf(withId(R.id.see_comment_row),
                        childAtPosition(
                                allOf(withId(R.id.bottom_buttons),
                                        childAtPosition(
                                                withId(R.id.qr_code_details_border),
                                                6)),
                                4)));
        tableRow3.perform(scrollTo(), click());

        ViewInteraction materialTextView = onView(
                allOf(withText("See Photo"),
                        childAtPosition(
                                allOf(withId(R.id.see_photo_row),
                                        childAtPosition(
                                                withId(R.id.bottom_buttons),
                                                2)),
                                0),
                        isDisplayed()));
        materialTextView.perform(click());

        ViewInteraction materialTextView2 = onView(
                allOf(withText("See Photo"),
                        childAtPosition(
                                allOf(withId(R.id.see_photo_row),
                                        childAtPosition(
                                                withId(R.id.bottom_buttons),
                                                2)),
                                0),
                        isDisplayed()));
        materialTextView2.perform(click());

        ViewInteraction materialTextView3 = onView(
                allOf(withText("See Who Has Scanned"),
                        childAtPosition(
                                allOf(withId(R.id.see_scanner_row),
                                        childAtPosition(
                                                withId(R.id.bottom_buttons),
                                                1)),
                                0),
                        isDisplayed()));
        materialTextView3.perform(click());

        ViewInteraction appCompatImageView11 = onView(
                allOf(withId(R.id.leaderboard_icon_img), withContentDescription("Leaderboard icon: Tap to view the leaderboard"),
                        childAtPosition(
                                allOf(withId(R.id.icon_layout),
                                        childAtPosition(
                                                withClassName(is("android.widget.LinearLayout")),
                                                4)),
                                2)));
        appCompatImageView11.perform(scrollTo(), click());

        ViewInteraction view = onView(
                allOf(withId(android.R.id.navigationBarBackground),
                        withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)),
                        isDisplayed()));
        view.check(matches(isDisplayed()));

        ViewInteraction view2 = onView(
                allOf(withId(android.R.id.navigationBarBackground),
                        withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)),
                        isDisplayed()));
        view2.check(matches(isDisplayed()));

        ViewInteraction view3 = onView(
                allOf(withId(android.R.id.statusBarBackground),
                        withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)),
                        isDisplayed()));
        view3.check(matches(isDisplayed()));

        ViewInteraction view4 = onView(
                allOf(withId(android.R.id.statusBarBackground),
                        withParent(IsInstanceOf.<View>instanceOf(android.widget.FrameLayout.class)),
                        isDisplayed()));
        view4.check(matches(isDisplayed()));

        ViewInteraction textView = onView(
                allOf(withText("QR-Project"),
                        withParent(allOf(withId(com.google.android.material.R.id.action_bar),
                                withParent(withId(com.google.android.material.R.id.action_bar_container)))),
                        isDisplayed()));
        textView.check(matches(withText("QR-Project")));

        ViewInteraction textView2 = onView(
                allOf(withText("QR-Project"),
                        withParent(allOf(withId(com.google.android.material.R.id.action_bar),
                                withParent(withId(com.google.android.material.R.id.action_bar_container)))),
                        isDisplayed()));
        textView2.check(matches(withText("QR-Project")));
    }

    private static Matcher<View> childAtPosition(
            final Matcher<View> parentMatcher, final int position) {

        return new TypeSafeMatcher<View>() {
            @Override
            public void describeTo(Description description) {
                description.appendText("Child at position " + position + " in parent ");
                parentMatcher.describeTo(description);
            }

            @Override
            public boolean matchesSafely(View view) {
                ViewParent parent = view.getParent();
                return parent instanceof ViewGroup && parentMatcher.matches(parent)
                        && view.equals(((ViewGroup) parent).getChildAt(position));
            }
        };
    }
}
