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

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ShareCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.android.youtube.player.YouTubeInitializationResult;
import com.google.android.youtube.player.YouTubeStandalonePlayer;
import com.paulmender.android.content.AsyncTaskBundleWrapper;
import com.paulmender.android.content.AsyncTaskLoaderWidgetHelper;
import com.paulmender.udacity.popularmovies.app.BaseMovieExtraFragment;
import com.paulmender.udacity.popularmovies.app.BaseMovieExtraListFragment;
import com.paulmender.udacity.popularmovies.content.MovieDbAsyncTaskLoaderCallbacks;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.paulmender.udacity.popularmovies.widget.MovieDbRecyclerAdapter;
import com.paulmender.utility.VideoPlayerExternal;

import java.util.List;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;
import static com.paulmender.udacity.popularmovies.app.BaseMovieActivity.MOVIE_ID_NOT_SET;

/**
 * Package: com.paulmender.udacity.popularmovies
 * Project: PopularMovies Stage 2
 *    Name: MovieVideoListFragment
 * Purpose: Display the videos list for a particular movie.
 *  Author: Paul on 20171120.
 * References:
 *  1. https://stackoverflow.com/questions/24832497/
 *          avoid-passing-null-as-the-view-root-need-to-resolve-layout-parameters-on-the-in
 *  2. https://possiblemobile.com/2013/05/layout-inflation-as-intended/
 *          Larry Schiefer Dec 8 '14 at 0:58
 *  3. com.examples.youtubeapidemo;
 *     https://developers.google.com/youtube/android/player/
 *  4. Research regarding error:
 *      "Activity com.google.android.youtube.api.StandalonePlayerActivity has leaked IntentReceiver
 *      aahs@caf2657 that was originally registered here.
 *      Are you missing a call to unregisterReceiver()?"
 *     Found references, but none of the solutions offered solved the issue.:
 *     https://stackoverflow.com/questions/47501857/intentreceiverleaked-youtubestandaloneplayer
 *     https://stackoverflow.com/questions/35678972/
 *             error-youtubeservice-has-leaked-intentreceiver-are-you-missing-a-call-to-un
 */
public class MovieVideoListFragment
        extends BaseMovieExtraListFragment<MovieDbAsyncTaskLoaderCallbacks>
            implements
                    MovieDbRecyclerAdapter.MovieDbAdapterOnClickHandler,
                    View.OnClickListener {

    private static final String TAG = "PKMI>MovieVideoListF...";

    private static final int reqResolveServiceMissing = 2;

    private static final int requestCreateVideo = 1002;

    private Intent createVideoIntent = null;

    //region Implements Methods

    @Override
    public <P extends Parcelable> void onClick(P p) {
        MovieVideoParcelable movieVideo = (MovieVideoParcelable) p;

        if (movieVideo.key != null) {
            startVideo(movieVideo.key);
        }
    }

    // View.onClickListener
    @Override
    public void onClick(View view) {
        onClickMovieVideoShare(view);
    }

    //endregion Implements Methods

    //region Override Methods

    @Override protected AsyncTaskLoaderWidgetHelper getAsyncTaskLoaderWidgetHelper() {
        return new AsyncTaskLoaderWidgetHelper(
                    (TextView) getActivity().findViewById(R.id.tv_fragment_movie_video_list_empty),
                    (ProgressBar) getActivity().findViewById(R.id.pb_fragment_movie_video_list),
                    (RecyclerView) getActivity().findViewById(R.id.rv_fragment_movie_video_list));
    }

    /**
     * Set the RecyclerView Adapter.
     */
    @Override protected void initMovieDbRecyclerView() {

        // Instantiate layout manager parameter.
        LinearLayoutManager layoutManager =
                new LinearLayoutManager(
                        getActivity(), LinearLayoutManager.VERTICAL, false);

        // Instantiate the recycler view adapter parameter.
        // Note: The MovieDbViewType parameter specifies the RecyclerAdapter view holder layout.
        MovieDbRecyclerAdapter movieDbRecyclerAdapter =
                new MovieDbRecyclerAdapter(this,
                        MovieDbRecyclerAdapter.MovieDbViewType.VIDEO_LIST_ITEM,
                        MOVIE_ID_NOT_SET);

        // Set the recycler view reference, and its adapter.
        setMovieDbRecyclerView(
                (RecyclerView) getActivity().findViewById(R.id.rv_fragment_movie_video_list),
                movieDbRecyclerAdapter);

        if (getMovieDbRecyclerView() != null) {
            // Set the RecyclerView Layout Manager.
            getMovieDbRecyclerView().setLayoutManager(layoutManager);
        }

        if (DEBUG) {
            Log.d(TAG, "initMovieDbRecyclerView 143: *RETURN*");
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        AsyncTaskBundleWrapper bundleWrapper = new AsyncTaskBundleWrapper(new Bundle());
        bundleWrapper.setItemId(getMovieId());

        onActivityCreatedInitLoader(
                BaseMovieExtraFragment.LOADER_ID_MOVIE_VIDEO_LIST_FRAGMENT,
                null,
                new MovieDbAsyncTaskLoaderCallbacks(
                        getActivity(),
                        getAsyncTaskLoaderWidgetHelper(),
                        bundleWrapper,
                        getMovieQuery()));

        setData();

        // Override this method to setup the recycler view.
        initMovieDbRecyclerView();
    }

    public void onClickMovieVideoPlay(View view) {
        if (DEBUG) {
            Log.d(TAG, String.format("*START* onClickMovieVideoPlay 171: getTag() = %s", getTag()));
        }
        String videoKey = view.getTag().toString();
        if (videoKey != null) {
            startVideo(videoKey);
        }
    }

    public void onClickMovieVideoShare(View view) {
        if (DEBUG) {
            Log.d(TAG, String.format("*START* onClickMovieVideoShare 181: getTag() = %s",getTag()));
        }

        String mimeType = "text/plain";

        String videoKey = (String) view.getTag();
        if (videoKey != null){

            // Create the title for the chooser window.
            String title = getActivity().getResources().getString(R.string.movie_video_share_title);

            Uri movieVideoUri = VideoPlayerExternal.getYouTubeUrl(videoKey);

            // Build the Intent and start the chooser.
            ShareCompat.IntentBuilder
                    .from(this.getActivity())
                    .setType(mimeType)
                    .setChooserTitle(title)
                    .setText(movieVideoUri.toString())
                    .startChooser();
        }
    }

    @Override public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the movie query associated with this fragment.
        // Note: It must be set before any calls to getMovieQuery().
        setMovieQuery(MovieDbUtility.MovieQuery.MOVIE_VIDEO_LIST);

        // Note: The view type must be set before getMovieDbViewType()
        setMovieDbViewType(MovieDbRecyclerAdapter.MovieDbViewType.VIDEO_LIST_ITEM);
    }

    @Nullable
    @Override public View onCreateView(
            LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {

        if (container == null){
            if (DEBUG){
                Log.d(TAG, "onCreateView 221: ignored container is null");
            }
        }

        int layoutId = MovieDbRecyclerAdapter.MovieDbViewType.VIDEO_LIST.getLayoutId();

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "onCreateView 229: MovieDbViewType.VIDEO_LIST.getLayoutId()=%d",layoutId ));
        }

        return inflater.inflate(layoutId, container, true);
    }

    @Override public void onPause(){
        unRegisterCreateVideoReceiver();
        super.onPause();
    }

    //endregion Override Methods

    //region Private Methods

    private void setData(){
        // If the reviews are loaded...
        if (getAsyncTaskLoaderCallbacks().isDataSetChanged){
            // ... restart the loader.
            getLoaderManager().restartLoader(
                    LOADER_ID_MOVIE_VIDEO_LIST_FRAGMENT,
                    getAsyncTaskLoaderCallbacks().getLoaderBundle(),
                    getAsyncTaskLoaderCallbacks());

            if (DEBUG) {
                Log.d(TAG, "*RETURN* setData 254: with restartLoader()");
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "*RETURN* setData 258: without restartLoader()");
            }
        }
    }

    private void startVideo(@NonNull String videoKey) {

        // Deprecated. See comments MovieVideoActivity.java. (if available):
        // Intent intent = new Intent(getActivity(),MovieVideoActivity.class);
        // intent.putExtra(INTENT_EXTRA_KEY_MOVIE_VIDEO_PARCELABLE,movieVideo);
        // return;

        // PENDING: See Reference 4. above. ...1/20/2018
        // Fix: Activity com.google.android.youtube.api.StandalonePlayerActivity
        // has leaked IntentReceiver aahp@41d4774 that was originally registered here.
        // Are you missing a call to unregisterReceiver()?

        this.createVideoIntent = YouTubeStandalonePlayer.createVideoIntent(
                getActivity(), BuildConfig.YOUTUBE_API_KEY, videoKey);

        if (this.createVideoIntent != null) {

            if (canResolveCreateVideoIntent(this.createVideoIntent)) {

                getActivity().startActivityForResult(this.createVideoIntent,requestCreateVideo);

                YouTubeInitializationResult youTubeInitializationResult =
                        YouTubeStandalonePlayer.getReturnedInitializationResult(
                                this.createVideoIntent);
                switch (youTubeInitializationResult) {
                    case DEVELOPER_KEY_INVALID:
                        if (DEBUG) {
                            Log.d(TAG,"startVideo 290: YouTube developer key is invalid.");
                        }
                        break;
                    // Could add more cases here.
                }

            } else {
                // Could not resolve the intent, so user must install
                // or update the YouTube API service.
                YouTubeInitializationResult.SERVICE_MISSING
                        .getErrorDialog(getActivity(), reqResolveServiceMissing).show();
            }
        }
    }

    private boolean canResolveCreateVideoIntent(Intent intent) {
        List<ResolveInfo> resolveInfo =
                getActivity().getPackageManager().queryIntentActivities(intent, 0);
        return resolveInfo != null && !resolveInfo.isEmpty();
    }

    private void unRegisterCreateVideoReceiver() {
        // PENDING: See Reference 4. above.
        this.createVideoIntent = null;
    }

    //endregion Private Methods
}
