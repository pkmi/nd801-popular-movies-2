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

package com.paulmender.android.app.fragment.extensions;

import android.app.Fragment;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;

import com.paulmender.android.content.AsyncTaskBundleWrapper;
import com.paulmender.android.content.AsyncTaskLoaderCallbacks;
import com.paulmender.android.content.AsyncTaskLoaderWidgetHelper;

/**
 * Package: com.paulmender.android.app.fragment.extensions
 *    Name: fragment Extension
 * Purpose: Add Shared preference support.
 *  Author: Paul on 20171216.
 */
@SuppressWarnings({"unused", "EmptyMethod"})
public class FragmentExtension<LC extends AsyncTaskLoaderCallbacks>
                extends Fragment{

    //region aggregation/composition fields

    // asyncTaskLoaderCallbacks
    private LC asyncTaskLoaderCallbacks = null;

    //endregion aggregation/composition fields

    //region Non-public, non-static fields

    // Shared preferences for storing state, and other data.
    private SharedPreferences sharedPreferences = null;

    // Standardize preference keys for subclasses.
    // - The preference key consists of a prefix (the context class name))and a suffix.
    // - The prefix is static, but the suffix can be set at the subclass level.
    @SuppressWarnings("unused")
    private static String sharedPreferencesFileName;
    @SuppressWarnings("unused")
    private static String sharedPreferenceKey; // Set by call from subclass.

    //endregion Non-public, non-static fields

    //region Getters and Setters

    // AsyncTaskLoaderParcelablesCallbacks
    protected LC getAsyncTaskLoaderCallbacks() {
        return asyncTaskLoaderCallbacks;
    }
    protected void setAsyncTaskLoaderCallbacks(
            LC asyncTaskLoaderCallbacks) {
        this.asyncTaskLoaderCallbacks = asyncTaskLoaderCallbacks;
    }

    public SharedPreferences getSharedPreferences() {
        return sharedPreferences;
    }

    // endregion Getters and Setters

    //region Implements methods

    //endregion Implements methods

    //region Override Methods

    @CallSuper
    @Override public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        // Set the shared preferences to the activity preferences file
        // (i.e., getPreferences() instead of getSharedPreferences()
        // so that the preference file name is activity class name specific.
        this.sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);

    }

    //endregion Override Methods

    //region protected Methods

    protected AsyncTaskLoaderWidgetHelper getAsyncTaskLoaderWidgetHelper(){
        // Subclass implementation.
        return null;
    }

    @CallSuper
    // Note: When calling the super.onActivityCreatedInitLoader() in the
    // subclass override, pass the actual loader Id, and (optionally) a loaderBundle.
    protected <PC extends AsyncTaskLoaderCallbacks> void onActivityCreatedInitLoader(
                            int loaderId,
                        AsyncTaskBundleWrapper bundleWrapper,
                        @NonNull LC loaderCallbacks){

        AsyncTaskLoaderCallbacks.setLoaderId(loaderId);

        if (loaderId != AsyncTaskLoaderCallbacks.LOADER_ID_NOT_SET) {

            setAsyncTaskLoaderCallbacks(loaderCallbacks);

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one. (3)
            getLoaderManager().initLoader(
                    loaderId,
                    bundleWrapper != null ?
                            (bundleWrapper.hasBundle() ? bundleWrapper.getBundle() : null) : null,
                    loaderCallbacks);
        }
    }

    /**
     * Populate the content view with content.
     * For example: setMovieReviewContent(); a method where the content @BindViews are defined.
     *
     * Note: Making this method abstract (and thereby, the class, too) resulted in
     *       "is not assignable to android.app.activity" when trying to define the
     *       subclasses as <activity> in the AndroidManifest.xml
     */
    protected void onCreateSetContent(){
        // Override in the subclass.
    }

    /**
     * Call all of the "onCreate..." extensions.
     * Subclasses should call this method once in onCreate().
     */
    @CallSuper
    protected void setSharedPreference(String preferenceKey, String preferenceValue) {
        SharedPreferences sharedPreferences = getActivity().getPreferences(Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(preferenceKey, preferenceValue);
        editor.apply(); // Save the preference to the preference file cache.
    }

    @CallSuper
    protected String getSharedPreference(String preferenceKey) {
        SharedPreferences preference = getActivity().getPreferences(Context.MODE_PRIVATE);
        return preference.getString(preferenceKey, null);
    }

    //endregion protected Methods
}
