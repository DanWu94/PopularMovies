package com.example.zjlxw.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment {

    private String LOG_TAG = DetailFragment.class.getSimpleName();

    public DetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail, container, false);

        Intent intent = getActivity().getIntent();
        if (intent != null && intent.hasExtra("movie")) {
            Movie movie = (Movie)intent.getSerializableExtra("movie");
            ((TextView)rootView.findViewById(R.id.title_text))
                    .setText(movie.getTitle());
            Log.d(LOG_TAG, "onCreateView: title: "+movie.getTitle());
            Log.d(LOG_TAG, "onCreateView: imageUrl: "+movie.getImageUrl());
            Picasso.with(getActivity())
                    .load(movie.getImageUrl())
                    .into((ImageView)rootView.findViewById(R.id.poster_image));

        }
        return rootView;
    }
}
