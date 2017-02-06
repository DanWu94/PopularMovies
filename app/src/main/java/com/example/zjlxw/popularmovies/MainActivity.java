package com.example.zjlxw.popularmovies;

import android.media.Image;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.Spinner;

import com.squareup.picasso.Picasso;

public class MainActivity extends AppCompatActivity {

    private Spinner spinner;
    public enum SortBy {
        MOST_POPULAR,
        TOP_RATED,
        FAVORITE
    }
    private SortBy sortBy = SortBy.TOP_RATED;
    public SortBy getSortBy() {
        return sortBy;
    }
    private ShowcaseFragment fragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        addListenerOnSpinnerItemSelection();

        if (savedInstanceState == null) {
            fragment = new ShowcaseFragment();
            getSupportFragmentManager().beginTransaction()
                    .add(R.id.container, fragment)
                    .commit();
        }
    }

    public void addListenerOnSpinnerItemSelection() {
        spinner = (Spinner) findViewById(R.id.sort_by);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                SortBy tempSortBy;
                switch (position) {
                    case 0:
                        tempSortBy = SortBy.MOST_POPULAR;
                        break;
                    case 1:
                        tempSortBy = SortBy.TOP_RATED;
                        break;
                    case 2:
                        tempSortBy = SortBy.FAVORITE;
                        break;
                    default:
                        tempSortBy = SortBy.MOST_POPULAR;
                        break;
                }
                if (tempSortBy != sortBy){
                    sortBy = tempSortBy;
                    if(fragment != null) {
                        fragment.updateMovies();
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
