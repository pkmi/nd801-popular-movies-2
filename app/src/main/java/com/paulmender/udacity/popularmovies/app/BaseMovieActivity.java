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

package com.paulmender.udacity.popularmovies.app;

import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.paulmender.android.support.v7.app.appcompatactivity.extensions.ActivityExtension;
import com.paulmender.udacity.popularmovies.MovieParcelable;
import com.paulmender.udacity.popularmovies.R;
import com.paulmender.udacity.popularmovies.content.MovieDbAsyncTaskLoaderCallbacks;
import com.paulmender.udacity.popularmovies.data.MovieDbContract;
import com.paulmender.udacity.popularmovies.utility.MovieDbJsonUtility;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.paulmender.udacity.popularmovies.widget.MovieDbRecyclerAdapter;

import java.security.InvalidParameterException;
import java.util.List;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies.app
 * Project: PopularMovies Stage 2
 *    Name: BaseMovieActivity
 * Purpose: Extend ActivityExtension to include base features for the movie application activities.
 *  Author: Paul on 20171208.
 */
@SuppressWarnings("unused")
public class BaseMovieActivity<T extends Parcelable, L extends List<T>> extends
        ActivityExtension<T, L, MovieDbAsyncTaskLoaderCallbacks> {

    private static final String TAG = "PKMI>BaseMovieActivity";

    //region public static final

    // Declare movie app intent extra keys.
    protected static final String INTENT_EXTRA_KEY_MOVIE_DETAIL_PARCELABLE = "movieDetail";
    public static final String INTENT_EXTRA_KEY_MOVIE_REVIEW_PARCELABLE = "movieReview";
    public static final String INTENT_EXTRA_KEY_MOVIE_TITLE_STRING = "movieDetail";
    public static final String INTENT_EXTRA_KEY_MOVIE_VIDEO_PARCELABLE = "movieVideo";

    protected static final int LOADER_ID_MOVIE_DETAIL_ACTIVITY = 1224;
    protected static final int LOADER_ID_MOVIE_MAIN_ACTIVITY = 1225;

    public static final int MOVIE_ID_NOT_SET = MovieDbContract.ID_NOT_SET;

    // Declare movie app preference keys.
    public static final String PREFERENCE_KEY_MOVIE_ID = "MovieId";
    protected static final String PREFERENCE_KEY_MOVIE_QUERY = "MovieQuery";
    protected static final String PREFERENCE_KEY_MOVIE_DETAIL = "MovieDetail";

    // TODO LOW: 12/21/2017 May use these to implement returning to last selected review/video.
    public static final String PREFERENCE_KEY_MOVIE_REVIEW_ID = "MovieReviewId";
    public static final String PREFERENCE_KEY_MOVIE_REVIEW_LIST_INDEX = "MovieReviewListIndex";

    public static final String PREFERENCE_KEY_MOVIE_VIDEO_ID = "movieVideoId";
    public static final String PREFERENCE_KEY_MOVIE_VIDEO_LIST_INDEX = "MovieVideoListIndex";

    //end region public static final

    //region Non-public, non-static fields

    private RecyclerView movieDbRecyclerView;

    @SuppressWarnings("WeakerAccess")
    protected int movieId = MOVIE_ID_NOT_SET;

    protected MovieParcelable movie = null;

    private MovieDbUtility.MovieQuery movieQuery;

    //endregion Non-public, non-static fields

    //region Getters and Setters

    // movieDbRecyclerView
    protected RecyclerView getMovieDbRecyclerView() {
        return movieDbRecyclerView;
    }
    private void setMovieDbRecyclerView(RecyclerView movieDbRecyclerView) {
        this.movieDbRecyclerView = movieDbRecyclerView;
    }

    // movieId
    public int getMovieId() {
        return this.movieId;
    }
    protected void setMovieId(int movieId) {
        this.movieId = movieId;
    }
    protected void setMovieId(String movieId){
        if (movieId != null){
            try{
                this.movieId = Integer.valueOf(movieId);
            } catch (InvalidParameterException ipe){
                Log.e(TAG, String.format("setMovieId: invalid movieId <%s>",movieId),ipe);
            }
        } else {
            this.movieId = MOVIE_ID_NOT_SET;
        }
    }

    public String getMovieIdString(){
        return String.valueOf(getMovieId());
    }

    protected String getMovieJson(){

        String json = null;

        if (this.movie != null){
            json = MovieDbJsonUtility.getMovieJson(this.movie);
        }

        return json;
    }

    protected void setMovieFromJson(@NonNull String json){
        this.movie = MovieDbJsonUtility.getMovieFromJson(json);
    }

    // movieQuery
    protected MovieDbUtility.MovieQuery getMovieQuery(){
        return this.movieQuery;
    }
   @SuppressWarnings("WeakerAccess")
   protected void setMovieQuery(MovieDbUtility.MovieQuery movieQuery) {
        this.movieQuery = movieQuery;
    }
    public void setMovieQuery(String movieQueryName){
        this.movieQuery = MovieDbUtility.MovieQuery.getEnumerator(movieQueryName);
    }

    //endregion Getters and Setters

    //region enum

    /**
     * Purpose: Maps the associated R.layout to the Activity
     *          to help manage activity layouts.
     * Parameters:
     *  int Layout
     * Note: "Derived" from on ContentViewLayoutEnumerator.
     */
    public enum Layout {
        MOVIE_DETAIL(R.layout.activity_movie_detail),
        MAIN(R.layout.activity_main),
        MOVIE_REVIEW(R.layout.activity_movie_review),
        MOVIE_VIDEO(R.layout.activity_movie_video),
        UNKNOWN(ActivityExtension.LAYOUT_ID_NOT_SET);

        public int layoutId;

        // Constructor
        Layout(int layoutId) {
            this.layoutId = layoutId;
        }
    }

    //endregion enum

    //region Override methods

    //endregion Override methods

    //region protected methods

    protected void setMovieDbRecyclerView(
            RecyclerView recyclerView, MovieDbRecyclerAdapter recyclerAdapter){

        setMovieDbRecyclerView(recyclerView);

        try {
            this.movieDbRecyclerView.setAdapter(recyclerAdapter);

            this.movieDbRecyclerView.setItemAnimator(new DefaultItemAnimator());
        } catch (NullPointerException npe){
            if (DEBUG) {
                Log.d(TAG,"setMovieDbRecyclerView 196: ",npe);
            }
        }
    }

    /**
     * Initialize the RecyclerView Adapter and its properties.
     */
    protected void initMovieDbRecyclerView() {
        // Subclass implementation.
    }

    /**
     * Update the current movie query value if necessary.
     * @param movieQuery The MoveQuery enumeration.
     * @return true if reset; otherwise false
     */
    protected boolean isMovieQueryReset(MovieDbUtility.MovieQuery movieQuery) {
        boolean returnIsReset = false;
        // If the movie query changed...
        if (movieQuery != null) {
            if (movieQuery != this.movieQuery) {
                // ... update the current movie query value.
                setMovieQuery(movieQuery);

                // ... and indicate it was reset.
                returnIsReset = true;
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "isMovieQueryReset 226: Invalid parameter: movieQuery is null");
            }
        }
        return returnIsReset;
    }

    /**
     * Update the current selected tab if necessary.
     * @param requestedTab The tab being requested.
     * @return true if reset; otherwise false
     */
    protected boolean isTabReset(int requestedTab) {
        boolean returnIsReset = false;

        // If the selected tab is being changed...
        if (requestedTab != getSelectedTabPosition()) {
            // ... update the currently selected tab.
            setTabSelect(requestedTab);

            // ... and indicate it was reset.
            returnIsReset = true;
        }

        return returnIsReset;
    }

    //endregion protected methods
}
