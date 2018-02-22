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

package com.paulmender.udacity.popularmovies.app.service;

import android.app.IntentService;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

import com.paulmender.udacity.popularmovies.data.MovieDbContract;
import com.paulmender.udacity.popularmovies.data.MovieDbContract.MovieFavoriteTable;

import java.security.InvalidParameterException;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;
import static com.paulmender.udacity.popularmovies.data.MovieDbContract.ID_NOT_SET;

/**
 * Package: com.paulmender.udacity.popularmovies.app.service
 * Project: PopularMovies Stage 2
 *    Name: MovieFavoriteIntentService
 * Purpose: Handle intents asynchronously for the Movie Favorites database operations.
 *  Author: Paul on 20180117.
 *  References:
 *  1. https://www.grokkingandroid.com/android-tutorial-writing-your-own-content-provider.
 */

@SuppressWarnings("WeakerAccess")
public class MovieFavoriteIntentService extends IntentService {

    private static final String TAG = "PKMI>MovieFavIntServ...";

    public static final String ACTION_ADD_FAVORITE = "actionAddFavorite";
    public static final String ACTION_REMOVE_FAVORITE = "actionRemoveFavorite";

    public static final String INTENT_EXTRA_RESULT_KEY = "intentExtraResultKey";
    public static final int INTENT_EXTRA_RESULT_SUCCESS = 1;
    public static final int INTENT_EXTRA_RESULT_FAILED = -1;
    public static final String INTENT_EXTRA_RESULT_KEY_URI = "intentExtraResultKeyUri";

    // Constructor
    public MovieFavoriteIntentService(){
        super(MovieFavoriteIntentService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent == null){
            if (DEBUG) {
                Log.d(TAG, "onHandleIntent 65: intent parameter is null");
            }
            return;
        }

        Uri uri;

        final String action = intent.getAction();
        if (action != null){
            switch (action){
            case ACTION_ADD_FAVORITE:
                uri = onActionAddFavorite(intent);
                intent.putExtra(INTENT_EXTRA_RESULT_KEY_URI,uri);
                intent.putExtra(INTENT_EXTRA_RESULT_KEY,
                        uri != null ? INTENT_EXTRA_RESULT_SUCCESS : INTENT_EXTRA_RESULT_FAILED);

                if (DEBUG) {
                    Log.d(TAG, String.format("onHandleIntent 82: uri <%s>",uri));
                }
                break;

            case ACTION_REMOVE_FAVORITE:
                if (onActionRemoveFavorite(intent) > 0) {
                    intent.putExtra(INTENT_EXTRA_RESULT_KEY,INTENT_EXTRA_RESULT_SUCCESS);
                } else {
                    intent.putExtra(INTENT_EXTRA_RESULT_KEY,INTENT_EXTRA_RESULT_FAILED);
                }
                break;

            default:
                if (DEBUG) {
                    throw new InvalidParameterException(
                        String.format(TAG+": onHandleIntent 97: action <%s> is invalid.",action));
                }
            }
        }
    }

    //region private methods

    public Uri onActionAddFavorite(Intent intent){
        Uri returnUri = null;

        int movieId = intent.getIntExtra(MovieFavoriteTable.MOVIE_ID, ID_NOT_SET);

        if (movieId != ID_NOT_SET){

            String movieTitle = intent.getStringExtra(
                    MovieFavoriteTable.MOVIE_TITLE);

            String movieJson = intent.getStringExtra(MovieFavoriteTable.MOVIE_JSON);

            ContentValues values = new ContentValues();
            values.put(MovieFavoriteTable.MOVIE_ID, movieId);
            values.put(MovieFavoriteTable.MOVIE_TITLE, movieTitle);
            values.put(MovieFavoriteTable.MOVIE_JSON, movieJson );

            ContentResolver resolver = getContentResolver();
            returnUri =  resolver.insert(MovieFavoriteTable.CONTENT_URI, values);
        }

        return returnUri;
    }

    public int onActionRemoveFavorite(Intent intent){
        int returnRemovedCount = 0;
        int movieId = intent.getIntExtra(MovieFavoriteTable.MOVIE_ID,ID_NOT_SET);
        if (movieId != ID_NOT_SET){
            String[] selectionArgs = {String.valueOf(movieId)};
            ContentResolver resolver = getContentResolver();
            returnRemovedCount = resolver.delete(
                    MovieFavoriteTable.CONTENT_URI,
                    MovieDbContract.SELECTION_MOVIE_ID,
                    selectionArgs);
        }
        return returnRemovedCount;
    }

    //endregion private methods
}
