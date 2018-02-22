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

package com.paulmender.udacity.popularmovies.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Defines the column names used from the MovieDB and the contract between clients
 * and content providers.
 * References:
 *  1. Udacity exercise S09.01-Solution-ContentProviderFoundation.
 *  2. https://www.grokkingandroid.com/android-tutorial-writing-your-own-content-provider/
 */

public final class MovieDbContract {

    //region Stage 1 project
    public static final String MOVIE_DB_ID = "id";
    public static final String MOVIE_DB_TITLE = "title";
    public static final String MOVIE_DB_RELEASE_DATE = "release_date";
    public static final String MOVIE_DB_POSTER_PATH = "poster_path";
    @SuppressWarnings("unused")
    public static final String MOVIE_DB_BACKDROP_PATH = "backdrop_path";
    public static final String MOVIE_DB_VOTE_AVERAGE = "vote_average";
    @SuppressWarnings("unused")
    public static final String MOVIE_DB_VOTE_COUNT = "vote_count";
    public static final String MOVIE_DB_OVERVIEW = "overview";
    //endregion

    //region Stage 2 project
    // Runtime
    public static final String MOVIE_DB_RUNTIME = "runtime";

    // Reviews
    public static final String MOVIE_DB_REVIEW_ID = "id";
    public static final String MOVIE_DB_REVIEW_AUTHOR = "author";
    public static final String MOVIE_DB_REVIEW_CONTENT = "content";
    public static final String MOVIE_DB_REVIEW_URL = "url";

    // Videos
    public static final String MOVIE_DB_VIDEOS_ID = "id";
    public static final String MOVIE_DB_VIDEOS_ISO_639_1 = "iso_639_1";
    public static final String MOVIE_DB_VIDEOS_ISO_3166_1 = "iso_3166_1";
    public static final String MOVIE_DB_VIDEOS_KEY = "key";
    public static final String MOVIE_DB_VIDEOS_NAME = "name";
    public static final String MOVIE_DB_VIDEOS_SITE = "site";
    public static final String MOVIE_DB_VIDEOS_SIZE = "size";
    public static final String MOVIE_DB_VIDEOS_TYPE = "type";

    // Movie Favorites Content Provider
    public static final String CONTENT_AUTHORITY = "com.paulmender.udacity.popularmovies";
    @SuppressWarnings("WeakerAccess")
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    /* A selection clause for movie ID based queries.*/
    public static final String SELECTION_MOVIE_ID = CommonColumns.MOVIE_ID + " = ? ";

    public static final String PATH_FAVORITE = "favorite";

    // ID not set; applies to Movie ID and base _ID.
    public static final int ID_NOT_SET = -1;

    //endregion Stage 2 project

    // Constructor.
    public MovieDbContract(){
    }

    //region Inner class to define the movie favorite table

    /* Define the table contents of the movie favorites table */
    public static final class MovieFavoriteTable implements CommonColumns {

        /* The name of the favorite table. */
        public static final String TABLE_NAME = "movieFavorite";

        /**
         * The content URI for movie favorite table.
         */
        public static final Uri CONTENT_URI =
                                    MovieDbContract.BASE_CONTENT_URI.buildUpon().
                                    appendPath(PATH_FAVORITE).
                                    build();
        /**
         * The mime type of a directory of movie favorites.
         */
        public static final String CONTENT_TYPE = ContentResolver.CURSOR_DIR_BASE_TYPE +
                "/vnd.com.paulmender.udacity.popularmovies.moviedb_favorite";

        /**
         * The mime type of a single movie favorite.
         */
        public static final String CONTENT_ITEM_TYPE = ContentResolver.CURSOR_ITEM_BASE_TYPE +
                "/vnd.com.paulmender.udacity.popularmovies.moviedb_favorite";

        public static final String MOVIE_JSON = "movieJson";

        /**
         * A projection of all columns in the movieFavorite table.
         */
        public static final String[] PROJECTION_ALL = {_ID, MOVIE_ID, MOVIE_TITLE, MOVIE_JSON};

    }

    //endregion inner class

    //region interfaces

    public interface CommonColumns extends BaseColumns{
        String MOVIE_ID = MovieDbContract.MOVIE_DB_ID;
        String MOVIE_TITLE = MovieDbContract.MOVIE_DB_TITLE;
    }

    //endregion interfaces

}