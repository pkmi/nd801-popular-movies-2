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

import android.os.Bundle;
import android.support.annotation.Nullable;

/**
 * Package: com.paulmender.udacity.popularmovies.content
 * Project: PopularMovies Stage 2
 *    Name: AsyncTaskBundleWrapper
 * Purpose: Since Bundle cannot be subclassed, wrap it in a class.
 *          Also provides standard bundle keys (enum BundleKey) and support for related values.
 *  Author: Paul on 20171006.
 * History:
 *  20171127 Refactored copy of com.paulmender.android.os.FetchDataParameters.
 *  20180102 Refactored, a "push down", from MovieDbAsyncTaskBundleWrapper.java.
 *           The primary objective was to eliminate application specific references
 *           while maintaining class interactions.
 */

@SuppressWarnings("unused")
public class AsyncTaskBundleWrapper {

    @SuppressWarnings("unused")
    public enum BundleKey {
        BUNDLE_KEY_QUERY_END_POINT,
        BUNDLE_KEY_QUERY_ITEM_ID,
        BUNDLE_KEY_QUERY_NAME,
        BUNDLE_KEY_QUERY_SORT_ORDER
    }

    //region private fields

    private String endPoint;
    private int itemId;
    private Bundle bundle = new Bundle();
    private String sortOrderName;

    //endregion private fields

    //region Getters and Setters

    // endPoint
    public String getEndPoint() {
        return endPoint;
    }
    public void setEndPoint(String endPointValue) {
        endPoint = endPointValue;
    }

    // itemId
    public int getItemId() {return itemId;}
    public void setItemId(int itemIdValue) {
        this.itemId = itemIdValue;
    }

    // bundle
    public Bundle getBundle() {
        return bundle;
    }
    public void setBundle(Bundle bundle) {
        this.bundle = bundle;
    }

    // sortOrderName
    public String getSortOrderName() {
        return sortOrderName;
    }
    public void setSortOrderName(String sortOrderValue) {
        sortOrderName = sortOrderValue;
    }

    //endregion Getters and Setters

    // Constructor
    public AsyncTaskBundleWrapper(@Nullable Bundle bundle){
        this.bundle = bundle;
    }

    //region public methods

    public String getKey(BundleKey bundleKey){
        return bundleKey.name();
    }

    public boolean hasBundle() {
        return this.bundle != null;
    }

    // Create a new bundle, if one does not already exist.
    public void initBundle(){
        if (bundle == null){
            this.bundle = new Bundle();
        }
    }

    public void replaceBundle(Bundle newBundle){
        this.bundle = newBundle;
    }

    //endregion public methods
}
