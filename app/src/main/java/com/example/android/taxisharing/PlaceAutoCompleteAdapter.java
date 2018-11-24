package com.example.android.taxisharing;
//
//
///*
// * Copyright (C) 2015 Google Inc. All Rights Reserved.
// *
// *  Licensed under the Apache License, Version 2.0 (the "License");
// *  you may not use this file except in compliance with the License.
// *  You may obtain a copy of the License at
// *
// *      http://www.apache.org/licenses/LICENSE-2.0
// *
// *  Unless required by applicable law or agreed to in writing, software
// *  distributed under the License is distributed on an "AS IS" BASIS,
// *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
// *  See the License for the specific language governing permissions and
// *  limitations under the License.
// */
//
//import android.content.Context;
//import android.graphics.Typeface;
//import android.text.style.CharacterStyle;
//import android.text.style.StyleSpan;
//import android.util.Log;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.ArrayAdapter;
//import android.widget.Filter;
//import android.widget.Filterable;
//import android.widget.TextView;
//import android.widget.Toast;
//
//import com.google.android.gms.common.data.DataBufferUtils;
//import com.google.android.gms.location.places.AutocompleteFilter;
//import com.google.android.gms.location.places.AutocompletePrediction;
//import com.google.android.gms.location.places.AutocompletePredictionBufferResponse;
//import com.google.android.gms.location.places.GeoDataClient;
//import com.google.android.gms.maps.model.LatLngBounds;
//import com.google.android.gms.tasks.RuntimeExecutionException;
//import com.google.android.gms.tasks.Task;
//import com.google.android.gms.tasks.Tasks;
//
//import java.util.ArrayList;
//import java.util.concurrent.ExecutionException;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeoutException;
//
///**
// * Adapter that handles Autocomplete requests from the Places Geo Data Client.
// * {@link AutocompletePrediction} results from the API are frozen and stored directly in this
// * adapter. (See {@link AutocompletePrediction#freeze()}.)
// */
//public class PlaceAutoCompleteAdapter
//        extends ArrayAdapter<AutocompletePrediction> implements Filterable {
//
//    private static final String TAG = "PlaceAutocompleteAdapter";
//    private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);
//    /**
//     * Current results returned by this adapter.
//     */
//    private ArrayList<AutocompletePrediction> mResultList;
//
//    /**
//     * Handles autocomplete requests.
//     */
//    private GeoDataClient mGeoDataClient;
//
//    /**
//     * The bounds used for Places Geo Data autocomplete API requests.
//     */
//    private LatLngBounds mBounds;
//
//    /**
//     * The autocomplete filter used to restrict queries to a specific set of place types.
//     */
//    private AutocompleteFilter mPlaceFilter;
//
//    /**
//     * Initializes with a resource for text rows and autocomplete query bounds.
//     *
//     * @see android.widget.ArrayAdapter#ArrayAdapter(android.content.Context, int)
//     */
//    public PlaceAutoCompleteAdapter(Context context, GeoDataClient geoDataClient,
//                                    LatLngBounds bounds, AutocompleteFilter filter) {
//        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
//        mGeoDataClient = geoDataClient;
//        mBounds = bounds;
//        mPlaceFilter = filter;
//    }
//
//    /**
//     * Sets the bounds for all subsequent queries.
//     */
//    public void setBounds(LatLngBounds bounds) {
//        mBounds = bounds;
//    }
//
//    /**
//     * Returns the number of results received in the last autocomplete query.
//     */
//    @Override
//    public int getCount() {
//        return mResultList.size();
//    }
//
//    /**
//     * Returns an item from the last autocomplete query.
//     */
//    @Override
//    public AutocompletePrediction getItem(int position) {
//        return mResultList.get(position);
//    }
//
//    @Override
//    public View getView(int position, View convertView, ViewGroup parent) {
//        View row = super.getView(position, convertView, parent);
//
//        // Sets the primary and secondary text for a row.
//        // Note that getPrimaryText() and getSecondaryText() return a CharSequence that may contain
//        // styling based on the given CharacterStyle.
//
//        AutocompletePrediction item = getItem(position);
//
//        TextView textView1 = (TextView) row.findViewById(android.R.id.text1);
//        TextView textView2 = (TextView) row.findViewById(android.R.id.text2);
//        textView1.setText(item.getPrimaryText(STYLE_BOLD));
//        textView2.setText(item.getSecondaryText(STYLE_BOLD));
//
//        return row;
//    }
//
//    /**
//     * Returns the filter for the current set of autocomplete results.
//     */
//    @Override
//    public Filter getFilter() {
//        return new Filter() {
//            @Override
//            protected FilterResults performFiltering(CharSequence constraint) {
//                FilterResults results = new FilterResults();
//
//                // We need a separate list to store the results, since
//                // this is run asynchronously.
//                ArrayList<AutocompletePrediction> filterData = new ArrayList<>();
//
//                // Skip the autocomplete query if no constraints are given.
//                if (constraint != null) {
//                    // Query the autocomplete API for the (constraint) search string.
//                    filterData = getAutocomplete(constraint);
//                }
//
//                results.values = filterData;
//                if (filterData != null) {
//                    results.count = filterData.size();
//                } else {
//                    results.count = 0;
//                }
//
//                return results;
//            }
//
//            @Override
//            protected void publishResults(CharSequence constraint, FilterResults results) {
//
//                if (results != null && results.count > 0) {
//                    // The API returned at least one result, update the data.
//                    mResultList = (ArrayList<AutocompletePrediction>) results.values;
//                    notifyDataSetChanged();
//                } else {
//                    // The API did not return any results, invalidate the data set.
//                    notifyDataSetInvalidated();
//                }
//            }
//
//            @Override
//            public CharSequence convertResultToString(Object resultValue) {
//                // Override this method to display a readable result in the AutocompleteTextView
//                // when clicked.
//                if (resultValue instanceof AutocompletePrediction) {
//                    return ((AutocompletePrediction) resultValue).getFullText(null);
//                } else {
//                    return super.convertResultToString(resultValue);
//                }
//            }
//        };
//    }
//
//    /**
//     * Submits an autocomplete query to the Places Geo Data Autocomplete API.
//     * Results are returned as frozen AutocompletePrediction objects, ready to be cached.
//     * Returns an empty list if no results were found.
//     * Returns null if the API client is not available or the query did not complete
//     * successfully.
//     * This method MUST be called off the main UI thread, as it will block until data is returned
//     * from the API, which may include a network request.
//     *
//     * @param constraint Autocomplete query string
//     * @return Results from the autocomplete API or null if the query was not successful.
//     * @see GeoDataClient#getAutocompletePredictions(String, LatLngBounds, AutocompleteFilter)
//     * @see AutocompletePrediction#freeze()
//     */
//    private ArrayList<AutocompletePrediction> getAutocomplete(CharSequence constraint) {
//        Log.i(TAG, "Starting autocomplete query for: " + constraint);
//
//        // Submit the query to the autocomplete API and retrieve a PendingResult that will
//        // contain the results when the query completes.
//        Task<AutocompletePredictionBufferResponse> results =
//                mGeoDataClient.getAutocompletePredictions(constraint.toString(), mBounds,
//                        mPlaceFilter);
//
//        // This method should have been called off the main UI thread. Block and wait for at most
//        // 60s for a result from the API.
//        try {
//            Tasks.await(results, 60, TimeUnit.SECONDS);
//        } catch (ExecutionException | InterruptedException | TimeoutException e) {
//            e.printStackTrace();
//        }
//
//        try {
//            AutocompletePredictionBufferResponse autocompletePredictions = results.getResult();
//
//            Log.i(TAG, "Query completed. Received " + autocompletePredictions.getCount()
//                    + " predictions.");
//
//            // Freeze the results immutable representation that can be stored safely.
//            return DataBufferUtils.freezeAndClose(autocompletePredictions);
//        } catch (RuntimeExecutionException e) {
//            // If the query did not complete successfully return null
//            Toast.makeText(getContext(), "Error contacting API: " + e.toString(),
//                    Toast.LENGTH_SHORT).show();
//            Log.e(TAG, "Error getting autocomplete prediction API call", e);
//            return null;
//        }
//    }
//}

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.style.CharacterStyle;
import android.text.style.StyleSpan;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.places.AutocompleteFilter;
import com.google.android.gms.location.places.AutocompletePrediction;
import com.google.android.gms.location.places.AutocompletePredictionBuffer;
import com.google.android.gms.location.places.Places;
import com.google.android.gms.maps.model.LatLngBounds;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.concurrent.TimeUnit;

/**
 * Adapter that handles autocomplete requests from the Google Places API.
 * Results are encoded as PlaceAutocompleteAdapter.PlaceAutocomplete objects
 * that contain both the place ID and the text description from the autocomplete query.
 */
public class PlaceAutoCompleteAdapter
        extends ArrayAdapter<PlaceAutoCompleteAdapter.PlaceAutocomplete> implements Filterable {

    private static final String TAG = "PlaceAutocompleteAdapter";
    /**
     * Current results returned by this adapter.
     */
    private ArrayList<PlaceAutocomplete> mResultList;

    /**
     * The Google Play services client that handles autocomplete requests.
     */
    private GoogleApiClient mGoogleApiClient;

    /**
     * The bounds used for Google Places API autocomplete requests.
     */
    private LatLngBounds mBounds;

    /**
     * The autocomplete filter used to restrict queries to a specific set of place types.
     */
    private AutocompleteFilter mPlaceFilter;

        private static final CharacterStyle STYLE_BOLD = new StyleSpan(Typeface.BOLD);


    /**
     * Initializes with a resource for text rows and autocomplete query bounds.
     *
     * @see android.widget.ArrayAdapter#ArrayAdapter(android.content.Context, int)
     */
    public PlaceAutoCompleteAdapter(Context context, GoogleApiClient googleApiClient, LatLngBounds bounds,
                                    AutocompleteFilter filter) {
        super(context, android.R.layout.simple_expandable_list_item_2, android.R.id.text1);
        mGoogleApiClient = googleApiClient;
        mBounds = bounds;
        mPlaceFilter = filter;
    }

    /**
     * Sets the Google Play services client used for autocomplete queries.
     * Autocomplete queries are suspended when the client is set to null.
     * Ensure that the client has successfully connected, contains the {@link Places#GEO_DATA_API}
     * API and is available for queries, otherwise API access will be disabled when it is set here.
     */
    public void setGoogleApiClient(GoogleApiClient googleApiClient) {
        if (googleApiClient == null || !googleApiClient.isConnected()) {
            mGoogleApiClient = null;
        } else {
            mGoogleApiClient = googleApiClient;
        }
    }

    /**
     * Sets the geographical bounds for all subsequent queries.
     */
    public void setBounds(LatLngBounds bounds) {
        mBounds = bounds;
    }

    /**
     * Returns the number of results received in the last autocomplete query.
     */
    @Override
    public int getCount() {
        return mResultList.size();
    }

    /**
     * Returns an item from the last autocomplete query.
     */
    @Override
    public PlaceAutocomplete getItem(int position) {
        return mResultList.get(position);
    }

    /**
     * Returns the filter for the current set of autocomplete results.
     */
    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                // Skip the autocomplete query if no constraints are given.
                if (constraint != null) {
                    // Query the autocomplete API for the (constraint) search string.
                    mResultList = getAutocomplete(constraint);
                    if (mResultList != null) {
                        // The API successfully returned results.
                        results.values = mResultList;
                        results.count = mResultList.size();
                    }
                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results != null && results.count > 0) {
                    // The API returned at least one result, update the data.
                    notifyDataSetChanged();
                } else {
                    // The API did not return any results, invalidate the data set.
                    notifyDataSetInvalidated();
                }
            }
        };
        return filter;
    }

    /**
     * Submits an autocomplete query to the Google Places API.
     * Results are returned as PlaceAutocompleteAdapter.PlaceAutocomplete
     * objects to store the place ID and description that the API returns.
     * Returns an empty list if no results were found.
     * Returns null if the API client is not available or the query did not complete
     * successfully.
     * This method MUST be called off the main UI thread, as it will block until data is returned
     * from the API, which may include a network request.
     *
     * @param constraint Autocomplete query string.
     * @return Results from the autocomplete API or null if the query was not successful.
     * @see Places#GEO_DATA_API#getAutocomplete(CharSequence)
     */
    @SuppressLint("LongLogTag")
    private ArrayList<PlaceAutocomplete> getAutocomplete(CharSequence constraint) {
        if (mGoogleApiClient != null) {
            Log.d(TAG, "Starting autocomplete query for: " + constraint);

            // Submit the query to the autocomplete API and retrieve a PendingResult that will
            // contain the results when the query completes.
            PendingResult<AutocompletePredictionBuffer> results =
                    Places.GeoDataApi
                            .getAutocompletePredictions(mGoogleApiClient, constraint.toString(),
                                    mBounds, mPlaceFilter);

            // This method should have been called off the main UI thread. Block and wait for at
            // most 60s for a result from the API.
            AutocompletePredictionBuffer autocompletePredictions = results
                    .await(60, TimeUnit.SECONDS);

            // Confirm that the query completed successfully, otherwise return null.
            final Status status = autocompletePredictions.getStatus();
            if (!status.isSuccess()) {
                Toast.makeText(getContext(), "Error contacting API: " + status.toString(),
                        Toast.LENGTH_SHORT).show();
                Log.e(TAG, "Error getting autocomplete prediction API call: " + status.toString());
                autocompletePredictions.release();
                return null;
            }

            Log.d(TAG, "Query completed. Received " + autocompletePredictions.getCount()
                    + " predictions.");

            // Copy the results into our own data structure, because we can't hold onto the buffer.
            // AutocompletePrediction objects encapsulate the API response
            // (place ID and description).
            Iterator<AutocompletePrediction> iterator = autocompletePredictions.iterator();
            ArrayList resultList = new ArrayList<>(autocompletePredictions.getCount());
            while (iterator.hasNext()) {
                AutocompletePrediction prediction = iterator.next();
                // Get the details of this prediction and copy it into a new PlaceAutocomplete
                // object.
                resultList.add(new PlaceAutocomplete(prediction.getPlaceId(),
                        prediction.getFullText(STYLE_BOLD)));
            }

            // Release the buffer now that all data has been copied.
            autocompletePredictions.release();

            return resultList;
        }
        Log.e(TAG, "Google API client is not connected for autocomplete query.");
        return null;
    }

    /**
     * Holder for Google Places API autocomplete results.
     */
    class PlaceAutocomplete {

        public CharSequence placeId;
        public CharSequence description;

        PlaceAutocomplete(CharSequence placeId, CharSequence description) {
            this.placeId = placeId;
            this.description = description;
        }

        @Override
        public String toString() {
            return description.toString();
        }
    }
}