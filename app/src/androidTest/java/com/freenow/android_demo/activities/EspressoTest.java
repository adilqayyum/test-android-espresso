package com.freenow.android_demo.activities;

import android.content.Intent;
import android.support.test.espresso.matcher.RootMatchers;
import android.support.test.espresso.ViewInteraction;
import android.support.test.filters.LargeTest;
import android.support.test.rule.ActivityTestRule;
import android.support.test.rule.GrantPermissionRule;
import android.support.test.runner.AndroidJUnit4;
import android.view.*;
import android.support.test.espresso.IdlingPolicies;
import android.support.test.espresso.IdlingResource;
import android.support.test.espresso.IdlingRegistry;
import java.util.concurrent.TimeUnit;
import com.freenow.android_demo.R;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.*;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.*;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.action.ViewActions.closeSoftKeyboard;
import static android.support.test.espresso.action.ViewActions.replaceText;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withClassName;
import static android.support.test.espresso.matcher.ViewMatchers.withContentDescription;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.Matchers.allOf;
import static org.hamcrest.Matchers.is;
import java.io.IOException;


@LargeTest
@RunWith(AndroidJUnit4.class)
public class EspressoTest {

    @Before
    public void resetTimeout() {
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(20, TimeUnit.SECONDS);
    }

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class, true, false);

    @Rule
    public GrantPermissionRule mGrantPermissionRule =
            GrantPermissionRule.grant("android.permission.ACCESS_FINE_LOCATION");

    @Test
    public void espressoTestLogin() throws IOException {

        Intent intent = new Intent();
        mActivityTestRule.launchActivity(intent);

        GetCredentials GC = new GetCredentials();
        GC.credentialsMethod();

        onView(withId(R.id.edt_username))
                .check(matches(isDisplayed()))
                .perform(replaceText(GetCredentials.uName), closeSoftKeyboard());

        onView(withId(R.id.edt_password))
                .check(matches(isDisplayed()))
                .perform(replaceText(GetCredentials.pWord), closeSoftKeyboard());

        onView(withId(R.id.btn_login))
                .check(matches(isDisplayed()))
                .perform(click());
    }

    @Test
    public void espressoTestNavigation() {

        Intent intent = new Intent();
        mActivityTestRule.launchActivity(intent);

        // Making sure Espresso does not time out
        IdlingPolicies.setMasterPolicyTimeout(60, TimeUnit.SECONDS);
        IdlingPolicies.setIdlingResourceTimeout(60, TimeUnit.SECONDS);

        // Setting up the wait
        IdlingResource idlingResource = new ElapsedTimeIdlingResource(60);
        IdlingRegistry.getInstance().register(idlingResource);


        onView(withId(R.id.fab))
                .check(matches(isDisplayed()))
                .perform(click());


        ViewInteraction searchField = onView(allOf(withId(R.id.textSearch),childAtPosition(
                                allOf(withId(R.id.searchContainer),
                                        childAtPosition(
                                                withClassName(
                                                        is("android.support.design.widget.CoordinatorLayout")),
                                                1)),0),isDisplayed()));

        searchField.perform(click());
        searchField.perform(replaceText("sa"), closeSoftKeyboard());

        onView(withText("Sarah Scott"))
                .inRoot(RootMatchers.isPlatformPopup())
                .perform(click());

        // Verify Driver Name
        onView(withId(R.id.textViewDriverName))
                .check(matches(withText("Sarah Scott")));

        ViewInteraction callButton = onView(allOf(withId(R.id.fab),childAtPosition(
                                childAtPosition(withId(android.R.id.content),0),2),
                        isDisplayed()));
        callButton.perform(click());


        // Logging out the application
        mActivityTestRule.launchActivity(null);

        ViewInteraction menuButton = onView(
                allOf(withContentDescription("Open navigation drawer"),
                        childAtPosition(
                                allOf(withId(R.id.toolbar),
                                        childAtPosition(
                                                withClassName(is("android.support.design.widget.AppBarLayout")),
                                                0)),1), isDisplayed()));

        menuButton.perform(click());

        // Verify Log Out Button Exists
        onView(withId(R.id.design_menu_item_text))
                .check(matches(isDisplayed()));

        ViewInteraction navigationMenuItemView = onView(
                allOf(childAtPosition(
                        allOf(withId(R.id.design_navigation_view),
                                childAtPosition(
                                        withId(R.id.nav_view),
                                        0)), 1), isDisplayed()));

        navigationMenuItemView.perform(click());


        // Clean up
        IdlingRegistry.getInstance().unregister(idlingResource);
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
