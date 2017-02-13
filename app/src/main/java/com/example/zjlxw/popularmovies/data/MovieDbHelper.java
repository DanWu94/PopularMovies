package com.example.zjlxw.popularmovies.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.zjlxw.popularmovies.data.MovieContract.FavoritesEntry;

/**
 * Created by zjlxw on 2017/1/17.
 */

public class MovieDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2;

    static final String DATABASE_NAME = "movie.db";

    public MovieDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_TABLE_FAVORITES_TABLE = "CREATE TABLE " + FavoritesEntry.TABLE_NAME + " (" +
                FavoritesEntry._ID + " INTEGER PRIMARY KEY," +
                FavoritesEntry.COLUMN_ID + " TEXT UNIQUE NOT NULL, " +
                FavoritesEntry.COLUMN_TITLE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_IMAGE_URL + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_VOTE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_RELEASE_DATE + " TEXT NOT NULL, " +
                FavoritesEntry.COLUMN_OVERVIEW + " TEXT NOT NULL " +
                " );";

        db.execSQL(SQL_CREATE_TABLE_FAVORITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + FavoritesEntry.TABLE_NAME);
        onCreate(db);
    }
}
