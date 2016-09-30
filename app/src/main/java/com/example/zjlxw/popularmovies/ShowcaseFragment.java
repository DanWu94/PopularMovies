package com.example.zjlxw.popularmovies;


import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ShowcaseFragment extends Fragment {

    private final String LOG_TAG = ShowcaseFragment.class.getSimpleName();

    private GridView mGridView;
    private MovieAdapter mMovieAdapter;
    public ShowcaseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Movie[] defaultMovies = {new Movie("Picasso", "http://i.imgur.com/DvpvklR.png")};
        mMovieAdapter = new MovieAdapter(getActivity(), defaultMovies);

        View rootView = inflater.inflate(R.layout.fragment_showcase, container, false);
        mGridView = (GridView)rootView.findViewById(R.id.showcase_gridview);
        mGridView.post(new Runnable() {
            @Override
            public void run() {
                mGridView.setAdapter(mMovieAdapter);
                mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                        Movie movie = mMovieAdapter.getItem(position);
                        Intent intent = new Intent(getActivity(), DetailActivity.class)
                                .putExtra(Intent.EXTRA_TEXT, movie.getTitle());
                        startActivity(intent);
                    }
                });
            }
        });
        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateMovies();
    }

    private void updateMovies() {
        FetchMovieTask movieTask = new FetchMovieTask();
        movieTask.execute();
        Log.d(LOG_TAG, "updateMovies: execute FetchMovieTask");
    }

    public class FetchMovieTask extends AsyncTask<String, Void, Movie[]> {
        private final String LOG_TAG = FetchMovieTask.class.getSimpleName();

        private Movie[] getMovieDataFromJson(String movieJsonStr)
                throws JSONException {
            final String POSTER_URL_PREFIX = "http://image.tmdb.org/t/p/w185";
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");
            Movie[] results = new Movie[movieArray.length()];
            for (int i = 0; i < movieArray.length(); i++) {
                results[i] = new Movie(
                        movieArray.getJSONObject(i).getString("title"),
                        POSTER_URL_PREFIX + movieArray.getJSONObject(i).getString("poster_path"));
                Log.d(LOG_TAG, "getMovieDataFromJson:"
                        +" title: "+results[i].getTitle()
                        +" imageUrl: "+results[i].getImageUrl());
            }
            return results;
        }

        @Override
        protected Movie[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;

            try {
                final String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/popular?";
                final String API_KEY = "api_key";
                Uri builtUri = Uri.parse(MOVIE_BASE_URL).buildUpon()
                        .appendQueryParameter(API_KEY, BuildConfig.MOVIEDB_API_KEY)
                        .build();

                URL url = new URL(builtUri.toString());

                urlConnection = (HttpURLConnection) url.openConnection();
                urlConnection.setRequestMethod("GET");
                urlConnection.connect();

                InputStream inputStream = urlConnection.getInputStream();
                StringBuffer buffer = new StringBuffer();
                if (inputStream == null) {
                    return null;
                }
                reader = new BufferedReader(new InputStreamReader(inputStream));

                String line;
                while ((line = reader.readLine()) != null) {
                    // Since it's JSON, adding a newline isn't necessary (it won't affect parsing)
                    // But it does make debugging a *lot* easier if you print out the completed
                    // buffer for debugging.
                    buffer.append(line + "\n");
                }
                Log.d(LOG_TAG, "JSON: \n"+buffer);

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

            return null;
        }

        @Override
        protected void onPostExecute(Movie[] results) {
            if (results != null) {
                mMovieAdapter.update(results);
                Log.d(LOG_TAG, "onPostExecute: adapter updated");
                mGridView.setAdapter(mMovieAdapter);
            }
        }
    }
}
