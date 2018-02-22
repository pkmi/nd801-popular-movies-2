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

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import static com.paulmender.udacity.popularmovies.data.MovieDbContract.MovieFavoriteTable;

/**
 * Package: com.paulmender.udacity.popularmovies.data
 * Project: PopularMovies Stage 2
 *    Name: MovieDbHelper
 * Purpose: Create the movieFavorite.db database.
 *  Author: Paul on 20180114.
 * References:
 *  1. Udacity WeatherDbHelper.java in com.example.android.sunshine.data.
 */
public class MovieDbHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "movieFavorite.db";

    private static final int databaseVersion = 1;

    private final Context context;
    @SuppressWarnings("unused")
    public Context getContext() {
        return context;
    }

    // Constructor
    public MovieDbHelper(Context context){
        super(context, DATABASE_NAME, null, databaseVersion);
        this.context = context;
    }

    //region Override/implement methods

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        // SQL command string to create the table (if it does not exist).
        final String sqlCreateTable =
                "CREATE TABLE IF NOT EXISTS " + MovieFavoriteTable.TABLE_NAME + " (" +
                MovieFavoriteTable._ID             + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                MovieFavoriteTable.MOVIE_ID     + " INTEGER NOT NULL, " +
                MovieFavoriteTable.MOVIE_TITLE  + " TEXT NOT NULL, " +
                MovieFavoriteTable.MOVIE_JSON  + " TEXT NOT NULL, " +
                " UNIQUE (" + MovieFavoriteTable.MOVIE_ID + ") ON CONFLICT REPLACE);";

        // Execute the SQL command to create the table.
        sqLiteDatabase.execSQL(sqlCreateTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // Possibly implement a conversion, e.g., ALTER TABLE ...
    }

    //endregion Override/implement methods
}
