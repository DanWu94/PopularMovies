package com.example.zjlxw.popularmovies;

import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
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
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.zjlxw.popularmovies.data.MovieContract;
import com.example.zjlxw.popularmovies.network.AsyncTaskCompleteListener;
import com.example.zjlxw.popularmovies.network.FetchReviewTask;
import com.example.zjlxw.popularmovies.network.FetchTrailerTask;
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

    static final String DETAIL_URI = "movie_detail";

    private Movie movie;

    private boolean mTwoPane;

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mTwoPane = getActivity().getSharedPreferences(MainActivity.MY_PREF, Context.MODE_PRIVATE).getBoolean(MainActivity.TWO_PANE, false);

        final View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Bundle arguments = getArguments();
        if (arguments!= null) {
            movie = arguments.getParcelable(DETAIL_URI);
            mId = movie.getId();
            ((TextView) rootView.findViewById(R.id.text_title))
                    .setText(movie.getTitle());
            final ImageView imageView = (ImageView) rootView.findViewById(R.id.image_poster);
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    Picasso.with(getActivity())
                            .load(movie.getImageUrl())
                            .placeholder(R.drawable.placeholder)
                            .error(R.drawable.placeholder_error)
                            .resize(((View) imageView.getParent()).getMeasuredWidth(), 0)
                            .into(imageView);
                }
            });
            ((TextView) rootView.findViewById(R.id.text_vote))
                    .setText(movie.getVote() + "/10");
            ((TextView) rootView.findViewById(R.id.text_release_date))
                    .setText(movie.getReleaseDate());
            ((TextView) rootView.findViewById(R.id.text_overview))
                    .setText(movie.getOverview());

            mFavoriteSwitch = (Switch) rootView.findViewById(R.id.switch_favorite);
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

                        if (mTwoPane) {
                            ((MainActivity) getActivity()).reloadFavorite();
                        }
                        Toast.makeText(getActivity(), "Movie added to Favorite", Toast.LENGTH_SHORT).show();
                    } else {
                        Log.d(LOG_TAG, "onCheckedChanged: delete");
                        getContext().getContentResolver().delete(
                                MovieContract.FavoritesEntry.CONTENT_URI,
                                mSelectionClause,
                                mSelectionArgs
                        );

                        if (mTwoPane) {
                            ((MainActivity)getActivity()).reloadFavorite();
                        } else {
                            getActivity().setResult(Activity.RESULT_OK);
                        }

                        Toast.makeText(getActivity(), "Movie deleted from Favorite", Toast.LENGTH_SHORT).show();
                    }
                }
            });

            mTrailerList = (ListView) rootView.findViewById(R.id.list_trailers);
            mTrailerAdapter = new ArrayAdapter<String>(getActivity(), R.layout.item_trailer) {
                @NonNull
                @Override
                public View getView(final int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_trailer, parent, false);
                    }
                    Button buttonTrailer = (Button) convertView.findViewById(R.id.button_trailer);
                    buttonTrailer.setText("Trailer " + (position + 1));
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

            mReviewList = (ListView) rootView.findViewById(R.id.list_reviews);
            mReviewAdapter = new ArrayAdapter<Review>(getActivity(), R.layout.item_review) {
                @NonNull
                @Override
                public View getView(int position, View convertView, ViewGroup parent) {
                    if (convertView == null) {
                        convertView = LayoutInflater.from(getContext())
                                .inflate(R.layout.item_review, parent, false);
                    }
                    TextView tvAuthor = (TextView) convertView.findViewById(R.id.tvAuthor);
                    tvAuthor.setText("Review By: " + getItem(position).author);
                    TextView tvContent = (TextView) convertView.findViewById(R.id.tvContent);
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
        new FetchTrailerTask(getActivity(), mId, new FetchTrailerTaskCompleteListener()).execute();
    }

    private void getReviews() {
        new FetchReviewTask(getActivity(), mId, new FetchReviewTaskCompleteListener()).execute();
    }

    public class FetchTrailerTaskCompleteListener implements AsyncTaskCompleteListener<String []> {
        @Override
        public void onTaskComplete(String[] result) {
            if (result != null) {
                mTrailerAdapter.clear();
                mTrailerAdapter.addAll(result);
                Utility.setListViewHeightBasedOnChildren(mTrailerList);
            }
        }
    }

    public class FetchReviewTaskCompleteListener implements AsyncTaskCompleteListener<Review []> {
        @Override
        public void onTaskComplete(Review[] result) {
            if (result != null) {
                mReviewAdapter.clear();
                mReviewAdapter.addAll(result);
                Utility.setListViewHeightBasedOnChildren(mReviewList);
            }
        }
    }
}
