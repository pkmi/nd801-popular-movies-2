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

package com.paulmender.udacity.popularmovies.app;

import android.content.res.Resources;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.paulmender.android.support.v4.app.BaseDialogFragment;
import com.paulmender.udacity.popularmovies.BuildConfig;
import com.paulmender.udacity.popularmovies.R;

import java.text.DateFormat;

import static com.paulmender.udacity.popularmovies.BuildConfig.BUILD_DATE_TIME;
import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies.app
 * Project: PopularMovies Stage 2
 *    Name: MovieAboutDialogFragment
 * Purpose: Further customization of the BaseMovieDialogFragment as needed.
 *  Author: Paul on 20180129.
 * References:
 *  1. https://stackoverflow.com/questions/6223739/how-do-i-get-my-application-version
 *  2. https://docs.oracle.com/javase/tutorial/i18n/format/dateFormat.html
 */

public class MovieAboutDialogFragment extends BaseDialogFragment {

    public static MovieAboutDialogFragment newInstance(Bundle dialogBundle) {
        MovieAboutDialogFragment f = new MovieAboutDialogFragment();
        f.setArguments(dialogBundle);
        return f;
    }

    //region Override methods

    @Override
    protected void setViewContent(View view) {

        Resources r = getResources();

        // Set the about dialog header.
        String appName = r.getString(R.string.app_name);
        TextView header = view.findViewById(R.id.tv_about_header);
        if (header != null) {
            header.setText(String.format(r.getString(R.string.dialog_about_header),appName));
        }

        // Set the version information.
        String versionName = BuildConfig.VERSION_NAME;
        if (DEBUG) {
            versionName += r.getString(R.string.dialog_about_version_debug_suffix);
        }
        int versionCode = BuildConfig.VERSION_CODE;
        TextView versionInfo = view.findViewById(R.id.tv_about_version_info);
        if (versionInfo != null) {
            String versionInfoText =
                    String.format(r.getString(R.string.dialog_about_version_info),
                                versionName, versionCode);

            versionInfo.setText(versionInfoText);
        }

        // Set the build date.
        DateFormat dateFormatter;
        dateFormatter = DateFormat.getDateInstance(DateFormat.DEFAULT);
        String buildDateText = dateFormatter.format(BUILD_DATE_TIME);

        TextView buildDate = view.findViewById(R.id.tv_about_build_date);
        if (buildDate != null) {
            buildDate.setText(String.format(
                    r.getString(R.string.dialog_about_build_date),
                    buildDateText));
        }
    }

    //endregion Override methods
}
