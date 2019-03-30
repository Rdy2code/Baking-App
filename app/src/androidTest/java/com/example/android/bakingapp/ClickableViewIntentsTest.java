package com.example.android.bakingapp;

import android.app.Activity;
import android.content.Context;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.IdlingRegistry;
import android.support.test.espresso.contrib.RecyclerViewActions;
import android.support.test.espresso.intent.rule.IntentsTestRule;
import android.support.test.runner.AndroidJUnit4;

import com.example.android.bakingapp.Utils.EspressoIdlingResource;

import org.junit.After;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.app.Instrumentation.ActivityResult;
import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.action.ViewActions.click;
import static android.support.test.espresso.intent.Intents.intended;
import static android.support.test.espresso.intent.Intents.intending;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasComponent;
import static android.support.test.espresso.intent.matcher.IntentMatchers.hasExtraWithKey;
import static android.support.test.espresso.intent.matcher.IntentMatchers.isInternal;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.core.AllOf.allOf;
import static org.hamcrest.core.IsNot.not;


/**
 * Intent Stub
 */
@RunWith(AndroidJUnit4.class)
public class ClickableViewIntentsTest {

    //Initialize and release Intents before and after the test is run
    @Rule
    public IntentsTestRule<MainActivity> mActivityRule = new IntentsTestRule<>(
            MainActivity.class);

    //Set intent responses to OK to block this part of the intent pathway, since we are only testing
    //the intent content in this test--Intent stubbing
    @Before
    public void stubAllExternalIntents() {

        // By default Espresso Intents does not stub any Intents. Stubbing needs to be setup before
        // every test run. In this case all external Intents will be blocked.
        intending(not(isInternal())).respondWith(new ActivityResult(Activity.RESULT_OK, null));

        //Register the idling resource so that Espresso can sync with tasks marked by the decrement
        //and increment method calls on instances of the EspressoIdlingResource class
        IdlingRegistry.getInstance().register((EspressoIdlingResource.getIdlingResource()));

    }

    //Intent Stub: Test that clicking on a recipe in the MainActivity sends an Intent to the
    //RecipeActivity with data associated with the given key.
    @Test
    public void clickRecyclerView_sendsIntentWithRecipeObjectl() {

        onView(withId(R.id.recipe_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        //Assert that when clicked the view sends an intent with the given key and is addressed
        //to the RecipeActivity
        intended(allOf(
                hasExtraWithKey(MainActivity.INTENT_KEY_RECIPE_LIST),
                hasComponent(RecipeActivity.class.getName())));
    }

    @Test
    public void clickRecyclerViewRecipeActivity_sendsIntentToDetailsActivity () {

        //Check if this test is being run on a tablet or a phone
        //Reference: https://stackoverflow.com/questions/26231752/android-espresso-tests-for-phone-and-tablet
        Context targetContext = InstrumentationRegistry.getTargetContext();
        targetContext.getResources().getBoolean(R.bool.tablet);
        boolean isTabletUsed = targetContext.getResources().getBoolean(R.bool.tablet);

        clickRecyclerView_sendsIntentWithRecipeObjectl();

        onView(withId(R.id.recipe_step_recyclerview))
                .perform(RecyclerViewActions.actionOnItemAtPosition(0, click()));

        if (!isTabletUsed) {
            //Phone is being tested
            intended(allOf(
                    hasExtraWithKey(RecipeActivity.STEP_DETAILS),
                    hasComponent(DetailsActivity.class.getName())));
        } else {
            //Tablet is being tested
            onView(withId(R.id.video_container)).check(matches(isDisplayed()));
            onView(withId(R.id.detailed_instructions_container)).check(matches(isDisplayed()));
        }
    }

    @After
    public void unregisterIdlingResource() {
        IdlingRegistry.getInstance().unregister(EspressoIdlingResource.getIdlingResource());
    }

}
