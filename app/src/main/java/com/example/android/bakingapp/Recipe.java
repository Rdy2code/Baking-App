package com.example.android.bakingapp;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;

public class Recipe implements Parcelable {

    private String mRecipeName;
    private List<String> mIngredients;
    private List<String> mBriefStepDescription;
    private List<String> mDetailedStepDescription;
    private List<String> mVideoUrl;
    private List<String> mThumbnailUrl;

    //Constructor
    public Recipe (String recipeName,
                   List<String> ingredients,
                   List<String> briefStepDescription,
                   List<String> detailedStepDescription,
                   List<String> videoUrl,
                   List<String> thumbnailUrl) {

        //Initialize member variables
        mRecipeName = recipeName;
        mIngredients = ingredients;
        mBriefStepDescription = briefStepDescription;
        mDetailedStepDescription = detailedStepDescription;
        mVideoUrl = videoUrl;
        mThumbnailUrl = thumbnailUrl;
    }

    //GETTER METHODS

    //Recipe Name
    public String getRecipeName() {
        return mRecipeName;
    }

    //List of Ingredients
    public List<String> getListOfIngredients() {
        return mIngredients;
    }

    //Short Description of Recipe Step
    public List<String> getBriefStepDescription() {
        return mBriefStepDescription;
    }

    //Detailed Description of Recipe Step
    public List<String> getDetailedStepDescription() {
        return mDetailedStepDescription;
    }

    //Video Url for Recipe Step
    public List<String> getVideoUrl() {
        return mVideoUrl;
    }

    //Thumbnail Url for Recipe Step
    public List<String> getThumbnailUrl() {
        return mThumbnailUrl;
    }

    //PARCEL SETTER METHODS
    //Write to Parcel
    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeString(mRecipeName);
        parcel.writeStringList(mIngredients);
        parcel.writeStringList(mBriefStepDescription);
        parcel.writeStringList(mDetailedStepDescription);
        parcel.writeStringList(mVideoUrl);
        parcel.writeStringList(mThumbnailUrl);
    }

    //Read from Parcel
    public Recipe (Parcel parcel) {
        mRecipeName = parcel.readString();
        mIngredients = parcel.createStringArrayList();
        mBriefStepDescription = parcel.createStringArrayList();
        mDetailedStepDescription = parcel.createStringArrayList();
        mVideoUrl = parcel.createStringArrayList();
        mThumbnailUrl = parcel.createStringArrayList();
    }

    public static final Parcelable.Creator<Recipe> CREATOR =
            new Parcelable.Creator<Recipe>() {

                @Override
                public Recipe createFromParcel(Parcel parcel) {
                    return new Recipe(parcel);
                }

                @Override
                public Recipe[] newArray(int i) {
                    return new Recipe[i];
                }
            };

    @Override
    public int describeContents() {
        return hashCode();
    }
}
