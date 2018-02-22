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

import com.paulmender.android.content.AsyncTaskLoaderCallbacks;
import com.paulmender.udacity.popularmovies.content.MovieDbAsyncTaskLoaderCallbacks;

/**
 * Package: com.paulmender.udacity.popularmovies.app
 * Project: PopularMovies Stage 2
 *    Name: BaseMovieExtraListFragment
 * Purpose: Base class for the list of extras, e.g., videos and reviews.
 *  Author: Paul on 20171120.
 * Reference:
 *  https://developer.android.com/training/basics/fragments/communicating.html
 */

public class BaseMovieExtraListFragment<LC extends AsyncTaskLoaderCallbacks>
                extends BaseMovieExtraFragment<MovieDbAsyncTaskLoaderCallbacks> {

    // Available for customizations when needed.

}