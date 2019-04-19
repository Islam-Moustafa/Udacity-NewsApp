/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.islam.udacity_news;

import android.content.AsyncTaskLoader;
import android.content.Context;

import java.util.List;

/**
 *  use AsyncTaskLoader instead AsyncTask to fetching data once across change rotation, so save memory
 * AsyncTaskLoader is generic class because take generic parameters such List<News> to ...
 * return from loadInBackground, so must at least override loadInBackground method
 **/

// load list of News from specified url in loader
public class NewsLoader extends AsyncTaskLoader<List<News>> {

    /** Tag for log messages */
    private static final String LOG_TAG = NewsLoader.class.getName();

    // Query URL
    private String mUrl;

    /**
     * Constructs a new NewsLoader.
     *
     * @param context of the activity
     * @param url to load data from
     */
    public NewsLoader(Context context, String url) {
        super(context);
        mUrl = url;
    }

    // onStartLoading automatic call from initLoader method to trigger loader start doing loadInBackground
    @Override
    protected void onStartLoading() {
        forceLoad();
    }

    // loadInBackground method must override in loader to perform the user task in background without freeze UI
    @Override
    public List<News> loadInBackground() {
        if (mUrl == null) {
            return null;
        }

        // fetch books from JSON file in background
        List<News> earthquakes = QueryUtils.fetchEarthquakeData(mUrl);
        return earthquakes;
    }
}
