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
package com.paulmender.udacity.popularmovies;

import android.app.LoaderManager;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.support.design.widget.TabLayout;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.paulmender.android.content.AsyncTaskBundleWrapper;
import com.paulmender.android.content.AsyncTaskLoaderWidgetHelper;
import com.paulmender.android.support.v7.app.appcompatactivity.extensions.ActivityExtension;
import com.paulmender.udacity.popularmovies.app.BaseMovieActivity;
import com.paulmender.udacity.popularmovies.app.service.MovieFavoriteIntentService;
import com.paulmender.udacity.popularmovies.content.MovieDbAsyncTaskLoaderCallbacks;
import com.paulmender.udacity.popularmovies.data.MovieDbContract;
import com.paulmender.udacity.popularmovies.data.MovieDbContract.MovieFavoriteTable;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.squareup.picasso.Picasso;

import java.security.InvalidParameterException;
import java.util.List;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies
 * Project: PopularMovies
 * Purpose: Display the details of the movie selected from the main activity:
 *          title, release date, movie poster, user rating (vote average),
 *          and plot synopsis (overview).
 * History:
 *  20171117 Start add runtime for Stage 2.
 *  20171208 Refactored: now extends new BaseMovieActivity; removed redundant code.
 * References:
 *  https://developer.android.com/reference/android/app/Fragment.html
 *  https://developer.android.com/guide/components/fragments.html
 *  https://developer.android.com/training/basics/fragments/creating.html
 *  https://developer.android.com/reference/android/app/FragmentTransaction.html#pubmethods
 *  https://stackoverflow.com/questions/28439003/use-parcelable-to-store-item-as-sharedpreferences
 *  https://github.com/google/gson/blob/master/UserGuide.md#TOC-Gson-With-Gradle
 */
public class MovieDetailActivity
        extends BaseMovieActivity<MovieParcelable, List<MovieParcelable>>
            implements
                    ActivityExtension.Extensions,
                    LoaderManager.LoaderCallbacks<Cursor>,
                    View.OnClickListener {

    private static final String TAG = "PKMI>MovieDetailAct...";

    //region Non-public, Non-static fields

    private boolean isFavorite = false;

    private Button movieFavoriteButton = null;

    private Intent movieFavoriteIntent = null;

    //endregion

    //region private Static members

    private final static int sFavoritesLoader = 116;

    private final static int sTabPositionReviews = 0;
    private final static int sTabPositionVideos = 1;

    private static final int sToastPosition = 200;

    //endregion

    //region Getters and Setters

    // isFavorite
    private boolean isFavorite() {
        return this.isFavorite;
    }

    private void setFavorite(boolean favorite) {
        this.isFavorite = favorite;
    }

    //endregion

    //region implements methods

    // AppCompatActivityExtension.extensions
    @Override
    public void onCreateSetContent() {
        setMovieDetailContent();
    }

    // LoaderCallbacks<Cursor>
    @Override
    public Loader<Cursor> onCreateLoader(int loaderId, Bundle args) {
        String[] selectionArgs = {String.valueOf(getMovieId())};
        return new CursorLoader(
                this,
                MovieFavoriteTable.CONTENT_URI,
                MovieDbContract.MovieFavoriteTable.PROJECTION_ALL,
                MovieDbContract.SELECTION_MOVIE_ID,
                selectionArgs,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Is this movie in the favorites cursor?
        boolean isFavorite = ((cursor != null) && (cursor.moveToFirst()));

        // Set the favorite field.
        setFavorite(isFavorite);

        // Set the movie favorite view.
        setMovieFavoriteView(isFavorite);

        if (DEBUG) {
            Log.d(TAG, String.format("onLoadFinished 143: isFavorite <%b>", isFavorite));
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        // Not implemented.
    }

    // TabLayout.OnTabSelectedListener methods
    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        showFragmentsBySelectedTab(tab);
    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {
        // Do nothing.
    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {
        // Do nothing.
    }

    //View.OnClickListener()
    @Override
    public void onClick(View view) {
        onClickMovieFavorite();
    }

    public void onClickMovieVideoPlay(View view) {
        try {
            getMovieVideoFragment().onClickMovieVideoPlay(view);
        } catch (Exception e) {
            Log.e(TAG, "onClickMovieVideoPlay: ", e);
        }
    }

    public void onClickMovieVideoShare(View view) {
        try {
            getMovieVideoFragment().onClickMovieVideoShare(view);
        } catch (Exception e) {
            Log.e(TAG, "onClickMovieVideoShare: ", e);
        }
    }

    //endregion implements methods

    //region public methods

    @Override
    public int getMovieId() {
        int value;
        if (this.movie != null) {
            value = this.movie.movieId;
        } else {
            value = super.getMovieId();
        }
        return value;
    }

    private String getMovieTitle() {
        String value = null;
        if (this.movie != null) {
            value = this.movie.title;
        }
        return value;
    }

    private void onClickMovieFavorite() {

        if (this.movieFavoriteIntent == null) {
            this.movieFavoriteIntent =
                    new Intent(this, MovieFavoriteIntentService.class);
        }

        this.movieFavoriteIntent.putExtra(MovieFavoriteTable.MOVIE_ID, this.getMovieId());
        this.movieFavoriteIntent.putExtra(MovieFavoriteTable.MOVIE_TITLE, this.getMovieTitle());
        this.movieFavoriteIntent.putExtra(MovieFavoriteTable.MOVIE_JSON, this.getMovieJson());

        boolean isFavoriteNow = isFavorite();

        // If the movie is currently a favorite...
        if (isFavoriteNow) {
            // ... remove it from the movie favorite table.
            this.movieFavoriteIntent.setAction(MovieFavoriteIntentService.ACTION_REMOVE_FAVORITE);
        } else {
            // Not currently a favorite, so add it to the movie favorite table.
            this.movieFavoriteIntent.setAction(MovieFavoriteIntentService.ACTION_ADD_FAVORITE);
        }
        // 1/24/2018 Consider using startForegroundService().
        // Update the movie favorite database through the intent service.
        startService(this.movieFavoriteIntent);

        boolean resultSuccess;
        int result = this.movieFavoriteIntent.getIntExtra(
                MovieFavoriteIntentService.INTENT_EXTRA_RESULT_KEY,
                MovieFavoriteIntentService.INTENT_EXTRA_RESULT_SUCCESS);
        resultSuccess = (result == MovieFavoriteIntentService.INTENT_EXTRA_RESULT_SUCCESS);

        int stringId;
        if (isFavoriteNow) {
            stringId = resultSuccess ?
                    R.string.removed_from_favorites :
                    R.string.removed_from_favorites_failed;
            if (stringId == R.string.removed_from_favorites) {
                setFavorite(false);
            }
        } else { // is not a favorite now
            stringId = resultSuccess ?
                    R.string.added_to_favorites :
                    R.string.added_to_favorites_failed;
            if (stringId == R.string.added_to_favorites) {
                setFavorite(true);
            }
        }

        String toastText = String.format(getResources().getString(stringId), getMovieTitle());
        Toast resultToast = Toast.makeText(getApplicationContext(), toastText, Toast.LENGTH_LONG);
        resultToast.setGravity(Gravity.TOP, 0, sToastPosition);
        //View toastView = resultToast.getView();
        //toastView.setBackgroundColor(getResources().getColor(R.color.colorAccent));
        //resultToast.setView(toastView);
        resultToast.show();

        setMovieFavoriteView(isFavorite());

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "*RETURN* onClickMovieFavorite 273: isFavoriteNow <>%b", isFavoriteNow));
        }
    }

    //endregion Public Methods

    //region Override Methods

    @Override
    protected AsyncTaskBundleWrapper getAsyncTaskBundleWrapper() {
        AsyncTaskBundleWrapper bundleWrapper = new AsyncTaskBundleWrapper(null);
        bundleWrapper.setItemId(getMovieId());
        return bundleWrapper;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(Layout.MOVIE_DETAIL.layoutId);

        restorePreferences(savedInstanceState);

        // Create a new relevant callbacks object for setting the extensions.
        MovieDbAsyncTaskLoaderCallbacks callbacks =
                new MovieDbAsyncTaskLoaderCallbacks(
                        this,
                        getAsyncTaskLoaderWidgetHelper(),
                        getAsyncTaskBundleWrapper(),
                        MovieDbUtility.MovieQuery.MOVIE_DETAIL);

        onCreateSetExtensions(
                LOADER_ID_MOVIE_DETAIL_ACTIVITY,
                callbacks,
                (TabLayout) findViewById(R.id.tl_movie_detail_activity_extras),
                sTabPositionReviews,
                this);

        setDataSource();

        // The fragments movie IDs must be set before this activity is created
        // so that they will have a reference to the movie id, and so on,
        // available in their respective onActivityCreated() methods.
        setFragments();

        // Initialize the movie favorites cursor loader.
        getLoaderManager().initLoader(sFavoritesLoader, null, this);

        setMovieDetailContent();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Destroy the service since it is no longer needed.
        this.movieFavoriteIntent = null;

    }

    @Override
    protected void onResume() {
        super.onResume();
        restorePreferences(null);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {

        outState.putParcelable(INTENT_EXTRA_KEY_MOVIE_DETAIL_PARCELABLE, this.movie);
        super.onSaveInstanceState(outState);

        savePreferences();

        // TODO LOW: 12/21/2017 Implement get...ListIndex() methods to return to selected list item.
        // Save the list indexes.
        // setSharedPreference(getSharedPreferenceKey(
        //         this,PREFERENCE_KEY_MOVIE_REVIEW_LIST_INDEX), getMovieReviewListIndex());
        // setSharedPreference(getSharedPreferenceKey(
        //         this,PREFERENCE_KEY_MOVIE_VIDEO_LIST_INDEX), getMovieVideoListIndex());
        if (DEBUG) {
            Log.d(TAG, "*RETURN* onSaveInstanceState 353 ");
        }
    }

    @Override
    protected AsyncTaskLoaderWidgetHelper getAsyncTaskLoaderWidgetHelper() {

        AsyncTaskLoaderWidgetHelper widgetHelper = new AsyncTaskLoaderWidgetHelper();

        widgetHelper.setErrorTextView((TextView) findViewById(R.id.tv_activity_movie_detail_error));
        widgetHelper.setProgressBar((ProgressBar) findViewById(R.id.pb_activity_movie_detail));

        // Note: Because the movie runtime is not populated by the query in the main movie
        // activity (due to the MovieDb query structure), a separate query is performed
        // the to retrieve the movie runtime in the asynchronous task loader above.

        // Configure the widget helper to set the movie runtime value through the
        // asynchronous task loader.
        widgetHelper.setTextView((TextView) findViewById(R.id.tv_movie_detail_runtime));
        widgetHelper.setTextViewDataSource(MovieDbContract.MOVIE_DB_RUNTIME);
        widgetHelper.setTextViewFormatString(
                getResources().getString(R.string.runtime_format_string));
        widgetHelper.setTextViewTagId(R.id.tag_movie_detail_runtime);

        return widgetHelper;
    }

    //endregion Override Method

    //region private methods

    private MovieReviewListFragment getMovieReviewFragment(){
        return (MovieReviewListFragment)
                getFragmentManagerHelper().findFragmentById(R.id.movie_review_list_fragment);
    }

    private MovieVideoListFragment getMovieVideoFragment(){
        return (MovieVideoListFragment)
                getFragmentManagerHelper().findFragmentById(R.id.movie_video_list_fragment);
    }

    private void restorePreferences(Bundle savedInstanceState) {

        if (savedInstanceState == null) {
            this.movie = getIntent().getParcelableExtra(INTENT_EXTRA_KEY_MOVIE_DETAIL_PARCELABLE);
            if (DEBUG) {
                Log.d(TAG, String.format("restorePreferences 399: getIntent().. this.movie %s",
                        this.movie));
            }
        }

        if (this.movie == null) {
            setMovieFromJson(getSharedPreference(PREFERENCE_KEY_MOVIE_DETAIL));
        }

        if (DEBUG) {
            Log.d(TAG, String.format("restorePreferences 409: showFragmentsBySelectedTab(%d)",
                    getSelectedTabPosition()));
        }

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "restorePreferences 415: " +
                            "getMovieId() <%d> | " +
                            "getSelectedTabPosition() <%d>",
                    getMovieId(), getSelectedTabPosition()));
        }
    }

    private void savePreferences() {

        setSharedPreference(PREFERENCE_KEY_MOVIE_DETAIL, getMovieJson());

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "savePreferences 428: getMovieId() <%d>", getMovieId()));
        }
    }

    /**
     * Set the data source for retrieving additional detail (e.g., runtime).
     */
    private void setDataSource() {

        MovieDbUtility.MovieQuery movieQuery = getMovieQuery();

        // If the movie query is being reset to a different movie query...
        if (isMovieQueryReset(movieQuery)) {

            try {
                // Set the callback movie query to the this movie query.
                getAsyncTaskLoaderCallbacks().setMovieQuery(movieQuery);

                // ... restart the loader with the requested movie query.
                getLoaderManager().restartLoader(
                        LOADER_ID_MOVIE_DETAIL_ACTIVITY,
                        getAsyncTaskLoaderCallbacks().getLoaderBundle(),
                        getAsyncTaskLoaderCallbacks());

            } catch (NullPointerException npe) {
                if (DEBUG) {
                    Log.d(TAG, "setDataSource 454:", npe);
                    npe.printStackTrace();
                }
            }
        }
    }

    private void setFragments() {

        setMovieReviewListFragment();
        setMovieVideoListFragment();

        try {
            showFragmentsBySelectedTab(getTabLayout().getTabAt(getSelectedTabPosition()));
        } catch (NullPointerException npe) {
            if (DEBUG) {
                Log.e(TAG, "setFragments 470: showFragmentsBySelectedTab ", npe);
            }
        }
    }

    /**
     * Populate the movie detail view.
     */
    private void setMovieDetailContent() {

        if (this.movie != null) {

            // Note: The movie Runtime is configured in getAsyncTaskLoaderWidgetHelper(),
            // and set through the asynchronous loader.

            TextView title = findViewById(R.id.tv_include_movie_detail_title);
            TextView titleScroll = findViewById(R.id.tv_movie_detail_title_scrolled);
            title.setText(this.movie.title);
            titleScroll.setText(this.movie.title);

            ImageView poster = findViewById(R.id.iv_movie_detail_poster);
            String loadImage = MovieDbUtility.getImageUrlString(this.movie.posterPath);

            //COMPLETED: Add error() per Stage 1 code review.
            // Note: Did not add the placeholder() as suggested because it was distracting
            // when the movies posters loaded.
            Picasso.with(this)
                    .load(loadImage)
                    .error(R.drawable.ic_movie_poster_error)
                    .into(poster);

            TextView year = findViewById(R.id.tv_movie_detail_year);
            String yearText = this.movie.releaseDate.substring(0, 4);
            year.setText(yearText);

            TextView voteAverage = findViewById(R.id.tv_movie_detail_vote_average);
            String voteAverageText = this.movie.voteAverage +
                    getResources().getString(R.string.vote_scale_suffix);
            voteAverage.setText(voteAverageText);

            TextView overview = findViewById(R.id.tv_movie_detail_overview);
            overview.setText(this.movie.overview);

            setMovieFavoriteButton();

        } else {
            if (DEBUG) {
                Log.w(TAG, "setMovieDetailContent 517: this.movie is null");
            }
        }
    }

    private void setMovieReviewListFragment() {

        MovieReviewListFragment movieReviewListFragment = getMovieReviewFragment();

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "setMovieReviewListFragment 528: movieReviewListFragment: %s",
                    movieReviewListFragment.toString()));
        }

        if (movieReviewListFragment != null) {

            movieReviewListFragment.setMovieId(this.getMovieId());
            movieReviewListFragment.setMovieTitle(this.getMovieTitle());

            if (DEBUG) {
                Log.d(TAG, String.format("setMovieReviewListFragment 538: movieId = %d",
                        movieReviewListFragment.getMovieId()));
            }
        } else {
            Log.e(TAG, "setMovieReviewListFragment 542: movieId is null");
        }
    }

    private void setMovieVideoListFragment(){

        MovieVideoListFragment movieVideoListFragment = getMovieVideoFragment();

        if (movieVideoListFragment != null) {
           
            movieVideoListFragment.setMovieId(this.getMovieId());
            movieVideoListFragment.setMovieTitle(this.getMovieTitle());

            if (DEBUG) {
                Log.d(TAG, String.format("setMovieVideoListFragment 556: movieId = %d",
                        movieVideoListFragment.getMovieId()));
            }
        } else {
            if (DEBUG) {
                Log.e(TAG,"setMovieVideoListFragment 561: movieId is null");
            }
        }
    }

    private void setMovieFavoriteButton(){

        // Note:Added (Button) to correct error:
        // java.lang.ClassCastException: android.support.v7.widget.AppCompatImageButton
        // cannot be cast to android.widget.Button
        this.movieFavoriteButton = findViewById(R.id.b_movie_detail_favorite);
        if(this.movieFavoriteButton != null){
            this.movieFavoriteButton.setOnClickListener(this);
        }

    }

    private void setMovieFavoriteView(boolean isFavorite){
        if (this.movieFavoriteButton != null) {
            if (isFavorite) {
                this.movieFavoriteButton.setText(R.string.mark_as_not_favorite);

            } else {
                this.movieFavoriteButton.setText(R.string.mark_as_favorite);
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "setMovieFavoriteView 588: favorite image button/s are null");
            }
        }
    }

    /**
     * Show the appropriate fragment according to the selected tab position.
     * @param tab The tab to show.
     */
    private void showFragmentsBySelectedTab(TabLayout.Tab tab) {

        switch (tab.getPosition()) {

            // If reviews tab position ...
            case sTabPositionReviews:
                MovieReviewListFragment movieReviewListFragment = getMovieReviewFragment();
                showMovieReviewList(movieReviewListFragment);
                break;

            // If videos tab position ...
            case sTabPositionVideos:
                MovieVideoListFragment movieVideoListFragment = getMovieVideoFragment();
                showMovieVideoList(movieVideoListFragment);
                break;

            default:
                InvalidParameterException ipe =
                        new InvalidParameterException();
                Log.w(TAG, String.format(
                        "showFragmentsBySelectedTab 617: tabPosition %d not found",
                        tab.getPosition()), ipe);
        }
        if (DEBUG) {
            Log.d(TAG, String.format("showFragmentsBySelectedTab 621: tabPosition = %d",
                    tab.getPosition()));
        }
    }

    private void showMovieReviewList(MovieReviewListFragment fragment){
        // Hide the Movie Video List
        getFragmentManagerHelper().hideFragment(getMovieVideoFragment());

        // Show the Movie Review List
        getFragmentManagerHelper().showFragment(fragment);
    }

    private void showMovieVideoList(MovieVideoListFragment fragment){
        // Hide the Movie Review List
        getFragmentManagerHelper().hideFragment(getMovieReviewFragment());

        // Show the Movie Video List
        getFragmentManagerHelper().showFragment(fragment);
    }

    //endregion private methods
}