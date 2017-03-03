package com.example.zjlxw.popularmovies;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Spinner;
import android.widget.Toast;

import com.example.zjlxw.popularmovies.data.MovieContract;

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
    private int prevGridPos = 0;

    private final String GRID_POS = "grid_pos";

    private Spinner spinner;
    public enum SortBy {
        MOST_POPULAR,
        TOP_RATED,
        FAVORITE,
        IDLE
    }
    public SortBy sortBy = SortBy.IDLE;

    public void setSpinner() {
        ((AppCompatActivity) getActivity()).getSupportActionBar().setDisplayShowCustomEnabled(true);
        LayoutInflater inflater = (LayoutInflater) getActivity().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View spinnerView = inflater.inflate(R.layout.spinner, null);
        spinner = (Spinner) spinnerView.findViewById(R.id.sort_by);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SortBy tempSortBy;
                switch (position) {
                    case 0:
                        tempSortBy = SortBy.MOST_POPULAR;
                        break;
                    case 1:
                        tempSortBy = SortBy.TOP_RATED;
                        break;
                    case 2:
                        tempSortBy = SortBy.FAVORITE;
                        break;
                    default:
                        tempSortBy = SortBy.IDLE;
                        break;
                }

                if (tempSortBy != sortBy) {
                    if (sortBy != SortBy.IDLE) {
                        prevGridPos = 0;
                    }
                    sortBy = tempSortBy;
                    updateMovies();

                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        ((AppCompatActivity) getActivity()).getSupportActionBar().setCustomView(spinnerView);
    }

    public interface Callback {
        public void onMovieSelected(Movie movie);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(LOG_TAG, "onCreateView: called");
        Movie[] defaultMovies = {new Movie("0", "Oops...There's something wrong with your Internet connection.", "http://i.imgur.com/DvpvklR.png", "0", "1970-1-1", "A little tip: check your WIFI or cellular data.")};
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
                        ((Callback)getActivity()).onMovieSelected(movie);
                    }
                });
            }
        });

        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putInt(GRID_POS, mGridView.getFirstVisiblePosition());
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (savedInstanceState != null) {
            prevGridPos = savedInstanceState.getInt(GRID_POS);
            Log.d(LOG_TAG, "onActivityCreated: " + prevGridPos);
        }
    }

    public void updateMovies() {
        if(sortBy == SortBy.FAVORITE) {
            loadFavoriteMovies();
        } else {
            FetchMovieTask movieTask = new FetchMovieTask();
            movieTask.execute();
        }
    }

    private void updateGridView(Movie [] movies) {
        mMovieAdapter.update(movies);
        mGridView.setAdapter(mMovieAdapter);
        mGridView.smoothScrollToPosition(prevGridPos);
    }

    public void loadFavoriteMovies() {
        Cursor cursor = getContext().getContentResolver().query(
                MovieContract.FavoritesEntry.CONTENT_URI,
                null,
                null,
                null,
                null
        );
        Movie[] results = new Movie[cursor.getCount()];
        if (!cursor.moveToFirst()) {
            Toast.makeText(getActivity(), "No favorite movies yet", Toast.LENGTH_SHORT).show();
        } else {
            int i = 0;
            do {
                results[i] = new Movie(
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoritesEntry.COLUMN_ID)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoritesEntry.COLUMN_TITLE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoritesEntry.COLUMN_IMAGE_URL)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoritesEntry.COLUMN_VOTE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoritesEntry.COLUMN_RELEASE_DATE)),
                        cursor.getString(cursor.getColumnIndex(MovieContract.FavoritesEntry.COLUMN_OVERVIEW))
                );
                i++;
            } while (cursor.moveToNext());
        }
        cursor.close();
        updateGridView(results);
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
            if (Utility.isOnline(getActivity())) {
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
        protected void onPostExecute(Movie[] results) {
            if (results != null) {
                updateGridView(results);
            }
        }
    }
}
