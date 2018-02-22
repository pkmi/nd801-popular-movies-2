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

import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.paulmender.udacity.popularmovies.BuildConfig;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies
 * Project: PopularMovies
 *    Name: MovieDbUtility
 * Purpose: Query the MovieDB API on the web and define various data related values.
 *  Author: Paul on 20170822.
 * History:
 *  20171128 Refactor MovieQuery enum to replace MovieSortOrderWrapper.
 *           Reference:
 *              https://stackoverflow.com/questions/604424/lookup-enum-by-string-value
 *  20171128 Remove all references to obsolete MovieSortOrderWrapper.
 *  20171128 Renamed MovieSortOrder to MovieQuery and refactored all references accordingly.
 *  20171206 Correct StrictMode error, see Correction 1 and 2.
 *  20180205 Added References and Notes sections in comments.
 *  20180205 Changed sort order to none for movie reviews and videos.
 *  References:
 *      1. https://developers.themoviedb.org/3/movies/get-top-rated-movies
 *      2. https://developers.themoviedb.org/3/movies/get-popular-movies
 *      3. https://developers.themoviedb.org/3/movies/get-movie-videos
 *      4. https://developers.themoviedb.org/3/movies/get-movie-reviews
 *   Notes:
 *      MovieDB Query Comparison
 *      MovieQuery...
 *      POPULAR:
 *       These return the same rows, except total_results values are different 1. 19757, 2. 344997.
 *       *1. https://api.themoviedb.org/3/movie/popular?api_key=<api_key>&sort_by=popularity.desc
 *       2. https://api.themoviedb.org/3/discover/movie?api_key=<api_key>&sort_by=popularity.desc
 *
 *      TOP_RATED:
 *       These do not return the same row. Probably due to the way "top rated" is calculated. Perhaps it's based on a combination of vote count, popularity, and vote average.
 *       *1. https://api.themoviedb.org/3/movie/top_rated?api_key=<api_key>&sort_by=vote_average.desc
 *       2. https://api.themoviedb.org/3/discover/movie?api_key=<api_key>&sort_by=vote_average.desc
 *
 *      MOVIE_DETAIL:
 *       Both queries return the same sort order regardless of the sort parameter.
 *       *1. https://api.themoviedb.org/3/movie/321612/?api_key=<api_key>
 *
 *      MOVIE_REVIEW_LIST:
 *       Both queries return the same sort order regardless of the sort parameter.
 *       1. https://api.themoviedb.org/3/movie/321612/reviews?api_key=<api_key>
 *       *2. https://api.themoviedb.org/3/movie/321612/reviews?api_key=<api_key>&sort_by=author.asc
 *
 *      MOVIE_VIDEO_LIST:
 *       Both queries return the same sort order regardless of the sort parameter.
 *       1. https://api.themoviedb.org/3/movie/321612/videos?api_key=<api_key>
 *       *2. https://api.themoviedb.org/3/movie/321612/videos?api_key=<api_key>&sort_by=type.asc
 *
 *    *Popular Movies application uses this query form.
 */
@SuppressWarnings("unused")
public class MovieDbUtility {

    /**
     * MovieDbUtility defines the URLs, URI components and column names used from the MovieDB.
     * Note: This approach is based on the Sunshine application.
     */

    private static final String TAG = "PKMI>MovieDbUtility";

    //region Public Static fields

    @SuppressWarnings("unused")
    public static final String MOVIE_VIDEO_YOUTUBE_SITE = "YouTube";

    //endregion

    //region enums

    public enum MovieQuery {
        POPULAR(sEndPointMoviePopular, sSortByPopularDesc),
        TOP_RATED(sEndPointMovieTopRated, sSortByRatingDesc),
        DEFAULT(sEndPointMoviePopular, sSortByDefault),
        FAVORITES(sEndPointNone,sSortByNone), // Stage 2
        MOVIE_DETAIL(sEndPointMovieId,sSortByNone), // Stage 2
        MOVIE_REVIEW_LIST(sEndPointMovieReviews,sSortByNone), // Stage 2
        MOVIE_VIDEO_LIST(sEndPointMovieVideos,sSortByNone); // Stage 2

        private String sortBy;
        private String endPoint;

        public String getEndPoint() {
            return endPoint;
        }

        public String getSortBy() {
            return sortBy;
        }

        // Constructor
        MovieQuery(String endPoint, String sortBy){
            this.endPoint = endPoint;
            this.sortBy = sortBy;
        }

        @Nullable
        public static MovieQuery getEnumerator(String enumName){
            for (MovieQuery movieQuery : MovieQuery.values()) {
                if (movieQuery.name().equalsIgnoreCase(enumName)) {
                    return movieQuery;
                }
            }
            return null;
        }
    }

    //endregion

    //region Private static fields
    private static final String sApiKey = BuildConfig.MOVIE_DB_API_KEY;

    // End Point constants
    private static final String sEndPointDiscover = "/discover";
    private static final String sEndPointDiscoverMovie = "/discover/movie";
    private static final String sEndPointFind = "/find";
    private static final String sEndPointImage = "image";
    private static final String sEndPointSearch = "/search";
    private static final String sEndPointMovieId = "/movie/%d";
    private static final String sEndPointMovieReviews = "/movie/%d/reviews";
    private static final String sEndPointMovieVideos = "/movie/%d/videos";
    private static final String sEndPointMoviePopular = "/movie/popular";
    private static final String sEndPointMovieTopRated = "/movie/top_rated";
    private static final String sEndPointNone = "";

    // Allowed sort_by values from MovieDb GET discover/movies.
    // https://developers.themoviedb.org/3/discover/movie-discover
    private static final String sSortByPopularAsc = "popularity.asc";
    private static final String sSortByPopularDesc = "popularity.desc";
    private static final String sSortByReleaseDateAsc = "release_date.asc";
    private static final String sSortByReleaseDateDesc = "release_date.desc";
    private static final String sSortByRevenueAsc = "revenue.asc";
    private static final String sSortByRevenueDesc = "revenue.desc";
    private static final String sSortByPrimaryReleaseDateAsc = "primary_release_date.asc";
    private static final String sSortByPrimaryReleaseDateDesc = "primary_release_date.desc";
    private static final String sSortByOriginalTitleAsc = "original_title.asc";
    private static final String sSortByOriginalTitleDesc = "original_title.desc";
    private static final String sSortByVoteAverageAsc = "vote_average.asc";
    private static final String sSortByVoteAverageDesc = "vote_average.desc";
    private static final String sSortByVoteCountAsc = "vote_count.asc";
    private static final String sSortByVoteCountDesc = "vote_count.desc";
    private static final String sSortByDefault = sSortByPopularDesc;

    // Declare additional application sort by values.
    private static final String sSortByRatingAsc = sSortByVoteAverageAsc;
    private static final String sSortByRatingDesc = sSortByVoteAverageDesc;
    private static final String sSortByTitleAsc = "title.asc"; // Stage 2
    private static final String sSortByVideoTypeAsc = "type.asc"; // Stage 2
    private static final String sSortByReviewAuthorAsc = "author.asc"; // Stage 2
    private static final String sSortByNone = "";

    private static final String sUriScheme = "http";

    private static final String sWebProtocol = sUriScheme +"://";
    private static final String sWebProtocolSecure = sUriScheme +"s://";

    /*
    * The base URI for connecting to the Movie DB.
    */
    private static final String sUriBase = sWebProtocolSecure+"api.themoviedb.org/3";

    /**
     * Parameter constants
     */
    private static final String sParamAppendToResponse = "append_to_response"; // Stage 2
    private static final String sParamApiKey = "api_key";
    private static final String sParamLanguage = "language"; // Stage 2
    private static final String sParamSortBy = "sort_by";

    /**
     * The base image URI used in conjunction with the poster path to complete the
     * URL to fetch images.
     */
    private static final String sUriImageAuthority = "image.tmdb.org/t/p/";

    /** Screen size options. */
    private static final String sUriImageSize92 = "w92";
    private static final String sUriImageSize154 = "w154";
    private static final String sUriImageSize185 = "w185";
    private static final String sUriImageSize342 = "w342";
    private static final String sUriImageSize500 = "w500";
    private static final String sUriImageSize780 = "w780";
    private static final String sUriImageSizeOriginal = "original";
    // endregion

    //region Private static methods

    /**
     * Builds the URL used to query the MovieDB.
     * @param endPoint The end point (defined by the END_POINT... constants).
     * @param sortBy The sort order (defined by the SORT_BY... constants).
     * @return The URL.
     * Returned URL Examples:
     * POPULAR:
     *  https://api.themoviedb.org/3/discover/movie?api_key=<<api_key>>&sort_by=popularity.desc
     * TOP_RATED:
     *  https://api.themoviedb.org/3/discover/movie?api_key=<<api_key>>&sort_by=popularity.desc
     */
    private static URL buildMovieUrl(String endPoint, String sortBy) {

        String baseUrl = sUriBase + endPoint;

        Uri builtUri = android.net.Uri.parse(baseUrl);
        builtUri = builtUri.buildUpon().appendQueryParameter(sParamApiKey, sApiKey).build();
        if (sortBy != null && !sortBy.equals(sSortByNone)) {
            builtUri = builtUri.buildUpon()
                    .appendQueryParameter(sParamSortBy, sortBy)
                    .build();
        }
        return getUrl(builtUri);
    }

    /**
     * Builds the URL used to query the MovieDB for movie details (e.g., reviews or videos)
     * related to a single movie. (Stage 2)
     * @param endPoint The end point (defined by the END_POINT... constants).
     * @param movieId The movie's MovieDB ID.
     * @param sortBy The sort order (defined by the SORT_BY... constants).
     * @return The URL.
     * Returned URL Examples:
     * https://api.themoviedb.org/3/movie/321612/videos?api_key=<your_api_key>&sort_by=type
     * https://api.themoviedb.org/3/movie/321612/reviews?api_key=<your_api_key>&sort_by=
     * Note: Tried using append_to_response but it fails on getJSONArray in the MovieDbJsonUtility conversion;
     *       therefore using separate URLs to fetch reviews and videos.
     *        https://api.themoviedb.org/3/movie/321612?api_key=<<api_key>>&append_to_response=videos,reviews
     */
    private static URL buildMovieUrlExtras(
            @NonNull String endPoint,
            int movieId,
            String sortBy) {

        String endPointWithMovieId = String.format(endPoint,movieId);
        String baseUrl = sUriBase + endPointWithMovieId;

        Uri builtUri = android.net.Uri.parse(baseUrl);
        builtUri = builtUri.buildUpon().appendQueryParameter(sParamApiKey, sApiKey).build();
        if (sortBy != null && !sortBy.equals(sSortByNone)) {
            builtUri = builtUri.buildUpon()
                    .appendQueryParameter(sParamSortBy, sortBy)
                    .build();
        }

        return getUrl(builtUri);
    }

    private static URL getUrl(android.net.Uri uri){
        URL url = null;

        try {
            url = new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    //endregion
    
    //region Public static methods

    public static URL getUrl(MovieQuery movieQuery){
        URL url = buildMovieUrl(
                        movieQuery.getEndPoint(), movieQuery.getSortBy());
        if (DEBUG) {
            Log.d(TAG, String.format("*RETURN* getUrl(MovieQuery movieQuery) 296: %s",url));
        }
        return url;
    }

    public static URL getUrl(int movieId, MovieQuery movieQuery){
        URL url = buildMovieUrlExtras(
                        movieQuery.getEndPoint(), movieId, movieQuery.getSortBy());
        if (DEBUG) {
            Log.d(TAG, String.format(
                    "*RETURN*  getUrl(int movieId, MovieQuery movieQuery) 306: %s",url));
        }
        return url;
    }

    /**
     * This method returns the entire result from the HTTP response.
     *
     * @param url The URL to fetch the HTTP response from.
     * @return The contents of the HTTP response.
     * @throws IOException Related to network and stream reading
     *
     * History:
     *  StrictMode error: Explicit termination method 'end' not called
     *  resource was acquired at attached stack trace but never released.
     *  See java.io.Closeable for information on avoiding resource leaks.
     *  20180205 Convert try to try (InputStream inputStream = urlConnection.getInputStream()..
     *           per lint warning (try with resources).
     */
    @Nullable
    public static String getResponseFromHttpUrl(URL url) throws IOException {

        if (url != null) {
            HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

            try (InputStream inputStream = urlConnection.getInputStream()){
                Scanner scanner = new Scanner(inputStream);

                scanner.useDelimiter("\\A");

                boolean hasInput = scanner.hasNext();
                if (hasInput) {
                    return scanner.next();
                } else {
                    return null;
                }
            } catch (Exception e) {
                Log.e(TAG, String.format("getResponseFromHttpUrl 343: %s: %s",
                        e.getClass().getSimpleName(), e.getMessage()));
                e.printStackTrace();
            }

            urlConnection.disconnect();

            if (DEBUG) {
                Log.d(TAG, "getResponseFromHttpUrl 351: urlConnection.disconnect()");
            }

            if (DEBUG) {
                Log.d(TAG, "getResponseFromHttpUrl 355: inputStream.close()");
            }
        } else {
            if (DEBUG) {
                Log.d(TAG, "getResponseFromHttpUrl 359: url is null");
            }
        }
        return null;
    }

    /**
     * Builds the URI used to retrieve an set the ImageView URI of poster from the MovieDB.
     * @param imagePath The Movie's image path.
     */
    public static String getImageUrlString(String imagePath) {

        // 185 image size was recommended in Udacity's Popular Movies Implementation Guide & Spec.

        return sWebProtocol + sUriImageAuthority + sUriImageSize185 + imagePath;
    }

    // /endregion Public static methods
}


