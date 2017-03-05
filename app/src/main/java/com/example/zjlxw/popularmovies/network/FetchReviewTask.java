package com.example.zjlxw.popularmovies.network;

import android.app.Activity;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.zjlxw.popularmovies.BuildConfig;
import com.example.zjlxw.popularmovies.Review;
import com.example.zjlxw.popularmovies.Utility;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by danwu on 3/4/17.
 */

public class FetchReviewTask extends AsyncTask<String, Void, Review[]> {

    private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

    private Activity context;
    private String mId;
    private AsyncTaskCompleteListener<Review[]> listener;

    public FetchReviewTask(Activity context, String movieId, AsyncTaskCompleteListener<Review []> listener) {
        this.context = context;
        this.mId = movieId;
        this.listener = listener;
    }

    private Review[] getTrailerFromJson(String movieJsonStr)
            throws JSONException {
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");
        int trailer_num = movieArray.length();
        Review[] reviews = new Review[trailer_num];
        for (int i = 0; i < trailer_num; i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);
            reviews[i] = new Review(movieObject.getString("author"), movieObject.getString("content"));
        }
        return reviews;
    }

    @Override
    protected Review[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;
        if (Utility.isOnline(context)) {
            try {
                String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + mId + "/reviews";
                final String API_KEY = "api_key";
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIEDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuilder buffer = new StringBuilder();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line);
                    buffer.append("\n");
                }

                if (buffer.length() == 0) {
                    return null;
                }

                movieJsonStr = buffer.toString();

            } catch (IOException e) {
                Log.e(LOG_TAG, "IO Error: ", e);
            } finally {
                if (urlConnection != null) {
                    urlConnection.disconnect();
                }
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (final IOException e) {
                        Log.e(LOG_TAG, "Error closing stream: ", e);
                    }
                }
            }

            try {
                return getTrailerFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Review[] result) {
        super.onPostExecute(result);
        listener.onTaskComplete(result);
    }
}
