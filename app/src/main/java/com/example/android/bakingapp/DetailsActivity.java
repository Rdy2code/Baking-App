package com.example.android.bakingapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.example.android.bakingapp.Fragments.DetailsFragment;
import com.example.android.bakingapp.Fragments.VideoFragment;

import java.util.List;

/**
 * This class is the host activity for two fragments: the video and detailed instructions of each step
 * in the recipe.
 */

public class DetailsActivity extends AppCompatActivity {

    private final static String LOG_TAG = DetailsActivity.class.getSimpleName();

    //Bundle key names for storing and retrieving detailed recipe step instructions and video url
    public static final String DETAILED_STEP = "detailed_instructions";
    public static final String VIDEO = "video";
    public static final String THUMBNAIL = "thumbnail";
    public static final String DEFAULT_TEXT = "default_text";

    //Variables for storing references to the fragments hosted by this activity
    DetailsFragment mDetailsFragment;
    VideoFragment mVideoFragment;

    //Variables for storing lists of current recipe steps and urls and current index in these lists
    private static List<String> mRecipeInstructions;
    private static List<String> mRecipeVideoUrls;
    private static List<String> mRecipeThumbnailUrls;
    private static int mListIndex;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_media);

        Button button = findViewById(R.id.next_button);

        if (savedInstanceState == null) {

            //Get the intent that started this activity
            Intent intent = getIntent();

            //Extract the identifier integer from the intent and screen to check which
            //URL, if any, is available for this recipe. Priority is given to VideoUrl.
            int integer = intent.getIntExtra("integer", 2);
            Bundle bundle = new Bundle();
            switch (integer) {
                case 0:     //Has video
                    String videoUrl = intent.getStringExtra(RecipeActivity.STEP_VIDEO);
                    bundle.putString(VIDEO, videoUrl);
                    Log.d(LOG_TAG, "Video URL for this step is: " + videoUrl);
                    break;
                case 1:     //No video, but thumbnail
                    String thumbnailUrl = intent.getStringExtra(RecipeActivity.STEP_THUMBNAIL);
                    bundle.putString(THUMBNAIL, thumbnailUrl);
                    break;
                case 2:     //No video or thumbnail, so load default text
                    bundle.putString(DEFAULT_TEXT, getString(R.string.error_message_network));
                    break;
            }

            String detailedRecipeStep = intent.getStringExtra(RecipeActivity.STEP_DETAILS);

            //https://stackoverflow.com/questions/11387740/where-how-to-getintent-getextras-in-an-android-fragment
            //Bundle the String extra for delivery to a fragment that will display the text
            bundle.putString(DETAILED_STEP, detailedRecipeStep);

            //Instantiate the DetailsFragment class and attach the Bundle
            mDetailsFragment = new DetailsFragment();
            mDetailsFragment.setArguments(bundle);

            //Get a reference to the FragmentManager and add the Fragment to the activity
            FragmentManager fragmentManager = getSupportFragmentManager();

            //Fragment transaction for detailed step instructions
            fragmentManager.beginTransaction()
                    .add(R.id.detailed_instructions_container, mDetailsFragment)
                    .commit();

            //Create a VideoFragment object instance and atttach the bundle with the video Url
            mVideoFragment = new VideoFragment();
            mVideoFragment.setArguments(bundle);

            //Fragment transaction for video
            fragmentManager.beginTransaction()
                    .add(R.id.video_container, mVideoFragment)
                    .commit();
        }


        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Increment the list index until the end of the list is reached
                if (mListIndex < mRecipeInstructions.size() - 1) {
                    mListIndex++;
                } else {
                    mListIndex = 0;
                }

                Bundle bundle = new Bundle();
                String recipeStep = mRecipeInstructions.get(mListIndex);
                String videoUrl = mRecipeVideoUrls.get(mListIndex);
                String thumbnail = mRecipeThumbnailUrls.get(mListIndex);
                bundle.putString(DETAILED_STEP, recipeStep);

                if (!videoUrl.isEmpty()) {
                    bundle.putString(VIDEO, videoUrl);
                } else if (!thumbnail.isEmpty()) {
                    bundle.putString(THUMBNAIL, thumbnail);
                } else {
                    bundle.putString(DEFAULT_TEXT, getString(R.string.no_media_available));
                }

                DetailsFragment detailsFragment = new DetailsFragment();
                VideoFragment videoFragment = new VideoFragment();
                detailsFragment.setArguments(bundle);
                videoFragment.setArguments(bundle);
                //Get a reference to the FragmentManager and add the Fragment to the activity
                FragmentManager fragmentManager = getSupportFragmentManager();
                //Fragment transaction for detailed step instructions
                fragmentManager.beginTransaction()
                        .replace(R.id.detailed_instructions_container, detailsFragment)
                        .commit();
                fragmentManager.beginTransaction()
                        .replace(R.id.video_container, videoFragment)
                        .commit();
            }
        });
    }

    //Setter methods for storing the list of video urls and recipe instructions for the currently
    //selected recipe
    public static void setListIndex(int index) {
        mListIndex = index;
        Log.d(LOG_TAG, "The index is: " + index);
    }

    public static void setListInstructions(List<String> listInstructions) {
        mRecipeInstructions = listInstructions;
        Log.d(LOG_TAG, "Instruction is: " + listInstructions.get(mListIndex));
    }

    public static void setListVideoUrls(List<String> listVideoUrls) {
        mRecipeVideoUrls = listVideoUrls;
    }

    public static void setListThumbnailUrls(List<String> listThumbnailUrls) {
        mRecipeThumbnailUrls = listThumbnailUrls;
    }
}
