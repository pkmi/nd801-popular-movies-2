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

import android.content.AsyncTaskLoader;
import android.content.Context;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.Nullable;

import java.util.List;

/**
 * Package: com.paulmender.android.content
 * Project: PopularMovies Stage 2
 *    Name: AsyncTaskLoaderParcelables
 * Purpose: Concrete extension of the AsyncTaskLoader, an abstract
 *          Loader that provides an AsyncTask to do the work. (1)
 *  Author: Paul on 20171225.
 * References:
 *  1. https://developer.android.com/reference/android/content/AsyncTaskLoader.html
 *      a. #loadInBackground()
 */

@SuppressWarnings({"unused", "EmptyMethod"})
public class AsyncTaskLoaderParcelables
                extends AsyncTaskLoader<List<? extends Parcelable>> {

    //region Non-static, non-public fields

    private List<? extends Parcelable> data;

    private AsyncTaskLoaderWidgetHelper asyncTaskLoaderWidgetHelper;

    private AsyncTaskBundleWrapper asyncTaskBundleWrapper;

    //endregion

    //region Getters and Setters

    public final Bundle getLoaderBundle() {
        if (hasBundleWrapper()) {
            return getAsyncTaskBundleWrapper().getBundle();
        } else { return null;}
    }

    public final void setLoaderBundle(Bundle loaderBundle) {
        if (hasBundleWrapper()) {
            getAsyncTaskBundleWrapper().setBundle(loaderBundle);
        }
    }

    protected AsyncTaskBundleWrapper getAsyncTaskBundleWrapper() {
        return asyncTaskBundleWrapper;
    }
    public void setAsyncTaskBundleWrapper(AsyncTaskBundleWrapper asyncTaskBundleWrapper) {
        this.asyncTaskBundleWrapper = asyncTaskBundleWrapper;
    }

    public final AsyncTaskLoaderWidgetHelper getAsyncTaskLoaderWidgetHelper() {
        return this.asyncTaskLoaderWidgetHelper;
    }
    public final void setAsyncTaskLoaderWidgetHelper(
            AsyncTaskLoaderWidgetHelper asyncTaskLoaderWidgetHelper) {
        this.asyncTaskLoaderWidgetHelper = asyncTaskLoaderWidgetHelper;
    }

    //endregion Getters and Setters
    
    //region Implementation methods

    /**
     * Called on a worker thread to perform the actual load and to return the result
     * of the load operation. Implementations should not deliver the result directly,
     * but should return them from this method.
     *  - Override deliverResult(D) to process results on the UI thread.
     *  - Periodically check isLoadInBackgroundCancelled(), and terminate if true.
     * For additional details, see References 1.a.
     * @return The list of parcelables.
     */
    @Override public List<? extends Parcelable> loadInBackground() {
        // Override in subclass.
        return null;
    }

    //endregion Implementation methods

    /**
     * Constructor
     * @param context The context of the caller.
     * @param bundleWrapper Contains any arguments from the caller required by the loader.
     * @param asyncTaskLoaderWidgetHelper Contains widget references from the caller used for displaying
     *                     the error messages, progress, and results.
     */
    public AsyncTaskLoaderParcelables(
            Context context,
            @Nullable AsyncTaskBundleWrapper bundleWrapper,
            @Nullable AsyncTaskLoaderWidgetHelper asyncTaskLoaderWidgetHelper) {
        super(context);

        this.asyncTaskBundleWrapper = bundleWrapper;
        this.asyncTaskLoaderWidgetHelper = asyncTaskLoaderWidgetHelper;
    }

    //region Override methods (of interest)

    @CallSuper
    @Override public void deliverResult(List<? extends Parcelable> data) {

        // If an async query came in while the loader is stopped...
        if (isReset()){
            // ... release the last saved parcelables.
            if (this.data != null){
                onReleaseResources(data);
            }
        }

        List<? extends Parcelable> staleData = this.data;

        // Set the data to the delivered result.
        this.data = data;

        // If the loader is started...
        if (isStarted()) {
            // ...deliver the results now.
            super.deliverResult(this.data);
        }

        // If there is stale data...
        if (staleData != null){
            // ... it is no longer needed, so release its resources (if any).
            onReleaseResources(staleData);
        }
    }

    @Override
    public boolean isLoadInBackgroundCanceled() {
        return super.isLoadInBackgroundCanceled();
    }

    /**
     * Handle the request to cancel a load.
     */
    @CallSuper
    @Override public void onCanceled(List<? extends Parcelable> parcelables) {
        super.onCanceled(parcelables);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(parcelables);
    }

    /**
     * Handles a request to completely reset the Loader.
     */
    @CallSuper
    @Override protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // If there is existing data...
        if (this.data != null) {
            // ... release it.
            onReleaseResources(this.data);
            this.data = null;
        }
    }

    /**
     * Handle the request to start loading.
     */
    @CallSuper
    @Override protected void onStartLoading() {

        // If there is data available...
        if (this.data != null) {
            // ... deliver it.
            deliverResult(this.data);
        } else {
            this.showProgressBar();
            forceLoad();
        }
    }

    /**
     * Handle the request to stop loading.
     */
    @CallSuper
    @Override protected void onStopLoading() {
        // Attempt to cancel the current load in progress.
        cancelLoad();
    }

    //endregion Override methods (of interest)

    //region protected methods

    @SuppressWarnings("WeakerAccess")
    protected void showProgressBar() {
        if (hasWidgetHelper()){
            this.asyncTaskLoaderWidgetHelper.showProgressBar();
        }
    }

    @SuppressWarnings("WeakerAccess")
    protected boolean hasWidgetHelper(){
        return (this.asyncTaskLoaderWidgetHelper != null);
    }

    protected boolean hasBundleWrapper() { return (this.asyncTaskBundleWrapper != null);}

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    @SuppressWarnings({"WeakerAccess", "EmptyMethod"})
    @CallSuper
    protected void onReleaseResources(List<? extends Parcelable> parcelables) {
    }

    //endregion protected methods
}
