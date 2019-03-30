package com.example.android.bakingapp;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

/**
 * This class manages the RecyclerView for the fragment containing the Short Steps. It is
 * activated from the MainActivity when a user clicks on a Recipe list item.
 */

public class RecipeStepAdapter extends RecyclerView.Adapter<RecipeStepAdapter.RecipeStepViewHolder> {

    //Variable for logging
    private final static String LOG_TAG = RecipeAdapter.class.getSimpleName();

    //Variable for storing the list of recipe steps
    public List<String> mBriefDescriptionStep;

    //Variable for storing a reference to an instance of a list item listener
    private final RecipeStepItemListener mOnClickListener;

    //Interface for binding click listeners to recipe step list items
    public interface RecipeStepItemListener {
        void onItemClick (int clickedItemIndex);
    }

    //Constructor
    public RecipeStepAdapter (List<String> briefDescription, RecipeStepItemListener listener) {
        mBriefDescriptionStep = briefDescription;
        mOnClickListener = listener;
    }

    @Override
    public RecipeStepAdapter.RecipeStepViewHolder onCreateViewHolder
            (ViewGroup viewGroup, int viewType) {

        //Inflate the recipe step list item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_step_item, viewGroup, false);

        //Attach the view to the ViewHolder
        RecipeStepViewHolder viewHolder = new RecipeStepViewHolder(view);

        //Return the ViewHolder object
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(RecipeStepAdapter.RecipeStepViewHolder viewHolder, int position) {

        //Get the Strings at each position in the array, one by one
        String description = mBriefDescriptionStep.get(position);

        //Attach the String to the view inside the ViewHolder
        viewHolder.mBriefRecipeStep.setText(description);

    }

    @Override
    public int getItemCount() {

        if (mBriefDescriptionStep == null) {
            return 0;
        } else {
            return mBriefDescriptionStep.size();
        }
    }

    //Inner class to hold a reference to the View inside each RecyclerView list item
    class RecipeStepViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        public TextView mBriefRecipeStep;

        public RecipeStepViewHolder(View itemView) {
            super(itemView);
            mBriefRecipeStep = itemView.findViewById(R.id.short_recipe_description);
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            //Get the item position in the list of steps
            int clickedPosition = getAdapterPosition();
            //Pass the item position to the RecipeStepItem listener interface
            mOnClickListener.onItemClick(clickedPosition);
        }
    }
}
