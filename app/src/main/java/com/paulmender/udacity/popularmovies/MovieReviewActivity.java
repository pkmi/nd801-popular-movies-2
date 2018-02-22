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

import android.os.Bundle;
import android.util.Log;
import android.widget.TextView;

import com.paulmender.udacity.popularmovies.app.BaseMovieActivity;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies
 * Project: PopularMovies Stage 2
 *    Name: MovieReviewActivity
 * Purpose: View a movie review.
 *  Author: Paul on 20171120.
 */
public class MovieReviewActivity extends BaseMovieActivity {

    private static final String TAG = "PKMI>MovieReviewAc...";
    
    private MovieReviewParcelable movieReview;

    private String movieTitle;

    //region Implement Abstract Methods

    public void onCreateSetContent(){
        setMovieReviewContent();
    }

    //endregion

    //region Override Methods

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(Layout.MOVIE_REVIEW.layoutId);

        setDataSource();

        setMovieReviewContent();
    }

    //endregion Override Methods

    //region Private Methods
    private void setDataSource(){
        this.movieTitle = getIntent().getStringExtra(INTENT_EXTRA_KEY_MOVIE_TITLE_STRING);
        this.movieReview = getIntent().getParcelableExtra(INTENT_EXTRA_KEY_MOVIE_REVIEW_PARCELABLE);
    }

    private void setMovieReviewContent(){

        if (this.movieReview != null) {

            TextView Title = findViewById(R.id.tv_include_movie_extra_title);
            TextView TitleScroll = findViewById(R.id.tv_movie_review_title_scrolled);

            TextView authorTextView = findViewById(R.id.tv_movie_review_author);
            TextView contentTextView = findViewById(R.id.tv_movie_review_content);

            try {
                Title.setText(this.movieTitle);
                TitleScroll.setText(this.movieTitle);

                authorTextView.setText(this.movieReview.author);
                contentTextView.setText(this.movieReview.content);
            } catch (NullPointerException npe){
                if (DEBUG) {
                    Log.d(TAG,
                            "setMovieReviewContent 89: ",npe);
                }
            }
        } else {
            if (DEBUG) {
                Log.w(TAG,
                        "setMovieReviewContent 95: this.movieReview is null cannot set content");
            }
        }
    }
    //endregion
}
