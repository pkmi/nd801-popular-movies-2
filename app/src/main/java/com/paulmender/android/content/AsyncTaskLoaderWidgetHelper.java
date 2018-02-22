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

import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.support.annotation.UiThread;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.paulmender.udacity.popularmovies.MovieParcelable;
import com.paulmender.udacity.popularmovies.utility.MovieDbJsonUtility;

import org.json.JSONException;

import java.security.InvalidParameterException;

/**
 * Package: com.paulmender.android.os
 * Project: PopularMovies Stage 2
 *    Name: AsyncTaskLoaderWidgetHelper
 * Purpose: Contains references to the widgets used by the AsyncTaskLoader* class/subclasses.
 *  Author: Paul on 20171005.
 * History:
 *  20171224 Refactor copy of com.paulmender.android.os.FetchDataTaskWidgets to this class.
 *  20180206 Add textView for Movie Runtime implementation.
 */

@SuppressWarnings("unused")
@UiThread
public class AsyncTaskLoaderWidgetHelper {

    private static final String TAG = "PKMI>AsyncTaskLoaderW..";

    private TextView errorTextView;

    private ProgressBar progressBar;

    private RecyclerView recyclerView;

    private static final int tagIdNotSet = -1;

    private TextView textView;

    private String textViewDataSource = null;

    private String textViewFormatString = null;

    private int textViewTagId = tagIdNotSet;

    private CharSequence errorTextViewMessage = null;
    
    //region Getters and Setters

    public TextView getErrorTextView() { return errorTextView; }
    public void setErrorTextView(TextView errorTextView) {this.errorTextView = errorTextView; }

    public CharSequence getErrorTextViewMessage() { return errorTextViewMessage; }
    protected void setErrorTextViewMessage(String message){ this.errorTextViewMessage = message; }

    public ProgressBar getProgressBar() { return progressBar; }
    public void setProgressBar(ProgressBar progressBar) { this.progressBar = progressBar; }

    public RecyclerView getRecyclerView() { return this.recyclerView; }
    public void setRecyclerView(RecyclerView recyclerView) { this.recyclerView = recyclerView; }

    @SuppressWarnings("WeakerAccess")
    public TextView getTextView() { return textView; }
    public void setTextView(TextView textView) { this.textView = textView; }

    @SuppressWarnings("WeakerAccess")
    public String getTextViewDataSource() { return textViewDataSource; }
    public void setTextViewDataSource(
            @SuppressWarnings("SameParameterValue") String textViewDataSource) {
        this.textViewDataSource = textViewDataSource;
    }

    @SuppressWarnings("WeakerAccess")
    public String getTextViewFormatString() { return textViewFormatString; }
    public void setTextViewFormatString(String textViewFormatString) {
        this.textViewFormatString = textViewFormatString;
    }

    @SuppressWarnings("WeakerAccess")
    public int getTextViewTagId() { return textViewTagId; }
    public void setTextViewTagId(@SuppressWarnings("SameParameterValue") int textViewTagId) {
        this.textViewTagId = textViewTagId;}

    //endregion Getters and Setters

    // Constructor
    /**
     * When using no parameter constructor, set the widgets "manually".
     * For example:
     * ... widgets = new Widgets();
     * widgets.errorTextView = mErrorTextView;
     * or
     * widgets.errorTextView = findViewById(R.id.tv_activity_main_error);
     */
    public AsyncTaskLoaderWidgetHelper() {
    }

    // Constructor overload
    public AsyncTaskLoaderWidgetHelper(
            @Nullable TextView errorTextView,
            @Nullable ProgressBar progressBar,
            @Nullable RecyclerView recyclerView) {

        this.errorTextView = errorTextView;
        this.progressBar = progressBar;
        this.recyclerView = recyclerView;
    }

    //region public methods

    @SuppressWarnings("WeakerAccess")
    public boolean hasErrorTextView(){
        return this.errorTextView != null;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasProgressBar(){
        return this.progressBar != null;
    }

    public boolean hasRecyclerView(){
        return this.recyclerView != null;
    }

    public boolean hasTextView(){
        return this.textView != null;
    }

    @SuppressWarnings("WeakerAccess")
    public boolean hasTextViewDataSource(){ return this.textViewDataSource != null;}

    public boolean hasTextViewTagId(){ return this.textViewTagId != tagIdNotSet; }

    public <P extends Parcelable> void setTextViewTag(P extendsParcelable){
        String tag = null;

        if (hasTextView() && hasTextViewDataSource() && hasTextViewTagId()){
            if (extendsParcelable != null){
                if (extendsParcelable instanceof MovieParcelable){
                    try {
                        tag = MovieDbJsonUtility.getMovieParcelableValue(
                                (MovieParcelable) extendsParcelable,
                                getTextViewDataSource(),null);
                    } catch (JSONException je){
                        Log.e(TAG, "setTextViewTag: ",je );
                    }
                }
            }
            getTextView().setTag(getTextViewTagId(),tag);
        }
    }

    public <P extends Parcelable> void setTextViewText(P extendsParcelable){
        String text = null;

        if (hasTextView() && hasTextViewDataSource()){
            if (extendsParcelable != null){
                if (extendsParcelable instanceof MovieParcelable){
                    try {
                        text = MovieDbJsonUtility.getMovieParcelableValue(
                                (MovieParcelable) extendsParcelable,
                                getTextViewDataSource(),
                                getTextViewFormatString());
                    } catch (JSONException je){
                        Log.e(TAG, "setTextViewText: ",je );
                    }
                }
            }
            getTextView().setText(text);
        }

    }

    @SuppressWarnings("WeakerAccess")
    public void showErrorMessage() {

        // Hide other widgets.
        if (hasProgressBar()) {
            this.progressBar.setVisibility(View.GONE);
        }
        if (hasRecyclerView()) {
            this.recyclerView.setVisibility(View.INVISIBLE);
        }
        if (hasTextView()) {
            this.textView.setVisibility(View.INVISIBLE);
        }

        // Show the error message.
        if (hasErrorTextView()) {
            if (this.errorTextViewMessage != null) {
                this.errorTextView.setText(this.errorTextViewMessage);
            }
            this.errorTextView.setVisibility(View.VISIBLE);
        }
    }

    public void showErrorMessage(String message) {
        this.errorTextViewMessage = message;
        showErrorMessage();
    }

    @SuppressWarnings("WeakerAccess")
    public void showErrorMessage(CharSequence message){
        this.errorTextViewMessage = message;
        showErrorMessage();
    }

    @SuppressWarnings("WeakerAccess")
    public void showProgressBar() {

        // Hide other widgets.
        if (hasErrorTextView()){
            this.errorTextView.setVisibility(View.GONE);
        }
        if (hasRecyclerView()){
            this.recyclerView.setVisibility(View.INVISIBLE);
        }
        if (hasTextView()) {
            this.textView.setVisibility(View.INVISIBLE);
        }

        // Show the progress bar.
        if (hasProgressBar()) {
            this.progressBar.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void showRecyclerView() {
        assertHas();

        // Hide other widgets.
        if (hasErrorTextView()) {
            this.errorTextView.setVisibility(View.GONE);
        }
        if (hasProgressBar()) {
            this.progressBar.setVisibility(View.GONE);
        }
        if (hasTextView()) {
            this.textView.setVisibility(View.VISIBLE);
        }

        // Show the recycler view.
        if (hasRecyclerView()) {
            this.recyclerView.setVisibility(View.VISIBLE);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void showTextView() {
        assertHas();

        // Hide other widgets.
        if (hasErrorTextView()) {
            this.errorTextView.setVisibility(View.GONE);
        }
        if (hasProgressBar()) {
            this.progressBar.setVisibility(View.GONE);
        }
        if (hasRecyclerView()){
            this.recyclerView.setVisibility(View.INVISIBLE);
        }

        // Show the text view.
        if (hasTextView()) {
            this.textView.setVisibility(View.VISIBLE);
        }
    }

    //endregion public methods

    //region private methods

    @SuppressWarnings("UnusedReturnValue")
    private boolean assertHas(){
        try {
            if (hasRecyclerView() && hasTextView()) {
                throw new InvalidParameterException();
            }
        } catch (InvalidParameterException ipe){
            Log.e(TAG,
                    "hasAssert: RecyclerView and TextView parameters together are unsupported.",ipe);
        }

        try {
            if (hasTextView() && !hasTextViewDataSource()) {
                throw new InvalidParameterException();
            }
        } catch (InvalidParameterException ipe){
            Log.e(TAG,
                    "hasAssert: TextView with TextViewDataSource parameter is required.",ipe);
        }

        return hasErrorTextView() && hasProgressBar() && (hasRecyclerView() || hasTextView());
    }

    //endregion private methods
}