package com.example.zjlxw.popularmovies;

import android.os.Bundle;
import android.support.design.widget.CollapsingToolbarLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            if (extras.containsKey("movie")) {
                Movie movie = extras.getParcelable("movie");

                CollapsingToolbarLayout collapsingToolbarLayout = (CollapsingToolbarLayout) findViewById(R.id.collapsing_toolbar);
                collapsingToolbarLayout.setTitle(movie.getTitle());

                ImageView imageView = (ImageView) findViewById(R.id.image);

                Picasso.with(this)
                        .load(movie.getImageUrl())
                        .placeholder(R.drawable.placeholder)
                        .error(R.drawable.placeholder_error)
                        .into(imageView);

                Bundle arguments = new Bundle();
                arguments.putParcelable(DetailFragment.DETAIL_URI, movie);

                DetailFragment fragment = new DetailFragment();
                fragment.setArguments(arguments);

                getSupportFragmentManager().beginTransaction()
                        .add(R.id.container, fragment)
                        .commit();
            }
        }
    }
}
