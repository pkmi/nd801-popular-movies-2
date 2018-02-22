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

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.paulmender.android.support.v7.app.appcompatactivity.extensions.ActivityExtension;
import com.paulmender.udacity.popularmovies.app.BaseMovieActivity;

import java.util.List;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;
import static com.paulmender.udacity.popularmovies.app.
        BaseMovieActivity.INTENT_EXTRA_KEY_MOVIE_VIDEO_PARCELABLE;
import static com.paulmender.udacity.popularmovies.app.
        BaseMovieActivity.INTENT_EXTRA_KEY_MOVIE_TITLE_STRING;

/**
 * Package: com.paulmender.udacity.popularmovies
 * Project: PopularMovies Stage 2
 *    Name: MovieVideoActivity
 * Purpose:
 *  Author: Paul on 20170918.
 * History:
 *      20180126 Refactor to correct:
 *                  Service com.google.android.youtube.api.service.YouTubeService
 *                  has leaked IntentReceiver ubi@1fa3bfce that was originally registered here.
 *                  Are you missing a call to unregisterReceiver()?
 *              Note: Error changed to java.lang.IllegalArgumentException:
 *              Service Intent must be explicit: Intent
 *              { act=com.google.android.youtube.api.StandalonePlayerActivity.START (has extras)
 *              Rather than use an activity to display the video, deprecated this approach
 *              in favor of starting the YouTube player from the MovieVideoListFragment.
 * References:
 *  1. com.examples.youtubeapidemo.PlayerViewDemoActivity
 */
@Deprecated
@SuppressLint({"Registered"})
public class MovieVideoActivity extends
        ActivityExtension {

    private static final String TAG = "PKMI>MovieVideoActivity";

    private MovieVideoParcelable movieVideo;
    private String movieTitle;

    // YouTube constants
    private static final int resolveServiceMissing = 2;

    // The time, in milliseconds, where playback should start in the first video played.
    private static final int videoStartTimeMillis = 0;

    // true to have the video start playback as soon as the standalone player loads,
    // false to cue the video.
    private static final boolean videoAutoplay = true;

    // true to have the video play in a dialog view above your current Activity,
    // false to have the video play fullscreen.
    private static final boolean videoLightboxMode = true;

    //region implements methods

    // ActivityExtensions.Extensions
    @Override
    public void onCreateSetContent() {
        setMovieVideoContent();
    }

    //endregion implements methods

    //region Override methods

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.movieVideo = getIntent().getParcelableExtra(INTENT_EXTRA_KEY_MOVIE_VIDEO_PARCELABLE);
        this.movieTitle = getIntent().getStringExtra(INTENT_EXTRA_KEY_MOVIE_TITLE_STRING);

        setContentView(BaseMovieActivity.Layout.MOVIE_VIDEO.layoutId);

        setMovieVideoContent();

        startVideo();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override public void onStop(){
        unRegisterCreateVideoReceiver();
        super.onStop();
    }
    //endregion Override methods

    //region Private Methods
    private void setMovieVideoContent(){

        if (this.movieVideo != null) {

            TextView Title = findViewById(R.id.tv_movie_video_title);


            TextView nameTextView = findViewById(R.id.tv_movie_video_name);

            // Visibility=gone in layout, keep for debug or future extension.
            TextView siteTextView = findViewById(R.id.tv_movie_video_site);
            TextView typeTextView = findViewById(R.id.tv_movie_video_type);
            TextView keyTextView = findViewById(R.id.tv_movie_video_key);
            TextView iso339TextView = findViewById(R.id.tv_movie_video_iso_639_1);
            TextView iso3166TextView = findViewById(R.id.tv_movie_video_iso_3166_1);

            if (DEBUG) {
                Log.d(TAG, String.format(
                        "setMovieVideoContent 144: this.movieVideo.site: %s",
                        this.movieVideo.site));
                Log.d(TAG, String.format(
                        "setMovieVideoContent 147: this.movieVideo.key: %s",
                        this.movieVideo.key));
            }
            try {
                Title.setText(this.movieTitle);

                typeTextView.setText(this.movieVideo.type);
                nameTextView.setText(this.movieVideo.name);
                siteTextView.setText(this.movieVideo.site);

                keyTextView.setText(this.movieVideo.key);
                iso339TextView.setText(this.movieVideo.iso_639_1);
                iso3166TextView.setText(this.movieVideo.iso_3166_1);

            } catch (NullPointerException npe){
                if (DEBUG) {
                    Log.e(TAG,
                            "setMovieVideoContent 164: ",npe);
                }
            }
        }
    }

    private boolean canResolveStartVideoIntent(Intent intent) {
        List<ResolveInfo> resolveInfo =
                getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private void startVideo(){

        Intent startVideoIntent = YouTubeStandalonePlayer.createVideoIntent(
                this,
                BuildConfig.YOUTUBE_API_KEY,
                movieVideo.key,
                videoStartTimeMillis,
                videoAutoplay,
                videoLightboxMode);

        if (startVideoIntent != null) {

            if (canResolveStartVideoIntent(startVideoIntent)) {
                startActivity(startVideoIntent);
            } else {
                // Could not resolve the intent -
                // must need to install or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING.
                        getErrorDialog(this,resolveServiceMissing).show();
            }
        }
    }

    @SuppressWarnings("EmptyMethod")
    private void unRegisterCreateVideoReceiver() {
        // COMPLETED by deprecation: 1/20/2018  Activity com.google.android.youtube.api.StandalonePlayerActivity
        // has leaked IntentReceiver aahs@317346a
    }

    //endregion private methods
}
