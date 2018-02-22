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

package com.paulmender.udacity.popularmovies.content;

import android.content.Context;
import android.content.Loader;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.text.Html;
import android.util.Log;

import com.paulmender.android.content.AsyncTaskBundleWrapper;
import com.paulmender.android.content.AsyncTaskLoaderCallbacks;
import com.paulmender.android.content.AsyncTaskLoaderWidgetHelper;
import com.paulmender.udacity.popularmovies.R;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.paulmender.udacity.popularmovies.widget.MovieDbRecyclerAdapter;

import java.util.List;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies.content
 * Project: PopularMovies Stage 2
 *    Name: MovieDbAsyncTaskLoaderCallbacks
 * Purpose: Provides the loader callbacks for MovieDbAsyncTaskLoaderParcelables.
 *  Author: Paul on 20180102.
 */

public final class MovieDbAsyncTaskLoaderCallbacks
        extends AsyncTaskLoaderCallbacks {

    private static final String TAG = "PKMI>MovieDb...Callback"; // 23 character limit

    //region public fields

    public boolean isDataSetChanged = false;

    //endregion

    // Parameter fields
    private MovieDbUtility.MovieQuery movieQuery = null;

    //region Getters and Setters

    // movieQuery
    @SuppressWarnings("WeakerAccess")
    public MovieDbUtility.MovieQuery getMovieQuery() {
        return movieQuery;
    }
    public void setMovieQuery(MovieDbUtility.MovieQuery movieQuery) {
        this.movieQuery = movieQuery;
    }

    //endregion Getters and Setters

    // Constructor
    public MovieDbAsyncTaskLoaderCallbacks(
            @NonNull Context context,
            @Nullable AsyncTaskLoaderWidgetHelper widgetHelper,
            @Nullable AsyncTaskBundleWrapper bundleWrapper,
            @NonNull MovieDbUtility.MovieQuery movieQuery) {
        super(context, widgetHelper, bundleWrapper);

        this.movieQuery = movieQuery;
    }

    //region Override methods
    
    @Override
    public Loader<List<? extends Parcelable>> onCreateLoader(int loaderId, Bundle bundle) {

        setLoaderId(loaderId );

        setLoaderBundle(bundle);

        return new MovieDbAsyncTaskLoaderParcelables(
                this.getContext(),
                getAsyncTaskBundleWrapper(),
                getWidgetHelper(),
                getMovieQuery());
    }

    @Override public void onLoadFinished(
                    Loader<List<? extends Parcelable>> loader,
                    List<? extends Parcelable> data) {
        super.onLoadFinished(loader, data);

        this.isDataSetChanged = false;

        if (hasMovieDbRecyclerAdapter()) {

            MovieDbRecyclerAdapter movieDbRecyclerAdapter = getMovieDbRecyclerAdapter();
            if (movieDbRecyclerAdapter != null) {
                // Set the recycler adapter data
                int selectedMovieId = getAsyncTaskBundleWrapper().getItemId();
                movieDbRecyclerAdapter.setData(data, selectedMovieId);

                this.isDataSetChanged = true;

                CharSequence errorMessage = getErrorMessage();
                if (data == null) {
                    if (errorMessage != null) {
                        showErrorMessage(errorMessage);
                    } else {
                        showErrorMessage();
                    }
                } else if (data.size() > 0) {
                    // Show the data.
                    showRecyclerView();
                } else {
                    showErrorMessage();
                }

                if (DEBUG) {
                    Log.d(TAG, "onLoadFinished 135: movieDbRecyclerAdapter is null");
                }
            }
        } else if (hasTextView()){
            if (data.size() > 0) {
                setTextView(data.get(0));
            } else {
                setTextView(null);
            }
            showTextView();
        }
    }

    //endregion Override methods

    //region private methods

    private CharSequence getErrorMessage(){
        Resources r = getContext().getResources();
        String s = null;
        CharSequence errorMessage = null;
        switch (getMovieQuery()){
            case FAVORITES:
                s = String.format(r.getString(R.string.error_message_no_favorites),
                        r.getString(R.string.movie_detail_title));
                break;
            case POPULAR:
                s = r.getString(R.string.error_message_no_query_results);
                break;
            case TOP_RATED:
                s = r.getString(R.string.error_message_no_query_results);
                break;
        }
        if (s != null){
            errorMessage = Html.fromHtml(s);
        }
        return errorMessage;
    }

    private MovieDbRecyclerAdapter getMovieDbRecyclerAdapter(){
        if (hasWidgetHelper() && getWidgetHelper().hasRecyclerView()) {
            return (MovieDbRecyclerAdapter) getWidgetHelper().getRecyclerView().getAdapter();
        } else {
            return null;
        }
    }

    private boolean hasMovieDbRecyclerAdapter(){
        return (getMovieDbRecyclerAdapter() != null);
    }

    private <P extends Parcelable> void setTextView(P extendsParcelable) {

        if (hasWidgetHelper() && getWidgetHelper().hasTextView()){

            // If there is a tag ID specified...
            if (getWidgetHelper().hasTextViewTagId()) {
                // ... set the text view tag.
                getWidgetHelper().setTextViewTag(extendsParcelable);
            }

            getWidgetHelper().setTextViewText(extendsParcelable);
        }
    }

    //endregion private methods
}
