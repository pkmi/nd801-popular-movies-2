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

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

/**
 * Package: com.paulmender.utility
 *    Name: NetworkUtility
 * Purpose: Provides status and other information related to a network.
 *  Author: Paul on 20180126.
 * Prerequisites:
 *      1. android.permission.ACCESS_NETWORK_STATE
 */
public class NetworkUtility {

    /**
    * Determines whether the device currently has a network connection.
    * @return true if the device has a network connection, false otherwise.
    *
    *  References:
    *      1. https://developers.google.com/youtube/v3/quickstart/android
    */
    @SuppressWarnings("unused")
    private boolean isDeviceOnline(Context context) {
        ConnectivityManager c =
                (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (c != null) {
            NetworkInfo networkInfo = c.getActiveNetworkInfo();
            return (networkInfo != null && networkInfo.isConnected());
        }

        return false;
    }
}
