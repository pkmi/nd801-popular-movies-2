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

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Package: com.paulmender.udacity.popularmovies
 * Project: PopularMovies Stage 2
 * Name: MovieReviewParcelable
 * Purpose: Message container for a movie review.
 * Author: Paul on 20170927.
 */
public class MovieReviewParcelable implements Parcelable {

    // Properties
    public int movieId;
    public String reviewId;
    public String author;
    public String content;
    public String url;

    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Note: The read (in.*) and write (dest.*) order of values must match.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.movieId);
        dest.writeString(this.reviewId);
        dest.writeString(this.author);
        dest.writeString(this.content);
        dest.writeString(this.url);
    }

    public MovieReviewParcelable() {
    }

    /**
     * Note: The read (in.*) and write (dest.*) order of values must match.
     */
    private MovieReviewParcelable(Parcel in) {
        this.movieId = in.readInt();
        this.reviewId = in.readString();
        this.author = in.readString();
        this.content = in.readString();
        this.url = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Creator<MovieReviewParcelable> CREATOR =
            new Creator<MovieReviewParcelable>() {
        @Override
        public MovieReviewParcelable createFromParcel(Parcel source) {
            return new MovieReviewParcelable(source);
        }

        @Override
        public MovieReviewParcelable[] newArray(int size) {
            return new MovieReviewParcelable[size];
        }
    };
}
