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

package com.paulmender.utility;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.utility
 *    Name: VideoPlayerExternal
 * Purpose: Play a video using the YouTube app or the default web browser.
 *  Author: Paul on 20171211.
 * History:
 *  20180129 Changed videoId to videoKey. Was passing videoId, not the key.
 *           Note: VideoPlayerExternal was deprecated, now using the YouTube API.
 *           Reinstated for testing and getYouTubeUrl().
 *           Example: https://www.youtube.com/watch?v=bgeSXHvPoBI.
 * References:
 *  https://stackoverflow.com/questions/574195/android-youtube-app-play-video-intent
 */

public class VideoPlayerExternal {

    private static final String TAG = "PKMI>VideoPlayerExte...";

    @SuppressWarnings("unused")
    public enum VideoPlayerIntent{
        WEB,
        YOUTUBE_APP,
        WEB_OR_YOUTUBE_APP,
        YOUTUBE_APP_OR_WEB,
    }

    //region Private Static

    private static final String sYouTubeDomain = "www.youtube.com";

    // Web Intent
    private static final String sWebUriScheme = "http";
    @SuppressWarnings("unused")
    private static final String sWebProtocol = sWebUriScheme + "://";
    private static final String sWebProtocolSecure = sWebUriScheme + "s://";

    // App Intent
    @SuppressWarnings("unused")
    private static final String appContentAuthority = "vnd.youtube:";

    //endregion Private Static

    @SuppressWarnings("unused")
    public void watchYoutubeVideo(
            VideoPlayerIntent videoPlayerIntent, Context context, String videoKey) {

        try {
            switch (videoPlayerIntent) {
                case WEB:
                    context.startActivity(getYouTubeWebIntent(videoKey));
                    break;
//                case YOUTUBE_APP:
//                    context.startActivity(getYouTubeAppIntent(videoKey));
//                    break;
//                case WEB_OR_YOUTUBE_APP:
//                    context.startActivity(getYouTubeWebIntent(videoKey));
//                    break;
//                case YOUTUBE_APP_OR_WEB:
//                    context.startActivity(getYouTubeAppIntent(videoKey));
//                    break;
                default:
                    if (DEBUG) {
                        Log.d(TAG, String.format(
                                "watchYoutubeVideo 82: videoPlayerIntent.WEB is "+
                                        "currently only supported: %s", videoPlayerIntent.name()));
                    }
            }
        } catch (ActivityNotFoundException anfe) {

            if (DEBUG) {
                Log.d(TAG, String.format(
                        "watchYoutubeVideo 90: YouTube failed: %s", anfe.getMessage()));
            }
            // Try the other intent.
            try {
                switch (videoPlayerIntent) {
                    case WEB_OR_YOUTUBE_APP:
                        context.startActivity(getYouTubeAppIntent(videoKey));
                        break;
                    case YOUTUBE_APP_OR_WEB:
                        context.startActivity(getYouTubeWebIntent(videoKey));
                        break;
                }
            } catch (ActivityNotFoundException anfe2) {
                Log.d(TAG, String.format(
                        "watchYoutubeVideo 106: YouTube failed: %s", anfe2.getMessage()));
            }
        }
    }

    private Intent getYouTubeAppIntent(String videoKey){
        return new Intent(Intent.ACTION_VIEW, Uri.parse("vnd.youtube:" + videoKey));
    }

    private Intent getYouTubeWebIntent(String videoKey){
        return new Intent(Intent.ACTION_VIEW,getYouTubeUrl(videoKey));
    }

    public static Uri getYouTubeUrl(String videoKey){

        Uri returnUri;

        String urlBase = sWebProtocolSecure + sYouTubeDomain;
        String urlPath = "/watch";
        String urlParameter = "?v=";

        String urlString = urlBase + urlPath + urlParameter + videoKey;

        // Note: According to https://developer.android.com/reference/android/net/Uri.html,
        // This class is very forgiving--in the face of invalid input,
        // it will return garbage rather than throw an exception unless otherwise specified.
        returnUri = Uri.parse(urlString);

        if (DEBUG) {
            Log.d(TAG, String.format(
                    "getYouTubeUrl 145: returnUri: %s, videoKey=%s", returnUri,videoKey));
        }

        return returnUri;
    }
}
