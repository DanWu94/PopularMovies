package com.example.zjlxw.popularmovies.network;

import android.app.Activity;
import android.content.Context;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import com.example.zjlxw.popularmovies.BuildConfig;
import com.example.zjlxw.popularmovies.Movie;
import com.example.zjlxw.popularmovies.ShowcaseFragment;
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

public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {

    private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

    private Activity context;
    private ShowcaseFragment.SortBy sortBy;
    private AsyncTaskCompleteListener<Movie[]> listener;

    public FetchMovieTask(Activity context, ShowcaseFragment.SortBy sortBy, AsyncTaskCompleteListener<Movie[]> listener) {
        this.context = context;
        this.sortBy = sortBy;
        this.listener = listener;
    }

    private Movie[] getMovieDataFromJson(String movieJsonStr)
            throws JSONException {
        final String POSTER_URL_PREFIX = "http://image.tmdb.org/t/p/w185";
        JSONObject movieJson = new JSONObject(movieJsonStr);
        JSONArray movieArray = movieJson.getJSONArray("results");
        Movie[] results = new Movie[movieArray.length()];
        for (int i = 0; i < movieArray.length(); i++) {
            JSONObject movieObject = movieArray.getJSONObject(i);
            results[i] = new Movie(
                    movieObject.getString("id"),
                    movieObject.getString("title"),
                    POSTER_URL_PREFIX + movieObject.getString("poster_path"),
                    movieObject.getString("vote_average"),
                    movieObject.getString("release_date"),
                    movieObject.getString("overview"));
        }
        return results;
    }

    @Override
    protected Movie[] doInBackground(String... params) {

        HttpURLConnection urlConnection = null;
        BufferedReader reader = null;

        String movieJsonStr = null;
        if (Utility.isOnline(context)) {
            try {
                String MOVIE_BASE_URL;
                switch (sortBy) {
                    case MOST_POPULAR:
                        MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
                        break;
                    case TOP_RATED:
                        MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/top_rated?";
                        break;
                    default:
                        MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
                }
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
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line);
                    buffer.append("\n");
                }

                if (buffer.length() == 0) {
                    // Stream was empty.  No point in parsing.
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
                return getMovieDataFromJson(movieJsonStr);
            } catch (JSONException e) {
                Log.e(LOG_TAG, e.getMessage(), e);
                e.printStackTrace();
            }
        }
        return null;
    }

    @Override
    protected void onPostExecute(Movie[] result) {
        super.onPostExecute(result);
        listener.onTaskComplete(result);
    }
}
