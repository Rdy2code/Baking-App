package com.example.android.bakingapp.Utils;

import android.text.TextUtils;
import android.util.Log;

import com.example.android.bakingapp.Recipe;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class JsonUtils {

    //Constant for logging messages for debugging purposes
    private static final String LOG_TAG = JsonUtils.class.getName();

    //Store JSON keys as String constants
    private static final String JSON_RECIPE_NAME = "name";
    private static final String JSON_RECIPE_INGREDIENTS = "ingredients";
    private static final String JSON_QUANTITY = "quantity";
    private static final String JSON_MEASURE = "measure";
    private static final String JSON_INGREDIENT = "ingredient";
    private static final String JSON_RECIPE_STEPS = "steps";
    private static final String JSON_STEP_NUMBER = "id";
    private static final String JSON_SHORT_DESCRIPTION = "shortDescription";
    private static final String JSON_DETAILED_DESCRIPTION = "description";
    private static final String JSON_VIDEO_URL = "videoURL";
    private static final String JSON_THUMBNAIL_URL = "thumbnailURL";

    //Create a dynamic list of Recipes
    private static final List<Recipe> recipes = new ArrayList<>();

    //Private constructor
    private JsonUtils() {
    }

    //Parse the JSON Object from the Udacity Server, extracting the values associated with the
    //given keys. Add the values to the fields inside the Recipe Object, and add the object to
    //the ArrayList of Recipe objects
    public static List<Recipe> parseJson(String recipeJson) {
        //Verify Json is valid
        if (TextUtils.isEmpty(recipeJson)) {
            return null;
        }

        try {
            //Instantiate a JsonArray containing the response from the network request
            JSONArray baseJsonArray = new JSONArray(recipeJson);

            //Iterate through the list of objects inside the first array
            for (int i = 0; i < baseJsonArray.length(); i++) {

                //Get the first object in the list of objects from the baseJson
                JSONObject jsonObject = baseJsonArray.getJSONObject(i);

                //Get the name of the recipe
                String recipeName = jsonObject.optString(JSON_RECIPE_NAME);

                //Get the array listing the various ingredients
                JSONArray ingredients = jsonObject.optJSONArray(JSON_RECIPE_INGREDIENTS);

                //Generate a list of the amount, measure, and names of the ingredients
                ArrayList<String> ingredientList = ingredientParse(ingredients);

                //Get the array listing the recipe steps, and videos
                JSONArray steps = jsonObject.optJSONArray(JSON_RECIPE_STEPS);

                //Generate a list of short recipe steps
                ArrayList<String> briefList = briefDescriptionParse(steps);

                //Generate a list of detailed recipe steps
                ArrayList<String> detailedList = detailedDescriptionParse(steps);

                //Generate a list of video URLs
                ArrayList<String> videoUrlList = videoUrlParse(steps);

                //Generate a list of thumbnail URLs
                ArrayList<String> thumbnailUrlList = thumbnailParse(steps);

                recipes.add(new Recipe(
                        recipeName,
                        ingredientList,
                        briefList,
                        detailedList,
                        videoUrlList,
                        thumbnailUrlList));
            }
        } catch (JSONException e) {
            Log.e(LOG_TAG, "JSON Error", e);
        }

        return recipes;
    }

    //HELPER METHODS

    // Parse the list of Ingredient Objects and create an ArrayList
    private static ArrayList<String> ingredientParse (JSONArray array) {

        ArrayList<String> list = new ArrayList<>();

        for (int index = 0; index < array.length(); index++) {
            try {
                JSONObject ingredientObject = array.getJSONObject(index);
                double quantity = ingredientObject.optDouble(JSON_QUANTITY);
                String measure = ingredientObject.optString(JSON_MEASURE);
                String ingredient = ingredientObject.optString(JSON_INGREDIENT);
                list.add(quantity + "  " + measure + "    " + ingredient + "\n");

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        return list;
    }

    private static ArrayList<String> briefDescriptionParse (JSONArray array) {

        ArrayList<String> list = new ArrayList<>();

        for (int index = 0; index < array.length(); index++) {
            try {
                JSONObject ingredientObject = array.getJSONObject(index);
                int stepNumber = ingredientObject.optInt(JSON_STEP_NUMBER);
                String shortDescription = ingredientObject.optString(JSON_SHORT_DESCRIPTION);
                list.add(stepNumber + ".  " + shortDescription);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    private static ArrayList<String> detailedDescriptionParse (JSONArray array) {

        ArrayList<String> list = new ArrayList<>();

        for (int index = 0; index < array.length(); index++) {
            try {
                JSONObject ingredientObject = array.getJSONObject(index);
                String detailedDescription = ingredientObject.optString(JSON_DETAILED_DESCRIPTION);
                list.add(detailedDescription);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    private static ArrayList<String> videoUrlParse (JSONArray array) {

        ArrayList<String> list = new ArrayList<>();

        for (int index = 0; index < array.length(); index++) {
            try {
                JSONObject ingredientObject = array.getJSONObject(index);
                String videoUrl = ingredientObject.optString(JSON_VIDEO_URL);
                Log.d(LOG_TAG, "Video URL is " + videoUrl);
                list.add(videoUrl);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }

    private static ArrayList<String> thumbnailParse (JSONArray array) {

        ArrayList<String> list = new ArrayList<>();

        for (int index = 0; index < array.length(); index++) {
            try {
                JSONObject ingredientObject = array.getJSONObject(index);
                String thumbnailUrl = ingredientObject.optString(JSON_THUMBNAIL_URL);
                Log.d(LOG_TAG, "Thumbnail URL is " + thumbnailUrl);
                list.add(thumbnailUrl);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        return list;
    }
}
