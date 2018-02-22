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

package com.paulmender.android.support.v7.app.appcompatactivity.extensions;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.annotation.CallSuper;
import android.support.annotation.IdRes;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v7.app.AppCompatActivity;

import com.paulmender.android.content.AsyncTaskBundleWrapper;
import com.paulmender.android.content.AsyncTaskLoaderCallbacks;
import com.paulmender.android.content.AsyncTaskLoaderWidgetHelper;

import java.util.List;

/**
 * Package: com.paulmender.android.support.v7.App.appcompatactivity.extensions
 * Project: PopularMovies Stage 2
 *    Name: ActivityExtension
 * Purpose: Extends the AppCompatibility class, including:
 *          - Tab Layout support
 *          - OnCreate method hooks
 *          - Loader initialization and callbacks.
 *  Author: Paul on 20171216.
 * History:
 *  20171231 Refactor: Renamed and moved from com.paulmender.app.AppCompatActivityExtension.java.
 * References:
 *  1. https://docs.oracle.com/javase/tutorial/java/IandI/interfaceDef.html
 *  2. https://developer.android.com/reference/android/app/Activity.html#getPreferences(int)
 *  3. https://developer.android.com/reference/android/app/LoaderManager.html
 *  4. https://developer.android.com/guide/components/loaders.html
 *  5. https://docs.oracle.com/javase/tutorial/extra/generics/literals.html
 */
@SuppressWarnings("unused")
public class ActivityExtension<
                T extends Parcelable, L extends List<T>, LC extends AsyncTaskLoaderCallbacks>
                        extends AppCompatActivity implements
                                TabLayout.OnTabSelectedListener {

    //region public constants

    public static final int LAYOUT_ID_NOT_SET = -1;

    private static final int tabPositionNotSet = -1;
    private static final int tabPositionDefault = 0;

    private static final String preferenceKeyTabPosition = "TabPosition";

    //endregion public constants

    //region aggregation/composition fields

    // asyncTaskLoaderCallbacks
    private LC asyncTaskLoaderCallbacks = null;

    // fragment manager helper for activities implementing fragments.
    private final FragmentManagerHelper fragmentManagerHelper =
            new FragmentManagerHelper(getFragmentManager());

    //endregion aggregation/composition fields

    //region private fields

    // Content view layout id.
    private int layoutId = LAYOUT_ID_NOT_SET;

    // Shared preferences for storing state, and other data.
    private SharedPreferences sharedPreferences = null;

    // Standardize preference keys for subclasses.
    // - The preference key consists of a prefix (the context class name))and a suffix.
    // - The prefix is static, but the suffix can be set at the subclass level.
    private static String sharedPreferencesFileName;
    private static String sharedPreferenceKey; // Set by call from subclass.

    //  The current preference key.
    private static String preferenceKey;

    // Holds a reference to the TabLayout (if any).
    private TabLayout tabLayout = null;

    //endregion private fields

    //region Getters and Setters

    // AsyncTaskLoaderCallbacks
    protected LC getAsyncTaskLoaderCallbacks() {
        return asyncTaskLoaderCallbacks;
    }
    private void setAsyncTaskLoaderCallbacks(
            LC asyncTaskLoaderCallbacks) {
        this.asyncTaskLoaderCallbacks = asyncTaskLoaderCallbacks;
    }

    // fragmentManagerHelper
    protected final FragmentManagerHelper getFragmentManagerHelper() {
        return fragmentManagerHelper;
    }

    // layoutId
    @CallSuper
    public final int getLayoutId() {
        return this.layoutId;
    }
    @CallSuper
    public final void setLayoutId(@IdRes int layoutId) {
        this.layoutId = layoutId;
    }

    // preferenceKey
    public static void setPreferenceKey(String preferenceKey) {
        ActivityExtension.preferenceKey = preferenceKey;
    }

    // sharedPreferenceKey
    @CallSuper
    public static String getSharedPreferenceKey() {
        return sharedPreferenceKey;
    }
    @CallSuper
    public static void setSharedPreferenceKey(String sharedPreferenceKey) {
        ActivityExtension.sharedPreferenceKey = sharedPreferenceKey;
    }

    // tabLayout
    @CallSuper
    protected final TabLayout getTabLayout(){
        return tabLayout;
    }
    @CallSuper
    public final void setTabLayout(TabLayout tabLayout) {
        this.tabLayout = tabLayout;
    }

    // endregion Getters and Setters

    //region Implements methods

    // TabLayout.OnTabSelectedListener methods
    public void onTabSelected(TabLayout.Tab tab) {
        // Stub
    }

    public void onTabUnselected(TabLayout.Tab tab) {
        // Stub
    }

    public void onTabReselected(TabLayout.Tab tab) {
        // Stub
    }

    //endregion Implements methods

    //region Interfaces

    @SuppressWarnings("WeakerAccess")
    public interface Extensions extends TabLayout.OnTabSelectedListener {

        // Interface method signatures
        void onCreateSetContent();
    }

    //endregion Interfaces

    //region Override methods

    @CallSuper
    @Override protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set the shared preferences to the activity preferences file
        // (i.e., getPreferences() instead of getSharedPreferences()
        // so that the preference file name is activity class name specific.
        this.sharedPreferences = getPreferences(Context.MODE_PRIVATE);
    }

    @CallSuper
    @Override protected void onDestroy() {
        if (hasTabLayout()){
            tabLayout.removeOnTabSelectedListener(this);
        }
        super.onDestroy();
    }

    @CallSuper
    @Override protected void onPause(){
        super.onPause();
        saveSelectedTabPosition();
    }

    @CallSuper
    @Override protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        saveSelectedTabPosition();
    }

    //endregion Override methods

    //region extensions interface methods

    @CallSuper
    private void onCreateInitLoader(
            int loaderId,
            @NonNull LC loaderCallbacks){

        AsyncTaskLoaderCallbacks.setLoaderId(loaderId);

        if (loaderId != AsyncTaskLoaderCallbacks.LOADER_ID_NOT_SET) {

            setAsyncTaskLoaderCallbacks(loaderCallbacks);

            AsyncTaskBundleWrapper bundleWrapper = getAsyncTaskBundleWrapper();

            // If there is a bundle wrapper, get the loader bundle (if any) from the bundle wrapper.
            Bundle loaderBundle = bundleWrapper != null ?
                    (bundleWrapper.hasBundle() ? bundleWrapper.getBundle() : null)
                    : null;

            // Prepare the loader.  Either re-connect with an existing one,
            // or start a new one. (3)
            getLoaderManager().initLoader(
                    loaderId,
                    loaderBundle,
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
    protected void onCreateSetExtensions(
            int loaderId,
            @NonNull LC loaderCallbacks,
            @Nullable TabLayout tabLayout,
            int tabPositionDefault,
            @NonNull TabLayout.OnTabSelectedListener listener){

        setAsyncTaskLoaderCallbacks(loaderCallbacks);

        onCreateSetContent();

        onCreateSetTabLayout(tabLayout, tabPositionDefault, listener);

        onCreateInitLoader(loaderId, loaderCallbacks);
}

    @CallSuper
    public void onCreateSetTabLayout(
            TabLayout tabLayout,
            int tabPositionDefault,
            @NonNull TabLayout.OnTabSelectedListener listener){

        this.tabLayout = tabLayout;

        setTabLayout(this.tabLayout, tabPositionDefault, listener);
    }

    //endregion extensions interface methods

    //region public methods

    protected int getSelectedTabPosition(){
        if (hasTabLayout()) {
            return this.tabLayout.getSelectedTabPosition();
        }
        return tabPositionNotSet;
    }

    // Example: getSharedPreferenceKey(this,"MovieId")
    @SuppressWarnings("WeakerAccess")
    protected String getSharedPreferenceKey(
                Context context,
                @NonNull @SuppressWarnings("SameParameterValue") String keyName) {
        return context.getClass().getSimpleName() + keyName;
    }

    //endregion public methods

    //region protected methods

    @SuppressWarnings("WeakerAccess")
    protected boolean hasTabLayout(){
        return (tabLayout != null);
    }

    // Note: getPreferences() is used here for accessing preferences that are
    // private to this activity. It passes the class name as the preferences file name.
    // (See References 2.)
    protected String getSharedPreference(String key) {

        // 12/21/2017 May implement this someday to manage non-activity class specific prefs.
        //Map<String, ?> allPreferencesMap = sharedPreferences.getAll();
        //if (allPreferencesMap.containsKey(key)){
        //    Object value = allPreferencesMap.get(key);
        //} else {
        //    if (DEBUG) {
        //        Log.d(TAG, String.format("getSharedPreference 331: key not found <%s>", key));
        //    }
        //}

        return this.sharedPreferences.getString(key, null);
    }

    protected AsyncTaskBundleWrapper getAsyncTaskBundleWrapper(){
        // Subclass implementation.
        return null;
    }

    protected AsyncTaskLoaderWidgetHelper getAsyncTaskLoaderWidgetHelper(){
        // Subclass implementation.
        return null;
    }

    protected void setSharedPreference(String sharedPreferenceKey, String value) {
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.putString(sharedPreferenceKey, value);
        editor.apply(); // Save the preference to the preference file in cache/background.
    }

    protected void setTabSelect(int tabPosition) {

        // If there is no tab layout...
        if (! hasTabLayout()) {
            // ... do nothing.
            return;
        }

        // If the default tab has not been specified...
        if (tabPosition == tabPositionNotSet) {
            // ... set it to the base class default.
            tabPosition = tabPositionDefault;
        }

        // Set the tab position to the with the saved preference (if any).
        String preferenceTabPosition = getSharedPreference(preferenceKeyTabPosition);

        if (preferenceTabPosition != null && !preferenceTabPosition.isEmpty()) {
            // Select the saved tab.
            tabPosition = Integer.valueOf(preferenceTabPosition);
        }

        // Get the tab to be selected from the tab layout.
        TabLayout.Tab selectTab = tabLayout.getTabAt(tabPosition);

        // If the tab exists...
        if (selectTab != null) {
            // ... select it.
            selectTab.select();
        }
    }


    //endregion protected methods

    //region private methods

    private void clearSharedPreferences(String sharedPreferenceKey){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.remove(sharedPreferenceKey); // Mark preference to be removed.
        editor.apply(); // Remove the preference from the preference file in cache/background..
    }

    private void clearSharedPreferences(){
        SharedPreferences.Editor editor = this.sharedPreferences.edit();
        editor.clear(); // Clear all preference entries.
        editor.apply(); // Remove all preferences from the preference file in cache/background..
    }

    private void saveSelectedTabPosition() {
        if (this.tabLayout != null) {
            // Save the current tab position.
            String selectedTabPosition = String.valueOf(this.tabLayout.getSelectedTabPosition());

            // Note: savedInstanceState was always null in OnCreate() so using SharedPreferences.
            String preferenceKey =
                    getSharedPreferenceKey(this, preferenceKeyTabPosition);
            setSharedPreference((preferenceKeyTabPosition),
                    selectedTabPosition);
        }
    }

    /**
     * Hint: Call onCreateSetTabLayout() in onCreate() to create a tab layout here.
     * @param tabLayout The tabLayout, e.g., from findViewById(...)
     * @param tabPositionDefault The default position, e.g., 0;
     * @param listener The listener, e.g., this.
     */
    private void setTabLayout(
        TabLayout tabLayout,
        int tabPositionDefault,
        @NonNull TabLayout.OnTabSelectedListener listener){

        this.tabLayout = tabLayout;

        if (this.tabLayout != null){
            this.tabLayout.addOnTabSelectedListener(listener);
        }
        setTabSelect(tabPositionDefault);
    }

    //endregion private methods

}
