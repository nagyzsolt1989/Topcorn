package com.nagy.zsolt.topcorn.data;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Zsolti on 2018.03.30..
 */

public class FavouritesDBHelper extends SQLiteOpenHelper {

    // The database name
    private static final String DATABASE_NAME = "topcorn.db";

    private static final int DATABASE_VERSION = 1;

    public FavouritesDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {

        final String SQL_CREATE_FAVOURITES_TABLE = "CREATE TABLE " + FavourtiesContract.FavouritesEntry.TABLE_NAME + " (" +
                FavourtiesContract.FavouritesEntry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_BACKDROP + " TEXT NOT NULL, " +
                FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_POSTER + " TEXT NOT NULL, " +
                FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_TITLE + " TEXT NOT NULL, " +
                FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_RELEASE_DATE + " DATE NOT NULL, " +
                FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_RATING + " TEXT NOT NULL, " +
                FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_DURATION + " INTEGER NOT NULL, " +

                FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_OVERVIEW + " TEXT NOT NULL, " +
                FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_TAGLINE + " TEXT NOT NULL, " +
                FavourtiesContract.FavouritesEntry.COLUMN_MOVIE_IMDB + " TEXT NOT NULL, " +
                FavourtiesContract.FavouritesEntry.COLUMN_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                "); ";

        sqLiteDatabase.execSQL(SQL_CREATE_FAVOURITES_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + FavourtiesContract.FavouritesEntry.TABLE_NAME);
        onCreate(sqLiteDatabase);
    }
}
