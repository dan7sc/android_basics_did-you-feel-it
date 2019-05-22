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
package com.example.android.didyoufeelit

import android.annotation.SuppressLint
import android.os.AsyncTask
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.widget.TextView
import android.view.View


/**
 * Displays the perceived strength of a single earthquake event based on responses from people who
 * felt the earthquake.
 */
class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Create an {@link AsyncTask} to perform the HTTP request to the given URL
        // on a background thread. When the result is received on the main UI thread,
        // then update the UI.
        val task = EarthquakeAsyncTask()
        task.execute(USGS_REQUEST_URL)
    }

    /**
     * Update the UI with the given earthquake information.
     */
    private fun updateUi(earthquake: Event) {
        val titleTextView: TextView = findViewById<View>(R.id.title) as TextView
        titleTextView.text = earthquake.title

        val tsunamiTextView: TextView = findViewById<View>(R.id.number_of_people) as TextView
        tsunamiTextView.text = getString(R.string.num_people_felt_it, earthquake.numOfPeople)

        val magnitudeTextView: TextView = findViewById<View>(R.id.perceived_magnitude) as TextView
        magnitudeTextView.text = earthquake.perceivedStrength
    }

    /**
     * [AsyncTask] to perform the network request on a background thread, and then
     * update the UI with the first earthquake in the response.
     */
    @SuppressLint("StaticFieldLeak")
    private inner class EarthquakeAsyncTask : AsyncTask<String, Void, Event?>() {
        /**
         * This method is invoked (or called) on a background thread, so we can perform
         * long-running operations like making a network request.
         *
         * It is NOT okay to update the UI from a background thread, so we just return an
         * [Event] object as the result.
         */
        override fun doInBackground(vararg urls: String): Event? {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.isEmpty()) {
                return null
            }

            return Utils.fetchEarthquakeData(urls[0])
        }

        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         *
         * It IS okay to modify the UI within this method. We take the [Event] object
         * (which was returned from the doInBackground() method) and update the views on the screen.
         */
        override fun onPostExecute(result: Event?) {
            // If there is no result, do nothing.
            if (result == null) {
                return
            }

            updateUi(result)
        }
    }

    companion object {

        /** URL for earthquake data from the USGS dataset  */
        private const val USGS_REQUEST_URL = "https://earthquake.usgs.gov/fdsnws/event/1/query?format=geojson&starttime=2016-01-01&endtime=2016-05-02&minfelt=50&minmagnitude=5"
    }
}
