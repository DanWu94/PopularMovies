package com.example.zjlxw.popularmovies;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;

public class DetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (savedInstanceState == null) {
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                if (extras.containsKey("movie")) {
                    Movie movie = extras.getParcelable("movie");

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
}
