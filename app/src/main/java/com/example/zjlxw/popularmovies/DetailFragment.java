package com.example.zjlxw.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;

import com.example.zjlxw.popularmovies.data.MovieContract;
import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private String LOG_TAG = DetailFragment.class.getSimpleName();

    private String mId;

    private ListView mTrailerList;

    private ArrayAdapter<String> mTrailerAdapter;

    private ListView mReviewList;

    private ArrayAdapter<Review> mReviewAdapter;

    private Switch mFavoriteSwitch;

    String mSelectionClause = MovieContract.FavoritesEntry.COLUMN_ID + " = ?";

    String[] mSelectionArgs = {""};

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            Bundle data = intent.getExtras();
            final Movie movie = data.getParcelable("movie");
            mId = movie.getId();
            ((TextView)rootView.findViewById(R.id.text_title))
                    .setText(movie.getTitle());
            final ImageView imageView = (ImageView)rootView.findViewById(R.id.image_poster);
            imageView.post(new Runnable() {
                @Override
                public void run() {
//                    Log.d(LOG_TAG, "run: "+((View)imageView.getParent()).getMeasuredWidth());
                    Picasso.with(getActivity())
                            .load(movie.getImageUrl())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder_error)
                            .resize(((View)imageView.getParent()).getMeasuredWidth(),0)
                            .into(imageView);
                }
            });
            ((TextView)rootView.findViewById(R.id.text_vote))
                    .setText(movie.getVote()+"/10");
            ((TextView)rootView.findViewById(R.id.text_release_date))
                    .setText(movie.getReleaseDate());
            ((TextView)rootView.findViewById(R.id.text_overview))
                    .setText(movie.getOverview());

            mFavoriteSwitch = (Switch)rootView.findViewById(R.id.switch_favorite);
            mSelectionArgs[0] = mId;
            Cursor cursor = getContext().getContentResolver().query(
                    MovieContract.FavoritesEntry.CONTENT_URI,
                    null,
                    mSelectionClause,
                    mSelectionArgs,
                    null
            );
            if (cursor.getCount() < 1) {
                mFavoriteSwitch.setChecked(false);
            } else {
                mFavoriteSwitch.setChecked(true);
            }
            cursor.close();

            mFavoriteSwitch.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                    if (isChecked) {
                        Log.d(LOG_TAG, "onCheckedChanged: insert");
                        ContentValues values = new ContentValues();
                        values.put(MovieContract.FavoritesEntry.COLUMN_ID, mId);
                        values.put(MovieContract.FavoritesEntry.COLUMN_TITLE, movie.getTitle());
                        values.put(MovieContract.FavoritesEntry.COLUMN_IMAGE_URL, movie.getImageUrl());
                        values.put(MovieContract.FavoritesEntry.COLUMN_VOTE, movie.getVote());
                        values.put(MovieContract.FavoritesEntry.COLUMN_RELEASE_DATE, movie.getReleaseDate());
                        values.put(MovieContract.FavoritesEntry.COLUMN_OVERVIEW, movie.getOverview());
                        getContext().getContentResolver().insert(
                                MovieContract.FavoritesEntry.CONTENT_URI,
                                values
                        );
                    } else {
                        Log.d(LOG_TAG, "onCheckedChanged: delete");
                        getContext().getContentResolver().delete(
                                MovieContract.FavoritesEntry.CONTENT_URI,
                                mSelectionClause,
                                mSelectionArgs
                        );
                    }
                }
            });

            mTrailerList = (ListView)rootView.findViewById(R.id.list_trailers);
            mTrailerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_trailer) {
                @NonNull
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_trailer, parent, false);
                    }
                    Button buttonTrailer = (Button) convertView.findViewById(R.id.button_trailer);
                    buttonTrailer.setText("Trailer " + (position+1));
                    buttonTrailer.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + getItem(position))));
                        }
                    });
                    return convertView;
                }
            };
            mTrailerList.setAdapter(mTrailerAdapter);
            getTrailerKeys();

            mReviewList = (ListView)rootView.findViewById(R.id.list_reviews);
            mReviewAdapter = new ArrayAdapter<Review>(getActivity(), R.layout.item_review) {
                @NonNull
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_review, parent, false);
                    }
                    TextView tvAuthor = (TextView)convertView.findViewById(R.id.tvAuthor);
                    tvAuthor.setText("Review By: " + getItem(position).author);
                    TextView tvContent = (TextView)convertView.findViewById(R.id.tvContent);
                    tvContent.setText(getItem(position).content);

                    return convertView;
                }
            };
            mReviewList.setAdapter(mReviewAdapter);
            getReviews();

            ScrollView scrollView = (ScrollView) rootView.findViewById(R.id.scroll_group);
            scrollView.smoothScrollTo(0, 0);
        }
        return rootView;
    }

    private void getTrailerKeys() {
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
        fetchTrailerTask.execute();
    }

    private void getReviews() {
        FetchReviewTask fetchReviewTask = new FetchReviewTask();
        fetchReviewTask.execute();
    }

    public class FetchTrailerTask extends AsyncTask<String, Void, String[]> {
        private final String LOG_TAG = FetchTrailerTask.class.getSimpleName();

        private String[] getTrailerFromJson(String movieJsonStr)
                throws JSONException {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");
            int trailer_num = movieArray.length();
            String[] keys = new String[trailer_num];
            for (int i = 0; i < trailer_num; i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);
                keys[i] = movieObject.getString("key");
            }
            return keys;
        }

        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            if (Utility.isOnline(getActivity())) {
                try {
                    String MOVIE_BASE_URL = "http://api.themoviedb.org/3/movie/" + mId + "/videos";
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
//                    Log.d(LOG_TAG, "JSON: \n" + buffer);

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
        protected void onPostExecute(String[] result) {
            if (result != null) {
//                Log.d(LOG_TAG, "onPostExecute: store trailer keys");
                mTrailerAdapter.clear();
                mTrailerAdapter.addAll(result);
                Utility.setListViewHeightBasedOnChildren(mTrailerList);
            }
        }
    }

    public class FetchReviewTask extends AsyncTask<String, Void, Review[]> {
        private final String LOG_TAG = FetchReviewTask.class.getSimpleName();

        private Review[] getTrailerFromJson(String movieJsonStr)
                throws JSONException {
            JSONObject movieJson = new JSONObject(movieJsonStr);
            JSONArray movieArray = movieJson.getJSONArray("results");
            int trailer_num = movieArray.length();
            Review[] reviews = new Review[trailer_num];
            for (int i = 0; i < trailer_num; i++) {
                JSONObject movieObject = movieArray.getJSONObject(i);
                reviews[i] = new Review(movieObject.getString("author"), movieObject.getString("content"));
//                Log.d(LOG_TAG, "getTrailerFromJson: author: " + reviews[i].author);
            }
            return reviews;
        }

        @Override
        protected Review[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            if (Utility.isOnline(getActivity())) {
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
//                    Log.d(LOG_TAG, "JSON: \n" + buffer);

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
            if (result != null) {
//                Log.d(LOG_TAG, "onPostExecute: store reviews");
                mReviewAdapter.clear();
                mReviewAdapter.addAll(result);
                Utility.setListViewHeightBasedOnChildren(mReviewList);
            }
        }
    }
}
