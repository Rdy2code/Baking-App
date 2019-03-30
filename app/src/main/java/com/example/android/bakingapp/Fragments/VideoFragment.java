package com.example.android.bakingapp.Fragments;

import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.android.bakingapp.DetailsActivity;
import com.example.android.bakingapp.R;
import com.example.android.bakingapp.RecipeActivity;
import com.google.android.exoplayer2.DefaultLoadControl;
import com.google.android.exoplayer2.ExoPlayerFactory;
import com.google.android.exoplayer2.LoadControl;
import com.google.android.exoplayer2.SimpleExoPlayer;
import com.google.android.exoplayer2.extractor.DefaultExtractorsFactory;
import com.google.android.exoplayer2.source.ExtractorMediaSource;
import com.google.android.exoplayer2.source.MediaSource;
import com.google.android.exoplayer2.trackselection.DefaultTrackSelector;
import com.google.android.exoplayer2.trackselection.TrackSelector;
import com.google.android.exoplayer2.ui.SimpleExoPlayerView;
import com.google.android.exoplayer2.upstream.DefaultDataSourceFactory;
import com.google.android.exoplayer2.util.Util;

import butterknife.BindView;
import butterknife.ButterKnife;

//TODO: Add interface to save player position on upnavigation to MainActivity

public class VideoFragment extends Fragment {

    //String constants
    private final String LOG_TAG = VideoFragment.class.getSimpleName();
    public static final String PLAYER_POSITION = "player_position";
    private static final String IS_READY = "is_player_ready";
    private static final String URL = "url";

    private SimpleExoPlayer mExoPlayer;
    private long mPlayerPosition;
    private boolean mIsPlayerReady;
    @BindView (R.id.exoplayer_view) SimpleExoPlayerView mPlayerView;
    @BindView(R.id.no_media_textview) TextView mNoMedia;

    private boolean mTwoPane;
    private String url = "";
    private String videoUrl;
    private String thumbnailUrl;

    //Constructor
    public VideoFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater,ViewGroup container,Bundle savedInstanceState) {

        if (savedInstanceState != null) {
            mPlayerPosition = savedInstanceState.getLong(PLAYER_POSITION);
            mIsPlayerReady = savedInstanceState.getBoolean(IS_READY);
            url = savedInstanceState.getString("url");
        }

        //Inflate the Video View fragment
        View rootView = inflater.inflate(R.layout.fragment_video, container, false);

        //Implement ButterKnife Library to get references to views
        //https://jakewharton.github.io/butterknife/
        ButterKnife.bind(this, rootView);

        if (savedInstanceState == null) {

            if (mTwoPane) {
                videoUrl = getArguments().getString(RecipeActivity.STEP_VIDEO);
                thumbnailUrl = getArguments().getString(RecipeActivity.STEP_THUMBNAIL);
            } else {
                //Video urls that may or may not be present in the JSON
                videoUrl = getArguments().getString(DetailsActivity.VIDEO);
                thumbnailUrl = getArguments().getString(DetailsActivity.THUMBNAIL);
            }

            if (videoUrl != null) {
                url = videoUrl;
            } else if (thumbnailUrl != null) {
                url = thumbnailUrl;
            }
        }

        if (!url.isEmpty()) {
            initializePlayer(Uri.parse(url));
        } else {
            setPlayerView();
        }

        return rootView;
    }

    private void setPlayerView () {
        mPlayerView.setVisibility(View.INVISIBLE);
        mNoMedia.setVisibility(View.VISIBLE);
        mNoMedia.setText(getString(R.string.no_media_available));
    }

    private void initializePlayer(Uri videoUri) {
        if (mExoPlayer == null) {
            //Default trackselector and loadcontrol
            TrackSelector trackSelector = new DefaultTrackSelector();
            LoadControl loadControl = new DefaultLoadControl();

            //Create a SimpleExoPlayer instance
            mExoPlayer = ExoPlayerFactory.newSimpleInstance(this.getActivity(), trackSelector, loadControl);

            //Connect the SimpleExoPlayerView to the SimpleExoplayer instance
            mPlayerView.setPlayer(mExoPlayer);

            //Prepare MediaSource
            String userAgent = Util.getUserAgent(this.getActivity(), "BakingApp");
            MediaSource mediaSource = new ExtractorMediaSource(
                    videoUri,
                    new DefaultDataSourceFactory(this.getActivity(), userAgent),
                    new DefaultExtractorsFactory(),
                    null,
                    null);

            if (mPlayerPosition != 0) {
                mExoPlayer.seekTo(mPlayerPosition);
            }

            mExoPlayer.prepare(mediaSource);
            mExoPlayer.setPlayWhenReady(mIsPlayerReady);
        }
    }

    //Check API version before releasing player
    //https://developer.android.com/reference/android/app/Activity
    @Override
    public void onPause() {
        super.onPause();
        if (mExoPlayer != null && Build.VERSION.SDK_INT < Build.VERSION_CODES.P) {
            Log.d(LOG_TAG, "onPause called");
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            mIsPlayerReady = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mExoPlayer != null && Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            Log.d(LOG_TAG, "onStop called");
            mPlayerPosition = mExoPlayer.getCurrentPosition();
            mIsPlayerReady = mExoPlayer.getPlayWhenReady();
            releasePlayer();
        }
    }

    /**
     * Release ExoPlayer
     */
    private void releasePlayer() {
        mExoPlayer.stop();
        mExoPlayer.release();
        mExoPlayer = null;
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putLong(PLAYER_POSITION, mPlayerPosition);
        outState.putBoolean(IS_READY, mIsPlayerReady);
        outState.putString(URL, url);
    }

    public void setTwoPane(boolean screenMode) {
        mTwoPane = screenMode;
    }
}
