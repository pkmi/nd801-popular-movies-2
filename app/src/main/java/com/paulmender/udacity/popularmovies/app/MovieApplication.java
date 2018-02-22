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

import android.app.Application;
import android.util.Log;

import com.facebook.stetho.Stetho;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;
import static com.paulmender.udacity.popularmovies.data.MovieDbHelper.DATABASE_NAME;

/**
 * Package: com.paulmender.udacity.popularmovies.app
 * Project: PopularMovies Stage 2
 *    Name: MovieApplication
 * Purpose: Initialize Stetho, and other application level items.
 *  Author: Paul on 20171220.
 */
public class MovieApplication extends Application{

    private static final String TAG = "PKMI>MovieApplication";

    public void onCreate() {
        super.onCreate();

        if (DEBUG){

            Stetho.initializeWithDefaults(this);

            // Verify the database path for the auto backup configuration.
            // See res/xml/movie_backup_rules.xml ... path="movieFavorite.db"
            Log.i(TAG, String.format("onCreate: getDataBasePath(%s) = %s",
                    DATABASE_NAME,this.getDatabasePath(DATABASE_NAME)));
        }
    }
}
