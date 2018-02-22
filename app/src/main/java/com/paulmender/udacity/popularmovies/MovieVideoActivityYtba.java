/*
 * Copyright (C) 2016 The Android Open Source Project
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 *
 * You may obtain a copy of the License at
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.paulmender.udacity.popularmovies;

import com.google.android.youtube.player.YouTubeBaseActivity;
import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubePlayer;
import com.google.android.youtube.player.YouTubePlayerView;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import com.paulmender.udacity.popularmovies.app.BaseMovieActivity;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;
import static com.paulmender.udacity.popularmovies.app.BaseMovieActivity.
        INTENT_EXTRA_KEY_MOVIE_TITLE_STRING;
import static com.paulmender.udacity.popularmovies.app.BaseMovieActivity.
        INTENT_EXTRA_KEY_MOVIE_VIDEO_PARCELABLE;

/**
 * Package: com.paulmender.udacity.popularmovies
 * Project: PopularMovies
 *    Name: MovieVideoActivityYtba
 * Purpose: View a movie's video.
 *  Author: Paul Mender on 20170927.
 * History:
 *  201801127  Note: Error changed to java.lang.IllegalArgumentException:
 *              Service Intent must be explicit: Intent
 *              { act=com.google.android.youtube.api.StandalonePlayerActivity.START (has extras)
 *              Rather than use an activity to display the video, deprecated this approach
 *              in favor of starting the YouTube player from the MovieVideoListFragment.
 * References:
 *  com.examples.youtubeapidemo.PlayerViewDemoActivity
 */
@SuppressLint("Registered")
@Deprecated
public class MovieVideoActivityYtba extends YouTubeBaseActivity implements
    YouTubePlayer.OnInitializedListener {

    private static final String TAG = "PKMI>MovieVideoAct_ytba";

    private static final int RECOVERY_DIALOG_REQUEST = 1;
    private MovieVideoParcelable movieVideo;
    private String movieTitle;

    //region Implements Methods

    @Override
    public void onInitializationFailure(YouTubePlayer.Provider provider,
                        YouTubeInitializationResult result) {

        if (DEBUG) {
            Log.e(TAG, String.format(
                    "onInitializationFailure 72: result: %s", result.name()));
        }

        if (result.isUserRecoverableError()) {
            result.getErrorDialog(this, RECOVERY_DIALOG_REQUEST).show();
        } else {
            String errorMessage =
                    String.format(getString(R.string.error_player),
                    result.toString());
            Toast.makeText(this, errorMessage, Toast.LENGTH_LONG).show();
        }
    }

    @Override
    public void onInitializationSuccess(YouTubePlayer.Provider provider,
                                        YouTubePlayer youTubePlayer, boolean isRestored) {
        if (DEBUG) {
            Log.d(TAG, String.format(
                    "onInitializationSuccess 90: this.movieVideo.key: %s",
                    this.movieVideo.key));
        }
        if (!isRestored) {
            youTubePlayer.cueVideo(this.movieVideo.key);

            if (DEBUG) {
                Log.d(TAG, String.format(
                        "onInitializationSuccess 98: this.movieVideo.key: %s",
                        this.movieVideo.key));
            }
        }
    }

    //endregion Implements Methods

    //region Override Methods

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(BaseMovieActivity.Layout.MOVIE_VIDEO.layoutId);

        this.movieVideo = getIntent().getParcelableExtra(INTENT_EXTRA_KEY_MOVIE_VIDEO_PARCELABLE);
        this.movieTitle = getIntent().getStringExtra(INTENT_EXTRA_KEY_MOVIE_TITLE_STRING);

        setMovieVideoContent();

        startVideo();
    }

    // Reference: com.examples.youtubeapidemo.PlayerViewDemoActivity.java
    @SuppressWarnings("unused")
    protected YouTubePlayer.Provider getYouTubePlayerProvider() {
        return (YouTubePlayerView) findViewById(R.id.ytpv_MovieVideo);
    }

    //endregion Override Methods

    //region private methods

    private void setMovieVideoContent(){

        if (this.movieVideo != null) {

            TextView Title = findViewById(R.id.tv_movie_video_title);
            TextView nameTextView = findViewById(R.id.tv_movie_video_name);

            // Visibility=gone in layout, keep for debug or future extension.
            //TextView siteTextView = findViewById(R.id.tv_movie_video_site);
            //TextView typeTextView = findViewById(R.id.tv_movie_video_type);
            //TextView keyTextView = findViewById(R.id.tv_movie_video_key);
            //TextView iso339TextView = findViewById(R.id.tv_movie_video_iso_639_1);
            //TextView iso3166TextView = findViewById(R.id.tv_movie_video_iso_3166_1);

            try {
                Title.setText(this.movieTitle);
                nameTextView.setText(this.movieVideo.name);
                //siteTextView.setText(this.movieVideo.site);
                //typeTextView.setText(this.movieVideo.type);                //
                //keyTextView.setText(this.movieVideo.key);
                //iso339TextView.setText(this.movieVideo.iso_639_1);
                //iso3166TextView.setText(this.movieVideo.iso_3166_1);

            } catch (NullPointerException npe){
                if (DEBUG) {
                    Log.e(TAG,
                            "setMovieVideoContent 158: ",npe);
                }
            }
        }
    }

    private void startVideo(){
        YouTubePlayerView youTubePlayerView = findViewById(R.id.ytpv_MovieVideo);
        if (youTubePlayerView != null){
            if (DEBUG) {
                Log.d(TAG,
                        "startVideo: next is youTubePlayerView.Initialize");
            }
            try {
                // COMPLETED by deprecation:  1/26/2018 youTubePlayerView.initialize:
                // Service Intent must be explicit: Intent
                youTubePlayerView.initialize(BuildConfig.YOUTUBE_API_KEY, this);
            } catch (Exception e){
                Log.e(TAG, String.format(
                        "startVideo: youTubePlayerView.initialize: %s", e.getMessage()));
                e.printStackTrace();
            }
        }
    }
    //endregion private methods
}
