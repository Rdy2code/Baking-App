package com.example.android.bakingapp.Fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeStepAdapter;

import java.util.ArrayList;
import java.util.List;

/**
 * This fragment displays the list of short recipe steps. It implements the onItemClickListener
 * interface defined in the RecipeStepAdapter class which manages the RecyclerView.
 */
public class RecipeStepFragment extends Fragment implements RecipeStepAdapter.RecipeStepItemListener {

    //Variable for the fragment-based interface that will be used to communicate with the host activity
    public OnRecipeStepClickListener mCallback;

    //Variable for storing the list of recipe steps for the Recipe object that started the
    // RecipeActivity
    private List<String> mRecipeStepList;

    //String values for use as keys in saving state between device rotations
    private static final String STEPS_LIST_ID = "steps_list";

    //OnRecipeStepClickListener interface, calls a method in the host activity named onRecipeStepSelected
    public interface OnRecipeStepClickListener {

        //Takes as parameter the position of the recipe step in the RecyclerView
        void onRecipeStepSelected (int position);
    }

    //onAttach ensures the host activity implements the callback method in the interface.
    //If not, throw and exception
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);

        try{
            mCallback = (OnRecipeStepClickListener) context;
        } catch (ClassCastException e) {
            throw new ClassCastException(context.toString() + " must implement OnRecipeStepClickListener");
        }
    }

    //Constructor
    public RecipeStepFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        //If this is a callback on device rotation, verify the presence of a saved state. If so
        //set the fragment views to the last set of recipe steps
        if (savedInstanceState != null) {
            mRecipeStepList = savedInstanceState.getStringArrayList(STEPS_LIST_ID);
        }

        //Inflate the Recipe Step RecyclerView fragment layout
        View rootView = inflater.inflate(R.layout.fragment_recipe_steps_main,
                container, false);

        //Get a reference to the recycler view inside the fragment layout xml
        final RecyclerView recyclerView = rootView.findViewById(R.id.recipe_step_recyclerview);

        //Set the LayoutManager
        LinearLayoutManager layoutManager = new LinearLayoutManager(this.getActivity());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setHasFixedSize(true);

        //If the ArrayList of Recipe Steps is valid, set the adapter
        if (mRecipeStepList != null) {
            RecipeStepAdapter adapter = new RecipeStepAdapter(mRecipeStepList, this);
            recyclerView.setAdapter(adapter);
        }

        //Set a click listener on the

        return rootView;
    }

    //Setter methods for displaying the steps of a given recipe
    public void setRecipeSteps (List<String> recipeSteps) {
        mRecipeStepList = recipeSteps;
    }

    //Save the current state of each fragment on rotation by saving the list
    //Note that 2nd argument is cast to an ArrayList type Integer
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putStringArrayList(STEPS_LIST_ID, (ArrayList<String>) mRecipeStepList);
    }

    //TODO: Add logic to communicate with the host activity,
    // which should open a new activity with video player in one fragment and a detailed description
    // of the recipe step in another
    //Add interface to this fragment to communicate up to the host activity, which will handle
    //activity to activity communication.
    @Override
    public void onItemClick(int clickedItemIndex) {

        //Trigger the callback method and pass the position that was clicked
        mCallback.onRecipeStepSelected(clickedItemIndex);
    }
}
