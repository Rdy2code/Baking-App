package com.example.android.bakingapp;

import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import com.example.android.bakingapp.Fragments.DetailsFragment;
import com.example.android.bakingapp.Fragments.IngredientsFragment;
import com.example.android.bakingapp.Fragments.RecipeStepFragment;
import com.example.android.bakingapp.Fragments.VideoFragment;

import org.apache.commons.lang3.StringUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * This class controls the display of the fragments for the recipe ingredients and brief description
 * of recipe steps. On a tablet, this activity also shows the video and detailed recipe instructions
 * on the right hand side.
 */

public class RecipeActivity extends AppCompatActivity
        implements RecipeStepFragment.OnRecipeStepClickListener {

    private final String LOG_TAG = RecipeActivity.class.getSimpleName();

    //Recipe Object and field variables
    private Recipe mRecipeObject;
    private List<String> mIngredients;
    private String mRecipeName;

    //Variable to track whether device is in two-pane mode
    private boolean mTwoPane;

    //Bundle keys for storing string text for recipe step instructions and video url
    public static final String STEP_DETAILS = "recipe_step_instructions";
    public static final String STEP_VIDEO = "recipe_step_video";
    public static final String STEP_THUMBNAIL ="recipe_thumbnail";

    //Keys for storing savedInstanceState
    private static final String ACTIVITY_TITLE = "title";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recipe);

        //Check if the device is in two-pane mode
        if (findViewById(R.id.tablet_linear_layout) != null) {
            mTwoPane = true;
        }

        if (savedInstanceState != null) {
            mRecipeObject = savedInstanceState.getParcelable(MainActivity.INTENT_KEY_RECIPE_LIST);
            setTitle(savedInstanceState.getString(ACTIVITY_TITLE));
        } else if (!mTwoPane) {
            setUpFragments();
        } else {

            //Instantiate the Ingredients and Short Recipe Steps fragments
            setUpFragments();

            //Get the List<String> of detailed recipe steps.
            List<String> list = mRecipeObject.getDetailedStepDescription();

            //Get the List<String> of video urls
            List<String> urlList = mRecipeObject.getVideoUrl();

            //Get the List<String> of thumbnail urls
            List<String> thumbnailList = mRecipeObject.getThumbnailUrl();

            //Select the instructions for this step from the list at the input position.
            String instructions = list.get(0);

            //Select the videoUrl for this step
            String stepVideo = urlList.get(0);

            //Select the thumbnailUrl for this step
            String thumbnail = thumbnailList.get(0);

            //Bundle the instructions and video URL
            Bundle b = new Bundle();
            b.putString(STEP_DETAILS, instructions);

            //Filter the JSON response for videos or thumbnail Urls
            if (!stepVideo.isEmpty()) {
                b.putString(STEP_VIDEO, stepVideo);
                b.putInt("integer", 0);
            } else if (!thumbnail.isEmpty()) {
                b.putString(STEP_THUMBNAIL,thumbnail);
                b.putInt("integer", 1);
            } else {
                b.putInt("integer", 2);
            }

            //Instantiate the Ingredients, RecipeSteps, Details, and Video Fragment classes
            DetailsFragment detailsFragment = new DetailsFragment();
            VideoFragment videoFragment = new VideoFragment();

            //Notify the VideoFragment that we are in two-pane mode so that it fetches arguments
            //from the RecipeActivity and not the DetailsActivity
            videoFragment.setTwoPane(mTwoPane);
            detailsFragment.setTwoPane(mTwoPane);

            //Attach the Bundle
            detailsFragment.setArguments(b);
            videoFragment.setArguments(b);

            //Get a reference to the FragmentManager
            FragmentManager fragmentManager = getSupportFragmentManager();

            //Add the fragments to the activity
            fragmentManager.beginTransaction()
                    .add(R.id.detailed_instructions_container, detailsFragment)
                    .commit();

            fragmentManager.beginTransaction()
                    .add(R.id.video_container, videoFragment)
                    .commit();
        }

        updateWidgetIngredients(mRecipeName, mIngredients);
    }

    //Callback triggered in the RecipeStepFragment class when user clicks on Recipe step.
    @Override
    public void onRecipeStepSelected(int position) {
        Toast.makeText(this, "The clicked item index is: " + position,
                Toast.LENGTH_SHORT).show();

        //Get the List<String> of detailed recipe steps.
        List<String> list = mRecipeObject.getDetailedStepDescription();

        //Get the List<String> of video urls
        List<String> urlList = mRecipeObject.getVideoUrl();

        //Get the List<String> of thumbnail urls
        List<String> thumbnailList = mRecipeObject.getThumbnailUrl();

        //Initialize the Lists and index in the DetailsActivity for the "Next" button usage
        DetailsActivity.setListInstructions(list);
        DetailsActivity.setListVideoUrls(urlList);
        DetailsActivity.setListThumbnailUrls(thumbnailList);
        DetailsActivity.setListIndex(position);

        //Select the instructions for this step from the list at the input position.
        String instructions = list.get(position);

        //Select the videoUrl for this step
        String stepVideo = urlList.get(position);

        //Select the thumbnailUrl for this step
        String thumbnail = thumbnailList.get(position);

        //Bundle the instructions and video URL for delivery to the DetailsActivity
        Bundle bundle = new Bundle();
        bundle.putString(STEP_DETAILS, instructions);

        //Select for videos or thumbnail Urls if present
        if (!stepVideo.isEmpty()) {
            bundle.putString(STEP_VIDEO, stepVideo);
            bundle.putInt("integer", 0);
        } else if (!thumbnail.isEmpty()) {
            bundle.putString(STEP_THUMBNAIL,thumbnail);
            bundle.putInt("integer", 1);
        } else {
            bundle.putInt("integer", 2);
        }

        //Update the click behavior to handle communication between fragments in a single UI
        if (mTwoPane) {

            //Instantiate the Fragment classes and attach the Bundle
            DetailsFragment dFragment = new DetailsFragment();
            VideoFragment vFragment = new VideoFragment();
            dFragment.setArguments(bundle);
            vFragment.setArguments(bundle);
            dFragment.setTwoPane(mTwoPane);
            vFragment.setTwoPane(mTwoPane);

            //Get a reference to the FragmentManager
            FragmentManager fm = getSupportFragmentManager();

            //Add the fragments to the activity
            fm.beginTransaction()
                    .replace(R.id.detailed_instructions_container, dFragment)
                    .commit();

            fm.beginTransaction()
                    .replace(R.id.video_container, vFragment)
                    .commit();

        } else {
            final Intent intent = new Intent(this, DetailsActivity.class);
            intent.putExtras(bundle);
            startActivity(intent);
        }
    }

    private void updateWidgetIngredients (String recipeName, List<String> ingredients) {

        //Format the list of ingredients into a single string for display purposes
        //https://stackoverflow.com/questions/13695547/arraylist-of-strings-to-one-single-string
        //https://stackoverflow.com/questions/1978933/a-quick-and-easy-way-to-join-array-elements-
        //with-a-separator-the-opposite-of-sp
        String widgetIngredientslist = StringUtils.join(ingredients, "\n");
        Log.d(LOG_TAG, "Widget list is " + widgetIngredientslist);

        //Get an instance of the AppWidgetManager class
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(this);

        //Extract the AppWidget IDs from the appWidgetManager
        int[] appWidgetIds = appWidgetManager
                .getAppWidgetIds(new ComponentName(this, BakingAppWidgetProvider.class));

        //Update all widgets on the homescreen with the list of ingredients
        BakingAppWidgetProvider.updateBakingAppWidgets(
                this, appWidgetManager, recipeName, widgetIngredientslist, appWidgetIds);

    }

    private void setUpFragments () {

        //Get the intent that started the activity
        Intent intentThatStartedThisActivity = getIntent();

        //Unpack the parcel sent with the intent
        mRecipeObject =
                intentThatStartedThisActivity.getParcelableExtra(MainActivity.INTENT_KEY_RECIPE_LIST);

        mRecipeName = mRecipeObject.getRecipeName();
        Log.d(LOG_TAG, "Recipe name is " + mRecipeName);
        setTitle(mRecipeName);

        //Extract the field values
        //List<String> ingredients = mRecipeObject.getListOfIngredients();
        mIngredients = mRecipeObject.getListOfIngredients();

        //Extract the list of short descriptions of recipe steps
        List<String> briefDescription = mRecipeObject.getBriefStepDescription();

        //Create an instance of IngredientsFragment
        IngredientsFragment ingredientsFragment = new IngredientsFragment();

        //Create an instance of RecipeStepFragment
        RecipeStepFragment recipeStepFragment = new RecipeStepFragment();

        //Set the list of ingredients inside the IngredientsFragment
        ingredientsFragment.setIngredients(mIngredients);

        //Set the list of recipe steps inside the RecipeStepFragment
        recipeStepFragment.setRecipeSteps(briefDescription);

        // Get a FragmentManager from the Support Library to start a transaction and
        // add the fragment to the screen
        FragmentManager fragmentManager = getSupportFragmentManager();

        //Fragment transaction: Add the Ingredients container to the host activity layout
        fragmentManager.beginTransaction()
                .add(R.id.ingredients_container, ingredientsFragment)
                .commit();

        //Fragment transaction: Add the Recipe Steps Fragment to the host activity layout
        fragmentManager.beginTransaction()
                .add(R.id.recipe_steps_container, recipeStepFragment)
                .commit();
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putParcelable(MainActivity.INTENT_KEY_RECIPE_LIST, mRecipeObject);
        outState.putString(ACTIVITY_TITLE, getTitle().toString());
    }

}
