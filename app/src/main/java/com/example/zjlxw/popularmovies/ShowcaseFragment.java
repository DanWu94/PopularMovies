package com.example.zjlxw.popularmovies;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ShowcaseFragment extends Fragment {
    public ShowcaseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.showcase_fragment, container, false);
        final GridView gridView = (GridView)rootView.findViewById(R.id.showcase_gridview);
        gridView.setNumColumns(2);
        gridView.post(new Runnable() {
            @Override
            public void run() {
                gridView.setAdapter(new ImageAdapter(getActivity()));
            }
        });
        return rootView;
    }
}
