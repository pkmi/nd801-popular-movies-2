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

/*
Popular Movies is an application project for the Udacity Android Developer Nanodegree
Author: Paul Mender
Stage 1 Requirements:
    01. Completed: App is written solely in the Java Programming Language.
    02. Completed: Movies are displayed in the main layout via a grid of their corresponding
        movie poster thumbnails.
    03. Completed: UI contains an element (i.e a spinner or settings menu) to toggle the
        sort order of the movies by: most popular, highest rated.
    04. Completed: UI contains a screen for displaying the details for a selected movie.
    05. Completed: Movie details layout contains title, release date, movie poster, vote average,
        and plot synopsis.
    06. Completed: When a user changes the sort criteria (most popular and highest rated)
        the main view gets updated correctly.
    07. Completed: When a movie poster thumbnail is selected, the movie details screen is launched.
    08. Completed: In a background thread, app queries the /movie/popular or
        /movie/top_rated API for the sort criteria specified in the settings menu.

Stage 1 Code Review Suggestions:
    CANCELLED: Inject views using ButterKnife.
    Note: I tried using butterknife, but it appears that it cannot bind to local variables.
    Local variables are used throughout my implementation to hold references to views; therefore,
    I am not using ButterKnife for this project.
    1. http://jakewharton.github.io/butterknife/
    2. https://code.tutsplus.com/tutorials/
        quick-tip-using-butter-knife-to-inject-views-on-android--cms-23542

    COMPLETED: Use error() and placeholder() provided by Picasso to avoid potential crash
    due to empty or null image url values.
    ... .placeholder(R.drawable.user_placeholder)
        .error(R.drawable.user_placeholder_error) ...

    COMPLETED: In order to make your codes reusable and structural, you can consider to refactor
    your codes and put this class [MovieMainActivity.FetchMoviesTask] in a separate Java file.

Stage 2 Requirements: (Addendum to Stage 1 requirements above.)
    09. COMPLETED: Movie Details layout contains a section for displaying trailer videos
        and user reviews.
    10. COMPLETED: When a user changes the sort criteria (most popular, highest rated,
        and favorites) the main view gets updated correctly.
    11. COMPLETED: When a trailer is selected, app uses an Intent to launch the trailer.
        (Either in the youtube app or a web browser).
    12. COMPLETED: In the movies detail screen, a user can tap a button(for example, a star)
        to mark it as a Favorite. This is for a local movies collection that you
        will maintain and does not require an API request.
    13. COMPLETED: App requests for related videos for a selected movie via the /movie/{id}/videos
        endpoint in a background thread and displays those details when the user selects a movie.
    14. COMPLETED: App requests for user reviews for a selected movie via the /movie/{id}/reviews
        endpoint in a background thread and displays those details when the user selects a movie.
    15. COMPLETED: The titles and ids of the user's favorite movies are stored in a
        ContentProvider backed by a SQLite database. This ContentProvider is updated
        whenever the user favorites or unfavorites a movie.
    16. COMPLETED: When the "favorites" setting option is selected, the main view displays
        the entire favorites collection based on movie ids stored in the ContentProvider.

Stage 2 Suggestions:
    17. COMPLETED: Extend the favorites ContentProvider to store the movie poster, synopsis,
        user rating, and release date, and display them even when offline.
    18. COMPLETED: Implement sharing functionality to allow the user to share the
        first trailer’s YouTube URL from the movie details screen.

General Requirements:
    Completed: App conforms to common standards found in the Android Nanodegree General Project Guidelines
    - Java      Completed:
    - Git       Completed: Note: For Stage 1, did not include the type.
    - Core      Completed: Note: The app may or may not adhere to Google Play Store App policies.
    - Tablet    Ignored for this project per Carlos.

Other Implementation Guide Requirements
    1. COMPLETED: (Using gradle.properties/BuildConfig) IMPORTANT: PLEASE REMOVE YOUR API KEY WHEN SHARING CODE PUBLICLY
    2. COMPLETED: 20180125 Fix unavailable messages. You must make sure your app does not crash when there is no network connection!

Movie DB Requirements:
    1. COMPLETED: (Paul) Add About or Credits menu option and include the attribution requirements found at
        https://www.themoviedb.org/faq/api.
*/
package com.paulmender.udacity.popularmovies;

import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.Bundle;
import android.os.Parcelable;
import android.os.StrictMode;
import android.support.design.widget.TabLayout;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulmender.android.content.AsyncTaskBundleWrapper;
import com.paulmender.android.content.AsyncTaskLoaderWidgetHelper;
import com.paulmender.android.support.v4.app.BaseDialogFragment;
import com.paulmender.android.support.v7.app.appcompatactivity.extensions.ActivityExtension;
import com.paulmender.android.widget.GridLayoutManagerAutofit;
import com.paulmender.udacity.popularmovies.app.BaseMovieActivity;
import com.paulmender.udacity.popularmovies.app.MovieAboutDialogFragment;
import com.paulmender.udacity.popularmovies.content.MovieDbAsyncTaskLoaderCallbacks;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility.MovieQuery;
import com.paulmender.udacity.popularmovies.widget.MovieDbRecyclerAdapter;

import java.util.List;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies.app
 * Project: PopularMovies Stage 2
 *    Name: MainMovieActivity
 * Purpose: The Popular Movies application's main activity.
 *  Author: Paul on 20171208.
 * History:
 *  20171208 Refactored: Moved common code to new BaseMovieActivity and
 *           changed the extends clause accordingly.
 * References:
 *  1. https://developer.android.com/reference/android/support/design/widget/TabLayout.html
 *          #setupWithViewPager(android.support.v4.view.ViewPager)
 *  2. https://www.bignerdranch.com/blog/viewpager-without-fragments/
 *  3. https://developer.android.com/guide/topics/ui/dialogs.html#ShowingADialog
 */

public class MovieMainActivity
        extends BaseMovieActivity<MovieParcelable, List<MovieParcelable>>
            implements MovieDbRecyclerAdapter.MovieDbAdapterOnClickHandler,
                    ActivityExtension.Extensions {

    private static final String TAG = "PKMI>MovieMainActivity";

    //region Static Private fields
    private static final String sDialogFragmentTagAbout = "AboutDialogFragment";

    private static final int sTabPositionPopular = 0;
    private static final int sTabPositionTopRated = 1;
    private static final int sTabPositionFavorites = 2;

    //endregion

    //region ActivityExtension.extensions methods

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onCreateSetContent() {
        // See RecyclerView rv_activity_main.
    }

    //endregion

    //region MovieDbAdapterOnClickHandler methods

    /**
     * onClick implements the MovieDbAdapterOnClickHandler interface.
     *
     * @param p The Parcelable object.
     */
    @Override
    public <P extends Parcelable> void onClick(P p) {
        MovieParcelable movieDetail = (MovieParcelable) p;

        if (DEBUG) {
            Log.d(TAG, String.format("onClick 178: *** movieDetail.Title *** = %s: movieId = %d",
                    movieDetail.title, movieDetail.movieId));
        }

        // Set the selected movie id.
        setMovieId(movieDetail.movieId);

        // Define the intent.
        Context context = this;
        Class destinationClass = MovieDetailActivity.class;
        Intent movieDetailIntent = new Intent(context, destinationClass);
        movieDetailIntent.putExtra(INTENT_EXTRA_KEY_MOVIE_DETAIL_PARCELABLE, movieDetail);

        startActivity(movieDetailIntent);
    }

    //endregion

    //region TabLayout.OnTabSelectedListener methods
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        // Set the data source according to the currently selected tab.
        setDataSource();
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // Do nothing.
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        // Do nothing.
    }

    //endregion

    //region Override Methods

    @Override
    protected AsyncTaskLoaderWidgetHelper getAsyncTaskLoaderWidgetHelper() {
        return new AsyncTaskLoaderWidgetHelper(
                (TextView) findViewById(R.id.tv_activity_main_error),
                (ProgressBar) findViewById(R.id.pb_activity_main),
                (RecyclerView) findViewById(R.id.rv_activity_main));
    }

    @Override
    protected AsyncTaskBundleWrapper getAsyncTaskBundleWrapper() {

        AsyncTaskBundleWrapper bundleWrapper = new AsyncTaskBundleWrapper(null);

        bundleWrapper.setItemId(getMovieId());

        return bundleWrapper;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (DEBUG) {

            /* When set to true, StrictMode related exceptions will be reported.
               Also see "StrictMode error" in comments for comments related to
               previous StrictMode errors.
            */
            setStrictMode(false);
        }

        setContentView(Layout.MAIN.layoutId);

        restoreSelectedItemPreferences();

        // Create a new relevant callbacks object for setting the extensions.
        MovieDbAsyncTaskLoaderCallbacks callbacks =
                new MovieDbAsyncTaskLoaderCallbacks(
                        this,
                        getAsyncTaskLoaderWidgetHelper(),
                        getAsyncTaskBundleWrapper(),
                        getSelectedMovieQuery());

        // Set the all of the "onCreate" extensions according to the parameters values.
        // Note: Override onCreateSetContent() to set the UI content.
        onCreateSetExtensions(
                LOADER_ID_MOVIE_MAIN_ACTIVITY,
                callbacks,
                (TabLayout) findViewById(R.id.tl_activity_main),
                sTabPositionPopular,
                this);

        // Populate the movieParcelable list.
        setDataSource();

        // Set the movieParcelable list recycler view.
        initMovieDbRecyclerView();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as a parent activity is defined in AndroidManifest.xml.
        int id = item.getItemId();

        switch (id) {
            case R.id.action_about:
                showAboutDialog();
            default:
                // If none of the above, call the superclass.
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveSelectedItemPreferences();

    }

    /**
     * Set the RecyclerView Adapter.
     */
    @Override
    protected void initMovieDbRecyclerView() {

        if (getMovieDbRecyclerView() == null) {
            MovieDbRecyclerAdapter movieDbRecyclerAdapter =
                    new MovieDbRecyclerAdapter(this,
                            MovieDbRecyclerAdapter.MovieDbViewType.MOVIE_POSTER,
                            getMovieId());
            movieDbRecyclerAdapter.setHasStableIds(true);
            setMovieDbRecyclerView(
                    (RecyclerView) findViewById(R.id.rv_activity_main),
                    movieDbRecyclerAdapter);
            setMovieDbRecyclerGridLayout();
        } else {
            if (DEBUG) {
                Log.i(TAG,
                        "*RETURN* initMovieDbRecyclerView 326: MovieDbRecyclerView not null");
            }
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (DEBUG) {
            Log.d(TAG, "*onDestroy*");
        }
    }

    @Override
    protected void onPause() {
        super.onPause();

        saveSelectedItemPreferences();
    }

    @Override
    protected void onResume() {
        super.onResume();

        if (DEBUG) {
            Log.d(TAG, "*onResume*");
        }
        restoreSelectedItemPreferences();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (DEBUG) {
            Log.d(TAG, "*onStart*");
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (DEBUG) {
            Log.d(TAG, "*onStop*");
        }
    }
    // endregion Override Methods

    //region Private Methods
    private MovieQuery getSelectedMovieQuery() {

        MovieQuery selectedMovieQuery;

        int tabPosition = getSelectedTabPosition();

        switch (tabPosition) {
            case sTabPositionPopular:
                selectedMovieQuery = MovieQuery.POPULAR;
                break;
            case sTabPositionTopRated:
                selectedMovieQuery = MovieQuery.TOP_RATED;
                break;
            case sTabPositionFavorites:
                selectedMovieQuery = MovieQuery.FAVORITES;
                break;
            default:
                selectedMovieQuery = MovieQuery.POPULAR;
        }

        return selectedMovieQuery;
    }

    private void restoreSelectedItemPreferences() {

        // Set the movie ID from the saved preference (if any).
        String movieQueryName = getSharedPreference(PREFERENCE_KEY_MOVIE_QUERY);

        if (movieQueryName != null) {
            setSelectedMovieQuery(MovieQuery.getEnumerator(movieQueryName));

            // The movie ID value preference key is the MovieQuery name.
            setMovieId(getSharedPreference(movieQueryName));

            if (DEBUG) {
                Log.d(TAG, String.format(
                        "restoreSelectedItemPreferences 410: " +
                                "movieQueryName <%s> | " +
                                "getMovieId() <%d> | " +
                                "getSelectedTabPosition() <%d>",
                        movieQueryName, getMovieId(), getSelectedTabPosition()));
            }
        }
    }

    private void saveSelectedItemPreferences() {
        // Save the currently selected movie ID and selected movie query.
        setSharedPreference(PREFERENCE_KEY_MOVIE_QUERY, getSelectedMovieQuery().name());

        // Note: The movie ID value is according to the MovieQuery name.
        setSharedPreference(getSelectedMovieQuery().name(), String.valueOf(getMovieId()));

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "saveSelectedItemPreferences 428: selectedMovieQuery <%s> | getMovieId() <%d>",
                    getSelectedMovieQuery().name(), getMovieId()));
        }
    }

    private void setSelectedMovieQuery(MovieQuery movieQuery) {

        int tabPosition = getSelectedTabPosition();

        int selectTabPosition;

        switch (movieQuery) {
            case POPULAR:
                selectTabPosition = sTabPositionPopular;
                break;
            case TOP_RATED:
                selectTabPosition = sTabPositionTopRated;
                break;
            case FAVORITES:
                selectTabPosition = sTabPositionFavorites;
                break;
            default:
                selectTabPosition = sTabPositionPopular;
        }
        if (tabPosition != selectTabPosition) {
            setTabSelect(selectTabPosition);
        }
    }

    /**
     * Set display properties for the movieParcelable poster grid.
     */
    private void setMovieDbRecyclerGridLayout() {

        // Automatically set the span count below based on column width, screen orientation,
        // and screen size to fit the images.
        int gridColumnWidth = getResources().
                getInteger(R.integer.movie_poster_grid_column_width_vertical_mode);

        try {
            GridLayoutManagerAutofit layoutManager
                    = new GridLayoutManagerAutofit(this, gridColumnWidth);
            getMovieDbRecyclerView().setLayoutManager(layoutManager);
        } catch (NullPointerException npe) {
            if (DEBUG) {
                Log.d(TAG, "setMovieDbRecyclerGridLayout 473: ", npe);
            }
        }
    }

    /**
     * Set favorites, popular, top rated data sources based on respective tab onClick().
     */
    private void setDataSource() {

        // Note: The selected movie query is based on the selected tab.
        MovieQuery requestedMovieQuery = getSelectedMovieQuery();

        // If the movie query is being reset to a different movie query...
        if (isMovieQueryReset(requestedMovieQuery)) {

            try {
                // Set the callback movie query to the requested movie query.
                getAsyncTaskLoaderCallbacks().setMovieQuery(requestedMovieQuery);

                // ... restart the loader with the requested movie query.
                getLoaderManager().restartLoader(
                        LOADER_ID_MOVIE_MAIN_ACTIVITY,
                        getAsyncTaskLoaderCallbacks().getLoaderBundle(),
                        getAsyncTaskLoaderCallbacks());

            } catch (NullPointerException npe) {
                if (DEBUG) {
                    Log.d(TAG, "setDataSource 501:", npe);
                    npe.printStackTrace();
                }
            }

            if (DEBUG) {
                Log.d(TAG, String.format("setDataSource 507: %s: restartLoader()",
                        requestedMovieQuery.name()));
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, String.format("setDataSource 512: %s: no restartLoader()",
                        requestedMovieQuery.name()));
            }
        }
    }

    /**
     * setStrictMode
     * Purpose:
     * StrictMode is most commonly used to catch accidental disk or network access
     * on the application's main thread, where UI operations are received and animations take place.
     *
     * @param on Reference:
     * @link https://developer.android.com/reference/android/os/StrictMode.html
     */
    @SuppressWarnings("SameParameterValue")
    private void setStrictMode(boolean on) {
        if (DEBUG) {
            if (on) {
                StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                        .detectDiskReads()
                        .detectDiskWrites()
                        .detectNetwork()   // or .detectAll() for all detectable problems
                        .penaltyLog()
                        .build());
                StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                        .detectLeakedSqlLiteObjects()
                        .detectLeakedClosableObjects()
                        .penaltyLog()
                        .penaltyDeath()
                        .build());
            }
        }
    }

    private void showAboutDialog(){

        Resources r = getResources();
        String positiveCaption = r.getString(R.string.dialog_button_close);
        int layoutId = R.layout.activity_main_about;

        Bundle args = new Bundle();
        args.putString(BaseDialogFragment.BUNDLE_KEY_CAPTION_POSITIVE, positiveCaption);
        args.putInt(BaseDialogFragment.BUNDLE_KEY_LAYOUT_ID, layoutId);

        MovieAboutDialogFragment aboutDialog = MovieAboutDialogFragment.newInstance(args);
        FragmentManager fm = getSupportFragmentManager();
        aboutDialog.show(fm, sDialogFragmentTagAbout);
    }

    //endregion Private methods

}
