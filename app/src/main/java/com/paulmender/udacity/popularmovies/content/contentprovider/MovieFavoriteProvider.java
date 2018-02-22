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

package com.paulmender.udacity.popularmovies.content.contentprovider;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteConstraintException;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.paulmender.udacity.popularmovies.data.MovieDbContract;
import com.paulmender.udacity.popularmovies.data.MovieDbHelper;

import java.util.Locale;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies.data
 * Project: PopularMovies Stage 2
 *    Name: MovieFavoriteProvider
 * Purpose: Manage movie favorites data.
 *  Author: Paul on 20180114.
 * References:
 *  1. Udacity exercise S09.01-Solution-ContentProviderFoundation.
 *  2. https://www.grokkingandroid.com/android-tutorial-writing-your-own-content-provider/
 */
public class MovieFavoriteProvider extends ContentProvider {

    private static final String TAG = "PKMI>MovieFaveProvide..";

    private static final int uriMatchFavoriteList = 100;
    private static final int uriMatchFavoriteMovieId = 110;

    @SuppressWarnings("WeakerAccess")
    public static final int ID_INSERT_FAILED = 0;

    private static final UriMatcher uriMatcher = buildUriMatcher();

    private MovieDbHelper dbHelper;

    // See Reference 2.
    private final ThreadLocal<Boolean> isInBatchMode = new ThreadLocal<>();

    //region Override methods

    /**
     * Since onCreate() is run on the main thread, minimize initialization here.
     */
    @Override
    public boolean onCreate() {
        dbHelper = new MovieDbHelper(getContext());
        return true;
    }

    @Nullable
    @Override
    public Cursor query(@NonNull Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {

        Cursor returnCursor = null;

        switch (uriMatcher.match(uri)) {
            case uriMatchFavoriteList:
                returnCursor = dbHelper.getReadableDatabase().query(
                        MovieDbContract.MovieFavoriteTable.TABLE_NAME,
                        projection,
                        selection,
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            case uriMatchFavoriteMovieId:
                returnCursor = dbHelper.getReadableDatabase().query(
                        MovieDbContract.MovieFavoriteTable.TABLE_NAME,
                        projection,
                        MovieDbContract.MovieFavoriteTable.MOVIE_ID + " = ? ",
                        selectionArgs,
                        null,
                        null,
                        sortOrder);
                break;

            default:
                if (DEBUG) {
                    Log.e(TAG, "query 108: Unknown URI: " + uri);
                    try {
                        throw new Exception(TAG + ": printStackTrace...");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
        }

        return returnCursor;
    }

    @Nullable
    @Override
    public String getType(@NonNull Uri uri) {
        switch ((uriMatcher.match(uri))){
            case uriMatchFavoriteList:
                return MovieDbContract.MovieFavoriteTable.CONTENT_TYPE;
            case uriMatchFavoriteMovieId:
                return MovieDbContract.MovieFavoriteTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException(String.format(TAG + ": uri: %s", uri));
        }
    }

    @Nullable
    @Override
    public Uri insert(@NonNull Uri uri, @Nullable ContentValues contentValues) {

        switch (uriMatcher.match(uri)) {

            case uriMatchFavoriteList:

                final SQLiteDatabase db = dbHelper.getWritableDatabase();

                long id = ID_INSERT_FAILED;

                try {
                    id = db.insert(
                            MovieDbContract.MovieFavoriteTable.TABLE_NAME,
                            null,
                            contentValues);
                } catch (SQLiteConstraintException sce){
                    Log.e(TAG, "insert: ", sce );
                }
                return getUriForId(id, uri);

            default:
                throw new IllegalArgumentException(
                        String.format(TAG + ": unsupported uri for insert: %s", uri));
        }
    }

    @Override
    public int delete(@NonNull Uri uri, @Nullable String selection, @Nullable String[] selectionArgs) {

        int returnDeleteCount = 0;

        /*
         * If null is passed as the selection to SQLiteDatabase#delete, the entire table will be
         * deleted, and the number of rows deleted will be unknown.
         * According to the SQLiteDatabase documentation, passing "1" for the selection will
         * delete all rows *and* return the number of deleted rows.
         */
        if (null == selection) {selection = "1";}

        switch (uriMatcher.match(uri)) {

            case uriMatchFavoriteList:
                returnDeleteCount = dbHelper.getWritableDatabase().delete(
                        MovieDbContract.MovieFavoriteTable.TABLE_NAME,
                        selection,
                        selectionArgs);

                break;

            default:
                if (DEBUG) {
                    Log.e(TAG, "delete 186: Unknown URI: " + uri);
                }
        }

        /* If rows were deleted, notify the observers (if any) of the change to this URI. */
        if (returnDeleteCount != 0) {
            if ((getContext() != null) && (getContext().getContentResolver() != null)){
                getContext().getContentResolver().notifyChange(uri, null);
            }
        }

        return returnDeleteCount;

    }

    @Override
    public int update(@NonNull Uri uri,
                      @Nullable ContentValues contentValues,
                      @Nullable String s,
                      @Nullable String[] strings) {
        return 0;
    }

    //endregion Override methods

    //region private methods

    private static UriMatcher buildUriMatcher() {

        // Initialize the URI matcher to the root URI.
        final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

        final String authority = MovieDbContract.CONTENT_AUTHORITY;

        // Add URI content://com.paulmender.udacity.popularmovies/movieFavorite/.
        uriMatcher.addURI(authority,
                MovieDbContract.PATH_FAVORITE,
                uriMatchFavoriteList);

        /*
         * Add URI with movie ID.
         * Example: content://com.paulmender.udacity.popularmovies/movieFavorite/129
         * The "/#" signifies to the UriMatcher that if PATH_FAVORITE is followed by ANY number,
         * that it should return the uriMatchFavoriteMovieId
         */
        uriMatcher.addURI(authority,
                MovieDbContract.PATH_FAVORITE + "/#",
                uriMatchFavoriteMovieId);

        return uriMatcher;
    }

    // See Reference 2.
    private Uri getUriForId(long id, Uri uri) {

        if (id > ID_INSERT_FAILED) {

            Uri itemUri = ContentUris.withAppendedId(uri, id);
            if (!isInBatchMode()) {
                // notify all listeners of changes:
                if ((getContext() != null) && (getContext().getContentResolver() != null)){
                    getContext().getContentResolver().notifyChange(itemUri, null);
                }
            }
            return itemUri;
        }

        if (DEBUG) {
            try {
                throw new SQLException();
            } catch (SQLException se) {
                Log.e(TAG, String.format(
                        Locale.US, TAG + ": getUriForId: id = %d, uri = %s", id, uri), se);

            }
        }

        return null;
    }

    // See Reference 2.
    private boolean isInBatchMode() {
        return this.isInBatchMode.get() != null && this.isInBatchMode.get();
    }
    //endregion private methods
}
