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
package com.paulmender.udacity.popularmovies.utility;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.google.gson.Gson;

import com.paulmender.udacity.popularmovies.MovieParcelable;
import com.paulmender.udacity.popularmovies.MovieVideoParcelable;
import com.paulmender.udacity.popularmovies.MovieReviewParcelable;
import com.paulmender.udacity.popularmovies.data.MovieDbContract;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.List;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Utilities to manage the MovieDb JSON data.
 * References:
 *   Udacity SO3.01-Solution-RecyclerView, OpenWeatherJsonUtils
 * Created by Paul on 8/28/2017.
 * History:
 *  20171117 Added runtime for Stage 2.
 *  20180124 Added getMovieDbStringsFromJson(MovieDbUtility.MovieQuery movieQuery) for Stage 2
 *           to show locally stored movie favorites on the Favorites tab.
 */
public final class MovieDbJsonUtility {

    private static final String TAG = "PKMI>MovieDbJsonUtility";

    public static final int MOVIE_ID_NOT_SET = -1;

    //region private static fields

    private static final String sMovieDbJsonErrorCode = "cod";
    private static final String sMovieDbJsonArrayMapName = "results";

    //endregion private static fields

    //region public methods

    /**
     * Parse the JSON response from the MovieDB API.
     *
     * @param movieDbJsonString response from server.
     * @return List of movie parcelables.
     * @throws JSONException If JSON data cannot be parsed.
     */
    public static List<MovieParcelable>
            getMovieDbStringsFromJson(String movieDbJsonString)
            throws JSONException {

        List<MovieParcelable> parsedMovies = new ArrayList<>();
        JSONObject movieJson = new JSONObject(movieDbJsonString);

        if (isValidJsonObject(movieJson)){
            JSONArray movieJsonArray = movieJson.getJSONArray(sMovieDbJsonArrayMapName);
            parsedMovies = MovieDbJsonUtility.getMovieDbStringsFromJson(movieJsonArray);
        }

        if (DEBUG) {
            Log.d(TAG, String.format("getMovieDbStringsFromJson(%s)", movieDbJsonString));
        }

        return parsedMovies;
    }

    /**
     * Parse the JSON response for MovieDB Videos from the MovieDB API.
     *
     * @param movieDbJsonString response from server.
     * @return List of movie video parcelables.
     * @throws JSONException If JSON data cannot be parsed.
    */
    public static List<MovieVideoParcelable>
            getMovieDbVideoStringsFromJson(String movieDbJsonString, int movieId)
            throws JSONException {

        List<MovieVideoParcelable> parsedMovieVideos = new ArrayList<>();
        JSONObject movieJson = new JSONObject(movieDbJsonString);
        if (DEBUG) {
            Log.d(TAG, String.format("getMovieDbVideoStringsFromJson 103: movieJson: %s", movieJson));
        }
        if (isValidJsonObject(movieJson)){

            try {

                JSONArray movieJsonArray = movieJson.getJSONArray(sMovieDbJsonArrayMapName);
                if (DEBUG) {
                    Log.d(TAG, String.format("getMovieDbVideoStringsFromJson 111: movieJsonArray: %s",
                            movieJsonArray));
                }

                for (int i = 0; i < movieJsonArray.length(); i++) {

                    JSONObject movieVideo = movieJsonArray.getJSONObject(i);

                    MovieVideoParcelable movieVideoParcelable = new MovieVideoParcelable();

                    movieVideoParcelable.movieId = movieId;
                    movieVideoParcelable.videoId =
                            movieVideo.getString(MovieDbContract.MOVIE_DB_VIDEOS_ID);
                    movieVideoParcelable.iso_639_1 =
                            movieVideo.getString(MovieDbContract.MOVIE_DB_VIDEOS_ISO_639_1);
                    movieVideoParcelable.iso_3166_1 =
                            movieVideo.getString(MovieDbContract.MOVIE_DB_VIDEOS_ISO_3166_1);
                    movieVideoParcelable.key =
                            movieVideo.getString(MovieDbContract.MOVIE_DB_VIDEOS_KEY);
                    movieVideoParcelable.name =
                            movieVideo.getString(MovieDbContract.MOVIE_DB_VIDEOS_NAME);
                    movieVideoParcelable.site =
                            movieVideo.getString(MovieDbContract.MOVIE_DB_VIDEOS_SITE);
                    movieVideoParcelable.size =
                            movieVideo.getInt(MovieDbContract.MOVIE_DB_VIDEOS_SIZE);
                    movieVideoParcelable.type =
                            movieVideo.getString(MovieDbContract.MOVIE_DB_VIDEOS_TYPE);

                    // Add the populated parcelable to the list.
                    parsedMovieVideos.add(i, movieVideoParcelable);
                }
            } catch (Exception e){
                Log.e(TAG, "getMovieDbVideoStringsFromJson 143: ",e );
            }
        } else {
            if (DEBUG){
                Log.w(TAG, String.format(
                        "getMovieDbVideoStringsFromJson 148: !isValidJsonObject movieJson = %s"
                        ,movieJson));
            }
        }

        return parsedMovieVideos;
    }

    /**
     * Parse the JSON response for MovieDB Reviews from the MovieDB API.
     *
     * @param movieDbJsonString response from server.
     * @return List of movie review parcelables.
     * @throws JSONException If JSON data cannot be parsed.
     */
    public static List<MovieReviewParcelable>
            getMovieDbReviewStringsFromJson(String movieDbJsonString, int movieId)
            throws JSONException {

        List<MovieReviewParcelable> parsedMovieReviews = new ArrayList<>();

        JSONObject movieJson = new JSONObject(movieDbJsonString);
        if (isValidJsonObject(movieJson)) {

            JSONArray movieJsonArray = movieJson.getJSONArray(sMovieDbJsonArrayMapName);
            for (int i = 0; i < movieJsonArray.length(); i++) {

                JSONObject movieReview = movieJsonArray.getJSONObject(i);

                MovieReviewParcelable movieReviewParcelable = new MovieReviewParcelable();

                movieReviewParcelable.movieId = movieId;
                movieReviewParcelable.reviewId =
                        movieReview.getString(MovieDbContract.MOVIE_DB_REVIEW_ID);
                movieReviewParcelable.author =
                        movieReview.getString(MovieDbContract.MOVIE_DB_REVIEW_AUTHOR);
                movieReviewParcelable.content =
                        movieReview.getString(MovieDbContract.MOVIE_DB_REVIEW_CONTENT);
                movieReviewParcelable.url =
                        movieReview.getString(MovieDbContract.MOVIE_DB_REVIEW_URL);

                parsedMovieReviews.add(i, movieReviewParcelable);
            }
        }
        return parsedMovieReviews;
    }

    public static List<MovieParcelable> getMovieParcelableList(String json) {
        List<MovieParcelable> returnParcelables = new ArrayList<>();
        try {

            MovieParcelable parcelable;
            Gson gson = new Gson();
            parcelable = gson.fromJson(json, MovieParcelable.class);
            returnParcelables.add(parcelable);

        } catch (Exception e) {
            Log.e(TAG, String.format("getParcelableList(%s) 205: ", json), e);
        }

        return returnParcelables;
    }

    public static String getMovieJson(MovieParcelable movie){

        String json = null;

        if (movie != null){
            Gson gson = new Gson();
            json = gson.toJson(movie);
        }

        return json;
    }

    public static MovieParcelable getMovieFromJson(@NonNull String json){
        Gson gson = new Gson();
        return gson.fromJson(json, MovieParcelable.class);
    }

    public static String getMovieParcelableValue(
            MovieParcelable movie, String fieldName,@Nullable String formatString)
            throws JSONException {
        String value = null;

        String json = MovieDbJsonUtility.getMovieJson(movie);
        if (json != null) {

            JSONObject movieJson = new JSONObject(json);
            if (isValidJsonObject(movieJson)) {
                if (formatString != null) {
                    value = String.format(formatString,movieJson.getString(fieldName));
                } else {
                    value = movieJson.getString(fieldName);
                }
            }
            if (DEBUG) {
                Log.d(TAG, String.format(
                        "getMovieParcelableValue 321: fieldName = %s: value = %s"
                        , fieldName, value));
            }
        }
        return value;
    }

    //endregion public methods

    //region private methods

    /**
     * Populate the list of movie parcelables from the JSONArray.
     *
     * @param movieJsonArray Array of movie object json string.
     * @return List of movie parcelables.
     * @throws JSONException If JSON data cannot be parsed.
     */
    private static List<MovieParcelable>
    getMovieDbStringsFromJson(JSONArray movieJsonArray)
            throws JSONException {

        List<MovieParcelable> parsedMovies = new ArrayList<>();

        for (int i = 0; i< movieJsonArray.length(); i++){

            JSONObject movie = movieJsonArray.getJSONObject(i);

            MovieParcelable movieParcelable = new MovieParcelable();
            movieParcelable.movieId =
                    movie.getInt(MovieDbContract.MOVIE_DB_ID);
            movieParcelable.overview =
                    movie.getString(MovieDbContract.MOVIE_DB_OVERVIEW);
            movieParcelable.posterPath =
                    movie.getString(MovieDbContract.MOVIE_DB_POSTER_PATH);
            movieParcelable.releaseDate =
                    movie.getString(MovieDbContract.MOVIE_DB_RELEASE_DATE);

            // Initialize the movie runtime. The actual value is not available
            // in this context/query.
            movieParcelable.runtime = MovieParcelable.MOVIE_DB_RUNTIME_NOT_SET;

            movieParcelable.title =
                    movie.getString(MovieDbContract.MOVIE_DB_TITLE);
            movieParcelable.voteAverage =
                    movie.getString(MovieDbContract.MOVIE_DB_VOTE_AVERAGE);

            parsedMovies.add(i,movieParcelable);
        }
        if (DEBUG) {
            Log.d(TAG, String.format(
                    "getMovieDbStringsFromJson(movieJsonArray) 299: parsedMovies.size() = %d",
                    parsedMovies.size()));
        }
        return parsedMovies;
    }

    private static boolean isValidJsonObject(JSONObject movieJson){
        boolean isValid = true;
        if (movieJson.has(sMovieDbJsonErrorCode)) {
            try {
                int errorCode = movieJson.getInt(sMovieDbJsonErrorCode);
                switch (errorCode) {
                    case HttpURLConnection.HTTP_OK:
                        isValid = true;
                        break;
                    case HttpURLConnection.HTTP_NOT_FOUND:
                    /* invalid query*/
                        isValid = false;
                        break;
                    default:
                    /* Server probably down */
                        isValid = false;
                        break;
                }
            }
            catch (JSONException e){
                e.printStackTrace();
            }
        }
        return isValid;
    }

    //endregion private methods
}