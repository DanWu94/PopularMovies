package com.example.zjlxw.popularmovies;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by zjlxw on 2016/9/27.
 */

public class MovieAdapter extends BaseAdapter {
    private Context mContext;

    // references to our movies
    private List<Movie> mMovieList = new ArrayList<Movie>();

    public MovieAdapter(Context c, Movie[] defaultMovies) {
        mContext = c;
        update(defaultMovies);
    }

    public void update(Movie[] movies) {
        mMovieList.clear();
        for (Movie movie : movies) {
            mMovieList.add(movie);
        }
    }

    public int getCount() {
        return mMovieList.size();
    }

    public Movie getItem(int position) {
        return mMovieList.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new ImageView for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView;
        if (convertView == null) {
            // if it's not recycled, initialize some attributes
            imageView = new ImageView(mContext);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext)
                .load(mMovieList.get(position).getImageUrl())
                .placeholder(R.drawable.placeholder)
                .error(R.drawable.placeholder_error)
                .resize(parent.getWidth()/((GridView)parent).getNumColumns(),0)
                .into(imageView);
        return imageView;
    }


}
