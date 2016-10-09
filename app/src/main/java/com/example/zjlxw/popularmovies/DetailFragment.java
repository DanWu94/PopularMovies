package com.example.zjlxw.popularmovies;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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
            final Movie movie = (Movie)intent.getSerializableExtra("movie");
            ((TextView)rootView.findViewById(R.id.text_title))
                    .setText(movie.getTitle());
            final ImageView imageView = (ImageView)rootView.findViewById(R.id.image_poster);
            imageView.post(new Runnable() {
                @Override
                public void run() {
                    Log.d(LOG_TAG, "run: "+((View)imageView.getParent()).getMeasuredWidth());
                    Picasso.with(getActivity())
                            .load(movie.getImageUrl())
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
}
