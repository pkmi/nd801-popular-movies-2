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

package com.paulmender.android.content;

import android.app.LoaderManager;
import android.content.Context;
import android.content.Loader;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Package: com.paulmender.android.content
 * Project: PopularMovies Stage 2
 *    Name: AsyncTaskLoaderCallbacks
 * Purpose: A customizable (through "on..." method subclass overrides)
 *          implementation of LoaderCallbacks for AsyncTaskLoaderParcelables.
 *  Author: Paul on 20180102.
 * History:
 *  20180206 Add show...() as pass through methods.
 * References:
 * 1. https://medium.com/google-developers/making-loading-data-on-android-lifecycle-aware-897e12760832
 */
@SuppressWarnings("unused")
public class AsyncTaskLoaderCallbacks
            implements LoaderManager.LoaderCallbacks<List<? extends Parcelable>> {

    //region public constants

    public static final int LOADER_ID_NOT_SET = -1;

    //endregion public constants

    //region Non-public, Non-static fields

    // Loader fields
    private static int loaderId = LOADER_ID_NOT_SET;
    private Bundle loaderBundle = null;

    // Parameter
    private AsyncTaskBundleWrapper asyncTaskBundleWrapper = null;

    // Parameter fields
    private Context context = null;

    private AsyncTaskLoaderWidgetHelper widgetHelper = null;

    //endregion Non-public, Non-static fields

    //region Getters and Setters

    // asyncTaskBundleWrapper
    protected AsyncTaskBundleWrapper getAsyncTaskBundleWrapper() {
        return this.asyncTaskBundleWrapper;
    }
    public void setAsyncTaskBundleWrapper(AsyncTaskBundleWrapper asyncTaskBundleWrapper) {
        this.asyncTaskBundleWrapper = asyncTaskBundleWrapper;
    }

    // context
    public Context getContext() {
        return this.context;
    }

    // loaderBundle
    public final Bundle getLoaderBundle() {
        return loaderBundle;
    }
    protected final void setLoaderBundle(Bundle loaderBundle) {
        this.loaderBundle = loaderBundle;
    }

    // loaderId
    @CallSuper
    public static int getLoaderId() {
        return loaderId;
    }
    @CallSuper
    public static void setLoaderId(int loaderId) {
        AsyncTaskLoaderCallbacks.loaderId = loaderId;
    }

    // widgetHelper
    protected AsyncTaskLoaderWidgetHelper getWidgetHelper() {
        return widgetHelper;
    }

    //endregion Getters and Setters

    // Constructor
    protected AsyncTaskLoaderCallbacks(
            @NonNull Context context,
            @Nullable AsyncTaskLoaderWidgetHelper widgetHelper,
            @Nullable AsyncTaskBundleWrapper bundleWrapper)
    {
        this.context = context;
        this.widgetHelper = widgetHelper;
        this.asyncTaskBundleWrapper = bundleWrapper;
    }

    //region implements methods

    // LoaderCallbacks
    @Override public Loader<List<? extends Parcelable>> onCreateLoader(int i, Bundle bundle) {

        setLoaderId(i);

        setLoaderBundle(bundle);

        return new AsyncTaskLoaderParcelables(
                        this.getContext(),
                        getAsyncTaskBundleWrapper(),
                        getWidgetHelper());
    }

    @Override public void onLoadFinished(
                    Loader<List<? extends Parcelable>> loader, List<? extends Parcelable> data) {
        // Subclass implementation
    }

    @Override public void onLoaderReset(Loader<List<? extends Parcelable>> loader) {
        // Subclass implementation
    }

    //endregion implements methods

    //region protected methods

    protected boolean hasTextView() {
        return (hasWidgetHelper() && getWidgetHelper().hasTextView());
    }

    protected boolean hasWidgetHelper(){
        return this.widgetHelper != null;
    }

    // Show the error message with the android:text in the TextView.
    protected void showErrorMessage() {
        if (hasWidgetHelper()) {
            this.widgetHelper.showErrorMessage();
        }
    }

    // Show the error message with the android:text in the TextView.
    protected void showErrorMessage(CharSequence message) {
        if (hasWidgetHelper()) {
            this.widgetHelper.showErrorMessage(message);
        }
    }

    protected void showRecyclerView() {
        if (hasWidgetHelper()) {
            this.widgetHelper.showRecyclerView();
        }
    }

    // Show the text view.
    protected void showTextView() {
        if (hasWidgetHelper()) {
            this.widgetHelper.showTextView();
        }
    }

    //endregion protected methods
}
