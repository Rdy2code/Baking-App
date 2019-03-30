package com.example.android.bakingapp;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.bakingapp.Utils.EspressoIdlingResource;
import com.example.android.bakingapp.Utils.JsonUtils;
import com.example.android.bakingapp.Utils.NetworkUtils;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity implements RecipeAdapter.ListItemClickListener {

    //Initialize views in main layout using the Butter Knife Library
    @BindView(R.id.recipe_recyclerview) RecyclerView mRecyclerView;
    @BindView(R.id.progress_bar) ProgressBar mProgressBar;
    @BindView(R.id.network_error_tv) TextView mErrorMessage;
    public List<Recipe> mRecipeList;
    private static LinearLayoutManager layoutManager;
    private RecipeAdapter mAdapter;

    //Variables for restoring RecyclerView state after rotation
    private Parcelable mListState;

    private static final String LOG_TAG = MainActivity.class.getSimpleName();

    //Key for retrieving recycler view layout from savedInstanceState bundle on rotation
    private static final String RECYCLER_LAYOUT = "recycler_layout_key";

    private static final String SAVED_RECYCLER_VIEW_DATASET_ID = "recipe_list_key";

    public static final String INTENT_KEY_RECIPE_LIST = "recipe";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Implement Butterknife library
        ButterKnife.bind(this);

        //Attach the RecyclerView to a LayoutManager
        layoutManager = new LinearLayoutManager(this);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);

        //Attach the RecyclerView to the adapter
        mAdapter = new RecipeAdapter(mRecipeList, this);

        mRecyclerView.setAdapter(mAdapter);

        if (savedInstanceState==null){

            URL networkUrl = NetworkUtils.buildNetworkUrl(NetworkUtils.buildUri());
            new GetJsonTask(this).execute(networkUrl);

        } else {

            // Get recyclerview position
            mListState = savedInstanceState.getParcelable(RECYCLER_LAYOUT);
            // Get recyclerview items
            mRecipeList = savedInstanceState.getParcelableArrayList(SAVED_RECYCLER_VIEW_DATASET_ID);
            Log.d(LOG_TAG, "Recipe List: " + mRecipeList.get(0).getRecipeName());
            // Restore adapter items
            mAdapter.setRecipeData(mRecipeList);
            // Restore previous instance of the RecyclerView and attachment to LayoutManager
            mRecyclerView.getLayoutManager().onRestoreInstanceState(mListState);

            //Hide the ProgressBar by calling showRecipes()
            //TODO: Note: This is a temporary solution. Replace AsyncTask with an AsyncTaskLoader
            showRecipes();
        }
    }

    @Override
    public void onListItemClick(int clickedItemIndex) {
        Intent intent = new Intent (MainActivity.this, RecipeActivity.class);
        intent.putExtra(INTENT_KEY_RECIPE_LIST, mRecipeList.get(clickedItemIndex));
        startActivity(intent);
    }

    private void showRecipes() {
        mErrorMessage.setVisibility(View.INVISIBLE);
        mRecyclerView.setVisibility(View.VISIBLE);
        mProgressBar.setVisibility(View.INVISIBLE);
    }

    private void showErrormessage() {
        mErrorMessage.setVisibility(View.VISIBLE);
        mRecyclerView.setVisibility(View.INVISIBLE);
    }

    //Fetch the Json response over the internet on a background thread
    private static class GetJsonTask extends AsyncTask<URL, Void, String> {

        //Create a weak reference to the MainActivity to access member variables and methods
        private WeakReference<MainActivity> activityWeakReference;

        //Retain only a weak reference to the Activity to prevent memory leaks
        GetJsonTask(MainActivity context) {
            activityWeakReference = new WeakReference<>(context);
        }

        @Override
        protected void onPreExecute() {
            MainActivity activity = activityWeakReference.get();
            activity.mProgressBar.setVisibility(View.VISIBLE);
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(URL... urls) {
            if (urls.length == 0) {
                return null;
            }

            //Set the Espresso test to idle
            EspressoIdlingResource.increment();

            URL recipeRequestUrl = urls[0];
            String recipeJson = "";

            try {
                recipeJson = NetworkUtils.getJsonFromHttpUrl(recipeRequestUrl);
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Continue with the Espresso test
            EspressoIdlingResource.decrement();

            return recipeJson;
        }

        @Override
        protected void onPostExecute(String recipeJson) {

            MainActivity activity = activityWeakReference.get();
            if (activity == null || activity.isFinishing()) {
                return;
            }

            if (recipeJson != null && !recipeJson.equals("")) {
                activity.showRecipes();
                activity.mRecipeList = JsonUtils.parseJson(recipeJson);
                activity.mAdapter.setRecipeData(activity.mRecipeList);

            } else {
                Toast.makeText(activity, "Error",
                        Toast.LENGTH_SHORT).show();
                activity.showErrormessage();
            }
        }
    }

    //Preserve the RecyclerView Layout and scroll position on device rotation
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Parcelable listState = mRecyclerView.getLayoutManager().onSaveInstanceState();
        // putting recyclerview position
        outState.putParcelable(RECYCLER_LAYOUT, listState);
        // putting recyclerview items
        outState.putParcelableArrayList(SAVED_RECYCLER_VIEW_DATASET_ID, (ArrayList<Recipe>) mRecipeList);
        super.onSaveInstanceState(outState);
        Log.d(LOG_TAG, "onSaveInstanceState called");
    }
}
