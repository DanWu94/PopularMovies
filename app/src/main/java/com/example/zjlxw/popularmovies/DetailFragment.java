package com.example.zjlxw.popularmovies;

import android.content.Intent;
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

/**
 * A placeholder fragment containing a simple view.
 */
public class DetailFragment extends Fragment implements View.OnClickListener {

    private String LOG_TAG = DetailFragment.class.getSimpleName();

    private String mId;

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
        }
        return rootView;
    }

    public void onTrailerClicked(View view) {
        Log.d(LOG_TAG, "onTrailorClicked: " + mId);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.button_trailer:
                onTrailerClicked(v);
                break;
        }

    }
}
