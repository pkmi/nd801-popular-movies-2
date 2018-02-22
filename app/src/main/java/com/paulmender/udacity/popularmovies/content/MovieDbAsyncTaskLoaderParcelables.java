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

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.OperationCanceledException;
import android.os.Parcelable;
import android.util.Log;

import com.google.gson.Gson;
import com.paulmender.android.content.AsyncTaskBundleWrapper;
import com.paulmender.android.content.AsyncTaskLoaderParcelables;
import com.paulmender.android.content.AsyncTaskLoaderWidgetHelper;
import com.paulmender.udacity.popularmovies.MovieParcelable;
import com.paulmender.udacity.popularmovies.data.MovieDbContract;
import com.paulmender.udacity.popularmovies.utility.MovieDbJsonUtility;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;

import org.json.JSONException;

import java.io.IOException;
import java.net.URL;
import java.security.InvalidParameterException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies.content
 * Project: PopularMovies Stage 2
 *    Name: MovieDbAsyncTaskLoaderParcelables
 * Purpose:
 *  Author: Paul on 20171225.
 * History:
 *  20171225 Refactor: Replace FetchMovieDbDataTask with
 *           AsyncTaskLoaderParcelables parent class.
 *  20180123 Refactor (implement Favorites):
 *           1. Change URL this.queryUrl to URL[] this.QueryUrls
 *           to implement multiple queries by MovieQuery.
 *           2. Change String this.queryResults to String[] this.QueryResults
 *           to implement multiple queries by MovieQuery.
 */

public final class MovieDbAsyncTaskLoaderParcelables
                        extends AsyncTaskLoaderParcelables{

    private static final String TAG = "PKMI>MovieDbAsyncTas...";

    //region Non-public, Non-static fields

    private int movieId = MovieDbJsonUtility.MOVIE_ID_NOT_SET;

    private MovieDbUtility.MovieQuery movieQuery = null;

    private String[] queryResults = null;

    private URL[] queryUrls = null;

    private List<? extends Parcelable> taskResult;

    //endregion Non-public, non-static fields

    //region Getters and Setters

    private MovieDbUtility.MovieQuery getMovieQuery() {
        return movieQuery;
    }

    //endregion

    //endregion

    // Constructor
    @SuppressWarnings("WeakerAccess")
    public MovieDbAsyncTaskLoaderParcelables(
            Context context,
            AsyncTaskBundleWrapper bundleWrapper,
            AsyncTaskLoaderWidgetHelper widgetHelper,
            MovieDbUtility.MovieQuery movieQuery) {
        super(context, bundleWrapper, widgetHelper);

        if(hasBundleWrapper()){
            this.movieId = this.getAsyncTaskBundleWrapper().getItemId();
        }

        this.movieQuery = movieQuery;
    }

    //region Override methods

    @Override public List<? extends Parcelable> loadInBackground() {

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "loadInBackground 110: *START* Loader ID %d", this.getId()));
            try {
                Log.d(TAG, String.format(
                        "loadInBackground 113: MovieQuery.%s", movieQuery.name()));
            } catch (NullPointerException npe) {
                Log.e(TAG, "loadInBackground 115: movieQuery is null", npe);
            }
        }

        // Initialize the taskResult.
        this.taskResult = null;

        if (! isMovieFavoriteQuery()) {
            // Set the query URL according to the requested movie query.
            try {
                setQueryUrls();
            } catch (NullPointerException npe) {
                if (DEBUG) {
                    Log.e(TAG, "loadInBackground 128: setQueryUrl()", npe);
                }
            }

            // Get the query result from the network connection.
            if (this.queryUrls != null && !isLoadInBackgroundCanceled()) {
                try {
                    this.queryResults = new String[this.queryUrls.length];
                    for (int i = 0; i < queryUrls.length; i++) {
                        this.queryResults[i] =
                                MovieDbUtility.getResponseFromHttpUrl(this.queryUrls[i]);
                    }

                } catch (NullPointerException | IOException e) {
                    if (DEBUG) {
                        Log.e(TAG,
                                "loadInBackground 142: MovieDbUtility.getResponseFromHttpUrl", e);
                    }
                }
            }
        } // not movie favorite query.

        if (isMovieFavoriteQuery() ||
                (this.queryResults != null && !isLoadInBackgroundCanceled())) {

            // Set the result according to the MovieQuery.
            setTaskResult();

        } else {
            if (!isMovieFavoriteQuery()) {
                if (DEBUG) {
                    Log.d(TAG, "loadInBackground 160: queryResults == null");
                }
            }
        }

        if (DEBUG) {
            if (this.taskResult != null) {
                Log.d(TAG, String.format(
                        "loadInBackground 170: this.taskResult.size() = %d",
                        this.taskResult.size()));
            } else {
                Log.d(TAG, "loadInBackground 173: this.taskResult is null.");
            }
            Log.d(TAG, String.format(
                    "loadInBackground 175: *RETURN* Loader ID %d", this.getId()));
        }

        return this.taskResult;
    }

    //endregion Override methods

    //region Private Methods

    // Set the query urls according to the specified MovieQuery.
    private void setQueryUrls(){

        this.queryUrls = new URL[1];

        MovieDbUtility.MovieQuery movieQuery = this.movieQuery;
        boolean movieIdIsExpected = false;
        try {
            switch (movieQuery) {
                case POPULAR:
                    this.queryUrls[0] = MovieDbUtility.getUrl(movieQuery);
                    break;

                case TOP_RATED:
                    this.queryUrls[0] = MovieDbUtility.getUrl(movieQuery);
                    break;

                case MOVIE_DETAIL:
                    movieIdIsExpected = true;
                    if (this.movieId > 0) {
                        this.queryUrls[0] = MovieDbUtility.getUrl(this.movieId, movieQuery);
                    }
                    break;

                case MOVIE_REVIEW_LIST:
                    movieIdIsExpected = true;
                    if (this.movieId > 0) {
                        this.queryUrls[0] = MovieDbUtility.getUrl(this.movieId, movieQuery);
                    }
                    break;

                case MOVIE_VIDEO_LIST:
                    movieIdIsExpected = true;
                    if (this.movieId > 0) {
                        this.queryUrls[0] = MovieDbUtility.getUrl(this.movieId, movieQuery);
                    }
                    break;

                default:
                    throw new InvalidParameterException();
            }
        } catch (InvalidParameterException ipe) {
            Log.e(TAG,
                    String.format("setQueryUrl 227: Loader ID %d: MovieQuery.%s is not supported.",
                    getId(), this.movieQuery.name()), ipe);
        }

        if (movieIdIsExpected && (this.movieId == 0 ||
                this.movieId == MovieDbJsonUtility.MOVIE_ID_NOT_SET)) {
            try{
                throw new OperationCanceledException(
                    String.format(Locale.US,
                    "this.movieId from this.bundleWrapper.getItemId() expected > 0," +
                            " actual = %d",this.movieId));

            } catch (OperationCanceledException oce){
                if (DEBUG) {
                    Log.e(TAG, String.format("setQueryUrl 241: Loader ID %d",getId()),oce);
                }
            }
        }

        if (this.queryUrls == null) {

            try{
                throw new OperationCanceledException("queryUrls is null");
            } catch (OperationCanceledException oce){
                if (DEBUG) {
                    Log.e(TAG, String.format("setQueryUrl 252: Loader ID %d", getId()),oce);
                }
            }
        }
    }

    // Set the result according to the specified MovieQuery (movieQuery).
    private void setTaskResult(){

        this.taskResult = null;

        try {
            switch (movieQuery) {

                case FAVORITES:
                    setTaskResultFavorites();
                    break;
                    
                case POPULAR:
                    setTaskResultMovies();
                    break;

                case TOP_RATED:
                    setTaskResultMovies();
                    break;

                case MOVIE_REVIEW_LIST:
                    setTaskResultMovieReviews();
                    break;

                case MOVIE_DETAIL:
                    setTaskResultMovieDetail();
                break;

                case MOVIE_VIDEO_LIST:
                    setTaskResultMovieVideos();
                    break;

                default:
                    throw new InvalidParameterException();

            }
        } catch (InvalidParameterException ipe){
            Log.w(TAG, String.format("setTaskResult 295: MovieQuery.%s is not supported."
                    , movieQuery.name()),ipe );
        }

        if (this.taskResult == null) {
            if (DEBUG) {
                Log.d(TAG, String.format("setTaskResult 301: this.taskResult is null: %s",
                        movieQuery.name()));
            }
        }
    }

    private void setTaskResultFavorites(){

        ContentResolver resolver = getContext().getContentResolver();
        Cursor favoritesCursor = resolver.query(MovieDbContract.MovieFavoriteTable.CONTENT_URI,
                MovieDbContract.MovieFavoriteTable.PROJECTION_ALL,
                null,
                null,
                null);
        if (favoritesCursor != null) {

            if (favoritesCursor.moveToFirst()){
                Gson gson = new Gson();

                MovieParcelable movieParcelable;

                List<MovieParcelable> parsedMovies = new ArrayList<>();

                int favoritesCount = favoritesCursor.getCount();
                int favoritesJsonColumnIndex = favoritesCursor.getColumnIndex(
                        MovieDbContract.MovieFavoriteTable.MOVIE_JSON);

                try {
                    for (int i = 0; i < favoritesCount; i++) {
                        String json = favoritesCursor.getString(favoritesJsonColumnIndex);
                        movieParcelable = gson.fromJson(json, MovieParcelable.class);
                        parsedMovies.add(movieParcelable);
                        if (!favoritesCursor.moveToNext()){
                            break;
                        }
                    }

                    this.taskResult = parsedMovies;
                    // Fix StrictMode:
                    // A resource was acquired at attached stack trace but never released.
                    favoritesCursor.close();

                } catch (Exception e) {
                    if (DEBUG) {
                        Log.d(TAG, "setTaskResultFavorites 345: ", e);
                    }
                } finally {
                    // Fix StrictMode:
                    // A resource was acquired at attached stack trace but never released.
                    favoritesCursor.close();
                }
            } else {
                favoritesCursor.close();
            }
        }
    }

    private void setTaskResultMovieDetail() {
        try {
            this.taskResult = MovieDbJsonUtility.getMovieParcelableList(queryResults[0]);
        } catch (Exception e) {
            if (DEBUG) {
                Log.d(TAG, "setTaskResultMovies 364: ",e);
            }
        }
    }

    private void setTaskResultMovies() {
        try {
            this.taskResult = MovieDbJsonUtility.getMovieDbStringsFromJson(queryResults[0]);
        } catch (JSONException | NullPointerException e) {
            if (DEBUG) {
                Log.d(TAG, "setTaskResultMovies 378: ",e);
            }
        }
    }

    private void setTaskResultMovieReviews() {
        try {
            this.taskResult = MovieDbJsonUtility.
                    getMovieDbReviewStringsFromJson(queryResults[0],this.movieId);
        } catch (JSONException | NullPointerException e) {
            if (DEBUG) {
                Log.d(TAG, "setTaskResultMovieReviews 384:",e);
            }
        }
    }

    private void setTaskResultMovieVideos() {
        try {
            this.taskResult = MovieDbJsonUtility.
                    getMovieDbVideoStringsFromJson(queryResults[0],this.movieId);
        } catch (JSONException | NullPointerException e) {
            if (DEBUG) {
                Log.d(TAG, "setTaskResultMovieVideos 395:",e);
            }
        }
    }

    private boolean isMovieFavoriteQuery(){
        return (getMovieQuery() == MovieDbUtility.MovieQuery.FAVORITES);
    }

    //endregion Private Methods
}
