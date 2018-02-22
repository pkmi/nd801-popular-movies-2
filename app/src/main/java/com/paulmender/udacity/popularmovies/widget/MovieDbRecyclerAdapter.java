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

package com.paulmender.udacity.popularmovies.widget;

import android.content.Context;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.paulmender.udacity.popularmovies.MovieParcelable;
import com.paulmender.udacity.popularmovies.MovieReviewParcelable;
import com.paulmender.udacity.popularmovies.MovieVideoParcelable;
import com.paulmender.udacity.popularmovies.R;
import com.paulmender.udacity.popularmovies.utility.MovieDbUtility;
import com.squareup.picasso.Picasso;

import java.util.List;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies.widget
 * Project: PopularMovies
 *    Name: MovieDbRecyclerAdapter
 * Purpose: Adapters provide a binding from an app-specific data set to views
 *          that are displayed within a RecyclerView (1).
 * References:
 *  1. https://developer.android.com/reference/android/support/v7/widget/RecyclerView.Adapter.html
 *  2. https://guides.codepath.com/android/using-the-recyclerview#creating-the-recyclerview-adapter.
 *  3. https://guides.codepath.com/android/Heterogenous-Layouts-inside-RecyclerView
 *  4. https://stackoverflow.com/questions/27809524/recyclerview-not-call-oncreateviewholder
 *           answered Jan 7 '15 at 0:24, Konstantin Burov.
 * History:
 *  20171118 Implement heterogeneous layouts for Stage 2.
 *  20171208 Renamed setMovieData to setData.
 *           Refactor MovieDbAdapterOnClickHandler to MovieDbAdapterOnClickHandler.
 */

public class MovieDbRecyclerAdapter extends
        RecyclerView.Adapter<MovieDbRecyclerAdapter.MovieDbViewHolder>{

    private static final String TAG = "PKMI>MovieDbRecycler...";

    //region Non-public, non-static fields

    // A generic reference to list of the various movie-related parcelables
    // populated from the MovieDB API.
    private List<? extends Parcelable> parcelablesList;

    private final MovieDbAdapterOnClickHandler clickHandler;

    private final MovieDbViewType movieDbViewType;

    private int movieId = movieIdNotSet;

    private int getItemCountCallCountForDebug = 0;

    //endregion Non-public, non-static fields

    //region Private static fields

    private static final int layOutUnknown = -1;

    private static final int movieIdNotSet = -1;

    //endregion Private static fields

    //region Getters and Setters

    // movieId
    private int getMovieId() {
        return this.movieId;
    }
    private void setMovieId(int movieId) {
        this.movieId = movieId;
    }

    //endregion Getters and Setters

    //region Constructors
    /**
     * Constructor
     * @param clickHandler Specifies a reference to the click handler.
     * @param movieDbViewType Specifies the view type.
     */
    public MovieDbRecyclerAdapter(
            MovieDbAdapterOnClickHandler clickHandler,
            @NonNull MovieDbViewType movieDbViewType,
            int movieId) {

        this.clickHandler = clickHandler;
        this.movieDbViewType = movieDbViewType;
        this.movieId = movieId;

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "Constructor 123: *FINISHED* movieDbViewType = %s, movieId = %d",
                    movieDbViewType.toString(),this.movieId));
        }
    }

    //endregion Constructors

    //region enums

    /**
     * Purpose: Maps the view type to its associated layout to help the MovieDbRecyclerAdapter
     *          manage several layouts for the recycler views used across various contexts.
     * Parameters:
     *      ViewType, Layout
     */
    public enum MovieDbViewType {
        MOVIE_POSTER(0, R.layout.movie_poster),
        FAVORITE(1, layOutUnknown),
        REVIEW(2, R.layout.activity_movie_review),
        REVIEW_LIST(3, R.layout.fragment_movie_review_list),
        REVIEW_LIST_ITEM(4, R.layout.fragment_movie_review_list_item),
        VIDEO(5, R.layout.activity_movie_video),
        VIDEO_LIST(6, R.layout.fragment_movie_video_list),
        VIDEO_LIST_ITEM(7, R.layout.fragment_movie_video_list_item),
        UNKNOWN(-1,-1);

        private final int viewType;
        private final int layoutId;

        public static final int LAYOUT_UNKNOWN = layOutUnknown;

        //region Getters and Setters

        @SuppressWarnings("unused")
        public int getViewType() {
            return viewType;
        }

        public int getLayoutId() {
            return layoutId;
        }

        //endregion Getters and Setters

        MovieDbViewType(int viewType, int layoutId) {
            this.viewType = viewType;
            this.layoutId = layoutId;
         }

        public static int getLayoutId(int viewType) {

            for (MovieDbViewType movieDbViewType : MovieDbViewType.values()) {
                if (movieDbViewType.viewType == viewType) {
                    return movieDbViewType.layoutId;
                }
            }

            if (DEBUG) {
                Log.w(TAG, "getLayoutId 175: viewType unknown");
            }

            return LAYOUT_UNKNOWN;
        }

        @Nullable
        public static MovieDbViewType getEnumerator(int viewType) {

            for (MovieDbViewType movieDbViewType : MovieDbViewType.values()) {
                if (movieDbViewType.viewType == viewType) {
                    return movieDbViewType;
                }
            }

            if (DEBUG) {
                Log.w(TAG, "getEnumerator 191: specified viewType not found");
            }

            return null;

        }
    }

    public enum SetViewOption{
        ON_CLICK,
        ON_BIND
    }

    // endregion enums

    //region inner classes

    /**
     * The view cache of the MovieDb items.
     * History:
     *   In Stage 1, removed static from the class definition to fix not displaying
     *   data in the main activity.
     */
    public class MovieDbViewHolder extends RecyclerView.ViewHolder
            implements View.OnClickListener {

        //region private fields

        private final Context context;

        private final View view;

        // Movie Poster views
        private ImageView moviePosterImageView;

        //endregion private fields

        /**
         * Called when the child views (in the ViewHolder) are clicked.
         * @param view The View that was clicked.
         */
        @Override
        public void onClick(View view) {
            if (getAdapterPosition() == RecyclerView.NO_POSITION) {
                if (DEBUG) {
                    Log.w(TAG, String.format("onClick 246: getAdapterPosition() = NO_POSITION,"+
                            " onClick(%s) not set.",view.getClass().getSimpleName()));
                }
            } else {
                // Call the view's onClick().
                setView(SetViewOption.ON_CLICK);
            }
        }

        // Constructor
        private MovieDbViewHolder(View view) {
            super(view);

            context = view.getContext();

            this.view = view;

            this.view.setOnClickListener(this);

            if (DEBUG) {
                Log.d(TAG, String.format("*Constructor* MovieDbViewHolder 256: %d",
                        this.getItemId()));
            }
        }

        public void setView(SetViewOption setViewOption) {
            switch (movieDbViewType) {

                case MOVIE_POSTER:
                    setMoviePoster(setViewOption);
                    break;

                case REVIEW_LIST_ITEM:
                    setMovieReviewListItem(setViewOption);
                    break;

                case VIDEO_LIST_ITEM:
                    setMovieVideoListItem(setViewOption);
                    break;

                case UNKNOWN:
                    // This case should never happen, movieDbViewType is set in the constructor.
                    if (DEBUG) {
                        Log.w(TAG, String.format(
                                "Invalid parameter 280: %s",movieDbViewType.name()));
                    }
                    break;
            }
        }

        //region Set Individual View methods

        private void setMoviePoster(SetViewOption setViewOption){

            int position = getAdapterPosition();
            MovieParcelable movie = (MovieParcelable) parcelablesList.get(position);

            switch (setViewOption) {
                case ON_BIND:

                    // Set the view content.
                    moviePosterImageView = this.itemView.findViewById(R.id.iv_movie_poster);

                    boolean isSelected = (movie.movieId == getMovieId());
                    // If the view is selected ...
                    if (isSelected) {
                        // ... accent the background.
                        moviePosterImageView.setBackground(
                                context.getResources().getDrawable(
                                        R.drawable.movie_poster_item_touch_selector_selected));
                    } else {
                        // ... not selected, so reset the background.
                        moviePosterImageView.setBackground(
                                context.getResources().getDrawable(
                                R.drawable.movie_poster_item_touch_selector));

                    }

                    if (DEBUG) {
                        Log.d(TAG, String.format("setMoviePoster 315 ON_BIND: "+
                                        "getMovieId() <%d> | movie.movieId <%d> | %s",
                                getMovieId(),
                                movie.movieId,
                                isSelected ? "*ACCENT*" : null));
                    }

                    String loadImage =  MovieDbUtility.getImageUrlString(movie.posterPath);
                    //COMPLETED: Add error() per Stage 1 code review.
                    // Note: Did not add the placeholder() as suggested because it was distracting
                    // when the movies posters loaded.
                    Picasso.with(context)
                            .load(loadImage)
                            .error(R.drawable.ic_movie_poster_error)
                            .into(moviePosterImageView);

                    break;

                case ON_CLICK:

                    // Call the clickHandler.onClick().
                    clickHandler.onClick(movie);

                    break;
            }
        }

        private void setMovieReviewListItem(SetViewOption setViewOption){
            int position = getAdapterPosition();

            MovieReviewParcelable movieReview = (MovieReviewParcelable) parcelablesList.get(position);

            switch (setViewOption) {
                case ON_BIND:

                    TextView reviewAuthorTextView =
                            this.view.findViewById(R.id.tv_movie_review_list_author);
                    reviewAuthorTextView.setText(movieReview.author);

                    break;

                case ON_CLICK:
                    // Call the clickHandler.onClick().
                    clickHandler.onClick(movieReview);
                    break;
            }
        }
        
        private void setMovieVideoListItem(SetViewOption setViewOption){
            int position = getAdapterPosition();

            MovieVideoParcelable movieVideo = (MovieVideoParcelable) parcelablesList.get(position);

            switch (setViewOption) {
                case ON_BIND:
                    // Set the view content.
                    TextView videoNameTextView =
                            this.view.findViewById(R.id.tv_movie_video_item_name);
                    videoNameTextView.setText(movieVideo.name);

                    ImageView videoShare =
                            this.view.findViewById(R.id.iv_movie_video_share);
                    videoShare.setTag(movieVideo.key);

                    ImageView videoPlay =
                            this.view.findViewById(R.id.iv_movie_video_play_icon);
                    videoPlay.setTag(movieVideo.key);
                    break;

                case ON_CLICK:
                    // Call the clickHandler.onClick().
                    if (this.view.getTag() != null){
                        if (DEBUG) {
                            Log.d(TAG, String.format(
                                    "setMovieVideoListItem 388: this.view.getTag() = %s",
                                    this.view.getTag()));
                        }
                    }

                    clickHandler.onClick(movieVideo);

                    break;
            }
        }

        //endregion
    }

    //endregion inner classes

    //region Interfaces

    // Interfaces for receiving click messages from activities and fragments
    // to communicate to the RecyclerView onClick().

    public interface MovieDbAdapterOnClickHandler {
         <P extends Parcelable> void onClick(P p);
    }

    //endregion Interfaces

    //region Override methods

    /**
     * This gets called when each new ViewHolder is created. This happens when the RecyclerView
     * is laid out. Enough ViewHolders will be created to fill the screen and allow for scrolling.
     *
     * @param viewGroup The ViewGroup that these ViewHolders are contained within.
     * @param viewType  Note: If the RecyclerView has more than one type of item,
     *                  use this viewType integer to provide a different layout. See
     *                  {@link android.support.v7.widget.RecyclerView.Adapter#getItemViewType(int)}
     *                  for more details.
     * @return A new MovieDbViewHolder that holds the View for each movie poster item.
     */
    @Override
    public MovieDbViewHolder onCreateViewHolder(ViewGroup viewGroup, int viewType) {

        String movieDbViewTypeName;

        MovieDbViewType movieDbViewType = MovieDbViewType.getEnumerator(viewType);
        if (movieDbViewType != null) {

            movieDbViewTypeName = movieDbViewType.name();

            if (DEBUG) {
                Log.d(TAG, String.format(
                        "onCreateViewHolder 447: *START* view type = %s",
                        movieDbViewTypeName));
            }

            // Create a new view based on the specified view type.
            int layoutId = MovieDbViewType.getLayoutId(viewType);

            if (viewType != MovieDbViewType.LAYOUT_UNKNOWN) {

                Context context = viewGroup.getContext();
                LayoutInflater inflater = LayoutInflater.from(context);
                final boolean attachToViewGroupImmediately = false;

                View view = inflater.inflate(layoutId, viewGroup, attachToViewGroupImmediately);
                view.setFocusable(true);

                if (DEBUG) {
                    Log.d(TAG, String.format(
                            "onCreateViewHolder 465: *RETURN* new MovieDbViewHolder(%s)"
                            , movieDbViewTypeName));
                }

                return new MovieDbViewHolder(view);
            }
        } else {

            if (DEBUG) {
                Log.w(TAG, String.format(
                        "onCreateViewHolder 475: Unknown layout for view type = %d", viewType));
            }
        }
        return null;
    }

    /**
     * OnBindViewHolder is called by the RecyclerView to display the data at the specified
     * position. Update the contents of the ViewHolder to display the movie
     * for this particular position.
     *
     * @param movieDbViewHolder The ViewHolder which should be updated to represent the
     *               contents of the item at the given position in the data set.
     * @param position The position of the item within the adapter's data set.
     */
    @Override
    public void onBindViewHolder(MovieDbViewHolder movieDbViewHolder, int position) {
        try {

            MovieDbViewType movieDbViewType =
                    MovieDbViewType.getEnumerator(movieDbViewHolder.getItemViewType());

            if (movieDbViewType != null){
                movieDbViewHolder.setView(SetViewOption.ON_BIND);
            } else {
                if (DEBUG) {
                    Log.e(TAG, "onBindViewHolder 493: movieDbViewType is null");
                }
            }
        }
        catch (Exception e)
        {
            Log.e(TAG, "onBindViewHolder 499: ", e);
        }
    }

    @Override
    public int getItemViewType(int position){
        return movieDbViewType.viewType;
    }

    @Override
    public int getItemCount() {

        // Note: If getItemCount returns 0, onCreateViewHolder() will not called so
        //       nothing will be displayed in the view.
        int count = 0;

        if (this.parcelablesList != null){
            count = this.parcelablesList.size();
        }

        if (DEBUG) {

            getItemCountCallCountForDebug++ ;

            int getItemCountCallCountStopLogAtForDebug = 2;
            if (getItemCountCallCountForDebug < getItemCountCallCountStopLogAtForDebug) {

                String movieDbViewTypeName;

                if (movieDbViewType == null) {
                    movieDbViewTypeName = " is null";
                } else {
                    movieDbViewTypeName = "."+movieDbViewType.name();
                }

                Log.w(TAG, String.format(
                        "getItemCount 535: movieDbViewType%s: count = %d: "+
                        "...CallCountForDebug = %d",
                        movieDbViewTypeName, count, getItemCountCallCountForDebug));
            }

            // Used this to help determine what calls getItemCount() so many times.
            //try{
            //    throw new Exception("PKMI throw debug stack trace 486");
            //} catch (Exception e) {
            //    Log.d(TAG,String.format("%s",e.getMessage()));
            //    e.printStackTrace();
            //}

        }

        return count;
    }

    //endregion Override methods

    //region public methods

    /**
     * Purpose: This method is used to set the parcelables data if the RecyclerAdapter
     *          already exists. This allows a new data query without the need to create
     *          a new RecyclerAdapter to display it. (1)
     * History:
     *  20171222 Confirmed: Always notify data set changed regardless of list size.
     * References:
     *  1. Udacity S03.01-Solution-RecyclerView.

     * @param parcelables The new movies to be displayed.
     */
    public void setData(List<? extends Parcelable> parcelables, int selectedItemId) {

        String parcelablesClassName = null;

        if (parcelables != null) {
            if (parcelables.size() > 0) {

                parcelablesClassName = parcelables.get(0).getClass().getSimpleName();

                if (DEBUG) {
                    Log.d(TAG, String.format(
                            "*START* setData 579: %s.size = %d: selectedItemId = %d",
                            parcelablesClassName, parcelables.size(), selectedItemId));
                }
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "*START* setData 585: parcelables is null");
            }
        }

        if (parcelablesClassName != null) {
            if (parcelables.get(0).getClass() == MovieParcelable.class) {

                // Used to highlight the selected movie in the movie poster grid.
                setMovieId(selectedItemId);
            }
        }

        this.parcelablesList = parcelables;

        notifyDataSetChanged();

        if (DEBUG) {
            Log.d(TAG,"*FINISH* setData 602: notifyDataSetChanged()");
        }
    }

    //endregion public methods
}
