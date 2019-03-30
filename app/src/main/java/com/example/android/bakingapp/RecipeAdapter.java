package com.example.android.bakingapp;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

public class RecipeAdapter extends RecyclerView.Adapter<RecipeAdapter.RecipeViewHolder> {

    private final static String LOG_TAG = RecipeAdapter.class.getSimpleName();

    public List<Recipe> mRecipeData;

    private final ListItemClickListener mOnClickListener;

    //Interface for Item Click Listener
    public interface ListItemClickListener {

        //Abstract method to be implemented in MainActivity
        void onListItemClick (int clickedItemIndex);
    }

    //Constructor
    public RecipeAdapter (List<Recipe> recipes, ListItemClickListener listener) {
        mRecipeData = recipes;
        mOnClickListener = listener;
    }

    @NonNull
    @Override
    public RecipeViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int viewType) {
        //Inflate the recipe_item layout
        Context context = viewGroup.getContext();
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(R.layout.recipe_item, viewGroup, false);

        //Attach the view to the ViewHolder
        RecipeViewHolder viewHolder = new RecipeViewHolder(view);

        //Return the ViewHolder object
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull RecipeViewHolder recipeViewHolder, int position) {

        //TODO: Add the servings to the viewholder for display in the Main UI
        Recipe recipe = mRecipeData.get(position);
        String recipeName = recipe.getRecipeName();

        //Bind the data to the views inside ViewHolder object
        recipeViewHolder.recipeItemView.setText(recipeName);
        recipeViewHolder.recipeIcon.setImageResource(R.drawable.main_icon);
    }

    @Override
    public int getItemCount() {
        if (mRecipeData == null) {
            return 0;
        } else {
            return mRecipeData.size();
        }
    }

    public void setRecipeData (List<Recipe> recipeData) {
        mRecipeData = recipeData;
        notifyDataSetChanged();
    }

    class RecipeViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        //Declare variables for each of the views inside the recipe_item layout
        TextView recipeItemView;
        ImageView recipeIcon;

        //Constructor
        private RecipeViewHolder(View itemView) {
            super(itemView);

            //Get references to the views inside each item and initialize the variables
            recipeItemView = itemView.findViewById(R.id.recipe_name_tv);
            recipeIcon = itemView.findViewById(R.id.recipe_icon);

            //Register the OnClickListener with the View held by the ViewHolder
            itemView.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            int clickedPosition = getAdapterPosition();
            mOnClickListener.onListItemClick(clickedPosition);
        }
    }
}
