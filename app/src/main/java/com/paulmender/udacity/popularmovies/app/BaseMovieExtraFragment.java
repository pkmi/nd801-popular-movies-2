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

import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

import com.paulmender.android.app.fragment.extensions.FragmentExtension;
import com.paulmender.android.content.AsyncTaskLoaderCallbacks;
import com.paulmender.udacity.popularmovies.content.*;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.paulmender.udacity.popularmovies.widget.MovieDbRecyclerAdapter;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies.app
 * Project: PopularMovies Stage 2
 *    Name: BaseMovieExtraFragment
 * Purpose: Provides a common methods and fields to subclasses.
 *  Author: Paul on 20171120.
 */
public abstract class BaseMovieExtraFragment<LC extends AsyncTaskLoaderCallbacks> extends
                            FragmentExtension<MovieDbAsyncTaskLoaderCallbacks> {

    private static final String TAG = "PKMI>BaseMovieExtraF...";

    //region public static final

    public static final int LOADER_ID_MOVIE_REVIEW_LIST_FRAGMENT = 123117;
    public static final int LOADER_ID_MOVIE_VIDEO_LIST_FRAGMENT = 10118;

    //endregion public static final

    //region Non-public, non-static fields

    private MovieDbUtility.MovieQuery movieQuery;

    private RecyclerView movieDbRecyclerView;

    private int movieId;

    private String movieTitle;

    // The view type to use when the fragment is created.
    private MovieDbRecyclerAdapter.MovieDbViewType movieDbViewType;

    //endregion Non-public, non-static fields

    //region Getters and Setters

    // movieDbRecyclerView
    protected RecyclerView getMovieDbRecyclerView() {
        return this.movieDbRecyclerView;
    }
    private void setMovieDbRecyclerView(RecyclerView movieDbRecyclerView) {
        this.movieDbRecyclerView = movieDbRecyclerView;
    }

    // MovieDbRecyclerAdapter.MovieDbViewType
    @SuppressWarnings("unused")
    protected MovieDbRecyclerAdapter.MovieDbViewType getMovieDbViewType() {
        try {
            if (this.movieDbViewType == null) {
                throw new IllegalStateException(
                    "Hint: Call setMovieDbViewType(MovieDbViewType) before getMovieDbViewType()");
            }
        } catch (IllegalStateException ise){
            Log.e(TAG, "getViewType 84: ",ise);
        }

        return this.movieDbViewType;
    }

    @CallSuper
    protected void setMovieDbViewType(@NonNull MovieDbRecyclerAdapter.MovieDbViewType movieDbViewType){
        this.movieDbViewType = movieDbViewType;
    }

    // movieId
    public int getMovieId() {
        return this.movieId;
    }
    public void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    // movieQuery
    protected MovieDbUtility.MovieQuery getMovieQuery() {
        return movieQuery;
    }
    protected void setMovieQuery(MovieDbUtility.MovieQuery movieQuery) {
        this.movieQuery = movieQuery;
    }

    // this.movieTitle
    protected String getMovieTitle() {
        return this.movieTitle;
    }
    public void setMovieTitle(String movieTitle) {
        this.movieTitle = movieTitle;
    }

    //endregion Getters and Setters

    //region Override methods

    /**
     * Initialize the RecyclerView Adapter and other properties.
     */
    @SuppressWarnings("unused")
    protected void initMovieDbRecyclerView() {
        // Subclass implementation.
    }
    //endregion

    //region protected methods

    protected void setMovieDbRecyclerView(
            RecyclerView recyclerView, MovieDbRecyclerAdapter recyclerAdapter){

        setMovieDbRecyclerView(recyclerView);

        try {
            this.movieDbRecyclerView.setAdapter(recyclerAdapter);

            this.movieDbRecyclerView.setItemAnimator(new DefaultItemAnimator());
        } catch (NullPointerException npe){
            if (DEBUG) {
                Log.d(TAG,"setMovieDbRecyclerView 144: ",npe);
            }
        }
    }

    //endregion protected methods

}
