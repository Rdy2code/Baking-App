package com.example.android.bakingapp;

import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.Utils.EspressoIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;

//Reference: https://www.pluralsight.com/guides/testing-in-android-with-espresso-part-2

@RunWith(AndroidJUnit4.class)
public class MainActivityRecyclerViewTest {

    @Rule
    public ActivityTestRule<MainActivity> mActivityTestRule =
            new ActivityTestRule<>(MainActivity.class);

    @Before
    public void registerIdlingResource() {
        //Synchronize this test with the potentially, long-running background Asynctask
        //Although, by default Espresso synchronizes view operations with the UI thread
        //and AsyncTasks (see IdlingResource developer page)
        IdlingRegistry.getInstance().register(EspressoIdlingResource.getIdlingResource());
    }

    @Test
    public void clickRecyclerViewItem_OpensRecipeActivity() {

        //Click on a specific item in the RecyclerView shown in the MainActivity
        onView(withId(R.id.recipe_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //Check to see that the Ingredients fragment and Recipe Step Fragments are displayed
        onView(withId(R.id.ingredients_container))
                .check(matches(isDisplayed()));

        onView(withId(R.id.recipe_steps_container))
                .check(matches(isDisplayed()));

        onView(withId(R.id.recipe_step_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        onView(withId(R.id.video_container))
                .check((matches(isDisplayed())));
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }
}
