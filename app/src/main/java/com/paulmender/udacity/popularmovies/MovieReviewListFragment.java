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

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulmender.android.content.AsyncTaskBundleWrapper;
import com.paulmender.android.content.AsyncTaskLoaderWidgetHelper;
import com.paulmender.udacity.popularmovies.app.BaseMovieExtraFragment;
import com.paulmender.udacity.popularmovies.app.BaseMovieExtraListFragment;
import com.paulmender.udacity.popularmovies.content.MovieDbAsyncTaskLoaderCallbacks;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.paulmender.udacity.popularmovies.widget.MovieDbRecyclerAdapter;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;
import static com.paulmender.udacity.popularmovies.app.
        BaseMovieActivity.INTENT_EXTRA_KEY_MOVIE_REVIEW_PARCELABLE;
import static com.paulmender.udacity.popularmovies.app.BaseMovieActivity.INTENT_EXTRA_KEY_MOVIE_TITLE_STRING;
import static com.paulmender.udacity.popularmovies.app.BaseMovieActivity.MOVIE_ID_NOT_SET;

/**
 * Package: com.paulmender.udacity.popularmovies
 * Project: PopularMovies Stage 2
 *    Name: MovieReviewListFragment
 * Purpose:
 *  Author: Paul on 20171120.
 */
public class MovieReviewListFragment
        extends BaseMovieExtraListFragment<MovieDbAsyncTaskLoaderCallbacks>
            implements MovieDbRecyclerAdapter.MovieDbAdapterOnClickHandler{
    private static final String TAG = "PKMI>MovieReviewList...";

    //region Implements Methods

    // Implement MovieDbAdapterOnClickHandler

    @Override
    public <P extends Parcelable> void onClick(P p) {
        MovieReviewParcelable movieReview = (MovieReviewParcelable) p;

        Context context = getActivity();
        Class destinationClass = MovieReviewActivity.class;

        Intent intent = new Intent(context, destinationClass);
        intent.putExtra(INTENT_EXTRA_KEY_MOVIE_REVIEW_PARCELABLE, movieReview);
        intent.putExtra(INTENT_EXTRA_KEY_MOVIE_TITLE_STRING,getMovieTitle());

        startActivity(intent);
    }

    //endregion Implements

    //region Override Methods

    @Override protected AsyncTaskLoaderWidgetHelper getAsyncTaskLoaderWidgetHelper() {
        return new AsyncTaskLoaderWidgetHelper(
                (TextView) getActivity().findViewById(R.id.tv_fragment_movie_review_list_empty),
                (ProgressBar) getActivity().findViewById(R.id.pb_fragment_movie_review_list),
                (RecyclerView) getActivity().findViewById(R.id.rv_fragment_movie_review_list));
    }

    /**
     * Set the RecyclerView Adapter.
     */
    @Override protected void initMovieDbRecyclerView(){

        // Instantiate layout manager parameter.
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        getActivity(), LinearLayoutManager.VERTICAL, false);

        // Instantiate the recycler view adapter parameter.
        // Note: The MovieDbViewType parameter specifies the RecyclerAdapter view holder layout.
        MovieDbRecyclerAdapter movieDbRecyclerAdapter =
                new MovieDbRecyclerAdapter(this,
                        MovieDbRecyclerAdapter.MovieDbViewType.REVIEW_LIST_ITEM,
                        MOVIE_ID_NOT_SET);

        // Set the recycler view reference, and its adapter.
        setMovieDbRecyclerView(
                (RecyclerView) getActivity().findViewById(R.id.rv_fragment_movie_review_list),
                movieDbRecyclerAdapter);

        if (getMovieDbRecyclerView() != null) {
            // Set the RecyclerView Layout Manager.
            getMovieDbRecyclerView().setLayoutManager(layoutManager);
        } else {
            if (DEBUG) {
                Log.e(TAG, "initMovieDbRecyclerView 114: MovieDbRecyclerView is null");
            }
        }
        if (DEBUG) {
            Log.d(TAG, "initMovieDbRecyclerView 118: *RETURN*");
        }
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the movie query associated with this fragment.
        // Note: It must be set before any calls to getMovieQuery().
        setMovieQuery(MovieDbUtility.MovieQuery.MOVIE_REVIEW_LIST);

        // Note: The view type must be set before getMovieDbViewType()
        setMovieDbViewType(MovieDbRecyclerAdapter.MovieDbViewType.REVIEW_LIST_ITEM);
    }

    @Nullable
    @Override
    public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (container == null){
            if (DEBUG){
                Log.d(TAG, "onCreateView 141: ignored container is null");
            }
        }

        int layoutId = MovieDbRecyclerAdapter.MovieDbViewType.REVIEW_LIST.getLayoutId();

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "onCreateView 1149: MovieDbViewType.REVIEW_LIST.getLayoutId()=%d",layoutId ));
        }

        return inflater.inflate(layoutId, container, true);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AsyncTaskBundleWrapper bundleWrapper = new AsyncTaskBundleWrapper(new Bundle());
        bundleWrapper.setItemId(getMovieId());

        onActivityCreatedInitLoader(
                BaseMovieExtraFragment.LOADER_ID_MOVIE_REVIEW_LIST_FRAGMENT,
                bundleWrapper,
                new MovieDbAsyncTaskLoaderCallbacks(
                        getActivity(),
                        getAsyncTaskLoaderWidgetHelper(),
                        bundleWrapper,
                        getMovieQuery()));

        setData();

        // Override this method to setup the recycler view.
        initMovieDbRecyclerView();
    }

    //endregion Override Methods

    //region Private Methods

    private void setData() {

        // If the reviews are loaded...
        if (getAsyncTaskLoaderCallbacks().isDataSetChanged){
            // ... restart the loader.
            getLoaderManager().restartLoader(
                    LOADER_ID_MOVIE_REVIEW_LIST_FRAGMENT,
                    getAsyncTaskLoaderCallbacks().getLoaderBundle(),
                    getAsyncTaskLoaderCallbacks());

            if (DEBUG) {
                Log.d(TAG, "*RETURN* setData 192: with restartLoader()");
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "*RETURN* setData 196: without restartLoader()");
            }
        }
    }

    //endregion Private Methods
}
