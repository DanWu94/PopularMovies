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

public class ImageAdapter extends BaseAdapter {
    private Context mContext;

    // references to our images
    private List<String> mImageUrl = new ArrayList<String>();

    public ImageAdapter(Context c, Movie[] defaultMovies) {
        mContext = c;
        update(defaultMovies);
    }

    public void update(Movie[] movies) {
        mImageUrl.clear();
        for (Movie movie : movies) {
            mImageUrl.add(movie.getImageUrl());
        }
    }

    public int getCount() {
        return mImageUrl.size();
    }

    public Object getItem(int position) {
        return null;
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
                .load(mImageUrl.get(position))
                .resize(parent.getWidth()/((GridView)parent).getNumColumns(),0)
                .into(imageView);
        return imageView;
    }


}
