package com.example.zjlxw.popularmovies;

import android.content.Context;
import android.content.res.Resources;
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
    private Resources mResources;

    // references to our images
    private List<String> mImageUrl = new ArrayList<String>();

    public ImageAdapter(Context c) {
        mContext = c;
        mResources = c.getResources();
        for (int i = 0; i < 4; i++) {
            mImageUrl.add(mResources.getString(R.string.test_image_url));
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
            int width = ((GridView)parent).getWidth()/((GridView)parent).getNumColumns();

            imageView.setLayoutParams(new GridView.LayoutParams(width, width));
            imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);
            imageView.setPadding(80, 80, 80, 80);
        } else {
            imageView = (ImageView) convertView;
        }
        Picasso.with(mContext).load(mImageUrl.get(position)).into(imageView);
        return imageView;
    }


}
