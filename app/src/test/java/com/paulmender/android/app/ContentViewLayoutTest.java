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

package com.paulmender.android.app;

import com.paulmender.udacity.popularmovies.R;

import org.junit.Test;

import java.util.Enumeration;
import java.util.Hashtable;

import static org.junit.Assert.assertEquals;

/**
 * Package: com.paulmender.udacity.popularmovies
 * Project: PopularMovies Stage 2
 *    Name: ContentViewLayoutTest
 * Purpose:
 *  Author: Paul on 20171218.
 */

public class ContentViewLayoutTest implements
        Enumeration{

    public static final int LAYOUT_UNKNOWN = -1;

    public static final String CONTENT_VIEW_LAYOUT_KEY = "MAIN";

    private Hashtable<String, Integer> layouts;

    public ContentViewLayoutTest(){

    }

    //region Implements

    @Override
    public boolean hasMoreElements() {
        return false;
    }

    @Override
    public Object nextElement() {
        return null;

    }
    //endregion


    @Test
    public void setLayouts(){

        Hashtable<String, Integer> layouts
                = new Hashtable<String, Integer>();
        layouts.put("MOVIE_DETAIL", R.layout.activity_movie_detail);
        layouts.put("MAIN",R.layout.activity_main);
        layouts.put("MOVIE_REVIEW",R.layout.activity_movie_review);
        layouts.put("MOVIE_VIDEO",R.layout.activity_movie_video);
        layouts.put("UNKNOWN",LAYOUT_UNKNOWN);
    }

    @Test
    public void getLayouts(String CONTENT_VIEW_LAYOUT_KEY){
        Integer n = layouts.get("two");
        if (n != null) {
            System.out.println("two = " + n);
        }
    }


    @Test
    public void getLayout(String CONTENT_VIEW_LAYOUT_KEY){
        Integer n = layouts.get("two");
        if (n != null) {
            System.out.println("two = " + n);
        }
    }

}


