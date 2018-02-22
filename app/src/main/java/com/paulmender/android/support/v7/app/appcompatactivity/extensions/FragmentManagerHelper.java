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

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;

/**
 * Package: com.paulmender.android.support.v7.App.appcompatactivity.extensions
 * Project: PopularMovies Stage 2
 *    Name: FragmentManagerHelper
 * Purpose: Wraps the FragmentManager to help manage Fragments and their
 *          extensions in the app framework (com.paulmender.android.app).
 *  Author: Paul on 20171216.
 * Reference:
 *  https://docs.oracle.com/javase/tutorial/java/generics/methods.html
 *  https://docs.oracle.com/javase/tutorial/java/generics/capture.html
 *      - By convention, helper methods are generally named originalMethodNameHelper.
 */

public class FragmentManagerHelper {

    private final FragmentManager mFragmentManager;

    // Constructor
    @SuppressWarnings("WeakerAccess")
    public FragmentManagerHelper(FragmentManager fragmentManager) {
        this.mFragmentManager = fragmentManager;
    }

    //region Public Methods

    public Fragment findFragmentById(int fragmentId){
        return mFragmentManager.findFragmentById(fragmentId);
    }

    public <F extends Fragment> void hideFragment(F extendsFragment){
        if (extendsFragment != null) {

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.hide(extendsFragment);
            fragmentTransaction.commit();
        }
    }

    public <F extends Fragment> void showFragment(F extendsFragment){
        if (extendsFragment != null) {

            FragmentTransaction fragmentTransaction = mFragmentManager.beginTransaction();
            fragmentTransaction.show(extendsFragment);
            fragmentTransaction.commit();
        }
    }

    //endregion Public Methods
}
