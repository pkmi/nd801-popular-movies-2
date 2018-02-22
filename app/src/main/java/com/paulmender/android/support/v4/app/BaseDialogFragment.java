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

package com.paulmender.android.support.v4.app;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;

import static com.paulmender.udacity.popularmovies.BuildConfig.DEBUG;

/**
 * Package: com.paulmender.udacity.popularmovies.app
 * Project: PopularMovies Stage 2
 *    Name: BaseMovieDialogFragment
 * Purpose: Base class for dialog fragments. Create a parameter bundle using the
 *          BUNDLE_KEY_* constants as needed, then setArgument(the parameter bundle).
 *  Author: Paul on 20180128.
 * References:
 *  1. https://developer.android.com/guide/topics/ui/dialogs.html
 *  2. https://developer.android.com/reference/android/app/DialogFragment.html
 */

@SuppressWarnings({"WeakerAccess", "unused"})
public class BaseDialogFragment extends DialogFragment {

    private static final String TAG = "PKMI>BaseDialogFragm...";

    public static final String BUNDLE_KEY_TITLE = "bundleKeyTitle";
    public static final String BUNDLE_KEY_MESSAGE = "bundleKeyMessage";
    public static final String BUNDLE_KEY_CAPTION_POSITIVE = "bundleKeyCaptionPositive";
    public static final String BUNDLE_KEY_CAPTION_NEGATIVE = "bundleKeyCaptionNegative";
    public static final String BUNDLE_KEY_LAYOUT_ID = "bundleKeyLayoutId";

    protected static final int LAYOUT_ID_NOT_SET = -1;

    //region Getters and Setters

    public int getLayoutId() {
        return layoutId;
    }
    public String getMessage() {
        return message;
    }
    public String getNegativeCaption() {
        return negativeCaption;
    }
    public String getPositiveCaption() {
        return positiveCaption;
    }
    public String getTitle() {
        return title;
    }

    //endregion Getters and Setters

    private int layoutId = LAYOUT_ID_NOT_SET;
    private String message;
    private String negativeCaption;
    private String positiveCaption;
    private String title;

    // Constructor
    public BaseDialogFragment(){}

    public static BaseDialogFragment newInstance(Bundle dialogBundle) {
        BaseDialogFragment f = new BaseDialogFragment();
        f.setArguments(dialogBundle);
        return f;
    }

    //region Override methods

    @Override
    public @NonNull Dialog onCreateDialog(Bundle savedInstanceState) {

        // Set the dialog parameters from the arguments bundle.
        parseArguments();

        if (hasLayoutId()) {
            setStyle(DialogFragment.STYLE_NO_TITLE, android.R.style.Theme_Material_Dialog
            );
        } else {
            setStyle(DialogFragment.STYLE_NORMAL, 0);
        }

        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        if (hasLayoutId() && getActivity() != null) {
            // Inflate the layout to use as dialog or embedded fragment.
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View view =  inflater.inflate(this.layoutId, null);

            // Note: Changes to the view content must be set prior to set the creating the
            // alert dialog to avoid:
            // ...AndroidRuntimeException: requestFeature() must be called before adding content
            setViewContent(view);

            builder.setView(view);
        }
        if (hasMessage()){
            builder.setMessage(this.message);
        }
        if (hasPositiveCaption()){
                builder.setPositiveButton(this.positiveCaption, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    onClickPositiveButton(dialog, id);
                }
            });
        }
        if (hasNegativeCaption()) {
            builder.setNegativeButton(this.negativeCaption, new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int id) {
                    onClickNegativeButton(dialog, id);
                }
            });
        }
        if (hasTitle()){
            builder.setTitle(this.title);
        }

        if (DEBUG) {
            Log.d(TAG, String.format("onCreateDialog 141: this.layoutId = %d",this.layoutId));
        }

        // Create the AlertDialog object and return it
        return builder.create();
    }

    //endregion

    //region public methods

    public boolean hasLayoutId(){
        return this.layoutId != LAYOUT_ID_NOT_SET;
    }

    public boolean hasMessage(){
        return this.message != null;
    }

    public boolean hasNegativeCaption(){
        return this.negativeCaption != null;
    }

    public boolean hasPositiveCaption(){
        return this.positiveCaption != null;
    }

    public boolean hasTitle(){
        return this.title != null;
    }

    @SuppressWarnings("EmptyMethod")
    public void onClickNegativeButton(DialogInterface dialog, int id){
        // Subclass implementation.
    }

    @SuppressWarnings("EmptyMethod")
    public void onClickPositiveButton(DialogInterface dialog, int id){
        // Subclass implementation.
    }

    //endregion public methods

    //region protected methods
    protected void setViewContent(View view){
        // Subclass implementation.
    }

    //endregion protected methods

    //region private methods

    private void parseArguments() {
        Bundle args = getArguments();
        if (args != null) {
            this.layoutId = args.getInt(BUNDLE_KEY_LAYOUT_ID, LAYOUT_ID_NOT_SET);
            this.message = args.getString(BUNDLE_KEY_MESSAGE, null);
            this.negativeCaption = args.getString(BUNDLE_KEY_CAPTION_NEGATIVE, null);
            this.positiveCaption = args.getString(BUNDLE_KEY_CAPTION_POSITIVE, null);
            this.title = args.getString(BUNDLE_KEY_TITLE, null);
        }
    }

    //endregion private methods
}
