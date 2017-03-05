package com.example.zjlxw.popularmovies;

import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.TaskStackBuilder;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

public class MainActivity extends AppCompatActivity implements ShowcaseFragment.Callback {

    private String LOG_TAG = MainActivity.class.getSimpleName();

    private static final String DETAILFRAGMENT_TAG = "DFTAG";
    private boolean mTwoPane;

    public static final int FAVORITE_DELETED = 1;
    public static final String MY_PREF = "MY_PREF";
    public static final String TWO_PANE = "TWO_PANE";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        ShowcaseFragment showcaseFragment = (ShowcaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_showcase);
        showcaseFragment.setSpinner();

        if (findViewById(R.id.movie_detail_container) != null) {
            mTwoPane = true;
        } else {
            mTwoPane = false;
        }

        getSharedPreferences(MY_PREF, MODE_PRIVATE).edit().putBoolean(TWO_PANE, mTwoPane).apply();
    }



    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onMovieSelected(Movie movie) {
        if (mTwoPane) {
            Bundle arguments = new Bundle();
            arguments.putParcelable(DetailFragment.DETAIL_URI, movie);

            DetailFragment fragment = new DetailFragment();
            fragment.setArguments(arguments);

            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.movie_detail_container, fragment, DETAILFRAGMENT_TAG)
                    .commit();

        } else {
            Intent intent = new Intent(this, DetailActivity.class)
                    .putExtra("movie", movie);
            PendingIntent pendingIntent =
                    TaskStackBuilder.create(this)
                            .addNextIntentWithParentStack(this.getIntent())
                            .getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT);

            NotificationCompat.Builder builder = new NotificationCompat.Builder(this);
            builder.setContentIntent(pendingIntent);
            startActivityForResult(intent, FAVORITE_DELETED);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == FAVORITE_DELETED) {
            if (resultCode == RESULT_OK) {
                reloadFavorite();
            }
        }
    }

    public void reloadFavorite() {
        ShowcaseFragment showcaseFragment = (ShowcaseFragment) getSupportFragmentManager().findFragmentById(R.id.fragment_showcase);
        if (showcaseFragment.sortBy == ShowcaseFragment.SortBy.FAVORITE) {
            showcaseFragment.loadFavoriteMovies();
        }
    }
}
