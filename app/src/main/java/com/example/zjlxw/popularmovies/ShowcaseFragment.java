package com.example.zjlxw.popularmovies;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class ShowcaseFragment extends Fragment {
    public ShowcaseFragment() {
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.showcase_fragment, container, false);
        ImageView imageView = (ImageView)rootView.findViewById(R.id.imageView);
        Picasso.with(getActivity()).load("http://i.imgur.com/DvpvklR.png").into(imageView);
        return rootView;
    }
}
