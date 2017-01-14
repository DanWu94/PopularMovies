package com.example.zjlxw.popularmovies;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {

    private String LOG_TAG = DetailFragment.class.getSimpleName();

    private String mId;

    private String[] mTrailerKeys;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);
        Button buttonTrailer = (Button) rootView.findViewById(R.id.button_trailer);
        buttonTrailer.setOnClickListener(this);

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
                    Log.d(LOG_TAG, "run: "+((View)imageView.getParent()).getMeasuredWidth());
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
            getTrailerKeys();
        }
        return rootView;
    }

    private void getTrailerKeys() {
        Log.d(LOG_TAG, "onTrailorClicked: " + mId);
        FetchTrailerTask fetchTrailerTask = new FetchTrailerTask();
        fetchTrailerTask.execute();
    }

    public void onTrailerClicked(int i) {
        Log.d(LOG_TAG, "onTrailorClicked: " + i);
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.youtube.com/watch?v=" + mTrailerKeys[i])));
    }

    public boolean isOnline() {
        Activity context = getActivity();
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getActiveNetworkInfo();
        return networkInfo != null && networkInfo.isConnectedOrConnecting();
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
            Log.d(LOG_TAG, "getTrailerFromJson: get trailer keys" + keys);
            return keys;
        }



        @Override
        protected String[] doInBackground(String... params) {

            HttpURLConnection urlConnection = null;
            BufferedReader reader = null;

            String movieJsonStr = null;
            if (isOnline()) {
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
                    Log.d(LOG_TAG, "JSON: \n" + buffer);

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
                Log.d(LOG_TAG, "onPostExecute: store trailer keys");
                mTrailerKeys = result;
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_trailer:
                onTrailerClicked(0);
                break;
        }

    }
}
