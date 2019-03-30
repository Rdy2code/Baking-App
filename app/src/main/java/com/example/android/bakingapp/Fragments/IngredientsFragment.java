package com.example.android.bakingapp.Fragments;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ScrollView;
import android.widget.TextView;

import com.example.android.bakingapp.R;

import java.util.List;

/**This class displays the list of ingredients for a recipe in a fragment**/
public class IngredientsFragment extends Fragment {

    //Logging
    String LOG_TAG = IngredientsFragment.class.getSimpleName();

    //Variables to store the list of ingredients this fragment displays
    private List<String> mIngredients;

    ScrollView mScrollView;

    //String values for use as Bundle keys in saving state between device rotations
    private static final String INGREDIENTS_LIST = "ingredients_list";
    private static final String SCROLL_POSITION = "article_scroll_position";

    //Empty constructor for instantiating the fragment
    public IngredientsFragment() {
    }

    //Inflates the fragment layout
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Get a reference to the ScrollView for preserving state between rotations
        mScrollView = getActivity().findViewById(R.id.ingredient_scrollview);

        //Inflate the Ingredients fragment layout
        View rootView = inflater.inflate(R.layout.fragment_recipe_ingredients,
                container, false);

        //Get a reference to the TextView in the rootView
        TextView ingredientsTv   = rootView.findViewById(R.id.ingredients_text_view);

        //If this is a callback on device rotation, verify the presence of a saved state, and
        //then set the fragment views to the last set of ingredients
        if (savedInstanceState != null) {

            //Get the ingredient
            CharSequence ingredientText = savedInstanceState.getCharSequence(INGREDIENTS_LIST);

            ingredientsTv.setText(ingredientText);

            //Keep scroll position on rotation
            final int[] position = savedInstanceState.getIntArray(SCROLL_POSITION);
            if(position != null) {
                mScrollView.post(new Runnable() {
                    public void run() {
                        mScrollView.scrollTo(position[0], position[1]);
                    }
                });
            }
        }

        if (savedInstanceState == null) {
            //Add the list of ingredients to the TextView for display in fragment container
            if (mIngredients != null) {
                for (int i = 0; i < mIngredients.size(); i++) {
                    ingredientsTv.append(mIngredients.get(i) + "\n");
                }
            } else {
                Log.v(LOG_TAG, "This fragment has a null list of ingredients");
            }

        }

        return rootView;
    }

    //Setter methods for displaying the ingredients of a given recipe
    public void setIngredients (List<String> ingredients) {
        mIngredients = ingredients;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        //Save the text currently displayed upon device rotation
        TextView ingredientTv = getActivity().findViewById(R.id.ingredients_text_view);
        CharSequence text = ingredientTv.getText();
        outState.putCharSequence(INGREDIENTS_LIST, text);

        //Save the scroll position in the ingredient string on rotation
        outState.putIntArray(SCROLL_POSITION,
                new int[]{ mScrollView.getScrollX(), mScrollView.getScrollY()});
    }
}
