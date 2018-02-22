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
 * Name: MovieVideoParcelable
 * Purpose: Message container for a movie video.
 * Author: Paul on 20170927.
 */
public class MovieVideoParcelable implements Parcelable {

    // Properties
    public int movieId;
    public String videoId;
    public String iso_639_1;
    public String iso_3166_1;
    public String key;
    public String name;
    public String site;
    public int size;
    public String type;

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
        dest.writeString(this.videoId);
        dest.writeString(this.iso_639_1);
        dest.writeString(this.iso_3166_1);
        dest.writeString(this.key);
        dest.writeString(this.name);
        dest.writeString(this.site);
        dest.writeInt(this.size);
        dest.writeString(this.type);
    }

    public MovieVideoParcelable() {
    }

    /**
     * Note: The read (in.*) and write (dest.*) order of values must match.
     */
    private MovieVideoParcelable(Parcel in) {
        this.movieId = in.readInt();
        this.videoId = in.readString();
        this.iso_639_1 = in.readString();
        this.iso_3166_1 = in.readString();
        this.key = in.readString();
        this.name = in.readString();
        this.site = in.readString();
        this.size = in.readInt();
        this.type = in.readString();
    }

    @SuppressWarnings("unused")
    public static final Creator<MovieVideoParcelable> CREATOR =
            new Creator<MovieVideoParcelable>() {
        @Override
        public MovieVideoParcelable createFromParcel(Parcel source) {
            return new MovieVideoParcelable(source);
        }

        @Override
        public MovieVideoParcelable[] newArray(int size) {
            return new MovieVideoParcelable[size];
        }
    };
}
