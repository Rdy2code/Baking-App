package com.example.android.bakingapp.Fragments;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.DetailsActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeActivity;

import org.apache.commons.lang3.StringEscapeUtils;


public class DetailsFragment extends Fragment {

    private final static String LOG_TAG = DetailsFragment.class.getSimpleName();

    //String values for use as Bundle keys in saving state between device rotations
    private static final String RECIPE_STEP_INSTRUCTIONS = "instructions";

    private boolean mTwoPane;
    private String recipeInstructions;

    //Constructor for instantiating the fragment
    public DetailsFragment() {
    }

    /**
     * Inflates the fragment layout and sets resources used for image, media, and text views
     */
    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        //Inflate the fragment layout
        View rootView = inflater.inflate(
                R.layout.fragment_detailed_instructions, container, false);

        //Get a reference to the TextView inside the rootView
        TextView textView = rootView.findViewById(R.id.detailed_recipe_instructions_textview);

        if (savedInstanceState != null) {
            String savedText = savedInstanceState.getString(RECIPE_STEP_INSTRUCTIONS);
            textView.setText(savedText);
        } else if (!mTwoPane) {
            recipeInstructions = getArguments().getString(DetailsActivity.DETAILED_STEP);
            //Read the escape characters, if any, that are present in the string
            recipeInstructions = StringEscapeUtils.unescapeJava(recipeInstructions);
            //Update the UI
            textView.setText(recipeInstructions);
        }

        if (mTwoPane)  {
            recipeInstructions = getArguments().getString(RecipeActivity.STEP_DETAILS);
            recipeInstructions = StringEscapeUtils.unescapeJava(recipeInstructions);
            //Update the UI
            textView.setText(recipeInstructions);
        }

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the text currently displayed upon device rotation
        TextView detailedStep = getActivity().findViewById(R.id.detailed_recipe_instructions_textview);

        outState.putString(RECIPE_STEP_INSTRUCTIONS, detailedStep.getText().toString());
    }

    //Setter method to notify DetailsFragment we are in TwoPane mode and that arguments are coming
    //from the RecipeActivity
    public void setTwoPane (boolean screenMode) {
        mTwoPane = screenMode;
    }
}

